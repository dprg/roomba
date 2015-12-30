package roombacomm;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

public class RoombaCommConfigUI extends JDialog {
	private JPanel connectDetails;
	private JTabbedPane connectDetailsTabs;
	private JPanel serialDetailsUI;
	private JButton Done;
	private JPanel config_buttons;
	private JTabbedPane jTabbedPane1;
	private JPanel configurationSettings;
	private JMenu jMenu1;
	private JMenuBar jMenuBar1;
	private JTextField netPort;
	private JLabel Port;
	private JTextField netHost;
	private JLabel netHostText;
	private JPanel netDetailsUI;
	private JComboBox serialCombo;
	private JLabel serialComboText;
	private RoombaCommConfigUI me;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("ran main");
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				System.out.println("ran main.run");
				RoombaCommConfigUI thisClass = new RoombaCommConfigUI();
//				thisClass.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				thisClass.setVisible(true);
//				thisClass.setSize(350, 350);
				System.out.println("should be able to see it now");
			}
		});
	}
	/**
	 * This is the default constructor
	 */
	public RoombaCommConfigUI() {
		super();
		me = this;
		initialize();
	}
	private void initialize() {
		System.out.println("ran initGUI");
		try {
			this.setTitle("Configuration Settings");
			this.setAlwaysOnTop(true);
			this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//			this.setMinimumSize(new java.awt.Dimension(350, 350));
			this.setModal(true);
			{
				configurationSettings = new JPanel();
				GridBagLayout configurationSettingsLayout = new GridBagLayout();
				getContentPane().add(configurationSettings, BorderLayout.CENTER);
				configurationSettingsLayout.rowWeights = new double[] {0.1};
				configurationSettingsLayout.rowHeights = new int[] {7};
				configurationSettingsLayout.columnWeights = new double[] {0.1};
				configurationSettingsLayout.columnWidths = new int[] {7};
				configurationSettings.setLayout(configurationSettingsLayout);
				configurationSettings.setBorder(BorderFactory.createTitledBorder(null, "Configuration settings", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION));
				configurationSettings.setPreferredSize(new java.awt.Dimension(300, 215));
				{
					jTabbedPane1 = new JTabbedPane();
					configurationSettings.add(jTabbedPane1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
					configurationSettings.add(getConfig_buttons(), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.LAST_LINE_END, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
					{
						serialDetailsUI = new JPanel();
						jTabbedPane1.addTab("serialDetailsUI", null, serialDetailsUI, null);
					}
					{
						netDetailsUI = new JPanel();
						GridBagLayout netDetailsUILayout = new GridBagLayout();
						netDetailsUI.setLayout(new GridBagLayout());
						netDetailsUI.add(getNetHostText(), new GridBagConstraints(-1, -1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
						netDetailsUI.add(getNetHost(), new GridBagConstraints(-1, -1, 1, 1, 15.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
						netDetailsUI.add(getPort(), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
						netDetailsUI.add(getNetPort(), new GridBagConstraints(1, 1, 1, 1, 15.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
						jTabbedPane1.addTab("netDetailsUI", null, netDetailsUI, null);
					}
				}
			}
			pack();
		} catch(Exception e) {
			e.printStackTrace();
		}
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
	
	private JPanel getConfig_buttons() {
		if(config_buttons == null) {
			config_buttons = new JPanel();
			FlowLayout config_buttonsLayout = new FlowLayout();
			config_buttonsLayout.setAlignment(FlowLayout.RIGHT);
			config_buttons.setLayout(config_buttonsLayout);
			config_buttons.add(getDone());
		}
		return config_buttons;
	}
	
	private JButton getDone() {
		if(Done == null) {
			Done = new JButton();
			Done.setText("Done");
			Done.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					System.out.println("Done.mouseClicked, event="+evt);
					//TODO add your code for Done.mouseClicked
					me.dispose();
				}
			});
		}
		return Done;
	}
}
