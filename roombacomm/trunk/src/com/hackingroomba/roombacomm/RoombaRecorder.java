/*
 * RoombaRecorder  -- 
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
import javax.swing.filechooser.*;
import java.util.*;
import java.io.*;

/**
 * A simple wrapper for multiple MacroRecorderPanels
 *
 */
public class RoombaRecorder extends JFrame implements WindowListener,ActionListener {

    static final String helpMsg = "<html>"+
        //"<h2> </h2>"+
        "<h2> Roomba Movement Keyboard Shortcuts </h2>"+
        "<ul>"+
        "<li> arrow keys -- move Roomba"+
        "<li> space bar  -- stop Roomba"+
        "<li> L -- blink Roomba LEDs"+
        "<li> V -- toggle Roomba vacuum"+
        "<li> T -- test Roomba"+
        "<li> R -- reset Roomba"+
        "<li> 1,2,3,4 -- adjust speed"+
        "</ul>"+
        "<h2> Application Control Keyboard Shortcuts </h2>"+
        "<ul>"+
        "<li> cmd-T -- open new tab"+
        "<li> cmd-W -- close current tab"+
        "<li> cmd-arrow-left/right -- cycle through tabs"+
        "<li> cmd-X/C/V -- cut/copy/paste events between tabs"+
        "</ul>"+
        "</html>";

    JPanel contentPane;
    JPanel recordPanel;
    JTabbedPane tabbedPane;
    JButton stopButton,recordButton,playButton;
    JCheckBox loopButton;
    JCheckBox playAllButton;
    JFileChooser fc;
    File file;
    boolean hwhandshake = false;
    int maxRoombas = 16;
    int lastTab = 0;
    HashMap clipboard;
    RoombaRecorderPanel rrpanel;  // just to make some of code lines shorter
    boolean movingForward = false;  // is roomba moving forward or not
    boolean vacuuming = false;  // is roomba vacuuming or not
    int turnval = 0;

    public static void main(String[] args) {
        new RoombaRecorder(args);
    }

    public RoombaRecorder(String[] args) {
        super("RoombaRecorder");
        addWindowListener(this);

        for(int i=0; i < args.length; i++) {
            if(args[i].endsWith("hwhandshake"))
                hwhandshake = true;
        }

        clipboard = new HashMap();
        fc = new JFileChooser();

        setResizable(false);
	
        makeMenu();
        
        makeRecordPanel();

        // Create and set up the content pane.
        tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(true); //content panes must be opaque
        openNewTab();
        
        contentPane = new JPanel(new BorderLayout());

        contentPane.add(recordPanel, BorderLayout.NORTH);
        contentPane.add(tabbedPane, BorderLayout.CENTER);

        //Container content = getContentPane();
        setContentPane(contentPane);

        addBindings();
        bindKeys();

        // Display the window.
        pack();          //setSize(400, 400);
        setVisible(true); 

        //setFocusableWindowState(false); // prevent focus
    }

    void makeMenu() {
        JMenuBar menubar = new JMenuBar();
        JMenuItem item;
        
        JMenu file = new JMenu("File");
        JMenu edit = new JMenu("Edit");
        JMenu help = new JMenu("Help");
        
        file.setMnemonic(KeyEvent.VK_F);
        edit.setMnemonic(KeyEvent.VK_E);
        help.setMnemonic(KeyEvent.VK_H);
        
        item = new JMenuItem("New Tab");
        item.addActionListener(this);
        file.add(item);
        item = new JMenuItem("Close Tab");
        item.addActionListener(this);
        file.add(item);
        item = new JMenuItem("Open Set");
        item.addActionListener(this);
        file.add(item);
        item = new JMenuItem("Save Set");
        item.addActionListener(this);
        file.add(item);
        item = new JMenuItem("Quit");
        item.addActionListener(this);
        file.add(item);
        
        item = new JMenuItem("Cut");
        item.addActionListener(this);
        edit.add(item);
        item = new JMenuItem("Copy");
        item.addActionListener(this);
        edit.add(item);
        item = new JMenuItem("Paste");
        item.addActionListener(this);
        edit.add(item);
        
        item = new JMenuItem("Help");
        item.addActionListener(this);
        help.add(item);

        menubar.add(file);
        menubar.add(edit);
        menubar.add(help);

        setJMenuBar(menubar);
    }

    void makeRecordPanel() {
        recordPanel = new JPanel();
        recordPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Action Record"), BorderFactory.createEmptyBorder())); //1,1,1,1)));
        stopButton = new JButton(createImageIcon("images/but_transport_stop.png","stop"));
        stopButton.setActionCommand("stop");
        stopButton.addActionListener(this);
        recordPanel.add(stopButton);
        playButton = new JButton(createImageIcon("images/but_transport_play.png","play"));
        playButton.setSelectedIcon(createImageIcon("images/but_transport_play_push.png","play"));
        playButton.setActionCommand("play");
        playButton.addActionListener(this);
        recordPanel.add(playButton);
        recordButton = new JButton(createImageIcon("images/but_transport_record.png","record"));
        recordButton.setSelectedIcon(createImageIcon("images/but_transport_record_push.png","record"));
        recordButton.setActionCommand("record");
        recordButton.addActionListener(this);
        recordPanel.add(recordButton);

        loopButton = new JCheckBox("loop");
        loopButton.setText("loop");
        loopButton.setActionCommand("loop");
        loopButton.addActionListener(this);
        recordPanel.add(loopButton);

        playAllButton = new JCheckBox("play all");
        playAllButton.addActionListener(this);
        recordPanel.add(playAllButton);
    }

    void addBindings() {
        InputMap inputMap = contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        KeyStroke key;

        //key = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,0);
        //contentPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(key);

        key = KeyStroke.getKeyStroke('T',KeyEvent.CTRL_MASK);
        inputMap.put(key, "new-tab");
        key = KeyStroke.getKeyStroke('T',KeyEvent.META_MASK);
        inputMap.put(key, "new-tab");
        key = KeyStroke.getKeyStroke('N',KeyEvent.CTRL_MASK);
        inputMap.put(key, "new-tab");
        key = KeyStroke.getKeyStroke('N',KeyEvent.META_MASK);
        inputMap.put(key, "new-tab");

        key = KeyStroke.getKeyStroke('W',KeyEvent.CTRL_MASK);
        inputMap.put(key, "close-tab");
        key = KeyStroke.getKeyStroke('W',KeyEvent.META_MASK);
        inputMap.put(key, "close-tab");

        key = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,KeyEvent.CTRL_MASK);
        inputMap.put(key, "right-tab");
        key = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,KeyEvent.CTRL_MASK);
        inputMap.put(key, "left-tab");
        key = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,KeyEvent.META_MASK);
        inputMap.put(key, "right-tab");
        key = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,KeyEvent.META_MASK);
        inputMap.put(key, "left-tab");

        key = KeyStroke.getKeyStroke('X',KeyEvent.CTRL_MASK);
        inputMap.put(key, "cut");
        key = KeyStroke.getKeyStroke('X',KeyEvent.META_MASK);
        inputMap.put(key, "cut");
        key = KeyStroke.getKeyStroke('C',KeyEvent.CTRL_MASK);
        inputMap.put(key, "copy");
        key = KeyStroke.getKeyStroke('C',KeyEvent.META_MASK);
        inputMap.put(key, "copy");
        key = KeyStroke.getKeyStroke('V',KeyEvent.CTRL_MASK);
        inputMap.put(key, "paste");
        key = KeyStroke.getKeyStroke('V',KeyEvent.META_MASK);
        inputMap.put(key, "paste");

        ActionMap actionMap = contentPane.getActionMap();
        actionMap.put("new-tab", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    openNewTab();
                } });
        actionMap.put("close-tab",new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    closeCurrentTab();
                } });
        actionMap.put("left-tab",new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    cycleTabLeft();
                } });
        actionMap.put("right-tab",new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    cycleTabRight();
                } });
        actionMap.put("cut",new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    clipboard = getCurrentTab().cut();
                } });
        actionMap.put("copy",new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    clipboard = getCurrentTab().copy();
                } });
        actionMap.put("paste",new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    getCurrentTab().paste(clipboard);
                } });
    }

    /**
     * Bind keys to actions using a custom KeyEventPostProcessor
     * (fixme: why can't this go in the panel?)
     */
    void bindKeys() {
        // ahh, the succinctness of java
        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();

        kfm.addKeyEventDispatcher( new KeyEventDispatcher() {
                public boolean dispatchKeyEvent(KeyEvent e) {
                    String action=null;
                    if(e.getID() != KeyEvent.KEY_PRESSED) 
                        return false;
                    if(e.getModifiers() != 0)
                        return false;
                    switch(e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        action = "forward";
                        movingForward=true;
                        turnval = 0;
                        break;
                    case KeyEvent.VK_DOWN:
                        action="backward";
                        movingForward=false;
                        turnval = 0;
                        break;
                    case KeyEvent.VK_LEFT:
                        if(movingForward) {
                            turnval = (turnval==0) ? 100 : turnval-turnval/2;
                            if(turnval<10) { action="spinleft"; turnval=0; }
                            else action = "turn"+turnval;
                        }
                        else action = "spinleft";
                        //action = (movingForward) ? "turn100" : "spinleft";
                        break;
                    case KeyEvent.VK_RIGHT:
                        if(movingForward) {
                            turnval = (turnval==0) ? -100 : turnval-turnval/2;
                            if(turnval>-10) { action="spinright"; turnval=0; }
                            else action = "turn"+turnval;
                        }
                        else action = "spinright";
                        //action = (movingForward) ? "turn-100" : "spinright";
                        break;
                    case KeyEvent.VK_SPACE:
                        action="stop";
                        movingForward=false;
                        turnval = 0;
                        break;
                    case KeyEvent.VK_L:
                        action="blink-leds";
                        break;
                    case KeyEvent.VK_R:
                        action="reset";
                        break;
                    case KeyEvent.VK_T:
                        action="test";
                        break;
                    case KeyEvent.VK_V:
                        vacuuming = !vacuuming;
                        action = (vacuuming) ? "vacuum-on":"vacuum-off";
                        break;
                    case KeyEvent.VK_1:
                        action="50";
                        break;
                    case KeyEvent.VK_2:
                        action="100";
                        break;
                    case KeyEvent.VK_3:
                        action="150";
                        break;
                    case KeyEvent.VK_4:
                        action="250";
                        break;
                    case KeyEvent.VK_5:
                        action="400";
                        break;
                    case KeyEvent.VK_6:
                        action="500";
                        break;
                    }

                    if(action!=null)
                        getCurrentTab().actionPerformed(new ActionEvent(this,0,action));
                    //System.out.println("process '"+e.getKeyCode()+"' "+e);
                    return true;
                } 
            });
    }

    /** Implement Actionlistener */
    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        //System.out.println("action:"+action+", event: "+event);
        if("stop".equals(action)) {
            playButton.setSelected(false);
            recordButton.setSelected(false);
            for(int i=0; i<tabbedPane.getTabCount(); i++) {
                rrpanel = (RoombaRecorderPanel)tabbedPane.getComponentAt(i);
                rrpanel.stop();
            }
        }
        else if("play".equals(action)) {
            playButton.setSelected(true);
            recordButton.setSelected(false);
            if( !playAllButton.isSelected() ) {
                boolean playing = getCurrentTab().play(); // FIXME: hack
                if(!playing) playButton.setSelected(false);
            } else {
                for(int i=0; i<tabbedPane.getTabCount(); i++) {
                    rrpanel= (RoombaRecorderPanel)tabbedPane.getComponentAt(i);
                    rrpanel.play();
                }
            }
        }
        else if("record".equals(action)) {
            playButton.setSelected(false);
            recordButton.setSelected(true);
            if( !playAllButton.isSelected() ) {
                getCurrentTab().record();
            } else {
                int ir = tabbedPane.getSelectedIndex();
                for(int i=0; i<tabbedPane.getTabCount(); i++) {
                    rrpanel= (RoombaRecorderPanel)tabbedPane.getComponentAt(i);
                    if( i!=ir ) rrpanel.play();
                    else rrpanel.record();
                }
            }
        }
        else if("loop".equals(action)) {
            for(int i=0; i<tabbedPane.getTabCount(); i++) {
                rrpanel= (RoombaRecorderPanel)tabbedPane.getComponentAt(i);
                rrpanel.setLooping(loopButton.isSelected());
            }
        }
        else if("New Tab".equals(action)) {
            openNewTab();
        }
        else if("Close Tab".equals(action)) {
            closeCurrentTab();
        }
        else if("Open Set".equals(action)) {
            loadSet();
        }
        else if("Save Set".equals(action)) {
            saveSet();
        }
        else if("Save Set As...".equals(action)) {
            file = null;
            saveSet();
        }
        else if("Quit".equals(action)) {
            closeAllTabs();
            windowClosing(null);
        }
        else if("Cut".equals(action)) {
            clipboard = getCurrentTab().cut();
        }
        else if("Copy".equals(action)) {
            clipboard = getCurrentTab().copy();
        }
        else if("Paste".equals(action)) {
            getCurrentTab().paste(clipboard);
        }
        else if("Help".equals(action)) {
            JOptionPane.showMessageDialog(null,new JLabel(helpMsg));
        }
    }

    public RoombaRecorderPanel getCurrentTab() {
        return (RoombaRecorderPanel) tabbedPane.getComponentAt(tabbedPane.getSelectedIndex());
    }

    public void openNewTab() {
        int i = tabbedPane.getTabCount();
        if(i < maxRoombas) {  // only 16 roombas supported
            rrpanel = new RoombaRecorderPanel();
            rrpanel.setShowHardwareHandhake(hwhandshake);
            lastTab = lastTab+1;
            tabbedPane.addTab("Roomba #"+lastTab, null, rrpanel);
            tabbedPane.setSelectedIndex(lastTab-1);
        }
    }
    public void closeAllTabs() {
        int c = tabbedPane.getTabCount();
        for( int i=0; i<c; i++)
            closeCurrentTab();
        lastTab = 0;
    }
    public void closeCurrentTab() {
        int i = tabbedPane.getSelectedIndex();
        rrpanel = (RoombaRecorderPanel)tabbedPane.getComponentAt(i);
        rrpanel.disconnect();
        tabbedPane.remove(i);
    }

    public void cycleTabLeft() {
        int i = tabbedPane.getSelectedIndex() - 1;
        if(i<0) i = tabbedPane.getTabCount() - 1;
        tabbedPane.setSelectedIndex(i);
        loopButton.setSelected( getCurrentTab().getLooping() );
    }
    public void cycleTabRight() {
        int i = tabbedPane.getSelectedIndex() + 1;
        if(i==tabbedPane.getTabCount()) i = 0;
        tabbedPane.setSelectedIndex(i);
    }
    
    /**
     * Load a save set from a chosen file
     */
    void loadSet() {
        int returnVal = fc.showOpenDialog(this);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            System.out.println("Open command cancelled by user.");
            return;
        }
        file = fc.getSelectedFile();
        System.out.println("Opening: " + file.getName());
        closeAllTabs();
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            //loopButton.setSelected( ois.readObject() ); 
            ArrayList l = (ArrayList) ois.readObject();
            System.out.println("read "+l.size()+" thingies");
            ois.close();
            for(int i=0; i<l.size(); i++) {
                HashMap m = (HashMap) l.get(i);
                openNewTab();
                getCurrentTab().paste(m);
            }
        } catch(Exception e) {
            System.out.println("Open error "+e);
        }
    }

    /**
     * Save the current set to a file.
     * Will ask for a filename if one hasn't been chosen.
     */
    void saveSet() {
        if( file==null ) {
            int returnVal = fc.showSaveDialog(this);
            if (returnVal != JFileChooser.APPROVE_OPTION) {
                System.out.println("Open command cancelled by user.");
                return;
            }
            file = fc.getSelectedFile();
        }
        System.out.println("Saving to: " + file.getName());
        try {
            int count = tabbedPane.getTabCount();
            ArrayList l = new ArrayList();
            for(int i=0; i< count; i++) {
                rrpanel= (RoombaRecorderPanel)tabbedPane.getComponentAt(i);
                l.add(rrpanel.copy());
            }
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            //oos.writeObject(loopButton);
            //oos.writeObject(
            oos.writeObject(l);
            oos.close();
        } catch( Exception e ) {
            System.out.println("Save error "+e);
        }
    }

    /** implement windowlistener */
    public void windowClosing(WindowEvent event) {
      //rcPanel.disconnect();
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
        // yes, this is supposed to say "RoombaCommTest"
        java.net.URL imgURL = RoombaCommPanel.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }


}

