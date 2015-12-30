/*
 * roombacomm.WatchVideo -- test out the video subsystem without robot motion
 *
 *  Copyright (c) 2009 Paul Bouchier, bouchier@at@classicnet.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General
 *  Public License along with this library; if not, write to the
 *  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA  02111-1307  USA
 *
 */
package com.hackingroomba.roombacomm;

import jargs.gnu.CmdLineParser;
import java.io.*;


/**	
	    Run it with something like: <pre>
	    java roombacomm.WatchVideo --videoServer 192.168.0.150 --videoPortNum 5005 -x 160 -y 120 --debug<br>
     Usage:
       roombacomm.WatchVideo --videoServer <IP> --videoPortNum <port> 
         -x <image X size> -y <image Y size> [options]<br>
     where 
     [options] can be one or more of:
      -debug       -- turn on debug output
      -hwhandshake -- use hardware-handshaking, for Windows Bluetooth
      -nohwhandshake -- don't use hardware-handshaking
	   </pre>
*/	 
	public class watchVideo {
	    
		private int height = 120;
		private int width = 160;
		String usage = 
	        "Usage: \n"+
	        "  roombacomm.Roborama --videoServer <IP> --videoPortNum <port> -x <image X size> -y <image Y size>[options]\n" +
	        "where [options] can be one or more of:\n"+
	        " -X | --debug       -- turn on debug output\n"+
	        " -x <width> or --width <width>: set width\n" +
	        " -y <height> or --height <height> -- set height\n" +
	        " -c | --color -- use color mode, otherwise grayscale\n" +
	        " --videoserver <IP> : set IP address of video server\n" +
	        " --videoPortNum <port> : set port number on video server\n" +
	        " -t | --threshold <threshold> : set quantization threshold\n" +
	        " -R | --RoboRealm : connect to Roborealm & send image to it. Roborealm requires color\n" +
	        " -hwhandshake -- use hardware-handshaking, for Windows Bluetooth\n";

	    boolean debug = false;
		private int waittime;
		private int thresholdOverride = 0;
		private String videoServer = "";
		private int videoPortNum = 5005 ;
		private int videoRowStart = 50;
		private int videoRowEnd = 54;
    	private int pixelCnt;
		private boolean color = false;		// when true, capture images in color
		private boolean roborealm = false; 	// when true, connect & send images to RoboRealm
		FrameProcessor fp;
	    
		public watchVideo() {
			// constructor, mustn't throw exceptions. Do nothing for now
		}
		
		// main - it all starts here
		public static void main(String[] args) {
			watchVideo b = new watchVideo();
			b.parseCmd(args);
        	b.initVideo();
			b.displayVideo();
		}
		
		public void displayVideo()
		{
			int frameCount = 0;
			long startTime, runTime;
			float avgFrameTime;

			startTime = System.currentTimeMillis();
			
			while (true) {
				getShowFrame();
				frameCount++;
				if (frameCount%128 == 0) {
					runTime = System.currentTimeMillis() - startTime;
					avgFrameTime = runTime / frameCount;
					System.out.println("Displayed " + frameCount + " frames in " + runTime/1000 
							+ " secs; avg frametime: " + avgFrameTime + "ms");					
				}
				if (roborealm) {
					try {
						getVisualHeading();						
					} catch (Exception e) {
						System.out.println("Error: no Roborealm data returned");						
					}
				}
			}
		}

		public int getVisualHeading() throws Exception
		{
			String rrString;
			//RoboRealm variables
			int rrShapeX;			// X location in image frame of detected shape from RoboRealm
			int rrConfidence;
			int rrSize;
			int rrRelativeHeading = 0;
			double conf;

			Boolean rv = fp.frame2Roborealm();	
			if (rv == false)
				System.out.println("error sending image to Roborealm");
			rrString = fp.getShapeData();
			if (rrString != null) {
				String s[] = rrString.split(",");  
				int xL, xR;
				xL = Integer.parseInt(s[3]);
				xR = Integer.parseInt(s[4]);
				rrShapeX = (xL + xR)/2;
				rrRelativeHeading = (rrShapeX - (getWidth()/2))/10;	// distance from image center in degrees
				conf = Double.parseDouble("0." + s[0]);
				conf *= 100;		// convert to %
				rrConfidence = (int)conf;
				rrSize = Integer.parseInt(s[2]);

				System.out.println("Found shape confidence " + rrConfidence + " at " + rrShapeX + " bearing: " + rrRelativeHeading);
			} else {
				//System.out.println("No RobotRealm data returned");
				throw new Exception("Error: no Roborealm data returned");
			}
			return rrRelativeHeading;
		}

		/**
		 * Get a frame from the video server and display it
		 */
		public void getShowFrame() {
			pixelCnt = fp.readFrame(getWidth(), getHeight(), color?1:0);
			if (pixelCnt != (getWidth() * getHeight())) {
				System.out.println("getShowFrame read " + pixelCnt + " bytes - abandoning frame");
				fp.flushFrame();
				return;
			}

			// output the frame to wherever it's wanted
			fp.displayFrame();
			return;
		}

		/**
		 * Connect to the video server, and to RoboRealm if requested
		 */
		public FrameProcessor initVideo() {
			if (getVideoServer() == null || getVideoServer().length() == 0) {
        		System.err.println(" you must supply a --videoServer value to use the command \"getVideo\"");
        		if (getVideoPortNum() <= 0) {
	        		System.err.println(" you must supply a --videoPortNum value to use the command \"getVideo\"");
        		}
        		System.exit(6);
        	}
        	if (getVideoPortNum() <= 0) {
        		System.err.println(" you must supply a --videoPortNum value to use the command \"getVideo\"");
        		System.exit(7);
        	}

        	fp = new FrameProcessor(getVideoServer(),getVideoPortNum(), 0, 0, 0, 0, getThresholdOverride());
        	//fp.testQuantization();
        	fp.createAndShowGUI();
        	if (roborealm) {
        		color = true;	// force color mode if roborealm was requested
        	}
        	
    		fp.connect(roborealm);		// connect to the video server, and to Roborealm too if requested
        	return fp;
		}
		
		public void parseCmd(String[] args){
			System.out.println("*** start of WatchVideo WparseCmd");

	        CmdLineParser parser = new CmdLineParser();
	        CmdLineParser.Option debugOption = parser.addBooleanOption('X', "debug");
//	        CmdLineParser.Option verboseOption = parser.addBooleanOption('W', "Verbose");
	        CmdLineParser.Option widthOption = parser.addIntegerOption('x', "width");
	        CmdLineParser.Option heightOption = parser.addIntegerOption('y', "height");
	        CmdLineParser.Option videoServerOption = parser.addStringOption("videoServer");
	        CmdLineParser.Option videoPortNumOption = parser.addIntegerOption("videoPortNum");
	        CmdLineParser.Option thresholdOption = parser.addIntegerOption('t', "threshold");
	        CmdLineParser.Option hwHandShakeOption = parser.addBooleanOption("nohwhandshake");
	        CmdLineParser.Option colorOption = parser.addBooleanOption('c', "color");
	        CmdLineParser.Option roborealmOption = parser.addBooleanOption('R', "roborealm");
	        try {
	            parser.parse(args);
	        }
	        catch ( CmdLineParser.OptionException e ) {
	            System.err.println(e.getMessage());
	            System.out.println("parseCmd had an error\n"+ usage );
	            System.exit(2);
	        }	        
	        
	        //	        String portname = args[0];  // e.g. "/dev/cu.KeySerial1", or "COM5" or "192.168.1.1"
	        setThresholdOverride(((Integer)parser.getOptionValue(thresholdOption, getThresholdOverride())).intValue());
	        setWidth(((Integer)parser.getOptionValue(widthOption,getWidth())).intValue());
	        setHeight(((Integer)parser.getOptionValue(heightOption,getHeight())).intValue());
	        setVideoServer(((String)parser.getOptionValue(videoServerOption)));
	        setVideoPortNum(((Integer)parser.getOptionValue(videoPortNumOption, getVideoPortNum())).intValue());
			//	        String cmd = args[1+argOffset];

	        Boolean debugBool = (Boolean)parser.getOptionValue(debugOption,new Boolean(false));
	        setDebug(debugBool.booleanValue());
	        System.out.println("debug is ("+isDebug()+")");
	        Boolean hwHandShakeBool = (Boolean)parser.getOptionValue(hwHandShakeOption, new Boolean(false));
	        setThresholdOverride((Integer)parser.getOptionValue(thresholdOption, new Integer(0)));
	        System.out.println("thresholdOverride is " + getThresholdOverride());
	        color = (Boolean)parser.getOptionValue(colorOption, new Boolean(false));
	        roborealm = (Boolean)parser.getOptionValue(roborealmOption, new Boolean(false));
	        System.out.println("color mode is " + color + ", roborealm mode is " + roborealm);
	        System.out.println("*** end of parseCmd");
	    }

		public boolean isDebug() {
			return debug;
		}
		public void setDebug(boolean debug_) {
			debug = debug_;
		}
		public int getWaittime() {
			return waittime;
		}
		public void setWaittime(int waittime_) {
			waittime = waittime_;
		}
		public int getThresholdOverride() {
			return thresholdOverride;
		}
		public void setThresholdOverride(int thresholdOverride) {
			this.thresholdOverride = thresholdOverride;
		}
		protected int getHeight() {
			return height;
		}
		protected void setHeight(int height) {
			this.height = height;
		}
		protected int getWidth() {
			return width;
		}
		protected void setWidth(int width) {
			this.width = width;
		}
		/**
		 * @return the videoServer
		 */
		protected String getVideoServer() {
			return videoServer;
		}
		/**
		 * @param videoServer the videoServer to set
		 */
		protected void setVideoServer(String videoServer) {
			this.videoServer = videoServer;
		}
		/**
		 * @return the videoPortNum
		 */
		protected int getVideoPortNum() {
			return videoPortNum;
		}
		/**
		 * @param videoPortNum the videoPortNum to set
		 */
		protected void setVideoPortNum(int videoPortNum) {
			this.videoPortNum = videoPortNum;
		}

		public boolean isColor() {
			return color;
		}

		public void setColor(boolean color) {
			this.color = color;
		}

		public boolean isRoborealm() {
			return roborealm;
		}

		public void setRoborealm(boolean roborealm) {
			this.roborealm = roborealm;
		}
		
	}
	
	