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

package com.hackingroomba.roombacomm;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultCaret;

/**
 * A Panel containing controls for testing RoombaComm. Normally put inside of a
 * frame, for example see RoombaCommTest
 * 
 * SVN id value is $Id$
 */
public class RoombaCommPanel extends JPanel implements ActionListener,
		ChangeListener {

	private static final long serialVersionUID = 1L;

	{
		// Set Look & Feel
		try {
			javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
  
	JPanel ctrlPanel, selectPanel, buttonPanel, displayPanel, ledPanel;
	private JTextPane jTextPane1;
	private JPanel jPanelPower;
	private JPanel jPanelVacuum;
	private JPanel jPanelSensors;
	private JPanel jPanelTestPrograms;
	JComboBox portChoices;
	JComboBox protocolChoices;
	JCheckBox handshakeButton;
	JTextArea displayText;
	JButton connectButton;
	JButton netButton;
	JSlider speedSlider, powerColorSlider, powerColorIntensity;
	private boolean debug = false;
	boolean tribbleOn = false;
	// default values for flags for LEDs
	boolean redOn = false;
	boolean greenOn = false;
	boolean toggleSpot = false;
	boolean toggleClean = false;
	boolean toggleMax = false;
	boolean toggleDirt = false;
	boolean toggleCheckRobot = false;
	boolean toggleDock = false;
	private JPanel jPanelSounds;
	private JPanel jPanelModes;
	private JPanel jPanel3;
	private JPanel jPanel2;
	private JLabel jLabelCOMM;
	private JTextField jTextFieldPort;
	private JLabel jLabelPort;
	private JPanel jPanel1;
	private JTextField jTextFieldHost;
	private JLabel jLabelHost;
	private JPanel jPanelConfigSerial;
	private JPanel jPanelConfigNet;
	private JTabbedPane jTabbedPanelConfig;
	private JButton jButton4;
	private JButton jButton3;
	private JButton jButton2;
	private JButton jButton1;
	int power_color = 0;
	int power_int = 0;
	RoombaCommSerial roombaCommSerial;
	RoombaComm roombaComm;
	RoombaCommTCPClient roombaCommTCPClient;
	SimpleDateFormat formatter = new SimpleDateFormat(
			"EEE, dd-MMM-yyyy HH:mm:ss");
	public RoombaCommPanel() {
		this(false);
	}

	public RoombaCommPanel(boolean debug) {
//		super(new BorderLayout());
		super();
//		this.setContentPane(contentPane)
		System.out.println("RoombaCommPanel-start");
		System.out.println(" debug is ("+debug+")");
		initialize();
		roombaCommSerial = new RoombaCommSerial();
		roombaCommTCPClient = new RoombaCommTCPClient();
		this.debug = debug;
		roombaCommSerial.debug = debug;
		roombaCommTCPClient.debug = debug;
		System.out.println("RoombaCommPanel-makePanels-start");
		makePanels();
		System.out.println("RoombaCommPanel-makePanels-end");
		System.out.println("RoombaCommPanel-end");
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		Dimension defaultSize = new Dimension(800,600); 
        this.addComponentListener(new java.awt.event.ComponentListener() {
        	public void componentResized(java.awt.event.ComponentEvent e) {
        		System.out.println("147-componentResized("+e.getComponent().getWidth()+","+e.getComponent().getHeight()+")"); // TODO Auto-generated Event stub componentResized()
        	}
        	public void componentMoved(java.awt.event.ComponentEvent e) {
        	}
        	public void componentShown(java.awt.event.ComponentEvent e) {
        	}
        	public void componentHidden(java.awt.event.ComponentEvent e) {
        	}
        });
        System.out.println("initialize : setting size to (" +defaultSize.getWidth()+","+defaultSize.getHeight()+")");
        GridBagLayout thisLayout = new GridBagLayout();
        this.setSize(defaultSize);
        thisLayout.rowWeights = new double[] {0.0, 0.0, 0.1};
        thisLayout.rowHeights = new int[] {137, 304, 5};
        thisLayout.columnWeights = new double[] {0.0, 0.0};
        thisLayout.columnWidths = new int[] {538, 206};
        this.setLayout(thisLayout);
        this.setPreferredSize(new java.awt.Dimension(800, 600)); //638
//        {
//        	buttonPanel = new JPanel();
//        	this.add(buttonPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
//        	GridBagLayout buttonPanelLayout = new GridBagLayout();
//        	buttonPanelLayout.rowWeights = new double[] {0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1};
//        	buttonPanelLayout.rowHeights = new int[] {7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7};
//        	buttonPanelLayout.columnWeights = new double[] {0.0, 0.0};
//        	buttonPanelLayout.columnWidths = new int[] {10, 10};
//        	buttonPanel.setLayout(buttonPanelLayout);
//        	buttonPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Commands"),BorderFactory.createEmptyBorder(5,5,5,5)));
//        	buttonPanel.setMinimumSize(new java.awt.Dimension(0,10));
//        	buttonPanel.setSize(200, 411);
//        	buttonPanel.setPreferredSize(new java.awt.Dimension(242, 249));
//        }
	}

	/**
	 * Set to 'false' to hide the "h/w handshake" button, which seems to be only
	 * needed on Windows
	 */
	public void setShowHardwareHandhake(boolean b) {
		handshakeButton.setVisible(b);
	}

	/** */
	public boolean connect() {
		System.out.println("connect-start");
		String portname = (String) portChoices.getSelectedItem();
		// roombacomm.debug=true;
		roombaCommSerial.setWaitForDSR(handshakeButton.isSelected());
		int i = protocolChoices.getSelectedIndex();
		roombaCommSerial.setProtocol((i == 0) ? "SCI" : "OI");
		if (portname == null){
			// I guess we do not yet have ports so refresh the list
			setCommPorts();
		}
		updateDisplay("connecting to " + portname + "\n");
		connectButton.setText("connecting");
		if (portname != null){
			if (!roombaCommSerial.connect(portname)) {
				updateDisplay("Couldn't connect to " + portname + "\n");
				connectButton.setText("  connect  ");
			// 	roombacomm.debug=false;
				System.out.println("connect-end (could not connect)");
				return false;
			}else{
				updateDisplay("connected to " + portname + "\n");
			}
		}else{
			updateDisplay("you must first select a COMM port to use before you can attempt to connect");
			jTabbedPanelConfig.setSelectedIndex(1);
			return false;
		}
		updateDisplay("Roomba startup\n");

		roombaCommSerial.startup();
		roombaCommSerial.control();
		roombaCommSerial.playNote(72, 10); // C , test note
		roombaCommSerial.pause(200);

		connectButton.setText("disconnect");
		connectButton.setActionCommand("disconnect");
		roombaComm = roombaCommSerial;
		// roombacomm.debug=true;
		updateDisplay("Checking for Roomba... ");
			if (roombaCommSerial.updateSensors()) {
				updateDisplay("Roomba found!\n");
				System.out.println("connect-end");
				return true;
			}else{
				updateDisplay("No Roomba. :(  Is it turned on?\n");
				System.out.println("connect-end");
				return true ;
			}
		}

	public boolean connectNet(String portname) {
		System.out.println("connectNet called");
		int i = protocolChoices.getSelectedIndex();
		if (roombaCommTCPClient == null){
			roombaCommTCPClient = new RoombaCommTCPClient();
			roombaCommTCPClient.setConnected(false);
		}
		roombaCommTCPClient.setProtocol((i == 0) ? "SCI" : "OI");

		if (!roombaCommTCPClient.connect(portname)) {
			updateDisplay("Couldn't connect to " + portname);
			return false;
		}

		updateDisplay("Roomba startup on port " + portname);
		roombaCommTCPClient.startup();
		roombaCommTCPClient.control();
		// roombacomm.setSensorsAutoUpdate(true);
		roombaCommTCPClient.pause(30);

		updateDisplay("Checking for Roomba... \n");
		// roombaCommTCPClient.setDebug(true);
		if (roombaCommTCPClient.updateSensors()) {
			updateDisplay("Roomba found!\n");
			updateDisplay(roombaCommTCPClient.getSensorsAsString());
		} else {
			updateDisplay("No Roomba. :(  Is it turned on?\n");
		}
		updateDisplay("buffer is(" + roombaCommTCPClient.getBuffer() + ")",
				this.debug);
		// roombaCommTCPClient.setDebug(false);
		// roombacomm.updateSensors();
		updateDisplay("connected (" + roombaCommTCPClient.connected() + ")\n");
		if (roombaCommTCPClient.connected()) {
			updateDisplay("Playing some notes\n");
			roombaCommTCPClient.playNote(72, 10); // C
			roombaCommTCPClient.pause(200);
			roombaCommTCPClient.playNote(79, 10); // G
			roombaCommTCPClient.pause(200);
			roombaCommTCPClient.playNote(76, 10); // E
			roombaCommTCPClient.pause(200);
			netButton.setText("disconnect-net");
			netButton.setActionCommand("disconnect-net");
			roombaComm = roombaCommTCPClient; // set the pointer so that the
												// action stuff can work against
												// any class
		}

		// roombaCommSerial.setWaitForDSR(handshakeButton.isSelected());
		//        
		//        
		// connectButton.setText("connecting");
		// if( ! roombaCommSerial.connect( portname ) ) {
		// updateDisplay("Couldn't connect to "+portname+"\n");
		// connectButton.setText("  connect  ");
		// //roombacomm.debug=false;
		// return false;
		// }
		// updateDisplay("Roomba startup\n");
		//
		// roombaCommSerial.startup();
		// roombaCommSerial.control();
		// roombaCommSerial.playNote( 72, 10 ); // C , test note
		// roombaCommSerial.pause( 200 );
		//
		// connectButton.setText("disconnect");
		// connectButton.setActionCommand("disconnect");
		// //roombacomm.debug=true;
		// updateDisplay("Checking for Roomba... ");
		// if( roombaCommSerial.updateSensors() )
		// updateDisplay("Roomba found!\n");
		// else
		// updateDisplay("No Roomba. :(  Is it turned on?\n");

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
	 * notenums 32-127: notenum == corresponding note played thru beeper
	 * velocity == duration in number of 1/64s of a second (e.g. 64==1second)
	 * notenum 24: notenum == main vacuum velocity == non-zero turns on, zero
	 * turns off notenum 25: blink LEDs, velcoity is color of Power notenum 28 &
	 * 29: spin left & spin right, velocity is speed
	 * 
	 */
	public void playMidiNote(int notenum, int velocity) {
		updateDisplay("play note: " + notenum + "," + velocity + "\n");
		if (!roombaCommSerial.connected())
			return;

		if (notenum >= 31) { // G and above
			if (velocity == 0)
				return;
			if (velocity < 4)
				velocity = 4; // has problems at lower durations
			else
				velocity = velocity / 2;
			roombaCommSerial.playNote(notenum, velocity);
		} else if (notenum == 24) { // C
			roombaCommSerial.vacuum(!(velocity == 0));
		} else if (notenum == 25) { // C#
			boolean lon = (velocity != 0);
			int inten = (lon) ? 255 : 128; // either full bright or half bright
			roombaCommSerial.setLEDs(lon, lon, lon, lon, lon, lon,
					velocity * 2, inten);
		} else if (notenum == 28) { // E
			if (velocity != 0)
				roombaCommSerial.spinLeftAt(velocity * 2);
			else
				roombaCommSerial.stop();
		} else if (notenum == 29) { // F
			if (velocity != 0)
				roombaCommSerial.spinRightAt(velocity * 2);
			else
				roombaCommSerial.stop();
		}
	}

	/** implement actionlistener */
	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		updateDisplay(formatter.format(new Date()) + ": action (" + action
				+ ") happened\n", this.debug);
		// roombacomm.setLEDs(false, false, false, false, false, false, 0, 0);
		if ("comboBoxChanged".equals(action)) {
//			String portname = (String) portChoices.getSelectedItem();
			int i = protocolChoices.getSelectedIndex();
			if (roombaComm != null) {
				roombaComm.setProtocol((i == 0) ? "SCI" : "OI");
			} else {
				updateDisplay(
						formatter.format(new Date())
								+ ": null roombaComm object found in actionPerformed\n",
						this.debug);
			}
			return;
		}
		// updateDisplay(action+"\n");
		if ("net".equals(action)) {
			if (jTextFieldHost != null && jTextFieldHost.getText() != null && jTextFieldPort != null && jTextFieldPort.getText() != null){
				if(connectNet(jTextFieldHost.getText()+":"+jTextFieldPort.getText())){
					// TODO: find a way to hide/disable the other connect tab until the session is disconnected
//					getJTabbedPanelConfig().getComponentAt(1).setVisible(false); // hide the Serial tab while we are connected with a Net port
				}else{
					// TODO: reenable/show the other tab(s)
//					getJTabbedPanelConfig().getComponentAt(0).setVisible(true); // make sure the net tab is not hidden when we fail to connect
				}
				
			}else{
				updateDisplay("connect (via net) pressed with missing values:");
				if (jTextFieldHost != null && jTextFieldHost.getText() != null){
					updateDisplay(" host :"+jTextFieldHost.getText());
				}else{
					updateDisplay(" host : MISSING VALUE");
				}
				if (jTextFieldPort != null && jTextFieldPort.getText() != null){
					updateDisplay(" port :"+jTextFieldPort.getText());
				}else{
					updateDisplay(" port : MISSING VALUE");
				}
			}
			return;
		} else if ("disconnect-net".equals(action)) {
			disconnectNet();
			// TODO: reenable/show the other tab(s)
//			getJTabbedPanelConfig().getComponentAt(1).setVisible(true); // show the serial tab while we disconnect from the net
			return;
		}
		if ("connect".equals(action)) {
			if (connect()){
				// TODO: find a way to hide/disable the other connect tab until the session is disconnected				
			}else{
				// TODO: reenable/show the other tab(s)
//				getJTabbedPanelConfig().getComponentAt(0).setEnabled(true); // make sure the net tab is not hidden when we fail to connect
//				getJTabbedPanelConfig().repaint(100);
			}
			return;
		} else if ("disconnect".equals(action)) {
			disconnect();
			// TODO: reenable/show the other tab(s)
//			getJTabbedPanelConfig().getComponentAt(0).setEnabled(true); // show the net tab while we disconnect from the serial
			return;
		}
		// stop right here if we're not connected
		if (roombaComm == null || !roombaComm.connected()) {
			updateDisplay("not connected!\n");
			return;
		}

		if ("stop".equals(action)) {
			roombaComm.stop();
		} else if ("forward".equals(action)) {
			// updateDisplay("Speed is("+roombaComm.getSpeed()+")\n");
			roombaComm.goForward();
		} else if ("backward".equals(action)) {
			// updateDisplay("Speed is("+roombaComm.getSpeed()+")\n");
			roombaComm.goBackward();
		} else if ("spinleft".equals(action)) {
			// updateDisplay("Speed is("+roombaComm.getSpeed()+")\n");
			roombaComm.spinLeft();
		} else if ("spinright".equals(action)) {
			// updateDisplay("Speed is("+roombaComm.getSpeed()+")\n");
			roombaComm.spinRight();
		} else if ("turnleft".equals(action)) {
			roombaComm.turnLeft();
		} else if ("turnright".equals(action)) {
			roombaComm.turnRight();
		} else if ("max".equals(action)) {
			roombaComm.max();
		} else if ("dock".equals(action)) {
			roombaComm.dock();
		} else if ("test".equals(action)) {
			LogoA.square(roombaComm, 300);
			/*
			 * updateDisplay("Playing some notes\n"); roombacomm.playNote( 72,
			 * 10 ); // C roombacomm.pause( 200 ); roombacomm.playNote( 79, 10
			 * ); // G roombacomm.pause( 200 ); roombacomm.playNote( 76, 10 );
			 * // E roombacomm.pause( 200 );
			 * 
			 * updateDisplay("Spinning left, then right\n");
			 * roombacomm.spinLeft(); roombacomm.pause(1000);
			 * roombacomm.spinRight(); roombacomm.pause(1000);
			 * roombacomm.stop();
			 * 
			 * updateDisplay("Going forward, then backward\n");
			 * roombacomm.goForward(); roombacomm.pause(1000);
			 * roombacomm.goBackward(); roombacomm.pause(1000);
			 * roombacomm.stop();
			 */
		} else if ("OSU".equals(action)) {
			updateDisplay("Going to play OSU\n");

			roombaComm.stop();
			roombaComm.pause(500);
			// RTTTLPlay rp = new RTTTLPlay();
			// /dev/cu.KeySerial1
			// 'tron:d=4,o=5,b=200:8f6,8c6,8g,e,8p,8f6,8c6,8g,8f6,8c6,8g,e,8p,8f6,8c6,8g,e.,2d'
			// String[]
			// osuSong={roombacomm.getPortname(),"OSU:d=4,o=5,b=125:a,g,a#,a,8g#,a,8g#,2a,8p,8f,8g,8g#,a,8g#,a,8g#,a,g,f,a,g,8a,g,d,8f,8p,8f,8p,8f,8p,8f,8p,c6,a,g,f,8a#,a,8g,f,p,c6,a,g,a,8a#,a,8a#,c6,p,2d6,d,8c6,a#,g,f,8f,8f,8g,a#,8g,a#,a,2a#"};
			playSong(
					roombaComm,
					"OSU:d=4,o=5,b=125:a,g,a#,a,8g#,a,8g#,2a,8p,8f,8g,8g#,a,8g#,a,8g#,a,g,f,a,g,8a,g,d,8f,8p,8f,8p,8f,8p,8f,8p,c6,a,g,f,8a#,a,8g,f,p,c6,a,g,a,8a#,a,8a#,c6,p,2d6,d,8c6,a#,g,f,8f,8f,8g,a#,8g,a#,a,2a#");

			// playSong(roombacomm,
			// "Baa Baa Black Sheep:d=4,o=5,b=125:c,c,g,g,8a,8b,8c6,8a,g,p,f,f,e,e,d,d,c");
			roombaComm.stop();
		} else if ("Tribble On".equals(action)) {
			tribbleOn = true;
			tribbleStart(roombaComm, displayText, tribbleOn);
		} else if ("reset".equals(action)) {
			roombaComm.stop();
			roombaComm.startup();
			roombaComm.control();
		} else if ("passive".equals(action)) {
			//passive / start command
			roombaComm.start();
		} else if ("safe".equals(action)) {
			roombaComm.safe();
		} else if ("full".equals(action)) {
			roombaComm.full();
		} else if ("power-off".equals(action)) {
			roombaComm.powerOff();
		} else if ("power-on".equals(action)) {
			roombaComm.powerOn();
		} else if ("wakeup".equals(action)) {
			roombaComm.wakeup();
		} else if ("beep-lo".equals(action)) {
			roombaComm.playNote(50, 32); // C1
			roombaComm.pause(200);
		} else if ("beep-hi".equals(action)) {
			roombaComm.playNote(90, 32); // C7
			roombaComm.pause(200);
		} else if ("clean".equals(action)) {
			roombaComm.clean();
		} else if ("spot".equals(action)) {
			roombaComm.spot();
		} else if ("vacuum-on".equals(action)) {
			roombaComm.vacuum(true);
		} else if ("vacuum-off".equals(action)) {
			roombaComm.vacuum(false);
		} else if ("blink-leds".equals(action)) {
			roombaComm.setLEDs(true, true, true, true, true, true, 255, 255);
			roombaComm.pause(300);
			roombaComm
					.setLEDs(false, false, false, false, false, false, 0, 128);
		} else if ("sensors".equals(action)) {
			if (roombaComm.updateSensors())
				updateDisplay(roombaComm.sensorsAsString() + "\n");
			else
				updateDisplay("couldn't read Roomba. Is it connected?\n");
		} else if ("chargedata".equals(action)) {
			if (roombaComm.updateSensors())
				updateDisplay("*****\n" + roombaComm.chargeDataAsString()
						+ "\n");
			else
				updateDisplay("couldn't read Roomba. Is it connected?\n");
		} else if ("toggleGreen".equals(action)) {
			setChgGreenLED(roombaComm, !greenOn);
		} else if ("toggleRed".equals(action)) {
			setChgRedLED(roombaComm, !redOn);
		} else if ("toggleSpot".equals(action)) {
			setChgSpotLED(roombaComm, !toggleSpot);
		} else if ("toggleClean".equals(action)) {
			setChgCleanLED(roombaComm, !toggleClean);
		} else if ("toggleMax".equals(action)) {
			setChgMaxLED(roombaComm, !toggleMax);
		} else if ("toggleDirt".equals(action)) {
			setChgDirtLED(roombaComm, !toggleDirt);
		} else if ("toggleCheckRobot".equals(action)) {
			setToggleCheckRobot(roombaComm, !toggleCheckRobot);
		} else if ("toggleDock".equals(action)) {
			setToggleDock(roombaComm, !toggleDock);
		}

	}

	/** implement ChangeListener, for the slider */
	public void stateChanged(ChangeEvent e) {
		// System.err.println("stateChanged:"+e);
		JSlider src = (JSlider) e.getSource();
		if (!src.getValueIsAdjusting()) {
			int speed = (int) src.getValue();
			speed = (speed < 1) ? 1 : speed; // don't allow zero speed
			updateDisplay("setting speed = " + speed + "\n");
			roombaComm.setSpeed(speed);
		}
	}

	/**
	 *
	 */
	void makePanels() {
		System.out.println("makePanels-start");
		makeSelectPanel();
		this.add(selectPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 2, 2));
		makeCtrlPanel();
		makeLedPanel();
		makeButtonPanel();
		makeDisplayPanel();
		this.add(displayPanel, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 2, 2));
		this.add(getJPanel1(), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 2, 2));
		this.add(getJPanel2(), new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 2, 2));
		this.add(getJPanel3(), new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		// pack(); //setVisible(true);
		updateDisplay("RoombaComm, version " + RoombaComm.VERSION + "\n");
		System.out.println("makePanels-finish");
	}
		void makeButtonPanel(){
//			jPanel1.add(buttonPanel, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		}
		void makeSelectPanel() {
			selectPanel = new JPanel();
			GridBagLayout selectPanelLayout = new GridBagLayout();
			selectPanelLayout.rowWeights = new double[] {0.0, 0.0};
			selectPanelLayout.rowHeights = new int[] {53, 58};
			selectPanelLayout.columnWeights = new double[] {0.0, 0.0};
			selectPanelLayout.columnWidths = new int[] {181, 88};
			selectPanel.setLayout(selectPanelLayout);

			// Create a combo box with protocols
			String[] protocols = { "Roomba 1xx-4xx (SCI)", "Roomba 5xx (OI)" };
			protocolChoices = new JComboBox(protocols);
			String p = roombaCommSerial.getProtocol();
			protocolChoices.setSelectedIndex(p.equals("SCI") ? 0 : 1);
	
//			// Create a combo box with choices.
//	//		String[] ports = roombaCommSerial.listPorts();
//			// for now short cutting looking for serial ports to speed up start/stop of the UI
//			String[] ports = {"a","b"};
//			System.out.println("found "+ports.length+" serialports");
//	//		portChoices = new JComboBox(ports);
			portChoices = new JComboBox();
//			for (int i = 0; i < ports.length; i++) {
//				String s = ports[i];
//				System.out.println("adding ["+i+"] as "+s);
//				portChoices.addItem(ports[i]);
//				if (s.equals(roombaCommSerial.getPortname())) {
//					System.out.println(" setting port as selected due to "+roombaCommSerial.getPortname());
//					portChoices.setSelectedItem(s);
//				}
//			}
			
			handshakeButton = new JCheckBox("<html>h/w<br>handshake</html>");
			// net button
			
			// Add a border around the select panel.
			selectPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
					.createTitledBorder("Select Roomba Type & Port"), BorderFactory
					.createEmptyBorder(1, 1, 1, 1)));
			System.out.println("577-setPreferredSize");
			selectPanel.setPreferredSize(new java.awt.Dimension(535, 128));
			System.out.println("577-setPreferredSize-done");
			selectPanel.add(protocolChoices, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
			protocolChoices.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0), "Protocal", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION));
			protocolChoices.setToolTipText("Set the protocal based on the roomba hardware version");
//			selectPanel.setJMenuBar(getJMenuBar1());
			selectPanel.add(handshakeButton, new GridBagConstraints(0, 2, 1, 1, 0.1, 0.1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			handshakeButton.setPreferredSize(new java.awt.Dimension(148, 40));
			handshakeButton.setText("h/w handshake");
			handshakeButton.setToolTipText("check if you want to use the hardware handshake setting");
			selectPanel.add(getJTabbedPanelConfig(), new GridBagConstraints(1, 1, 1, 2, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
			selectPanel.add(getJTextPane1(), new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 2, 2));

			// Listen to events from the combo box.
			protocolChoices.addActionListener(this);
		}

	/** 
     * 
     */
	void makeCtrlPanel() {

	}

	void makeLedPanel() {

		// if (roombaCommSerial.getProtocol().equalsIgnoreCase("SCI")) {
//		String off = "None";
//		String green = "Green";
//		String red = "Red";
//		String both = "Orange";

		// Group the radio buttons.
//		ButtonGroup group = new ButtonGroup();
		// }

		// powerColorIntensity.addChangeListener(this);
		// powerColorIntensity.addChangeListener(new ChangeListener() {setPow
		// });

	} // End make led panel

	/**
     *
     */
	void makeDisplayPanel() {
		displayPanel = new JPanel();
		GridBagLayout displayPanelLayout = new GridBagLayout();
		displayPanelLayout.rowWeights = new double[] {0.1};
		displayPanelLayout.rowHeights = new int[] {7};
		displayPanelLayout.columnWeights = new double[] {0.1};
		displayPanelLayout.columnWidths = new int[] {7};
		displayPanel.setLayout(displayPanelLayout);
		displayPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder("Display"), BorderFactory
				.createEmptyBorder(1, 1, 1, 1)));
		displayPanel.setPreferredSize(new java.awt.Dimension(412, 45));
		{
			displayText = new JTextArea(10, 75);
			displayPanel.add(displayText, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			displayText.setLineWrap(false);
			DefaultCaret caret = (DefaultCaret)displayText.getCaret();
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
			displayText.setCaret(caret);
			displayText.setPreferredSize(new java.awt.Dimension(825, 27));
			displayText.setSize(795, 160);
			
		}
		
		JTextArea displayText2 = new JTextArea(25, 30);
		displayText2.setLineWrap(true);
		DefaultCaret dc = new DefaultCaret();
		dc.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		// only works on Java 1.5+
		// dc.setUpdatePolicy( DefaultCaret.ALWAYS_UPDATE );
		JScrollPane scrollPane = new JScrollPane(displayText,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar();
		// scrollPane.add(displayText2,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		// JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		displayPanel.add(scrollPane, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		scrollPane.setSize(795, 163);
		scrollPane.setPreferredSize(new java.awt.Dimension(766, 81));
	}

	protected void updateDisplay(String s) {
		System.out.println("updateDisplay(string): start");
		displayText.append(s);
		if (s != null && !(s.endsWith("\n"))) {
			displayText.append("\n");
		}
//		displayText.setCaretPosition(displayText.getDocument().getLength());
		System.out.println("updateDisplay(string): reposition to "+displayText.getDocument().getLength());
		// TODO: need to find a way to auto scroll to the end of the text so the user can just follow along.
		// currently on my PC. I am not even able to scroll down to the end of the text. sigh...
		displayText.getParent().validate();
		displayText.getParent().repaint(100);
		displayText.setText(displayText.getText() );
		    
////		for (int i = 0; i < 20; i++) {        
////			displayText.append("This is text " + i + "\n");
//			scrollPaneVbar.setValue(scrollPaneVbar.getMaximum());
//			scrollPaneVbar.paint(scrollPaneVbar.getGraphics());
//			displayText.scrollRectToVisible(displayText.getVisibleRect());
//			if (displayText != null && displayText.getGraphics() != null){
//				displayText.paint(displayText.getGraphics());
//				scrollPaneVbar.getParent().getParent().repaint(100);
////				displayText.repaint(100);
//				System.out.println("displayText calling paint on displayText "); // this should never happen
//			}else{
//				if (displayText != null){
//					System.out.println("displayText is null"); // this should never happen
//				}
//				if (displayText.getGraphics() != null){
//					System.out.println("displayText.getGraphics is null"); // this might happen?
//				}
//			}
//			try {            
//				Thread.sleep(250);        
//				} catch (InterruptedException ex) {            
////					Logger.getLogger(ScrollTextView.class.getName()).log(Level.SEVERE, null, ex);
//					System.out.println("InterruptedException while trying to update displayText");
//				}    
////		}
//		// displayText.getParent().validate();
		System.out.println("updateDisplay(string): end");
	}

	protected void updateDisplay(String s, boolean onlyDebug) {
		if (onlyDebug && (roombaCommSerial.debug || roombaCommTCPClient.debug)) {
			updateDisplay(s);
			System.out.println(s);
		}
	}

	protected static void updateDisplay(RoombaComm roombacomm,
			JTextArea jtextarea, String s, boolean onlyDebug) {
		if (onlyDebug && roombacomm.debug) {
			jtextarea.append(s);
			jtextarea.setCaretPosition(jtextarea.getDocument().getLength());
			jtextarea.getParent().validate();
			jtextarea.getParent().repaint(100);
			System.out.println(s);
		}
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path, String description) {
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
	protected void playSong(RoombaComm roombacomm, String rtttl) {
		ArrayList notelist = RTTTLParser.parse(rtttl);
		int songsize = notelist.size();
		// if within the size of a roomba song, make the nsong, then play
		if (songsize <= 16) {
			updateDisplay("creating a song with createSong()", this.debug);
			int notearray[] = new int[songsize * 2];
			int j = 0;
			for (int i = 0; i < songsize; i++) {
				Note note = (Note) notelist.get(i);
				int sec64ths = note.duration * 64 / 1000;
				notearray[j++] = note.notenum;
				notearray[j++] = sec64ths;
			}
			roombacomm.createSong(1, notearray);
			roombacomm.playSong(1);
		}
		// otherwise, try to play it in realtime
		else {
			updateDisplay("playing song in realtime with playNote()\n",
					this.debug);
			int fudge = 20;
			for (int i = 0; i < songsize; i++) {
				Note note = (Note) notelist.get(i);
				int duration = note.duration;
				int sec64ths = duration * 64 / 1000;
				if (sec64ths < 5)
					sec64ths = 5;
				if (note.notenum != 0)
					roombacomm.playNote(note.notenum, sec64ths);
				roombacomm.pause(duration + fudge);
			}
		}
	}

	protected static void tribbleStart(RoombaComm roombacomm,
			JTextArea jtextarea, boolean tribbleOn) {
		createTribblePurrSong(roombacomm);

		updateDisplay(roombacomm, jtextarea, "Press return to exit.",
				roombacomm.debug);

		while (tribbleOn) {

			purr(roombacomm, jtextarea);

			if (Math.random() < 0.1)
				bark(roombacomm, jtextarea);

			roombacomm.pause(1500 + (int) (Math.random() * 500));
			// tribbleOn = keyIsPressed();
			roombacomm.updateSensors();
			boolean b = roombacomm.maxButton();
			updateDisplay(roombacomm, jtextarea, "max button is (" + b + ")",
					roombacomm.debug);
			tribbleOn = (!b);
		}
	}

	protected static void purr(RoombaComm roombacomm, JTextArea jtextarea) {
		updateDisplay(roombacomm, jtextarea, "purr", roombacomm.debug);
		roombacomm.playSong(5);
		for (int i = 0; i < 5; i++) {
			roombacomm.spinLeftAt(75);
			roombacomm.pause(100);
			roombacomm.spinRightAt(75);
			roombacomm.pause(100);
			roombacomm.stop();
		}
	}

	protected static void createTribblePurrSong(RoombaComm roombacomm) {
		int song[] = { 68, 4, 67, 4, 66, 4, 65, 4, 64, 4, 63, 4, 62, 4, 61, 4,
				60, 4, 59, 4, 60, 4, 61, 4, };
		roombacomm.createSong(5, song);
	}

	protected static void bark(RoombaComm roombacomm, JTextArea jtextarea) {
		updateDisplay(roombacomm, jtextarea, "bark", roombacomm.debug);
		roombacomm.vacuum(true);
		roombacomm.playNote(50, 5);
		roombacomm.pause(150);
		roombacomm.vacuum(false);
	}

	protected int getPower_color() {
		return power_color;
	}

	protected void setPower_color(int power_color) {
		if (power_color >= 0 && power_color <= 255) {
			this.power_color = power_color;
		} else {
			this.power_color = 0;
			updateDisplay("invalid power color attempted (" + power_color + ")");
		}
	}

	protected int getPower_int() {
		return power_int;
	}

	protected void setPower_int(int power_int) {

		if (power_color >= 0 && power_color <= 255) {
			this.power_int = power_int;
		} else {
			this.power_int = 0;
			updateDisplay("invalid power intensity attempted (" + power_color
					+ ")");
		}
	}

	protected void setLEDs(RoombaComm roombacomm) {
		if (!roombacomm.connected())
			return;
		if (roombacomm.getProtocol().equalsIgnoreCase("SCI")) {
			roombacomm.setLEDs(this.greenOn, this.redOn, this.toggleSpot,
					this.toggleClean, this.toggleMax, this.toggleDirt,
					this.power_color, this.power_int);
		}
		if (roombacomm.getProtocol().equalsIgnoreCase("OI")) {
			roombacomm.setLEDsOI(this.toggleCheckRobot, this.toggleSpot,
					this.toggleDock, this.toggleDirt, this.power_color,
					this.power_int);
		}
		return;
	}

	protected void setChgGreenLED(RoombaComm roombacomm, boolean green) {
		this.greenOn = green;
		updateDisplay("setChgGreenLED", this.debug);
		this.setLEDs(roombacomm);
	}

	protected void setChgRedLED(RoombaComm roombacomm, boolean red) {
		this.redOn = red;
		updateDisplay("setChgRedLED", this.debug);
		this.setLEDs(roombacomm);
	}

	protected void setChgSpotLED(RoombaComm roombacomm, boolean spot) {
		updateDisplay("setChgSpotLED value(" + spot + ")", this.debug);
		roombacomm.setChgSpotLED(roombacomm, spot);
		this.toggleSpot = roombacomm.isToggleSpot();
	}

	protected void setChgCleanLED(RoombaComm roombacomm, boolean clean) {
		updateDisplay("setChgCleanLED value(" + clean + ")", this.debug);
		roombacomm.setChgCleanLED(roombacomm, clean);
		this.toggleClean = roombacomm.isToggleClean();
	}

	protected void setChgMaxLED(RoombaComm roombacomm, boolean max) {
		// updateDisplay("setChgMaxLED",this.debug);
		// this.toggleMax=max;
		// this.setLEDs(roombacomm);
		updateDisplay("setChgMaxLED value(" + max + ")", this.debug);
		roombacomm.setChgMaxLED(roombacomm, max);
		this.toggleMax = roombacomm.isToggleMax();
	}

	protected void setChgDirtLED(RoombaComm roombacomm, boolean dirt) {
		updateDisplay("setChgDirtLED value(" + dirt + ")", this.debug);
		roombacomm.setChgDirtLED(roombacomm, dirt);
		this.toggleDirt = roombacomm.isToggleDirt();
	}

	public void setToggleCheckRobot(RoombaComm roombacomm, boolean CheckRobot) {
		updateDisplay("setChgCheckRobotLED value(" + CheckRobot + ")",
				this.debug);
		roombacomm.setChgCheckRobotLED(roombacomm, CheckRobot);
		this.toggleCheckRobot = roombacomm.isToggleCheckRobot();
	}

	public void setToggleDock(RoombaComm roombacomm, boolean dock) {
		updateDisplay("setChgDockLED value(" + dock + ")", this.debug);
		roombacomm.setChgDockLED(roombacomm, dock);
		this.toggleDock = roombacomm.isToggleDock();
	}

	protected void setChgPowerColorLED(RoombaComm roombacomm, int power_color) {
		updateDisplay("setChgPowerColorLED value(" + power_color + ")",
				this.debug);
		// this.power_color=power_color;
		roombacomm.setChgPowerColorLED(roombacomm, power_color);
		// this.power_color
		// TODO: Keep track of power color
	}

	protected void setChgPowerIntensityLED(RoombaComm roombacomm,
			int power_intensity) {
		updateDisplay("setChgPowerIntensityLED value(" + power_intensity + ")",
				this.debug);
		roombacomm.setChgPowerIntensityLED(roombacomm, power_intensity);
		// TODO: Keep track of Power Intensity
	}

	public boolean getToggleCheckRobot() {
		return toggleCheckRobot;
	}

	public boolean getToggleDock() {
		return toggleDock;
	}
	
//	private JPanel getJPanelMiddle() {
//		if(jPanelMiddle == null) {
//			jPanelMiddle = new JPanel();
//		}
//		return jPanelMiddle;
//	}
	
	private JTabbedPane getJTabbedPanelConfig() {
		if(jTabbedPanelConfig == null) {
			jTabbedPanelConfig = new JTabbedPane();
			jTabbedPanelConfig.setPreferredSize(new java.awt.Dimension(139, 28));
			jTabbedPanelConfig.addTab("Net", null, getJPanelConfigNet(), "set the TCP network settings here");
			jTabbedPanelConfig.addTab("Serial", null, getJPanelConfigSerial(), "set the serial settings here");
		}
		jTabbedPanelConfig.setMinimumSize(new Dimension(200,100));
		jTabbedPanelConfig.setPreferredSize(new java.awt.Dimension(323, 100));
		jTabbedPanelConfig.setToolTipText("Use one of the two connection methods to communicate with the Roomba");
		// Register a change listener
		jTabbedPanelConfig.addChangeListener(new ChangeListener() {
	        // This method is called whenever the selected tab changes
	        public void stateChanged(ChangeEvent evt) {
	        	System.out.println("jTabbedPanelConfig - state changed");
	            JTabbedPane pane = (JTabbedPane)evt.getSource();
	            // Get current tab
	            int sel = pane.getSelectedIndex();
	            System.out.println("jTabbedPanelConfig - tab "+sel+" selected");
	            System.out.println("jTabbedPanelConfig - roombaCommSerial.isConnected() "+roombaCommSerial.isConnected());
	            System.out.println("jTabbedPanelConfig - roombaCommTCPClient.isConnected() "+roombaCommTCPClient.isConnected());
	            if (roombaCommTCPClient.isConnected() && roombaCommSerial.isConnected()){
	            	System.out.println("***** I have no idea why both should ever be connected at the same time... THIS IS STRANGE *****");
	            }
	            if (sel == 1){
	            	setCommPorts();
	            	if (roombaCommTCPClient.isConnected() && !roombaCommSerial.isConnected()){
	            		pane.setSelectedIndex(0);
	            		System.out.println("active net connection found setting focus to the correct tab");
	            	}
	            }
	            if (sel == 0){
	            	if (!roombaCommTCPClient.isConnected() && roombaCommSerial.isConnected()){
	            		pane.setSelectedIndex(1);
	            		System.out.println("active Serial connection found setting focus to the correct tab");
	            	}
	            }
	        }
	    });

		return jTabbedPanelConfig;
	}
	
	private JPanel getJPanelConfigNet() {
		if(jPanelConfigNet == null) {
			jPanelConfigNet = new JPanel();
			GridBagLayout jPanelConfigNetLayout = new GridBagLayout();
			jPanelConfigNetLayout.rowWeights = new double[] {0.0, 0.1};
			jPanelConfigNetLayout.rowHeights = new int[] {26, 7};
			jPanelConfigNetLayout.columnWeights = new double[] {0.1, 0.1, 0.1};
			jPanelConfigNetLayout.columnWidths = new int[] {7, 7, 7};
			jPanelConfigNet.setLayout(jPanelConfigNetLayout);
			jPanelConfigNet.setPreferredSize(new java.awt.Dimension(318, 72));
			jPanelConfigNet.add(getJLabelHost(), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
			jPanelConfigNet.add(getJTextFieldHost(), new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
			jPanelConfigNet.add(getJLabelPort(), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			jPanelConfigNet.add(getJTextFieldPort(), new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
			{
				netButton = new JButton();
				jPanelConfigNet.add(netButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				netButton.setText("connect");
				netButton.setActionCommand("net");
				netButton.addActionListener(this);
			}
		}
		return jPanelConfigNet;
	}
	
	private JPanel getJPanelConfigSerial() {
		if(jPanelConfigSerial == null) {
			jPanelConfigSerial = new JPanel();
			GridBagLayout jPanelConfigSerialLayout = new GridBagLayout();
			jPanelConfigSerial.setPreferredSize(new java.awt.Dimension(318, 72));
			jPanelConfigSerialLayout.rowWeights = new double[] {0.1};
			jPanelConfigSerialLayout.rowHeights = new int[] {7};
			jPanelConfigSerialLayout.columnWeights = new double[] {0.1, 0.1, 0.1};
			jPanelConfigSerialLayout.columnWidths = new int[] {7, 7, 7};
			jPanelConfigSerial.setLayout(jPanelConfigSerialLayout);
			jPanelConfigSerial.addFocusListener(new FocusAdapter() {
				public void focusGained(FocusEvent evt) {
					jPanelConfigSerialFocusGained(evt);
				}
			});
			{
				portChoices = new JComboBox();
				jPanelConfigSerial.add(portChoices, new GridBagConstraints(1, -1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
				portChoices.validate();
				portChoices.repaint();
				portChoices.addActionListener(this);
			}
			{
				connectButton = new JButton();
				jPanelConfigSerial.add(connectButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				jPanelConfigSerial.add(getJLabelCOMM(), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
				connectButton.setText("  connect  ");
				connectButton.setActionCommand("connect");
				connectButton.addActionListener(this);
			}
		}
		return jPanelConfigSerial;
	}
	
	private JLabel getJLabelHost() {
		if(jLabelHost == null) {
			jLabelHost = new JLabel();
			jLabelHost.setLayout(null);
			jLabelHost.setText("Host");
			jLabelHost.setPreferredSize(new java.awt.Dimension(39, 17));
			jLabelHost.setLabelFor(getJTextFieldHost());
			jLabelHost.setToolTipText("Set the hostname/IP address");
		}
		return jLabelHost;
	}
	
	private JTextField getJTextFieldHost() {
		if(jTextFieldHost == null) {
			jTextFieldHost = new JTextField();
			jTextFieldHost.setText("192.168.15.240");
			jTextFieldHost.setToolTipText("Set the hostname/IP address");
		}
		return jTextFieldHost;
	}
	
	private JPanel getJPanel1() {
		if(jPanel1 == null) {
			jPanel1 = new JPanel();
			GridBagLayout jPanel1Layout = new GridBagLayout();
			jPanel1Layout.rowWeights = new double[] {0.1};
			jPanel1Layout.rowHeights = new int[] {7};
			jPanel1Layout.columnWeights = new double[] {0.1, 0.1};
			jPanel1Layout.columnWidths = new int[] {7, 7};
			jPanel1.setLayout(jPanel1Layout);
			jPanel1.setPreferredSize(new java.awt.Dimension(800, 320));
			{
				ledPanel = new JPanel();
				GridBagLayout ledPanelLayout = new GridBagLayout();
				ledPanel.setLayout(ledPanelLayout);
				jPanel1.add(ledPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				{
					JPanel ledPanel1 = new JPanel(new GridLayout(3, 3));
					ledPanel.add(ledPanel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
					ButtonGroup group = new ButtonGroup();
					String off="None";
					String green="Green";
					String red="Red";
					String both="Orange";
					{
						JRadioButton statusOffButton = new JRadioButton(off);
						ledPanel1.add(statusOffButton);
						statusOffButton.setMnemonic(KeyEvent.VK_N);
						statusOffButton.setActionCommand("StatusLED-" + off);
						statusOffButton.setSelected(true);
						group.add(statusOffButton);
						statusOffButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								updateDisplay("setting Power Color = " + e.getActionCommand()
										+ "\n");
								setChgGreenLED(roombaCommSerial, false);
								setChgRedLED(roombaCommSerial, false);
							}
						});
					}
					{
						JRadioButton statusGreenButton = new JRadioButton(green);
						ledPanel1.add(statusGreenButton);
						statusGreenButton.setMnemonic(KeyEvent.VK_G);
						statusGreenButton.setActionCommand("StatusLED-" + green);
						group.add(statusGreenButton);
						statusGreenButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								updateDisplay("setting Power Color = " + e.getActionCommand()
										+ "\n");
								setChgGreenLED(roombaCommSerial, true);
								setChgRedLED(roombaCommSerial, false);
							}
						});
					}
					{
						JRadioButton statusRedButton = new JRadioButton(red);
						ledPanel1.add(statusRedButton);
						statusRedButton.setMnemonic(KeyEvent.VK_R);
						statusRedButton.setActionCommand("StatusLED-" + red);
						group.add(statusRedButton);
						statusRedButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								updateDisplay("setting Power Color = " + e.getActionCommand()
										+ "\n");
								setChgGreenLED(roombaCommSerial, false);
								setChgRedLED(roombaCommSerial, true);
							}
						});
					}
					{
						JRadioButton statusBothButton = new JRadioButton(both);
						ledPanel1.add(statusBothButton);
						statusBothButton.setMnemonic(KeyEvent.VK_O);
						statusBothButton.setActionCommand("StatusLED-" + both);
						group.add(statusBothButton);
						statusBothButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								updateDisplay("setting Power Color = " + e.getActionCommand()
										+ "\n");
								setChgGreenLED(roombaCommSerial, true);
								setChgRedLED(roombaCommSerial, true);
							}
						});
					}
					{
						JButton but_toggleSpot = new JButton();
						ledPanel1.add(but_toggleSpot);
						but_toggleSpot.setActionCommand("toggleSpot");
						but_toggleSpot.setIcon(new ImageIcon(getClass().getClassLoader().getResource("com/hackingroomba/roombacomm/images/but_spotOn.png")));
						but_toggleSpot.setPreferredSize(new java.awt.Dimension(46, 50));
						but_toggleSpot.setSize(40, 40);
						but_toggleSpot.addActionListener(this);
					}
					{
						JButton but_toggleClean = new JButton();
						ledPanel1.add(but_toggleClean);
						but_toggleClean.setActionCommand("toggleClean");
						but_toggleClean.setIcon(new ImageIcon(getClass().getClassLoader().getResource("com/hackingroomba/roombacomm/images/but_cleanOn.png")));
						but_toggleClean.setPreferredSize(new java.awt.Dimension(40, 40));
						but_toggleClean.setSize(40, 40);
						but_toggleClean.addActionListener(this);
					}
					{
						JButton but_toggleDirt = new JButton();
						ledPanel1.add(but_toggleDirt);
						but_toggleDirt.setActionCommand("toggleDirt");
						but_toggleDirt.setIcon(new ImageIcon(getClass().getClassLoader().getResource("com/hackingroomba/roombacomm/images/but_dirtOn.png")));
						but_toggleDirt.setPreferredSize(new java.awt.Dimension(40, 40));
						but_toggleDirt.setSize(40, 40);
						but_toggleDirt.addActionListener(this);
					}
					{
						JButton but_toggleMax = new JButton();
						ledPanel1.add(but_toggleMax);
						but_toggleMax.setActionCommand("toggleMax");
						but_toggleMax.setIcon(new ImageIcon(getClass().getClassLoader().getResource("com/hackingroomba/roombacomm/images/but_maxOn.png")));
						but_toggleMax.setSize(40, 40);
						but_toggleMax.setPreferredSize(new java.awt.Dimension(40, 40));
						but_toggleMax.addActionListener(this);
					}
					{
						JButton but_toggleDock = new JButton();
						ledPanel1.add(but_toggleDock);
						but_toggleDock.setActionCommand("toggleDock");
						but_toggleDock.setIcon(new ImageIcon(getClass().getClassLoader().getResource("com/hackingroomba/roombacomm/images/but_dockOn.png")));
						but_toggleDock.setPreferredSize(new java.awt.Dimension(40, 40));
						but_toggleDock.setSize(40, 40);
						but_toggleDock.addActionListener(this);
					}
					{
						JButton but_toggleCheckRobot = new JButton();
						ledPanel1.add(but_toggleCheckRobot);
						but_toggleCheckRobot.setActionCommand("toggleCheckRobot");
						but_toggleCheckRobot.setIcon(new ImageIcon(getClass().getClassLoader().getResource("com/hackingroomba/roombacomm/images/but_checkrobotOn.png")));
						but_toggleCheckRobot.setPreferredSize(new java.awt.Dimension(40, 40));
						but_toggleCheckRobot.setSize(40, 40);
						but_toggleCheckRobot.addActionListener(this);
					}
				}
				{
					powerColorSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 100);
					ledPanel.add(powerColorSlider, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 2, 2));
					powerColorSlider.setPaintTicks(true);
					powerColorSlider.setMajorTickSpacing(100);
					powerColorSlider.setMinorTickSpacing(25);
					powerColorSlider.setPaintLabels(true);
					powerColorSlider.addChangeListener(new ChangeListener() {
						public void stateChanged(ChangeEvent e) {
							JSlider src = (JSlider) e.getSource();
							if (!src.getValueIsAdjusting()) {
								setPower_color((int) src.getValue());
								updateDisplay("setting Power Color = " + getPower_color()
										+ "\n");
								setLEDs(roombaCommSerial);
							}
						}
					});
				}
				{
					JLabel powerColorLabel = new JLabel("PowerColor", JLabel.CENTER);
					ledPanel.add(powerColorLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 2, 2));
				}
				{
					powerColorIntensity = new JSlider(JSlider.HORIZONTAL, 0, 255, 100);
					ledPanel.add(powerColorIntensity, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 2, 2));
					powerColorIntensity.setPaintTicks(true);
					powerColorIntensity.setMajorTickSpacing(100);
					powerColorIntensity.setMinorTickSpacing(25);
					powerColorIntensity.setPaintLabels(true);
					powerColorIntensity.addChangeListener(new ChangeListener() {
						public void stateChanged(ChangeEvent e) {
							// System.err.println("stateChanged:"+e);
							JSlider src = (JSlider) e.getSource();
							if (!src.getValueIsAdjusting()) {
								setPower_int((int) src.getValue());
								updateDisplay("setting Power Color Intensity = "
										+ getPower_int() + "\n");
								setLEDs(roombaCommSerial);
							}
						}
					});
				}
				{
					JLabel powerColorIntensityLabel = new JLabel("PowerColorIntensity",
							JLabel.CENTER);
					ledPanel.add(powerColorIntensityLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 2, 2));
				}
				ledPanelLayout.rowWeights = new double[] {0.1, 0.1, 0.1, 0.1, 0.1};
				ledPanelLayout.rowHeights = new int[] {7, 7, 7, 7, 7};
				ledPanelLayout.columnWeights = new double[] {0.1};
				ledPanelLayout.columnWidths = new int[] {7};
				ledPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("LEDs"),BorderFactory.createEmptyBorder(5,5,5,5)));
				ledPanel.setPreferredSize(new java.awt.Dimension(288, 313));
			}
			
			{
				ctrlPanel = new JPanel();
				jPanel1.add(ctrlPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
				{
					JPanel ctrlPanel1 = new JPanel();
					ctrlPanel.add(ctrlPanel1);
					{
						JButton but_turnleft = new JButton();
						ctrlPanel1.add(but_turnleft);
						but_turnleft.setActionCommand("turnleft");
						but_turnleft.setIcon(new ImageIcon(getClass().getClassLoader().getResource("com/hackingroomba/roombacomm/images/but_turnleft.png")));
						but_turnleft.addActionListener(this);
					}
					{
						JButton but_forward = new JButton();
						ctrlPanel1.add(but_forward);
						but_forward.setActionCommand("forward");
						but_forward.setIcon(new ImageIcon(getClass().getClassLoader().getResource("com/hackingroomba/roombacomm/images/but_forward.png")));
						but_forward.addActionListener(this);
					}
					{
						JButton but_turnright = new JButton();
						ctrlPanel1.add(but_turnright);
						but_turnright.setActionCommand("turnright");
						but_turnright.setIcon(new ImageIcon(getClass().getClassLoader().getResource("com/hackingroomba/roombacomm/images/but_turnright.png")));
						but_turnright.addActionListener(this);
					}
					{
						JButton but_spinleft = new JButton();
						ctrlPanel1.add(but_spinleft);
						but_spinleft.setActionCommand("spinleft");
						but_spinleft.setIcon(new ImageIcon(getClass().getClassLoader().getResource("com/hackingroomba/roombacomm/images/but_spinleft.png")));
						but_spinleft.addActionListener(this);
					}
					{
						JButton but_stop = new JButton();
						ctrlPanel1.add(but_stop);
						but_stop.setActionCommand("stop");
						but_stop.setIcon(new ImageIcon(getClass().getClassLoader().getResource("com/hackingroomba/roombacomm/images/but_stop.png")));
						but_stop.addActionListener(this);
					}
					{
						JButton but_spinright = new JButton();
						ctrlPanel1.add(but_spinright);
						but_spinright.setActionCommand("spinright");
						but_spinright.setIcon(new ImageIcon(getClass().getClassLoader().getResource("com/hackingroomba/roombacomm/images/but_spinright.png")));
						but_spinright.addActionListener(this);
					}
					{
						ctrlPanel1.add(new JLabel());
					}
					{
						JButton but_backward = new JButton();
						ctrlPanel1.add(but_backward);
						but_backward.setActionCommand("backward");
						but_backward.setIcon(new ImageIcon(getClass().getClassLoader().getResource("com/hackingroomba/roombacomm/images/but_backward.png")));
						but_backward.setPreferredSize(new java.awt.Dimension(55, 78));
						but_backward.addActionListener(this);
					}
					{
						ctrlPanel1.add(new JLabel());
					}
					ctrlPanel1.setLayout(new GridLayout(3, 3));
					ctrlPanel1.setPreferredSize(new java.awt.Dimension(194, 199));
					ctrlPanel1.setSize(194, 199);
//					ctrlPanel1.setTabTitle("");
				}
				{
					speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 500, 200);
					ctrlPanel.add(speedSlider);
					speedSlider.setPaintTicks(true);
					speedSlider.setMajorTickSpacing(100);
					speedSlider.setMinorTickSpacing(25);
					speedSlider.setPaintLabels(true);
					speedSlider.addChangeListener(this);
				}
				{
					JLabel sliderLabel = new JLabel();
					ctrlPanel.add(sliderLabel);
					sliderLabel.setText("speed (mm/s)");
					sliderLabel.setAlignmentX(JLabel.CENTER);
				}
				ctrlPanel.setLayout(new BoxLayout(ctrlPanel, BoxLayout.Y_AXIS));
				ctrlPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Movement"),BorderFactory.createEmptyBorder(5,5,5,5)));
			}
		}
		return jPanel1;
	}
	
	private JLabel getJLabelPort() {
		if(jLabelPort == null) {
			jLabelPort = new JLabel();
			jLabelPort.setText("Port");
			jLabelPort.setPreferredSize(new java.awt.Dimension(39, 33));
			jLabelPort.setLabelFor(getJTextFieldPort());
			jLabelPort.setToolTipText("Set the TCP port");
		}
		return jLabelPort;
	}
	
	private JTextField getJTextFieldPort() {
		if(jTextFieldPort == null) {
			jTextFieldPort = new JTextField();
			jTextFieldPort.setToolTipText("Set the TCP port");
			jTextFieldPort.setText("5001");
		}
		return jTextFieldPort;
	}
	
	private void jPanelConfigSerialFocusGained(FocusEvent evt) {
		System.out.println("jPanelConfigSerial.focusGained, event="+evt);
		//TODO add your code for jPanelConfigSerial.focusGained
		// make sure we have a roombaCommSerial object
		setCommPorts();
	}

	/**
	 * 
	 */
	private void setCommPorts() {
		System.out.println("setCommPorts-start");
		if (roombaCommSerial == null){
			System.out.println("setCommPorts-roombaCommSerial is null");
			roombaCommSerial = new RoombaCommSerial(this.debug);	
		}
		if (portChoices != null) {
			System.out.println("setCommPorts-portChoices object is not null");
			// if the list of ports is empty then try to fill it
			if (portChoices.getItemCount() ==0){
			// fill in the comm ports (combo box) with choices.
				System.out.println("setCommPorts-getting list of ports");
				String[] ports = roombaCommSerial.listPorts();
				// for now short cutting looking for serial ports to speed up start/stop of the UI
	//				String[] ports = {"a","b"};
				System.out.println("setCommPorts-found "+ports.length+" serialports");
				for (int i = 0; i < ports.length; i++) {
					String s = ports[i];
					System.out.println("setCommPorts-adding ["+i+"] as "+s);
					portChoices.addItem(ports[i]);
					if (s.equals(roombaCommSerial.getPortname())) {
						System.out.println("setCommPorts- setting port as selected due to "+roombaCommSerial.getPortname());
						portChoices.setSelectedItem(s);
					}
				}
				portChoices.validate();
				portChoices.repaint();
			}
		}
		System.out.println("setCommPorts-start");
	}
	
	private JLabel getJLabelCOMM() {
		if(jLabelCOMM == null) {
			jLabelCOMM = new JLabel();
			jLabelCOMM.setText("COM");
		}
		return jLabelCOMM;
	}
	
	private JPanel getJPanel2() {
		if(jPanel2 == null) {
			jPanel2 = new JPanel();
			GridBagLayout jPanel2Layout = new GridBagLayout();
			jPanel2Layout.rowWeights = new double[] {0.1, 0.1, 0.1, 0.1, 0.1, 0.1};
			jPanel2Layout.rowHeights = new int[] {7, 7, 7, 7, 7, 7};
			jPanel2Layout.columnWeights = new double[] {0.0};
			jPanel2Layout.columnWidths = new int[] {150};
			jPanel2.setLayout(jPanel2Layout);
			jPanel2.setBorder(BorderFactory.createTitledBorder(null, "Commands", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION));
			jPanel2.setPreferredSize(new java.awt.Dimension(233, 505));
			jPanel2.add(getJPanelModes(), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
			jPanel2.add(getJPanelSounds(), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
			jPanel2.add(getJPanelTestPrograms(), new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
			jPanel2.add(getJPanelVacuum(), new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
			jPanel2.add(getJPanelPower(), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
			jPanel2.add(getJPanel4(), new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
		}
		return jPanel2;
	}
	
	private JPanel getJPanel3() {
		if(jPanel3 == null) {
			jPanel3 = new JPanel();
			jPanel3.setVisible(false);
			GridBagLayout jPanel3Layout = new GridBagLayout();
			jPanel3Layout.rowWeights = new double[] {0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1};
			jPanel3Layout.rowHeights = new int[] {7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7};
			jPanel3Layout.columnWeights = new double[] {0.1};
			jPanel3Layout.columnWidths = new int[] {7};
			jPanel3.setLayout(jPanel3Layout);
			{
				JButton but_nyi = new JButton();
				jPanel3.add(but_nyi, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				but_nyi.setText("nyi");
				but_nyi.setVisible(false);
			}
			{
				JButton but_nyi2 = new JButton();
				jPanel3.add(but_nyi2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				but_nyi2.setText("nyi2");
				but_nyi2.setVisible(false);
			}
			{
				JButton but_nyi3 = new JButton();
				jPanel3.add(but_nyi3, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				but_nyi3.setText("nyi3");
				but_nyi3.setVisible(false);
			}
			{
				JButton but_nyi4 = new JButton();
				jPanel3.add(but_nyi4, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				but_nyi4.setText("nyi4");
				but_nyi4.setVisible(false);
			}
			{
				JButton but_nyi5 = new JButton();
				jPanel3.add(but_nyi5, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				but_nyi5.setText("nyi5");
				but_nyi5.setVisible(false);
			}
			{
				JButton but_nyi6 = new JButton();
				jPanel3.add(but_nyi6, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				but_nyi6.setText("nyi6");
				but_nyi6.setVisible(false);
			}
			{
				JButton but_nyi7 = new JButton();
				jPanel3.add(but_nyi7, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				but_nyi7.setText("nyi7");
				but_nyi7.setVisible(false);
			}
			{
				jButton2 = new JButton();
				jPanel3.add(jButton2, new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				jButton2.setText("nyi5");
				jButton2.setVisible(false);
			}
			{
				jButton1 = new JButton();
				jPanel3.add(jButton1, new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				jButton1.setText("nyi6");
				jButton1.setVisible(false);
			}
			{
				jButton3 = new JButton();
				jPanel3.add(jButton3, new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				jButton3.setText("nyi3");
				jButton3.setVisible(false);
			}
			{
				jButton4 = new JButton();
				jPanel3.add(jButton4, new GridBagConstraints(0, 12, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				jButton4.setText("nyi2");
				jButton4.setVisible(false);
			}
		}
		return jPanel3;
	}
	
	private JPanel getJPanelModes() {
		if(jPanelModes == null) {
			jPanelModes = new JPanel();
			GridBagLayout jPanelModesLayout = new GridBagLayout();
			jPanelModesLayout.rowWeights = new double[] {0.1, 0.1, 0.1, 0.1, 0.1};
			jPanelModesLayout.rowHeights = new int[] {7, 7, 7, 7, 7};
			jPanelModesLayout.columnWeights = new double[] {0.1, 0.1};
			jPanelModesLayout.columnWidths = new int[] {95, 95};
			jPanelModes.setLayout(jPanelModesLayout);
			jPanelModes.setBorder(BorderFactory.createTitledBorder("Modes"));
			{
				JButton but_spot = new JButton();
				jPanelModes.add(but_spot, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
				but_spot.setText("spot");
				but_spot.setMargin(new java.awt.Insets(2, 2, 2, 2));
				but_spot.addActionListener(this);
			}
			{
				JButton but_full = new JButton();
				jPanelModes.add(but_full, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
				but_full.setText("full");
				but_full.setMargin(new java.awt.Insets(2, 2, 2, 2));
				but_full.addActionListener(this);
			}
			{
				JButton but_safe = new JButton();
				jPanelModes.add(but_safe, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
				but_safe.setText("safe");
				but_safe.setMargin(new java.awt.Insets(2, 2, 2, 2));
				but_safe.addActionListener(this);
			}
			{
				JButton but_dock = new JButton();
				jPanelModes.add(but_dock, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
				but_dock.setText("dock");
				but_dock.setMargin(new java.awt.Insets(2, 2, 2, 2));
				but_dock.addActionListener(this);		
			}
			{
				JButton but_max = new JButton();
				jPanelModes.add(but_max, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
				but_max.setText("max");
				but_max.setMargin(new java.awt.Insets(2, 2, 2, 2));
				but_max.addActionListener(this);
			}
			{
				JButton but_clean = new JButton();
				jPanelModes.add(but_clean, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
				but_clean.setText("clean");
				but_clean.setMargin(new java.awt.Insets(2, 2, 2, 2));
				but_clean.addActionListener(this);
			}
			{
				JButton but_wakeup = new JButton();
				jPanelModes.add(but_wakeup, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
				but_wakeup.setText("wakeup");
				but_wakeup.setMargin(new java.awt.Insets(2, 2, 2, 2));
				but_wakeup.addActionListener(this);
			}
			{
				JButton but_reset = new JButton();
				jPanelModes.add(but_reset, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
				but_reset.setText("reset");
				but_reset.setMargin(new java.awt.Insets(2, 2, 2, 2));
				but_reset.addActionListener(this);
			}
			{
				JButton but_passive = new JButton();
				jPanelModes.add(but_passive, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
				but_passive.setText("passive");
				but_passive.setMargin(new java.awt.Insets(2, 2, 2, 2));
				but_passive.addActionListener(this);		
			}
		}
		return jPanelModes;
	}
	
	private JPanel getJPanelSounds() {
		if(jPanelSounds == null) {
			jPanelSounds = new JPanel();
			GridBagLayout jPanelSoundsLayout = new GridBagLayout();
			jPanelSoundsLayout.rowWeights = new double[] {0.1, 0.1};
			jPanelSoundsLayout.rowHeights = new int[] {7, 7};
			jPanelSoundsLayout.columnWeights = new double[] {0.1, 0.1};
			jPanelSoundsLayout.columnWidths = new int[] {95, 95};
			jPanelSounds.setLayout(jPanelSoundsLayout);
			jPanelSounds.setBorder(BorderFactory.createTitledBorder("Sounds"));
			{
				JButton but_OSU = new JButton();
				jPanelSounds.add(but_OSU, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
				but_OSU.setText("OSU");
				but_OSU.setMargin(new java.awt.Insets(2, 2, 2, 2));
				but_OSU.addActionListener(this);		
			}
			{
				JButton but_beeplo = new JButton();
				jPanelSounds.add(but_beeplo, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
				but_beeplo.setText("beep-lo");
				but_beeplo.setMargin(new java.awt.Insets(2, 2, 2, 2));
				but_beeplo.addActionListener(this);
			}
			{
				JButton but_beephi = new JButton();
				jPanelSounds.add(but_beephi, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
				but_beephi.setText("beep-hi");
				but_beephi.setMargin(new java.awt.Insets(2, 2, 2, 2));
				but_beephi.addActionListener(this);
			}
		}
		return jPanelSounds;
	}
	
	private JPanel getJPanelTestPrograms() {
		if(jPanelTestPrograms == null) {
			jPanelTestPrograms = new JPanel();
			GridBagLayout jPanelTestProgramsLayout = new GridBagLayout();
			jPanelTestProgramsLayout.rowWeights = new double[] {0.1};
			jPanelTestProgramsLayout.rowHeights = new int[] {7};
			jPanelTestProgramsLayout.columnWeights = new double[] {0.1, 0.1};
			jPanelTestProgramsLayout.columnWidths = new int[] {95, 95};
			jPanelTestPrograms.setLayout(jPanelTestProgramsLayout);
			jPanelTestPrograms.setBorder(BorderFactory.createTitledBorder("Test Programs"));
			{
				JButton but_TribbleOn = new JButton();
				jPanelTestPrograms.add(but_TribbleOn, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
				but_TribbleOn.setText("Tribble On");
				but_TribbleOn.setPreferredSize(new java.awt.Dimension(93, 26));
				but_TribbleOn.setMargin(new java.awt.Insets(2, 2, 2, 2));
				but_TribbleOn.setSize(93, 26);
				but_TribbleOn.addActionListener(this);
			}
			{
				JButton but_test = new JButton();
				jPanelTestPrograms.add(but_test, new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));				
				but_test.setText("LogoA.square");
				but_test.setPreferredSize(new java.awt.Dimension(93, 26));
				but_test.setMargin(new java.awt.Insets(2, 2, 2, 2));
				but_test.setSize(93, 26);
				but_test.addActionListener(this);
			}
		}
		return jPanelTestPrograms;
	}
	
	private JPanel getJPanel4() {
		if(jPanelSensors == null) {
			jPanelSensors = new JPanel();
			GridBagLayout jPanelSensorsLayout = new GridBagLayout();
			jPanelSensorsLayout.rowWeights = new double[] {0.1};
			jPanelSensorsLayout.rowHeights = new int[] {7};
			jPanelSensorsLayout.columnWeights = new double[] {0.1, 0.1};
			jPanelSensorsLayout.columnWidths = new int[] {95, 95};
			jPanelSensors.setLayout(jPanelSensorsLayout);
			jPanelSensors.setBorder(BorderFactory.createTitledBorder(null, "Sensors", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION));
			{
				JButton but_chargedata = new JButton();
				jPanelSensors.add(but_chargedata, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
				but_chargedata.setText("chargedata");
				but_chargedata.setMargin(new java.awt.Insets(2, 2, 2, 2));
				but_chargedata.setPreferredSize(new java.awt.Dimension(93, 26));
				but_chargedata.setSize(93, 26);
				but_chargedata.addActionListener(this);
			}
			{
				JButton but_sensors = new JButton();
				jPanelSensors.add(but_sensors, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
				but_sensors.setText("sensors");
				but_sensors.setMargin(new java.awt.Insets(2, 2, 2, 2));
				but_sensors.setPreferredSize(new java.awt.Dimension(93, 26));
				but_sensors.setSize(93, 26);
				but_sensors.addActionListener(this);
			}
		}
		return jPanelSensors;
	}
	
	private JPanel getJPanelVacuum() {
		if(jPanelVacuum == null) {
			jPanelVacuum = new JPanel();
			GridBagLayout jPanelVacuumLayout = new GridBagLayout();
			jPanelVacuumLayout.rowWeights = new double[] {0.1};
			jPanelVacuumLayout.rowHeights = new int[] {7};
			jPanelVacuumLayout.columnWeights = new double[] {0.1, 0.1};
			jPanelVacuumLayout.columnWidths = new int[] {95, 95};
			jPanelVacuum.setLayout(jPanelVacuumLayout);
			jPanelVacuum.setBorder(BorderFactory.createTitledBorder(BorderFactory.createTitledBorder(""), "Vacuum", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION));
			{
				JButton but_vacon = new JButton();
				jPanelVacuum.add(but_vacon, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
				but_vacon.setText("vacuum-on");
				but_vacon.setSize(93, 26);
				but_vacon.setPreferredSize(new java.awt.Dimension(93, 26));
				but_vacon.setMargin(new java.awt.Insets(2, 2, 2, 2));
				but_vacon.addActionListener(this);
			}
			{
				JButton but_vacoff = new JButton();
				but_vacoff.setLayout(null);
				jPanelVacuum.add(but_vacoff, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
				but_vacoff.setText("vacuum-off");
				but_vacoff.setMargin(new java.awt.Insets(2, 2, 2, 2));
				but_vacoff.setPreferredSize(new java.awt.Dimension(93, 26));
				but_vacoff.setSize(93, 26);
				but_vacoff.addActionListener(this);
			}
		}
		return jPanelVacuum;
	}
	
	private JPanel getJPanelPower() {
		if(jPanelPower == null) {
			jPanelPower = new JPanel();
			GridBagLayout jPanelPowerLayout = new GridBagLayout();
			jPanelPower.setBorder(BorderFactory.createTitledBorder(null, "Power", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION));
			jPanelPowerLayout.rowWeights = new double[] {0.1};
			jPanelPowerLayout.rowHeights = new int[] {7};
			jPanelPowerLayout.columnWeights = new double[] {0.1, 0.1};
			jPanelPowerLayout.columnWidths = new int[] {95, 95};
			jPanelPower.setLayout(jPanelPowerLayout);
			{
				JButton but_powerOn = new JButton();
				jPanelPower.add(but_powerOn, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
				but_powerOn.setText("power-on");
				but_powerOn.setMargin(new java.awt.Insets(2, 2, 2, 2));
				but_powerOn.addActionListener(this);
			}
			{
				JButton but_power = new JButton();
				jPanelPower.add(but_power, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
				but_power.setText("power-off");
				but_power.setMargin(new java.awt.Insets(2, 2, 2, 2));
				but_power.addActionListener(this);
			}
		}
		return jPanelPower;
	}
	
	private JTextPane getJTextPane1() {
		if(jTextPane1 == null) {
			jTextPane1 = new JTextPane();
			jTextPane1.setText("Set the Protocal, then connect via a network or Serial connection.\nThen you can use the Commands, LED's, and/or Movement controls to operate your Roomba.");
			jTextPane1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			jTextPane1.setOpaque(false);
		}
		return jTextPane1;
	}

}
