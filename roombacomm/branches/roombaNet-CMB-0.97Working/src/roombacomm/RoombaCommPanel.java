/*
 * RoombaCommPanel - 
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

import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.SSLEngineResult.Status;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import javax.swing.text.*;

import roombacomm.net.RoombaCommTCPClient;
//import sun.misc.Cleaner;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
/**
 * A Panel containing controls for testing RoombaComm. 
 * Normally put inside of a frame, for example see RoombaCommTest
 *
 * SVN id value is $Id$
 */
public class RoombaCommPanel extends JPanel implements ActionListener,ChangeListener {

    JPanel ctrlPanel, selectPanel, buttonPanel, displayPanel, ledPanel;
    JComboBox portChoices;
    JComboBox protocolChoices;
    JCheckBox handshakeButton;
    JTextArea displayText;
    JButton  connectButton;
    JButton  netButton;
    JSlider speedSlider , powerColorSlider , powerColorIntensity;
    private boolean debug = false;
    boolean tribbleOn = false;
//     default values for flags for LEDs 
    boolean redOn = false;
    boolean greenOn = false;
    boolean toggleSpot = false;
    boolean toggleClean = false;
    boolean toggleMax = false;
    boolean toggleDirt = false;
    boolean toggleCheckRobot = false;
	boolean toggleDock = false;
    int power_color=0;
    int power_int = 0;
    RoombaCommSerial roombaCommSerial;
    RoombaComm roombaComm; 
    RoombaCommTCPClient roombaCommTCPClient;
    SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss");
    

    public RoombaCommPanel() {
        super(new BorderLayout());
        
        roombaCommSerial = new RoombaCommSerial(true, 1);
        
        makePanels();
    }
    
    public RoombaCommPanel(boolean debug) {
        super(new BorderLayout());
        
        roombaCommSerial = new RoombaCommSerial();
        roombaCommTCPClient = new RoombaCommTCPClient();
        //String portname = "test";  // e.g. "/dev/cu.KeySerial1"
        //RoombaComm roombacomm = new RoombaCommTCPClient();
        this.debug=debug;
        roombaCommSerial.debug = debug;
        roombaCommTCPClient.debug = debug;
        makePanels();
    }
    /**
     * Set to 'false' to hide the "h/w handshake" button, which seems to be
     * only needed on Windows
     */
    public void setShowHardwareHandhake( boolean b ) {
        handshakeButton.setVisible( b );
    }

    /** */
    public boolean connect() {
        String portname = (String) portChoices.getSelectedItem();
        //roombacomm.debug=true;
        roombaCommSerial.setWaitForDSR(handshakeButton.isSelected());
        int i = protocolChoices.getSelectedIndex();
        roombaCommSerial.setProtocol((i==0)?"SCI":"OI");
        
        connectButton.setText("connecting");
        if( ! roombaCommSerial.connect( portname ) ) {
            updateDisplay("Couldn't connect to "+portname+"\n");
            connectButton.setText("  connect  ");
            //roombacomm.debug=false;
            return false;
        }
        updateDisplay("Roomba startup\n");

        roombaCommSerial.startup();
        roombaCommSerial.control();
        roombaCommSerial.playNote( 72, 10 );  // C , test note
        roombaCommSerial.pause( 200 );

        connectButton.setText("disconnect");
        connectButton.setActionCommand("disconnect");
        roombaComm=roombaCommSerial;
        //roombacomm.debug=true;
        updateDisplay("Checking for Roomba... ");
        if( roombaCommSerial.updateSensors() )
            updateDisplay("Roomba found!\n");
        else
            updateDisplay("No Roomba. :(  Is it turned on?\n");

        return true;
    }
    public boolean connectNet(String portname) {
    	System.out.println("connectNet called");
    	int i = protocolChoices.getSelectedIndex();

    	roombaCommTCPClient.setProtocol((i==0)?"SCI":"OI");
        
    	if( ! roombaCommTCPClient.connect( portname ) ) {
    		updateDisplay("Couldn't connect to "+portname);
            return false;
        }
    	
    	updateDisplay("Roomba startup on port "+portname);     
        roombaCommTCPClient.startup();
        roombaCommTCPClient.control();
        //roombacomm.setSensorsAutoUpdate(true);
        roombaCommTCPClient.pause(30);
        

        updateDisplay("Checking for Roomba... \n");
        //roombaCommTCPClient.setDebug(true);
        if( roombaCommTCPClient.updateSensors() ){
        	updateDisplay("Roomba found!\n");
        	updateDisplay(roombaCommTCPClient.getSensorsAsString());
        }else{
        	updateDisplay("No Roomba. :(  Is it turned on?\n");
        }
        updateDisplay("buffer is("+roombaCommTCPClient.getBuffer()+")", this.debug);
        //roombaCommTCPClient.setDebug(false);
        //roombacomm.updateSensors();
        updateDisplay("connected ("+roombaCommTCPClient.connected()+")\n");
        if (roombaCommTCPClient.connected()){
        	updateDisplay("Playing some notes\n");
	        roombaCommTCPClient.playNote( 72, 10 );  // C
	        roombaCommTCPClient.pause( 200 );
	        roombaCommTCPClient.playNote( 79, 10 );  // G
	        roombaCommTCPClient.pause( 200 );
	        roombaCommTCPClient.playNote( 76, 10 );  // E
	        roombaCommTCPClient.pause( 200 );
	        netButton.setText("disconnect-net");
	        netButton.setActionCommand("disconnect-net");
	        roombaComm=roombaCommTCPClient; // set the pointer so that the action stuff can work against any class
        }
        
//        roombaCommSerial.setWaitForDSR(handshakeButton.isSelected());
//        
//        
//        connectButton.setText("connecting");
//        if( ! roombaCommSerial.connect( portname ) ) {
//            updateDisplay("Couldn't connect to "+portname+"\n");
//            connectButton.setText("  connect  ");
//            //roombacomm.debug=false;
//            return false;
//        }
//        updateDisplay("Roomba startup\n");
//
//        roombaCommSerial.startup();
//        roombaCommSerial.control();
//        roombaCommSerial.playNote( 72, 10 );  // C , test note
//        roombaCommSerial.pause( 200 );
//
//        connectButton.setText("disconnect");
//        connectButton.setActionCommand("disconnect");
//        //roombacomm.debug=true;
//        updateDisplay("Checking for Roomba... ");
//        if( roombaCommSerial.updateSensors() )
//            updateDisplay("Roomba found!\n");
//        else
//            updateDisplay("No Roomba. :(  Is it turned on?\n");

        return true;
    }

    /** */
    public void disconnect() {
        roombaCommSerial.disconnect();
        connectButton.setText("  connect  ");
        connectButton.setActionCommand("connect");
    }
    public void disconnectNet() {
        roombaCommTCPClient.disconnect();
        netButton.setText("  net  ");
        netButton.setActionCommand("net");
    }
    /**
     * Play a (MIDI) note, that is, make the Roomba a musical instrument
     * notenums 32-127:
     *   notenum  == corresponding note played thru beeper
     *   velocity == duration in number of 1/64s of a second (e.g. 64==1second)
     * notenum 24:
     *    notenum  == main vacuum
     *    velocity == non-zero turns on, zero turns off 
     * notenum 25: 
     *    blink LEDs, velcoity is color of Power 
     * notenum 28 & 29:
     *    spin left & spin right, velocity is speed
     *
     */
    public void playMidiNote( int notenum, int velocity ) {
        updateDisplay("play note: "+notenum+","+velocity+"\n");
        if( !roombaCommSerial.connected() ) return;
        
        if( notenum >= 31 ) {                // G and above
            if( velocity == 0 ) return;
            if( velocity < 4 ) velocity = 4; // has problems at lower durations
            else velocity = velocity/2;
            roombaCommSerial.playNote( notenum, velocity );
        } 
        else if( notenum == 24 ) {           // C
            roombaCommSerial.vacuum( !(velocity==0) );
        }
        else if( notenum == 25 ) {           // C#
            boolean lon = (velocity!=0);
            int inten = (lon) ? 255:128; // either full bright or half bright
            //roombaCommSerial.setLEDs( lon,lon,lon,lon,lon,lon, velocity*2, inten );
        }
        else if( notenum == 28 ) {           // E
            if( velocity!=0 ) roombaCommSerial.spinLeftAt( velocity*2 );
            else roombaCommSerial.stop();
        }
        else if( notenum == 29 ) {           // F
            if( velocity!=0 ) roombaCommSerial.spinRightAt( velocity*2 );
            else roombaCommSerial.stop();
        }
    }

    /** implement actionlistener */
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        updateDisplay(formatter.format(new Date())+": action ("+action+") happened\n", this.debug);
        //roombacomm.setLEDs(false, false, false, false, false, false, 0, 0);
        if( "comboBoxChanged".equals(action) ) {
            String portname = (String) portChoices.getSelectedItem();
//            roombaComm.setPortname(portname);
            int i = protocolChoices.getSelectedIndex();
            if (roombaComm != null){
            	roombaComm.setProtocol((i==0)?"SCI":"OI");
            }else{
            	updateDisplay(formatter.format(new Date())+": null roombaComm object found in actionPerformed\n", this.debug);    	
            }
            return;
        }
//        updateDisplay(action+"\n");
        if( "net".equals(action) ) {
            connectNet("192.168.0.3:5001");
            //roombaComm.setDebug(true);
            return;
        }else if( "disconnect-net".equals(action) ) {
            disconnectNet();
            return;
        }
        if( "connect".equals(action) ) {
            connect();
            return;
        }
        else if( "disconnect".equals(action) ) {
            disconnect();
            return;
        }

        // stop right here if we're not connected
        if( !roombaComm.connected() ) {
            updateDisplay("not connected!\n");
            return;
        }

        if( "stop".equals(action) ) {
        	roombaComm.stop();
        }
        else if( "forward".equals(action) ) {
//        	updateDisplay("Speed is("+roombaComm.getSpeed()+")\n");
        	roombaComm.goForward();
        }
        else if( "backward".equals(action) ) {
//        	updateDisplay("Speed is("+roombaComm.getSpeed()+")\n");
        	roombaComm.goBackward();
        }
        else if( "spinleft".equals(action) ) {
//        	updateDisplay("Speed is("+roombaComm.getSpeed()+")\n");
        	roombaComm.spinLeft();
        }
        else if( "spinright".equals(action) ) {
//        	updateDisplay("Speed is("+roombaComm.getSpeed()+")\n");
        	roombaComm.spinRight();
        }
        else if( "turnleft".equals(action) ) {
        	roombaComm.turnLeft();           
        }
        else if( "turnright".equals(action) ) {
        	roombaComm.turnRight();
        }
        else if( "max".equals(action) ) {
        	roombaComm.max();
        }
        else if( "dock".equals(action) ) {
        	roombaComm.dock();
        }
        else if( "test".equals(action) ) {
            LogoA.square(roombaComm, 300);
            /*
        	updateDisplay("Playing some notes\n");
            roombacomm.playNote( 72, 10 );  // C
            roombacomm.pause( 200 );
            roombacomm.playNote( 79, 10 );  // G
            roombacomm.pause( 200 );
            roombacomm.playNote( 76, 10 );  // E
            roombacomm.pause( 200 );

            updateDisplay("Spinning left, then right\n");
            roombacomm.spinLeft();
            roombacomm.pause(1000);
            roombacomm.spinRight();
            roombacomm.pause(1000);
            roombacomm.stop();

            updateDisplay("Going forward, then backward\n");
            roombacomm.goForward();
            roombacomm.pause(1000);
            roombacomm.goBackward();
            roombacomm.pause(1000);
            roombacomm.stop();
 */
        }
        else if( "OSU".equals(action) ) {
            updateDisplay("Going to play OSU\n");
            
            roombaComm.stop();
            roombaComm.pause(500);
//            RTTTLPlay rp = new RTTTLPlay();
//            /dev/cu.KeySerial1 'tron:d=4,o=5,b=200:8f6,8c6,8g,e,8p,8f6,8c6,8g,8f6,8c6,8g,e,8p,8f6,8c6,8g,e.,2d'
//            String[] osuSong={roombacomm.getPortname(),"OSU:d=4,o=5,b=125:a,g,a#,a,8g#,a,8g#,2a,8p,8f,8g,8g#,a,8g#,a,8g#,a,g,f,a,g,8a,g,d,8f,8p,8f,8p,8f,8p,8f,8p,c6,a,g,f,8a#,a,8g,f,p,c6,a,g,a,8a#,a,8a#,c6,p,2d6,d,8c6,a#,g,f,8f,8f,8g,a#,8g,a#,a,2a#"};
            playSong(roombaComm, "OSU:d=4,o=5,b=125:a,g,a#,a,8g#,a,8g#,2a,8p,8f,8g,8g#,a,8g#,a,8g#,a,g,f,a,g,8a,g,d,8f,8p,8f,8p,8f,8p,8f,8p,c6,a,g,f,8a#,a,8g,f,p,c6,a,g,a,8a#,a,8a#,c6,p,2d6,d,8c6,a#,g,f,8f,8f,8g,a#,8g,a#,a,2a#");
       
//            playSong(roombacomm, "Baa Baa Black Sheep:d=4,o=5,b=125:c,c,g,g,8a,8b,8c6,8a,g,p,f,f,e,e,d,d,c");
            roombaComm.stop();
        }
        else if( "Tribble On".equals(action) ) {
        	tribbleOn = true;
        	tribbleStart(roombaComm, displayText, tribbleOn);
        }
        else if( "reset".equals(action) ) {
        	roombaComm.stop();
        	roombaComm.startup();
        	roombaComm.control();
        }
        else if( "safe".equals(action) ) {
        	roombaComm.safe();
        }
        else if( "full".equals(action) ) {
        	roombaComm.full();
        }
        else if( "power-off".equals(action) ) {
        	roombaComm.powerOff();
        }
        else if( "power-on".equals(action) ) {
        	roombaComm.powerOn();
        }
        else if( "wakeup".equals(action) ) {
        	roombaComm.wakeup();
        }
        else if( "beep-lo".equals(action) ) {
        	roombaComm.playNote( 50, 32 );  // C1
        	roombaComm.pause( 200 );
        }
        else if( "beep-hi".equals(action) ) {
        	roombaComm.playNote( 90, 32 );  // C7
        	roombaComm.pause( 200 );
        }
        else if( "clean".equals(action) ) {
        	roombaComm.clean();
        }
        else if( "spot".equals(action) ) {
        	roombaComm.spot();
        }
        else if( "vacuum-on".equals(action) ) {
        	roombaComm.vacuum(true);
        }
        else if( "vacuum-off".equals(action) ) {
        	roombaComm.vacuum(false);
        }
        else if( "blink-leds".equals(action) ) {
//        	roombaComm.setLEDs( true,true,true,true,true,true, 255, 255 );
        	roombaComm.setChgCheckRobotLED(roombaComm, true);
        	roombaComm.setChgCleanLED(roombaComm, true);
        	roombaComm.setChgDirtLED(roombaComm, true);
        	roombaComm.setChgDockLED(roombaComm, true);
        	roombaComm.setChgGreenLED(roombaComm, true);
        	roombaComm.setChgMaxLED(roombaComm, true);
        	roombaComm.setChgPowerColorLED(roombaComm, 255);
        	roombaComm.setChgPowerIntensityLED(roombaComm, 255);
        	roombaComm.setChgRedLED(roombaComm, true);
        	roombaComm.setChgSpotLED(roombaComm, true);
        	roombaComm.setLEDs(roombaComm);
        	roombaComm.pause(300);
//        	roombaComm.setLEDs( false,false,false, false,false,false, 0, 128);
        	roombaComm.setChgCheckRobotLED(roombaComm, false);
        	roombaComm.setChgCleanLED(roombaComm, false);
        	roombaComm.setChgDirtLED(roombaComm, false);
        	roombaComm.setChgDockLED(roombaComm, false);
        	roombaComm.setChgGreenLED(roombaComm, false);
        	roombaComm.setChgMaxLED(roombaComm, false);
        	roombaComm.setChgPowerColorLED(roombaComm, 0);
        	roombaComm.setChgPowerIntensityLED(roombaComm, 128);
        	roombaComm.setChgRedLED(roombaComm, false);
        	roombaComm.setChgSpotLED(roombaComm, false);
        	roombaComm.setLEDs(roombaComm);
        }        
        else if( "sensors".equals(action) ) {
            if( roombaComm.updateSensors() )
                updateDisplay( roombaComm.sensorsAsString()+"\n");
            else 
                updateDisplay("couldn't read Roomba. Is it connected?\n");
        }else if( "toggleGreen".equals(action) ) {
        		setChgGreenLED(roombaComm, !greenOn);
        }else if( "toggleRed".equals(action) ) {
        		setChgRedLED(roombaComm, !redOn);
        }else if( "toggleSpot".equals(action) ) {
        	setChgSpotLED(roombaComm, !toggleSpot);
        }else if( "toggleClean".equals(action) ) {
        	setChgCleanLED(roombaComm, !toggleClean);
        }else if( "toggleMax".equals(action) ) {
        	setChgMaxLED(roombaComm, !toggleMax);
        }else if( "toggleDirt".equals(action) ) {
        	setChgDirtLED(roombaComm, !toggleDirt);
        }else if( "toggleCheckRobot".equals(action) ) {
        	setToggleCheckRobot(roombaComm, !toggleCheckRobot);        	
        }else if( "toggleDock".equals(action) ) {
        	setToggleDock(roombaComm, !toggleDock);        	
        }
        
    }

    /** implement ChangeListener, for the slider */
    public void stateChanged(ChangeEvent e) {
        //System.err.println("stateChanged:"+e);
        JSlider src = (JSlider)e.getSource();
        if (!src.getValueIsAdjusting()) {
            int speed = (int)src.getValue();
            speed = (speed<1) ? 1 : speed; // don't allow zero speed
            updateDisplay("setting speed = "+speed+"\n");
            roombaComm.setSpeed(speed);
        }
    }
    /** implement ChangeListener, for the slider */
//    public void setPowerColor(ChangeEvent e) {
//        JSlider src = (JSlider)e.getSource();
//        if (!src.getValueIsAdjusting()) {
//            setPower_color((int)src.getValue());
//            updateDisplay("setting Power Color = "+getPower_color()+"\n");
//            setLEDs(roombacomm);
//        }
//    }
    /** implement ChangeListener, for the slider */
//    public void setPowerColorIntensity(ChangeEvent e) {
//        //System.err.println("stateChanged:"+e);
//        JSlider src = (JSlider)e.getSource();
//        if (!src.getValueIsAdjusting()) {
//            setPower_int((int)src.getValue());
//            updateDisplay("setting Power Color Intensity = "+getPower_int()+"\n");
//            setLEDs(roombacomm);
//        }
//    }

    /**
     *
     */
    void makePanels() {
        makeSelectPanel();
        add( selectPanel, BorderLayout.NORTH );

        makeCtrlPanel();
        add( ctrlPanel, BorderLayout.EAST );        

        makeLedPanel();
        add( ledPanel, BorderLayout.WEST );
        
        makeButtonPanel();
        add( buttonPanel, BorderLayout.CENTER );

        makeDisplayPanel();
        add( displayPanel, BorderLayout.SOUTH );

        //pack(); //setVisible(true);
        updateDisplay("RoombaComm, version "+RoombaComm.VERSION+"\n");
    }
    
    void makeSelectPanel() {
        selectPanel  = new JPanel();
        
        // Create a combo box with protocols
        String[] protocols = {"Roomba 1xx-4xx (SCI)", "Roomba 5xx (OI)"};
        protocolChoices = new JComboBox(protocols);
        String p = roombaCommSerial.getProtocol();
        protocolChoices.setSelectedIndex(p.equals("SCI")?0:1);
        
        //Create a combo box with choices.
        String[] ports = roombaCommSerial.listPorts();
        portChoices = new JComboBox(ports);
        portChoices.setSelectedIndex(0);
        for( int i=0; i<ports.length; i++ ) { 
            String s = ports[i];
        	if (s.equals(roombaCommSerial.getPortname())) {
            	portChoices.setSelectedItem(s);        		
        	}
        }
        connectButton = new JButton();
        connectButton.setText("  connect  ");
        connectButton.setActionCommand("connect");
        handshakeButton = new JCheckBox("<html>h/w<br>handshake</html>");
        
        // net button
        netButton = new JButton();
        netButton.setText("  net  ");
        netButton.setActionCommand("net");
        
        //Add a border around the select panel.
        selectPanel.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createTitledBorder("Select Roomba Type & Port"), BorderFactory.createEmptyBorder(1,1,1,1)));
        
        selectPanel.add(protocolChoices);
        selectPanel.add(portChoices);
        selectPanel.add(connectButton);
        selectPanel.add(netButton);
        selectPanel.add(handshakeButton);

        //Listen to events from the combo box.
        portChoices.addActionListener(this);
        connectButton.addActionListener(this);
        netButton.addActionListener(this);
        protocolChoices.addActionListener(this);
    }
    

    /** 
     * 
     */
    void makeCtrlPanel() {
        JPanel ctrlPanel1 = new JPanel(new GridLayout(3,3));

        JButton but_turnleft =
            new JButton(createImageIcon("images/but_turnleft.png","turnleft"));
        ctrlPanel1.add( but_turnleft );
        JButton but_forward =
            new JButton(createImageIcon("images/but_forward.png","forward"));
        ctrlPanel1.add( but_forward );
        JButton but_turnright =
            new JButton(createImageIcon("images/but_turnright.png","turnright"));
        ctrlPanel1.add( but_turnright );

        JButton but_spinleft =
            new JButton(createImageIcon("images/but_spinleft.png","spinleft"));
        ctrlPanel1.add( but_spinleft );
        JButton but_stop =
            new JButton(createImageIcon("images/but_stop.png", "stop"));
        ctrlPanel1.add( but_stop );
        JButton but_spinright =
            new JButton(createImageIcon("images/but_spinright.png","spinright"));
        ctrlPanel1.add( but_spinright );

        ctrlPanel1.add(new JLabel());
        JButton but_backward =
            new JButton(createImageIcon("images/but_backward.png","backward"));
        ctrlPanel1.add( but_backward );
        ctrlPanel1.add(new JLabel());

        JLabel sliderLabel = new JLabel("speed (mm/s)", JLabel.CENTER);
        speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 500, 200 );
        speedSlider.setPaintTicks(true);
        speedSlider.setMajorTickSpacing(100);
        speedSlider.setMinorTickSpacing(25);
        speedSlider.setPaintLabels(true);
        speedSlider.addChangeListener(this);

        ctrlPanel = new JPanel();
        ctrlPanel.setLayout( new BoxLayout(ctrlPanel, BoxLayout.Y_AXIS ) );
        ctrlPanel.setBorder(BorderFactory.createCompoundBorder(
        		BorderFactory.createTitledBorder(null, "Movement", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION), 
        		BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        ctrlPanel.add( ctrlPanel1 );
        ctrlPanel.add( speedSlider );
        ctrlPanel.add( sliderLabel );

        but_turnleft.setActionCommand("turnleft");
        but_turnleft.setIcon(new ImageIcon(getClass().getClassLoader().getResource("roombacomm/images/but_turnleft.png")));
        but_turnright.setActionCommand("turnright");
        but_spinleft.setActionCommand("spinleft");
        but_spinright.setActionCommand("spinright");
        but_forward.setActionCommand("forward");
        but_backward.setActionCommand("backward");
        but_stop.setActionCommand("stop");
        but_turnleft.addActionListener(this);
        but_turnright.addActionListener(this);
        but_spinleft.addActionListener(this);
        but_spinright.addActionListener(this);
        but_forward.addActionListener(this);
        but_backward.addActionListener(this);
        but_stop.addActionListener(this);
    }
    
    void makeLedPanel() {
        JPanel ledPanel1 = new JPanel(new GridLayout(3,3));

//        if (roombaCommSerial.getProtocol().equalsIgnoreCase("SCI")) {
	        String off="None";
	        String green="Green";
	        String red="Red";
	        String both="Orange";
	        JRadioButton statusOffButton = new JRadioButton(off);
	        statusOffButton.setMnemonic(KeyEvent.VK_N);
	        statusOffButton.setActionCommand("StatusLED-"+off);
	        statusOffButton.setSelected(true);
	
	        JRadioButton statusGreenButton = new JRadioButton(green);
	        statusGreenButton.setMnemonic(KeyEvent.VK_G);
	        statusGreenButton.setActionCommand("StatusLED-"+green);
	
	        JRadioButton statusRedButton = new JRadioButton(red);
	        statusRedButton.setMnemonic(KeyEvent.VK_R);
	        statusRedButton.setActionCommand("StatusLED-"+red);
	
	        JRadioButton statusBothButton = new JRadioButton(both);
	        statusBothButton.setMnemonic(KeyEvent.VK_O);
	        statusBothButton.setActionCommand("StatusLED-"+both);
	
	        //Group the radio buttons.
	        ButtonGroup group = new ButtonGroup();
	        group.add(statusOffButton);
	        group.add(statusGreenButton);
	        group.add(statusRedButton);
	        group.add(statusBothButton);
	        ledPanel1.add(statusOffButton);
	        ledPanel1.add(statusGreenButton);
	        ledPanel1.add(statusRedButton);
	        ledPanel1.add(statusBothButton);
//		}        
        statusOffButton.addActionListener(new ActionListener() {
      		 public void actionPerformed(ActionEvent e) {
      			 updateDisplay("setting Power Color = "+e.getActionCommand()+"\n");
      			 setChgGreenLED(roombaCommSerial, false);
      			 setChgRedLED(roombaCommSerial, false);
      			 }
 		});
        statusGreenButton.addActionListener(new ActionListener() {
     		 public void actionPerformed(ActionEvent e) {
     			 updateDisplay("setting Power Color = "+e.getActionCommand()+"\n");
     			 setChgGreenLED(roombaCommSerial, true);
     			 setChgRedLED(roombaCommSerial, false);
     			 }
		});
        statusRedButton.addActionListener(new ActionListener() {
     		 public void actionPerformed(ActionEvent e) {
     			 updateDisplay("setting Power Color = "+e.getActionCommand()+"\n");
     			 setChgGreenLED(roombaCommSerial, false);
     			 setChgRedLED(roombaCommSerial, true);
     			 }
		});
        statusBothButton.addActionListener(new ActionListener() {
     		 public void actionPerformed(ActionEvent e) {
     			 updateDisplay("setting Power Color = "+e.getActionCommand()+"\n");
     			 setChgGreenLED(roombaCommSerial, true);
     			 setChgRedLED(roombaCommSerial, true);
     			 }
		});

        JButton but_toggleSpot= new JButton(createImageIcon("images/but_spotOn.png","turnright"));
        ledPanel1.add( but_toggleSpot );
        JButton but_toggleClean = new JButton(createImageIcon("images/but_cleanOn.png","spinleft"));
        ledPanel1.add( but_toggleClean );
        JButton but_toggleDirt = new JButton(createImageIcon("images/but_dirtOn.png","spinright"));
        ledPanel1.add( but_toggleDirt );

        JButton but_toggleMax = new JButton(createImageIcon("images/but_maxOn.png", "Maxon"));
        ledPanel1.add( but_toggleMax );

      
        JButton but_toggleDock = new JButton(createImageIcon("images/but_dockOn.png","Dockon"));
	    ledPanel1.add( but_toggleDock );	        
        
	    JButton but_toggleCheckRobot = new JButton(createImageIcon("images/but_checkrobotOn.png","CheckRobot"));
	    ledPanel1.add( but_toggleCheckRobot );	        

      
	    JLabel powerColorLabel = new JLabel("PowerColor", JLabel.CENTER);
        powerColorSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 100 );
        powerColorSlider.setPaintTicks(true);
        powerColorSlider.setMajorTickSpacing(100);
        powerColorSlider.setMinorTickSpacing(25);
        powerColorSlider.setPaintLabels(true);
        powerColorSlider.addChangeListener(
        		new ChangeListener() {
           		 public void stateChanged(ChangeEvent e) {
           		        JSlider src = (JSlider)e.getSource();
           		        if (!src.getValueIsAdjusting()) {
           		            setPower_color((int)src.getValue());
           		            updateDisplay("setting Power Color = "+getPower_color()+"\n");
           		            setLEDs(roombaCommSerial);
           		        }
           		 }
        		}
        );
        
        JLabel powerColorIntensityLabel = new JLabel("PowerColorIntensity", JLabel.CENTER);
        powerColorIntensity= new JSlider(JSlider.HORIZONTAL, 0, 255, 100 );
        powerColorIntensity.setPaintTicks(true);
        powerColorIntensity.setMajorTickSpacing(100);
        powerColorIntensity.setMinorTickSpacing(25);
        powerColorIntensity.setPaintLabels(true);
//        powerColorIntensity.addChangeListener(this);
//        powerColorIntensity.addChangeListener(new ChangeListener() {setPow });
        powerColorIntensity.addChangeListener(
        		new ChangeListener() {
        		 public void stateChanged(ChangeEvent e) {
        		        //System.err.println("stateChanged:"+e);
        		        JSlider src = (JSlider)e.getSource();
        		        if (!src.getValueIsAdjusting()) {
        		            setPower_int((int)src.getValue());
        		            updateDisplay("setting Power Color Intensity = "+getPower_int()+"\n");
        		            setLEDs(roombaCommSerial);
        		        }
        		 }
        		}
        );

        ledPanel = new JPanel();
        ledPanel.setLayout( new BoxLayout(ledPanel, BoxLayout.Y_AXIS ) );

        ledPanel.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createTitledBorder("LEDs"), BorderFactory.createEmptyBorder(5,5,5,5)));
        ledPanel.add( ledPanel1 );
        ledPanel.add( powerColorSlider );
        ledPanel.add( powerColorLabel );
        ledPanel.add( powerColorIntensity );
        ledPanel.add( powerColorIntensityLabel );

        but_toggleSpot.setActionCommand("toggleSpot");
        but_toggleClean.setActionCommand("toggleClean");
        but_toggleDirt.setActionCommand("toggleDirt");

        but_toggleMax.setActionCommand("toggleMax");
        but_toggleDock.setActionCommand("toggleDock");
        but_toggleCheckRobot.setActionCommand("toggleCheckRobot");

        but_toggleSpot.addActionListener(this);
        but_toggleClean.addActionListener(this);
        but_toggleDirt.addActionListener(this);

        but_toggleMax.addActionListener(this);
        but_toggleDock.addActionListener(this);
        but_toggleCheckRobot.addActionListener(this);
        
       
    } //End make led panel
    /**
     *
     */
    void makeButtonPanel() {
        buttonPanel = new JPanel( new GridLayout( 9,2 ) );
        
        buttonPanel.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createTitledBorder("Commands"), BorderFactory.createEmptyBorder(5,5,5,5)));

        JButton but_powerOn = new JButton("power-on");
        buttonPanel.add( but_powerOn );
        but_powerOn.addActionListener(this);
        
        JButton but_power = new JButton("power-off");
        buttonPanel.add( but_power );
        but_power.addActionListener(this);

        JButton but_wakeup = new JButton("wakeup");
        buttonPanel.add( but_wakeup );
        but_wakeup.addActionListener(this);

        JButton but_sensors = new JButton("sensors");
        buttonPanel.add( but_sensors );
        but_sensors.addActionListener(this);
        
        JButton but_safe = new JButton("safe");
        buttonPanel.add( but_safe );
        but_safe.addActionListener(this);

        JButton but_full = new JButton("full");
        buttonPanel.add( but_full );
        but_full.addActionListener(this);
        
//        JButton but_reset = new JButton("reset");
//        buttonPanel.add( but_reset );
//        but_reset.addActionListener(this);

        JButton but_beeplo = new JButton("beep-lo");
        buttonPanel.add( but_beeplo );
        but_beeplo.addActionListener(this);

        JButton but_beephi = new JButton("beep-hi");
        buttonPanel.add( but_beephi );
        but_beephi.addActionListener(this);

        JButton but_vacon = new JButton("vacuum-on");
        buttonPanel.add( but_vacon );
        but_vacon.addActionListener(this);

        JButton but_vacoff = new JButton("vacuum-off");
        buttonPanel.add( but_vacoff );
        but_vacoff.addActionListener(this);

        JButton but_clean = new JButton("clean");
        buttonPanel.add( but_clean );
        but_clean.addActionListener(this);

        JButton but_spot = new JButton("spot");
        buttonPanel.add( but_spot );
        but_spot.addActionListener(this);

        JButton but_max = new JButton("max");
        buttonPanel.add( but_max );
        but_max.addActionListener(this);

        JButton but_dock = new JButton("dock");
        buttonPanel.add( but_dock );
        but_dock.addActionListener(this);

        JButton but_blinkleds = new JButton("blink-leds");
        buttonPanel.add( but_blinkleds );
        but_blinkleds.addActionListener(this);

        JButton but_OSU = new JButton("OSU");
        buttonPanel.add( but_OSU );
        but_OSU.addActionListener(this);
        
        JButton but_TribbleOn = new JButton("Tribble On");
        buttonPanel.add( but_TribbleOn );
        but_TribbleOn.addActionListener(this);

        JButton but_test = new JButton("test");
        buttonPanel.add( but_test );
        but_test.addActionListener(this);

        
    }

    /**
     *
     */
    void makeDisplayPanel() {
        displayPanel = new JPanel();
        displayPanel.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createTitledBorder("Display"), BorderFactory.createEmptyBorder(1,1,1,1)));
        displayText = new JTextArea(20,40);
        displayText.setLineWrap(false);
        
        JTextArea displayText2 = new JTextArea(25,30);
        displayText2.setLineWrap(true);
        DefaultCaret dc = new DefaultCaret();
        // only works on Java 1.5+
        //dc.setUpdatePolicy( DefaultCaret.ALWAYS_UPDATE );
        displayText.setCaret(dc);
        JScrollPane scrollPane = 
            new JScrollPane(displayText, 
                            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
//        scrollPane.add(displayText2,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
//                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        displayPanel.add( scrollPane );
    }
    
    protected void updateDisplay(String s) {
        displayText.append( s );
        if (!(s.endsWith("\n"))){
        	displayText.append( "\n" );
        }
        displayText.setCaretPosition(displayText.getDocument().getLength());
        
        displayText.getParent().validate();
        displayText.getParent().repaint(100);
//        displayText.getParent().validate();
    }
    protected void updateDisplay(String s,boolean onlyDebug) {
        if (onlyDebug && (roombaCommSerial.debug || roombaCommTCPClient.debug)){
        	updateDisplay(s);
        	System.out.println(s);
        }
    }
    
    protected static void updateDisplay(RoombaComm roombacomm, JTextArea jtextarea, String s,boolean onlyDebug) {
        if (onlyDebug && roombacomm.debug){
        	jtextarea.append(s);
        	jtextarea.setCaretPosition(jtextarea.getDocument().getLength());
        	System.out.println(s);
        }
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path,
                                               String description) {
        // yes, this is supposed to say "RoombaCommTest"
        java.net.URL imgURL = RoombaCommPanel.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    @SuppressWarnings("unchecked")
	protected void playSong(RoombaComm roombacomm, String rtttl){
    	ArrayList notelist = RTTTLParser.parse( rtttl );
        int songsize = notelist.size();
        // if within the size of a roomba song,  make the nsong, then play
        if( songsize <= 16 ) {
        	updateDisplay("creating a song with createSong()", this.debug);
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
        	updateDisplay("playing song in realtime with playNote()\n",this.debug);
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
    protected static void tribbleStart(RoombaComm roombacomm, JTextArea jtextarea, boolean tribbleOn){
    	createTribblePurrSong(roombacomm);
        
    	updateDisplay(roombacomm,jtextarea,"Press return to exit.",roombacomm.debug);
        
        while( tribbleOn ) { 
            
        	purr(roombacomm,jtextarea);
            
            if( Math.random() < 0.1 )
            	bark(roombacomm,jtextarea);
            
            roombacomm.pause(1500 + (int)(Math.random()*500) );           
//            tribbleOn = keyIsPressed();
            roombacomm.updateSensors();
            boolean b = roombacomm.maxButton();
            updateDisplay(roombacomm,jtextarea,"max button is ("+b+")",roombacomm.debug);
            tribbleOn=(!b);
        }
    }
    
    protected static void purr(RoombaComm roombacomm, JTextArea jtextarea) {
        updateDisplay(roombacomm, jtextarea, "purr",roombacomm.debug);
        roombacomm.playSong( 5 );
        for( int i=0; i<5; i++ ) {
            roombacomm.spinLeftAt( 75 );
            roombacomm.pause( 100 );
            roombacomm.spinRightAt( 75 );
            roombacomm.pause( 100 );
            roombacomm.stop();
        }
    }
    
    protected static void createTribblePurrSong(RoombaComm roombacomm) {
        int song[] = 
            { 68,4, 67,4, 66,4, 65,4,
              64,4, 63,4, 62,4, 61,4,
              60,4, 59,4, 60,4, 61,4,
            };
        roombacomm.createSong( 5, song );
    }
    
    protected static void bark(RoombaComm roombacomm, JTextArea jtextarea) {
        updateDisplay(roombacomm, jtextarea, "bark",roombacomm.debug);
        roombacomm.vacuum(true);
        roombacomm.playNote( 50, 5 );
        roombacomm.pause(150);
        roombacomm.vacuum(false);
    }

    protected int getPower_color() {
		return power_color;
	}

	protected void setPower_color(int power_color) {
		if (power_color >=0 && power_color <=255){
			this.power_color = power_color;
		}else{
			this.power_color = 0;
			updateDisplay("invalid power color attempted ("+power_color+")");
		}
	}

	protected int getPower_int() {
		return power_int;
	}

	protected void setPower_int(int power_int) {
		
		if (power_color >=0 && power_color <=255){
			this.power_int = power_int;
		}else{
			this.power_int = 0;
			updateDisplay("invalid power intensity attempted ("+power_color+")");
		}
	}
	protected void setLEDs(RoombaComm roombacomm){
		
		if( !roombacomm.connected() ) return;
		// set the LEDs state for the roombacomm object that was passed in with the values the UI has
		roombaComm.setToggleCheckRobot(this.toggleCheckRobot);
    	roombaComm.setToggleClean(this.toggleClean);
    	roombaComm.setToggleDirt(this.toggleDirt);
    	roombaComm.setToggleDirt(this.toggleDock);
    	roombaComm.setGreenOn(this.greenOn);
    	roombaComm.setToggleMax(this.toggleMax);
    	roombaComm.setPower_color(this.power_color);
    	roombaComm.setPower_int(this.power_int);
    	roombaComm.setRedOn(this.redOn);
    	roombaComm.setToggleSpot(this.toggleSpot);
    	roombaComm.setLEDs(roombaComm);
		roombacomm.setLEDs(roombacomm); // simplified by moving all of the protocal sensitive choices into internal logic for roombacomm
//		if (roombacomm.getProtocol().equalsIgnoreCase("SCI")) {
//		roombacomm.setLEDs(this.greenOn, this.redOn, this.toggleSpot, this.toggleClean, this.toggleMax, this.toggleDirt, 
//				this.power_color, this.power_int);
//		}
//		if (roombacomm.getProtocol().equalsIgnoreCase("OI")) {
//			roombacomm.setLEDsOI(this.toggleCheckRobot,this.toggleSpot,this.toggleDock,this.toggleDirt,
//					this.power_color, this.power_int);
//		}
		return;
	}
	protected void setChgGreenLED(RoombaComm roombacomm, boolean green){
		this.greenOn=green;
		updateDisplay("setChgGreenLED", this.debug);
		this.setLEDs(roombacomm);
	}
	protected void setChgRedLED(RoombaComm roombacomm, boolean red){
		this.redOn=red;
		updateDisplay("setChgRedLED", this.debug);
		this.setLEDs(roombacomm);
	}
	protected void setChgSpotLED(RoombaComm roombacomm, boolean spot){
		updateDisplay("setChgSpotLED value("+spot+")", this.debug);		
		roombacomm.setChgSpotLED(roombacomm, spot);
		this.toggleSpot =roombacomm.isToggleSpot();
	}
	protected void setChgCleanLED(RoombaComm roombacomm, boolean clean){
		updateDisplay("setChgCleanLED value("+clean+")",this.debug);
		roombacomm.setChgCleanLED(roombacomm,clean);
		this.toggleClean = roombacomm.isToggleClean();
	}
	protected void setChgMaxLED(RoombaComm roombacomm, boolean max){
//		updateDisplay("setChgMaxLED",this.debug);
//		this.toggleMax=max;
//		this.setLEDs(roombacomm);
		updateDisplay("setChgMaxLED value("+max+")", this.debug);		
		roombacomm.setChgMaxLED(roombacomm,max);
		this.toggleMax = roombacomm.isToggleMax();		
	}
	protected void setChgDirtLED(RoombaComm roombacomm, boolean dirt){
		updateDisplay("setChgDirtLED value("+dirt+")", this.debug);		
		roombacomm.setChgDirtLED(roombacomm, dirt);
		this.toggleDirt =roombacomm.isToggleDirt();
	}
	public void setToggleCheckRobot(RoombaComm roombacomm, boolean CheckRobot) {
		updateDisplay("setChgCheckRobotLED value("+CheckRobot+")", this.debug);		
		roombacomm.setChgCheckRobotLED(roombacomm, CheckRobot);
		this.toggleCheckRobot =roombacomm.isToggleCheckRobot();
	}

	public void setToggleDock(RoombaComm roombacomm, boolean dock) {
		updateDisplay("setChgDockLED value("+dock+")", this.debug);		
		roombacomm.setChgDockLED(roombacomm, dock);
		this.toggleDock = roombacomm.isToggleDock();
	}
	protected void setChgPowerColorLED(RoombaComm roombacomm, int power_color){
		updateDisplay("setChgPowerColorLED value("+power_color+")",this.debug);
		//this.power_color=power_color;
		roombacomm.setChgPowerColorLED(roombacomm, power_color);
//		this.power_color
//      TODO: Keep track of power color
	}
	protected void setChgPowerIntensityLED(RoombaComm roombacomm, int power_intensity){
		updateDisplay("setChgPowerIntensityLED value("+power_intensity+")",this.debug);
		roombacomm.setChgPowerIntensityLED(roombacomm, power_intensity);
//      TODO: Keep track of Power Intensity
	}
    public boolean getToggleCheckRobot() {
		return toggleCheckRobot;
	}

    public boolean getToggleDock() {
		return toggleDock;
	}

//	protected void setStatusOffButton(boolean set){
//		statusOffButton
//	}

	
}
