package com.hackingroomba.roombacomm;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.Thread;
import java.util.*;
import java.lang.Math;


public class FrameProcessor {
 	private static final long serialVersionUID = 1L;
 	private static final int lumBufHeight = 32;
 	// socket variables
	int portNum = 5005;
	Socket videoSocket;
	BufferedInputStream in;
	BufferedOutputStream out;
	RR_API rr;
	
	// video variables
	byte[] readBuf;		// where we read raw network data into
	byte[] vidBuf;		// raw pixel bytes
	int[] vidDispBuf, sliceBufInt;
	int[] lumBuf = new int[256*lumBufHeight];
	int[] qDisplayBuf, q;	// the quantized array, qDisplayBuf is video data, q is 0 or 1 in each int
	int[] ltBuf;		// the graphic markers for current middle & bundaries of tracking
	String server = "";
	int frameSize;
	int imgWidth;
	int imgHeight;
	Container contentPane;
	Image img, lumImg, quantizedImg, sliceImg, trackingImg;
    JFrame frame;
    Insets insets;
    int qStartRow, qEndRow;
    int thresholdOverride = 0;
    int frameCount = 0;
    
    // line variables. Units are pixels distance from center of image. Negative value is left of center
    class LineBoundary {
    	int start;
    	int middle;
    	int end;
    };
    ArrayList<LineBoundary> lineList;
    LineBoundary currentLine;
    int first;
    int[] smoothSlice;	// a quantized slice across the image, averaged to remove noise
    int minLineThickness, maxLineThickness;

	public FrameProcessor(String server,int portnum, int width, int height, int minLine, int maxline, int thresholdOv)
	{
		imgWidth = width;
		imgHeight = height;
		minLineThickness = minLine;
		maxLineThickness = maxline;
		lineList = new ArrayList<LineBoundary>();
		currentLine = new LineBoundary();
		first = 1;
		smoothSlice = new int[width];
		ltBuf = new int[4*imgWidth];
		thresholdOverride = thresholdOv;
		this.server = server;
		this.portNum = portnum;		
	}
	
	public void makeImagePane()
	{
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}
	
	void createAndShowGUI() {
        //Create and set up the window.
        frame = new JFrame("Raw Image");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        contentPane = frame.getContentPane();
        contentPane.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));

        //Display the window.
        frame.pack();
        frame.setVisible(true);
		insets = frame.getInsets();
		frame.setMinimumSize(new Dimension(400,350));

	}
	
	public void displayFrame()
	{
		Graphics g = frame.getGraphics();
        
        // display the raw image
		img = frame.createImage(new MemoryImageSource(imgWidth,imgHeight,vidDispBuf, 0, imgWidth));
		g.drawImage(img, insets.left+2, insets.top+2, null);

 		// display the slice
 		sliceImg = frame.createImage(new MemoryImageSource(imgWidth, qEndRow-qStartRow, sliceBufInt, 0, imgWidth));
 		g.drawImage(sliceImg, insets.left, insets.top + 20 + imgHeight, null);

		// display the quantized image
 		quantizedImg = frame.createImage(new MemoryImageSource(imgWidth, qEndRow-qStartRow, qDisplayBuf, 0, imgWidth));
 		g.drawImage(quantizedImg, insets.left, insets.top + 40 + imgHeight, null);
 		
		// display the line tracking markers
 		trackingImg = frame.createImage(new MemoryImageSource(imgWidth, 4, ltBuf, 0, imgWidth));
 		g.drawImage(trackingImg, insets.left, insets.top + 60 + imgHeight, null);
 		
 		//lumImg = frame.createImage(new MemoryImageSource(256,lumBufHeight,lumBuf, 0, 256));
 		//g.drawImage(lumImg, insets.left, insets.top + 20 + imgHeight, null);
	}
	
	public Boolean frame2Roborealm()
	{
		return rr.setImage(vidBuf, imgWidth, imgHeight);
	}
	
	public String getShapeData()
	{
		String rrString = rr.getVariable("SHAPES");
		System.out.println(rrString);
		return rrString;
	}
	
	public void connect(boolean connectRoborealm)
	{
		try {
			videoSocket = new Socket(server, portNum);
			in = new BufferedInputStream(videoSocket.getInputStream());
			out = new BufferedOutputStream(videoSocket.getOutputStream());
		} catch (UnknownHostException e) {
			System.out.println("Unknown host: " + server + ":" + portNum);
			System.exit(-1);
		} catch(IOException e) {
			System.out.println("I/O exception");
			e.printStackTrace();
			System.exit(-1);
		}	
		System.out.println("connected to video");
		
		// connect to RoboRealm if requested
		if (connectRoborealm) {
		    rr = new RR_API();
		    if (!rr.connect("localhost"))
		    {
		      System.out.println("Could not connect to RoboRealm on localhost! Exiting...");
		      System.exit(-1);
		    }			
		}
	}
	
	public void disconnect()
	{
        try {
            // do io streams need to be closed first?
            if (in != null) in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        in = null;
        
        try {
            if (videoSocket != null) videoSocket.close();       
        } catch (Exception e) {
            e.printStackTrace();
        }
        videoSocket = null;
        System.out.println("disconnected from Video");
	}

    /**
     * Read a video frame from the network device
     * @param width in pixels
     * @param height in pixels
     * @param captureType 0 = monochrome, 1 = rgb color
     * @return number of pixels read or negative for error during read (e.g. size doesn't match frame size)
     */

	public int readFrame(int width, int height, int captureType)
	{
		int bytesPerPixel = 1;
		int readLength = 0;
		int rxImageSize;
		int rgbShiftSize = 16;
		int pixel = 255 << 24;
		
		if (captureType == 1)	// if rgb color, 3 bytes/pixel, otherwise 1 byte/pixel for grayscale
			bytesPerPixel = 3;
		frameSize = width * height * bytesPerPixel;
		
		int maxReadSize = frameSize;
		readBuf = new byte[frameSize];			// analysis form - temp buffer for raw received data
		vidBuf = new byte[frameSize];			// raw image bytes
		vidDispBuf = new int[frameSize];		// display version (alpha set, bytes replicated if needed)
		int vidDispBufIx = 0;
		int vidBufIx = 0;
		
		readBuf[0] = (byte)(200 + captureType);	// send the "capture" command
		byte[] t = new byte[4];
		
		try {
			out.write(readBuf, 0, 1);
			out.flush();
			
			readLength = in.read(t, 0, 4); // read imgWidth
			if (readLength != 4) 
				return -4;
			imgWidth = ((t[2] << 8) & 0xff00) | t[3] & 0xff;	// I think this way of getting byte to int is broken
			if (imgWidth != width) {
				System.out.println("Width mismatch, received " + t[0] + " " + t[1] + " " + t[2] + " " + t[3]);
				return -1;
			}		
			readLength = in.read(t, 0, 4); // read imgHeight
			//System.out.println(t[0] + " " + t[1] + " " + t[2] + " " + t[3]);
			if (readLength != 4) 
				return -5;
			imgHeight = (int)(t[2] & 0xff) << 8 | t[3] & 0xff;
			//System.out.println(t[0] + " " + t[1] + " " + t[2] + " " + t[3]);
			if (imgHeight != height)
				return -2;

			readLength = in.read(t, 0, 4); // read imgLength
			if (readLength != 4) 
				return -6;
			rxImageSize = ((t[1] << 16) & 0xff0000) | ((t[2] << 8) & 0xff00) | (int)t[3] & 0xff;
			//System.out.println(t[0] + " " + t[1] + " " + t[2] + " " + t[3]);
			if (rxImageSize != frameSize)
				return -3;

			//System.out.println("Chumby reports image size " + imgWidth + "x" + imgHeight + " length " + rxImageSize + " for frameSize " + frameSize);


			// ACHTUNG - readBuf gets overwritten at the beginning by multiple read buffers - use vidDispBuf or vidBuf for image
			
			for (int i=0; i<100; i++) {
				readLength = in.read(readBuf, 0, maxReadSize);
				maxReadSize -= readLength;
				for (int j=0; j<readLength; j++) {
					vidBuf[vidBufIx++] = readBuf[j];
					if (captureType == 0) {	 
						// grayscale; copy pixel luminance into r, g, b bytes in vidDispBuf & set alpha to opaque
						vidDispBuf[vidDispBufIx++] = (255 << 24) | (int)(readBuf[j] & 0xff) << 16 
							| (int)(readBuf[j] & 0xff) << 8 | (readBuf[j] & 0xff);		
					} else {	
						// color; copy each byte (shifted) into the pixel int, setting alpha channel to 255, then store int
						switch (rgbShiftSize) {
						case 16: 
							pixel |= (readBuf[j] << rgbShiftSize) & 0xff0000;
							rgbShiftSize = 8;
							break;
						case 8:
							pixel |= (readBuf[j] << rgbShiftSize) & 0xff00;
							rgbShiftSize = 0;
							break;
						case 0:
							pixel |= (readBuf[j]) & 0xff;
							vidDispBuf[vidDispBufIx++] = pixel;
							pixel = 255 << 24;
							rgbShiftSize = 16;
							break;
						default:
							System.err.println("Illegal value for rgbShiftSize " + rgbShiftSize);
							return -100;
						}
					}
				}
				if (maxReadSize == 0)
					break;
				try {
					Thread.sleep(20);
					//System.out.println("Have " + vidBufIx + ", trying again");
				} catch (Exception e) {
					System.out.print(e);
				}
			}
		} catch (IOException e) {
			System.out.println("I/O exception");
			e.printStackTrace();
			System.exit(-1);			
		}
		
		//System.out.println("readFrame read " + frameSize + " bytes");
		System.out.print(".");
		frameCount++;
		if (frameCount%128 == 0)
			System.out.println();
		return (vidDispBufIx);
	}
	
	/*
	 * Flush anything still in the pipe, so we can start a new frame
	 */
	public void flushFrame()
	{	
		readBuf = new byte[1000000];			// analysis form - temp buffer for raw received data
		long endTime;
		int readLength;
		
		endTime = System.currentTimeMillis() + 1000;	
		try {
			while (endTime > System.currentTimeMillis()) {
				readLength = in.read(readBuf, 0, 1000000);
				
			}			
		} catch (IOException e) {
			System.out.println("I/O exception");
			e.printStackTrace();
			System.exit(-1);						
		}
	}
	/*
	 * Develop a normalization array that compensates for illumination variations in each voxel.
	 * This method returns an array of doubles, one per voxel, containing the factor by which each
	 * voxel must be multiplied to normalize its value to 0xc0. getFrame must have been called with the 
	 * camera looking at white before this is called. It processes vidDispBuf at the requested rows.
	 */
	public double [] normalizeRows(int startRow, int endRow)
	{
		int start, end;
		
		assert (endRow <= startRow);
		start = startRow * imgWidth;
		end = endRow * imgWidth;
		double [] normalizeArray = new double[end - start];
		for (int i=start; i<end; i++) {
			normalizeArray[i] = 192.0 / (double)(vidDispBuf[start + i]);
		}
		return (normalizeArray);
	}
	
	/*
	 * quantize the start row through end row-1 (looking down the image)
	 * See UTD Prof Schweitzer's notes on "Thresholding by Quantization"
	 */
	public int quantizeRows (int startRow, int endRow) 
	{
		assert (endRow <= startRow);
		qStartRow = startRow;
		qEndRow = endRow;
		return(quantize(startRow*imgWidth, endRow*imgWidth));
	}
	// take a start & end offset into the readBuf array
	private int quantize(int start, int end) { 
		int[] h, xh;	// the histogram of the slice, and x * histogram
		int[] q1, q2, e;	// array of possible quantization values & Error for each t
		int t, tMin, tMax;
		int eMin, threshold;
		int sigmaXhQ1, sigmaHQ1, sigmaXhQ2, sigmaHQ2;
		int sigmaXQ1H, sigmaXQ2H;
		int x;
		int qtmp;
		
		if (end <= start) {
			System.out.println("error: quantize end < start");
			System.exit(0);
		}
		// array values zeroed on create
		h = new int[256];	
		xh = new int[256];
		q1 = new int[256];
		q2 = new int[256];
		e = new int[256]; 	// E term (error) at t value 
		qDisplayBuf = new int[(end-start)];
		q = new int[(end-start)];
		sliceBufInt = new int[(end-start)];

		// mark the live image with start/end
		for (int i=(start-imgWidth); i<start; i++) {
			if (i < 0) break;
			vidDispBuf[i] = 0xff<<24 | 0xff;
		}
		for (int i=end; i<(end+imgWidth); i++) {
			vidDispBuf[i] = 0xff<<24 | 0xff;			
		}
		// build the histogram
		for (int i=(start), j=0; i<(end); i++, j++) {
			x = (int)(vidDispBuf[i])& 0xff;	
			h[x]++;	// increment the appropriate histogram bucket for this image value
			sliceBufInt[j] = x | ((x<<8)&0xff00) | ((x<<16)& 0xff0000) | (0xff<<24);
		}
		// calculate x * h(x) & store in xh, and print histogram values for testing
		//System.out.println("Histogram: x, h, xh");
		tMin = 0;
		tMax = 0;
		for (int j=0; j<h.length; j++) {
			xh[j] = j * h[j];
			if (tMin == 0) {		// initialize tMin to the next t value after the first non-zero histogram bucket (avoid divide-by-zero)
				if (h[j] != 0)
					tMin = j+1;
			} else {
				if (h[j] != 0) {
					tMax = j-1;
				}
			}
			//if (h[j] != 0) 	System.out.println(j + "\t" + h[j] + "\t" + xh[j]);
		}
		if ((tMax - tMin) < 3) {
			System.out.println("Error: image is too uniform in value - abandoning quantization");
			return(-1);
		}
		
		// build arrays of q1, q2. Start summation at the first non-zero histogram index
		t = tMin;
		sigmaXhQ1 = xh[tMin-1];
		sigmaHQ1 = h[tMin-1];
		
		// initialize the q2 summations
		sigmaXhQ2 = 0;
		sigmaHQ2 = 0;
		for (x=tMin; x<256; x++) {
			sigmaXhQ2 += xh[x];
			sigmaHQ2 += h[x];
		}
		
		// calculate q1 & q2 arrays for t = 1 to t = 254
		do {
			q1[t] = sigmaXhQ1 / sigmaHQ1;	
			q2[t] = sigmaXhQ2 / sigmaHQ2;
			sigmaXhQ1 += xh[t]; // incrementing t means sigma**Q1 gets one more histogram value, and
			sigmaHQ1 += h[t];	// sigma**Q2 loses that same one
			sigmaXhQ2-= xh[t];
			sigmaHQ2 -= h[t];
			if (sigmaXhQ2 == 0) {	// if we reach the highest luminance value, set tMax & bail
				tMax = t;
				break;
			}
			t++;		
		} while (t<255);
		
		// calculate e array for t=1 to t=254
		//System.out.println("\nt\tq1\tq2\tsgmQ1H\tsgmQ2H\te");	// print the header for diagnostic prints
		for (t=tMin; t<tMax; t++) {
			for (x=tMin-1, qtmp = q1[t], sigmaXQ1H=0; x<t; x++) {
				sigmaXQ1H += (Math.pow((x - qtmp),2)) * h[x];
			}
			for (x=t, qtmp = q2[t], sigmaXQ2H=0; x<tMax; x++) {
				sigmaXQ2H += (java.lang.Math.pow((x - qtmp),2)) * h[x];
			}
			e[t] = sigmaXQ1H + sigmaXQ2H;
			//System.out.println(t + "\t" + q1[t] + "\t" + q2[t] + "\t" + sigmaXQ1H + "\t" + sigmaXQ2H + "\t" + e[t]);
		}
		
		// find minimum e & corresponding t
		eMin = (int)2E9;	// close to max positive number
		threshold = 1;
		for (t=tMin; t<tMax; t++) {
			if (e[t] < eMin) {
				eMin = e[t];
				threshold = t;
			}
		}
		//System.out.println("Threshold = " + threshold + " q1 = " + q1[t] + " q2 = " + q2[t] + "\n");
		
		// Create the quantized image
		x = 0;
		if (thresholdOverride != 0)
			threshold = thresholdOverride;
		for (int srcIx=start; srcIx<end; srcIx++, x++) {
			qDisplayBuf[x] = ((vidDispBuf[srcIx] & 0xff) < threshold) ? 0xff<<24 : -1;	// assign quantized values to each pixel
			q[x] = ((vidDispBuf[srcIx] & 0xff) < threshold) ? 0 : 1;	// assign quantized values to each pixel
		}
		return(0);
	}
	
	/*
	 * segment the image into lines by doing an initial smoothing & averaging which ignores regions of black with fewer than 3
	 * black pixels in a 4-pixel vertical line. This produces a 1-line array of ints representing black or white at that
	 * portion of the image (called a smoothSlice). Then scan the smoothSlice and extract white-black-white transitions
	 * into an array container of found line boundaries. 
	 * Note: this is designed to track black on white, but this is where tracking white on black would be supported
	 */
	public int segmentImage()
	{
		int off1, off2, off3;	// offsets into quantized array
		boolean inBlack = false;	// initially assume we're in white (virtual white at beginning of slice)
		int blackStart, blackEnd;
		
		lineList.clear();		// clear out previous lines
		// average 4 vertical pixels to decide whether this point of the slice is white or black
		off1 = imgWidth;
		off2 = imgWidth * 2;
		off3 = imgWidth * 3;
		for (int i=0; i<imgWidth; i++) {
			int whiteCnt = q[i] + q[i+off1] + q[i+off2] + q[i+off3];
			if (whiteCnt < 3)
				smoothSlice[i] = 0;
			else
				smoothSlice[i] = 1;
		}
		
		// scan the slice & pick out the black regions > minLineThickness & create a LineBoundary for each
		blackStart = blackEnd = 0;
		for (int i=0; i<imgWidth; i++) {
			if (inBlack) {	// we're in a black part of the image (can never happen on element 0)
				if ((smoothSlice[i] == 1) || (i == imgWidth-1)) {	// were in black, just transitioned to white, or end of array
					blackEnd = i-1;
					inBlack = false;
					if (((blackEnd - blackStart) > minLineThickness) && ((blackEnd - blackStart) < maxLineThickness)) {
						LineBoundary lb = new LineBoundary();
						lb.start = blackStart - (imgWidth/2);
						lb.end = blackEnd - (imgWidth/2);
						lb.middle = ((lb.start + lb.end)/2);
						lineList.add(lb);
					}	// else ignore this as a false line (noise) - we're in white now
				}
			} else {	// inWhite
				if (smoothSlice[i] == 0) {
					inBlack = true;	// were in white, just transitioned to black (can happen on element 0)
					blackStart = i;
				}
			}
		}
		
		// print found lines
//		System.out.print("Found " + lineList.size() + " lines: ");
//		for (LineBoundary l : lineList) {
//			System.out.print(l.start + " " + l.middle + " " + l.end + " ");
//		}
//		System.out.println();
		
		return(0);
	}
	
	/*
	 * Find the line we should be following. The very first time this runs, or if it loses the line & backs up
	 * it will pick the line closest to center. Thereafter it chooses the first found line who's center is within
	 * the boundaries of the last line it chose. If it can't find one, it returns an error. Therefore it will always take
	 * a left fork. It returns the middle value of the chosen line, or a large value if error.
	 */
	public int trackLine()
	{
		int m;
		LineBoundary lTmp = new LineBoundary();
		// clear out current markers in the tracking display
		m = currentLine.middle + imgWidth/2;
		ltBuf[m + 3*imgWidth] = ltBuf[m + 2*imgWidth] = ltBuf[m + imgWidth] = ltBuf[m] = 0;
		
		if (lineList.size() == 0) {
			if (currentLine.middle >= 0) {
				System.out.println("Error: line disappeared to right, last seen at " + currentLine.middle);
				return(100);				
			} else {
				System.out.println("Error: line disappeared to left, last seen at " + currentLine.middle);
				return (-100);				
			}
		}
		if (first == 1) {
			lTmp.middle = 100; 	// any found line will be closer than this
			for (LineBoundary lb : lineList) {
				if (Math.abs(lb.middle) < Math.abs(lTmp.middle)) {
					lTmp = lb;	// save the new lineBoundary with the lowest absolute value of middle
				}
			}
			currentLine = lTmp;
			first = 0;
			System.out.print("Picked line center at " + lTmp.middle);
			return(lTmp.middle);
		} else {
			if (lineList.size() == 1) {
				System.out.println("tracking line center at " + lineList.get(0).middle + " width: " + (lineList.get(0).end-lineList.get(0).start));
				currentLine = lineList.get(0);
				return (lineList.get(0).middle);				
			}
			for (LineBoundary lb : lineList) {
				if ((lb.middle > currentLine.start-20) && (lb.middle < currentLine.end + 20)) {
					System.out.println("tracking line center at " + lb.middle + " width: " + (lb.end-lb.start));
					currentLine = lb;
					// write the image of the new currentLine middle
					m = lb.middle+(imgWidth/2);
					ltBuf[m] = 0xff<<24 | 0xff<<16;
					ltBuf[m + 3*imgWidth] = ltBuf[m + 2*imgWidth] = ltBuf[m + imgWidth] = ltBuf[m];
					return (lb.middle);
				}
			}
		}
		System.out.println("Error: Lost the line I was tracking");
		first = 1;
		return(2001);
	}
	public void testQuantization()
	{
		byte[] testArray1 = 
			{6, 6, 6, 10, 
			6, 6, 6, 10,
			17, 17, 17, 17,
			17, 17, 17, 88};
		for (int i=0; i<testArray1.length; i++) {
			readBuf[i] = testArray1[i];
		}
		quantize(0, testArray1.length);		
	}
	void dumpVideo(int row)
	{
		System.out.print("row " + row + " ");

		for (int col=0; col<160; col++) {
			System.out.print((int)readBuf[row*160 + col] + " ");
		}
		System.out.println();
	}

}
