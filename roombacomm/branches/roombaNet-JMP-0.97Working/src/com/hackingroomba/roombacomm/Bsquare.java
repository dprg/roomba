/*
 * roombacomm.Bsquare -- test out the Bsquare command
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
import com.hackingroomba.roombacomm.RoombaCommTCPClient;
import java.io.*;


/**	
	   Drive the Roomba forward, back, or CW or CCW square<br>
	  <p>
	   Run it with something like: <pre>
	    java roombacomm.Bsquare /dev/cu.KeySerial1 [protocol] command velocity distance<br>
     Usage:
       roombacomm.Bsquare <serialportname> [protocol] <command> <velocity> <distance> [options]<br>
     where 
     protocol (optional) is SCI or OI
     command is one of:
      f -- forward or back; direction controlled by +/- speed
	  bcw or bccw -- clockwise or counter-clockwise b-square
	  onb -- out and back
     [options] can be one or more of:
      -debug       -- turn on debug output
      -hwhandshake -- use hardware-handshaking, for Windows Bluetooth
      -nohwhandshake -- don't use hardware-handshaking
     velocity and distance are in inches\n";
	   </pre>
*/	 
	public class Bsquare {
	    
	    private int maxLineWidth = 30;
		private int minLineWidth = 5;
		private int height = 120;
		private int width = 160;
		String usage = 
	        "Usage: \n"+
	        "  roombacomm.Bsquare <serialportname> [protocol] <command> <velocity> <distance> [options]\n" +
	        "where <command> is one of:\n" +
	        "  f: go forward or back based on velocity for the specified distance\n" +
	        "  bcw: do a clockwise Borenstein square with leg size specified\n" +
	        "  bccw: do a counter-clockwise Borenstein square with leg size specified\n" +
	        "  onb: do out'n'back for distance specified\n" +
	        "  fig8: do figure 8 with length specified (width is hard-coded to 3 feet)\n" +
	        "and where [options] can be one or more of:\n"+
	        " -debug       -- turn on debug output\n"+
	        " -hwhandshake -- use hardware-handshaking, for Windows Bluetooth\n"+
	        "velocity and distance are in mm/s and mm\n";
	    boolean debug = false;
	    boolean hwhandshake = false;
		byte [] sensorBytes;	// The last bytes read by roombacomm library
		byte bumpSensor;
	    short odoDistance;
	    int leftEncoder, rightEncoder;
	    int leftMm, rightMm;
		int beginLeft, beginRight;
	    private final static double countsPerMm = 2.57;
	    private int minVelocity = 50;
	    RoombaComm roombacomm;
		private String portname;
		private String cmd;
		private int velocity;
		private int waittime;
		private String protocol;
		private int distance = 0 ;
		private int angle;
		private int radius;
		private int thresholdOverride = 0;
		private String videoServer = "";
		private int videoPortNum = 0 ;
		private int videoRowStart = 50;
		private int videoRowEnd = 54;
	    
		public Bsquare() {
			// constructor, mustn't throw exceptions. Do nothing for now
		}
		
		// main - it all starts here
		public static void main(String[] args) {
			Bsquare b = new Bsquare();
			b.doCommand(args);
		}
		
		public void f()
		{
        	/*
        	// get sensor values before starting
        	roombacomm.queryList(sensorList, 6);
        	boolean sensorStatus = roombacomm.wait4Sensors();
        	if (sensorStatus == false) {
        		System.out.println("Failed to read sensors");		// wait until sensorsValid true, when 6 bytes ready
        		System.exit(-1);
        	}
        	sensorBytes = roombacomm.getSensor_bytes();
        	odoDistance = roombacomm.toShort(sensorBytes[0], sensorBytes[1]);
        	leftEncoder = roombacomm.toShort(sensorBytes[2], sensorBytes[3]);
        	rightEncoder = roombacomm.toShort(sensorBytes[4], sensorBytes[5]);
        	System.out.println("Odometry sensors: distance: " + odoDistance + " left encoder: "
        			+ leftEncoder + " rightEncoder " + rightEncoder);
*/
        	// now drive
            roombacomm.drive( getVelocity(), 0x8000 );
            roombacomm.pause(getWaittime());
            roombacomm.stop();
            roombacomm.pause(250);		// wait for robot to stop moving
/*	            
            // get sensor values after the run
        	roombacomm.queryList(sensorList, 6);
        	sensorStatus = roombacomm.wait4Sensors();
        	if (sensorStatus == false) {
        		System.out.println("Failed to read sensors");		// wait until sensorsValid true, when 6 bytes ready
        		System.exit(-1);
        	}
        	sensorBytes = roombacomm.getSensor_bytes();
        	odoDistance = roombacomm.toShort(sensorBytes[0], sensorBytes[1]);
        	leftEncoder = roombacomm.toShort(sensorBytes[2], sensorBytes[3]);
        	rightEncoder = roombacomm.toShort(sensorBytes[4], sensorBytes[5]);
        	System.out.println("Odometry sensors: distance: " + odoDistance + " left encoder: "
        			+ leftEncoder + " rightEncoder " + rightEncoder);
*/	            
		}

		void initOdometry()
		{
			beginLeft = beginRight = 0;
        	// get sensor values before starting
        	getOdometry();		// get 5 bytes of return data
        	beginLeft = leftEncoder;
        	beginRight = rightEncoder;
        	getOdometry();		// update leftMm & rightMm (should be 0)
        	printOdometry();			
		}
		boolean getOdometry()
		{
        	byte[] sensorList = {7, 43, 44};	// bump/wheeldrop, and encoders
        	int returnLength = 5;
        	
        	roombacomm.queryList(sensorList, returnLength);
            roombacomm.logmsg("updateSensors: pausing.");

	        for(int i=0; i < 20; i++) {
	            if( roombacomm.sensorsValid() ) { 
	                break;
	            }
	            roombacomm.pause( 50 );
	        }
	        if (!roombacomm.sensorsValid()) {
	        	System.out.println("ERROR: unable to read sensors");
	        	return(false);
	        }
        	sensorBytes = roombacomm.getSensor_bytes();
        	bumpSensor = sensorBytes[0];
        	leftEncoder = RoombaComm.toUnsignedShort(sensorBytes[1], sensorBytes[2]);
        	leftMm = (int)((leftEncoder - beginLeft) / countsPerMm);
        	rightEncoder = RoombaComm.toUnsignedShort(sensorBytes[3], sensorBytes[4]);
        	rightMm = (int)((rightEncoder - beginRight) / countsPerMm);
			return(true);
		}
		void printOdometry() 
		{
        	System.out.println("Odometry sensors: left encoder: "
        			+ leftEncoder + " (" + leftMm + "mm), rightEncoder " + rightEncoder + " (" + rightMm + "mm) bump: " + bumpSensor);
			
		}
		public void fod()	// forward using odometry
		{
			int rampDownDistance;
			int currentVelocity = minVelocity;	// starting velocity
			long nextUpdateTime;
			boolean rampUp, rampDown;
			int currentRadius = radius;
			int initialDifference;
			int distanceTravelled;
			
			initOdometry();
        	initialDifference = leftEncoder - rightEncoder;
        	rampDownDistance = (distance - 150); // - (int)(Math.pow(velocity, 2)/ 200);
        	System.out.println("target: " + distance + " rampdown Distance: " + rampDownDistance);
        	rampUp = true;
        	rampDown = false;
        	
        	// every 100ms calculate new speed & correct course
        	roombacomm.logmsg("velocity: " + velocity + " rampUp: " + rampUp + " rampDown: " + rampDown);
        	do {
        		nextUpdateTime = System.currentTimeMillis() + 100;
                if (rampUp == true) {
                	currentVelocity += 20;
                	if (currentVelocity >= velocity) {
                		currentVelocity = velocity;
                		rampUp = false;
                	}
                }
                if (rampDown == true) {
                	currentVelocity -= 20;
                	if (currentVelocity < minVelocity) {
                		currentVelocity = minVelocity;
                	}
                }
                getOdometry();
            	printOdometry();
            	
            	// check whether we hit anything            	
            	if (bumpSensor != 0) {
            		System.out.println("Hit bumper - stopping");
            		roombacomm.stop();
            		roombacomm.powerOff();
            		System.exit(-1);
            	}
            	
            	// Check which wheel to track for distance forward, or track average if not spinning
            	if (radius == 1) {
            		distanceTravelled = rightMm;
            	} else if (radius == -1) {
            		distanceTravelled = leftMm;
            	} else {
            		distanceTravelled = leftMm;
            	}
            	
                if (distanceTravelled > rampDownDistance) {
                	rampUp = false;
                	rampDown = true;
                }
                
                if (distanceTravelled > distance)
                	break;
                
                System.out.println("encoder difference: " + (leftEncoder - rightEncoder - initialDifference));
                //System.out.println("vel " + currentVelocity + " rampUp " + rampUp + " rampDown " + rampDown);
        		roombacomm.logmsg("driving at: " + currentVelocity + " radius: " + radius + " leftMm: " + leftMm);
                roombacomm.drive( currentVelocity, radius );
                
                // Sleep till next tick
                if (System.currentTimeMillis() < nextUpdateTime) {
                    try { 
                    	Thread.sleep(nextUpdateTime - System.currentTimeMillis()); 
                    } catch(Exception e) {
                    	e.printStackTrace(); 
                    }             	
                } else {
                	System.out.println("WARNING: missed frame by " + (System.currentTimeMillis() - nextUpdateTime) + "ms");
                }
        	} while (distanceTravelled < distance);

        	roombacomm.stop();
            roombacomm.pause(500);		// wait for roomba to settle
            
        	getOdometry();		// get 5 bytes of return data
        	printOdometry();
        	
        	System.out.println("Ldistance: " + (leftEncoder - beginLeft) + " Rdistance: " + (rightEncoder - beginRight));


		}
		
		public void bcwod()
		{
			int tempDistance = distance;
			int tempRadius = radius;
			int turnDistance = angle;	// use --angle to set turnDistance
			
			// left leg
			distance = tempDistance;
			radius = tempRadius;
			fod();
			
			radius = -1;
			distance = turnDistance;
			fod();

			// top leg
			distance = tempDistance;
			radius = tempRadius;
			fod();
			
			radius = -1;
			distance = turnDistance;
			fod();

			// right leg
			distance = tempDistance;
			radius = tempRadius;
			fod();
			
			radius = -1;
			distance = turnDistance;
			fod();

			// bottom leg
			distance = tempDistance;
			radius = tempRadius;
			fod();
			
			radius = -1;
			distance = turnDistance;
			fod();

		}
		public void bcw()
		{
        	roombacomm.drive( velocity, radius );
            roombacomm.pause(waittime);
            roombacomm.stop();
            roombacomm.pause(1000);
            roombacomm.spin(-angle);
            roombacomm.pause(1000);
       
        	roombacomm.drive( velocity, radius );
            roombacomm.pause(waittime);
            roombacomm.stop();
            roombacomm.pause(1000);
            roombacomm.spin(-angle);
            roombacomm.pause(1000);
            
        	roombacomm.drive( velocity, radius );
            roombacomm.pause(waittime);
            roombacomm.stop();
            roombacomm.pause(1000);
            roombacomm.spin(-angle);
            roombacomm.pause(1000);
            
        	roombacomm.drive( velocity, radius );
            roombacomm.pause(waittime);
            roombacomm.stop();
            roombacomm.pause(1000);
            roombacomm.spin(-angle);	            			
		}
		
		public void bccw()
		{
        	roombacomm.drive( velocity, radius );
            roombacomm.pause(waittime);
            roombacomm.stop();
            roombacomm.pause(1000);
            roombacomm.spin(angle);
            roombacomm.pause(1000);
       
        	roombacomm.drive( velocity, radius );
            roombacomm.pause(waittime);
            roombacomm.stop();
            roombacomm.pause(1000);
            roombacomm.spin(angle);
            roombacomm.pause(1000);
            
        	roombacomm.drive( velocity, radius );
            roombacomm.pause(waittime);
            roombacomm.stop();
            roombacomm.pause(1000);
            roombacomm.spin(angle);
            roombacomm.pause(1000);
            
        	roombacomm.drive( velocity, radius );
            roombacomm.pause(waittime);
            roombacomm.stop();
            roombacomm.pause(1000);
            roombacomm.spin(angle);	            			
		}
		
		public void onbod()
		{
			fod();
        	roombacomm.drive(-200, radius);
        	roombacomm.pause(500);
        	roombacomm.drive(-400, radius);
        	roombacomm.pause(500);
        	roombacomm.drive( 0-velocity, radius );
            roombacomm.pause(waittime+5000);
            roombacomm.stop();
            roombacomm.pause(1500);

		}
		public void onb()
		{
        	roombacomm.drive(200, radius);
        	roombacomm.pause(500);
        	roombacomm.drive(400, radius);
        	roombacomm.pause(500);
        	roombacomm.drive( velocity, radius );
            roombacomm.pause(waittime);
            roombacomm.stop();
            roombacomm.pause(500);
        	roombacomm.drive(-200, radius);
        	roombacomm.pause(500);
        	roombacomm.drive(-400, radius);
        	roombacomm.pause(500);
        	roombacomm.drive( 0-velocity, radius );
            roombacomm.pause(waittime);
            roombacomm.stop();
            roombacomm.pause(1000);
            //roombacomm.spin(360);
		}
		
		public void fig8()
		{
        	// calculate delay when crossing from one side of figure 8 to the other
        	int crosstime = Math.abs((1000 / velocity) * 900);	// waittime in ms (correction .9) - 3' cross
        	
        	// lower left leg
        	System.out.println("lower left leg");
        	fod();
            roombacomm.spin(-angle);
            roombacomm.pause(500); 
            
            // 1st middle crossing
            System.out.println("1st middle crossing");
            fod();
            roombacomm.spin(angle);
            roombacomm.pause(500);
            
            // upper right leg
            System.out.println("upper right leg");
            fod();
            roombacomm.spin(angle);
            roombacomm.pause(500); 
            
            // upper crossing
            System.out.println("upper crossing");
            fod();
            roombacomm.spin(angle);
            roombacomm.pause(500);

            // upper left leg
            System.out.println("upper left leg");
            fod();
            roombacomm.spin(angle);
            roombacomm.pause(500); 
            	            
            // 2nd middle crossing
            System.out.println("2nd middle crossing");
            fod();
            roombacomm.spin(-angle);
            roombacomm.pause(500);
            
        	// lower right leg
            System.out.println("lower right leg");
            fod();
            roombacomm.spin(-angle);
            roombacomm.pause(500); 
            
            // bottom crossing
            System.out.println("bottom crossing");
            fod();
            roombacomm.spin(-angle);
            roombacomm.pause(500);
		}
		
		public void followLine()
		{
        	int frameSize;
        	int trackError, trackErrorPrev1, trackErrorPrev2;
        	double loopGain = 5;
        	
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
        	trackErrorPrev1 = 0;
        	trackErrorPrev2 = 0;
        	
        	FrameProcessor fp = new FrameProcessor(getVideoServer(),getVideoPortNum(), getWidth(), getHeight(), getMinLineWidth(), getMaxLineWidth(), getThresholdOverride());
        	//fp.testQuantization();
        	fp.createAndShowGUI();
        	fp.connect();
        	
        	while (true) {
	        	frameSize = fp.readFrame(getWidth(), getHeight());
	        	//System.out.println("getVideo read " + frameSize + " bytes");
	        	if (fp.quantizeRows(videoRowStart, videoRowEnd) == 0)
	        		fp.segmentImage();
	        	fp.displayFrame();
	        	
	        	trackError = 0 - fp.trackLine();	// change error sign to correspond to desired turn direction

	        	// calculate drive radius - lots of magic numbers here
	        	if (Math.abs(trackError)<5){
	        		radius = 0x8000;
	        	} else if ((trackError >= 5) && (trackError < 74)){
	        		radius = (int)(-loopGain * trackError + 321.0);
	        		if (radius < 1)
	        			radius = 1;
	        	} else if ((trackError <= -5) && trackError > -74) {
	        		radius = (int)(-loopGain * trackError - 321);
	        		if (radius > -1)
	        			radius = -1;
	        	} else if ((trackError == -100) || (trackError == 100)) {
	        		roombacomm.stop();
		        	roombacomm.pause(1000);
		        	roombacomm.drive(50, 0x8000);
		        	roombacomm.pause(3240);		// go forward 1/2 a roomba length
		        	roombacomm.stop();
		        	roombacomm.pause(200);
		        	roombacomm.spin((trackError < 0) ? -angle : angle);	// spin 90 degrees
		        	roombacomm.stop();
		        	roombacomm.pause(200);
		        	roombacomm.drive(-50, 0x8000);	// back up 1/2 a roomba length
		        	roombacomm.pause(3240);
		        	roombacomm.stop();			// camera should be on same point we lost the line
		        	continue;
	        	} else if (trackError == 2001) {
	        		roombacomm.stop();
		        	roombacomm.pause(1000);
		        	roombacomm.drive(-50, 0x8000);
		        	roombacomm.pause(500);		// go forward 1/2 a roomba length
		        	roombacomm.stop();
	        		roombacomm.pause(200);
	        		continue;
	        	} else {
	        		System.out.println("HELP: TRACKERROR OUT OF BOUNDS: " + trackError);
	        		roombacomm.drive(0, 8000);	// stop
	        		continue;
	        	}
	        	roombacomm.logmsg(" Driving radius: " + radius);
	        	roombacomm.drive(velocity, radius);
	        	
        	}
        	//frameSize = fp.readFrame();
        	//System.out.println("getVideo read " + frameSize + " bytes");
        	//fp.disconnect();
		}
		
		public void normalize()
		{
        	int frameSize;
        	FileOutputStream fos; 
        	DataOutputStream dos;
        	
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
         	
        	FrameProcessor fp = new FrameProcessor(getVideoServer(),getVideoPortNum(), getWidth(), getHeight(), getMinLineWidth(), getMaxLineWidth(), getThresholdOverride());
        	fp.connect();
        	
	        frameSize = fp.readFrame(getWidth(), getHeight());
	        System.out.println("getVideo read " + frameSize + " bytes");
	        double [] normalizeArray = fp.normalizeRows(videoRowStart, videoRowEnd);
	        
	        try {
	            File file= new File("normalizeArray");
	            fos = new FileOutputStream(file);
	            dos=new DataOutputStream(fos);
	            for (int i=0; i<normalizeArray.length; i++) {
	            	System.out.print(normalizeArray[i] + " ");
	            	if ((i % getWidth()) == 0) {
	            		System.out.println();
	            	}
		            dos.writeDouble(normalizeArray[i]);	            	
	            }
	          } catch (IOException e) {
	            e.printStackTrace();
	          }
		}
	        		
		void doCommand(String[] args)
		{
			// parse the arguments
	    	if( args.length < 4 ) {
	    		System.out.println("args length was ("+args.length+") and it need to be >=4\n");
	            System.out.println( usage );
	            System.exit(0);
	        }
	    	parseCmd(args);
	    	
	    	// open a connection to Roomba (net or serial)
        	char portNameChar1 = getPortname().charAt(0);
	        if (portNameChar1 >= '0' && portNameChar1 <='9') {	// portname begins with number, assume it's an IP
	        	System.out.println("Using network IP " + getPortname());
		        RoombaCommTCPClient roombacommTCPClient = new RoombaCommTCPClient();
//	        	roombacommTCPClient.setProtocol(args[1]);
	        	roombacommTCPClient.setProtocol(getProtocol());
		    	if( ! roombacommTCPClient.connect( getPortname() ) ) {
		    		System.out.println("Couldn't connect to "+getPortname());
		            System.exit(1);
		        }
	        	roombacomm = roombacommTCPClient;
	        } else {
	        	System.out.println("using serial port " + getPortname());
		        RoombaCommSerial roombacommSerial = new RoombaCommSerial();
	        	roombacommSerial.setProtocol(getProtocol());
	        	roombacommSerial.setWaitForDSR(isHwhandshake());
		        if( ! roombacommSerial.connect( getPortname() ) ) {
		            System.out.println("Couldn't connect to "+getPortname());
		            System.exit(1);
		        }      
	        	roombacomm = roombacommSerial;
	        }
	        
	        // set up ^C handling
	        MyShutdown sh = new MyShutdown(roombacomm);	
	        Runtime.getRuntime().addShutdownHook(sh);
	        roombacomm.debug = isDebug();       
	        System.out.println("Roomba startup");
	        roombacomm.startup();
	        roombacomm.control();
//over ride 
	        // roombacomm.full();
	        roombacomm.pause(100);

	        // run the requested command
	        System.out.println("running command " + getCmd() + "\n");
	        if (cmd.equals("f") ) {
	        	f();
	        } else if (cmd.equals("bccw")) {
	        	bccw();
	        } else if (cmd.equals("bcw")){
	        	bcw();
	        } else if (cmd.equals("onb")) {
	        	onb();
	        } else if (cmd.equals("onbod")) {
	        	onbod();
	        } else if (cmd.equals("spin")) {
	        	for (int i=0; i<4; i++) {
	        	roombacomm.spin(-angle);
	        	roombacomm.pause(1000);
	        	}
	        } else if (cmd.equals("fig8")) {
	        	fig8();
	        } else if (cmd.equals("followLine")) {
	        	followLine();
	        } else if (cmd.equals("fod")){	// forward using odometry
	        	fod();
	        } else if (cmd.equals("bcwod")) {
	        	bcwod();
	        } else {
	        	System.out.println("Invalid Command");
	        }
	        roombacomm.stop();

	        System.out.println("Disconnecting");
	        roombacomm.disconnect();
	        
	        System.out.println("Done");
	    }

		public void parseCmd(String[] args){
			System.out.println("*** start of parseCmd");

	        CmdLineParser parser = new CmdLineParser();
	        CmdLineParser.Option debugOption = parser.addBooleanOption('X', "Debug");
//	        CmdLineParser.Option verboseOption = parser.addBooleanOption('W', "Verbose");
	        CmdLineParser.Option portNameOption = parser.addStringOption('p', "portname");
	        CmdLineParser.Option protocalOption = parser.addStringOption('a', "api");
	        CmdLineParser.Option angleOption = parser.addIntegerOption("angle");
	        CmdLineParser.Option velocityOption = parser.addIntegerOption('v', "velocity");
	        CmdLineParser.Option distanceOption = parser.addIntegerOption('d', "distance");
	        CmdLineParser.Option commandOption = parser.addStringOption('c', "command");
	        CmdLineParser.Option radiusOption = parser.addIntegerOption('r', "radius");
	        CmdLineParser.Option widthOption = parser.addIntegerOption('x', "width");
	        CmdLineParser.Option heightOption = parser.addIntegerOption('y', "height");
	        CmdLineParser.Option minlineOption = parser.addIntegerOption("min");
	        CmdLineParser.Option maxlineOption = parser.addIntegerOption("max");
	        CmdLineParser.Option videoServerOption = parser.addStringOption("videoServer");
	        CmdLineParser.Option videoPortNumOption = parser.addIntegerOption("videoPortNum");
	        CmdLineParser.Option thresholdOption = parser.addIntegerOption('t', "threshold");
	        CmdLineParser.Option hwHandShakeOption = parser.addBooleanOption("nohwhandshake");
	        try {
	            parser.parse(args);
	        }
	        catch ( CmdLineParser.OptionException e ) {
	            System.err.println(e.getMessage());
	            System.out.println("parseCmd had an error\n"+ usage );
	            System.exit(2);
	        }	        
	        
	        //	        String portname = args[0];  // e.g. "/dev/cu.KeySerial1", or "COM5" or "192.168.1.1"
	        setPortname((String)parser.getOptionValue(portNameOption));
	        System.out.println("portname is ("+getPortname()+")");
	        
	        setProtocol((String)parser.getOptionValue(protocalOption,"SCI"));
	        System.out.println("protocal is ("+getProtocol()+")");
	        setAngle((Integer)parser.getOptionValue(angleOption,new Integer(83)));
	        System.out.println("angle is ("+getAngle()+")");
	        setRadius((Integer)parser.getOptionValue(radiusOption,new Integer(0x8000)));
	        System.out.println("radius is ("+getRadius()+")");
	        if (args[1].equals("SCI") || (args[1].equals("OI"))) {	
	        } else {
	        }
	        setThresholdOverride(((Integer)parser.getOptionValue(thresholdOption, getThresholdOverride())).intValue());
	        setWidth(((Integer)parser.getOptionValue(widthOption,getWidth())).intValue());
	        setHeight(((Integer)parser.getOptionValue(heightOption,getHeight())).intValue());
	        setMinLineWidth(((Integer)parser.getOptionValue(minlineOption,getMinLineWidth())).intValue());
	        setMaxLineWidth(((Integer)parser.getOptionValue(maxlineOption,getMaxLineWidth())).intValue());
	        setVideoServer(((String)parser.getOptionValue(videoServerOption)));
	        setVideoPortNum(((Integer)parser.getOptionValue(videoPortNumOption, getVideoPortNum())).intValue());
			//	        String cmd = args[1+argOffset];
	        setCmd((String)parser.getOptionValue(commandOption,"fig8"));
	        if (getCmd().equalsIgnoreCase("followLine")){
	        	System.out.println("videoServer is ("+getVideoServer()+")");
	        	System.out.println("videoPortNum is ("+getVideoPortNum()+")");
	        }
	        Integer velocityInt = (Integer)parser.getOptionValue(velocityOption,new Integer(0));
	        Integer distanceInt = (Integer)parser.getOptionValue(distanceOption,new Integer(0));
	        try { 
//	            velocity = (int)(Integer.parseInt( args[2+argOffset]));
//	            distance   = (int)(Integer.parseInt( args[3+argOffset] ));
	        	setVelocity(velocityInt.intValue());
	        	System.out.println("velocity is ("+getVelocity()+")");
	        	setDistance(distanceInt.intValue());
	        	System.out.println("distance is ("+getDistance()+")");
	            setWaittime(Math.abs((getDistance()/getVelocity()) * 900));	// waittime in ms (correction .9)
	            System.out.println("waittime is ("+getWaittime()+")");
		        if (getWaittime() == 0) {
		        	System.out.println("Invalid waittime "+getWaittime());
		        }
		        if (getMinLineWidth() == 0) {
		        	System.out.println("Invalid MinLineWidth "+getMinLineWidth());
		        } else {
		        	System.out.println("MinLineWidth is ("+getMinLineWidth()+")");
		        }
		        if (getMaxLineWidth() == 0) {
		        	System.out.println("Invalid MaxLineWidth "+getMaxLineWidth());
		        } else {
		        	System.out.println("MaxLineWidth is ("+getMaxLineWidth()+")");
	        	}
		        System.out.println("velocity: " + getVelocity() + " distance: " + getDistance() + " waittime: " + getWaittime());
	        } catch( Exception e ) {
	        	System.err.println(e.getMessage());
	            System.err.println("Couldn't parse velocity or distance2");
	            System.exit(1);
	        }
//	        for( int i=4+argOffset; i < args.length; i++ ) {
//	            if( args[i].endsWith("debug") )
//	                debug = true;
//	        }
	        Boolean debugBool = (Boolean)parser.getOptionValue(debugOption,new Boolean(false));
	        setDebug(debugBool.booleanValue());
	        System.out.println("debug is ("+isDebug()+")");
	        Boolean hwHandShakeBool = (Boolean)parser.getOptionValue(hwHandShakeOption, new Boolean(false));
	        setHwhandshake(hwHandShakeBool.booleanValue());
	        setThresholdOverride((Integer)parser.getOptionValue(thresholdOption, new Integer(0)));
	        System.out.println("thresholdOverride is " + getThresholdOverride());
	        System.out.println("hwHandShake is ("+isHwhandshake()+")");      
	        System.out.println("*** end of parseCmd");
	    }
	    public int getRadius() {
			return radius;
		}
		public void setRadius(int rad) {
			radius = rad;
		}
		public int getDistance() {
			return distance;
		}
		public void setDistance(int dist) {
			distance = dist;
		}
		public boolean isHwhandshake() {
			return hwhandshake;
		}
		public void setHwhandshake(boolean hwhandshake_) {
			hwhandshake = hwhandshake_;
		}
		public int getAngle() {
			return angle;
		}
		public void setAngle(int ang) {
			angle = ang;
		}
		public boolean isDebug() {
			return debug;
		}
		public void setDebug(boolean debug_) {
			debug = debug_;
		}
		public String getPortname() {
			return portname;
		}
		public void setPortname(String portname_) {
			portname = portname_;
		}
		public String getCmd() {
			return cmd;
		}
		public void setCmd(String cmd_) {
			cmd = cmd_;
		}
		public int getVelocity() {
			return velocity;
		}
		public void setVelocity(int velocity_) {
			velocity = velocity_;
		}
		public int getWaittime() {
			return waittime;
		}
		public void setWaittime(int waittime_) {
			waittime = waittime_;
		}
		public String getProtocol() {
			return protocol;
		}
		public void setProtocol(String protocol_) {
			protocol = protocol_;
		}
		public class MyShutdown extends Thread {
			RoombaComm roomba = null;
			public MyShutdown(RoombaComm roomba){
				this.roomba = roomba;
			}
		    public void run() {
		        System.out.println("MyShutdown hook called");
		        if (roomba != null && roomba.isConnected()){
		        	System.out.println("roomba not null trying to stop and disconenct");
		        	roomba.stop();
		        	System.out.println("roomba stop issued");
		        	roomba.disconnect();
		        	System.out.println("roomba disconenct issued");
		        }else{
		        	System.out.println("roomba was null or not connected");
		        }
		    }
		}
		public int getThresholdOverride() {
			return thresholdOverride;
		}
		public void setThresholdOverride(int thresholdOverride) {
			this.thresholdOverride = thresholdOverride;
		}
		protected int getMaxLineWidth() {
			return maxLineWidth;
		}
		protected void setMaxLineWidth(int maxLineWidth) {
			this.maxLineWidth = maxLineWidth;
		}
		protected int getMinLineWidth() {
			return minLineWidth;
		}
		protected void setMinLineWidth(int minLineWidth) {
			this.minLineWidth = minLineWidth;
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
		
	}
	
	