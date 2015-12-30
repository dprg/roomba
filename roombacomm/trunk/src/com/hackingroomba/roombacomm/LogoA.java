/*
 * roombacomm.LogoA
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

package com.hackingroomba.roombacomm;

/**
 Some example Logo-like things to do
 <p>
 Run it with something like: <pre>
  java roombacomm.LogoA /dev/cu.KeySerial1<br>
 Usage: 
 roombacomm.LogoA serialportname [protocol] [options]<br>
 where:
 protocol (optional) is SCI or OI
 [options] can be one or more of:
  -debug       -- turn on debug output
 </pre>
 
*/ 
public class LogoA {
    
    static String usage = 
        "Usage: \n"+
        "  roombacomm.LogoA <serialportname> [options]\n" +
        "where protocol (optional) is SCI or OI\n"+
        "[options] can be one or more of:\n"+
        " -debug       -- turn on debug output\n"+
        "\n";
    static boolean debug = false;

    public static void main(String[] args) {
        if( args.length == 0 ) {
            System.out.println( usage );
            System.exit(0);
        }

        String portname = args[0];  // e.g. "/dev/cu.KeySerial1"
        RoombaCommSerial roombacomm = new RoombaCommSerial();
        for( int i=1; i < args.length; i++ ) {
        	if (args[i].equals("SCI") || (args[i].equals("OI"))) {
        		roombacomm.setProtocol(args[i]);
        	} else if( args[i].endsWith("debug") )
                debug = true;
        }
        
        roombacomm.debug = debug;
        
        if( ! roombacomm.connect( portname ) ) {
            System.out.println("Couldn't connect to "+portname);
            System.exit(1);
        }      

        System.out.println("Roomba startup");
        roombacomm.startup();
        roombacomm.control();
        roombacomm.pause(30);
        
        for( int i=0; i<8; i++ ) {
            roombacomm.spinRight( 45 );
            square( roombacomm, 100 );
        }
        
        System.out.println("Disconnecting");
        roombacomm.disconnect();
        
        System.out.println("Done");
    }

    /**
     * Make a square with a Roomba.
     * Leaves Roomba in same place it began (theoretically)
     * @param rc RoombaComm object connected to a Roomba
     * @param size size of square in mm
     */
    public static void square(RoombaComm rc, int size) {
        rc.goForward( size );  
        rc.spinLeft( 90 );
        rc.goForward( size );
        rc.spinLeft( 90 );
        rc.goForward( size );
        rc.spinLeft( 90 );
        rc.goForward( size );
        rc.spinLeft( 90 );
    }
}

