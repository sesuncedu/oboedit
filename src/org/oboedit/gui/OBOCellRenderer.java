package org.oboedit.gui;

import org.bbop.swing.*;
import org.bbop.util.ObjectUtil;
import org.obo.datamodel.*;
import org.obo.filters.*;
import org.obo.util.FilterUtil;
import org.obo.util.TermUtil;
import org.oboedit.controller.FilterManager;
import org.oboedit.controller.SelectionManager;
import org.oboedit.gui.components.OBOTermPanel;
import org.oboedit.gui.filter.ForegroundColorSpecField;
import org.oboedit.gui.filter.ConfiguredColor;
import org.oboedit.gui.filter.GeneralRendererSpec;
import org.oboedit.gui.filter.GeneralRendererSpecField;
import org.oboedit.gui.filter.LineTypeSpecField;
import org.oboedit.gui.filter.LineWidthSpecField;
import org.oboedit.gui.filter.LinkRenderSpec;
import org.oboedit.gui.filter.ObjectRenderSpec;
import org.oboedit.gui.filter.RenderSpec;
import org.oboedit.gui.filter.RenderedFilter;
import org.oboedit.util.GUIUtil;
import org.oboedit.util.PathUtil;

import javax.swing.tree.*;
import javax.swing.border.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.net.*;
import java.util.*;

public class OBOCellRenderer extends JLabel implements TreeCellRenderer,
		ListCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected final static Color ignoreSelectionColor = new Color(204, 255, 204);

	protected final static Color highlightColor = Color.yellow;

	protected final static Color clickBorderColor = Color.black;

	protected final static Color tabBorderColor = Color.blue;

	protected static LineBorder tabBorder = new LineBorder(tabBorderColor);

	protected LinkRenderSpec linkSpec = new LinkRenderSpec();

	protected static HashMap fontHash = new HashMap();

	protected static HashMap strokeHash = new HashMap();

	protected java.util.List defaultSpecs = new ArrayList();

	protected MultiIcon multiIcon = new MultiIcon();

	protected Icon nec_inv_icon;

	protected Icon nec_icon;

	protected Icon inv_icon;

	protected Icon completes_icon;

	protected JComponent renderComponent = this;

	protected void createDefaultSpecs() {
		/*
		 * defaultSpecs.add(createPropertyRenderer());
		 * defaultSpecs.add(createRedundantRenderer());
		 * defaultSpecs.add(createObsoleteRenderer());
		 * defaultSpecs.add(createImpliedRenderer());
		 */
	}

	public OBOCellRenderer() {
		super();
		createDefaultSpecs();
		nec_inv_icon = Preferences.loadLibraryIcon("inv_and_nec_icon.gif");
		inv_icon = Preferences.loadLibraryIcon("inv_icon.gif");
		nec_icon = Preferences.loadLibraryIcon("nec_icon.gif");
		completes_icon = Preferences.loadLibraryIcon("completes.gif");

		setIcon(multiIcon);
	}

	@Override
	public boolean isShowing() {
		return true;
	}

	@Override
	public Dimension getSize() {
		Dimension d = super.getSize();
		d.height += 4;
		return d;
	}

	protected ArrowIcon linkIcon = new ArrowIcon();

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (isSelected) {
			setOpaque(true);
			setBackground(Preferences.defaultSelectionColor());
		} else {
			setOpaque(false);
			setBackground(null);
		}
		setBorder(null);

		IdentifiedObject term = (IdentifiedObject) value;
		String text = term.toString();

		if (TermUtil.isObsolete(term))
			setForeground(Color.red);
		else
			setForeground(Color.black);

		setText(text);

		return this;
	}

	protected Font getFont(ObjectRenderSpec spec) {
		Font font = Preferences.getPreferences().getFont();
		String fontName = font.getFontName();
		int size = font.getSize();
		int style = Font.PLAIN;
		if (spec.getBold() && spec.getItalic())
			style = Font.BOLD | Font.ITALIC;
		else if (spec.getBold())
			style = Font.BOLD;
		else if (spec.getItalic())
			style = Font.ITALIC;
		if (spec.getFontName() != null)
			fontName = spec.getFontName();
		if (spec.getFontSize() > 0)
			size = spec.getFontSize();

		String hashval = fontName + "-" + size + "-" + style;

		font = (Font) fontHash.get(hashval);
		if (font == null) {
			font = new Font(fontName, style, size);
			fontHash.put(hashval, font);
		}
		return font;
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		boolean highlighted = false;
		boolean clickTarget = false;
		boolean tabRow = false;
		boolean ignoreSelection = false;

		if (tree instanceof OBOTermPanel) {
			OBOTermPanel ot = (OBOTermPanel) tree;
			highlighted = row == ot.getHighlightRow();
			clickTarget = row == ot.getClickTarget();
			tabRow = row == ot.getTabRow();
			ignoreSelection = ot.ignoreSelection();
		}
		return getTreeCellRendererComponent(tree, value, selected, expanded,
				leaf, highlighted, clickTarget, tabRow, ignoreSelection, row,
				hasFocus);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf,
			boolean highlighted, boolean clickTarget, boolean tabRow,
			boolean ignoreSelection, int row, boolean hasFocus) {
		try {
			multiIcon.clearIcons();
			if (value.equals(PathUtil.OBSOLETE)) {
				setText("Obsolete");
				setForeground(Color.red);
				setBorder(null);
				setOpaque(false);
				setBackground(null);
				return this;
			} else if (value.equals(PathUtil.TYPES)) {
				setText("Relations");
				setForeground(Color.blue);
				setBorder(null);
				setOpaque(false);
				setBackground(null);
				return this;
			} else if (value.equals(PathUtil.INSTANCES)) {
				setText("Instances");
				setForeground(Color.green);
				setBorder(null);
				setOpaque(false);
				setBackground(null);
				return this;
			} else if (value.equals(PathUtil.CLASSES)) {
				setText("Classes");
				setForeground(Color.black);
				setBorder(null);
				setOpaque(false);
				setBackground(null);
				return this;
			} else
				setForeground(Color.black);

			multiIcon.addIcon(linkIcon);
			linkIcon.setColor(Color.black);
			linkIcon.setLineWidth(1);
			linkIcon.setLineType(LineType.SOLID_LINE);
			if (!(value instanceof Relationship)) {
				setText("Some unknown item " + value);
				return this;
			}

			Relationship link = (Relationship) value;
			RenderSpec spec;
			if (tree instanceof FilteredRenderable) {
				FilteredRenderable fr = (FilteredRenderable) tree;
				NodeLabelProvider provider = fr.getNodeLabelProvider();
				LinkedObject lo = link.getChild();
				String s = provider.getLabel(lo);
				setText(s);
				spec = GUIUtil.getSpec(link, FilterManager.getManager()
						.getGlobalLinkRenderers(), fr.getLinkRenderers());
			} else {
				spec = GUIUtil.getSpec(link, FilterManager.getManager()
						.getGlobalLinkRenderers());
				setText(link.getChild().getName());
			}
			if (spec instanceof GeneralRendererSpec) {
				GeneralRendererSpec s = (GeneralRendererSpec) spec;
				ConfiguredColor f = s.getValue(ForegroundColorSpecField.FIELD);
				if (f != null) {
					linkIcon.setColor(f.getColor());
				}
				Integer width = s.getValue(LineWidthSpecField.FIELD);
				if (width != null)
					linkIcon.setLineWidth(width.intValue());
				LineType type = s.getValue(LineTypeSpecField.FIELD);
				if (type != null) {
					linkIcon.setLineType(type);
				}

			}

			linkIcon.setLeaf(leaf);
			linkIcon.setPath(tree.getPathForRow(row));

			Icon icon = null;

			if (link.getType() != null) {
				icon = Preferences.getPreferences().getIconForRelationshipType(
						link.getType());
			}

			if (icon != null) {
				multiIcon.addIcon(icon);
			}
			if (link instanceof OBORestriction) {
				OBORestriction tr = (OBORestriction) link;
				if (!tr.isNecessarilyTrue() && tr.isInverseNecessarilyTrue())
					multiIcon.addIcon(nec_inv_icon);
				else if (!tr.isNecessarilyTrue()) {
					multiIcon.addIcon(nec_icon);
				} else if (tr.isInverseNecessarilyTrue()) {
					multiIcon.addIcon(inv_icon);
				}

				if (tr.completes()) {
					multiIcon.addIcon(completes_icon);
				}
			}

			TreePath path = tree.getPathForRow(row);
			if (path != null && PathUtil.pathIsCircular(path))
				setEnabled(false);
			else
				setEnabled(true);

			if (highlighted) {
				setOpaque(true);
				setBackground(highlightColor);
			} else if (selected) {
				setOpaque(true);
				if (ignoreSelection)
					setBackground(ignoreSelectionColor);
				else {
					Selection selection = SelectionManager.getManager()
							.getGlobalSelection();
					if (link.getChild().equals(selection.getTermSubSelection())
							|| ObjectUtil.equals(link, selection
									.getLinkSubSelection())) {
						setBackground(Preferences.defaultSelectionColor());
					} else {
						setBackground(Preferences.lightSelectionColor());
					}
				}
			} else {
				setOpaque(false);
				setBackground(null);
			}
			if (tabRow) {
				setBorder(tabBorder);
			} else {
				setBorder(null);
			}

		} catch (Throwable t) {
			t.printStackTrace();
		}
		validate();
		return this;
	}
}
