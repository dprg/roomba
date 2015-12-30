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
 */package roombacomm;

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
	    
	    static String usage = 
	        "Usage: \n"+
	        "  roombacomm.Bsquare <serialportname> [protocol] <command> <velocity> <distance> [options]\n" +
	        "where [options] can be one or more of:\n"+
	        " -debug       -- turn on debug output\n"+
	        " -hwhandshake -- use hardware-handshaking, for Windows Bluetooth\n"+
	        "velocity and distance are in mm/s and mm\n";
	    static boolean debug = false;
	    static boolean hwhandshake = false;
	    static byte [] sensorBytes;	// The last bytes read by roombacomm library
	    static short odoDistance;
	    static short leftEncoder;
	    static short rightEncoder;

	    public static void main(String[] args) {
	        if( args.length < 4 ) {
	            System.out.println( usage );
	            System.exit(0);
	        }

	        String portname = args[0];  // e.g. "/dev/cu.KeySerial1", or "COM5"
	        RoombaCommSerial roombacomm = new RoombaCommSerial();
	        int argOffset = 0;
	        if (args[1].equals("SCI") || (args[1].equals("OI"))) {
	        	roombacomm.setProtocol(args[1]);
	        	argOffset = 1;
	        }
	        int velocity=0, distance=0, waittime=0;
	        String cmd = args[1+argOffset];
	        
	        try { 
	            velocity = (int)(Integer.parseInt( args[2+argOffset]));
	            distance   = (int)(Integer.parseInt( args[3+argOffset] ));
	            waittime = Math.abs((distance / velocity) * 900);	// waittime in ms (correction .9)
	            System.out.println("velocity: " + velocity + " distance: " + distance + 
	            		" waittime: " + waittime);
	        } catch( Exception e ) {
	            System.err.println("Couldn't parse velocity or distance");
	            System.exit(1);
	        }

	        for( int i=4+argOffset; i < args.length; i++ ) {
	            if( args[i].endsWith("debug") )
	                debug = true;
	            else if( args[i].endsWith("nohwhandshake") )
	                roombacomm.setWaitForDSR(false);
	            else if( args[i].endsWith("hwhandshake") )
	                roombacomm.setWaitForDSR(true);
	        }
	        
	        if (waittime == 0) {
	        	System.out.println("Invalid waittime 0");
	        }

	        roombacomm.debug = debug;

	        if( ! roombacomm.connect( portname ) ) {
	            System.out.println("Couldn't connect to "+portname);
	            System.exit(1);
	        }      
	        
	        System.out.println("Roomba startup");
	        roombacomm.startup();
	        roombacomm.control();
	        roombacomm.pause(100);

	        System.out.println("running command " + cmd + "\n");
	        int angle = 83;
	        int radius = 0x8000;
	        if (cmd.equals("f") ) {
	        	byte [] sensorList = {19, 44, 43};	// get distance, L & R encoder counts
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
	            roombacomm.drive( velocity, 0x8000 );
	            roombacomm.pause(waittime);
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
	        } else if (cmd.equals("bccw")) {
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
	        } else if (cmd.equals("bcw")){
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
	        } else if (cmd.equals("onb")) {
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
	        } else if (cmd.equals("spin")) {
	        	for (int i=0; i<4; i++) {
	        	roombacomm.spin(-angle);
	        	roombacomm.pause(1000);
	        	}
	        } else {
	        	System.out.println("Invalid Command");
	        }
	        roombacomm.stop();

	        System.out.println("Disconnecting");
	        roombacomm.disconnect();
	        
	        System.out.println("Done");
	    }

	}



