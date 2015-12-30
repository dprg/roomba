/**
 * 
 */
package roombacomm;

import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.SpringLayout;

import java.awt.GridBagConstraints;
import java.awt.FlowLayout;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.JButton;

import roombacomm.net.RoombaCommTCPClient;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JInternalFrame;
import java.awt.GridLayout;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.BoxLayout;


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
 * @author black.123
 * 
 */
public class RoombaCommUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private String[] protocols = { "Roomba 1xx-4xx (SCI)", "Roomba 5xx (OI)" };

	private JMenuBar jMenuBar1;

	private static RoombaCommSerial rcs = null;

	private ButtonGroup connectMethods;

	private JComboBox serialCombo;

	private JMenuItem fileMenu_Exit;

	private JMenuItem file_ConfigMenuItem;

	private JMenu jMenuFile;

	private JTextArea displayText;

	private JScrollPane scrollPane;

	private JPanel displayPanel;

	private JSlider jSlider3;

	private JPanel jPanel4;

	private JButton but_backward;

	private JButton but_spinright;

	private JButton but_stop;

	private JButton but_spinleft;

	private JButton but_turnright;

	private JButton but_forward;

	private JButton but_turnleft;

	private JPanel ctrlPanel1;

	private JPanel ctrlPanel;

	private JButton but_passive;

	private JButton but_reset;

	private JButton but_chargeData;

	private JButton but_test;

	private JButton but_TribbleOn;

	private JButton but_OSU;

	private JButton but_blinkleds;

	private JButton but_dock;

	private JButton but_max;

	private JButton but_spot;

	private JButton but_clean;

	private JButton but_vacoff;

	private JButton but_vacon;

	private JButton but_beephi;

	private JButton but_beeplo;

	private JButton but_full;

	private JButton but_safe;

	private JButton but_sensors;

	private JButton but_wakeup;

	private JButton but_power;

	private JButton but_powerOn;

	private JPanel buttonPanel;

	private JSlider jSlider2;

	private JPanel jPanel3;

	private JSlider jSlider1;

	private JPanel jPanel2;

	private JButton but_toggleCheckRobot;

	private JButton but_toggleDock;

	private JButton but_toggleMax;

	private JButton but_toggleDirt;

	private JButton but_toggleClean;

	private JButton but_toggleSpot;

	private JRadioButton jRadioButton4;

	private JRadioButton jRadioButton3;

	private JRadioButton jRadioButton2;

	private JRadioButton jRadioButton1;

	private JPanel jPanel1;

	private JPanel ledPanel;

	private JButton connectButton;

	private JPanel connectButtonPanel;

	private JTextField netPort;

	private JLabel Port;

	private JTextField netHost;

	private JLabel netHostText;

	private JPanel netDetailsUI;

	private JLabel serialComboText;

	private JPanel serialDetailsUI;

	private JTabbedPane connectDetailsTabs;

	private JPanel connectDetails;

	private JRadioButton connectMethodTCP;

	private JRadioButton connectMethod;

	private JPanel connectMethodPanel;

	private JComboBox protocalCombo;

	private JLabel protocalText;

	private JPanel connectGeneral;

	private JPanel connectPanel;

	private JPanel jContentPane;

	private ButtonGroup buttonGroup1;

	private static RoombaCommTCPClient rctcpc = null;

	private RoombaCommUI me;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// rcs = new RoombaCommSerial();
		// rctcpc = new RoombaCommTCPClient();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				RoombaCommUI thisClass = new RoombaCommUI();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});
	}

	/**
	 * This is the default constructor
	 */
	public RoombaCommUI() {
		super();
		me = this;
		this.setContentPane(getJContentPane());
		{
			jMenuBar1 = new JMenuBar();
			setJMenuBar(jMenuBar1);
			jMenuBar1.add(getjMenuFile());
		}
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setTitle("RoombaComm Manual Control UI");
		this.setName("RoombaComm Manual Control UI frame");
		rcs = new RoombaCommSerial();
		rctcpc = new RoombaCommTCPClient();
		// this.addComponentListener(new SysoutOnResize());
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				// TODO Auto-generated Event stub componentResized()
				System.out.println(e.getComponent().getName() + " resized to " + e.getComponent().getWidth() + "," + e.getComponent().getHeight()); 
				if (e.getComponent().getWidth() < connectPanel.getMinimumSize().getWidth()) {
					e.getComponent().setSize(Double.valueOf(connectPanel.getMinimumSize().getWidth()).intValue(), e.getComponent().getHeight());
					// TODO Auto-generated Event stub componentResized()
					System.out.println("*" + e.getComponent().getName() + " width to " + Double.valueOf(connectPanel.getMinimumSize().getWidth()).intValue() + "," + e.getComponent().getHeight()); 
				} else {
					System.out.println(" " + e.getComponent().getName() + " width ok (>" + connectPanel.getMinimumSize().getWidth() + ")");
				}
				if (e.getComponent().getHeight() < connectPanel.getMinimumSize().getHeight()) {
					e.getComponent().setSize(e.getComponent().getWidth(), Double.valueOf(connectPanel.getMinimumSize().getHeight()).intValue());
					// TODO Auto-generated Event stub componentResized()
					System.out.println("*" + e.getComponent().getName() + " height to " + Double.valueOf(connectPanel.getMinimumSize().getHeight()).intValue() + "," + e.getComponent().getHeight()); 
				} else {
					System.out.println(" " + e.getComponent().getName() + " height ok (>" + connectPanel.getMinimumSize().getHeight() + ")");
				}
			}
		});

		pack();
		this.setSize(800, 692);
	}

	private ButtonGroup getButtonGroup1() {
		if (buttonGroup1 == null) {
			buttonGroup1 = new ButtonGroup();
			buttonGroup1.add(getJRadioButton1());
			buttonGroup1.add(getJRadioButton2());
			buttonGroup1.add(getJRadioButton3());
			buttonGroup1.add(getJRadioButton4());
		}
		return buttonGroup1;
	}

	private void jMenuFileMousePressed(MouseEvent evt) {
		System.out.println("jMenuFile.mousePressed, event=" + evt);
		// TODO add your code for jMenuFile.mousePressed
		if (evt.isPopupTrigger()) {
			System.out.println(evt.getComponent().getName() + " is PopupTrigger");
		} else {
			System.out.println(evt.getComponent().getName() + " is not PopupTrigger");
		}
	}

	/**
	 * Auto-generated method for setting the popup menu for a component
	 */
	private void setComponentPopupMenu(final java.awt.Component parent, final javax.swing.JPopupMenu menu) {
		parent.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent e) {
				if (e.isPopupTrigger()) menu.show(parent, e.getX(), e.getY());
			}

			public void mouseReleased(java.awt.event.MouseEvent e) {
				if (e.isPopupTrigger()) menu.show(parent, e.getX(), e.getY());
			}
		});
	}

	public void actionPerformed(ActionEvent e) {
		JMenuItem source = (JMenuItem) (e.getSource());
		String newline = "\n";
		String s = "Action event detected." + newline + "    Event source: " + source.getText()
		// + " (an instance of " + getClassName(source) + ")"
		;
		System.out.println(s + newline);
	}

	private ButtonGroup getConnectMethods() {
		if (connectMethods == null) {
			connectMethods = new ButtonGroup();
		}
		return connectMethods;
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints10.weightx = 1.0;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.ipadx = 153;
			gridBagConstraints9.ipady = 120;
			gridBagConstraints9.fill = GridBagConstraints.NONE;
			gridBagConstraints9.gridy = 1;
			jContentPane = new JPanel();
			jContentPane.setName("jContentPane");
			GridBagLayout jContentPaneLayout = new GridBagLayout();
			jContentPane.setLayout(jContentPaneLayout);
			jContentPane.add(getConnectPanel(), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			jContentPaneLayout.rowWeights = new double[] { 0.1, 0.1 };
			jContentPaneLayout.rowHeights = new int[] { 7, 7 };
			jContentPaneLayout.columnWeights = new double[] { 0.1 };
			jContentPaneLayout.columnWidths = new int[] { 7 };
			jContentPane.addComponentListener(new ComponentAdapter() {
				public void componentResized(java.awt.event.ComponentEvent e) {
					System.out.println(" " + e.getComponent().getName() + " resized to " + e.getComponent().getWidth() + "," + e.getComponent().getHeight()); // TODO
																																								// Auto-generated
																																								// Event
																																								// stub
																																								// componentResized()
					if (e.getComponent().getWidth() < connectPanel.getMinimumSize().getWidth()) {
						e.getComponent().setSize(Double.valueOf(connectPanel.getMinimumSize().getWidth()).intValue(), e.getComponent().getHeight());
						System.out.println("*" + e.getComponent().getName() + " resized width to " + Double.valueOf(connectPanel.getMinimumSize().getWidth()).intValue() + "," + e.getComponent().getHeight()); // TODO
																																																				// Auto-generated
																																																				// Event
																																																				// stub
																																																				// componentResized()
					} else {
						System.out.println(" " + e.getComponent().getName() + " width ok (>" + connectPanel.getMinimumSize().getWidth() + ")");
					}
					if (e.getComponent().getHeight() < connectPanel.getMinimumSize().getHeight()) {
						e.getComponent().setSize(e.getComponent().getWidth(), Double.valueOf(connectPanel.getMinimumSize().getHeight()).intValue());
						System.out.println("*" + e.getComponent().getName() + " resized height to " + e.getComponent().getWidth() + "," + e.getComponent().getHeight()); // TODO
																																											// Auto-generated
																																											// Event
																																											// stub
																																											// componentResized()
					} else {
						System.out.println(" " + e.getComponent().getName() + " height ok (>" + connectPanel.getMinimumSize().getWidth() + ")");
					}
				}
			});
		}
		return jContentPane;
	}

	/**
	 * This method initializes connectPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getConnectPanel() {
		if (connectPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			// gridBagConstraints1.fill = GridBagConstraints.BOTH;
			// gridBagConstraints1.weighty = 1.0;
			// gridBagConstraints1.gridx = 0;
			// gridBagConstraints1.gridy = 0;
			// gridBagConstraints1.weightx = 1.0;
			connectPanel = new JPanel();
			connectPanel.setName("connectPanel");
			// connectPanel.setLayout(new FlowLayout());
			GridBagLayout connectPanelLayout = new GridBagLayout();
			connectPanel.setLayout(connectPanelLayout);
			connectPanelLayout.rowWeights = new double[] { 0.1, 0.1, 0.1 };
			connectPanelLayout.rowHeights = new int[] { 7, 7, 7 };
			connectPanelLayout.columnWeights = new double[] { 0.1, 0.0, 0.1 };
			connectPanelLayout.columnWidths = new int[] { 7, 156, 7 };
			connectPanel.setMinimumSize(new java.awt.Dimension(580, 220));
			connectPanel.setMaximumSize(new Dimension(1200, 220));
			connectPanel.add(getConnectGeneral(), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			connectPanel.add(getConnectDetails(), new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			connectPanel.add(getConnectButtonPanel(), new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			connectPanel.add(getJPanel1(), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			connectPanel.add(getJPanel4(), new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			connectPanel.add(getJPanel4x(), new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			connectPanel.add(getJPanel5(), new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		}
		return connectPanel;
	}

	/**
	 * This method initializes connectGeneral
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getConnectGeneral() {
		if (connectGeneral == null) {
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.anchor = GridBagConstraints.EAST;
			gridBagConstraints12.gridwidth = 2;
			gridBagConstraints12.ipadx = 5;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			// gridBagConstraints6.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints6.weightx = 1.0;
			connectGeneral = new JPanel();
			connectGeneral.setName("connectGeneral");
			connectGeneral.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Select Roomba Type & Port"), BorderFactory.createEmptyBorder(1, 1, 1, 1)));
			connectGeneral.setMinimumSize(new Dimension(375, 15));
			GridBagLayout connectGeneralLayout = new GridBagLayout();
			connectGeneral.setLayout(connectGeneralLayout);
			connectGeneral.add(getProtocalText(), new GridBagConstraints(-1, -1, 2, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 5, 0));
			connectGeneral.add(getProtocalCombo(), new GridBagConstraints(-1, -1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			connectGeneral.add(getConnectMethodPanel(), new GridBagConstraints(-1, -1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			connectGeneralLayout.columnWeights = new double[] { 0.1, 0.1, 0.1 };
			connectGeneralLayout.columnWidths = new int[] { 7, 7, 7 };
			connectGeneralLayout.rowWeights = new double[] { 0.1 };
			connectGeneralLayout.rowHeights = new int[] { 7 };
			connectGeneral.addComponentListener(new SysoutOnResize());
			// connectGeneral.addComponentListener(new
			// java.awt.event.ComponentAdapter() {
			// public void componentResized(java.awt.event.ComponentEvent e) {
			// System.out.println("componentResized() to "+connectGeneral.getSize().width+","+connectGeneral.getSize().height);
			// // TODO Auto-generated Event stub componentResized()
			// }
			// });
		}
		return connectGeneral;
	}

	private JLabel getProtocalText() {
		if (protocalText == null) {
			protocalText = new JLabel();
			protocalText.setText("Roomba Model");
			protocalText.setMinimumSize(new Dimension(100, 10));
			protocalText.setName("protocalText");
		}
		return protocalText;
	}

	/**
	 * This method initializes protocalCombo
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getProtocalCombo() {
		if (protocalCombo == null) {
			protocalCombo = new JComboBox(protocols);
			for (int i = 0; i < protocols.length; i++) {
				String val = protocols[i];
				protocalCombo.add(val, new JLabel(protocols[i]));
			}

			protocalCombo.setName("protocalCombo");
			protocalCombo.setAlignmentX(LEFT_ALIGNMENT);
			protocalCombo.setMinimumSize(new Dimension(300, 10));
		}
		return protocalCombo;
	}

	/**
	 * This method initializes connectMethodPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getConnectMethodPanel() {
		if (connectMethodPanel == null) {
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.insets = new Insets(0, 5, 0, 5);
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.insets = new Insets(0, 5, 0, 5);
			gridBagConstraints7.gridy = 1;
			connectMethodPanel = new JPanel();
			connectMethodPanel.setName("connectMethodPanel");
			GridBagLayout connectMethodPanelLayout = new GridBagLayout();
			connectMethodPanel.setLayout(new GridBagLayout());
			connectMethodPanel.add(getConnectMethod(), new GridBagConstraints(-1, -1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
			connectMethodPanel.add(getConnectMethodTCP(), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
		}
		return connectMethodPanel;
	}

	/**
	 * This method initializes connectMethod
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getConnectMethod() {
		if (connectMethod == null) {
			connectMethod = new JRadioButton();
			connectMethod.setName("connectMethod");
			connectMethod.setText("Serial");
			getConnectMethods().add(connectMethod);
			connectMethod.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("actionPerformed():" + e.getActionCommand());
					if (connectDetailsTabs.getSelectedIndex() != 0) {
						connectDetailsTabs.setSelectedIndex(0);
					}
				}
			});
		}
		return connectMethod;
	}

	/**
	 * This method initializes connectMethodTCP
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getConnectMethodTCP() {
		if (connectMethodTCP == null) {
			connectMethodTCP = new JRadioButton();
			connectMethodTCP.setName("connectMethodTCP");
			connectMethodTCP.setText("Net");
			connectMethods.add(connectMethodTCP);
			connectMethodTCP.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("actionPerformed():" + e.getActionCommand());
					if (connectDetailsTabs.getSelectedIndex() != 1) {
						connectDetailsTabs.setSelectedIndex(1);
					}
					if (serialDetailsUI != null && serialDetailsUI.isVisible()) {
						serialDetailsUI.setVisible(!serialDetailsUI.isVisible());
					}
					if (netDetailsUI != null && !netDetailsUI.isVisible()) {
						netDetailsUI.setVisible(!netDetailsUI.isVisible());
					}
				}
			});
		}
		return connectMethodTCP;
	}

	/**
	 * This method initializes connectDetails
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getConnectDetails() {
		if (connectDetails == null) {
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.fill = GridBagConstraints.BOTH;
			gridBagConstraints13.weighty = 1.0;
			gridBagConstraints13.weightx = 1.0;
			connectDetails = new JPanel();
			connectDetails.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Connection details"), BorderFactory.createEmptyBorder(1, 1, 1, 1)));
			connectDetails.setMinimumSize(new Dimension(200, 100));
			connectDetails.setMaximumSize(new Dimension(300, 150));
			GridBagLayout connectDetailsLayout = new GridBagLayout();
			connectDetails.setLayout(new GridBagLayout());
			connectDetails.add(getConnectDetailsTabs(), new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		}
		return connectDetails;
	}

	/**
	 * This method initializes connectDetailsTabs
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getConnectDetailsTabs() {
		if (connectDetailsTabs == null) {
			connectDetailsTabs = new JTabbedPane();
			connectDetailsTabs.setName("connectDetailsTabs");
			connectDetailsTabs.addTab("Serial", null, getSerialDetailsUI(), null);
			connectDetailsTabs.addTab("Net", null, getNetDetailsUI(), null);
			// connectDetailsTabs.setMinimumSize(new Dimension(200,10));
			// connectDetailsTabs.setMaximumSize(new Dimension(200,100));
		}
		return connectDetailsTabs;
	}

	/**
	 * This method initializes serialDetailsUI
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getSerialDetailsUI() {
		if (serialDetailsUI == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints3.gridy = -1;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.gridx = -1;
			serialDetailsUI = new JPanel();
			serialDetailsUI.setName("serialDetailsUI");
			serialDetailsUI.setMinimumSize(new Dimension(150, 10));
			// serialDetailsUI.setToolTipText("Serial Connection details");
			serialDetailsUI.setVisible(false);
			GridBagLayout serialDetailsUILayout = new GridBagLayout();
			serialDetailsUI.setLayout(new GridBagLayout());
			serialDetailsUI.add(getSerialComboText(), new GridBagConstraints(-1, -1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			serialDetailsUI.add(getSerialCombo(), new GridBagConstraints(-1, -1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(0, 10, 0, 0), 0, 0));
		}
		return serialDetailsUI;
	}

	private JLabel getSerialComboText() {
		if (serialComboText == null) {
			serialComboText = new JLabel();
			serialComboText.setText("Com port");
			serialComboText.setName("serialComboText");
		}
		return serialComboText;
	}

	private JComboBox getSerialCombo() {
		if (serialCombo == null) {
			serialCombo = new JComboBox();
			serialCombo.setName("serialCombo");
			if (rcs != null) {
				serialCombo = new JComboBox();
				String[] ports = rcs.listPorts();
				for (int i = 0; i < ports.length; i++) {
					JLabel label = new JLabel(ports[i]);
					label.setName(ports[i]);
					serialCombo.add(ports[i], label);
				}
				serialCombo.setName("serialCombo");
			}
		}
		// TODO: This also needs to be selected by the local props
		return serialCombo;
	}

	/**
	 * This method initializes netDetailsUI
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getNetDetailsUI() {
		if (netDetailsUI == null) {
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.gridx = 0;
			gridBagConstraints14.gridy = 1;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.insets = new Insets(0, 5, 0, 5);
			gridBagConstraints5.gridx = 1;
			gridBagConstraints5.gridy = 1;
			gridBagConstraints5.weightx = 15.0;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.insets = new Insets(0, 5, 0, 5);
			gridBagConstraints4.gridwidth = 1;
			gridBagConstraints4.gridheight = 1;
			gridBagConstraints4.weightx = 15.0;
			netDetailsUI = new JPanel();
			netDetailsUI.setName("netDetailsUI");
			netDetailsUI.setMinimumSize(new Dimension(150, 10));
			// netDetailsUI.setToolTipText("Net Connection details");
			netDetailsUI.setVisible(false);
			GridBagLayout netDetailsUILayout = new GridBagLayout();
			netDetailsUI.setLayout(new GridBagLayout());
			netDetailsUI.add(getNetHostText(), new GridBagConstraints(-1, -1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			netDetailsUI.add(getNetHost(), new GridBagConstraints(-1, -1, 1, 1, 15.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
			netDetailsUI.add(getPort(), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			netDetailsUI.add(getNetPort(), new GridBagConstraints(1, 1, 1, 1, 15.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
		}
		return netDetailsUI;
	}

	private JLabel getNetHostText() {
		if (netHostText == null) {
			netHostText = new JLabel();
			netHostText.setText("Host");
			netHostText.setName("netHostText");
		}
		return netHostText;
	}

	/**
	 * This method initializes netHost
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getNetHost() {
		if (netHost == null) {
			netHost = new JTextField();
			netHost.setName("netHost");
			netHost.setText(""); // TODO: this needs to be set by the props
		}
		return netHost;
	}

	private JLabel getPort() {
		if (Port == null) {
			Port = new JLabel();
			Port.setText("Port");
			Port.setName("Port");
		}
		return Port;
	}

	/**
	 * This method initializes netPort
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getNetPort() {
		if (netPort == null) {
			netPort = new JTextField();
			netPort.setName("netPort");
			netPort.setText(""); // TODO: this needs to be set by the local
									// props
		}
		return netPort;
	}

	/**
	 * This method initializes connectButtonPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getConnectButtonPanel() {
		if (connectButtonPanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints2.weightx = 1.0;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.insets = new Insets(0, 5, 0, 5);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.ipadx = 1;
			gridBagConstraints.insets = new Insets(0, 5, 0, 5);
			gridBagConstraints.weightx = 1.0;
			connectButtonPanel = new JPanel();
			connectButtonPanel.setName("connectButtonPanel");
			connectButtonPanel.setMinimumSize(new Dimension(200, 10));
			GridBagLayout connectButtonPanelLayout = new GridBagLayout();
			connectButtonPanel.setLayout(new GridBagLayout());
			connectButtonPanel.add(getConnectButton(), new GridBagConstraints(-1, -1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
			// connectButtonPanel.add(getSerialCombo(), gridBagConstraints2);
		}
		return connectButtonPanel;
	}

	/**
	 * This method initializes connectButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getConnectButton() {
		if (connectButton == null) {
			connectButton = new JButton();
			connectButton.setName("connectButton");
			connectButton.setText("Connect");
			connectButton.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("Connect button pressedactionPerformed()"); // TODO
																					// Auto-generated
																					// Event
																					// stub
																					// actionPerformed()
					System.out.println("me (" + me.getHeight() + "," + me.getWidth() + ")");
					System.out.println("me component count(" + me.getComponentCount() + ")");
					for (int i = 0; i < me.getComponents().length; i++) {
						Component comp = me.getComponents()[i];
						for (int j = 0; j < me.getComponents().length; j++) {
							Component comps = me.getComponents()[j];
							listComponentsWithSizes(comps, "[" + j + "]");
						}
						// if (comp instanceof Container) {
						// Container compAsContainer = (Container) comp;
						// for (int ii = 0; ii <
						// compAsContainer.getComponents().length; ii++) {
						// System.out.println("found ("+compAsContainer.getComponents().length+") child components");
						// System.out.println("["+ii+"]("+comp.getName()+")("+comp.getWidth()+","+comp.getHeight()+")");
						// listComponentsWithSizes(compAsContainer.getComponent(ii));
						// }
						// }else{
						// System.out.println("["+i+"]("+comp.getName()+")("+comp.getWidth()+","+comp.getHeight()+")");
						// }
					}
				}

				private void listComponentsWithSizes(Component comp, String prefix) {
					System.out.println(prefix + "(" + comp.getName() + ")(" + comp.getWidth() + "," + comp.getHeight() + ")");
					if (comp instanceof Container) {
						Container compAsContainer = (Container) comp;
						for (int ii = 0; ii < compAsContainer.getComponents().length; ii++) {
							listComponentsWithSizes(compAsContainer.getComponent(ii), prefix + "[" + ii + "]");
						}
					}
				}
			});
		}
		return connectButton;
	}

	private JPanel getJPanel1() {
		if (ledPanel == null) {
			ledPanel = new JPanel();
			GridBagLayout panel_LEDsLayout = new GridBagLayout();
			panel_LEDsLayout.rowWeights = new double[] { 0.1, 0.1, 0.1 };
			panel_LEDsLayout.rowHeights = new int[] { 7, 7, 7 };
			panel_LEDsLayout.columnWeights = new double[] { 0.1 };
			panel_LEDsLayout.columnWidths = new int[] { 7 };
			ledPanel.setLayout(panel_LEDsLayout);
			ledPanel.setBorder(BorderFactory.createTitledBorder("LEDs"));
			ledPanel.add(getJPanel1x(), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
			ledPanel.add(getJPanel2(), new GridBagConstraints(0, 1, 1, 3, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
			ledPanel.add(getJPanel3(), new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		}
		return ledPanel;
	}

	private JPanel getJPanel1x() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			GridBagLayout jPanel1Layout = new GridBagLayout();
			jPanel1Layout.rowWeights = new double[] { 0.1, 0.1, 0.1, 0.1 };
			jPanel1Layout.rowHeights = new int[] { 7, 7, 7, 7 };
			jPanel1Layout.columnWeights = new double[] { 0.1, 0.1, 0.1, 0.1 };
			jPanel1Layout.columnWidths = new int[] { 7, 7, 7, 7 };
			jPanel1.setLayout(jPanel1Layout);
			jPanel1.add(getJRadioButton1(), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			jPanel1.add(getJRadioButton2(), new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			jPanel1.add(getJRadioButton3(), new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			jPanel1.add(getJRadioButton4(), new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			jPanel1.add(getJButton1(), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			jPanel1.add(getJButton2(), new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			jPanel1.add(getJButton3(), new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			jPanel1.add(getJButton4(), new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			jPanel1.add(getJButton5(), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			jPanel1.add(getJButton6(), new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
		}
		return jPanel1;
	}

	private JRadioButton getJRadioButton1() {
		if (jRadioButton1 == null) {
			jRadioButton1 = new JRadioButton();
			jRadioButton1.setText("None");
		}
		return jRadioButton1;
	}

	private JRadioButton getJRadioButton2() {
		if (jRadioButton2 == null) {
			jRadioButton2 = new JRadioButton();
			jRadioButton2.setText("Green");
		}
		return jRadioButton2;
	}

	private JRadioButton getJRadioButton3() {
		if (jRadioButton3 == null) {
			jRadioButton3 = new JRadioButton();
			jRadioButton3.setText("Red");
		}
		return jRadioButton3;
	}

	private JRadioButton getJRadioButton4() {
		if (jRadioButton4 == null) {
			jRadioButton4 = new JRadioButton();
			jRadioButton4.setText("Orange");
		}
		return jRadioButton4;
	}

	private JButton getJButton1() {
		if (but_toggleSpot == null) {
			but_toggleSpot = new JButton();
			but_toggleSpot.setIcon(new ImageIcon(getClass().getClassLoader().getResource("roombacomm/images/but_spotOn.png")));
			but_toggleSpot.setToolTipText("Spot");
		}
		return but_toggleSpot;
	}

	private JButton getJButton2() {
		if (but_toggleClean == null) {
			but_toggleClean = new JButton();
			but_toggleClean.setIcon(new ImageIcon(getClass().getClassLoader().getResource("roombacomm/images/but_cleanOn.png")));
			but_toggleClean.setToolTipText("Clean");
		}
		return but_toggleClean;
	}

	private JButton getJButton3() {
		if (but_toggleDirt == null) {
			but_toggleDirt = new JButton();
			but_toggleDirt.setIcon(new ImageIcon(getClass().getClassLoader().getResource("roombacomm/images/but_dirtOn.png")));
			but_toggleDirt.setToolTipText("Dirt");
		}
		return but_toggleDirt;
	}

	private JButton getJButton4() {
		if (but_toggleMax == null) {
			but_toggleMax = new JButton();
			but_toggleMax.setToolTipText("Max");
			but_toggleMax.setIcon(new ImageIcon(getClass().getClassLoader().getResource("roombacomm/images/but_maxOn.png")));
		}
		return but_toggleMax;
	}

	private JButton getJButton5() {
		if (but_toggleDock == null) {
			but_toggleDock = new JButton();
			but_toggleDock.setToolTipText("Dock");
			but_toggleDock.setIcon(new ImageIcon(getClass().getClassLoader().getResource("roombacomm/images/but_dockOn.png")));
		}
		return but_toggleDock;
	}

	private JButton getJButton6() {
		if (but_toggleCheckRobot == null) {
			but_toggleCheckRobot = new JButton();
			but_toggleCheckRobot.setIcon(new ImageIcon(getClass().getClassLoader().getResource("roombacomm/images/but_checkrobotOn.png")));
			but_toggleCheckRobot.setToolTipText("Check Robot");
		}
		return but_toggleCheckRobot;
	}

	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jPanel2 = new JPanel();
			FlowLayout jPanel2Layout = new FlowLayout();
			jPanel2Layout.setAlignment(FlowLayout.LEFT);
			jPanel2.setLayout(jPanel2Layout);
			jPanel2.setBorder(BorderFactory.createTitledBorder(null, "PowerColor", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION));
			jPanel2.add(getJSlider1());
		}
		return jPanel2;
	}

	private JSlider getJSlider1() {
		if (jSlider1 == null) {
			jSlider1 = new JSlider();
			FlowLayout jSlider1Layout = new FlowLayout();
			jSlider1Layout.setAlignment(FlowLayout.LEFT);
			jSlider1.setLayout(jSlider1Layout);
			jSlider1.setMaximum(255);
			jSlider1.setPreferredSize(new java.awt.Dimension(302, 49));
			jSlider1.setValue(100);
			jSlider1.setMajorTickSpacing(100);
			jSlider1.setMinorTickSpacing(25);
			jSlider1.setPaintLabels(true);
			jSlider1.setPaintTicks(true);
		}
		return jSlider1;
	}

	private JPanel getJPanel3() {
		if (jPanel3 == null) {
			jPanel3 = new JPanel();
			jPanel3.setBorder(BorderFactory.createTitledBorder(null, "PowerColorIntensity", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION));
			jPanel3.add(getJSlider2());
		}
		return jPanel3;
	}

	private JSlider getJSlider2() {
		if (jSlider2 == null) {
			jSlider2 = new JSlider();
			FlowLayout jSlider2Layout = new FlowLayout();
			jSlider2Layout.setAlignment(FlowLayout.LEFT);
			jSlider2.setLayout(jSlider2Layout);
			jSlider2.setMaximum(255);
			jSlider2.setValue(100);
			jSlider2.setMinorTickSpacing(25);
			jSlider2.setMajorTickSpacing(100);
			jSlider2.setPaintLabels(true);
			jSlider2.setPaintTicks(true);
			jSlider2.setPreferredSize(new java.awt.Dimension(298, 43));
		}
		return jSlider2;
	}

	private JPanel getJPanel4() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			GridBagLayout jPanel4Layout = new GridBagLayout();
			buttonPanel.setBorder(BorderFactory.createTitledBorder("Commands"));
			jPanel4Layout.rowWeights = new double[] { 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1 };
			jPanel4Layout.rowHeights = new int[] { 7, 7, 7, 7, 7, 7, 7, 7, 7, 7 };
			jPanel4Layout.columnWeights = new double[] { 0.1, 0.1 };
			jPanel4Layout.columnWidths = new int[] { 7, 7 };
			buttonPanel.setLayout(jPanel4Layout);
			buttonPanel.add(getJButton1x(), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			buttonPanel.add(getJButton2x(), new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			buttonPanel.add(getJButton3x(), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			buttonPanel.add(getJButton4x(), new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			buttonPanel.add(getJButton5x(), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			buttonPanel.add(getJButton6x(), new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			buttonPanel.add(getJButton7(), new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			buttonPanel.add(getJButton8(), new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			buttonPanel.add(getJButton9(), new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			buttonPanel.add(getJButton10(), new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			buttonPanel.add(getJButton11(), new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			buttonPanel.add(getJButton12(), new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			buttonPanel.add(getJButton13(), new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			buttonPanel.add(getJButton14(), new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			buttonPanel.add(getJButton15(), new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			buttonPanel.add(getJButton16(), new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			buttonPanel.add(getJButton17(), new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			buttonPanel.add(getJButton18(), new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			buttonPanel.add(getJButton1xxx(), new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			buttonPanel.add(getBut_reset(), new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			buttonPanel.add(getBut_passive(), new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
		}
		return buttonPanel;
	}

	private JButton getJButton1x() {
		if (but_powerOn == null) {
			but_powerOn = new JButton();
			but_powerOn.setText("power-on");
		}
		return but_powerOn;
	}

	private JButton getJButton2x() {
		if (but_power == null) {
			but_power = new JButton();
			but_power.setText("power-off");
		}
		return but_power;
	}

	private JButton getJButton3x() {
		if (but_wakeup == null) {
			but_wakeup = new JButton();
			but_wakeup.setText("wakeup");
		}
		return but_wakeup;
	}

	private JButton getJButton4x() {
		if (but_sensors == null) {
			but_sensors = new JButton();
			but_sensors.setText("sensors");
		}
		return but_sensors;
	}

	private JButton getJButton5x() {
		if (but_safe == null) {
			but_safe = new JButton();
			but_safe.setText("safe");
		}
		return but_safe;
	}

	private JButton getJButton6x() {
		if (but_full == null) {
			but_full = new JButton();
			but_full.setText("full");
		}
		return but_full;
	}

	private JButton getJButton7() {
		if (but_beeplo == null) {
			but_beeplo = new JButton();
			but_beeplo.setText("beep-lo");
		}
		return but_beeplo;
	}

	private JButton getJButton8() {
		if (but_beephi == null) {
			but_beephi = new JButton();
			but_beephi.setText("beep-hi");
		}
		return but_beephi;
	}

	private JButton getJButton9() {
		if (but_vacon == null) {
			but_vacon = new JButton();
			but_vacon.setText("vacuum-on");
		}
		return but_vacon;
	}

	private JButton getJButton10() {
		if (but_vacoff == null) {
			but_vacoff = new JButton();
			but_vacoff.setText("vacuum-off");
		}
		return but_vacoff;
	}

	private JButton getJButton11() {
		if (but_clean == null) {
			but_clean = new JButton();
			but_clean.setText("clean");
		}
		return but_clean;
	}

	private JButton getJButton12() {
		if (but_spot == null) {
			but_spot = new JButton();
			but_spot.setText("spot");
		}
		return but_spot;
	}

	private JButton getJButton13() {
		if (but_max == null) {
			but_max = new JButton();
			but_max.setText("max");
		}
		return but_max;
	}

	private JButton getJButton14() {
		if (but_dock == null) {
			but_dock = new JButton();
			but_dock.setText("dock");
		}
		return but_dock;
	}

	private JButton getJButton15() {
		if (but_blinkleds == null) {
			but_blinkleds = new JButton();
			but_blinkleds.setText("blink-leds");
		}
		return but_blinkleds;
	}

	private JButton getJButton16() {
		if (but_OSU == null) {
			but_OSU = new JButton();
			but_OSU.setText("OSU");
		}
		return but_OSU;
	}

	private JButton getJButton17() {
		if (but_TribbleOn == null) {
			but_TribbleOn = new JButton();
			but_TribbleOn.setText("tribble");
		}
		return but_TribbleOn;
	}

	private JButton getJButton18() {
		if (but_test == null) {
			but_test = new JButton();
			but_test.setText("test");
		}
		return but_test;
	}

	private JButton getJButton1xxx() {
		if (but_chargeData == null) {
			but_chargeData = new JButton();
			but_chargeData.setText("charge data");
		}
		return but_chargeData;
	}

	private JButton getBut_reset() {
		if (but_reset == null) {
			but_reset = new JButton();
			but_reset.setText("reset");
		}
		return but_reset;
	}

	private JButton getBut_passive() {
		if (but_passive == null) {
			but_passive = new JButton();
			but_passive.setText("passive");
		}
		return but_passive;
	}

	private JPanel getJPanel4x() {
		if (ctrlPanel == null) {
			ctrlPanel = new JPanel();
			GridBagLayout ctrlPanelLayout = new GridBagLayout();
			ctrlPanelLayout.rowWeights = new double[] { 0.1, 0.1, 0.1 };
			ctrlPanelLayout.rowHeights = new int[] { 7, 7, 7 };
			ctrlPanelLayout.columnWeights = new double[] { 0.1 };
			ctrlPanelLayout.columnWidths = new int[] { 7 };
			ctrlPanel.setLayout(ctrlPanelLayout);
			ctrlPanel.setBorder(BorderFactory.createTitledBorder("Movement"));
			ctrlPanel.add(getJPanel4xx(), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			ctrlPanel.add(getJPanel4xxx(), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		}
		return ctrlPanel;
	}

	private JPanel getJPanel4xx() {
		if (ctrlPanel1 == null) {
			ctrlPanel1 = new JPanel();
			GridBagLayout ctrlPanel1Layout = new GridBagLayout();
			ctrlPanel1Layout.rowWeights = new double[] { 0.1, 0.1, 0.1 };
			ctrlPanel1Layout.rowHeights = new int[] { 7, 7, 7 };
			ctrlPanel1Layout.columnWeights = new double[] { 0.1, 0.1, 0.1 };
			ctrlPanel1Layout.columnWidths = new int[] { 7, 7, 7 };
			ctrlPanel1.setLayout(ctrlPanel1Layout);
			ctrlPanel1.add(getJButton1xx(), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			ctrlPanel1.add(getJButton2xx(), new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			ctrlPanel1.add(getJButton3xx(), new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			ctrlPanel1.add(getJButton4xx(), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			ctrlPanel1.add(getJButton5xx(), new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			ctrlPanel1.add(getJButton6xx(), new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			ctrlPanel1.add(getJButton7x(), new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
		}
		return ctrlPanel1;
	}

	private JButton getJButton1xx() {
		if (but_turnleft == null) {
			but_turnleft = new JButton();
			but_turnleft.setIcon(new ImageIcon(getClass().getClassLoader().getResource("roombacomm/images/but_turnleft.png")));
		}
		return but_turnleft;
	}

	private JButton getJButton2xx() {
		if (but_forward == null) {
			but_forward = new JButton();
			but_forward.setIcon(new ImageIcon(getClass().getClassLoader().getResource("roombacomm/images/but_forward.png")));
		}
		return but_forward;
	}

	private JButton getJButton3xx() {
		if (but_turnright == null) {
			but_turnright = new JButton();
			but_turnright.setIcon(new ImageIcon(getClass().getClassLoader().getResource("roombacomm/images/but_turnright.png")));
		}
		return but_turnright;
	}

	private JButton getJButton4xx() {
		if (but_spinleft == null) {
			but_spinleft = new JButton();
			but_spinleft.setIcon(new ImageIcon(getClass().getClassLoader().getResource("roombacomm/images/but_spinleft.png")));
		}
		return but_spinleft;
	}

	private JButton getJButton5xx() {
		if (but_stop == null) {
			but_stop = new JButton();
			but_stop.setIcon(new ImageIcon(getClass().getClassLoader().getResource("roombacomm/images/but_stop.png")));
		}
		return but_stop;
	}

	private JButton getJButton6xx() {
		if (but_spinright == null) {
			but_spinright = new JButton();
			but_spinright.setIcon(new ImageIcon(getClass().getClassLoader().getResource("roombacomm/images/but_turnright.png")));
		}
		return but_spinright;
	}

	private JButton getJButton7x() {
		if (but_backward == null) {
			but_backward = new JButton();
			but_backward.setIcon(new ImageIcon(getClass().getClassLoader().getResource("roombacomm/images/but_backward.png")));
		}
		return but_backward;
	}

	private JPanel getJPanel4xxx() {
		if (jPanel4 == null) {
			jPanel4 = new JPanel();
			jPanel4.setBorder(BorderFactory.createTitledBorder(null, "speed (mm/s)", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION));
			jPanel4.add(getJSlider3());
		}
		return jPanel4;
	}

	private JSlider getJSlider3() {
		if (jSlider3 == null) {
			jSlider3 = new JSlider();
			jSlider3.setMaximum(500);
			jSlider3.setValue(200);
			jSlider3.setMajorTickSpacing(100);
			jSlider3.setMinorTickSpacing(25);
			jSlider3.setPaintLabels(true);
			jSlider3.setPaintTicks(true);
		}
		return jSlider3;
	}

	private JPanel getJPanel5() {
		if (displayPanel == null) {
			displayPanel = new JPanel();
			displayPanel.setBorder(BorderFactory.createTitledBorder("Display"));
			displayPanel.setPreferredSize(new java.awt.Dimension(784, 136));
			displayPanel.add(getJScrollPane1());
		}
		return displayPanel;
	}

	private JScrollPane getJScrollPane1() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setPreferredSize(new java.awt.Dimension(727, 100));
			scrollPane.setViewportView(getJTextArea1());
		}
		return scrollPane;
	}

	private JTextArea getJTextArea1() {
		if (displayText == null) {
			displayText = new JTextArea();
			//displayText.setSize(724, 100);
			//displayText.setPreferredSize(new java.awt.Dimension(724, 317));
		}
		return displayText;
	}

	private JMenu getjMenuFile() {
		if (jMenuFile == null) {
			jMenuFile = new JMenu();
			jMenuFile.setText("File");
			jMenuFile.setAutoscrolls(true);
			jMenuFile.setName("File");
			jMenuFile.add(getFile_ConfigMenuItem());
			jMenuFile.add(getFileMenuExit());
			// jMenuFile.setNextFocusableComponent(getJMenuItemExit());
			jMenuFile.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent evt) {
					jMenuFileMousePressed(evt);
				}
			});
			jMenuFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					System.out.println("jMenuFile.actionPerformed, event=" + evt);
					// TODO add your code for jMenuFile.actionPerformed
				}
			});
			jMenuFile.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent evt) {
					System.out.println("jMenuFile.keyTyped, event=" + evt);
					// TODO add your code for jMenuFile.keyTyped
				}
			});
		}
		return jMenuFile;
	}

	private JMenuItem getFile_ConfigMenuItem() {
		if (file_ConfigMenuItem == null) {
			file_ConfigMenuItem = new JMenuItem();
			file_ConfigMenuItem.setText("Configurations");
			file_ConfigMenuItem.setBounds(76, 20, 82, 19);
			file_ConfigMenuItem.setVisible(false);
			file_ConfigMenuItem.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent evt) {
					System.out.println("file_ConfigMenuItem.mousePressed, event=" + evt);
					// TODO add your code for file_ConfigMenuItem.mouseClicked
					JDialog config = new RoombaCommConfigUI();
					config.setAlwaysOnTop(true);
					config.setModal(true);
					config.setVisible(true);
					config.addWindowListener(new WindowListener() {
						
						public void windowOpened(WindowEvent arg0) {
							// TODO Auto-generated method stub
							
						}
						
						public void windowIconified(WindowEvent arg0) {
							// TODO Auto-generated method stub
							
						}
						
						public void windowDeiconified(WindowEvent arg0) {
							// TODO Auto-generated method stub
							
						}
						
						public void windowDeactivated(WindowEvent arg0) {
							// TODO Auto-generated method stub
							
						}
						
						public void windowClosing(WindowEvent arg0) {
							// TODO Auto-generated method stub
							System.out.println("dialog sent windowClosing");
						}
						
						public void windowClosed(WindowEvent arg0) {
							// TODO Auto-generated method stub
							
						}
						
						public void windowActivated(WindowEvent arg0) {
							// TODO Auto-generated method stub
							
						}
					});
					
				}
			});
		}
		return file_ConfigMenuItem;
	}

	private JMenuItem getFileMenuExit() {
		if (fileMenu_Exit == null) {
			fileMenu_Exit = new JMenuItem();
			fileMenu_Exit.setText("Exit");
			fileMenu_Exit.setBounds(82, 39, 82, 19);
			fileMenu_Exit.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent evt) {
					System.out.println("fileMenu_Exit.mousePressed, event=" + evt);
					// TODO add your code for fileMenu_Exit.mousePressed
					dispose();
				}
			});
		}
		return fileMenu_Exit;
	}

	private final class SysoutOnResize extends java.awt.event.ComponentAdapter {
		public void componentResized(java.awt.event.ComponentEvent e) {
			System.out.println(e.getComponent().getName() + " resized to " + e.getComponent().getWidth() + "," + e.getComponent().getHeight()); // TODO
																																				// Auto-generated
																																				// Event
																																				// stub
																																				// componentResized()
		}
	}
} // @jve:decl-index=0:visual-constraint="10,10"
