/*
 * Copyright (c) 1995 - 2008 Sun Microsystems, Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

//package layout;
/*
 * GridBagLayoutDemo.java requires no other files.
 */

import java.awt.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.Dimension;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

public class GridBagLayoutDemo {
	final static boolean shouldFill = true;

	final static boolean shouldWeightX = true;

	final static boolean RIGHT_TO_LEFT = false;

	private static JPanel jContentPane = null;  //  @jve:decl-index=0:visual-constraint="10,298"

	private static JButton button1;

	private static JButton button2;

	private static JButton button3;

	private static JButton button4;

	private static JButton button5;

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("GridBagLayoutDemo");
		frame.setLayout(new GridBagLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setSize(new Dimension(420, 278));
		frame.setContentPane(getJContentPane());
		// Set up the content pane.
		

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private static JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());			
			if (RIGHT_TO_LEFT) {
				jContentPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
				jContentPane.setSize(new Dimension(243, 80));
			}
			jContentPane.setLayout(new GridBagLayout());
			GridBagConstraints c1 = new GridBagConstraints();
			GridBagConstraints c2 = new GridBagConstraints();
			GridBagConstraints c3 = new GridBagConstraints();
			GridBagConstraints c4 = new GridBagConstraints();
			GridBagConstraints c5 = new GridBagConstraints();
			if (shouldFill) {
				// natural height, maximum width
				c1.fill = GridBagConstraints.HORIZONTAL;
			}
			button1 = new JButton("Button 1");
			if (shouldWeightX) {
				c1.weightx = 0.5;
			}
			c1.fill = GridBagConstraints.HORIZONTAL;
			c1.gridx = 0;
			c1.gridy = 0;
			jContentPane.add(button1, c1);

			button2 = new JButton("Button 2");
			c2.fill = GridBagConstraints.HORIZONTAL;
			c2.weightx = 0.5;
			c2.gridx = 1;
			c2.gridy = 0;
			jContentPane.add(button2, c2);

			button3 = new JButton("Button 3");
			c3.fill = GridBagConstraints.HORIZONTAL;
			c3.weightx = 0.5;
			c3.gridx = 2;
			c3.gridy = 0;
			jContentPane.add(button3, c3);

			button4 = new JButton("Long-Named Button 4");
			c4.fill = GridBagConstraints.HORIZONTAL;
			c4.gridheight = 1;
			c4.ipady = 0; // make this component tall
			c4.weightx = 0.0;
			c4.gridwidth = 3;
			c4.gridx = 0;
			c4.gridy = 1;
			button5 = new JButton("5");
			c5.fill = GridBagConstraints.HORIZONTAL;
			c5.gridheight = 1;
			c5.ipady = 0; // reset to default
			c5.weighty = 1.0; // request any extra vertical space
			c5.anchor = GridBagConstraints.SOUTHEAST; // bottom of space
			c5.insets = new Insets(10, 0, 0, 0); // top padding
			c5.gridx = 1; // aligned with button 2
			c5.gridwidth = 2; // 2 columns wide
			c5.gridy = 3; // third row
			jContentPane.add(button4, c4);
			jContentPane.add(button5, c5);
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridwidth = 2;
			gridBagConstraints2.gridy = 2;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridwidth = 3;
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.weighty = 0.0;
			gridBagConstraints1.gridy = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
//			
//			jContentPane.add(button1, gridBagConstraints);
//			jContentPane.add(button2, new GridBagConstraints());
//			jContentPane.add(button3, new GridBagConstraints());
//			jContentPane.add(button4, gridBagConstraints1);
//			jContentPane.add(button5, gridBagConstraints2);
		}
		return jContentPane;
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}
