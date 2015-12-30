/*
 * roombacomm.net.SimpleTest
 *
 *  Copyright (c) 2005 Tod E. Kurt, tod@todbot.com
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

package roombacomm.net;

import roombacomm.*;

/**
 *  A simple test of RoombaComm and RoombaCommSerial functionality.
 * <p>
 *  Run it with something like: <pre>
 *   java roombacomm.SimpleTest /dev/cu.KeySerial1
 *  </pre>
 *
 */
public class SimpleTest {
    
    static String usage = 
        "Usage: \n"+
        "  roombacomm.net.SimpleTest <host:port> [options]\n" +
        "where [options] can be one or more of:\n"+
        " -debug       -- turn on debug output\n"+
        //" -hwhandshake -- use hardware-handshaking, for Windows Bluetooth\n"+
        //" -flush       -- flush on sends(), normally not needed\n"+
        "\n";
    static boolean debug = false;
    static boolean hwhandshake = false;
    static boolean flush = false;

    public static void main(String[] args) {
        if( args.length == 0 ) {
            System.out.println( usage );
            System.exit(0);
        }

        String portname = args[0];  // e.g. "/dev/cu.KeySerial1"

        for( int i=1; i < args.length; i++ ) {
            if( args[i].endsWith("debug") )
                debug = true;
            //else if( args[i].endsWith("hwhandshake") )
            //    hwhandshake = true;
            //else if( args[i].endsWith("flush") )
            //    flush = true;
        }
        
        RoombaComm roombacomm = new RoombaCommTCPClient();

        roombacomm.debug = debug;
        //roombacomm.waitForDSR = hwhandshake;
        //roombacomm.flushOutput = flush;
        
        String portlist[] = roombacomm.listPorts();
        System.out.println("Available ports:");
        for(int i=0;i<portlist.length;i++)
            System.out.println("  "+i+": "+portlist[i]);
        
        if( ! roombacomm.connect( portname ) ) {
            System.out.println("Couldn't connect to "+portname);
            System.exit(1);
        }      

        System.out.println("Roomba startup on port "+portname);
        roombacomm.startup();
        roombacomm.control();
        roombacomm.pause(30);

        System.out.println("Checking for Roomba... ");
        if( roombacomm.updateSensors() )
            System.out.println("Roomba found!");
        else
            System.out.println("No Roomba. :(  Is it turned on?");
        
        //roombacomm.updateSensors();
        
        System.out.println("Playing some notes");
        roombacomm.playNote( 72, 10 );  // C
        roombacomm.pause( 200 );
        roombacomm.playNote( 79, 10 );  // G
        roombacomm.pause( 200 );
        roombacomm.playNote( 76, 10 );  // E
        roombacomm.pause( 200 );
        
        System.out.println("Spinning left, then right");
        roombacomm.spinLeft();
        roombacomm.pause(1000);
        roombacomm.spinRight();
        roombacomm.pause(1000);
        roombacomm.stop();
        
        System.out.println("Going forward, then backward");
        roombacomm.goForward();
        roombacomm.pause(1000);
        roombacomm.goBackward();
        roombacomm.pause(1000);
        roombacomm.stop();
        

        System.out.println("Moving via send()");
        byte cmd[] = {(byte)RoombaComm.DRIVE, 
                      (byte)0x00,(byte)0xfa, (byte)0x00,(byte)0x00};
        roombacomm.send( cmd ) ;
        roombacomm.pause(1000);
        roombacomm.stop();

        System.out.println("Disconnecting");
        roombacomm.disconnect();
        
        System.out.println("Done");
    }
}

