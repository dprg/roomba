/*
 *  RoombaComm TCP Interface
 *
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


package com.hackingroomba.roombacomm;


import java.net.*;
import java.io.*;

import com.hackingroomba.roombacomm.*;
/*
 *  SVN id value is $Id$
 */
public class RoombaCommTCPClient extends RoombaComm implements Runnable
{
    String host = null;
    int port = -1;

    Socket socket;
    InputStream input;
    OutputStream output;

    Thread thread;
    private byte buffer[] = new byte[32768];
    int bufferIndex;
    int bufferLast;
    
    
    // constructor
    public RoombaCommTCPClient() {
        super();
    }
    
    public RoombaCommTCPClient(RobotConnection rc)
    {
    	super(rc);
    }
    
    public boolean connect(String portid) {
        String s[] = portid.split(":");
        if( s.length < 2 ) {
            logmsg("bad portid "+portid);
            return false;
        }else{
        	logmsg("portid is ("+portid+")");
        	port=0;
	        host = s[0];
	        if (host != null){
	        	logmsg("Host is ("+host+")");
	        }else{
	        	logmsg("Host is (null)");
	        	return false;
	        }
	        try { 
	            port = Integer.parseInt(s[1]);
	        } catch( Exception e ) {
	            logmsg("bad port "+e);
	            return false;
	        }
	        logmsg("connecting to '"+host+":"+port+"'");
	        try {
	            socket = new Socket(host, port);
	            socket.setKeepAlive(true);
	            socket.setSoTimeout(30000);// timeout in milliseconds - 30 sec	
	            input = socket.getInputStream();
	            output = socket.getOutputStream();
	            thread = new Thread(this);
	            thread.start();
	            //this.setConnected(this.updateSensors()); // test if we are connected or not
	            this.setConnected(true); // TODO: fix this properly
	        } catch( Exception e ) {
	            logmsg("connect: "+e); //e.printStackTrace();
	            return false;
	        }
        } 
        return true;
    }
    
    public void disconnect() {
        try {
            // do io streams need to be closed first?
            if (input != null) input.close();
            if (output != null) output.close();
            this.setConnected(false);
        } catch (Exception e) {
        	System.out.print("exception in disconnect");
            e.printStackTrace();
        }
        thread = null;
        input = null;
        output = null;
        
        try {
            if (socket != null) socket.close();       
        } catch (Exception e) {
            e.printStackTrace();
        }
        socket = null;
    }
    
    public boolean send(byte[] bytes) {
    	// use robotConnection if it exists, else do it the old way
    	if (robotConnection != null) {
    		return(robotConnection.send(bytes));
    	}
    	
        try {
            //logmsg("Send_byte( "+bytes+")");
        	output.write(bytes);
            output.flush();            
        } catch (Exception e) { 
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean send(int b) {  // will also cover char
    	// use robotConnection if it exists, else do it the old way
    	if (robotConnection != null) {	
    		return(robotConnection.send(b));
    	}
    	
        try {
            //logmsg("Send_( "+b+" & 0xff)");
            output.write(b & 0xff);  // for good measure do the &
            output.flush(); 
        } catch (Exception e) { 
            e.printStackTrace();
            return false;
        }
        return false;
    }
    
    public void wakeup() {
        logmsg("wakup unimplemented");
    }
    
    public boolean updateSensorsOLD() {
    	sensorsValid = false;
        for(int i=0; i < 20; i++) {
        	sensors();
            if( sensorsValid ) { 
            	if ((buffer[1] > 1) || (buffer[1] < 0)) {
            		sensorsValid = false;
            		logmsg("updateSensors: received invalid data while attempting to read Roomba sensors!");
            	} else {
                    logmsg("updateSensors: sensorsValid!");            		
            	}
                break;
            }
            logmsg("updateSensors: pausing...");
            pause( 50 );
        }
        return sensorsValid;
    }
    
    // copied from RoombaCommSerial
    public void computeSensors() {
        sensorsValid = true;
        sensorsLastUpdateTime = System.currentTimeMillis();
        computeSafetyFault();
    }

    public String[] listPorts() {
        String s[] = new String[0];
        if( host !=null && port !=-1 ) {
            String p[] = { host+":"+port };
            return p;
        }
        return s;
    }


    //////////////////////////////////////////////////////////////////

    /**
     *
     */
    public void run() {
    	bufferLast = 0;
        while (Thread.currentThread() == thread) {
            try {
                while (input != null) {  
                   buffer[bufferLast++] = (byte) input.read(); // this will block - wait until there's something 
                   //logmsg("TCP received sensor data: " + buffer[bufferLast-1]);
                   if( bufferLast == readRequestLength ) {
                        bufferLast = 0;
                        System.arraycopy(buffer, 0, sensor_bytes, 0, 26);
                        sensorsValid = true;
                        //computeSensors();
                    }

//                    synchronized (getBuffer()) {
//                        if (bufferLast == getBuffer().length) {
//                            byte temp[] = new byte[bufferLast << 1];
//                            System.arraycopy(getBuffer(), 0, temp, 0, bufferLast);
//                            setBuffer(temp);
//                        }
//                        getBuffer()[bufferLast++] = (byte) input.read();
//                    }
                }

//                try {
//                    // uhh.. not sure what's best here.. since blocking,
//                    // do we need to worry about sleeping much? or is this
//                    // gonna try to slurp cpu away from the main applet?
//                    Thread.sleep(10);
//                } catch (InterruptedException ex) { }
                
            } catch (IOException e) {
                System.err.println("run:"+e);
            }
        }
    }

    /**
     * Returns the number of bytes that have been read from serial
     * and are waiting to be dealt with by the user.
     */
    public int available() {
        return (bufferLast - bufferIndex);
    }
    
    /**
     * Ignore all the bytes read so far and empty the buffer.
     */
    public void clear() {
        bufferLast = 0;
        bufferIndex = 0;
    }
    
    /**
     * Returns a number between 0 and 255 for the next byte that's
     * waiting in the buffer.
     * Returns -1 if there was no byte (although the user should
     * first check available() to see if things are ready to avoid this)
     */
    public int read() {
        if (bufferIndex == bufferLast) return -1;
        
        synchronized (getBuffer()) {
            int outgoing = getBuffer()[bufferIndex++] & 0xff;
            if (bufferIndex == bufferLast) {  // rewind
                bufferIndex = 0;
                bufferLast = 0;
            }
            return outgoing;
        }
    }
    

    /**
     * Returns the next byte in the buffer as a char.
     * Returns -1, or 0xffff, if nothing is there.
     */
    public char readChar() {
        if (bufferIndex == bufferLast) return (char)(-1);
        return (char) read();
    }
    
    
    /**
     * Return a byte array of anything that's in the serial buffer.
     * Not particularly memory/speed efficient, because it creates
     * a byte array on each read, but it's easier to use than
     * readBytes(byte b[]) (see below).
     */
    public byte[] readBytes() {
        if (bufferIndex == bufferLast) return null;
        
        synchronized (getBuffer()) {
            int length = bufferLast - bufferIndex;
            byte outgoing[] = new byte[length];
            System.arraycopy(getBuffer(), bufferIndex, outgoing, 0, length);
            
            bufferIndex = 0;  // rewind
            bufferLast = 0;
            return outgoing;
        }
    }
    
    /**
     * Grab whatever is in the serial buffer, and stuff it into a
     * byte buffer passed in by the user. This is more memory/time
     * efficient than readBytes() returning a byte[] array.
     *
     * Returns an int for how many bytes were read. If more bytes
     * are available than can fit into the byte array, only those
     * that will fit are read.
     */
    public int readBytes(byte outgoing[]) {
        if (bufferIndex == bufferLast) return 0;
        
        synchronized (getBuffer()) {
            int length = bufferLast - bufferIndex;
            if (length > outgoing.length) length = outgoing.length;
            System.arraycopy(getBuffer(), bufferIndex, outgoing, 0, length);
            
            bufferIndex += length;
            if (bufferIndex == bufferLast) {
                bufferIndex = 0;  // rewind
                bufferLast = 0;
            }
            return length;
        }
    }
    
    
    /**
     * Reads from the serial port into a buffer of bytes up to and
     * including a particular character. If the character isn't in
     * the serial buffer, then 'null' is returned.
     */
    public byte[] readBytesUntil(int interesting) {
        if (bufferIndex == bufferLast) return null;
        byte what = (byte)interesting;
        
        synchronized (getBuffer()) {
            int found = -1;
            for (int k = bufferIndex; k < bufferLast; k++) {
                if (getBuffer()[k] == what) {
                    found = k;
                    break;
                }
            }
            if (found == -1) return null;
            
            int length = found - bufferIndex + 1;
            byte outgoing[] = new byte[length];
            System.arraycopy(getBuffer(), bufferIndex, outgoing, 0, length);
            
            bufferIndex = 0;  // rewind
            bufferLast = 0;
            return outgoing;
        }
    }
    
    
    /**
     * Reads from the serial port into a buffer of bytes until a
     * particular character. If the character isn't in the serial
     * buffer, then 'null' is returned.
     *
     * If outgoing[] is not big enough, then -1 is returned,
     *   and an error message is printed on the console.
     * If nothing is in the buffer, zero is returned.
     * If 'interesting' byte is not in the buffer, then 0 is returned.
     */
    public int readBytesUntil(int interesting, byte outgoing[]) {
        if (bufferIndex == bufferLast) return 0;
        byte what = (byte)interesting;
        
        synchronized (getBuffer()) {
            int found = -1;
            for (int k = bufferIndex; k < bufferLast; k++) {
                if (getBuffer()[k] == what) {
                    found = k;
                    break;
                }
            }
            if (found == -1) return 0;
            
            int length = found - bufferIndex + 1;
            if (length > outgoing.length) {
                System.err.println("readBytesUntil() byte buffer is" +
                                   " too small for the " + length +
                                   " bytes up to and including char " + interesting);
                return -1;
            }
            //byte outgoing[] = new byte[length];
            System.arraycopy(getBuffer(), bufferIndex, outgoing, 0, length);
            
            bufferIndex += length;
            if (bufferIndex == bufferLast) {
                bufferIndex = 0;  // rewind
                bufferLast = 0;
            }
            return length;
        }
    }
    
    
    /**
     * Return whatever has been read from the serial port so far
     * as a String. It assumes that the incoming characters are ASCII.
     *
     * If you want to move Unicode data, you can first convert the
     * String to a byte stream in the representation of your choice
     * (i.e. UTF8 or two-byte Unicode data), and send it as a byte array.
     */
    public String readString() {
        if (bufferIndex == bufferLast) return null;
        return new String(readBytes());
    }
    
    
    /**
     * Combination of readBytesUntil and readString. See caveats in
     * each function. Returns null if it still hasn't found what
     * you're looking for.
     *
     * If you want to move Unicode data, you can first convert the
     * String to a byte stream in the representation of your choice
     * (i.e. UTF8 or two-byte Unicode data), and send it as a byte array.
     */
    public String readStringUntil(int interesting) {
        byte b[] = readBytesUntil(interesting);
        if (b == null) return null;
        return new String(b);
    }

	/**
	 * @param buffer the buffer to set
	 */
	public void setBuffer(byte buffer[]) {
		this.buffer = buffer;
	}

	/**
	 * @return the buffer
	 */
	public byte[] getBuffer() {
		return buffer;
	}    


}


