/*
 * roombacomm.ListSerialPorts
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
 *  A simple test of RoombaComm and RoombaCommSerial functionality.
 * <p>
 *  Run it with something like: <pre>
 *   java roombacomm.ListSerialPorts
 *  </pre>
 *
 */
public class ListSerialPorts {

  public static void main(String[] args) {

    RoombaComm roombacomm = new RoombaCommSerial();
    String portlist[];
    //roombacomm.debug = true;

    portlist = roombacomm.listPorts();
    System.err.println("Available ports:");
    for(int i=0;i<portlist.length;i++) {
      System.err.println("  "+i+": "+portlist[i]);
    }

    System.err.println("Sleeping for 5 seconds so you can (un)plug a USB serial device in and watch the port list change...\n");
    try { Thread.sleep(5000); } catch( Exception e ) {}

    portlist = roombacomm.listPorts();
    System.err.println("Available ports (again):");
    for(int i=0;i<portlist.length;i++) {
      System.err.println("  "+i+": "+portlist[i]);
    }


  }

}
