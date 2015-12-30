/*
 * roombacomm.Spiro1 -- a Spirograph-like example 
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
  A Spirograph-like example
 <p>
  Run it with something like: <pre>
   java roombacomm.Spiro1 /dev/cu.KeySerial1 velocity radius waittime<br>
  Usage: \n"+
    roombacomm.Spiro1 <serialportname> [protocol] <velocity> <radius> <waittime> [options]<br>
  where: 
  protocol (optional) is SCI or OI
  velocity and radius in mm, waittime in milliseconds
  [options] can be one or more of:
   -debug       -- turn on debug output
   -hwhandshake -- use hardware-handshaking, for Windows Bluetooth
   -nohwhandshake -- don't use hardware-handshaking
  </pre>
 */
public class Spiro1 {
    
    static String usage = 
        "Usage: \n"+
        "  roombacomm.Spiro1 <serialportname> [protocol] <velocity> <radius> <waittime> [options]\n" +
        "where: protocol (optional) is SCI or OI\n" +
        "velocity and radius in mm, waittime in milliseconds\n"+
        "[options] can be one or more of:\n"+
        " -debug       -- turn on debug output\n"+
        " -hwhandshake -- use hardware-handshaking, for Windows Bluetooth\n"+
        " -nohwhandshake -- don't use hardware-handshaking\n"+
        "\n";
    static boolean debug = false;
    static boolean hwhandshake = false;

    public static void main(String[] args) {
        if( args.length < 4 ) {
            System.out.println( usage );
            System.exit(0);
        }

        String portname = args[0];  // e.g. "/dev/cu.KeySerial1"
        RoombaCommSerial roombacomm = new RoombaCommSerial();
        int argOffset = 0;
        if (args[1].equals("SCI") || (args[1].equals("OI"))) {
        	roombacomm.setProtocol(args[1]);
        	argOffset = 1;
        }

        int velocity=0, radius=0, waittime=0;
        try { 
            velocity = Integer.parseInt( args[1+argOffset] );
            radius   = Integer.parseInt( args[2+argOffset] );
            waittime = Integer.parseInt( args[3+argOffset] );
        } catch( Exception e ) {
            System.err.println("Couldn't parse velocity & radius");
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

        roombacomm.debug = debug;

        if( ! roombacomm.connect( portname ) ) {
            System.out.println("Couldn't connect to "+portname);
            System.exit(1);
        }      
        
        System.out.println("Roomba startup");
        roombacomm.startup();
        roombacomm.control();
        roombacomm.pause(30);
        roombacomm.full();
        roombacomm.pause(50);

        int v = velocity;
        int r = radius;
        int dr = -10;
        
        boolean done = false;
        while( !done ) {
            roombacomm.drive( v,r );
            roombacomm.pause( waittime );
            roombacomm.drive( v, (int) r / Math.abs(dr) );
            roombacomm.pause( waittime );
            r += -10;
            done = keyIsPressed();
        }

        roombacomm.stop();
        roombacomm.safe();
        System.out.println("Disconnecting");
        roombacomm.disconnect();
        
        System.out.println("Done");
    }

    /** check for keypress, return true if so */
    public static boolean keyIsPressed() {
        boolean press = false;
        try { 
            if( System.in.available() != 0 ) {
                System.out.println("key pressed");
                press = true;
            }
        } catch( IOException ioe ) { }
        return press;
    }
}

