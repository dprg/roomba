/*
 * roombacomm.Spiro2 -- a Spirograph-like example 
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
 *  A Spirograph-like example
 * <p>
 *  Run it with something like: <pre>
 *   java roombacomm.Spiro1 /dev/cu.KeySerial1 velocity radius waittime
 *  </pre>
 *
 */
public class Spiro2 {
    
    static String usage = 
        "Usage: \n"+
        "  roombacomm.Spiro2 <serialportname> <velocity> <radius> <radius2> <waittime> <waittime2> [options]\n" +
        "where [options] can be one or more of:\n"+
        " -debug       -- turn on debug output\n"+
        " -hwhandshake -- use hardware-handshaking, for Windows Bluetooth\n"+
        "\n";
    static boolean debug = false;
    static boolean hwhandshake = false;

    public static void main(String[] args) {
        if( args.length < 6 ) {
            System.out.println( usage );
            System.exit(0);
        }

        String portname = args[0];  // e.g. "/dev/cu.KeySerial1"
        int velocity=0, radius=0, waittime=0, waittime2=0, radius2=0;
        try { 
            velocity = Integer.parseInt( args[1] );
            radius   = Integer.parseInt( args[2] );
            radius2  = Integer.parseInt( args[3] );
            waittime = Integer.parseInt( args[4] );
            waittime2 = Integer.parseInt( args[5] );
        } catch( Exception e ) {
            System.err.println("Couldn't parse arguments");
            System.exit(1);
        }

        for( int i=4; i < args.length; i++ ) {
            if( args[i].endsWith("debug") )
                debug = true;
            else if( args[i].endsWith("hwhandshake") )
                hwhandshake = true;
        }

        RoombaCommSerial roombacomm = new RoombaCommSerial();
        roombacomm.debug = debug;
        roombacomm.waitForDSR = hwhandshake;

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

        //int v = velocity;
        //int r = radius;
        //int dr = dradius;
        
        int w,dr;

        boolean done = false;
        while( !done ) {
            roombacomm.drive( velocity,radius );
            //roombacomm.pause( waittime );

            // lets try some easing
            w = waittime / 10; // divide into 10 msec chucks
            dr = (radius2 - radius) / 10;
            System.out.println("easing "+w+" times at "+dr+" radius");
            for( int i =0; i<w; i++) { 
                roombacomm.drive( velocity,radius + dr );
                roombacomm.pause( 10 );
            }

            roombacomm.drive( velocity, radius2 );
            //roombacomm.pause( waittime2 );

            // lets try some easing
            w = waittime2 / 10; // divide into 10 msec chucks
            dr = (radius - radius2) / 10;
            System.out.println("easing "+w+" times at "+dr+" radius");
            for( int i =0; i<w; i++) { 
                roombacomm.drive( velocity,radius2 + dr );
                roombacomm.pause( 10 );
            }

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

