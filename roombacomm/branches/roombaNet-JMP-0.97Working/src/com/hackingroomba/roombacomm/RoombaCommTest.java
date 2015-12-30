/*
 * RoombaCommtest  -- small GUI to test out RoombaComm
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

/**
 * A simple wrapper for RoombaCommPanel.
 *
 */
public class RoombaCommTest extends JFrame implements WindowListener {

    RoombaCommPanel rcPanel;
    boolean hwhandshake = false;
    boolean debug = false;

    public static void main(String[] args) {
        new RoombaCommTest(args);
    }

    public RoombaCommTest(String[] args) {
        super("RoombaCommTest");
        addWindowListener(this);

        for( int i=0; i < args.length; i++ ) {
            if( args[i].endsWith("hwhandshake") )
                hwhandshake = true;
            else if (args[i].endsWith("debug"))
            	debug = true;
        }

        rcPanel = new RoombaCommPanel(debug);

        rcPanel.setShowHardwareHandhake( hwhandshake );

        Container content = getContentPane();
        //content.setBackground(Color.lightGray);
        content.add( rcPanel ); // , BorderLayout.CENTER );

        setResizable(false);
        pack();
        setVisible(true);
    }


    /** implement windowlistener */
    public void windowClosing(WindowEvent event) {
        rcPanel.disconnect();
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

}

