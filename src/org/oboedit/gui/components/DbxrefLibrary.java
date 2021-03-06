package org.oboedit.gui.components;

import org.bbop.framework.AbstractGUIComponent;
import org.bbop.framework.ComponentConfiguration;
import org.bbop.framework.GUIManager;
import org.bbop.swing.*;
import org.obo.datamodel.*;
import org.oboedit.controller.SessionManager;
import org.oboedit.controller.SelectionManager;
import org.oboedit.gui.*;
import org.oboedit.gui.components.GeneralXrefEditorComponent;
import org.oboedit.gui.widget.DbxrefListEditor;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

import org.apache.log4j.*;

public class DbxrefLibrary extends AbstractGUIComponent {

	//initialize logger
	protected final static Logger logger = Logger.getLogger(DbxrefLibrary.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ListEditor editor;

	JList refList;

	JButton useButton;
//	JButton useForAllButton;
	JButton useAsDefButton;
//	JButton useAsDefForAllButton;
	JCheckBox useForAll;
	JButton saveButton;
	JButton configureButton;
	JButton importButton;
	JButton exportButton;
	
	/**
	 * The option to stop a change in the term selection in the Ontology Tree Editor from triggering reloads of other 
	 * component interfaces. 
	 */
	static String noGUIReloadOnSelection = "noGUIReloadOnSelection";

	Vector<Dbxref> dbxrefs = new Vector<Dbxref>();

	JDialog dialog;

	public static String getVersion() {
		return "1.000";
	}

	@Override
	public String getName() {
		return "Dbxref Library v" + getVersion();
	}

	public DbxrefLibrary(String id) {
		super(id);
		setLayout(new BorderLayout());
		useButton = new JButton("Add as dbxref");
		useAsDefButton = new JButton("Add as definition dbxref");
		useForAll = new JCheckBox("Apply to all selected terms");
//		useForAllButton = new JButton("Add dbxref to ALL selected terms");
//		useAsDefForAllButton = new JButton("Add as def dbxref to ALL selected terms");
		importButton = new JButton("Import dbxrefs");
		exportButton = new JButton("Export dbxrefs");
		saveButton = new JButton("Save configuration");
		configureButton = new JButton("Configure dbxrefs");
		refList = new JList();
	}

	public static class DbxrefLibraryConfiguration implements
	ComponentConfiguration {
		protected Vector<Dbxref> library;

		public DbxrefLibraryConfiguration() {
			library = new Vector<Dbxref>();
		}

		public DbxrefLibraryConfiguration(Vector<Dbxref> library) {
			setLibrary(library);
		}

		public void setLibrary(Vector<Dbxref> library) {
			this.library = library;
		}

		public Vector<Dbxref> getLibrary() {
			return library;
		}
	}

	@Override
	public void setConfiguration(ComponentConfiguration config) {
		if (config instanceof DbxrefLibraryConfiguration) {
			dbxrefs = ((DbxrefLibraryConfiguration) config).getLibrary();
                        Collections.sort(dbxrefs, sortByDbxrefDescrip);
			updateList();
//			if (dbxrefs.size() > 0) {
//			useButton.setEnabled(true);
//			useAsDefButton.setEnabled(true);
//			}
		}
	}

	@Override
	public ComponentConfiguration getConfiguration() {
		return new DbxrefLibraryConfiguration(dbxrefs);
	}

	protected Vector<Dbxref> getDbxrefList() {
		return dbxrefs;
	}

	protected void configure() {
		JLabel noSelection = new JLabel("Nothing selected");

		JPanel panel = new JPanel();
		panel.add(noSelection);
		DbxrefListEditor component = new DbxrefListEditor();
		panel.setPreferredSize(new Dimension(350, 300));
		component.setPreferredSize(new Dimension(350, 300));

		editor = new ListEditor(component, panel, getDbxrefList(), true, true,
				true, true, false);
		editor.setPreferredSize(new Dimension(450, 300));

		dialog = new JDialog();

		JPanel dialogPanel = new JPanel();
		dialogPanel.setLayout(new BorderLayout());
		dialogPanel.add(editor, "Center");
		dialogPanel.add(saveButton, "South");
		dialog.setContentPane(dialogPanel);

		dialog.setSize(450, 330);

		dialog.show();
		Collections.sort(dbxrefs, sortByDbxrefDescrip);
		refList.setListData(dbxrefs);
	}

	protected void sendUpdates(boolean isDef, boolean allSelected) {
		java.util.List<Dbxref> refs = new LinkedList<Dbxref>();
		Object[] selection = refList.getSelectedValues();
		for (int i = 0; i < selection.length; i++) {
			Dbxref ref = (Dbxref) ((Dbxref) selection[i]).clone();
			if (isDef) {
				ref.setType(Dbxref.DEFINITION);
			} else
				ref.setType(Dbxref.ANALOG);
			refs.add(ref);
		}
		if (allSelected) {
			Selection selected = SelectionManager.getManager().getSelection();
			GeneralXrefEditorComponent dbxrefEditor = new GeneralXrefEditorComponent();
			LinkedObject current = (LinkedObject) dbxrefEditor.getObject();
//			logger.debug("DbxrefLibrary: selection = " + selected + ", dbxrefEditor.getObject = " + current); // DEL
			// Do for each selected term
			for (LinkedObject lo : selected.getTerms()) {
				Selection newSelection = SelectionManager.changeSubSelection(
						SelectionManager.getGlobalSelection(), lo);
				
				SelectionManager.setGlobalSelection(newSelection, noGUIReloadOnSelection);
				
				GUIManager.getManager().fireUserEvent(
						new AbstractDbxrefEditorComponent.DbxrefUpdateEvent(this,
								(isDef ? "gui.dbxref.def.add"
										: "gui.xref.general.add"), 
										refs.toArray(new Dbxref[0])));
			}
			

			// Put selection back the way it was originally
			SelectionManager.setGlobalSelection(
					SelectionManager.changeSubSelection(SelectionManager.getGlobalSelection(), current));
		}
		else
			GUIManager.getManager().fireUserEvent(
					new AbstractDbxrefEditorComponent.DbxrefUpdateEvent(this,
							(isDef ? "gui.dbxref.def.add"
									: "gui.xref.general.add"), 
									refs.toArray(new Dbxref[0])));
	}

	protected void saveConfiguration() {
		dbxrefs = editor.getData();
		if (dbxrefs == null)
			dbxrefs = new Vector<Dbxref>();
		Collections.sort(dbxrefs, sortByDbxrefDescrip);
		refList.setListData(dbxrefs);
		refList.repaint();
	}

	protected void attachListeners() {
		useButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendUpdates(false, useForAll.isSelected());
			}
		});
		useAsDefButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendUpdates(true, useForAll.isSelected());
			}
		});
//		useForAllButton.addActionListener(new ActionListener() {
//		public void actionPerformed(ActionEvent e) {
//		sendUpdates(false, true);
//		}
//		});
//		useAsDefForAllButton.addActionListener(new ActionListener() {
//		public void actionPerformed(ActionEvent e) {
//		sendUpdates(true, true);
//		}
//		});
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveConfiguration();
				dialog.dispose();
			}
		});
		configureButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				configure();
			}
		});
		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportFile();
			}
		});
		importButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importFile();
			}
		});
	}

	protected void updateList() {
		refList.setListData(getDbxrefList());
		refList.repaint();
	}

	protected void exportFile() {
		SelectDialog dialog = SelectDialog.getFileSelector(SelectDialog.SAVE, null);
		dialog.show();
		File selected = dialog.getSelected();
		if (selected != null) {
			try {
				PrintWriter writer = new PrintWriter(new FileWriter(selected));
				for (int i = 0; i < dbxrefs.size(); i++) {
					Dbxref ref = dbxrefs.get(i);
					String line = ref.getDatabase() + ":" + ref.getDatabaseID();
					if (ref.getDesc() != null) {
						line += " " + ref.getDesc();
					}
					writer.println(line);
				}
				writer.close();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(GUIManager.getManager()
						.getFrame(), "Could not write file " + selected);
			}
		}
	}

	protected void importFile() {
		SelectDialog dialog = SelectDialog.getFileSelector(SelectDialog.LOAD, null);
		dialog.show();
		File selected = dialog.getSelected();
		if (selected != null) {
			dbxrefs.clear();
			try {
				BufferedReader reader = new BufferedReader(new FileReader(selected));
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.length() == 0)
						continue;
					int colonIndex = line.indexOf(':');
					int spaceIndex = line.indexOf(' ');
					int dbxrefEndIndex = spaceIndex;
					if (dbxrefEndIndex == -1)
						dbxrefEndIndex = line.length();
					String db = line.substring(0, colonIndex);
					String id = line.substring(colonIndex + 1, dbxrefEndIndex);
					String desc = null;
					if (spaceIndex != -1) {
						desc = line.substring(spaceIndex + 1, line.length());
					}
					Dbxref dbxref = SessionManager.getManager().getSession()
					.getObjectFactory().createDbxref(db, id, desc,
							Dbxref.ANALOG, null);
					dbxrefs.add(dbxref);
				}
                                Collections.sort(dbxrefs, sortByDbxrefDescrip);
				refList.setListData(dbxrefs);
				refList.repaint();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(null, "Could not read file "
						+ selected);
			}
		}
	}

	@Override
	public void init() {
		removeAll();
		// buildDbxrefList(props);

		JScrollPane scroller = new JScrollPane(refList,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		// scroller.setPreferredSize(new Dimension(200, 200));

		attachListeners();

		updateList();

		JPanel ioPanel = new JPanel();
		ioPanel.setLayout(new GridLayout(1, 2));
		ioPanel.add(importButton);
		ioPanel.add(exportButton);
		ioPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, importButton.getPreferredSize().height + 5)); // ?

		JPanel usePanel = new JPanel();
		usePanel.setLayout(new GridLayout(1, 2));
		usePanel.add(useButton);
		usePanel.add(useAsDefButton);
		// I tried disabling them until we're sure a term was selected, but didn't get
		// that working yet.
//		useButton.setEnabled(false);
//		useAsDefButton.setEnabled(false);
		JPanel usePanel2 = new JPanel();
		usePanel2.setLayout(new BorderLayout());
		usePanel2.add(useForAll, "Center");
//		usePanel2.add(useForAllButton);
//		usePanel2.add(useAsDefForAllButton);

		JPanel usePanels = new JPanel();
		usePanels.setLayout(new GridLayout(2, 1));
		usePanels.setBorder(new TitledBorder("Add selected dbxref"));
		usePanels.add(usePanel);
		usePanels.add(usePanel2);

		JPanel buttonPanel = new JPanel();
//		buttonPanel.setLayout(new GridLayout(3, 2));
		buttonPanel.setLayout(new BorderLayout());

		buttonPanel.add(usePanels, "North");
		buttonPanel.add(ioPanel, "Center");
		buttonPanel.add(configureButton, "South");

		add(scroller, "Center");
		add(buttonPanel, "South");
	}

	@Override
	public void cleanup() {
		// controller.getDragController().unregisterDropTarget(dropTarget);
	}

        // Sort by description, if any; then sort by db:ID
	public static final Comparator sortByDbxrefDescrip = new Comparator() {
		public int compare(Object a, Object b) {
			Dbxref ad = (Dbxref) a;
			Dbxref bd = (Dbxref) b;
			if (ad.getDesc() != null) { // a has description
                            if (bd.getDesc() == null) // b doesn't
                                return -1;
                            else { // both have description
                                return ad.getDesc().compareToIgnoreCase(bd.getDesc());
                            }
			} else if (bd.getDesc() != null) {// a doesn't have description; b does {
				return 1;
                        }

			int compVal = ad.getDatabase()
					.compareToIgnoreCase(bd.getDatabase());
			if (compVal != 0)
				return compVal;
			compVal = ad.getDatabaseID().compareToIgnoreCase(bd.getDatabaseID());
			if (compVal != 0)
				return compVal;
                        return 0;
		}
            };
}
