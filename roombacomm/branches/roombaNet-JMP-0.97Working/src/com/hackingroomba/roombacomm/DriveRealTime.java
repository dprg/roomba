/*
 * roombacomm.Drive -- test out the DRIVE command
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
   Drive the Roomba with the arrow keys in real-time
  <p>
   Run it with something like: <pre>
    java roombacomm.DriveRealTime /dev/cu.KeySerial1<br>
   Usage: 
   roombacomm.DriveRealTime serialportname [protocol] [options]<br>
   where: 
   protocol (optional) is SCI or OI 
   [options] can be one or more of:
   -debug       -- turn on debug output
   -hwhandshake -- use hardware-handshaking, for Windows Bluetooth
   -nohwhandshake -- don't use hardware-handshaking
   </pre>
*/ 
public class DriveRealTime extends JFrame implements KeyListener {

    static String usage = 
        "Usage: \n"+
        "  roombacomm.DriveRealTime <serialportname> [protocol] [options] \n" +
        "where: protocol (optional) is SCI or OI\n" +
        "[options] can be one or more of:\n"+
        " -debug       -- turn on debug output\n"+
        " -hwhandshake -- use hardware-handshaking, for Windows Bluetooth\n"+
        "-nohwhandshake -- don't use hardware-handshaking\n"+
        "\n";
    static boolean debug = false;
    static boolean hwhandshake = false;
    
    RoombaCommSerial roombacomm;
    JTextArea displayText;

    public static void main(String[] args) {
        if( args.length < 1 ) {
            System.out.println( usage );
            System.exit(0);
        }
        new DriveRealTime(args);
    }
    
    public DriveRealTime(String[] args) {
        super("DriveRealTime");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        String portname = args[0];
        roombacomm = new RoombaCommSerial();
        for( int i=1; i < args.length; i++ ) {
        	if (args[i].equals("SCI") || (args[i].equals("OI"))) {
        		roombacomm.setProtocol(args[i]);
        	} else if( args[i].endsWith("debug") )
                debug = true;
            else if( args[i].endsWith("nohwhandshake") )
                roombacomm.setWaitForDSR(false);
            else if( args[i].endsWith("hwhandshake") )
                roombacomm.setWaitForDSR(true);
        }

        roombacomm.debug = debug;

        if( ! roombacomm.connect( portname ) ) {
            System.out.println("Couldn't connect to "+portname);
            System.exit(1);
        }      
        
        System.out.println("Roomba startup");
        roombacomm.startup();
        roombacomm.control();
        roombacomm.pause(50);

        setupWindow();

        updateDisplay("click on this window\nthen use arrow keys to drive Roomba around.\n");
    }

    /** Handle the key pressed event from the text field. */
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if(      keyCode == KeyEvent.VK_SPACE ) {
            updateDisplay("stop");
            roombacomm.stop();
        }
        else if( keyCode == KeyEvent.VK_UP ) {
            updateDisplay("forward");
            roombacomm.goForward();
        }
        else if( keyCode == KeyEvent.VK_DOWN ) {
            updateDisplay("backward");
            roombacomm.goBackward();
        }
        else if( keyCode == KeyEvent.VK_LEFT ) {
            updateDisplay("spinleft");
            roombacomm.spinLeft();
        }
        else if( keyCode == KeyEvent.VK_RIGHT ) {
            updateDisplay("spinright");
            roombacomm.spinRight();
        }
        else if( keyCode == KeyEvent.VK_COMMA ) {
            updateDisplay("speed down");
            roombacomm.setSpeed( roombacomm.getSpeed() - 50 );
        }
        else if( keyCode == KeyEvent.VK_PERIOD ) {
            updateDisplay("speed up");
            roombacomm.setSpeed( roombacomm.getSpeed() + 50 );
        }
        else if( keyCode == KeyEvent.VK_R ) {
            updateDisplay("reset");
            roombacomm.reset();
            roombacomm.control();
        }
    }

    /** Handle the key released event from the text field. */
    public void keyReleased(KeyEvent e) {
    }

    /** Handle the key typed event from the text field. */
    public void keyTyped(KeyEvent e) {
    }

    /** a sort of gui equivalent to system.out.println */
    public void updateDisplay( String s ) {
        displayText.append( s+"\n" );
        displayText.setCaretPosition(displayText.getDocument().getLength());
    }
    
    /**
     */
    public void setupWindow() {
        displayText = new JTextArea(20,30);
        displayText.setLineWrap(true);
        displayText.setEditable(false);
        displayText.addKeyListener(this);
        JScrollPane scrollPane = new JScrollPane(displayText, 
                            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        Container content = getContentPane();
        content.add( scrollPane, BorderLayout.CENTER );
        addKeyListener(this);
        pack();
        setResizable(false);
        setVisible(true);
    }
}
