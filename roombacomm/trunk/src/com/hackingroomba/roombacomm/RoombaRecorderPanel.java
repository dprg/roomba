/*
 * RoombaRecorderPanel - 
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.util.*;

/**
 * A Panel containing controls for testing RoombaComm. 
 * Normally put inside of a frame, for example see RoombaCommTest
 *
 */
public class RoombaRecorderPanel extends JPanel implements ActionListener,Runnable {

    JPanel ctrlPanel, selectPanel, buttonPanel, displayPanel;
    JComboBox portChoices;
    JCheckBox handshakeButton;
    JTextArea displayText;
    JButton  connectButton;
    JSlider speedSlider;

    RoombaCommSerial roombacomm;

    Thread thread;
    ArrayList events;
    ArrayList eventTimes;
    //ActionEvent[] events;
    //long[] eventTimes;
    int eventCount;
    int eventIndex;
    int eventMax = 1000;  // fixme:
    long playStartTime;
    long recStartTime;
    boolean recording = false;
    boolean playing = false;
    boolean looping = false;

    public RoombaRecorderPanel() {
        super(new BorderLayout());

        roombacomm = new RoombaCommSerial();

        events     = new ArrayList();
        eventTimes = new ArrayList();
        eventIndex = 0;
        eventCount = 0;

        makeSelectPanel();
        add(selectPanel, BorderLayout.NORTH);

        makeCtrlPanel();
        add(ctrlPanel, BorderLayout.EAST);

        makeButtonPanel();
        add(buttonPanel, BorderLayout.CENTER);

        makeDisplayPanel();
        add(displayPanel, BorderLayout.SOUTH);

        updateDisplay("RoombaComm, version "+RoombaComm.VERSION+"\n");

        //bindKeys();

        thread = new Thread(this);
        thread.start();
    }
    
    /**
     * Set to 'false' to hide the "h/w handshake" button, which seems to be
     * only needed on Windows
     */
    public void setShowHardwareHandhake(boolean b) {
        handshakeButton.setVisible(b);
    }

    /** */
    public boolean connect() {
        String portname = (String) portChoices.getSelectedItem();
        roombacomm.debug=true;
        roombacomm.waitForDSR = handshakeButton.isSelected();
        
        connectButton.setText("connecting");
        if(!roombacomm.connect(portname)) {
            updateDisplay("Couldn't connect to "+portname+"\n");
            connectButton.setText("  connect  ");
            roombacomm.debug=false;
            return false;
        }
        updateDisplay("Roomba startup\n");

        roombacomm.startup();
        roombacomm.control();
        roombacomm.pause(50);
        roombacomm.playNote(72, 10);  // C , test note
        roombacomm.pause(200);

        connectButton.setText("disconnect");
        connectButton.setActionCommand("disconnect");
        updateDisplay("Roomba connected\n");
        roombacomm.debug=true;
        return true;
    }

    /** */
    public void disconnect() {
        roombacomm.disconnect();
        connectButton.setText("  connect  ");
        connectButton.setActionCommand("connect");
    }
    
    public void stop() {
        //thread.interrupt();
        if(recording) 
            eventCount = eventIndex;
        recording = false;
        playing = false; 
        updateDisplay("Stopped\n");
    }
    public boolean play() {
        if(eventCount==0) {
            updateDisplay("nothing recorded\n");
            return false;
        }
        if(recording)
            eventCount = eventIndex;
        playStartTime = millis();
        playing = true;
        recording = false;
        eventIndex = 0;
        updateDisplay("Playback...\n");
        return true;
    }
    public void record() { 
        recStartTime = millis();
        playing = false;
        recording = true;
        eventIndex = 0;
        updateDisplay("Recording...\n");
    }
    public void setLooping(boolean b) { looping = b; }
    public boolean getLooping() { return looping; }

    public HashMap cut() {
        HashMap m = copy();
        stop();
        eventCount=0;
        return m;
    }
    public HashMap copy() {
        HashMap m = new HashMap();
        m.put("events",events);
        m.put("eventTimes",eventTimes);
        m.put("portname", portChoices.getSelectedItem());
        updateDisplay("Copied "+eventCount+" events\n");
        return m;
    }
    public void paste(HashMap m) {
        stop();
        ArrayList al1 = (ArrayList) m.get("events");
        ArrayList al2 = (ArrayList) m.get("eventTimes");
        portChoices.setSelectedItem(m.get("portname"));
        if(al1!= null && al2!=null) {
            events = al1;
            eventTimes = al2;
            eventCount = events.size();
            updateDisplay("Pasted "+eventCount+" events\n");
        }
    }

    /** Implement Runnable */
    public void run() {
        boolean done = false;
        while(!done) {
            if(playing && eventCount !=0) {
                long t = ((Long)eventTimes.get(eventIndex)).longValue();
                //long t = eventTimes[eventIndex];
                if(millis() - playStartTime > t) {
                    ActionEvent event = (ActionEvent) events.get(eventIndex);
                    actionPerformed(event);
                    eventIndex++;
                    if(eventIndex == eventCount) {
                        if(!looping) {
                            playing = false;
                        } else {
                            eventIndex = 0;
                            playStartTime = millis();
                        }
                    }
                }
            }
            try { 
                Thread.sleep(10);
            } catch(Exception e) {
                done = true;
            }
        }
    }

    /** 
     * Implement Actionlistener, also this is the locus for record & playback
     *
     */
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        String actstr = "";

        if("comboBoxChanged".equals(action))
            return;  // don't care about comboBox 

        if(recording) {
            long t = millis() - recStartTime;
            actstr += t+":r#"+eventIndex+": ";
            events.add(eventIndex, event);
            eventTimes.add(eventIndex, new Long(millis()-recStartTime));
            eventIndex++;
        }
        if(playing) {
            long t = millis() - playStartTime;
            actstr += t+":p#"+eventIndex+": ";
        }
        updateDisplay(actstr+action+"\n");  /// DEBUG

        if("connect".equals(action)) {
            updateDisplay("connecting...\n");
            connect();
            return;
        }
        else if("disconnect".equals(action)) {
            updateDisplay("disconnecting.\n");
            disconnect();
            return;
        }
        else {
            try { 
                int speed = Integer.parseInt(action);
                //updateDisplay("setting speed = "+speed+"\n");
                roombacomm.setSpeed(speed);
                return;
            } catch(NumberFormatException e) { }
        }
        // stop right here if we're not connected
        if(!roombacomm.connected()) {
            updateDisplay("not connected!\n");
            return;
        }

        //updateDisplay(action+"\n");
        if("stop".equals(action)) {
            roombacomm.stop();
        }
        else if("forward".equals(action)) {
            roombacomm.goForward();
        }
        else if("backward".equals(action)) {
            roombacomm.goBackward();
        }
        else if("spinleft".equals(action)) {
            roombacomm.spinLeft();
        }
        else if("spinright".equals(action)) {
            roombacomm.spinRight();
        }
        else if("turnleft".equals(action)) {
            roombacomm.turnLeft();
        }
        else if("turnright".equals(action)) {
            roombacomm.turnRight();
        }
        else if(action.matches("^turn[-+0-9]+$")) {
            int turnval = Integer.parseInt(action.substring(4));
            System.out.println("turnval="+turnval);
            roombacomm.turn(turnval);
        }
        else if("test".equals(action)) {
            updateDisplay("Playing some notes\n");
            roombacomm.playNote(72, 10);  // C
            roombacomm.pause(200);
            roombacomm.playNote(79, 10);  // G
            roombacomm.pause(200);
            roombacomm.playNote(76, 10);  // E
            roombacomm.pause(200);

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
        }
        else if("reset".equals(action)) {
            roombacomm.stop();
            roombacomm.startup();
            roombacomm.control();
        }
        else if("power-off".equals(action)) {
            roombacomm.powerOff();
        }
        else if("wakeup".equals(action)) {
            roombacomm.wakeup();
        }
        else if("beep-lo".equals(action)) {
            roombacomm.playNote(36, 10);  // C1
            roombacomm.pause(200);
        }
        else if("beep-hi".equals(action)) {
            roombacomm.playNote(120, 10);  // C7
            roombacomm.pause(200);
        }
        else if("clean".equals(action)) {
            roombacomm.clean();
        }
        else if("spot".equals(action)) {
            roombacomm.spot();
        }
        else if("vacuum-on".equals(action)) {
            roombacomm.vacuum(true);
        }
        else if("vacuum-off".equals(action)) {
            roombacomm.vacuum(false);
        }
        else if("blink-leds".equals(action)) {
            roombacomm.setLEDs(true,true,true, true,true,true, 255, 255);
            roombacomm.pause(300);
            roombacomm.setLEDs(false,false,false, false,false,false, 0, 128);
        }        
        else if("sensors".equals(action)) {
            if(roombacomm.updateSensors())
                updateDisplay(roombacomm.sensorsAsString()+"\n");
            else 
                updateDisplay("couldn't read Roomba. Is it connected?\n");
        }
    }

    /**
     *
     */
    void makeSelectPanel() {
        selectPanel  = new JPanel();
        //Create a combo box with choices.
        String[] ports = roombacomm.listPorts();
        portChoices = new JComboBox(ports);
        portChoices.setSelectedIndex(0);
        connectButton = new JButton();
        connectButton.setText("  connect  ");
        connectButton.setActionCommand("connect");
        handshakeButton = new JCheckBox("<html>h/w<br>handshake</html>");
        
        //Add a border around the select panel.
        selectPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Select Roomba Port"), BorderFactory.createEmptyBorder()));
        
        selectPanel.add(portChoices);
        selectPanel.add(connectButton);
        selectPanel.add(handshakeButton);

        //Listen to events from the combo box.
        portChoices.addActionListener(this);
        connectButton.addActionListener(this);
    }
    
    /** 
     * 
     */
    void makeCtrlPanel() {
        JPanel ctrlPanel1 = new JPanel(new GridLayout(3,3));

        JButton but_turnleft =
            new JButton(createImageIcon("images/but_turnleft.png","turnleft"));
        ctrlPanel1.add(but_turnleft);
        JButton but_forward =
            new JButton(createImageIcon("images/but_forward.png","forward"));
        ctrlPanel1.add(but_forward);
        JButton but_turnright =
            new JButton(createImageIcon("images/but_turnright.png","turnright"));
        ctrlPanel1.add(but_turnright);

        JButton but_spinleft =
            new JButton(createImageIcon("images/but_spinleft.png","spinleft"));
        ctrlPanel1.add(but_spinleft);
        JButton but_stop =
            new JButton(createImageIcon("images/but_stop.png", "stop"));
        ctrlPanel1.add(but_stop);
        JButton but_spinright =
            new JButton(createImageIcon("images/but_spinright.png","spinright"));
        ctrlPanel1.add(but_spinright);

        ctrlPanel1.add(new JLabel());
        JButton but_backward =
            new JButton(createImageIcon("images/but_backward.png","backward"));
        ctrlPanel1.add(but_backward);
        ctrlPanel1.add(new JLabel());

        JLabel speedLabel = new JLabel("speed (mm/s)", JLabel.CENTER);
        JRadioButton speed050button = new JRadioButton("50");
        JRadioButton speed100button = new JRadioButton("100");
        JRadioButton speed150button = new JRadioButton("150");
        JRadioButton speed250button = new JRadioButton("250");
        JRadioButton speed400button = new JRadioButton("400");
        JRadioButton speed500button = new JRadioButton("500");
        speed050button.setVerticalTextPosition(SwingConstants.BOTTOM);
        speed050button.setHorizontalTextPosition(SwingConstants.CENTER);
        speed100button.setVerticalTextPosition(SwingConstants.BOTTOM);
        speed100button.setHorizontalTextPosition(SwingConstants.CENTER);
        speed150button.setVerticalTextPosition(SwingConstants.BOTTOM);
        speed150button.setHorizontalTextPosition(SwingConstants.CENTER);
        speed250button.setVerticalTextPosition(SwingConstants.BOTTOM);
        speed250button.setHorizontalTextPosition(SwingConstants.CENTER);
        speed400button.setVerticalTextPosition(SwingConstants.BOTTOM);
        speed400button.setHorizontalTextPosition(SwingConstants.CENTER);
        speed500button.setVerticalTextPosition(SwingConstants.BOTTOM);
        speed500button.setHorizontalTextPosition(SwingConstants.CENTER);

        speed250button.setSelected(true);

        speed050button.addActionListener(this);
        speed100button.addActionListener(this);
        speed150button.addActionListener(this);
        speed250button.addActionListener(this);
        speed400button.addActionListener(this);
        speed500button.addActionListener(this);

        ButtonGroup speedGroup = new ButtonGroup();
        speedGroup.add(speed050button);
        speedGroup.add(speed100button);
        speedGroup.add(speed150button);
        speedGroup.add(speed250button);
        speedGroup.add(speed400button);
        speedGroup.add(speed500button);

        JPanel ctrlPanel2 = new JPanel();
        ctrlPanel2.add(speed050button);
        ctrlPanel2.add(speed100button);
        ctrlPanel2.add(speed150button);
        ctrlPanel2.add(speed250button);
        ctrlPanel2.add(speed400button);
        ctrlPanel2.add(speed500button);

        ctrlPanel = new JPanel();
        ctrlPanel.setLayout(new BoxLayout(ctrlPanel, BoxLayout.Y_AXIS));
        ctrlPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Movement"), BorderFactory.createEmptyBorder()));//5,5,5,5)));
        ctrlPanel.add(ctrlPanel1);
        ctrlPanel.add(ctrlPanel2);
        ctrlPanel.add(speedLabel);

        but_turnleft.setActionCommand("turnleft");
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

    /**
     *
     */
    void makeButtonPanel() {
        buttonPanel = new JPanel(new GridLayout(8,2));
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Commands"), BorderFactory.createEmptyBorder()));//5,5,5,5)));

        JButton but_reset = new JButton("reset");
        JButton but_test = new JButton("test");
        JButton but_power = new JButton("power-off");
        JButton but_wakeup = new JButton("wakeup");
        JButton but_beeplo = new JButton("beep-lo");
        JButton but_beephi = new JButton("beep-hi");
        JButton but_clean = new JButton("clean");
        JButton but_spot = new JButton("spot");
        JButton but_vacon = new JButton("vacuum-on");
        JButton but_vacoff = new JButton("vacuum-off");
        JButton but_blinkleds = new JButton("blink-leds");
        JButton but_sensors = new JButton("sensors");

        buttonPanel.add(but_reset);
        buttonPanel.add(but_test);
        buttonPanel.add(but_power);
        buttonPanel.add(but_wakeup);
        buttonPanel.add(but_beeplo);
        buttonPanel.add(but_beephi);
        buttonPanel.add(but_clean);
        buttonPanel.add(but_spot);
        buttonPanel.add(but_vacon);
        buttonPanel.add(but_vacoff);
        buttonPanel.add(but_blinkleds);
        buttonPanel.add(but_sensors);

        but_reset.addActionListener(this);
        but_test.addActionListener(this);
        but_power.addActionListener(this);
        but_wakeup.addActionListener(this);
        but_beeplo.addActionListener(this);
        but_beephi.addActionListener(this);
        but_clean.addActionListener(this);
        but_spot.addActionListener(this);
        but_vacon.addActionListener(this);
        but_vacoff.addActionListener(this);
        but_blinkleds.addActionListener(this);
        but_sensors.addActionListener(this);
    }

    /**
     *
     */
    void makeDisplayPanel() {
        displayPanel = new JPanel();
        displayPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Display"), BorderFactory.createEmptyBorder()));//1,1,1,1)));

        displayText = new JTextArea(5,30);
        displayText.setEditable(false);
        displayText.setLineWrap(true);
        DefaultCaret dc = new DefaultCaret();
        // only works on Java 1.5+
        //dc.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        displayText.setCaret(dc);
        JScrollPane scrollPane = 
            new JScrollPane(displayText, 
                            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        displayPanel.add(scrollPane);
    }
    
    public void updateDisplay(String s) {
        displayText.append(s);
        displayText.setCaretPosition(displayText.getDocument().getLength());
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

    public int millis() {
        int millisOffset = 0;
        return (int) (System.currentTimeMillis() - millisOffset);
    }

}
