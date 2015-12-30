/*
 * roombacomm.Spy
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
    java roombacomm.Spy /dev/cu.KeySerial1<br>
   Usage:
     roombacomm.Spy <serialportname> [protocol] [options]<br>
   where: protocol (optional) is SCI or OI
   [options] can be one or more of:
    -pause <n>   -- pause n millseconds between sensor read
    -debug       -- turn on debug output
    -hwhandshake -- use hardware-handshaking, for Windows Bluetooth
    -flush       -- flush on sends(), normally not needed
    -power       -- power on/off Roomba (if interface supports it)
   </pre>
 */
public class Spy {
    
    static String usage = 
        "Usage: \n"+
        "  roombacomm.Spy <serialportname> [protocol] [options]\n" +
        "where: protocol (optional) is SCI or OI\n" +
        " [options] can be one or more of:\n"+
        " -pause <n>   -- pause n millseconds between sensor read\n"+
        " -debug       -- turn on debug output\n"+
        " -hwhandshake -- use hardware-handshaking, for Windows Bluetooth\n"+
        " -flush       -- flush on sends(), normally not needed\n"+
        " -power       -- power on/off Roomba (if interface supports it)\n"+
        "\n";

    static boolean debug = false;
    static boolean hwhandshake = false;
    static boolean power = false;
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
        		roombacomm.setProtocol(args[1]);
        	} else if( args[i].endsWith("debug") ) 
                debug = true;
            else if( args[i].endsWith("power") )
                power = true;
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
        
        System.out.println("Press return to exit.");
        boolean running = true;
        while( running ) { 

            try { 
                if( System.in.available() != 0 ) {
                    System.out.println("key pressed");
                    running = false;		    
                }
            } catch( IOException ioe ) { }
            
            boolean rc =  roombacomm.updateSensors();
            if( !rc ) {
                System.out.println("No Roomba. :(  Is it turned on?");
                continue;
            }

            System.out.println( System.currentTimeMillis() + ":"+ 
                                roombacomm.sensorsAsString() );
            
            roombacomm.pause( pausetime );
        }
        System.out.println("Disconnecting");
        roombacomm.disconnect();
        
        System.out.println("Done");
        
	System.out.println("goodbye.");
	roombacomm.disconnect();
    }
}
    
