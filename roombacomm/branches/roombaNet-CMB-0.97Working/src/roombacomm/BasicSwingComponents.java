package roombacomm;
/**
 * Example of components laid out in a grid
 */
public class BasicSwingComponents extends javax.swing.JFrame {
	private javax.swing.JButton ivjJButton1 = null;

	private javax.swing.JCheckBox ivjJCheckBox1 = null;

	private javax.swing.JComboBox ivjJComboBox1 = null;

	private javax.swing.JPanel ivjJFrameContentPane = null;

	private javax.swing.JLabel ivjJLabel1 = null;

	private javax.swing.JPasswordField ivjJPasswordField1 = null;

	private javax.swing.JProgressBar ivjJProgressBar1 = null;

	private javax.swing.JRadioButton ivjJRadioButton1 = null;

	private javax.swing.JScrollBar ivjJScrollBar1 = null;

	private javax.swing.JSlider ivjJSlider1 = null;

	private javax.swing.JTextArea ivjJTextArea1 = null;

	private javax.swing.JTextField ivjJTextField1 = null;

	private javax.swing.JToggleButton ivjJToggleButton1 = null;

	public BasicSwingComponents() {
		super();
		initialize();
	}

	/**
	 * Return the JButton1 property value.
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton1() {
		if (ivjJButton1 == null) {
			ivjJButton1 = new javax.swing.JButton();
			ivjJButton1.setName("JButton1");
			ivjJButton1.setText("JButton");
		}
		return ivjJButton1;
	}

	/**
	 * Return the JCheckBox1 property value.
	 * @return javax.swing.JCheckBox
	 */
	private javax.swing.JCheckBox getJCheckBox1() {
		if (ivjJCheckBox1 == null) {
			ivjJCheckBox1 = new javax.swing.JCheckBox();
			ivjJCheckBox1.setName("JCheckBox1");
			ivjJCheckBox1.setText("JCheckBox");
		}
		return ivjJCheckBox1;
	}

	/**
	 * Return the JComboBox1 property value.
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getJComboBox1() {
		if (ivjJComboBox1 == null) {
			ivjJComboBox1 = new javax.swing.JComboBox();
			ivjJComboBox1.setName("JComboBox1");
		}
		return ivjJComboBox1;
	}

	/**
	 * Return the JFrameContentPane property value.
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJFrameContentPane() {
		if (ivjJFrameContentPane == null) {
			ivjJFrameContentPane = new javax.swing.JPanel();
			ivjJFrameContentPane.setName("JFrameContentPane");
			ivjJFrameContentPane.setLayout(new java.awt.FlowLayout());
			getJFrameContentPane().add(getJButton1(), getJButton1().getName());
			getJFrameContentPane().add(getJCheckBox1(), getJCheckBox1().getName());
			getJFrameContentPane().add(getJRadioButton1(), getJRadioButton1().getName());
			getJFrameContentPane().add(getJToggleButton1(), getJToggleButton1().getName());
			getJFrameContentPane().add(getJLabel1(), getJLabel1().getName());
			getJFrameContentPane().add(getJTextField1(), getJTextField1().getName());
			getJFrameContentPane().add(getJPasswordField1(), getJPasswordField1().getName());
			getJFrameContentPane().add(getJTextArea1(), getJTextArea1().getName());
			getJFrameContentPane().add(getJSlider1(), getJSlider1().getName());
			getJFrameContentPane().add(getJScrollBar1(), getJScrollBar1().getName());
			getJFrameContentPane().add(getJComboBox1(), getJComboBox1().getName());
			getJFrameContentPane().add(getJProgressBar1(), getJProgressBar1().getName());
		}
		return ivjJFrameContentPane;
	}

	/**
	 * Return the JLabel1 property value.
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel1() {
		if (ivjJLabel1 == null) {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setText("JLabel");
		}
		return ivjJLabel1;
	}

	/**
	 * Return the JPasswordField1 property value.
	 * @return javax.swing.JPasswordField
	 */

	private javax.swing.JPasswordField getJPasswordField1() {
		if (ivjJPasswordField1 == null) {
			ivjJPasswordField1 = new javax.swing.JPasswordField();
			ivjJPasswordField1.setName("JPasswordField1");
		}
		return ivjJPasswordField1;
	}

	/**
	 * Return the JProgressBar1 property value.
	 * @return javax.swing.JProgressBar
	 */
	private javax.swing.JProgressBar getJProgressBar1() {
		if (ivjJProgressBar1 == null) {
			ivjJProgressBar1 = new javax.swing.JProgressBar();
			ivjJProgressBar1.setName("JProgressBar1");
			ivjJProgressBar1.setValue(50);
		}
		return ivjJProgressBar1;
	}

	/**
	 * Return the JRadioButton1 property value.
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getJRadioButton1() {
		if (ivjJRadioButton1 == null) {
			ivjJRadioButton1 = new javax.swing.JRadioButton();
			ivjJRadioButton1.setName("JRadioButton1");
			ivjJRadioButton1.setText("JRadioButton");
		}
		return ivjJRadioButton1;
	}

	/**
	 * Return the JScrollBar1 property value.
	 * @return javax.swing.JScrollBar
	 */
	private javax.swing.JScrollBar getJScrollBar1() {
		if (ivjJScrollBar1 == null) {
			ivjJScrollBar1 = new javax.swing.JScrollBar();
			ivjJScrollBar1.setName("JScrollBar1");
		}
		return ivjJScrollBar1;
	}

	/**
	 * Return the JSlider1 property value.
	 * @return javax.swing.JSlider
	 */
	private javax.swing.JSlider getJSlider1() {
		if (ivjJSlider1 == null) {
			ivjJSlider1 = new javax.swing.JSlider();
			ivjJSlider1.setName("JSlider1");
		}
		return ivjJSlider1;
	}

	/**
	 * Return the JTextArea1 property value.
	 * @return javax.swing.JTextArea
	 */
	private javax.swing.JTextArea getJTextArea1() {
		if (ivjJTextArea1 == null) {
			ivjJTextArea1 = new javax.swing.JTextArea();
			ivjJTextArea1.setName("JTextArea1");
			ivjJTextArea1.setRows(3);
			ivjJTextArea1.setColumns(7);
		}
		return ivjJTextArea1;
	}

	/**
	 * Return the JTextField1 property value.
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextField1() {
		if (ivjJTextField1 == null) {
			ivjJTextField1 = new javax.swing.JTextField();
			ivjJTextField1.setName("JTextField1");
			ivjJTextField1.setText("JTextField");
		}
		return ivjJTextField1;
	}

	/**
	 * Return the JToggleButton1 property value.
	 * @return javax.swing.JToggleButton
	 */
	private javax.swing.JToggleButton getJToggleButton1() {
		if (ivjJToggleButton1 == null) {
			ivjJToggleButton1 = new javax.swing.JToggleButton();
			ivjJToggleButton1.setName("JToggleButton1");
			ivjJToggleButton1.setText("JToggleButton");
		}
		return ivjJToggleButton1;
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {

		this.setName("JFrame1");
		this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.setBounds(45, 25, 317, 273);
		this.setTitle("BasicSwingComponents");
		this.setContentPane(getJFrameContentPane());

	}
}
