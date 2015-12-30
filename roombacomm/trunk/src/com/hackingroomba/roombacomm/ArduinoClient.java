/*
 * roombacomm.ArduinoClient -- test out the Arduino subsystem without robot motion
 *
 *  Copyright (c) 2009 Paul Bouchier, bouchier@at@classicnet.net
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

import jargs.gnu.CmdLineParser;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * A test class for Arduino communications.
 * 
 * <h2> Overview </h2>
 * This class contains the communications layer-independent parts of
 * how to communicate with an Arduino.  It does assume a very serial port-like
 * interaction.
**/

public class ArduinoClient {
	 	private static final long serialVersionUID = 1L;
	 	private static final int lumBufHeight = 32;

		String usage = 
	        "Usage: \n"+
	        "  ArduinoClient {--arduinoServer|-s} <IP> [{--arduinoPortNum|-p} <port>] [options]\n" +
	        "where [options] can be one or more of:\n"+
	        " --debug|-X       -- turn on debug output\n";
		
		private RobotConnection arduinoConnection;
		private String arduinoServer;
		private Boolean debug;

		public ArduinoClient() {
			// constructor, mustn't throw exceptions. Do nothing for now
		}
		
		// main - it all starts here; when run as main class, ask Arduino for its compass reading
		public static void main(String[] args) {
			ArduinoClient l = new ArduinoClient();
			l.testClient(args);
		}
		
		public void testClient(String[] args) {
			parseCmd(args);
			arduinoConnection = new RobotConnection(arduinoServer);
			arduinoConnection.connect();
			ArduinoBot arduinobot = new ArduinoBot(arduinoConnection);
			arduinobot.printCompass();
			arduinoConnection.disconnect();
		}
		

		public void parseCmd(String[] args){
			//System.out.println("*** running ArduinoClient standalone WparseCmd");

	        CmdLineParser parser = new CmdLineParser();
	        CmdLineParser.Option debugOption = parser.addBooleanOption('X', "debug");
	        CmdLineParser.Option arduinoServerOption = parser.addStringOption('s', "arduinoServer");
	        
	        try {
	            parser.parse(args);
	        }
	        catch ( CmdLineParser.OptionException e ) {
	            System.err.println(e.getMessage());
	            System.out.println("parseCmd had an error\n"+ usage );
	            System.exit(2);
	        }	        
	        
	        //	        String portname = args[0];  // e.g. "/dev/cu.KeySerial1", or "COM5" or "192.168.1.1:5002"
	        arduinoServer = (((String)parser.getOptionValue(arduinoServerOption)));
	        Boolean debugBool = (Boolean)parser.getOptionValue(debugOption,new Boolean(false));
	        setDebug(debugBool.booleanValue());
	        //System.out.println("debug is ("+isDebug()+")");
	        //System.out.println("*** end of parseCmd");
	    }


		public boolean isDebug() {
			return debug;
		}
		public void setDebug(boolean debug_) {
			debug = debug_;
		}
		/**
		 * @param arduuinoServer the localizerServer to set
		 */

	}
	
	