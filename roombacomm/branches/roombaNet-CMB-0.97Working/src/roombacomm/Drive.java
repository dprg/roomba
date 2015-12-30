/*
 * roombacomm.Drive -- test out the DRIVE command
 *
 *  Copyright (c) 2006 Tod E. Kurt, tod@todbot.com, ThingM
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

package roombacomm;

import java.io.*;

/**
 *  A program for driving Roomba.
 * <p>
 *  Run it with something like: <pre>
 *   java roombacomm.Drive /dev/cu.KeySerial1 byte1 byte2 byte3 byte4<br>
 *  Usage:
 *  roombacomm.Drive serialportname [protocol] velocity radius waittime [options]<br>
 *  where protocol (optional) is SCI or OI 
 *  velocity is in mm/sec
 *  radius is mm from the centerpoint
 *  waittime is in milliseconds
 *  [options] can be one or more of:
 *  -debug       -- turn on debug output
 *  -hwhandshake -- use hardware-handshaking, for Windows Bluetooth
 *  -nohwhandshake -- don't use hardware-handshaking
 *  </pre>
 *
 */
public class Drive {
    
    static String usage = 
        "Usage: \n"+
        "  roombacomm.Drive <serialportname> [protocol] <velocity> <radius> <waittime> [options]\n" +
        "where protocol (optional) is SCI or OI\n" +
        "[options] can be one or more of:\n"+
        " -debug       -- turn on debug output\n"+
        " -hwhandshake -- use hardware-handshaking, for Windows Bluetooth\n"+
        " -nohwhandshake -- don't use hardware-handshaking\n"+
        "\n";
    static boolean debug = false;
    static boolean hwhandshake = false;

    public static void main(String[] args) {
    	new Drive(args);
    }
    
    Drive(String[] args)
    {
    	int argIndx;
    	String portname, protocol;
        if( args.length < 4 ) {
            System.out.println( usage );
            System.exit(0);
        }

        /*
         * Parse port & protocol
         */
        portname = args[0];  // e.g. "/dev/cu.KeySerial1"
        RoombaCommSerial roombacomm = new RoombaCommSerial();
        int argOffset = 0;
        if (args[1].equals("SCI") || (args[1].equals("OI"))) {
        	roombacomm.setProtocol(args[1]);
        	argOffset = 1;
        }
        
     	/*
    	 * Parse command arguments
    	 */
        int velocity=0, radius=0, waittime=0;
        try { 
			velocity = Integer.parseInt( args[1+argOffset] );	// velocity would be the 1st numeric
            radius   = Integer.parseInt( args[2+argOffset] );
            waittime = Integer.parseInt( args[3+argOffset] );
        } catch( Exception e ) {
            System.err.println("Couldn't parse velocity, radius, or waittime");
            System.exit(1);
        }

        
        /*
         * Parse options
         */
        for( int i=4+argOffset; i < args.length; i++ ) {
            if( args[i].endsWith("debug") )
                debug = true;
            else if( args[i].endsWith("nohwhandshake") )
                roombacomm.setWaitForDSR(false);
            else if( args[i].endsWith("hwhandshake") )
                roombacomm.setWaitForDSR(true);
        }

        roombacomm.debug = debug;

        if( ! roombacomm.connect( roombacomm.getPortname())) {
            System.out.println("Couldn't connect to "+portname);
            System.exit(1);
        } 
        System.out.println("Using port: " + roombacomm.getPortname() + " protocol: " 
        		+ roombacomm.getProtocol() + " velocity: " + velocity + " radius: "
        		+ radius + " waittime: " + waittime + "\n");
        
        System.out.println("Roomba startup");
        roombacomm.startup();
        roombacomm.pause(100);
        roombacomm.control();
        roombacomm.playNote( 72, 10 );  // C , test note
        roombacomm.pause( 200 );
        if( roombacomm.updateSensors() )
            System.out.println("Roomba found!\n");
        else
            System.out.println("No Roomba. :(  Is it turned on?\n");

        roombacomm.drive( velocity, radius );
        roombacomm.pause(waittime);
        roombacomm.stop();

        System.out.println("Disconnecting");
        roombacomm.disconnect();
        
        System.out.println("Done");
    }

}

