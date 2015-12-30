/*
 * RoombaCommFrame - 
 * 
 * Based heavily on RoombaCommPanel, but very altered.
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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.JFrame;
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
 * A JFrame containing controls for testing RoombaComm.
 * 
 * It is hoped that this UI will implement ways to 
 * manually test RoombaComm and maybe even support more automated 
 * testing in the future.
 * 
 * SVN id value is $Id$
 */
public class RoombaCommFrame extends JFrame implements ActionListener,
		ChangeListener {
	
	/** The led panel. */
	JPanel ctrlPanel, selectPanel, buttonPanel, displayPanel, ledPanel;
	
	/** The j panel4. */
	private JPanel jPanel4;
	
	/** The led panel shared. */
	private JPanel ledPanelShared;
	
	/** The led panel oi only. */
	private JPanel ledPanelOIOnly;
	
	/** The j text pane1. */
	private JTextPane jTextPane1;
	
	/** The j panel power. */
	private JPanel jPanelPower;
	
	/** The j panel vacuum. */
	private JPanel jPanelVacuum;
	
	/** The but_play rttl. */
	private JButton but_playRTTL;
	
	/** The j panel sensors. */
	private JPanel jPanelSensors;
	
	/** The j panel test programs. */
	private JPanel jPanelTestPrograms;
	
	/** The port choices. */
	JComboBox portChoices;
	
	/** The protocol choices. */
	JComboBox protocolChoices;
	
	/** The handshake button. */
	JCheckBox handshakeButton;
	
	/** The display text. */
	JTextArea displayText;
	
	/** The connect button. */
	JButton connectButton;
	
	/** The net button. */
	JButton netButton;
	
	/** The power color intensity. */
	JSlider speedSlider, powerColorSlider, powerColorIntensity;
	
	/** The debug. */
	private boolean debug = false;
	
	/** The tribble on. */
	boolean tribbleOn = false;
	// default values for flags for LEDs
	/** The red on. */
	boolean redOn = false;
	
	/** The green on. */
	boolean greenOn = false;
	
	/** The toggle spot. */
	boolean toggleSpot = false;
	
	/** The toggle clean. */
	boolean toggleClean = false;
	
	/** The toggle max. */
	boolean toggleMax = false;
	
	/** The toggle dirt. */
	boolean toggleDirt = false;
	
	/** The toggle check robot. */
	boolean toggleCheckRobot = false;
	
	/** The toggle dock. */
	boolean toggleDock = false;
	
	/** The j panel sounds. */
	private JPanel jPanelSounds;
	
	/** The j panel modes. */
	private JPanel jPanelModes;
	
	/** The j panel3. */
	private JPanel jPanel3;
	
	/** The j panel2. */
	private JPanel jPanel2;
	
	/** The j label comm. */
	private JLabel jLabelCOMM;
	
	/** The j text field port. */
	private JTextField jTextFieldPort;
	
	/** The j label port. */
	private JLabel jLabelPort;
	
	/** The j panel1. */
	private JPanel jPanel1;
	
	/** The j text field host. */
	private JTextField jTextFieldHost;
	
	/** The j label host. */
	private JLabel jLabelHost;
	
	/** The j panel config serial. */
	private JPanel jPanelConfigSerial;
	
	/** The j panel config net. */
	private JPanel jPanelConfigNet;
	
	/** The j tabbed panel config. */
	private JTabbedPane jTabbedPanelConfig;
	
	/** The j button4. */
	private JButton jButton4;
	
	/** The j button3. */
	private JButton jButton3;
	
	/** The j button2. */
	private JButton jButton2;
	
	/** The j button1. */
	private JButton jButton1;
	
	/** The power_color. */
	int power_color = 0;
	
	/** The power_int. */
	int power_int = 0;
	
	/** The roomba comm serial. */
	RoombaCommSerial roombaCommSerial;
	
	/** The roomba comm abstract class to allow the actions to work with serial or net connections. */
	RoombaComm roombaComm;
	
	/** The roomba comm tcp client. */
	RoombaCommTCPClient roombaCommTCPClient;
	
	/** The formatter. */
	SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss");
	
	/** The led panel sci only. */
	private JPanel ledPanelSCIOnly;
	
	/** The protocols. */
	private String[] protocols = new String[] { "Roomba 1xx-4xx (SCI)", "Roomba 5xx (OI)" };

	/**
	 * Instantiates a new roomba comm frame without any params
	 */
	public RoombaCommFrame() {
		this(false);
	}

	/**
	 * Instantiates a new roomba comm frame.
	 * 
	 * @param debug boolean will increase STDOUT to show details about the process at runtime.
	 */
	public RoombaCommFrame(boolean debug) {
		super();
		debugPrintln(debug,"RoombaCommFrame-start");
		debugPrintln(debug," debug is ("+debug+")");
		initialize();
		roombaCommSerial = new RoombaCommSerial();
		roombaCommTCPClient = new RoombaCommTCPClient();
		this.debug = debug;
		roombaCommSerial.debug = debug;
		roombaCommTCPClient.debug = debug;
		debugPrintln(debug,"RoombaCommFrame-makePanels-start");
		makePanels();
		debugPrintln(debug,"RoombaCommFrame-makePanels-end");
		// Set Look & Feel
		try {
			javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		debugPrintln(debug,"RoombaCommFrame-end");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * The main method.
	 * 
	 * @param args the arguments are currently ignored
	 */
	public static void main(String[] args) {
		// by default (for now) turn on debug output if the class is called directly.
		RoombaCommFrame me = new RoombaCommFrame(true);
		me.pack();
		me.setVisible(true);
    }
	
	/**
	 * This method initializes this class to initial default conditions.
	 * In the future this method may also take args to control/style the UI to multiple styles.
	 */
	private void initialize() {
		Dimension defaultSize = new Dimension(826,651);
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(java.awt.event.ComponentEvent e) {
				debugPrintln(debug,"147-componentResized("+e.getComponent().getWidth()+","+e.getComponent().getHeight()+")"); // TODO Auto-generated Event stub componentResized()
			}
			public void componentMoved(java.awt.event.ComponentEvent e) {
			}
			public void componentShown(ComponentEvent e) {
			}
			public void componentHidden(java.awt.event.ComponentEvent e) {
			}
		});
        debugPrintln(debug,"initialize : setting size to (" +defaultSize.getWidth()+","+defaultSize.getHeight()+")");
        GridBagLayout thisLayout = new GridBagLayout();
        this.setSize(defaultSize);
        thisLayout.rowWeights = new double[] {0.0};
        thisLayout.rowHeights = new int[] {137};
        thisLayout.columnWeights = new double[] {0.0};
        thisLayout.columnWidths = new int[] {538};
        getContentPane().setLayout(thisLayout);
        this.setPreferredSize(new java.awt.Dimension(defaultSize)); //638
        this.setFocusTraversalKeysEnabled(false);
        this.setTitle("RoombaComm");
        this.addWindowListener(new WindowAdapter() {
        	public void windowClosed(WindowEvent evt) {
        		thisWindowClosed(evt);
        	}
        });
	}

	/**
	 * Set to 'false' to hide the "h/w handshake" button, which seems to be only
	 * needed on Windows.
	 * 
	 * @param b the new show hardware handhake
	 */
	public void setShowHardwareHandhake(boolean b) {
		handshakeButton.setVisible(b);
	}

	/**
	 * Connect to a Roomba using a serial connection.<br>
	 * This method uses the value of protocol to set the connection to the correct Roomba API version. <br>
	 * This method uses the value of handshakeButton and portChoices to make the serial connection. <br>
	 * This method also "chirps" the Roomba when it connects to provide an audio feedback/confirmation of the connection.
	 * 
	 * @return true, if successful
	 */
	public boolean connect() {
		debugPrintln(debug,"connect-start");
		String portname = (String) portChoices.getSelectedItem();
		roombaCommSerial.setWaitForDSR(handshakeButton.isSelected());
		int i = protocolChoices.getSelectedIndex();
		roombaCommSerial.setProtocol((i == 0) ? "SCI" : "OI");
		if (portname == null){
			// we do not yet have ports so refresh the list
			setCommPorts();
		}
		updateDisplay("connecting to " + portname + "\n");
		connectButton.setText("connecting");
		if (portname != null){
			if (!roombaCommSerial.connect(portname)) {
				updateDisplay("Couldn't connect to " + portname + "\n");
				connectButton.setText("  connect  ");
				debugPrintln(debug,"connect-end (could not connect)");
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
		updateDisplay("Checking for Roomba... ");
			if (roombaCommSerial.updateSensors()) {
				updateDisplay("Roomba found!\n");
				debugPrintln(debug,"connect-end");
				return true;
			}else{
				updateDisplay("No Roomba. :(  Is it turned on?\n");
				debugPrintln(debug,"connect-end");
				return true ;
			}
		}

	/**
	 * Connect to a Roomba using a network connection.<br>
	 * This method uses the value of protocol to set the connection to the correct Roomba API version. <br>
	 * This method uses the value of host and port to make the network connection. <br>
	 * This method also "chirps" the Roomba when it connects to provide an audio feedback/confirmation of the connection.
	 * 
	 * @param portname the portname
	 * @return true, if successful
	 */
	public boolean connectNet(String portname) {
		debugPrintln(debug,"connectNet called");
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
		//TODO: I am not sure this works yet... need to test to confirm. (2010.03.03)
		updateDisplay("Checking for Roomba... \n");
		if (roombaCommTCPClient.updateSensors()) {
			updateDisplay("Roomba found!\n");
			updateDisplay(roombaCommTCPClient.getSensorsAsString());
		} else {
			updateDisplay("No Roomba. :(  Is it turned on?\n");
		}
		updateDisplay("buffer is(" + roombaCommTCPClient.getBuffer() + ")",
				this.debug);
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
		return true;
	}

	/**
	 * Disconnect from the roomba when it is connected via a serial connection.
	 */
	public void disconnect() {
		roombaCommSerial.disconnect();
		connectButton.setText("  connect  ");
		connectButton.setActionCommand("connect");
	}

	/**
	 * Disconnect from the roomba when it is connected via a network connection.
	 */
	public void disconnectNet() {
		roombaCommTCPClient.disconnect();
		netButton.setText("  net  ");
		netButton.setActionCommand("net");
	}

	/**
	 * Play a (MIDI) note, that is, make the Roomba a musical instrument<br>
	 * notenums 32-127:<br> notenum == corresponding note played thru beeper<br>
	 * velocity == duration in number of 1/64s of a second (e.g. 64==1 second)<br>
	 * notenum 24: notenum == main vacuum velocity == non-zero turns on, zero
	 * turns off<br> notenum 25: blink LEDs, velcoity is color of Power<br> notenum 28 & 29: spin left & spin right, velocity is speed
	 * 
	 * @param notenum 32-127<br>corresponding note played thru beeper<br>
	 * @param velocity duration in number of 1/64s of a second (e.g. 64==1 second)
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

	/**
	 * implement actionlistener.
	 * 
	 * @param event the event
	 */
	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		updateDisplay(formatter.format(new Date()) + ": action (" + action
				+ ") happened\n", this.debug);
		if ("comboBoxChanged".equals(action)) {
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
		} else if ("OSU".equals(action)) {
			updateDisplay("Going to play OSU\n");
			roombaComm.stop();
			roombaComm.pause(500);
			playSong(
					roombaComm,
					"OSU:d=4,o=5,b=125:a,g,a#,a,8g#,a,8g#,2a,8p,8f,8g,8g#,a,8g#,a,8g#,a,g,f,a,g,8a,g,d,8f,8p,8f,8p,8f,8p,8f,8p,c6,a,g,f,8a#,a,8g,f,p,c6,a,g,a,8a#,a,8a#,c6,p,2d6,d,8c6,a#,g,f,8f,8f,8g,a#,8g,a#,a,2a#");
			// playSong(roombacomm,"Baa Baa Black Sheep:d=4,o=5,b=125:c,c,g,g,8a,8b,8c6,8a,g,p,f,f,e,e,d,d,c");
			roombaComm.stop();
		} else if ("Play RTTL".equals(action)){
			updateDisplay("Going to prompt for rttl string...\n");
			String rttl ="";
			updateDisplay("Going to play rttl string ("+rttl+")...\n");
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
			roombaComm.setLEDs(false, false, false, false, false, false, 0, 128);
		} else if ("sensors".equals(action)) {
			if (roombaComm.updateSensors())
				updateDisplay(roombaComm.sensorsAsString() + "\n");
			else
				updateDisplay("couldn't read Roomba. Is it connected?\n");
		} else if ("chargedata".equals(action)) {
			if (roombaComm.updateSensors())
				updateDisplay("*****\n" + roombaComm.chargeDataAsString()+ "\n");
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

	/**
	 * implement ChangeListener, for the slider.
	 * 
	 * @param e the e
	 */
	public void stateChanged(ChangeEvent e) {
		// System.err.println("stateChanged:"+e);
		JSlider src = (JSlider) e.getSource();
		if (!src.getValueIsAdjusting()) {
			int speed = (int) src.getValue();
			speed = (speed < 1) ? 1 : speed; // don't allow zero speed
			if (roombaComm != null){
				updateDisplay("setting speed = " + speed + "\n");
				roombaComm.setSpeed(speed);
			}
		}
	}

	/**
	 * TODO: comment needed for makePanels.
	 */
	void makePanels() {
		debugPrintln(debug,"makePanels-start");
		getContentPane().add(getJPanel4xx(), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 2, 2));
		makeDisplayPanel();
		updateDisplay("RoombaComm, version " + RoombaComm.VERSION + "\n");
		debugPrintln(debug,"makePanels-finish");
	}

	/**
	 * TODO: need comment for makeDisplayPanel.
	 */
	void makeDisplayPanel() {
		JTextArea displayText2 = new JTextArea(25, 30);
		displayText2.setLineWrap(true);
		DefaultCaret dc = new DefaultCaret();
		dc.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	}

	/**
	 * Update display.
	 * 
	 * @param s the s
	 */
	protected void updateDisplay(String s) {
		debugPrintln(debug,"updateDisplay(string): start");
		displayText.append(s);
		if (s != null && !(s.endsWith("\n"))) {
			displayText.append("\n");
		}
		debugPrintln(debug,"updateDisplay(string): reposition to "+displayText.getDocument().getLength());
		displayText.getParent().validate();
		displayText.getParent().repaint(100);
		displayText.setText(displayText.getText() );
		debugPrintln(debug,"updateDisplay(string): end");
	}

	/**
	 * Update display.
	 * 
	 * @param s the s
	 * @param onlyDebug the only debug
	 */
	protected void updateDisplay(String s, boolean onlyDebug) {
		if (onlyDebug && (roombaCommSerial.debug || roombaCommTCPClient.debug)) {
			updateDisplay(s);
			debugPrintln(true,s);
		}
	}

	/**
	 * Update display.
	 * 
	 * @param roombacomm the roombacomm
	 * @param jtextarea the jtextarea
	 * @param s the s
	 * @param onlyDebug the only debug
	 */
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

	/**
	 * Returns an ImageIcon, or null if the path was invalid.
	 * 
	 * @param path the path
	 * @param description the description
	 * @return the image icon
	 */
	protected static ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = RoombaCommFrame.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	// uh... we should try to avoid @SuppressWarnings ... 
	/**
	 * Play song.
	 * 
	 * @param roombacomm the roombacomm
	 * @param rtttl the rtttl
	 */
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

	/**
	 * Tribble start.
	 * 
	 * @param roombacomm the roombacomm
	 * @param jtextarea the jtextarea
	 * @param tribbleOn the tribble on
	 */
	protected static void tribbleStart(RoombaComm roombacomm,
			JTextArea jtextarea, boolean tribbleOn) {
		createTribblePurrSong(roombacomm);

		updateDisplay(roombacomm, jtextarea, "Press return to exit.", roombacomm.debug);

		while (tribbleOn) {
			purr(roombacomm, jtextarea);
			if (Math.random() < 0.1)
				bark(roombacomm, jtextarea);
			roombacomm.pause(1500 + (int) (Math.random() * 500));
			// tribbleOn = keyIsPressed();
			roombacomm.updateSensors();
			boolean b = roombacomm.maxButton();
			updateDisplay(roombacomm, jtextarea, "max button is (" + b + ")",roombacomm.debug);
			tribbleOn = (!b);
		}
	}

	/**
	 * Purr.
	 * 
	 * @param roombacomm the roombacomm
	 * @param jtextarea the jtextarea
	 */
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

	/**
	 * Creates the tribble purr song.
	 * 
	 * @param roombacomm the roombacomm
	 */
	protected static void createTribblePurrSong(RoombaComm roombacomm) {
		int song[] = { 68, 4, 67, 4, 66, 4, 65, 4, 64, 4, 63, 4, 62, 4, 61, 4,
				60, 4, 59, 4, 60, 4, 61, 4, };
		roombacomm.createSong(5, song);
	}

	/**
	 * Bark.
	 * 
	 * @param roombacomm the roombacomm
	 * @param jtextarea the jtextarea
	 */
	protected static void bark(RoombaComm roombacomm, JTextArea jtextarea) {
		updateDisplay(roombacomm, jtextarea, "bark", roombacomm.debug);
		roombacomm.vacuum(true);
		roombacomm.playNote(50, 5);
		roombacomm.pause(150);
		roombacomm.vacuum(false);
	}

	/**
	 * Gets the power_color.
	 * 
	 * @return the power_color
	 */
	protected int getPower_color() {
		return power_color;
	}

	/**
	 * Sets the power_color.
	 * 
	 * @param power_color the new power_color
	 */
	protected void setPower_color(int power_color) {
		if (power_color >= 0 && power_color <= 255) {
			this.power_color = power_color;
		} else {
			this.power_color = 0;
			updateDisplay("invalid power color attempted (" + power_color + ")");
		}
	}

	/**
	 * Gets the power_int.
	 * 
	 * @return the power_int
	 */
	protected int getPower_int() {
		return power_int;
	}

	/**
	 * Sets the power_int.
	 * 
	 * @param power_int the new power_int
	 */
	protected void setPower_int(int power_int) {

		if (power_color >= 0 && power_color <= 255) {
			this.power_int = power_int;
		} else {
			this.power_int = 0;
			updateDisplay("invalid power intensity attempted (" + power_color
					+ ")");
		}
	}

	/**
	 * Sets the lE ds.
	 * 
	 * @param roombacomm the new lE ds
	 */
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

	/**
	 * Sets the chg green led.
	 * 
	 * @param roombacomm the roombacomm
	 * @param green the green
	 */
	protected void setChgGreenLED(RoombaComm roombacomm, boolean green) {
		this.greenOn = green;
		updateDisplay("setChgGreenLED", this.debug);
		this.setLEDs(roombacomm);
	}

	/**
	 * Sets the chg red led.
	 * 
	 * @param roombacomm the roombacomm
	 * @param red the red
	 */
	protected void setChgRedLED(RoombaComm roombacomm, boolean red) {
		this.redOn = red;
		updateDisplay("setChgRedLED", this.debug);
		this.setLEDs(roombacomm);
	}

	/**
	 * Sets the chg spot led.
	 * 
	 * @param roombacomm the roombacomm
	 * @param spot the spot
	 */
	protected void setChgSpotLED(RoombaComm roombacomm, boolean spot) {
		updateDisplay("setChgSpotLED value(" + spot + ")", this.debug);
		roombacomm.setChgSpotLED(roombacomm, spot);
		this.toggleSpot = roombacomm.isToggleSpot();
	}

	/**
	 * Sets the chg clean led.
	 * 
	 * @param roombacomm the roombacomm
	 * @param clean the clean
	 */
	protected void setChgCleanLED(RoombaComm roombacomm, boolean clean) {
		updateDisplay("setChgCleanLED value(" + clean + ")", this.debug);
		roombacomm.setChgCleanLED(roombacomm, clean);
		this.toggleClean = roombacomm.isToggleClean();
	}

	/**
	 * Sets the chg max led.
	 * 
	 * @param roombacomm the roombacomm
	 * @param max the max
	 */
	protected void setChgMaxLED(RoombaComm roombacomm, boolean max) {
		updateDisplay("setChgMaxLED value(" + max + ")", this.debug);
		roombacomm.setChgMaxLED(roombacomm, max);
		this.toggleMax = roombacomm.isToggleMax();
	}

	/**
	 * Sets the chg dirt led.
	 * 
	 * @param roombacomm the roombacomm
	 * @param dirt the dirt
	 */
	protected void setChgDirtLED(RoombaComm roombacomm, boolean dirt) {
		updateDisplay("setChgDirtLED value(" + dirt + ")", this.debug);
		roombacomm.setChgDirtLED(roombacomm, dirt);
		this.toggleDirt = roombacomm.isToggleDirt();
	}

	/**
	 * Sets the toggle check robot.
	 * 
	 * @param roombacomm the roombacomm
	 * @param CheckRobot the check robot
	 */
	public void setToggleCheckRobot(RoombaComm roombacomm, boolean CheckRobot) {
		updateDisplay("setChgCheckRobotLED value(" + CheckRobot + ")",this.debug);
		roombacomm.setChgCheckRobotLED(roombacomm, CheckRobot);
		this.toggleCheckRobot = roombacomm.isToggleCheckRobot();
	}

	/**
	 * Sets the toggle dock.
	 * 
	 * @param roombacomm the roombacomm
	 * @param dock the dock
	 */
	public void setToggleDock(RoombaComm roombacomm, boolean dock) {
		updateDisplay("setChgDockLED value(" + dock + ")", this.debug);
		roombacomm.setChgDockLED(roombacomm, dock);
		this.toggleDock = roombacomm.isToggleDock();
	}

	/**
	 * Sets the chg power color led.
	 * 
	 * @param roombacomm the roombacomm
	 * @param power_color the power_color
	 */
	protected void setChgPowerColorLED(RoombaComm roombacomm, int power_color) {
		updateDisplay("setChgPowerColorLED value(" + power_color + ")",this.debug);
		roombacomm.setChgPowerColorLED(roombacomm, power_color);
		// this.power_color
		// TODO: Keep track of power color
	}

	/**
	 * Sets the chg power intensity led.
	 * 
	 * @param roombacomm the roombacomm
	 * @param power_intensity the power_intensity
	 */
	protected void setChgPowerIntensityLED(RoombaComm roombacomm, int power_intensity) {
		updateDisplay("setChgPowerIntensityLED value(" + power_intensity + ")",this.debug);
		roombacomm.setChgPowerIntensityLED(roombacomm, power_intensity);
		// TODO: Keep track of Power Intensity
	}

	/**
	 * Gets the toggle check robot.
	 * 
	 * @return the toggle check robot
	 */
	public boolean getToggleCheckRobot() {
		return toggleCheckRobot;
	}

	/**
	 * Gets the toggle dock.
	 * 
	 * @return the toggle dock
	 */
	public boolean getToggleDock() {
		return toggleDock;
	}
	
	/**
	 * Gets the j tabbed panel config.
	 * 
	 * @return the j tabbed panel config
	 */
	private JTabbedPane getJTabbedPanelConfig() {
		if(jTabbedPanelConfig == null) {
			jTabbedPanelConfig = new JTabbedPane();
			jTabbedPanelConfig.setPreferredSize(new java.awt.Dimension(139, 28));
			jTabbedPanelConfig.addTab("Net", null, getJPanelConfigNet(), "set the TCP network settings here");
			jTabbedPanelConfig.addTab("Serial", null, getJPanelConfigSerial(), "set the serial settings here");
		}
		jTabbedPanelConfig.setMinimumSize(new Dimension(200,100));
		jTabbedPanelConfig.setPreferredSize(new java.awt.Dimension(254, 100));
		jTabbedPanelConfig.setToolTipText("Use one of the two connection methods to communicate with the Roomba");
		// Register a change listener
		jTabbedPanelConfig.addChangeListener(new ChangeListener() {
	        // This method is called whenever the selected tab changes
	        public void stateChanged(ChangeEvent evt) {
	        	debugPrintln(debug,"jTabbedPanelConfig - state changed");
	            JTabbedPane pane = (JTabbedPane)evt.getSource();
	            // Get current tab
	            int sel = pane.getSelectedIndex();
	            debugPrintln(debug,"jTabbedPanelConfig - tab "+sel+" selected");
	            debugPrintln(debug,"jTabbedPanelConfig - roombaCommSerial.isConnected() "+roombaCommSerial.isConnected());
	            debugPrintln(debug,"jTabbedPanelConfig - roombaCommTCPClient.isConnected() "+roombaCommTCPClient.isConnected());
	            if (roombaCommTCPClient.isConnected() && roombaCommSerial.isConnected()){
	            	debugPrintln(debug,"***** I have no idea why both should ever be connected at the same time... THIS IS STRANGE *****");
	            }
	            if (sel == 1){
	            	setCommPorts();
	            	if (roombaCommTCPClient.isConnected() && !roombaCommSerial.isConnected()){
	            		pane.setSelectedIndex(0);
	            		debugPrintln(debug,"active net connection found setting focus to the correct tab");
	            	}
	            }
	            if (sel == 0){
	            	if (!roombaCommTCPClient.isConnected() && roombaCommSerial.isConnected()){
	            		pane.setSelectedIndex(1);
	            		debugPrintln(debug,"active Serial connection found setting focus to the correct tab");
	            	}
	            }
	        }
	    });
		return jTabbedPanelConfig;
	}
	
	/**
	 * Gets the j panel config net.
	 * 
	 * @return the j panel config net
	 */
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
	
	/**
	 * Gets the j panel config serial.
	 * 
	 * @return the j panel config serial
	 */
	private JPanel getJPanelConfigSerial() {
		if(jPanelConfigSerial == null) {
			jPanelConfigSerial = new JPanel();
			GridBagLayout jPanelConfigSerialLayout = new GridBagLayout();
			jPanelConfigSerial.setPreferredSize(new java.awt.Dimension(318, 72));
			jPanelConfigSerialLayout.rowWeights = new double[] {0.1};
			jPanelConfigSerialLayout.rowHeights = new int[] {7};
			jPanelConfigSerialLayout.columnWeights = new double[] {0.0, 0.1, 0.1};
			jPanelConfigSerialLayout.columnWidths = new int[] {77, 7, 7};
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
	
	/**
	 * Gets the j label host.
	 * 
	 * @return the j label host
	 */
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
	
	/**
	 * Gets the j text field host.
	 * 
	 * @return the j text field host
	 */
	private JTextField getJTextFieldHost() {
		if(jTextFieldHost == null) {
			jTextFieldHost = new JTextField();
			jTextFieldHost.setText("192.168.15.240");
			jTextFieldHost.setToolTipText("Set the hostname/IP address");
		}
		return jTextFieldHost;
	}
	
	/**
	 * Gets the j panel1.
	 * 
	 * @return the j panel1
	 */
	private JPanel getJPanel1() {
		if(jPanel1 == null) {
			jPanel1 = new JPanel();
			GridBagLayout jPanel1Layout = new GridBagLayout();
			jPanel1Layout.rowWeights = new double[] {0.1};
			jPanel1Layout.rowHeights = new int[] {7};
			jPanel1Layout.columnWeights = new double[] {0.1, 0.1};
			jPanel1Layout.columnWidths = new int[] {7, 7};
			jPanel1.setLayout(jPanel1Layout);
			jPanel1.setPreferredSize(new java.awt.Dimension(800,320));
			{
				ledPanel = new JPanel();
				GridBagLayout ledPanelLayout = new GridBagLayout();
				ledPanel.setLayout(ledPanelLayout);
				jPanel1.add(ledPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				{
					ledPanelSCIOnly = new JPanel(new GridLayout(3, 3));
					GridBagLayout ledPanel1Layout = new GridBagLayout();
					ledPanel.add(getLedPanelShared(), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
					ledPanel.add(getJPanel4x(), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
					ledPanel.add(ledPanelSCIOnly, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
					ledPanel1Layout.rowWeights = new double[] {0.1};
					ledPanel1Layout.rowHeights = new int[] {7};
					ledPanel1Layout.columnWeights = new double[] {0.1, 0.1, 0.1, 0.1};
					ledPanel1Layout.columnWidths = new int[] {7, 7, 7, 7};
					ledPanelSCIOnly.setLayout(ledPanel1Layout);
					ButtonGroup group = new ButtonGroup();
					String off="None";
					String green="Green";
					String red="Red";
					String both="Orange";
					{
						JRadioButton statusOffButton = new JRadioButton(off);
						ledPanelSCIOnly.add(statusOffButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
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
						ledPanelSCIOnly.add(statusGreenButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
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
						ledPanelSCIOnly.add(statusRedButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
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
						ledPanelSCIOnly.add(statusBothButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
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
				}
				{
					powerColorSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 100);
					ledPanel.add(powerColorSlider, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 2, 2));
					powerColorSlider.setPaintTicks(true);
					powerColorSlider.setMajorTickSpacing(100);
					powerColorSlider.setMinorTickSpacing(25);
					powerColorSlider.setPaintLabels(true);
					powerColorSlider.setSize(200, 46);
					powerColorSlider.setPreferredSize(new java.awt.Dimension(224, 48));
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
					ledPanel.add(powerColorLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 2, 2));
				}
				{
					powerColorIntensity = new JSlider(JSlider.HORIZONTAL, 0, 255, 100);
					ledPanel.add(powerColorIntensity, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 2, 2));
					powerColorIntensity.setPaintTicks(true);
					powerColorIntensity.setMajorTickSpacing(100);
					powerColorIntensity.setMinorTickSpacing(25);
					powerColorIntensity.setPaintLabels(true);
					powerColorIntensity.setPreferredSize(new java.awt.Dimension(248, 45));
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
					ledPanel.add(powerColorIntensityLabel, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 2, 2));
				}
				ledPanelLayout.rowWeights = new double[] {0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1};
				ledPanelLayout.rowHeights = new int[] {7, 7, 7, 7, 7, 7, 7};
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
	
	/**
	 * Gets the j label port.
	 * 
	 * @return the j label port
	 */
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
	
	/**
	 * Gets the j text field port.
	 * 
	 * @return the j text field port
	 */
	private JTextField getJTextFieldPort() {
		if(jTextFieldPort == null) {
			jTextFieldPort = new JTextField();
			jTextFieldPort.setToolTipText("Set the TCP port");
			jTextFieldPort.setText("5001");
		}
		return jTextFieldPort;
	}
	
	/**
	 * J panel config serial focus gained.
	 * 
	 * @param evt the evt
	 */
	private void jPanelConfigSerialFocusGained(FocusEvent evt) {
		debugPrintln(debug,"jPanelConfigSerial.focusGained, event="+evt);
		//TODO add your code for jPanelConfigSerial.focusGained
		// make sure we have a roombaCommSerial object
		setCommPorts();
	}

	/**
	 * Sets the comm ports.
	 */
	private void setCommPorts() {
		debugPrintln(debug,"setCommPorts-start");
		if (roombaCommSerial == null){
			debugPrintln(debug,"setCommPorts-roombaCommSerial is null");
			roombaCommSerial = new RoombaCommSerial(this.debug);	
		}
		if (portChoices != null) {
			debugPrintln(debug,"setCommPorts-portChoices object is not null");
			// if the list of ports is empty then try to fill it
			if (portChoices.getItemCount() ==0){
			// fill in the comm ports (combo box) with choices.
				debugPrintln(debug,"setCommPorts-getting list of ports");
				String[] ports = roombaCommSerial.listPorts();
				// for now short cutting looking for serial ports to speed up start/stop of the UI
	//				String[] ports = {"a","b"};
				debugPrintln(debug,"setCommPorts-found "+ports.length+" serialports");
				for (int i = 0; i < ports.length; i++) {
					String s = ports[i];
					debugPrintln(debug,"setCommPorts-adding ["+i+"] as "+s);
					portChoices.addItem(ports[i]);
					if (s.equals(roombaCommSerial.getPortname())) {
						debugPrintln(debug,"setCommPorts- setting port as selected due to "+roombaCommSerial.getPortname());
						portChoices.setSelectedItem(s);
					}
				}
				portChoices.validate();
				portChoices.repaint();
			}
		}
		debugPrintln(debug,"setCommPorts-start");
	}
	
	/**
	 * Gets the j label comm.
	 * 
	 * @return the j label comm
	 */
	private JLabel getJLabelCOMM() {
		if(jLabelCOMM == null) {
			jLabelCOMM = new JLabel();
			jLabelCOMM.setText("COM");
		}
		return jLabelCOMM;
	}
	
	/**
	 * Gets the j panel2.
	 * 
	 * @return the j panel2
	 */
	private JPanel getJPanel2() {
		if(jPanel2 == null) {
			jPanel2 = new JPanel();
			GridBagLayout jPanel2Layout = new GridBagLayout();
			jPanel2Layout.rowWeights = new double[] {0.1, 0.1, 0.1, 0.1, 0.1, 0.1};
			jPanel2Layout.rowHeights = new int[] {7, 7, 7, 7, 7, 7};
			jPanel2Layout.columnWeights = new double[] {0.0};
			jPanel2Layout.columnWidths = new int[] {150};
			jPanel2.setLayout(jPanel2Layout);
			jPanel2.setBorder(BorderFactory.createTitledBorder(null,"Commands",TitledBorder.LEADING,TitledBorder.DEFAULT_POSITION));
			jPanel2.setPreferredSize(new java.awt.Dimension(233,505));
			jPanel2.add(getJPanelModes(), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
			jPanel2.add(getJPanelSounds(), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
			jPanel2.add(getJPanelTestPrograms(), new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
			jPanel2.add(getJPanelVacuum(), new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
			jPanel2.add(getJPanelPower(), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
			jPanel2.add(getJPanel4(), new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
		}
		return jPanel2;
	}
	
	/**
	 * Gets the j panel3.
	 * 
	 * @return the j panel3
	 */
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
	
	/**
	 * Gets the j panel modes.
	 * 
	 * @return the j panel modes
	 */
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
	
	/**
	 * Gets the j panel sounds.
	 * 
	 * @return the j panel sounds
	 */
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
			{
				JButton but_playRTTL = getBut_playRTTL();
				jPanelSounds.add(getBut_playRTTL(), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
				but_playRTTL.setMargin(new java.awt.Insets(2, 2, 2, 2));
				but_playRTTL.addActionListener(this);
			}
		}
		return jPanelSounds;
	}
	
	/**
	 * Gets the j panel test programs.
	 * 
	 * @return the j panel test programs
	 */
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
	
	/**
	 * Gets the j panel4.
	 * 
	 * @return the j panel4
	 */
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
	
	/**
	 * Gets the j panel vacuum.
	 * 
	 * @return the j panel vacuum
	 */
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
	
	/**
	 * Gets the j panel power.
	 * 
	 * @return the j panel power
	 */
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
	
	/**
	 * Gets the j text pane1.
	 * 
	 * @return the j text pane1
	 */
	private JTextPane getJTextPane1() {
		if(jTextPane1 == null) {
			jTextPane1 = new JTextPane();
			jTextPane1.setText("Set the Protocal, then connect via a network or Serial connection.\nThen you can use the Commands, LED's, and/or Movement controls to operate your Roomba.");
			jTextPane1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			jTextPane1.setOpaque(false);
		}
		return jTextPane1;
	}
	
	/**
	 * Gets the j panel4x.
	 * 
	 * @return the j panel4x
	 */
	private JPanel getJPanel4x() {
		if(ledPanelOIOnly == null) {
			ledPanelOIOnly = new JPanel();
			GridBagLayout jPanel4Layout = new GridBagLayout();
			ledPanelOIOnly.setPreferredSize(new java.awt.Dimension(134, 49));
			jPanel4Layout.rowWeights = new double[] {0.1};
			jPanel4Layout.rowHeights = new int[] {7};
			jPanel4Layout.columnWeights = new double[] {0.1, 0.1, 0.1, 0.1};
			jPanel4Layout.columnWidths = new int[] {7, 7, 7, 7};
			ledPanelOIOnly.setLayout(jPanel4Layout);
			ledPanelOIOnly.setVisible(false);
			{
				JButton but_toggleDock = new JButton();
				GridBagLayout but_toggleDockLayout = new GridBagLayout();
				ledPanelOIOnly.add(but_toggleDock, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				but_toggleDock.setActionCommand("toggleDock");
				but_toggleDock.setIcon(new ImageIcon(getClass().getClassLoader().getResource("com/hackingroomba/roombacomm/images/but_dockOn.png")));
				but_toggleDock.setPreferredSize(new java.awt.Dimension(40, 40));
				but_toggleDock.setSize(40, 40);
				but_toggleDockLayout.rowWeights = new double[] {0.1};
				but_toggleDockLayout.rowHeights = new int[] {7};
				but_toggleDockLayout.columnWeights = new double[] {0.1, 0.1};
				but_toggleDockLayout.columnWidths = new int[] {7, 7};
				but_toggleDock.setLayout(but_toggleDockLayout);
				but_toggleDock.addActionListener(this);
			}
			{
				JButton but_toggleCheckRobot = new JButton();
				GridBagLayout but_toggleCheckRobotLayout = new GridBagLayout();
				ledPanelOIOnly.add(but_toggleCheckRobot, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				but_toggleCheckRobot.setActionCommand("toggleCheckRobot");
				but_toggleCheckRobot.setIcon(new ImageIcon(getClass().getClassLoader().getResource("com/hackingroomba/roombacomm/images/but_checkrobotOn.png")));
				but_toggleCheckRobot.setPreferredSize(new java.awt.Dimension(40, 40));
				but_toggleCheckRobot.setSize(40, 40);
				but_toggleCheckRobotLayout.rowWeights = new double[] {0.1};
				but_toggleCheckRobotLayout.rowHeights = new int[] {7};
				but_toggleCheckRobotLayout.columnWeights = new double[] {0.1, 0.1};
				but_toggleCheckRobotLayout.columnWidths = new int[] {7, 7};
				but_toggleCheckRobot.setLayout(but_toggleCheckRobotLayout);
				but_toggleCheckRobot.addActionListener(this);
			}
		}
		return ledPanelOIOnly;
	}
	
	/**
	 * Gets the led panel shared.
	 * 
	 * @return the led panel shared
	 */
	private JPanel getLedPanelShared() {
		if(ledPanelShared == null) {
			ledPanelShared = new JPanel();
			{
				JButton but_toggleSpot = new JButton();
				ledPanelShared.add(but_toggleSpot);
				but_toggleSpot.setActionCommand("toggleSpot");
				but_toggleSpot.setIcon(getIcon("com/hackingroomba/roombacomm/images/but_spotOn.png"));
				but_toggleSpot.setPreferredSize(new java.awt.Dimension(40, 40));
				but_toggleSpot.setSize(40, 40);
				but_toggleSpot.addActionListener(this);
			}
			{
				JButton but_toggleClean = new JButton();
				ledPanelShared.add(but_toggleClean);
				but_toggleClean.setActionCommand("toggleClean");
				but_toggleClean.setIcon(new ImageIcon(getClass().getClassLoader().getResource("com/hackingroomba/roombacomm/images/but_cleanOn.png")));
				but_toggleClean.setPreferredSize(new java.awt.Dimension(40, 40));
				but_toggleClean.setSize(40, 40);
				but_toggleClean.addActionListener(this);
			}
			{
				JButton but_toggleDirt = new JButton();
				ledPanelShared.add(but_toggleDirt);
				but_toggleDirt.setActionCommand("toggleDirt");
				but_toggleDirt.setIcon(new ImageIcon(getClass().getClassLoader().getResource("com/hackingroomba/roombacomm/images/but_dirtOn.png")));
				but_toggleDirt.setPreferredSize(new java.awt.Dimension(40, 40));
				but_toggleDirt.setSize(40, 40);
				but_toggleDirt.addActionListener(this);
			}
			{
				JButton but_toggleMax = new JButton();
				ledPanelShared.add(but_toggleMax);
				but_toggleMax.setActionCommand("toggleMax");
				but_toggleMax.setIcon(new ImageIcon(getClass().getClassLoader().getResource("com/hackingroomba/roombacomm/images/but_maxOn.png")));
				but_toggleMax.setSize(40, 40);
				but_toggleMax.setPreferredSize(new java.awt.Dimension(40, 40));
				but_toggleMax.addActionListener(this);
			}
		}
		return ledPanelShared;
	}

	/**
	 * Gets the icon.
	 * 
	 * @param str the str
	 * @return the icon
	 * @return
	 */
	private ImageIcon getIcon(String str) {
		java.net.URL file =getClass().getClassLoader().getResource(str);
		if (file != null){
			return new ImageIcon(file);
		}else{
			// return the default icon
			//TODO: Get a better default icon. :)
			System.err.println("failed to find icon (" + str + ")");
			return new ImageIcon("com/hackingroomba/roombacomm/images/but_spotOn.png"); 
		}
	}
	
	/**
	 * Gets the j panel4xx.
	 * 
	 * @return the j panel4xx
	 */
	private JPanel getJPanel4xx() {
		if(jPanel4 == null) {
			jPanel4 = new JPanel();
			GridBagLayout jPanel4Layout1 = new GridBagLayout();
			jPanel4.setLayout(jPanel4Layout1);
			jPanel4.setPreferredSize(new java.awt.Dimension(806, 608));
			{
				selectPanel = new JPanel();
				GridBagLayout selectPanelLayout = new GridBagLayout();
				selectPanel.setLayout(selectPanelLayout);
				jPanel4.add(selectPanel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
				{
					protocolChoices = new JComboBox(protocols);
					selectPanel.add(protocolChoices, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
					protocolChoices.setSelectedIndex(protocolChoices.getSelectedIndex() >=0 ? protocolChoices.getSelectedIndex() : 0);
					protocolChoices.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0), "Protocal", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION));
					protocolChoices.setToolTipText("Set the protocal based on the roomba hardware version");
					protocolChoices.addItemListener(new ItemListener() {
						public void itemStateChanged(ItemEvent evt) {
							debugPrintln(debug,"protocolChoices.itemStateChanged, event="+evt);
							//TODO add your code for protocolChoices.itemStateChanged
							if (evt.getItem().equals(protocolChoices.getItemAt(0)) && (evt.getStateChange() == ItemEvent.SELECTED)){
								ledPanelOIOnly.setVisible(false);
								ledPanelSCIOnly.setVisible(true);
							}
							if (evt.getItem().equals(protocolChoices.getItemAt(1)) && (evt.getStateChange() == ItemEvent.SELECTED)){
								ledPanelOIOnly.setVisible(true);
								ledPanelSCIOnly.setVisible(false);
							}
							ledPanelOIOnly.repaint();
							ledPanelSCIOnly.repaint();
						}
					});
					protocolChoices.addActionListener(this);
				}
				{
					handshakeButton = new JCheckBox("<html>h/w<br>handshake</html>");
					selectPanel.add(handshakeButton, new GridBagConstraints(0, 2, 1, 1, 0.1, 0.1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
					selectPanel.add(getJTabbedPanelConfig(), new GridBagConstraints(1, 1, 1, 2, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
					selectPanel.add(getJTextPane1(), new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 2, 2));
					handshakeButton.setPreferredSize(new java.awt.Dimension(148, 40));
					handshakeButton.setText("h/w handshake");
					handshakeButton.setToolTipText("check if you want to use the hardware handshake setting");
				}
				selectPanelLayout.rowWeights = new double[] {0.0, 0.0};
				selectPanelLayout.rowHeights = new int[] {53, 58};
				selectPanelLayout.columnWeights = new double[] {0.0, 0.0};
				selectPanelLayout.columnWidths = new int[] {177, 375};
				selectPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Select Roomba Type & Port"),BorderFactory.createEmptyBorder(1,1,1,1)));
				selectPanel.setPreferredSize(new java.awt.Dimension(535,128));
			}
			{
				displayPanel = new JPanel();
				jPanel4.add(displayPanel, new GridBagConstraints(1, 2, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
				jPanel4.add(getJPanel1(), new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				jPanel4.add(getJPanel2(), new GridBagConstraints(2, 0, 1, 2, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				jPanel4.add(getJPanel3(), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				GridBagLayout displayPanelLayout = new GridBagLayout();
				displayPanelLayout.rowWeights = new double[] {0.1};
				displayPanelLayout.rowHeights = new int[] {7};
				displayPanelLayout.columnWeights = new double[] {0.1};
				displayPanelLayout.columnWidths = new int[] {7};
				displayPanel.setLayout(displayPanelLayout);
				displayPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Display"),BorderFactory.createEmptyBorder(1,1,1,1)));
				displayPanel.setPreferredSize(new java.awt.Dimension(803, 67));
				{
					displayText = new JTextArea(10, 75);
					displayText.setEditable(false);
					JScrollPane scrollPane = new JScrollPane(displayText,
							JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
							JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
					displayPanel.add(scrollPane, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
					scrollPane.setSize(795, 163);
					scrollPane.setPreferredSize(new java.awt.Dimension(791, 38));
					displayText.setLineWrap(false);					
				}
			}
			jPanel4Layout1.rowWeights = new double[] {0.1, 0.1, 2.0};
			jPanel4Layout1.rowHeights = new int[] {7, 7, 80};
			jPanel4Layout1.columnWeights = new double[] {0.1, 0.1, 0.1};
			jPanel4Layout1.columnWidths = new int[] {7, 7, 7};
		}
		return jPanel4;
	}
    
    /**
     * Debug println.
     * 
     * @param debug the debug
     * @param str the str
     */
    private void debugPrintln(boolean debug, String str){
    	if (debug){
    		System.out.println(str);
    	}
    }
    
    /**
     * This window closed.
     * 
     * @param evt the evt
     */
    private void thisWindowClosed(WindowEvent evt) {
    	System.out.println("this.windowClosed, event="+evt);
    	//TODO: add code for this.windowClosed ? Do we need anything else?
    	dispose();
    }
    
    /**
     * Gets the but_play rttl.
     * 
     * @return the but_play rttl
     */
    private JButton getBut_playRTTL() {
    	if(but_playRTTL == null) {
    		but_playRTTL = new JButton();
    		but_playRTTL.setText("Play RTTL");
    		but_playRTTL.setVisible(false);
    	}
    	return but_playRTTL;
    }
}