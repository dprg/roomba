/*
 * roombacomm.AudioLocalizerClient -- test out the audio localizer subsystem without robot motion
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
	    Run it with something like: <pre>
	    java roombacomm.AudioLocalizerClient --localizerServer 192.168.0.150 --localizerPortNum 5005 --debug<br>
     Usage:
       roombacomm.AudioLocalizerClient --localizerServer <IP> --localizerPortNum <port> [options]<br>
     where 
     [options] can be one or more of:
      -debug       -- turn on debug output
	   </pre>
*/	 
	public class AudioLocalizerClient {
	 	private static final long serialVersionUID = 1L;
	 	private static final int lumBufHeight = 32;
	    
		String usage = 
	        "Usage: \n"+
	        "  AudioLocalizerClient --localizerServer <IP> --localizerPortNum <port> [options]\n" +
	        "where [options] can be one or more of:\n"+
	        " --debug       -- turn on debug output\n";
	    boolean debug = false;
		private int waittime;
		private String localizerServer = "";
		private int localizerPortNum = 5010 ;
		private int locationStringSize = 200;
		private byte[] locationBytes = new byte[locationStringSize];
		private byte[] locationStringBuf = new byte[locationStringSize];
		private double x, y;
		
		// socket variables
		Socket localizerSocket;
		BufferedInputStream in;
		BufferedOutputStream out;
	    
		public AudioLocalizerClient() {
			// constructor, mustn't throw exceptions. Do nothing for now
		}
		
		// main - it all starts here
		public static void main(String[] args) {
			AudioLocalizerClient l = new AudioLocalizerClient();
			l.parseCmd(args);
			l.connectLocalizer();
			l.printLocation();
		}
		
		public void connectLocalizer()
		{  	
        	if (getLocalizerServer() == null || getLocalizerServer().length() == 0) {
        		System.err.println(" you must supply a --localizerServer value to use the command");
        		if (getLocalizerPortNum() <= 0) {
	        		System.err.println(" you must supply a --localizerPortNum value to use the command");
        		}
        		System.exit(6);
        	}
        	if (getLocalizerPortNum() <= 0) {
        		System.err.println(" you must supply a --localizerPortNum value to use the command \"getVideo\"");
        		System.exit(7);
        	}

        	connect();
		}
		
		public void printLocation()
		{        	
        	//while (true) {
	        	int size = readLocation();
	        	String locationString = new String(locationBytes, 0, size);
	        	System.out.println("received string: " + locationString);
	        	String [] splitLocationStrings = locationString.split("\\s");
	        	if (splitLocationStrings[0].compareTo("Invalid") == 0) {
	        		System.out.println("Location is invalid");
	        	} else {
	        		x = new Double(splitLocationStrings[1]);
	        		y = new Double(splitLocationStrings[2]);
	        		System.out.println("X: " + x + " Y: " + y);
	        	}
        	//}
		}
		public int readLocation()
		{
			int readLength = 0;
			int maxReadSize = locationStringSize;
			locationBytes[0] = 'L';	// send the "localize" command
			int locStringIx = 0;

			try {
				out.write(locationBytes, 0, 1);
				out.flush();
				
				// ACHTUNG - locationStringBuf gets overwritten at the beginning by multiple read buffers - use locationString
				for (int i=0; i<5; i++) {
					readLength = in.read(locationStringBuf, 0, maxReadSize);
					maxReadSize -= readLength;
					for (int j=0; j<readLength; j++, locStringIx++) {
						locationBytes[locStringIx] = locationStringBuf[j];
					}
					if ((locationBytes[locStringIx-1] == '\0') || (locStringIx == locationStringSize))
						break;

					try {
						Thread.sleep(10);
						System.out.println("Have " + locStringIx + ", trying again");
					} catch (Exception e) {
						System.out.print(e);
					}
				}
			} catch (IOException e) {
				System.out.println("I/O exception");
				e.printStackTrace();
				System.exit(-1);			
			}
			
			System.out.println("readLocation read " + locStringIx + " bytes");
			
			return (locStringIx);
		}

		public void connect()
		{
			try {
				localizerSocket = new Socket(localizerServer, localizerPortNum);
				in = new BufferedInputStream(localizerSocket.getInputStream());
				out = new BufferedOutputStream(localizerSocket.getOutputStream());
			} catch (UnknownHostException e) {
				System.out.println("Unknown host: " + localizerServer + ":" + localizerPortNum);
				System.exit(-1);
			} catch(IOException e) {
				System.out.println("I/O exception");
				e.printStackTrace();
				System.exit(-1);
			}	
			System.out.println("connected to localizer");
		}
		
		public void disconnect()
		{
	        try {
	            // do io streams need to be closed first?
	            if (in != null) in.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        in = null;
	        
	        try {
	            if (localizerSocket != null) localizerSocket.close();       
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        localizerSocket = null;
	        System.out.println("disconnected from Video");
		}
		     		
		public void parseCmd(String[] args){
			//System.out.println("*** start of AudioLocalizerClient WparseCmd");

	        CmdLineParser parser = new CmdLineParser();
	        CmdLineParser.Option debugOption = parser.addBooleanOption('X', "debug");
	        CmdLineParser.Option localizerServerOption = parser.addStringOption('l', "localizerServer");
	        CmdLineParser.Option localizerPortNumOption = parser.addIntegerOption('p', "localizerPortNum");
	        
	        try {
	            parser.parse(args);
	        }
	        catch ( CmdLineParser.OptionException e ) {
	            System.err.println(e.getMessage());
	            System.out.println("parseCmd had an error\n"+ usage );
	            System.exit(2);
	        }	        
	        
	        //	        String portname = args[0];  // e.g. "/dev/cu.KeySerial1", or "COM5" or "192.168.1.1"
	        setLocalizerServer(((String)parser.getOptionValue(localizerServerOption)));
	        setLocalizerPortNum(((Integer)parser.getOptionValue(localizerPortNumOption, getLocalizerPortNum())).intValue());
			//	        String cmd = args[1+argOffset];

	        Boolean debugBool = (Boolean)parser.getOptionValue(debugOption,new Boolean(false));
	        setDebug(debugBool.booleanValue());
	        System.out.println("debug is ("+isDebug()+")");
	        //System.out.println("*** end of parseCmd");
	    }


		public boolean isDebug() {
			return debug;
		}
		public void setDebug(boolean debug_) {
			debug = debug_;
		}
		public int getWaittime() {
			return waittime;
		}
		public void setWaittime(int waittime_) {
			waittime = waittime_;
		}
		/**
		 * @return the localizerServer
		 */
		protected String getLocalizerServer() {
			return localizerServer;
		}
		/**
		 * @param localizerServer the localizerServer to set
		 */
		public void setLocalizerServer(String localizerServer) {
			this.localizerServer = localizerServer;
		}
		/**
		 * @return the localizerPortNum
		 */
		protected int getLocalizerPortNum() {
			return localizerPortNum;
		}
		/**
		 * @param localizerPortNum the localizerPortNum to set
		 */
		public void setLocalizerPortNum(int localizerPortNum) {
			this.localizerPortNum = localizerPortNum;
		}
	 	public double getX() {
			return x;
		}


		public double getY() {
			return y;
		}
	}
	
	