//
// RoombaCommtest  -- small GUI to test out RoombaComm
//
// Tod E. Kurt, tod@todbot.com
//
//

package com.hackingroomba.roombacomm;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

public class RoombaCommTestOld extends JFrame implements ActionListener,ChangeListener,WindowListener {
    static final String VERSION = "DO NOT USE";

    JPanel ctrlPanel, selectPanel, buttonPanel, displayPanel;
    JComboBox portChoices;
    JTextArea displayText;
    JButton  connectButton;
    JSlider speedSlider;

    RoombaComm roombacomm;

    public static void main(String[] args) {
        new RoombaCommTestOld();
    }

    public RoombaCommTestOld() {
        super("RoombaCommTestOld");
        setResizable(false);
        //setNativeLookAndFeel();
        addWindowListener(this);

        roombacomm = new RoombaCommSerial();

        Container content = getContentPane();
        content.setBackground(Color.lightGray);

        makeSelectPanel();
        content.add( selectPanel, BorderLayout.NORTH );

        makeCtrlPanel();
        content.add( ctrlPanel, BorderLayout.EAST );        

        makeButtonPanel();
        content.add( buttonPanel, BorderLayout.CENTER );

        makeDisplayPanel();
        content.add( displayPanel, BorderLayout.SOUTH );

        pack();
        setVisible(true);
        displayText.append("RoombaCommTestOld, version "+VERSION+"\n");
    }

    /** implement actionlistener */
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        if( "comboBoxChanged".equals(action) ) {
            return;
        }
        displayText.append(action+"\n");
        if( "connect".equals(action) ) {
            roombacomm.debug=true;
            if( roombacomm.connected() ) {
                roombacomm.disconnect();   // just in case
                connectButton.setText("  connect  ");
                return;
            }
            else {
                displayText.append("could not connect...darn\n");
            }
            connectButton.setText("connecting");
            String portname = (String) portChoices.getSelectedItem();
            if( ! roombacomm.connect( portname ) ) {
                displayText.append("Couldn't connect to "+portname+"\n");
                return;
            }
            displayText.append("Roomba startup\n");
            roombacomm.startup();
            roombacomm.control();
            roombacomm.playNote( 72, 10 );  // C
            roombacomm.pause( 200 );
            connectButton.setText("disconnect");
            displayText.append("Roomba connected\n");
            return;
        }

        // stop right here if we're not connected
        if( !roombacomm.connected() ) {
            displayText.append("not connected!\n");
            return;
        }

        if( "stop".equals(action) ) {
            roombacomm.stop();
        }
        else if( "forward".equals(action) ) {
            roombacomm.goForward();
        }
        else if( "backward".equals(action) ) {
            roombacomm.goBackward();
        }
        else if( "spinleft".equals(action) ) {
            roombacomm.spinLeft();
        }
        else if( "spinright".equals(action) ) {
            roombacomm.spinRight();
        }
        else if( "test".equals(action) ) {
            displayText.append("Playing some notes\n");
            roombacomm.playNote( 72, 10 );  // C
            roombacomm.pause( 200 );
            roombacomm.playNote( 79, 10 );  // G
            roombacomm.pause( 200 );
            roombacomm.playNote( 76, 10 );  // E
            roombacomm.pause( 200 );

            displayText.append("Spinning left, then right\n");
            roombacomm.spinLeft();
            roombacomm.pause(1000);
            roombacomm.spinRight();
            roombacomm.pause(1000);
            roombacomm.stop();

            displayText.append("Going forward, then backward\n");
            roombacomm.goForward();
            roombacomm.pause(1000);
            roombacomm.goBackward();
            roombacomm.pause(1000);
            roombacomm.stop();
        }
        else if( "reset".equals(action) ) {
            roombacomm.stop();
            roombacomm.startup();
            roombacomm.control();
        }
        else if( "power-off".equals(action) ) {
            roombacomm.powerOff();
        }
        else if( "wakeup".equals(action) ) {
            roombacomm.wakeup();
        }
        else if( "beep-lo".equals(action) ) {
            roombacomm.playNote( 36, 10 );  // C0
            roombacomm.pause( 200 );
        }
        else if( "beep-hi".equals(action) ) {
            roombacomm.playNote( 120, 10 );  // C7
            roombacomm.pause( 200 );
        }
        else if( "clean".equals(action) ) {
            roombacomm.clean();
        }
        else if( "spot".equals(action) ) {
            roombacomm.spot();
        }
        else if( "vacuum-on".equals(action) ) {
            roombacomm.vacuum(true);
        }
        else if( "vacuum-off".equals(action) ) {
            roombacomm.vacuum(false);
        }
        else if( "sensors".equals(action) ) {
            if( roombacomm.updateSensors() )
                displayText.append( roombacomm.sensorsAsString()+"\n");
            else 
                displayText.append("couldn't read Roomba. Is it connected?\n");
        }
    }

    /** implement ChangeListener, for the slider */
    public void stateChanged(ChangeEvent e) {
        //System.err.println("stateChanged:"+e);
        JSlider src = (JSlider)e.getSource();
        if (!src.getValueIsAdjusting()) {
            int speed = (int)src.getValue();
            speed = (speed<1) ? 1 : speed; // don't allow zero speed
            displayText.append("setting speed = "+speed+"\n");
            roombacomm.setSpeed(speed);
        }
    }

    /** 
     * 
     */
    void makeCtrlPanel() {
        JPanel ctrlPanel1 = new JPanel(new GridLayout(3,3));

        ctrlPanel1.add(new JLabel());
        JButton but_forward =
            new JButton(createImageIcon("images/forward.png", "forward"));
        ctrlPanel1.add( but_forward, BorderLayout.NORTH );
        ctrlPanel1.add(new JLabel());

        JButton but_spinleft =
            new JButton(createImageIcon("images/spinleft.png", "spinleft"));
        ctrlPanel1.add( but_spinleft, BorderLayout.WEST );
        JButton but_stop =
            new JButton(createImageIcon("images/stop.png", "stop"));
        ctrlPanel1.add( but_stop, BorderLayout.CENTER );
        JButton but_spinright =
            new JButton( createImageIcon("images/spinright.png", "spinright"));
        ctrlPanel1.add( but_spinright, BorderLayout.EAST);

        ctrlPanel1.add(new JLabel());
        JButton but_backward =
            new JButton(createImageIcon("images/backward.png", "backward"));
        ctrlPanel1.add( but_backward, BorderLayout.SOUTH );
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

        ctrlPanel.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createTitledBorder("Movement"), BorderFactory.createEmptyBorder(5,5,5,5)));
        ctrlPanel.add( ctrlPanel1 );
        ctrlPanel.add( speedSlider );
        ctrlPanel.add( sliderLabel );

        but_spinleft.setActionCommand("spinleft");
        but_spinright.setActionCommand("spinright");
        but_forward.setActionCommand("forward");
        but_backward.setActionCommand("backward");
        but_stop.setActionCommand("stop");
        but_spinleft.addActionListener(this);
        but_spinright.addActionListener(this);
        but_forward.addActionListener(this);
        but_backward.addActionListener(this);
        but_stop.addActionListener(this);
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

        //Add a border around the select panel.
        selectPanel.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createTitledBorder("Select Roomba Port"), BorderFactory.createEmptyBorder(5,5,5,5)));

        selectPanel.add(portChoices);
        selectPanel.add(connectButton);

        //Listen to events from the combo box.
        portChoices.addActionListener(this);
        connectButton.addActionListener(this);
    }

    /**
     *
     */
    void makeButtonPanel() {
        //buttonPanel = new JPanel();
        //buttonPanel.setLayout( new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel = new JPanel( new GridLayout( 8,2 ) );
        buttonPanel.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createTitledBorder("Commands"), BorderFactory.createEmptyBorder(5,5,5,5)));

        JButton but_reset = new JButton("reset");
        buttonPanel.add( but_reset );
        but_reset.addActionListener(this);

        JButton but_test = new JButton("test");
        buttonPanel.add( but_test );
        but_test.addActionListener(this);

        JButton but_power = new JButton("power-off");
        buttonPanel.add( but_power );
        but_power.addActionListener(this);

        JButton but_wakeup = new JButton("wakeup");
        buttonPanel.add( but_wakeup );
        but_wakeup.addActionListener(this);

        JButton but_beeplo = new JButton("beep-lo");
        buttonPanel.add( but_beeplo );
        but_beeplo.addActionListener(this);
        
        JButton but_beephi = new JButton("beep-hi");
        buttonPanel.add( but_beephi );
        but_beephi.addActionListener(this);

        JButton but_clean = new JButton("clean");
        buttonPanel.add( but_clean );
        but_clean.addActionListener(this);

        JButton but_spot = new JButton("spot");
        buttonPanel.add( but_spot );
        but_spot.addActionListener(this);

        JButton but_vacon = new JButton("vacuum-on");
        buttonPanel.add( but_vacon );
        but_vacon.addActionListener(this);

        JButton but_vacoff = new JButton("vacuum-off");
        buttonPanel.add( but_vacoff );
        but_vacoff.addActionListener(this);

        JButton but_sensors = new JButton("sensors");
        buttonPanel.add( but_sensors );
        but_sensors.addActionListener(this);
    }

    /**
     *
     */
    void makeDisplayPanel() {
        displayPanel = new JPanel();
        displayPanel.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createTitledBorder("Display"), BorderFactory.createEmptyBorder(1,1,1,1)));

        displayText = new JTextArea(5,30);
        displayText.setLineWrap(true);
        DefaultCaret dc = new DefaultCaret();
        // only works on Java 1.5+
        // dc.setUpdatePolicy( DefaultCaret.ALWAYS_UPDATE );
        displayText.setCaret(dc);
        JScrollPane scrollPane = 
            new JScrollPane(displayText, 
                            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        displayPanel.add( scrollPane );

    }
    
    /** implement windowlistener */
    public void windowClosing(WindowEvent event) {
        if( roombacomm.connected() ) 
            roombacomm.disconnect();
        System.exit(0);
    }
    /** implement windowlistener */
    public void windowClosed(WindowEvent event) {
        // do nothing
    }
    /** implement windowlistener */
    public void windowOpened(WindowEvent event) {
        // do nothing
    }
    /** implement windowlistener */
    public void windowActivated(WindowEvent event) {
        // do nothing
    }
    /** implement windowlistener */
    public void windowDeactivated(WindowEvent event) {
        // do nothing
    }
    /** implement windowlistener */
    public void windowIconified(WindowEvent event) {
        // do nothing
    }
    /** implement windowlistener */
    public void windowDeiconified(WindowEvent event) {
        // do nothing
    }

       
    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path,
                                               String description) {
        java.net.URL imgURL = RoombaCommTestOld.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    

}
