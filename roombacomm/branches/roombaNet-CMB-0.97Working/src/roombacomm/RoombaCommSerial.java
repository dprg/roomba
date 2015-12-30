/*
 *  RoombaComm Serial Interface
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

package roombacomm;

import gnu.io.*;
import java.io.*;
import java.util.*;


/**
 *  The serial-port based implementation of RoombaComm.
 *  Handles both physical RS-232 ports, USB adapter ports like Keyspan 
 *  USA-19HS, and Bluetooth serial port profiles.
 * 
 * <p> Some code taken from processing.serial.Serial.  Thanks guys! </p>
 *
 *  The interaction model for setting the port and protocol and WaitForDSR parameters is as follows.
 *  <p>
 *  On creation, the class initializes the parameters, then tries to read .roomba_config.
 *  If it can read the config file and parse out the parameters, it sets the parameters to
 *  the values in the config file. Apps can read the current settings for display using methods
 *  on the class. Apps can override the settings by accepting user input and setting the
 *  parameters using methods on the class, or the connect() method. Parameters that are
 *  changed by the app are re-written in the config file, for use as defaults next run.
 *  Command-line apps can make these parameters optional, by using the defaults if the
 *  user doesn't specify them.
 *  
 *  SVN id value is $Id$  
 */
public class RoombaCommSerial extends RoombaComm implements SerialPortEventListener
{
static final int databits = 8;
    static final int parity   = SerialPort.PARITY_NONE;
    static final int stopbits = SerialPort.STOPBITS_1;
    /**
     * The time to wait in milliseconds after sending sensors command before
     * attempting to read
     */
    public static int updateSensorsPause = 400;

    /** the serial input stream, normally you don't need access to this */
    public InputStream input;
    /** the serial output stream, normally you don't need access to this */
    public OutputStream output;

    /** 
     * RXTX bombs when flushing output sometimes, so by default do not
     * flush the output stream.  If the output is too buffered to be 
     * useful, do:  
     *  roombacomm.comm.flushOutput = true;
     * before using it and see if it works.
     */
    public boolean flushOutput = false;
    byte buffer[] = new byte[32768];
    int bufferLast;

    //int bufferSize = 26;  // how big before reset or event firing
    //boolean bufferUntil;
    //int bufferUntilByte;

    /**
     * Let you check to see if a port is in use by another Rooomba
     * before trying to use it.
     */
    public static boolean isPortInUse( String pname ) {
        Boolean inuse = (Boolean) ports.get( pname );
        if( inuse !=null ) {
            return inuse.booleanValue();
        }
        return false;
    }

    // constructor
    public RoombaCommSerial() {
        super();
        makePorts();
        readConfigFile();
    }
    public RoombaCommSerial(boolean autoupdate) {
        super(autoupdate);
        makePorts();
        readConfigFile();
    }
    public RoombaCommSerial(boolean autoupdate, int updateTime) {
        super(autoupdate, updateTime);
        makePorts();
        readConfigFile();
    }

    void makePorts() {
        if( ports == null ) 
            ports = Collections.synchronizedMap(new TreeMap());
    }
    /**
     * Connect to a serial port specified by portid
     * doesn't guarantee connection to Roomba, just to serial port
     * @param portid name of port, e.g. "/dev/cu.KeySerial1" or "COM3"
     * @return true if connect was successful, false otherwise
     */
    public boolean connect(String portid) {
        logmsg("connecting to port '"+portid+"'");
        portname = portid;
		writeConfigFile(portname, getProtocol(), waitForDSR?'Y':'N');

        if( isPortInUse( portid ) ) {
            logmsg("port is in use");
            return false;
        }

        connected = open_port();

        if( connected ) {
            // log in the global ports hash if the port is in use now or not
            ports.put( portname, new Boolean( connected ) );
            sensorsValid = false;
        }
        else {
            disconnect();
        }
        
        return connected;
    }
    public boolean connect(String portid,String protocal) {
    	String setProt="OI";
    	if (protocal.equalsIgnoreCase("SCI")){
    		setProt="SCI";
    	}
    	if (protocal.equalsIgnoreCase("OI")){
    		setProt="OI";
    	}
    	setProtocol(setProt);
        logmsg("connecting to port '"+portid+"' using protocal '"+setProt+"'");
        return connect(portid);
    }

    /**
     * Disconnect from serial port
     */
    public void disconnect() {
        connected = false;

        // log in the global ports hash if the port is in use now or not
        ports.put( portname, new Boolean( connected ) );

        try {
            // do io streams need to be closed first?
            if (input != null) input.close();
            if (output != null) output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        input = null;
        output = null;
    
        try {
            if (serialPort != null) serialPort.close();  // close the port 
        } catch (Exception e) {
            e.printStackTrace();
        }
        serialPort = null;
    }

    /**
     * subclassed.  FIXME: 
     */
    public boolean send(byte[] bytes) {
        try {
            output.write(bytes);
            if( flushOutput ) output.flush();   // hmm, not sure if a good idea
        } catch (Exception e) { // null pointer or serial port dead
            e.printStackTrace();
        }
        return true;
    }

    /**
     * This will handle both ints, bytes and chars transparently.
     */
    public boolean send(int b) {  // will also cover char or byte
        try {
            output.write(b & 0xff);  // for good measure do the &
            if( flushOutput ) output.flush();   // hmm, not sure if a good idea
        } catch (Exception e) { // null pointer or serial port dead
            //errorMessage("send", e);
            e.printStackTrace();
        }
        return true;
    }

    /**
     * toggles DD line via serial port DTR  (if available)
     */
    public void wakeup() {
        serialPort.setDTR(false);
        pause(500);
        serialPort.setDTR(true);
    }
  
    /**
     * Update sensors.  Block for up to 1000 ms waiting for update
     * To use non-blocking, call sensors() and then poll sensorsValid()
     */
    public boolean updateSensors() {
        sensorsValid = false;
        sensors();
        for(int i=0; i < 20; i++) {
            if( sensorsValid ) { 
                logmsg("updateSensors: sensorsValid!");
                break;
            }
            logmsg("updateSensors: pausing...");
            pause( 50 );
        }

        return sensorsValid;
    }
    
    /**
     * Update sensors.  Block for up to 1000 ms waiting for update
     * To use non-blocking, call sensors() and then poll sensorsValid()
     */
    public boolean updateSensors(int packetcode) {
        sensorsValid = false;
        sensors(packetcode);
        for(int i=0; i < 20; i++) {
            if( sensorsValid ) { 
                logmsg("updateSensors: sensorsValid!");
                break;
            }
            logmsg("updateSensors: pausing...");
            pause( 50 );
        }

        return sensorsValid;
    }

    /**
     * called by serialEvent when we have enough bytes to make sensors valid
     */
    public void computeSensors() {
        sensorsValid = true;
        sensorsLastUpdateTime = System.currentTimeMillis();
        computeSafetyFault();
    }
        /*
        pause(updateSensorsPause);     // take a breather to let data come back
        sensorsValid = false;          // assume the worst, we're gothy
        int n = available();
        //logmsg("updateSensors:n="+n);
        if( n >= 26) {                 // there are enough bytes to read
            n = readBytes(sensor_bytes);
            if( n==26 ) {              // did we get enough?
                sensorsValid = true;   // then everything's good, otherwise bad
                computeSafetyFault();
            }
        } else {
            logmsg("updateSensors:only "+n+" bytes available, not updating sensors");
        }
        
        //logmsg("buffer contains: "+ buffer );
        return sensorsValid;
        */

    /**
     * If this just hangs and never completes on Windows,
     * it may be because the DLL doesn't have its exec bit set.
     * Why the hell that'd be the case, who knows.
     * FIXME: deal more gracefully
     * (from processing.serial.Serial)
     */
    public String[] listPorts() {
        Map ps = Collections.synchronizedMap(new LinkedHashMap());
        //Vector list = new Vector();
        try {
            //System.err.println("trying");
        	Enumeration portList=null;
        	try {
        		portList = CommPortIdentifier.getPortIdentifiers();
        	} catch (Exception e) {
                //System.err.println("2");
                errorMessage("listPorts1", e);
            }
            //System.err.println("got port list");
            while (portList.hasMoreElements()) {
                CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
                logmsg("Found port: " + portId.getName());
                
                if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                    String name = portId.getName();
                    //list.addElement(name);
                    Boolean state = (Boolean) ports.get( name );
                    if( state==null ) state = new Boolean(false);
                    ps.put( name, state );
                }
            }
        } catch (UnsatisfiedLinkError e) {
            //System.err.println("1");
            errorMessage("listPorts", e);
        } catch (Exception e) {
            //System.err.println("2");
            errorMessage("listPorts", e);
        }
        //System.err.println("move out");
        /*
        for( Enumeration e = list.elements(); e.hasMoreElements(); ) {
            String p = (String) e.nextElement();
            if( ! ports.containsKey( p ) ) {
                ports.put( p, new Boolean(false) );
            }
        }

        // DEBUG
        System.err.println("ports hashtable:");
        for( Enumeration e = ports.keys(); e.hasMoreElements(); ) {
            String p = (String) e.nextElement();
            Boolean b = (Boolean) ports.get(p);
            System.err.println("port:"+p+", inuse:"+b);
        }
        */
        ports = ps;
        String outgoing[] = 
            (String[]) new TreeSet(ports.keySet()).toArray(new String[0]);

        return outgoing;
    }


    public boolean isWaitForDSR() {
		return waitForDSR;
	}

	public void setWaitForDSR(boolean waitForDSR) {
		this.waitForDSR = waitForDSR;
		writeConfigFile(portname, getProtocol(), waitForDSR?'Y':'N');
	}

	public String getPortname() {
		return portname;
	}
	
	public void setPortname(String p) {
		portname = p;
		logmsg("Port: " + portname);
		writeConfigFile(portname, getProtocol(), waitForDSR?'Y':'N');

	}

   // -------------------------------------------------------------

    // below only used internally to this class
    // -------------------------------------------------------------

	/**
     * internal method, used by connect()
     * FIXME: make it faile more gracefully, recognize bad port
     */
    private boolean open_port() {
        boolean success = false;
        try {
            Enumeration portList = CommPortIdentifier.getPortIdentifiers();
            while (portList.hasMoreElements()) {
                CommPortIdentifier portId =
                    (CommPortIdentifier) portList.nextElement();
                
                if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                    System.out.println("found " + portId.getName());
                    if (portId.getName().equals(portname)) {
                        logmsg("open_port:"+ portId.getName());
                        serialPort = (SerialPort)portId.open("roomba serial", 2000);
                        //port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
                        input  = serialPort.getInputStream();
                        output = serialPort.getOutputStream();
                        serialPort.setSerialPortParams(rate,databits,stopbits,parity);
                        serialPort.addEventListener(this);
                        serialPort.notifyOnDataAvailable(true);
                        logmsg("port "+portname+" opened successfully");

                        if( waitForDSR ) {
                            int i=40;
                            while( !serialPort.isDSR() && i-- != 0) {
                                logmsg("DSR not ready yet");
                                pause(150); // 150*40 = 6 seconds
                            }
                            success = serialPort.isDSR();
                        } else {
                            success = true;
                        }
                    }
                }
            }
      
        } catch (Exception e) {
            logmsg("connect failed: "+e);
            serialPort = null;
            input = null;
            output = null;
        }
                        
        return success;
    }

    /**
     * callback for SerialPortEventListener
     * (from processing.serial.Serial)
     */
    synchronized public void serialEvent(SerialPortEvent serialEvent) {
        try {
        logmsg("serialEvent:"+serialEvent+", nvailable:"+input.available());
        if (serialEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                while (input.available() > 0) {
                    //logmsg("serialEvent: available="+input.available());
                    buffer[bufferLast++] = (byte) input.read();
                    if( bufferLast == 26 ) {
                        bufferLast = 0;
                        System.arraycopy(buffer, 0, sensor_bytes, 0, 26);
                        computeSensors();
                    }
                    /*
                    synchronized (buffer) {
                        if (bufferLast == buffer.length) {
                            byte temp[] = new byte[bufferLast << 1];
                            System.arraycopy(buffer, 0, temp, 0, bufferLast);
                            buffer = temp;
                        }
                        buffer[bufferLast++] = (byte) input.read();
                    }
                    */
                } // while
        }
        } catch (IOException e) {
            errorMessage("serialEvent", e);
        }
    }
    
    @SuppressWarnings("unchecked")
	public void playSong(RoombaComm roombacomm, String rtttl){
    	ArrayList notelist = RTTTLParser.parse( rtttl );
        int songsize = notelist.size();
        // if within the size of a roomba song,  make the nsong, then play
        if( songsize <= 16 ) {
        	updateDisplay("creating a song with createSong()", true);
        	int notearray[] = new int[songsize*2];
            int j=0;
            for( int i=0; i< songsize; i++ ) {
                Note note = (Note) notelist.get(i);
                int sec64ths = note.duration * 64/1000;
                notearray[j++] = note.notenum;
                notearray[j++] = sec64ths;
            }
            roombacomm.createSong( 1, notearray );
            roombacomm.playSong( 1 );
        }
        // otherwise, try to play it in realtime
        else {
        	updateDisplay("playing song in realtime with playNote()",true);
            int fudge = 20;
            for( int i=0; i< songsize; i++ ) {
                Note note = (Note) notelist.get(i);
                int duration = note.duration;
                int sec64ths = duration*64/1000;
                if( sec64ths < 5 ) sec64ths = 5;
                if( note.notenum != 0 )
                    roombacomm.playNote( note.notenum, sec64ths );
                roombacomm.pause( duration + fudge );
            }
        }
    }	
}
