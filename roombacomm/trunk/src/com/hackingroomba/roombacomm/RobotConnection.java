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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RobotConnection implements Runnable {
	private int waittime;
	private String robotServer;
	private int robotPortNum = 0;
	private Socket socket;
	public InputStream in;
	public OutputStream out;
	public enum ConnectionType {SERIAL, NET};
	private ConnectionType connectionType;
	private int maxStringSize = 200;
    byte[] readBytes = new byte[maxStringSize];
	byte[] readBytesBuf = new byte[maxStringSize];
	
	// variables for the read thread
    Thread thread;
	private int readRequestLength = 0;
	private int numBytesRead;
	private boolean readComplete;
	public enum ReadTerminator {LF, COUNT, NULL};
	private ReadTerminator readTerminator;
	private int readTimeout = 1000;		// 1000ms read timeout

	public RobotConnection(String robotServer, int preferredPortNum) {
    	connectionType = ConnectionType.NET;
		robotPortNum = preferredPortNum;
		this.robotServer = robotServer;
	}
	/**
	 * Initialize the connection with a server name and optional ':' delimited port number
	 * @param robotServer port name (if serial) or server IP. Server IP may have a trailing ":port"
	 */
	public RobotConnection(String robotServer) {
		this.robotServer = robotServer;
	}
	
	private void parseServerString(String robotServer)
	{
    	// open a connection to robot (net or serial)
    	char portNameChar1 = robotServer.charAt(0);
        if (portNameChar1 >= '0' && portNameChar1 <='9') {	// portname begins with number, assume it's an IP
        	connectionType = ConnectionType.NET;
    		String s[] = robotServer.split(":");
            if (s.length == 2){		// if length = 1, leave robotServer alone - portNum was specified another way
    	        this.robotServer = s[0];
    	        try { 
    	            robotPortNum = Integer.parseInt(s[1]);
    	        } catch( Exception e ) {
    	        	robotPortNum = 0;
    	        }
    	        System.err.println("Using network server '" + this.robotServer + ":" + robotPortNum + "'" );
            }

        } else {
        	connectionType = ConnectionType.SERIAL;
    		this.robotServer = robotServer;		// serial port name
        	System.out.println("Using serial port " + robotServer);
        	System.err.println("Error: Serial port not supported");
        }
	}

	/**
	 * Create a network or serial connection and associated input & output streams. After creating
	 * the connection, the caller should get the input & output streams to use for reading & writing 
	 * the connection. Alternatively, the caller can use the in and out objects.
	 */
	public boolean connect()
	{
		parseServerString(robotServer);	// the server string can override the preferred port number & connectionType
		if (connectionType == ConnectionType.NET) {
	        try {
	        	if ((robotServer == null) || (robotPortNum == 0)) {
	        		System.out.println("Error: server or port not set");
	        		return false;
	        	}
	        	// open a socket to the robot
	            socket = new Socket(robotServer, robotPortNum);
	            socket.setKeepAlive(true);
	            socket.setTcpNoDelay(true);
	            socket.setSoTimeout(0);
	            //socket.setSoTimeout(30000);// timeout in milliseconds - 30 sec	
	            in = socket.getInputStream();
	            out = socket.getOutputStream();
	            
	            // hang a read thread on the socket
	            readComplete = true;	// initialize the "go" variables
	            readRequestLength = 0;
	            thread = new Thread(this);
	            thread.setPriority(Thread.MAX_PRIORITY);
	            thread.start();
	        } catch( Exception e ) {
	            System.out.println("connect: "+e); //e.printStackTrace();
	            return false;
	        }
		} else if (connectionType == ConnectionType.SERIAL) {
        	System.out.println("ERROR: SERIAL NOT IMPLEMENTED YET");
        	return false;
		}
        return true;
    } 

	public void disconnect() {
		try {
	        // do io streams need to be closed first?
	        if (in != null) in.close();
	        if (out != null) out.close();
	    } catch (Exception e) {
	    	System.out.print("exception in disconnect");
	        e.printStackTrace();
	    }
	    in = null;
	    out = null;
	    
	    try {
	        if (socket != null) socket.close();       
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    socket = null;
	}
    public boolean send(int b) {  // will also cover char
        try {
            //System.out.println("Send_( "+b+" & 0xff)");
            out.write(b & 0xff);  // for good measure do the &
            out.flush(); 
        } catch (Exception e) { 
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean send(byte[] bytes) {
        try {
            //logmsg("Send_byte( "+bytes+")");
        	out.write(bytes);
            out.flush();            
        } catch (Exception e) { 
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean sendToArduino(int b) {  // will also cover char
    	byte [] arduinoHeader = {'m', ' '};	// roomba commands to arduino are prefixed with m<space> & end with LF
    	byte arduinoTrailer = '\r';	

    	try {
            //System.out.println("Send_( "+b+" & 0xff)");
        	out.write(arduinoHeader);
            out.write(b & 0xff);  // for good measure do the &
            out.write(arduinoTrailer);
            out.flush(); 
        } catch (Exception e) { 
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean sendToArduino(byte[] bytes) {
    	byte [] arduinoHeader = {'m', ' '};	// roomba commands to arduino are prefixed with m<space> & end with LF
    	byte arduinoTrailer = '\r';	

    	try {
            //logmsg("Send_byte( "+bytes+")");
        	out.write(arduinoHeader);
        	out.write(bytes);
            out.write(arduinoTrailer);
            out.flush();            
        } catch (Exception e) { 
            e.printStackTrace();
            return false;
        }
        return true;
    }

	public byte [] readBot(int count) throws Exception
	{
		byte [] returnData = new byte[count];
		readComplete = false;
		readTerminator = ReadTerminator.COUNT;
		readRequestLength = count;
		
		// wait max 1000ms for read to complete - most anything should be done by then
		//System.out.println("Trying to read " + readRequestLength + " bytes");
		for (int i=0; i<200; i++) {
			Thread.sleep(5);	// wait 5ms for answer to come back
			if (readComplete == true)
				break;
		}
		if (readComplete) {
			System.arraycopy(readBytes, 0, returnData, 0, numBytesRead);
			return (returnData);			
		} else {
			return (null);
		}
	}
	
	public void flushInput()
	{
		while(true) { 
			String junk;
			System.out.print("Flushing... ");
			try {
				junk = readBotToTerminator(ReadTerminator.LF);
				if (junk != null)
					System.out.println("Flushed: " + junk);
				else
					return;
			} catch (Exception e) {
				System.err.println("Exception while flushing" + e.getMessage());
				return;
			}
		}
	}
	
	/**
	 * Read bytes from ArduinoBot up to \n, \r, or other terminator
	 * @return String read from robot
	 */
	public String readBotToTerminator(ReadTerminator terminator) throws Exception 
	{
		readComplete = false;
		readTerminator = terminator;
		readRequestLength = maxStringSize;
		numBytesRead = 0;
		
		// wait readTimout ms for read to complete - most anything should be done by then
		Thread.sleep(10);
		for (int i=0; i<readTimeout/5; i++) {
			Thread.sleep(5);	// wait 5ms for answer to come back
			if (readComplete == true) {
				//System.out.println("readComplete with: " + numBytesRead);
				break;
			}
		}
		if (readComplete == false) {
			System.out.println("Error in readBotToTerminator: timeout");
			return null;
		}
		// skip over leading white space
		int readBytesIx = 0;
		while (readBytes[readBytesIx] < ' ')
			readBytesIx++;
		String readString = new String(readBytes, readBytesIx, numBytesRead);
		//System.out.println("readBotToTerminator read: " + numBytesRead + " string length: " + readString.length() + " string: "+ readString);
		return (readString);

	}
	
	/**
	 * Runs in a separate thread waiting for input. Sets input complete when the desired termination
	 * read terminator is found (\n or count to read
	 */
    public void run() {
   		readByBlock();
    }
    

    public void readByBlock()
    {
		int readLength = 0;
		int bytesLeftToRead;
        byte buffer[] = new byte[maxStringSize];
        int bufferIndex = 0;

		
        //System.out.println("ReadByBlock thread started");
        while ((Thread.currentThread() == thread) && (in != null)) {
    		try {
    			if (readRequestLength == 0) {
    				bufferIndex = 0;
    				readLength = in.available();
    				if (readLength > 0) {
    					readLength = in.read(buffer);	// read & discard data following the terminator (handles extra \n)
    					//System.out.println("readByBlock discarded " + readLength + " bytes");
    				}
    				Thread.sleep(5);
    				continue;
    			}
    			bytesLeftToRead = readRequestLength;
    			readBytes[0] = 0;	// initialize data to empty string
    			//System.out.println("readByBlock starting to read");
    			// ACHTUNG - buffer gets overwritten at the beginning by multiple read buffers
    			for (int i=0; i<5; i++) {
    				readLength = in.read(buffer, 0, (readRequestLength - bufferIndex));	// read as much as there is, even if its more than was requested
                	if ((readLength == -1) || (in == null)) {
                		System.out.println("Error in run: in.read() returned -1 or null; application exiting in 30s: " + readLength);
                		Thread.sleep(30000);
                		System.exit(-1);;
                	}
    				bytesLeftToRead -= readLength;
    				for (int j=0; j<readLength; j++, bufferIndex++) {
    					readBytes[bufferIndex] = buffer[j];
    				}
					//System.out.print("readByBlock read " + readLength + " bytes from byte[0]: "); System.out.printf("0x%x", buffer[0]);	System.out.print(" to byte[" + (readLength - 1) + "]: "); System.out.printf("0x%x\n", buffer[readLength-1]);

					// check to see if it's the end of input
					if ((bufferIndex > 0) && (((readTerminator == ReadTerminator.COUNT) && (bufferIndex >= readRequestLength)) ||
                			((readTerminator == ReadTerminator.LF) && (readBytes[bufferIndex-1] == '\n') && (bufferIndex > 1)) ||	// guard against a single \n from previous read causing premature termination
                			((readTerminator == ReadTerminator.NULL) && (readBytes[bufferIndex-1] == '\0')) ||
                			(bufferIndex == maxStringSize)))
                	{						
                		numBytesRead = bufferIndex;
                		readRequestLength = 0;	// flag this request as complete (so ditch all following data until next request
                		readComplete = true;
                		bufferIndex = 0;
                		//System.out.println("readByBlock completed read with bufferIndex: " + numBytesRead + " readLength: " + readLength);
                		break;
                	} else {
    					Thread.sleep(10);
    					if (bufferIndex > 0) {
        					//System.out.println("readByBlock has " + bufferIndex + ", trying again");   						
    					} else {
    						System.out.print(".");
    					}
                	}
    			}
    		} catch (Exception e) {
    			System.out.println("Exception: readByBlock thread exiting" + e.getMessage());
    			//e.printStackTrace();
    			return;	// causes thread to exit			
    		}   		
    		//System.out.println("readByBlock read " + readLength + " bytes");       	
        }
    }
    
	public InputStream getIn() {
		return in;
	}
	public OutputStream getOut() {
		return out;
	}
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

}