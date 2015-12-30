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

import com.hackingroomba.roombacomm.RobotConnection.ReadTerminator;
import com.hackingroomba.roombacomm.RobotType.robotTypes;

import java.io.*;

/**
 * The extensions to roombacomm for ArduinoBot operations.
 * 
 * <h2> Overview </h2>
 * This class contains the operations available from an ArduinoBot
**/

public class ArduinoBot extends RoombaComm {
	
	// connection variables
	private String arduinoString;
	private RobotConnection robotConnection;
	private RobotType robotType;
	
	// compass variables
	private Integer compassHeading;
	private int compassOffset = 0;
	private boolean compassOffsetInitialized = false;
	private boolean testCompassMode;	// when set, returns a test heading
	private int previousCompass;
	
	// encoder variables
	private int encoder;

	private long startTime, endTime;
	public long elapsedTime;
	
	public ArduinoBot() {
		super();
	}
	
	public ArduinoBot(RobotConnection robotConnection) {
		super(robotConnection);
		this.robotConnection = robotConnection;
	}

	/**
	 * Initialize an Arduino robot controller
	 */
	public boolean initArduinoBot(RobotType rt)
	{
		byte[] robotTypeMsg = "robot m\r".getBytes();	// message to Arduino to tell it the robot type
		
		robotType = rt;		// record robotType for later use
		System.out.println("checking for ArduinoBot... \n");
		try {
			Thread.sleep(2000);			
		} catch (Exception e) {System.out.println("Exception in Thread.sleep()"); }	
		
		// send the robot type message to Arduino, so it knows how to read the encoders, and any other robot-specific things
		if (robotType.robotType == RobotType.robotTypes.frankenRoomba) {
			System.out.println("Sending robot r to Arduino");
			robotTypeMsg = "robot r\r".getBytes();
		} else if (robotType.robotType == robotTypes.tankbot) {
			System.out.println("Sending robot t to Arduino");
			robotTypeMsg = "robot t\r".getBytes();
		} 
		send(robotTypeMsg);
		
		printCompass();
		printEncoders();
		return true;
	}
	

    /** 
     * Send a single byte to the ArduinoBot 
     * (defined as int because of stupid java signed bytes). SendToArduino method of RobotConnection
     * is used because this is an ardunobot, which requires a leading m and trailing <LF> to make
     * the parser work right
     * @param b byte of an Arduino command to send
     * @return true on successful send
     */
    public boolean send(int b) {
     	return(robotConnection.sendToArduino(b));
    }
    
   /**
     * Send a byte array to the ArduinoBot. If the first byte is 128 or higher, preface the byte string with m<space>
     * so it's treated by Arduino as a roomba command
     */
    public boolean send(byte[] bytes) {
    	if (bytes[0] < 0)		// must be a roomba command
    		return(robotConnection.sendToArduino(bytes));
    	else
    		return(robotConnection.send(bytes));
    }

	/**
	 * Read a string from Arduino up to a <LF>
	 * @return array of bytes read from ArduinoBot
	 */
    public String readArduinoBot() throws Exception {
    	return(robotConnection.readBotToTerminator(ReadTerminator.LF));
    }

	public void printCompass() {
		int compassReading;
		startTime = System.currentTimeMillis();
		try {
			compassReading = getCompass();			
		} catch (Exception e) {
			return;
		}
		endTime = System.currentTimeMillis();
	    elapsedTime = endTime - startTime;
    	System.out.println("Heading: " + compassReading + " in " + elapsedTime + " ms");
	}
	
	public int getCompass() throws Exception
	{
		// read the uncorrected heading
		compassHeading = getUncorrectedCompass();			
		// if we haven't initialized the compass offset yet, try to read an offset from a config file
		// compassOffsetInitialized ensures we only try this once
		try {
			if (!compassOffsetInitialized) {	// get the compass offset from file if it exists
				compassOffset = readConfigInt(new String("compass_offset"));
				compassOffsetInitialized = true;
			}
		} catch (Exception e) {
			System.out.println("No compass_offset file could be opened\n" + e.getMessage());
			compassOffsetInitialized = true;
		}
		
		// offset the uncorrected heading by the compassOffset, which depends on mounting position
    	compassHeading -= compassOffset;
		compassHeading %= 360;		// compensate for offset which can make it exceed 360
    	if (compassHeading < 0) compassHeading += 360;
		return compassHeading;
	}

	private int getUncorrectedCompass() throws Exception {
		byte [] compassCmd = {'c', '\r'};
		int heading;
		int i = 3;		// try reading the compass 3 times before giving up
		
		if (testCompassMode) {
			return compassHeading;		// setCompassTestHeading was used to set compassHeading
		}
		//System.out.println("getUncorrectedCompass");
		startTime = System.currentTimeMillis();
		
		while (i != 0) {
			if (!send(compassCmd)) {
				System.out.println("Error sending c command");
			}
			try {	
				arduinoString = robotConnection.readBotToTerminator(ReadTerminator.LF);
				if (arduinoString == null) {
					System.out.println("Error: getUncorrectedCompass failed to read heading - null string");
				} else {
					break;
				}
			} catch (Exception e) {
	    	    elapsedTime = System.currentTimeMillis() - startTime;
				System.err.println("Exception reading compass in readBotToTerminator in " + elapsedTime + " ms");
	            //e.printStackTrace();
	        }	
			i--;
		}
	    elapsedTime = System.currentTimeMillis() - startTime;
	    if (i == 0) {
	    	throw new Exception("Throwing exception owing to fail to read compass in 3 tries");
	    }

    	//System.out.println("received string: " + arduinoString);
    	String headingString = arduinoString.split("\\s")[0];
    	try {
        	heading = new Integer(headingString);   		
    	} catch (Exception e) {
    		heading = previousCompass;
    	}
    	previousCompass = heading;
    	return heading;
	}
	
	public boolean setCompassOffset(int currentMagHeading)
	{
		int heading;
		
		if ((currentMagHeading < 0) || (currentMagHeading > 360)) {
			System.out.println("currentMagHeading must be between 0 and 360");
			return false;
		}
		try {
			heading = getUncorrectedCompass();			
			if (heading < 0) return false;
			compassOffset = heading - currentMagHeading;
			if (compassOffset < 0) compassOffset += 360;
			writeConfigInt(new String("compass_offset"), compassOffset);
		} catch (Exception e) {
			System.err.println("Error getting compass or writing compass offset");
			e.printStackTrace();
			return false;
		}
		compassOffsetInitialized = true;
		return true;
	}

	/* 
	 * For testing, cause the compass to return a test value which we pass in. 
	 * No way out of test mode!
	 */
	public void setCompassTestHeading(int testHeading, int testOffset)
	{
		testCompassMode = true;
		compassOffsetInitialized = true;
		compassOffset = testOffset;
		compassHeading = testHeading;
	}
	
	/*
	 * Print, Get the current wheel encoders reading
	 */
	public void printEncoders()
	{
		double readEncDistance = 0;
		
		startTime = System.currentTimeMillis();
		try {
			readEncDistance = getEncoders();
		} catch (Exception e) {
			System.err.println("Exception reading robot encoders: " + e.getMessage());
		}
		long elapsedTime = System.currentTimeMillis() - startTime;
		System.out.println("Wheel encoders reading: " + readEncDistance + " in " + elapsedTime + " ms");

	}
	
	public double getEncoders() throws Exception {
		byte [] cmd = {'e', '\r'};
		
		//System.out.println("read encoders");
		startTime = System.currentTimeMillis();		
		if (!send(cmd)) {
			System.out.println("Error sending e command");
		}
		try {	
			arduinoString = robotConnection.readBotToTerminator(ReadTerminator.LF);
		    elapsedTime = System.currentTimeMillis() - startTime;
			if (arduinoString == null) {
				System.err.println("Error: getEncoders failed to read encoders, null returned in " + elapsedTime + " ms");
				return -1;
			}
		} catch (Exception e) {
		    elapsedTime = System.currentTimeMillis() - startTime;
		    System.err.println("Exception reading encoders from readBotToTerminator in " + elapsedTime + " ms");
            e.printStackTrace();
            throw e;
        }

    	//System.out.println("received string: " + arduinoString);
    	String [] encoderStrings = arduinoString.split("\\s");
    	if (encoderStrings.length == 1) {
        	encoder = new Integer(encoderStrings[0]);
    	} else {
    		System.out.println("Error reading encoders - expected 1 string, found:" + encoderStrings.length 
    				+ " in: " + arduinoString + " in " + elapsedTime + " ms");
    		return -1;
    	}
    	
		double encoderDistance = encoder /robotType.countsPerInch;
		return encoderDistance;
	}
	

	/**
	 * Read roomba 26-byte sensor record using robotConnection. Tries once to read valid data, allowing 100ms
	 * timeout on each attempt. 
	 * @return true if read 26 bytes of valid data. Data has been stored in sensor_bytes. False otherwise
	 */
 	public boolean updateSensors()
 	{
 		return updateSensors(SENSORS_ALL);
 	}
 	
 	public boolean updateSensors(int sensorGroup)
 	{
    	int sensorGroupSize;
    	
 		if (robotConnection == null) {
 	 		System.out.println("Error at ArduinoBot.updateSensors(): no connection object for robot");
 	 		return false; 			
 		}

 		switch(sensorGroup) {
 		case SENSORS_ALL: sensorGroupSize = 26; break;
 		case 100: sensorGroupSize = 80; break;
 		default:
 			System.err.println("Invalid sensor group in updateSensors(): " + sensorGroup);
 			return false;
 		}
    	sensors(sensorGroup);
 		return getSensorData(sensorGroupSize); 		
 	}

	/**
	 * Writes an int to the named file
	 * @throws FileNotFoundException If it can't open the file
	 * @throws IOException If it can't write to the file
	 */
	public void writeConfigInt(String filename, int value) throws FileNotFoundException, IOException {
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(filename)));
		dos.writeInt(value);
	}
	
	/**
	 * Read a configuration file containing an int.
	 * @param filename The file to open & read an int from
	 * @return Int read from file
	 * @throws Exception related to attempting to open & read int from file
	 */
	public int readConfigInt(String filename) throws Exception {
		int configInt = -1;
		
		try {
			DataInputStream dis = new DataInputStream(new FileInputStream(new File(filename)));	
			configInt = dis.readInt();

		} catch (Exception e) {
			System.out.println("No config file could be read\n" + e.getMessage());
		}
		return configInt;
	}


	/**
	 * Writes a double to the named file
	 * @throws FileNotFoundException If it can't open the file
	 * @throws IOException If it can't write to the file
	 */
	public void writeConfigDouble(String filename, double value) throws FileNotFoundException, IOException {
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(filename)));
		dos.writeDouble(value);
	}
	
	/**
	 * Read a configuration file containing a double.
	 * @param filename The file to open & read a double from
	 * @return double read from file
	 * @throws Exception related to attempting to open & read double from file
	 */
	public double readConfigDouble(String filename) throws Exception {
		double configDouble = -1;
		
		try {
			DataInputStream dis = new DataInputStream(new FileInputStream(new File(filename)));	
			configDouble = dis.readDouble();

		} catch (Exception e) {
			System.out.println("No config file could be read\n" + e.getMessage());
			throw(e);
		}
		return configDouble;
	}

	public int getEncoder() {
		return encoder;
	}

	/**
	 * Bogus methods which should never be called on ArduinoClient
	 */
 	public String [] listPorts()
 	{
 		System.out.println("Error at ArduinoBot.listPorts(): should never get here");
 		return (new String[] {"Error: listports calledon ArdunoBot; not supported"});
 	}
 	public boolean connect(String s)
 	{
 		System.out.println("Error at ArduinoBot.connect(): should never get here");
 		return false;
 	}
 	public void disconnect()
 	{
 		System.out.println("Error at ArduinoBot.disconnect(): should never get here");
 		return;
 	}

	public int getCompassOffset() {
		return compassOffset;
	}

}