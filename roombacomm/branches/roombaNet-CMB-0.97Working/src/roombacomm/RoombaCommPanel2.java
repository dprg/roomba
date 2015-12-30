package roombacomm;

import java.awt.Container;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

import java.awt.Dimension;


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
public class RoombaCommPanel2 extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPanel jPanel_Connecct = null;
	private JComboBox protocal_Select = null;
	private JTextField protocalSelectLabel = null;
	private String[] protocols = {"Roomba 1xx-4xx (SCI)", "Roomba 5xx (OI)"};
	static RoombaCommPanel2 rcPanel;
	/**
	 * This method initializes jPanel_Connecct	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_Connecct() {
		if (jPanel_Connecct == null) {
			jPanel_Connecct = new JPanel();
			GridLayout jPanel_ConnecctLayout = new GridLayout(1, 1);
			jPanel_ConnecctLayout.setColumns(3);
			jPanel_ConnecctLayout.setHgap(5);
			jPanel_ConnecctLayout.setVgap(5);
			jPanel_Connecct.setLayout(jPanel_ConnecctLayout);
		}
		return jPanel_Connecct;
	}

	/**
	 * This method initializes protocal_Select	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getProtocal_Select() {
		if (protocal_Select == null) {
			protocal_Select = new JComboBox(protocols);
		}
		return protocal_Select;
	}

	/**
	 * This method initializes protocalSelectLabel	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getProtocalSelectLabel() {
		if (protocalSelectLabel == null) {
			protocalSelectLabel = new JTextField();
			protocalSelectLabel.setText("Protocal");
			protocalSelectLabel.setFocusable(false);
		}
		return protocalSelectLabel;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {  // use a Swing look-and-feel that's the same across all OSs
	          //MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
	          UIManager.setLookAndFeel( new MetalLookAndFeel() );
	        } catch(Exception e) { System.err.println("drat: "+e); }
	        
//	        addWindowListener(this);

//	        for( int i=0; i < args.length; i++ ) {
//	            if( args[i].endsWith("hwhandshake") )
//	                hwhandshake = true;
//	            else if (args[i].endsWith("debug"))
//	            	debug = true;
//	        }

	        rcPanel = new RoombaCommPanel2();
	        rcPanel.initialize();

//	        rcPanel.setShowHardwareHandhake( hwhandshake );

//	        Container content = getContentPane();
	        //content.setBackground(Color.lightGray);
//	        content.add( rcPanel ); // , BorderLayout.CENTER );

//	        setResizable(false);
//	        pack();
//	        setVisible(true);
	}

	/**
	 * This is the default constructor
	 */
	public RoombaCommPanel2() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints2.gridy = 0;
		gridBagConstraints2.weightx = 1.0;
		gridBagConstraints2.gridx = 1;
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.gridx = 1;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
		this.add(getJPanel_Connecct(), gridBagConstraints);
		this.add(getProtocal_Select(), gridBagConstraints1);
		this.add(getProtocalSelectLabel(), gridBagConstraints2);
	}

}
