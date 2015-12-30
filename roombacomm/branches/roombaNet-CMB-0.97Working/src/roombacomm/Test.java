/*
 *  RoombaComm Interface Test
 *
 *  Copyright (c) 2005 Tod E. Kurt, tod@todbot.com, ThingM
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
  A fairly thorough test of the RoombaComm API.
  <p>
  Run it with something like: <pre>
    java roombacomm.Test /dev/cu.KeySerial1
  Usage: 
  roombacomm.Test serialportname [protocol] [options]<br>
  where: protocol (optional) is SCI or OI
  [options] can be one or more of:
   -debug       -- turn on debug output
   -hwhandshake -- use hardware-handshaking, for Windows Bluetooth
   -nohwhandshake -- don't use hardware-handshaking 
   -flush -- flush on sends(), normally not needed
   -power       -- power on/off Roomba (if interface supports it)
 * </pre>
 * </p>
 */
public class Test {

    static String usage = 
        "Usage: \n"+
        "  roombacomm.Test <serialportname> [protocol] [options]\n" +
        "where: protocol (optional) is SCI or OI\n" +
        "[options] can be one or more of:\n"+
        " -debug       -- turn on debug output\n"+
        " -hwhandshake -- use hardware-handshaking, for Windows Bluetooth\n"+
        " -nohwhandshake -- don't use hardware-handshaking\n"+
        " -flush       -- flush on sends(), normally not needed\n"+
        " -power       -- power on/off Roomba (if interface supports it)\n"+
        "\n";
        
    static boolean debug = false;
    static boolean hwhandshake = false;
    static boolean power = false;
    static boolean flush = false;
    
    public static void main(String[] args) {
        if( args.length == 0 ) {
            System.out.println(usage);
            System.exit(0);
        }
        
        String portname = args[0];  // e.g. "/dev/cu.KeySerial1"
        RoombaCommSerial roombacomm = new RoombaCommSerial();

        for( int i=1; i<args.length; i++ ) {
        	if (args[i].equals("SCI") || (args[i].equals("OI"))) {
        		roombacomm.setProtocol(args[i]);
        	} else if( args[i].endsWith("debug") ) 
                debug = true;
            else if( args[i].endsWith("power") )
                power = true;
            else if( args[i].endsWith("nohwhandshake") )
                roombacomm.setWaitForDSR(false);
            else if( args[i].endsWith("hwhandshake") )
                roombacomm.setWaitForDSR(true);
            else if( args[i].endsWith("flush") )
                flush = true;
        }
        roombacomm.debug = debug;
        roombacomm.flushOutput = flush;
        
        String portlist[] = roombacomm.listPorts();
        System.out.println("Available ports:");
        for( int i=0; i<portlist.length; i++ )
            System.out.println("  "+i+": "+portlist[i]);
        
        if( ! roombacomm.connect( portname ) ) {
            System.out.println("Couldn't connect to "+portname);
            System.exit(1);
        }
        
        if( power ) {
            System.out.println("waking up Roomba...");
            roombacomm.wakeup();
        }

        System.out.println("Roomba startup");
        roombacomm.startup();
        roombacomm.control();
        roombacomm.pause(30);
        
        System.out.println("Checking for Roomba... ");
        if( roombacomm.updateSensors() ) {
            System.out.println("Roomba found!");
        } else {
            System.out.println("No Roomba. :(");
        }
        
        // must pause after every playNote to let to note sound
        System.out.println("Playing some notes");
        roombacomm.playNote( 72, 10 );
        roombacomm.pause( 200 );
        roombacomm.playNote( 79, 10 );
        roombacomm.pause( 200 );
        roombacomm.playNote( 76, 10 );
        roombacomm.pause( 200 );
        
        // test Logo-like functions (blocking)
        // speed is in mm/s, go* is in mm, spin is in degrees
        roombacomm.setSpeed( 100 );     // can be positive or negative
        roombacomm.goStraight( 100 );   // can be positive or negative
        roombacomm.goForward( 100 );    // negative numbers not allowed
        roombacomm.goBackward( 200 );   // negative numbers not allowed
        
        roombacomm.setSpeed( 150 );
        roombacomm.spin( -360 );        // can be positive or negative
        roombacomm.spinRight( 360 );    // negative numbers not allowed
        roombacomm.spinLeft( 360 );     // negative numbers not allowed
        
        // test non-blocking functions
        roombacomm.goStraightAt(200);   // speed argument
        roombacomm.pause(1000);
        roombacomm.goForwardAt(200);   // speed argument
        roombacomm.pause(1000);
        roombacomm.goBackwardAt(400);   // speed argument
        roombacomm.pause(1000);
        
        roombacomm.spinLeftAt( -15 );    // mm/s or degs/sec ?
        roombacomm.pause(1000);
        roombacomm.spinRightAt( 15 );
        roombacomm.pause(1000);
        
        roombacomm.stop();
        
        //roombacomm.turn();
        
        /*
        //    roombacomm.goStraight( 100, 100 );
        roombacomm.spinLeft(  100, 360 );
        roombacomm.spinRight( 200, 360 );
        roombacomm.spinLeft(  300, 360 );
        roombacomm.spinRight( 400, 360 );
        roombacomm.spinLeft(  500, 360 );
        roombacomm.spinRight( 600, 360 );
        */
        
        if( power ) {
            System.out.println("Powering off in 3 seconds.");
            roombacomm.pause( 3000 );
            roombacomm.powerOff();
        }
        
        System.out.println("Disconnecting");
        roombacomm.disconnect();
        
        System.out.println("Done");
    }
    

}
