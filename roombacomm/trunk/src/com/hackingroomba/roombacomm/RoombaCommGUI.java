/*
 * RoombaCommGUI  -- GUI to test out RoombaComm
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

/**
 * A simple wrapper for RoombaCommFrame.
 * 
 * This class shows someone how to use RoombaCommFrame from the "outside".
 *  AKA: No Java Swing skills needed for this class. (is the goal)
 * 
 * SVN id value is $Id$
 */
import jargs.examples.gnu.AutoHelpParser;
//TODO: CMB: add support for different initial GUI sizes (640(w)x480(H),1024x768... maybe others)
import jargs.gnu.CmdLineParser;

/**
 * This is an example class that will demonstrate how to use RoombaCommFrame.java.<br />
 * It also demonstrates a use of jargs to support command line parsing for this class.
 * 
 */
public class RoombaCommGUI  {
    
    /** The hwhandshake. */
    boolean hwhandshake = false;
    
    /** The debug boolean will increase STDOUT to show details about the process at runtime. */
    boolean debug = false;

    /**
     * The main method simply instantiates an instance of this class and passes in any command line arguments to that class.<br />
     * See RoombaCommGUI(java.lang.String[] args) for details about Command Line Interface (CLI) values 
     * 
     * @param args the arguments  
     */
    public static void main(String[] args) {        
        new RoombaCommGUI(args);
    }
    
    /**
     * Instantiates a new RoombaComm GUI without any args.
     */
    public RoombaCommGUI() {
    	this(new String[0]);
    }

	/**
	 * Instantiates a new RoombaComm GUI with any args by use of jargs.
	 * 
	 * @param args the args<br />
	 *        --hwhandshake     boolean value (true,false)<br />
	 *        -d,--debug        boolean value to increase STDOUT<br />
	 *        -h,--help         print usage input and exit<br />
	 */
	public RoombaCommGUI(String[] args) {
		AutoHelpParser parser = new AutoHelpParser();
    	CmdLineParser.Option hwhandshake_opt = parser.addHelp(parser.addBooleanOption("hwhandshake"),"Use this to turn on the hardware hand shake");
    	CmdLineParser.Option debug_opt = parser.addHelp(parser.addBooleanOption('d', "debug"),"output more information at runtime");
        CmdLineParser.Option help_opt = parser.addHelp(parser.addBooleanOption('h', "help"),"Show this help message");
        if (args != null){
        	String[] org_args = args;
        	try {
                parser.parse(args);
            }
            catch ( CmdLineParser.OptionException e ) {
                System.err.println(e.getMessage());
                parser.printUsage();
                System.exit(2);
            }
            if ( Boolean.TRUE.equals(parser.getOptionValue(hwhandshake_opt))) {
            	hwhandshake = true;
            }
            if ( Boolean.TRUE.equals(parser.getOptionValue(debug_opt))) {
            	debug = true;
            }
            if (debug){
            	for( int i=0; i < org_args.length; i++ ) {
    				System.out.println(i+": "+org_args[i]);
    			}
            }
            if ( Boolean.TRUE.equals(parser.getOptionValue(help_opt))) {
                parser.printUsage();
                System.exit(0);
            } 
		}
        // Schedule a job for the event dispatch thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	createAndShowGUI(debug);
            }
        });
    }
    
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the event dispatch thread.
     * 
     * @param debug to increase STDOUT at runtime
     */
    private static void createAndShowGUI(boolean debug) {
    	RoombaCommFrame rcPanel = new RoombaCommFrame(debug);
    	rcPanel.setResizable(true);
    	rcPanel.pack();
    	rcPanel.setVisible(true);
    }      
}