/*
 * roombacomm.SpySimple
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

import java.io.*;

/**
  Spy on the Roomba as it goes about its normal business

  <p>
  Run it with something like: <pre>
    java roombacomm.SpySimple /dev/cu.KeySerial1
  Usage: 
  roombacomm.SpySimple serialportname [protocol] [options]<br>
  where: protocol (optional) is SCI or OI
  [options] can be one or more of:
   -pause n   -- pause n millseconds between sensor read
   -debug       -- turn on debug output
   -hwhandshake -- use hardware-handshaking, for Windows Bluetooth
   -nohwhandshake -- don't use hardware-handshaking 
   -flush -- flush on sends(), normally not needed *  </pre>
 *
 */
public class SpySimple {
    
    static String usage = 
        "Usage: \n"+
        "  roombacomm.SpySimple <serialportname> [protocol] [options]\n" +
        "where: protocol (optional) is SCI or OI\n" +
        "[options] can be one or more of:\n"+
        " -pause <n>   -- pause n millseconds between sensor read\n"+
        " -debug       -- turn on debug output\n"+
        " -hwhandshake -- use hardware-handshaking, for Windows Bluetooth\n"+
        " -nohwhandshake -- don't use hardware-handshaking\n"+
        " -flush       -- flush on sends(), normally not needed\n"+
        "\n";

    static boolean debug = false;
    static boolean hwhandshake = false;
    static int pausetime = 500;
    
    public static void main(String[] args) {
        if( args.length == 0 ) {
            System.out.println( usage );
            System.exit(0);
        }
        String portname = args[0];  // e.g. "/dev/cu.KeySerial1"
        RoombaCommSerial roombacomm = new RoombaCommSerial();

        for( int i=1; i<args.length; i++ ) {
        	if (args[i].equals("SCI") || (args[i].equals("OI"))) {
        		roombacomm.setProtocol(args[i]);
        	} else if( args[i].endsWith("debug") ) 
                debug = true;
            else if( args[i].endsWith("nohwhandshake") )
                roombacomm.setWaitForDSR(false);
            else if( args[i].endsWith("hwhandshake") )
                roombacomm.setWaitForDSR(true);
            else if( args[i].endsWith("pause") ) {
                i++;
                int p = 0;
                try { p = Integer.parseInt( args[i] ); }
                catch( NumberFormatException nfe ) { }
                if( p!=0 ) pausetime = p;
            }
        }
        roombacomm.debug = debug;
        
        if( ! roombacomm.connect( portname ) ) {
            System.out.println("Couldn't connect to "+portname);
            System.exit(1);
        }
        
        System.out.println("Roomba startup");
        roombacomm.startup();
        
        boolean done = false;
        while( !done ) {
            roombacomm.updateSensors();
            printSensors(roombacomm);
            roombacomm.pause( pausetime );
            done = keyIsPressed();
        }
        System.out.println("Disconnecting");
        roombacomm.disconnect();
        
        System.out.println("Done");
        
    }
    
    public static void printSensors(RoombaCommSerial rc) {
        System.out.println( System.currentTimeMillis() + ":"+ 
                            "bump:" + 
                            (rc.bumpLeft()?"l":"_") + 
                            (rc.bumpRight()?"r":"_") +
                            " wheel:" +
                            (rc.wheelDropLeft()  ?"l":"_") +
                            (rc.wheelDropCenter()?"c":"_") +
                            (rc.wheelDropLeft()  ?"r":"_") 
                            );
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
