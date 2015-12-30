/*
 * roombacomm.Waggle -- test out the DRIVE command, showing a waggle
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

/**
  Drive the Roomba in a Waggle, like when it's searching for something
  <p>
  Run it with something like: <pre>
   java roombacomm.Waggle /dev/cu.KeySerial1 [protocol] velocity radius waittime [options]
  where: 
  protocol (optional) is SCI or OI
  velocity and radius in mm, waittime in milliseconds
  [options] can be one or more of:
   -debug       -- turn on debug output
   -hwhandshake -- use hardware-handshaking, for Windows Bluetooth
   -nohwhandshake -- don't use hardware-handshaking
 *  </pre>
 *
 */
public class Waggle {
    
    static String usage = 
        "Usage: \n"+
        "  roombacomm.Waggle <serialportname> [protocol] <velocity> <radius> <waittime> [options]\n" +
        "where: protocol (optional) is SCI or OI\n" +
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
            System.err.println("Couldn't parse velocity or radius or waittime");
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
        roombacomm.pause(100);

        System.out.println("waggling 5 times\n");
        for( int i=0; i<5; i++ ) {
            roombacomm.drive( velocity, radius );
            roombacomm.pause(waittime);
            roombacomm.drive( velocity, -radius );
            roombacomm.pause(waittime);
        }
        roombacomm.stop();

        System.out.println("Disconnecting");
        roombacomm.disconnect();
        
        System.out.println("Done");
    }

}

