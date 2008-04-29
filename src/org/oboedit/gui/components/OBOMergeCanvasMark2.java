package org.oboedit.gui.components;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.bbop.dataadapter.DataAdapterException;
import org.bbop.framework.AbstractGUIComponent;
import org.bbop.framework.GUIComponent;
import org.bbop.framework.dock.LayoutAdapter;
import org.bbop.framework.dock.LayoutListener;
import org.bbop.swing.MinusIcon;
import org.bbop.swing.PlusIcon;
import javax.swing.JFrame;


/*
 * By Jennifer Deegan and Nicolas Rodriguez
 * EMBL-EBI
 * January to April 2008
 */

public class OBOMergeCanvasMark2 extends AbstractGUIComponent {


	/**
	 * @author  Ranganath Kini
	 * @see      javax.swing.JTextArea
	 * http://www.jcreator.com/forums/index.php?showtopic=773
	 */
	public class TextAreaOutputStream extends OutputStream {
		private JTextArea textControl;

		public TextAreaOutputStream(JTextArea control) {
			textControl = control;
		}

		public void write(int b) throws IOException {
			// append the data as characters to the JTextArea control
			textControl.append(String.valueOf((char) b));
		}
	}
	/**
	 * @author  Ranganath Kini
	 * @see      javax.swing.JTextArea
	 * http://www.jcreator.com/forums/index.php?showtopic=773
	 */
	private class TextAreaOutputStream1 extends OutputStream {
		private JTextArea textControl;
		public void TextAreaOutputStream(JTextArea control) {
			textControl = control;
		}
		@Override
		public void write(int b) throws IOException {
			// append the data as characters to the JTextArea control
			textControl.append(String.valueOf((char) b));
		}
	}
//	public static final int HIDE_ON_CLOSE = 1;
//	public static void main(String args[]) {
//	java.awt.EventQueue.invokeLater(new Runnable() {
//	public void run() {
//	new OBOMergeCanvasMark2().setVisible(true);
//	}
//	});
//	}
	private static void addAComponentXAlignment(JComponent componentName, Container container) {
//		componentName.setAlignmentX(Component.CENTER_ALIGNMENT);
		container.add(componentName);

	}
	private static void addAComponentYAlignment(JComponent componentName, Container container) {
//		componentName.setAlignmentY(Component.CENTER_ALIGNMENT);
		container.add(componentName);

	}
	private int defaultCloseOperation = HIDE_ON_CLOSE;
	public static final int HIDE_ON_CLOSE = 1;
	protected LayoutListener layoutListener = new LayoutAdapter() {
		public boolean closing(GUIComponent c) {
			if (c.equals(OBOMergeCanvasMark2.this)) {
				save();
			}
			return true;
		}

	};
	JPanel inputFilePanel = new JPanel();
	JPanel filePathPanel = new JPanel();
	JPanel mergeOptionPanel = new JPanel();
	JPanel saveProfilePanel = new JPanel();
	JPanel parentFilePanel = new JPanel();
	JPanel liveFilePanel = new JPanel();
	JPanel branchFilePanel = new JPanel();
	JPanel mergedFilePanel = new JPanel();
	JLabel parentFileLabel = new JLabel("Parent File");
	JTextField parentFileTextField = new JTextField("");
	JButton parentFileBrowseButton = new JButton("Browse");
	JLabel branchFileLabel = new JLabel("Branch File");
	JTextField branchFileTextField = new JTextField("");
	JButton branchFileBrowseButton = new JButton("Browse");
	JLabel liveFileLabel = new JLabel("Live File   ");
	JTextField liveFileTextField = new JTextField("");
	JButton liveFileBrowseButton = new JButton("Browse");
	JLabel mergedFileLabel = new JLabel("Merged File");
	JTextField mergedFileTextField = new JTextField("");
	JButton mergedFileBrowseButton = new JButton("Browse");
	JLabel saveProfileLabel = new JLabel("Save Profile");
	String[] savedProfiles = {"new profile", ""};
	JComboBox saveProfileComboBox = new JComboBox(savedProfiles);
	JButton addProfilePlusButton = new JButton(new PlusIcon(1.5f, 8, 8));
	JButton removeProfileMinusButton = new JButton(new MinusIcon(1.5f, 8, 8));
	JPanel topLinePanel = new JPanel();
	JPanel bottomLinePanel = new JPanel();
	JLabel updateIDsLabel = new JLabel("Update IDs");
	String[] idOptionsFailOnClash = { "IF_LIKELY", "   ", "NEVER", "ALWAYS" };
	String[] idOptionsUpdateIDs = { "   ", "NEVER", "ALWAYS", "IF_LIKELY" };
	JComboBox updateIDsCombobox = new JComboBox(idOptionsUpdateIDs);
	JLabel mergedFileFormatLabel = new JLabel("Output File Format");
	String[] fileFormatOptions = { "OBO_1_2", "OBO_1_0" };
	JComboBox mergedFileFormatCombobox = new JComboBox(fileFormatOptions);
	JComponent failOnClashLabel = new JLabel("Fail On Clash");
	JComboBox failOnClashCombobox = new JComboBox(idOptionsFailOnClash);
	JLabel ignoreClashOnIDsLabel = new JLabel("Ignore Clash on ID");
	JTextArea ignoreClashOnIDsTextArea = new JTextArea();
	String id;
	JTabbedPane oboMergeTabbedPane = new JTabbedPane();
	JPanel processFeedbackPanel = new JPanel();
	JButton saveFeedbackToFileBrowseButton = new JButton("Browse");
	JLabel saveFeedbackToFileLabel = new JLabel("Save Feedback to File");
	JTextField saveFeedbackToFileTextField = new JTextField();
	JTextField ProgressTextField = new JTextField();
	String saveFeedbackToFileTextFieldString = new String("C:\\output.txt");
	JFileChooser fileChooser = new JFileChooser();
	String updateIDsChoiceString = new String("   ");
	String failOnClashChoiceString = new String("IF_LIKELY");
	String outputFormatChoiceString = new String("OBO_1_2");
	String[] obomergeArgsArray = new String[0];
	JButton mergeButton = new JButton("Merge");
	JFrame missingPathFrame = new JFrame();
	ArrayList<String> obomergeArgsArrayList = new ArrayList<String>();
	String parentFileTextFieldString = new String();
	String liveFileTextFieldString = new String();
	String branchFileTextFieldString = new String();
	String mergedFileTextFieldString = new String();
	JPanel finalOptionPanel = new JPanel();
	JButton advancedButton = new JButton("Advanced");
	JPanel showProgressPanel = new JPanel();
	JPanel saveProgressToFilePanel = new JPanel();
	JPanel centerPanel = new JPanel();
	JPanel saveFeedbackToFileDetailPanel = new JPanel();
	String feedbackTextAreaString = new String();
	JTextArea feedbackTextArea = new JTextArea(feedbackTextAreaString);
	JLabel feedbackFilePathLabel = new JLabel("Feedback File Path");
	JTextField feedbackFileTextField = new JTextField();
	String ignoreClashOnIDsChoiceString = new String();
	PrintStream feedbackFileOutputStream;

	public OBOMergeCanvasMark2(String id) {
		super(id);
	}

	@Override
	public void init() {

		setLayout(new BorderLayout());
		JPanel mainGUIPanel = new JPanel();

		JScrollPane mainGUIPanelScrollPane = new JScrollPane(mainGUIPanel);

		
		add(oboMergeTabbedPane, "Center");
		oboMergeTabbedPane.addTab("Ontology Files", null, mainGUIPanelScrollPane, "Ontology Files");
		oboMergeTabbedPane.addTab("Process Feedback", null, processFeedbackPanel, "Process Feedback");

		
		
//		mainGUIPanel.setLayout(new BorderLayout());
		mainGUIPanel.setLayout(new GridBagLayout());
		GridBagConstraints mainGUIPanelGBC = new GridBagConstraints();

		mainGUIPanelGBC.fill = GridBagConstraints.HORIZONTAL;
		mainGUIPanelGBC.gridx = 0;
		mainGUIPanelGBC.gridy = 0;
		mainGUIPanelGBC.anchor = GridBagConstraints.PAGE_START;
		mainGUIPanelGBC.weightx = 1;
		mainGUIPanelGBC.insets = new Insets(5,5,5,5);
		mainGUIPanelGBC.gridwidth = 4;
		mainGUIPanelGBC.gridheight = 1;
		mainGUIPanel.add(saveProfilePanel, mainGUIPanelGBC);
		saveProfilePanel.setBorder(new TitledBorder ("Saved Profiles"));

		mainGUIPanelGBC.gridx = 0;
		mainGUIPanelGBC.gridy = 1;
		mainGUIPanelGBC.gridwidth = 4;
		mainGUIPanelGBC.gridheight = 2;
		mainGUIPanel.add(centerPanel, mainGUIPanelGBC);
		centerPanel.setBorder(new TitledBorder ("Center Panel"));

		mainGUIPanelGBC.gridx = 0;
		mainGUIPanelGBC.gridy = 3;
		mainGUIPanelGBC.gridwidth = 4;
		mainGUIPanelGBC.gridheight = 1;
		mainGUIPanel.add(mergeOptionPanel, mainGUIPanelGBC);
		mergeOptionPanel.setBorder(new TitledBorder ("Merge Options"));


		mainGUIPanelGBC.gridx = 0;
		mainGUIPanelGBC.gridy = 4;
		mainGUIPanelGBC.gridwidth = 4;
		mainGUIPanelGBC.gridheight = 1;
		mainGUIPanel.add(finalOptionPanel, mainGUIPanelGBC);
		mergeOptionPanel.setBorder(new TitledBorder ("Merge Options"));

				
		
		centerPanel.setLayout(new GridBagLayout());
		GridBagConstraints centerPanelGBC = new GridBagConstraints();

		centerPanelGBC.gridx = 0;
		centerPanelGBC.gridy = 0;
		centerPanelGBC.weightx = 1;
		centerPanelGBC.gridheight = 1;
		centerPanelGBC.gridwidth = 1;
		centerPanelGBC.anchor = GridBagConstraints.FIRST_LINE_START;
		centerPanel.add(inputFilePanel, mainGUIPanelGBC);
		inputFilePanel.setBorder(new TitledBorder ("Ontology File Paths"));

//		centerPanelGBC.gridheight = 1;
//		centerPanelGBC.gridwidth = 1;
//		centerPanelGBC.gridx = 1;
//		centerPanelGBC.gridy = 0;
//		centerPanelGBC.anchor = GridBagConstraints.FIRST_LINE_END;
//		centerPanel.add(finalOptionPanel, centerPanelGBC);
//		finalOptionPanel.setBorder(new TitledBorder ("Final Options"));


		//Make GridBag layout for the contents of the inputFilePanel. 
		inputFilePanel.setLayout(new GridBagLayout());
		GridBagConstraints inputFilePanelGBC = new GridBagConstraints();

		inputFilePanelGBC.fill = GridBagConstraints.HORIZONTAL;
		inputFilePanelGBC.gridx = 0;
		inputFilePanelGBC.gridy = 0;
		inputFilePanelGBC.anchor = GridBagConstraints.FIRST_LINE_START;
		inputFilePanelGBC.weightx = 1;
		inputFilePanelGBC.insets = new Insets(5,5,5,5);
		//Add the four horizontal panels to take a path each. 
		inputFilePanel.add(parentFilePanel, inputFilePanelGBC);

		inputFilePanelGBC.gridx = 0;
		inputFilePanelGBC.gridy = 1;

		inputFilePanel.add(liveFilePanel, inputFilePanelGBC);
		inputFilePanelGBC.gridx = 0;
		inputFilePanelGBC.gridy = 2;

		inputFilePanel.add(branchFilePanel, inputFilePanelGBC);
		inputFilePanelGBC.gridx = 0;
		inputFilePanelGBC.gridy = 3;

		inputFilePanel.add(mergedFilePanel, inputFilePanelGBC);




		//set up the contents for the parent file path. 
		parentFilePanel.setLayout(new GridBagLayout());
		GridBagConstraints parentFilePanelGBC = new GridBagConstraints();

		parentFilePanelGBC.fill = GridBagConstraints.NONE;
		parentFilePanelGBC.gridx = 0;
		parentFilePanelGBC.gridy = 0;
		parentFilePanelGBC.anchor = GridBagConstraints.LINE_START;
		parentFilePanelGBC.insets = new Insets(0,2,2,0);
		parentFilePanel.add(parentFileLabel, parentFilePanelGBC);

		parentFilePanelGBC.fill = GridBagConstraints.HORIZONTAL;
		parentFilePanelGBC.gridx = 1;
		parentFilePanelGBC.gridy = 0;
		parentFilePanelGBC.anchor = GridBagConstraints.CENTER;
		parentFilePanelGBC.weightx = 1;

		parentFilePanel.add(parentFileTextField, parentFilePanelGBC);

		parentFilePanelGBC.fill = GridBagConstraints.NONE;
		parentFilePanelGBC.gridx = 2;
		parentFilePanelGBC.gridy = 0;
		parentFilePanelGBC.anchor = GridBagConstraints.LINE_END;
		parentFilePanelGBC.weightx = 0;

		parentFilePanel.add(parentFileBrowseButton, parentFilePanelGBC);

		//set up the contents for the parent file path. 
		liveFilePanel.setLayout(new GridBagLayout());
		GridBagConstraints liveFilePanelGBC = new GridBagConstraints();

		liveFilePanelGBC.fill = GridBagConstraints.NONE;
		liveFilePanelGBC.gridx = 0;
		liveFilePanelGBC.gridy = 0;
		liveFilePanelGBC.anchor = GridBagConstraints.LINE_START;
		liveFilePanelGBC.insets = new Insets(0,2,2,0);
		liveFilePanel.add(liveFileLabel, liveFilePanelGBC);

		liveFilePanelGBC.fill = GridBagConstraints.HORIZONTAL;
		liveFilePanelGBC.gridx = 1;
		liveFilePanelGBC.gridy = 0;
		liveFilePanelGBC.anchor = GridBagConstraints.CENTER;
		liveFilePanelGBC.weightx = 1;

		liveFilePanel.add(liveFileTextField, liveFilePanelGBC);

		liveFilePanelGBC.fill = GridBagConstraints.NONE;
		liveFilePanelGBC.gridx = 2;
		liveFilePanelGBC.gridy = 0;
		liveFilePanelGBC.anchor = GridBagConstraints.LINE_END;
		liveFilePanelGBC.weightx = 0;

		liveFilePanel.add(liveFileBrowseButton, liveFilePanelGBC);

		//set up the contents for the branch file path. 
		branchFilePanel.setLayout(new GridBagLayout());
		GridBagConstraints branchFilePanelGBC = new GridBagConstraints();

		branchFilePanelGBC.fill = GridBagConstraints.NONE;
		branchFilePanelGBC.gridx = 0;
		branchFilePanelGBC.gridy = 0;
		branchFilePanelGBC.anchor = GridBagConstraints.LINE_START;
		branchFilePanelGBC.insets = new Insets(0,2,2,0);
		branchFilePanel.add(branchFileLabel, branchFilePanelGBC);

		branchFilePanelGBC.fill = GridBagConstraints.HORIZONTAL;
		branchFilePanelGBC.gridx = 1;
		branchFilePanelGBC.gridy = 0;
		branchFilePanelGBC.anchor = GridBagConstraints.CENTER;
		branchFilePanelGBC.weightx = 1;

		branchFilePanel.add(branchFileTextField, branchFilePanelGBC);

		branchFilePanelGBC.fill = GridBagConstraints.NONE;
		branchFilePanelGBC.gridx = 2;
		branchFilePanelGBC.gridy = 0;
		branchFilePanelGBC.anchor = GridBagConstraints.LINE_END;
		branchFilePanelGBC.weightx = 0;

		branchFilePanel.add(branchFileBrowseButton, branchFilePanelGBC);

		//set up the contents for the merged file path. 
		mergedFilePanel.setLayout(new GridBagLayout());
		GridBagConstraints mergedFilePanelGBC = new GridBagConstraints();

		mergedFilePanelGBC.fill = GridBagConstraints.NONE;
		mergedFilePanelGBC.gridx = 0;
		mergedFilePanelGBC.gridy = 0;
		mergedFilePanelGBC.anchor = GridBagConstraints.LINE_START;
		mergedFilePanelGBC.insets = new Insets(0,2,2,0);
		mergedFilePanel.add(mergedFileLabel, mergedFilePanelGBC);

		mergedFilePanelGBC.fill = GridBagConstraints.HORIZONTAL;
		mergedFilePanelGBC.gridx = 1;
		mergedFilePanelGBC.gridy = 0;
		mergedFilePanelGBC.anchor = GridBagConstraints.CENTER;
		mergedFilePanelGBC.weightx = 1;

		mergedFilePanel.add(mergedFileTextField, mergedFilePanelGBC);

		mergedFilePanelGBC.fill = GridBagConstraints.NONE;
		mergedFilePanelGBC.gridx = 2;
		mergedFilePanelGBC.gridy = 0;
		mergedFilePanelGBC.anchor = GridBagConstraints.LINE_END;
		mergedFilePanelGBC.weightx = 0;

		mergedFilePanel.add(mergedFileBrowseButton, mergedFilePanelGBC);

		saveProfilePanel.setLayout(new GridBagLayout());
		GridBagConstraints saveProfilePanelGBC = new GridBagConstraints();

		saveProfilePanelGBC.fill = GridBagConstraints.NONE;
		saveProfilePanelGBC.gridx = 0;
		saveProfilePanelGBC.gridy = 0;
		saveProfilePanelGBC.anchor = GridBagConstraints.LINE_START;
		saveProfilePanelGBC.insets = new Insets(5,5,5,5);
		saveProfilePanel.add(saveProfileLabel, saveProfilePanelGBC);

		saveProfilePanelGBC.fill = GridBagConstraints.HORIZONTAL;
		saveProfilePanelGBC.gridx = 1;
		saveProfilePanelGBC.gridy = 0;
		saveProfilePanelGBC.anchor = GridBagConstraints.CENTER;
		saveProfilePanelGBC.weightx = 1;

		saveProfilePanel.add(saveProfileComboBox, saveProfilePanelGBC);
		saveProfileComboBox.setEditable(true);

		saveProfilePanelGBC.fill = GridBagConstraints.NONE;
		saveProfilePanelGBC.gridx = 2;
		saveProfilePanelGBC.gridy = 0;
//		saveProfilePanelGBC.anchor = GridBagConstraints.CENTER;
		saveProfilePanelGBC.weightx = 0;

		saveProfilePanel.add(addProfilePlusButton, saveProfilePanelGBC);

		saveProfilePanelGBC.fill = GridBagConstraints.NONE;
		saveProfilePanelGBC.gridx = 3;
		saveProfilePanelGBC.gridy = 0;
		saveProfilePanelGBC.anchor = GridBagConstraints.LINE_END;
		saveProfilePanelGBC.weightx = 0;

		saveProfilePanel.add(removeProfileMinusButton, saveProfilePanelGBC);


		mergeOptionPanel.setLayout(new GridBagLayout());
		GridBagConstraints mergeOptionPanelGBC = new GridBagConstraints();

		mergeOptionPanelGBC.fill = GridBagConstraints.NONE;
		mergeOptionPanelGBC.gridx = 0;
		mergeOptionPanelGBC.gridy = 0;
		mergeOptionPanelGBC.ipadx = 5;
		mergeOptionPanelGBC.ipady = 5;
		mergeOptionPanelGBC.insets = new Insets(5,5,5,5);
//		mergeOptionPanelGBC.anchor = GridBagConstraints.CENTER;
//		mergeOptionPanelGBC.weightx = 1;
		mergeOptionPanel.add(updateIDsLabel, mergeOptionPanelGBC);

		mergeOptionPanelGBC.gridx = 1;
		mergeOptionPanelGBC.gridy = 0;
		mergeOptionPanel.add(updateIDsCombobox, mergeOptionPanelGBC);

		mergeOptionPanelGBC.gridx = 2;
		mergeOptionPanelGBC.gridy = 0;
		mergeOptionPanel.add(mergedFileFormatLabel, mergeOptionPanelGBC);

		mergeOptionPanelGBC.gridx = 3;
		mergeOptionPanelGBC.gridy = 0;
		mergeOptionPanel.add(mergedFileFormatCombobox, mergeOptionPanelGBC);

		mergeOptionPanelGBC.gridx = 0;
		mergeOptionPanelGBC.gridy = 1;
		mergeOptionPanel.add(failOnClashLabel, mergeOptionPanelGBC);

		mergeOptionPanelGBC.gridx = 1;
		mergeOptionPanelGBC.gridy = 1;
		mergeOptionPanel.add(failOnClashCombobox, mergeOptionPanelGBC);

		mergeOptionPanelGBC.gridx = 2;
		mergeOptionPanelGBC.gridy = 1;
		mergeOptionPanel.add(ignoreClashOnIDsLabel, mergeOptionPanelGBC);

		mergeOptionPanelGBC.gridx = 3;
		mergeOptionPanelGBC.gridy = 1;
		mergeOptionPanel.add(ignoreClashOnIDsTextArea, mergeOptionPanelGBC);

		finalOptionPanel.setLayout(new GridBagLayout());
		GridBagConstraints finalOptionPanelGBC = new GridBagConstraints();

		finalOptionPanelGBC.fill = GridBagConstraints.NONE;
		finalOptionPanelGBC.gridx = 0;
		finalOptionPanelGBC.gridy = 0;
		//	finalOptionPanelGBC.anchor = GridBagConstraints.FIRST_LINE_START;
		finalOptionPanelGBC.weightx = 1;
//		finalOptionPanelGBC.insets = new Insets(5,5,5,5);
		finalOptionPanel.add(advancedButton, finalOptionPanelGBC);

		finalOptionPanelGBC.gridx = 1;
		finalOptionPanel.add(mergeButton, finalOptionPanelGBC);

		processFeedbackPanel.setLayout(new GridBagLayout());
		GridBagConstraints processFeedbackPanelGBC = new GridBagConstraints();

		JScrollPane feedbackTextAreaScrollPane = new JScrollPane(feedbackTextArea);

		processFeedbackPanelGBC.fill = GridBagConstraints.BOTH;
		processFeedbackPanelGBC.gridx = 0;
		processFeedbackPanelGBC.gridy = 1;
		processFeedbackPanelGBC.anchor = GridBagConstraints.PAGE_END;
		processFeedbackPanelGBC.weightx = 1;
		processFeedbackPanelGBC.weighty = 1;
		processFeedbackPanelGBC.insets = new Insets(5,5,5,5);
		processFeedbackPanel.add(feedbackTextAreaScrollPane, processFeedbackPanelGBC);

		processFeedbackPanelGBC.fill = GridBagConstraints.HORIZONTAL;
		processFeedbackPanelGBC.gridy = 0;
		processFeedbackPanelGBC.weightx = 1;
		processFeedbackPanelGBC.weighty = 0;
		processFeedbackPanelGBC.anchor = GridBagConstraints.PAGE_START;
		processFeedbackPanel.add(saveFeedbackToFileDetailPanel, processFeedbackPanelGBC);


		saveFeedbackToFileDetailPanel.setLayout(new GridBagLayout());
		GridBagConstraints saveFeedbackToFileDetailPanelGBC = new GridBagConstraints();

		saveFeedbackToFileDetailPanelGBC.fill = GridBagConstraints.NONE;
		saveFeedbackToFileDetailPanelGBC.gridx = 0;
		saveFeedbackToFileDetailPanelGBC.gridy = 0;
		saveFeedbackToFileDetailPanelGBC.weightx = 0;
		saveFeedbackToFileDetailPanelGBC.insets = new Insets(5,5,5,5);
		saveFeedbackToFileDetailPanel.add(feedbackFilePathLabel, saveFeedbackToFileDetailPanelGBC);

		saveFeedbackToFileDetailPanelGBC.fill = GridBagConstraints.HORIZONTAL;
		saveFeedbackToFileDetailPanelGBC.gridx = 1;
		saveFeedbackToFileDetailPanelGBC.gridy = 0;
		saveFeedbackToFileDetailPanelGBC.weightx = 1;
		saveFeedbackToFileDetailPanel.add(saveFeedbackToFileTextField, saveFeedbackToFileDetailPanelGBC);

		saveFeedbackToFileDetailPanelGBC.fill = GridBagConstraints.NONE;
		saveFeedbackToFileDetailPanelGBC.gridx = 2;
		saveFeedbackToFileDetailPanelGBC.gridy = 0;
		saveFeedbackToFileDetailPanelGBC.weightx = 0;
		saveFeedbackToFileDetailPanel.add(saveFeedbackToFileBrowseButton, saveFeedbackToFileDetailPanelGBC);


		validate();
		repaint();


		saveFeedbackToFileBrowseButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveFeedbackToFileBrowseButtonActionPerformed(evt);
			}
		});

		mergedFileFormatCombobox
		.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				mergedFileFormatComboboxActionPerformed(evt);
			}
		});

		updateIDsCombobox
		.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				updateIDsComboboxActionPerformed(evt);
			}
		});

		failOnClashCombobox
		.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				failOnClashChoiceComboBoxActionPerformed(evt);
			}
		});


		parentFileBrowseButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				parentFileButtonActionPerformed(evt);
			}
		});

		mergedFileTextField
		.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				mergedFileTextFieldActionPerformed(evt);
			}
		});

		branchFileBrowseButton
		.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				branchFileBrowseButtonActionPerformed(evt);
			}
		});

		liveFileBrowseButton
		.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				liveFileBrowseButtonActionPerformed(evt);
			}
		});

		mergeButton
		.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				mergeButtonActionPerformed(evt);
			}
		});

		mergedFileBrowseButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				mergedFileBrowseButtonActionPerformed(evt);
			}
		});
		
		
		liveFileTextField
		.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				liveFileTextFieldActionPerformed(evt);
			}
		});


		advancedButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				advancedButtonActionPerformed(evt);
			}

			private void advancedButtonActionPerformed(ActionEvent evt) {
				boolean saveProfilePanelVisibility = saveProfilePanel.isVisible();
				saveProfilePanel.setVisible(!saveProfilePanelVisibility);

				boolean mergeOptionPanelVisibility = mergeOptionPanel.isVisible();
				mergeOptionPanel.setVisible(!mergeOptionPanelVisibility);

				if (saveProfilePanelVisibility) {
					advancedButton.setText("Advanced");
				} else {
					advancedButton.setText("Basic");
				}			
			}
		});
		saveProfilePanel.setVisible(false);
		mergeOptionPanel.setVisible(false);
	}







	public void save(){
		System.out.println("all going well so far.");
	}

	private void branchFileBrowseButtonActionPerformed(
			java.awt.event.ActionEvent evt) {
		int showOpenDialogReturnValue = fileChooser.showOpenDialog(null);
		if (showOpenDialogReturnValue == JFileChooser.APPROVE_OPTION) {
			File SecondaryEditedChosenFile = fileChooser.getSelectedFile();
			branchFileTextField.setText(SecondaryEditedChosenFile
					.getAbsolutePath());
			branchFileTextFieldString = branchFileTextField
			.getText();

			System.out.println("arg = " + branchFileTextFieldString);

		}

	}

	private void failOnClashChoiceComboBoxActionPerformed(
			java.awt.event.ActionEvent evt) {
		failOnClashChoiceString = (String) failOnClashCombobox
		.getSelectedItem();
		System.out.println("arg = " + failOnClashChoiceString);

	};

	private int getDefaultCloseOperation() {
		// TODO Auto-generated method stub
		return defaultCloseOperation;

	}
	private void liveFileTextFieldActionPerformed(
			java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
	}

	private Boolean makeArgArrayList() {
		/*
		 * This class takes the return strings from all the
		 *  GUI controls and puts them
		 * into the array to be fed to obomerge.
		 */
		System.out.println("Arguments applied are:");

		if (failOnClashChoiceString != "   ") {
			obomergeArgsArrayList.add("-fail-on-clash");
			obomergeArgsArrayList.add(failOnClashChoiceString);
			System.out.println("    -fail-on-clash " + failOnClashChoiceString);

		}
//		Need to fix this. The "!=" is not working. 
		//				if (updateIDsChoiceString != "   ") {
//		obomergeArgsArrayList.add("-update-ids");
//		obomergeArgsArrayList.add(updateIDsChoiceString);
//		System.out.println("    -update-ids " + updateIDsChoiceString);
//		}
		//This feature not fully implemented. 
//		if (ignoreClashOnIDsChoiceString != "") {
//		obomergeArgsArrayList.add("-ignore-clash-on-id");
//		obomergeArgsArrayList.add(ignoreClashOnIDsChoiceString);
//		System.out.println("    -ignore-clash-on-id "
//		+ ignoreClashOnIDsChoiceString);
//		}


		parentFileTextFieldString = parentFileTextField.getText();
		liveFileTextFieldString = liveFileTextField.getText();
		branchFileTextFieldString = branchFileTextField
		.getText();
		mergedFileTextFieldString = mergedFileTextField.getText();

		if (parentFileTextFieldString.length() == 0
				|| liveFileTextFieldString.length() == 0
				|| branchFileTextFieldString.length() == 0
				|| mergedFileTextFieldString.length() == 0) {

			JOptionPane.showMessageDialog(missingPathFrame,
					"Please fill in all of the necessary file paths.",
					"Missing Information", getDefaultCloseOperation());

			return false;
		}
		obomergeArgsArrayList.add("-version");
		obomergeArgsArrayList.add(outputFormatChoiceString);
		System.out.println("    -version " + outputFormatChoiceString);

		obomergeArgsArrayList.add("-original");
		obomergeArgsArrayList.add(parentFileTextFieldString);
		System.out.println("    -original " + parentFileTextFieldString);

		obomergeArgsArrayList.add("-revision");
		obomergeArgsArrayList.add(liveFileTextFieldString);
		System.out.println("    -revision " + liveFileTextFieldString);

		obomergeArgsArrayList.add("-revision");
		obomergeArgsArrayList.add(branchFileTextFieldString);
		System.out.println("    -revision "
				+ branchFileTextFieldString);

		obomergeArgsArrayList.add("-o");
		obomergeArgsArrayList.add(mergedFileTextFieldString);
		System.out.println("    -o " + mergedFileTextFieldString);

		System.out.println(obomergeArgsArrayList);
		obomergeArgsArrayList.trimToSize();
		obomergeArgsArray = obomergeArgsArrayList.toArray(obomergeArgsArray);
		return true;
	}

	private void mergeButtonActionPerformed(java.awt.event.ActionEvent evt) {

		if (makeArgArrayList() == true) {
			try {
				WriteFeedbackToFile();
				org.oboedit.launcher.OBOMerge.main(obomergeArgsArray);
				ShowFeedbackInWindow();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DataAdapterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void mergedFileBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {
		int returnVal = fileChooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			mergedFileTextFieldString = fileChooser.getSelectedFile()
			.getAbsolutePath();
			mergedFileTextField.setText(mergedFileTextFieldString);

			System.out.println("arg = " + mergedFileTextFieldString);
		}
	}

	private void mergedFileFormatComboboxActionPerformed(
			java.awt.event.ActionEvent evt) {
		outputFormatChoiceString = (String) mergedFileFormatCombobox.getSelectedItem();
	}
	private void mergedFileTextFieldActionPerformed(
			java.awt.event.ActionEvent evt) {
		int returnVal = fileChooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			mergedFileTextFieldString = fileChooser.getSelectedFile()
			.getAbsolutePath();
			mergedFileTextField.setText(mergedFileTextFieldString);

			System.out.println("arg = " + mergedFileTextFieldString);
		}

	}
	private void parentFileButtonActionPerformed(java.awt.event.ActionEvent evt) {
		int returnVal = fileChooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			parentFileTextFieldString = fileChooser.getSelectedFile()
			.getAbsolutePath();
			parentFileTextField.setText(parentFileTextFieldString);

			System.out.println("arg = " + parentFileTextFieldString);
		}

	}
	private void saveFeedbackToFileBrowseButtonActionPerformed(
			java.awt.event.ActionEvent evt) {
		int returnVal = fileChooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			saveFeedbackToFileTextFieldString = fileChooser.getSelectedFile().getAbsolutePath();
			saveFeedbackToFileTextField.setText(saveFeedbackToFileTextFieldString);
		}
System.out.println("saveFeedbackToFileBrowseButtonActionPerformed : We are in button action performed.");
	}



	private void updateIDsComboboxActionPerformed(
			java.awt.event.ActionEvent evt) {
		updateIDsChoiceString = (String) updateIDsCombobox
		.getSelectedItem();
		System.out.println("arg = " + updateIDsChoiceString);
	}


	private void WriteFeedbackToFile() {
		File feedbackFile = new File(saveFeedbackToFileTextFieldString);
		try {

			feedbackFileOutputStream = new PrintStream(
					feedbackFile);
//			ObjectOutputStream feedbackFileObjectOutputStream = new ObjectOutputStream(
//					feedbackFileOutputStream);

			System.setOut(feedbackFileOutputStream);
			System.setErr(feedbackFileOutputStream);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	private void ShowFeedbackInWindow() {
			feedbackTextArea.setText(feedbackFileOutputStream.toString());
	}

















	protected void liveFileBrowseButtonActionPerformed(ActionEvent evt) {

		int returnVal = fileChooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			liveFileTextFieldString = fileChooser.getSelectedFile()
			.getAbsolutePath();
			liveFileTextField.setText(liveFileTextFieldString);

			System.out.println("arg = " + liveFileTextFieldString);

		}

	}
}


