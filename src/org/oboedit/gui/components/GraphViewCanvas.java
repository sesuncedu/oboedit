package org.oboedit.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import org.bbop.framework.AbstractGUIComponent;
import org.bbop.framework.ComponentConfiguration;
import org.bbop.framework.ConfigurationPanel;
import org.bbop.swing.TaskPanel;
import org.bbop.util.AbstractTaskDelegate;
import org.obo.datamodel.Link;
import org.obo.datamodel.LinkDatabase;
import org.obo.datamodel.LinkedObject;
import org.obo.datamodel.MutableLinkDatabase;
import org.obo.datamodel.OBOProperty;
import org.obo.datamodel.OBOSession;
import org.obo.datamodel.PathCapable;
import org.obo.datamodel.RootAlgorithm;
import org.obo.datamodel.impl.DefaultMutableLinkDatabase;
import org.obo.datamodel.impl.FilteredLinkDatabase;
import org.obo.datamodel.impl.MaskedLinkDatabase;
import org.obo.filters.Filter;
import org.obo.filters.LinkFilterImpl;
import org.obo.reasoner.ReasonedLinkDatabase;
import org.obo.util.ReasonerUtil;
import org.obo.util.TermUtil;
import org.oboedit.controller.SelectionManager;
import org.oboedit.controller.SessionManager;
import org.oboedit.graph.BoundsGuarantor;
import org.oboedit.graph.FocusPicker;
import org.oboedit.graph.HierarchicalGraphLayout;
import org.oboedit.graph.SelectionBehavior;
import org.oboedit.graph.TooltipBehavior;
import org.oboedit.graph.ZoomToAllGuarantor;
import org.oboedit.graph.ZoomWidgetBehavior;
import org.oboedit.gui.Filterable;
import org.oboedit.gui.FilteredRenderable;
import org.oboedit.gui.HTMLNodeLabelProvider;
import org.oboedit.gui.NodeLabelProvider;
import org.oboedit.gui.Selection;
import org.oboedit.gui.event.ExpandCollapseListener;
import org.oboedit.gui.event.ReloadEvent;
import org.oboedit.gui.event.ReloadListener;
import org.oboedit.gui.event.SelectionEvent;
import org.oboedit.gui.event.SelectionListener;
import org.oboedit.gui.filter.RenderedFilter;
import org.oboedit.util.GUIUtil;

import org.apache.log4j.*;

public class GraphViewCanvas extends AbstractGUIComponent implements Filterable,
FilteredRenderable {

	//initialize logger
	protected final static Logger logger = Logger.getLogger(GraphViewCanvas.class);

	public static class GraphViewConfiguration implements
	ComponentConfiguration {
		protected boolean showAnimations = false;
		//trim reasoner-generated (implied) links from display
		protected boolean trimImpliedLinks = false;
		protected boolean showPerType = true;
		protected boolean allTypes = true;
		protected boolean nonTransitive = false;
		protected int orientation = HORIZONTAL;
		protected String htmlExpression = "$name$";

		public boolean isShowAnimations() {
			return showAnimations;
		}

		public void setShowAnimations(boolean showAnimations) {
			this.showAnimations = showAnimations;
		}

		public boolean istrimImpliedLinksDisplay() {
			return trimImpliedLinks;
		}

		public void settrimImpliedLinksDisplay(boolean trimImpliedLinksDisplay) {
			this.trimImpliedLinks = trimImpliedLinksDisplay;
		}

		public boolean isShowPerType() {
			return showPerType;
		}

		public void setShowPerType(boolean showPerType) {
			this.showPerType = showPerType;
		}

		public boolean isAllTypes() {
			return allTypes;
		}

		public void setAllTypes(boolean allTypes) {
			this.allTypes = allTypes;
		}

		public boolean isNonTransitive() {
			return nonTransitive;
		}

		public void setNonTransitive(boolean nonTransitive) {
			this.nonTransitive = nonTransitive;
		}

		public int getOrientation() {
			return orientation;
		}

		public void setOrientation(int orientation) {
			this.orientation = orientation;
		}

		public String getHTMLExpression() {
			return htmlExpression;
		}

		public void setHTMLExpression(String htmlExpression) {
			this.htmlExpression = htmlExpression;
		}
	}

	private static final long serialVersionUID = 1221804652480622045L;

	public static int HORIZONTAL = 0;
	public static int VERTICAL = 1;

	protected GraphViewConfiguration config = new GraphViewConfiguration();

	protected OBOSession session;

	protected LinkDatabase linkProviderDatabase;

	protected ReasonedLinkDatabase reasoner;

	protected List<LinkedObject> terms = new LinkedList<LinkedObject>();

	protected JPanel dagPanel = new JPanel();
	protected TaskPanel taskPanel = new TaskPanel();

	protected JLabel topLabel = new JLabel();

	Collection<LinkDatabaseCanvas> canvasList = new LinkedList<LinkDatabaseCanvas>();

	protected JCheckBox showAnimations = new JCheckBox("Animate", false);
	protected JCheckBox trimImpliedLinksCheckbox = new JCheckBox("Trim reasoner-generated (implied) links from display", true);
	protected JCheckBox showBreakdownBox = new JCheckBox("Show per-type panels", true);
	protected JCheckBox allTypesBox = new JCheckBox("Show all types panel",true);
	protected JCheckBox nonTransitiveBox = new JCheckBox("Show non-transitive types", false);

	protected JTextArea htmlArea = new JTextArea();

	protected static final String[] orientations = { "horizontal", "vertical" };

	protected JComboBox orientationChooser = new JComboBox(orientations);

	protected boolean isLive = true;

	protected static final Color lockGray = new Color(240, 240, 240);  // when panel is in local selection mode

	protected ConfigurationPanel configPanel = new ConfigurationPanel() {

		@Override
		public void commit() {
			commitConfig();
		}

		@Override
		public void init() {
			initConfig();
		}

	};

	protected class ReloadTaskDelegate extends
	AbstractTaskDelegate<Map<String, LinkDatabase>> {

		public ReloadTaskDelegate() {
			setSwingFriendly(true);
			addPostExecuteRunnable(new Runnable() {
				public void run() {
					updatePanels(getResults());
				}
			});
		}

		@Override
		protected void setProgressValue(Integer progress) {
			// TODO Auto-generated method stub
			super.setProgressValue(progress);
		}

		@Override
		public void execute() throws Exception {
			long time = System.currentTimeMillis();
			setProgressValue(null);
			setProgressString("Working...");
			int totalCount = 0;
			if (config.isAllTypes())
				totalCount++;
			Map<OBOProperty, Collection<Link>> typeMap = null;
			if (config.isShowPerType()) {
				typeMap = getTypeMap();
				totalCount += typeMap.size();
			}
			Map<String, LinkDatabase> databases = new LinkedHashMap<String, LinkDatabase>();
			int currentProgress = 0;
			if (config.isAllTypes()) {
				Collection<Link> ancestors = new HashSet<Link>();
				logger.debug("reasoner: " + reasoner);

				for (LinkedObject lo : terms) {
					Collection<Link> parentLinks = null;
					Collection<Link> ancestorLinks = null;

					LinkDatabase ld = SessionManager.getManager().getSession().getLinkDatabase();	

					//get all parent links for lo
					parentLinks = ld.getParents(lo);

					//get ancestor links for lo
					ancestorLinks = getAncestors(lo);

					Collection<Link> allLinks = new LinkedHashSet<Link>(parentLinks.size() + ancestorLinks.size());
					allLinks.addAll(parentLinks);
					allLinks.addAll(ancestorLinks);

					for (Link Link : allLinks) {	
						if (config.isNonTransitive() || Link.getType().isTransitive())
							ancestors.add(Link);
					}
				}

				setProgressValue(currentProgress++ * 100 / totalCount);
				if (isCancelled())
					return;
				LinkDatabase linkDatabase = createLinkDatabase(null, ancestors, false);
				databases.put("All", linkDatabase);
			}


			if (config.isShowPerType()) {
				for (OBOProperty type : typeMap.keySet()) {
					if (!config.isNonTransitive() && !type.isTransitive())
						continue;
					Collection<Link> parents = typeMap.get(type);
					setProgressValue(currentProgress++ * 100 / totalCount);
					if (isCancelled())
						return;
					LinkDatabase linkDatabase = createLinkDatabase(type,parents, config.istrimImpliedLinksDisplay());
					databases.put(type.getID(), linkDatabase);
				}
			}
			setProgressValue(100);
			setResults(databases);
			//			logger.info("found paths in " + (System.currentTimeMillis() - time));
		}



	}

	//fetches all ancestor links (above immediate parent link) up to root for a given object
	public Collection<Link> getAncestors(LinkedObject lo) {
		//get all ancestor terms
		Collection<LinkedObject> ancestors = TermUtil.getAncestors(lo, null);
		Collection<Link> out = new LinkedHashSet<Link>(ancestors.size());

		for(LinkedObject a : ancestors){
			//get all child links for that ancestor term
			Collection<Link> achildren = a.getChildren();
			//examine child links and choose the ones present in the hierarchy for ancestor terms 
			for(Link achild : achildren){
				for(LinkedObject x : ancestors){
					if(achild.getChild().equals(x))
						out.add(achild);
				}
			}
		}
		return out;
	}



	public GraphViewCanvas(String id) {
		super(id);
		configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));
		configPanel.add(createPanel(allTypesBox));
		configPanel.add(createPanel(showAnimations));
		configPanel.add(createPanel(nonTransitiveBox));
		configPanel.add(createPanel(showBreakdownBox));
		configPanel.add(createPanel(trimImpliedLinksCheckbox));
		Box orientationPanel = Box.createHorizontalBox();
		orientationPanel.add(new JLabel("Panel orientation"));
		orientationPanel.add(Box.createHorizontalStrut(10));
		orientationPanel.add(orientationChooser);
		orientationPanel.add(Box.createHorizontalGlue());
		configPanel.add(orientationPanel);
		JPanel htmlPanel = new JPanel();
		htmlPanel.setLayout(new BorderLayout());
		htmlPanel
		.add(
				new JLabel(
						"<html>Enter an expression below that will "
						+ "determine what information is shown for each term. "
						+ "To include specific term information, use search "
						+ "criteria ids enclosed in $ characters. For example, "
						+ "to display the term name on one line and the term "
						+ "id in italics on the line below, use the expression "
						+ "<b>&lt;center&gt;$name$&lt;br&gt;&lt;i&gt;&lt;font "
						+ "size=-1&gt;$id$&lt;/font&gt;&lt;/i&gt;&lt;/center&gt;</b></html>"),
						BorderLayout.NORTH);
		htmlPanel.add(new JScrollPane(htmlArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		configPanel.add(htmlPanel);
		configPanel.add(Box.createVerticalGlue());
	}



	protected static JComponent createPanel(JComponent c) {
		Box out = Box.createHorizontalBox();
		out.add(c);
		out.add(Box.createHorizontalGlue());
		return out;
	}

	protected void commitConfig() {
		config.setAllTypes(allTypesBox.isSelected());
		config.setShowAnimations(showAnimations.isSelected());
		config.setNonTransitive(nonTransitiveBox.isSelected());
		config.setShowPerType(showBreakdownBox.isSelected());
		config.settrimImpliedLinksDisplay(trimImpliedLinksCheckbox.isSelected());
		config.setOrientation(orientationChooser.getSelectedIndex());
		config.setHTMLExpression(htmlArea.getText());
		reload();
	}

	protected void initConfig() {
		allTypesBox.setAlignmentX(0);
		showAnimations.setAlignmentX(0);
		allTypesBox.setSelected(config.isAllTypes());
		showAnimations.setSelected(config.isShowAnimations());
		nonTransitiveBox.setSelected(config.isNonTransitive());
		showBreakdownBox.setSelected(config.isShowPerType());
		trimImpliedLinksCheckbox.setSelected(config.istrimImpliedLinksDisplay());
		orientationChooser.setSelectedIndex(config.getOrientation());
		htmlArea.setText(config.getHTMLExpression());
	}

	public void setDataProviders(OBOSession session,
			LinkDatabase linkProviderDatabase, ReasonedLinkDatabase reasoner) {
		this.linkProviderDatabase = linkProviderDatabase;
		this.reasoner = reasoner;
		this.session = session;
	}

	public void setTerms(Collection<LinkedObject> t) {
		this.terms.clear();
		if (t != null)
			this.terms.addAll(t);
		reload();
	}

	protected LinkDatabase createLinkDatabase(OBOProperty type,
			Collection<Link> parents, boolean trimImpliedLinks ) {
		LinkDatabase linkDatabase;

		//		logger.debug("GVC.createLinkDatabase -- trimImpliedLinks: " + trimImpliedLinks);
		// not trimming implied links from graph
		if (!trimImpliedLinks) {
			MutableLinkDatabase mutable = new DefaultMutableLinkDatabase(true);
			for (Link link : parents) {
				if (TermUtil.isImplied(link)) {
					Collection<Link> implied = ReasonerUtil.getGivenSupportingLinks(reasoner, link);
					for (Link backingLink : implied) {
						mutable.addParent(backingLink);
					}
				} else
					mutable.addParent(link);
			}

			linkDatabase = mutable;
		} else { // succinct view - trimming implied links from graph
			FilteredLinkDatabase filtered = new FilteredLinkDatabase(reasoner);
			filtered.setLinkFilter(new LinkFilterImpl(type));
			MaskedLinkDatabase collapsible = new MaskedLinkDatabase(filtered);
			Collection<LinkedObject> objects = new LinkedList<LinkedObject>();
			objects.addAll(terms);
			// for (Link link : parents) {
			// objects.add(link.getParent());
			// }
			for (Link link : parents) {
				if (reasoner.isSubPropertyOf(link.getType(), type)) {
					if (TermUtil.isImplied(link)) {
						Collection<Link> implied = ReasonerUtil.getGivenSupportingLinks(reasoner, link);
						for (Link backingLink : implied) {
							if (reasoner.isSubPropertyOf(backingLink.getType(), type))
								objects.add(backingLink.getParent());
						}
					} else
						objects.add(link.getParent());
				}
			}
			collapsible.setVisible(objects, true);
			linkDatabase = collapsible;
		}
		return linkDatabase;
	}

	protected LinkDatabaseCanvas getCanvas(LinkDatabase linkDatabase) {
		LinkDatabaseCanvas canvas = new LinkDatabaseCanvas(
				new HierarchicalGraphLayout()) {

			protected void addDefaultBehaviors() {
				addViewBehavior(new FocusPicker());
				addViewBehavior(new SelectionBehavior(true));
				// addViewBehavior(new LinkoutMeterBehavior());
				addViewBehavior(new TooltipBehavior());
				addViewBehavior(new ZoomWidgetBehavior(8, 20));
				addViewBehavior(new BoundsGuarantor() {
					@Override
					protected void installDefaultCyclers() {
						addBoundsGuarantor(new ZoomToAllGuarantor(canvas));
					}
				});
			}

		};
		if (linkFilter != null)
			canvas.setLinkFilter(linkFilter);
		if (termFilter != null)
			canvas.setTermFilter(termFilter);
		for (ExpandCollapseListener listener : expandCollapseListeners)
			canvas.addExpansionListener(listener);
		for (SelectionListener listener : selectionListeners)
			canvas.addSelectionListener(listener);
		for (RenderedFilter filter : automaticObjectRenderers)
			canvas.addAutomaticObjectRenderer(filter);
		for (RenderedFilter filter : objectRenderers)
			canvas.addObjectRenderer(filter);
		for (RenderedFilter filter : linkRenderers)
			canvas.addLinkRenderer(filter);
		if (labelProvider != null)
			canvas.setNodeLabelProvider(labelProvider);
		canvas.setLive(true);
		canvas.setDisableAnimations(!config.isShowAnimations());
		if (canvas.getNodeLabelProvider() instanceof HTMLNodeLabelProvider) {
			((HTMLNodeLabelProvider) canvas.getNodeLabelProvider())
			.setHtmlExpression(config.getHTMLExpression());
		}

		// canvas.setLinkDatabase(reasoner);
		canvas.setLinkDatabase(linkDatabase);
		canvas.setLinkProviderDatabase(linkProviderDatabase);
		canvas.setVisibleObjects(linkDatabase.getObjects());
		canvas.relayout();
		return canvas;
	}

	@Override
	public ConfigurationPanel getConfigurationPanel() {
		return configPanel;
	}

	protected static JPanel wrapCanvas(String title, LinkDatabaseCanvas canvas) {
		JPanel panel = new JPanel();
		panel.setOpaque(true);
		panel.setBackground(Color.white);
		panel.setLayout(new GridLayout(1, 1));
		panel.add(canvas);
		panel.setBorder(new TitledBorder(title));
		return panel;
	}

	public void reload() {
		ReloadTaskDelegate task = new ReloadTaskDelegate();
		taskPanel.schedule(task, true);
	}

	public void updatePanels(Map<String, LinkDatabase> databases) {
		if (canvasList != null) {
			for (LinkDatabaseCanvas canvas : canvasList) {
				canvas.cleanup();
			}
			canvasList.clear();
		}
		dagPanel.removeAll();
		if (terms.size() == 0) {
			topLabel.setText("Select a term to see ancestry views");
		} else {
			StringBuffer buffer = new StringBuffer("Ancestor views of ");
			for (int i = 0; i < terms.size(); i++) {
				if (i > 0 && terms.size() > 2)
					buffer.append(", ");
				if (i == terms.size() - 1 && terms.size() > 1)
					buffer.append(" and ");
				buffer.append(terms.get(i).getName());
			}
			topLabel.setText(buffer.toString());
			canvasList = new LinkedList<LinkDatabaseCanvas>();
			Collection<JComponent> componentList = new LinkedList<JComponent>();
			for (String name : databases.keySet()) {
				LinkDatabaseCanvas canvas = getCanvas(databases.get(name));
				JPanel panel = wrapCanvas(name, canvas);
				canvasList.add(canvas);
				componentList.add(panel);
			}

			if (config.getOrientation() == HORIZONTAL)
				dagPanel.setLayout(new GridLayout(1, canvasList.size()));
			else
				dagPanel.setLayout(new GridLayout(canvasList.size(), 1));

			for (JComponent c : componentList) {
				dagPanel.add(c);
			}
			dagPanel.validate();
			validate();
			repaint();
			for (LinkDatabaseCanvas c : canvasList) {
				c.zoomToObjects();
			}
		}
	}

	public Map<OBOProperty, Collection<Link>> getTypeMap() {
		Map<OBOProperty, Collection<Link>> map = new HashMap<OBOProperty, Collection<Link>>();
		if (reasoner == null)
			return map;
		for (LinkedObject term : terms) {
			for (Link link : reasoner.getParents(term)) {
				Collection<Link> objects = map.get(link.getType());
				if (objects == null) {
					objects = new LinkedList<Link>();
					map.put(link.getType(), objects);
				}
				objects.add(link);
			}
		}
		return map;
	}

	public void cleanup() {
		GUIUtil.removeReloadListener(reloadListener);
		SelectionManager.getManager().removeSelectionListener(selectListener);
		if (canvasList != null) {
			for (LinkDatabaseCanvas canvas : canvasList) {
				canvas.cleanup();
			}
			canvasList.clear();
		}
	}

	public JComponent getComponent() {
		return this;
	}

	public ComponentConfiguration getConfiguration() {
		return config;
	}

	protected ReloadListener reloadListener = new ReloadListener() {
		public void reload(ReloadEvent e) {
			updateProviders();
		}
	};

	protected SelectionListener selectListener = new SelectionListener() {
		public void selectionChanged(SelectionEvent e) {
			if (isLive) {
				select(e.getSelection());
			}
		}
	};

	protected void updateProviders() {
		OBOSession session = SessionManager.getManager().getSession();
		ReasonedLinkDatabase reasoner;
		if (SessionManager.getManager().getUseReasoner())
			reasoner = SessionManager.getManager().getReasoner();
		else
			//			reasoner = new OnTheFlyReasoner(session.getLinkDatabase());
			reasoner = null;
		setDataProviders(session, session.getLinkDatabase(), reasoner);
		reload();
	}

	public void init() {
		removeAll();

		setLayout(new BorderLayout());
		setBackground(Color.white);
		taskPanel.removeAll();
		taskPanel.setLayout(new GridLayout(1, 1));
		taskPanel.add(dagPanel);
		taskPanel.setOpaque(false);
		dagPanel.setOpaque(false);
		add(taskPanel, "Center");
		add(topLabel, "North");
		GUIUtil.addReloadListener(reloadListener);
		SelectionManager.getManager().addSelectionListener(selectListener);
		updateProviders();
		setTerms(SelectionManager.getGlobalSelection().getTerms());
	}

	public boolean isSingleton() {
		return false;
	}

	public void setConfiguration(ComponentConfiguration config) {
		if (config == null)
			config = new GraphViewConfiguration();
		this.config = (GraphViewConfiguration) config;
	}

	public void setXML(String xml) {
	}

	public boolean isXMLSettable() {
		return false;
	}

	protected Filter<?> linkFilter;
	protected Filter<?> termFilter;
	protected Collection<ExpandCollapseListener> expandCollapseListeners = new ArrayList<ExpandCollapseListener>();
	protected Collection<SelectionListener> selectionListeners = new ArrayList<SelectionListener>();
	protected List<RenderedFilter> automaticObjectRenderers = new ArrayList<RenderedFilter>();
	protected List<RenderedFilter> objectRenderers = new ArrayList<RenderedFilter>();
	protected List<RenderedFilter> linkRenderers = new ArrayList<RenderedFilter>();
	protected NodeLabelProvider labelProvider;

	public Filter<?> getLinkFilter() {
		return linkFilter;
	}

	public Filter<?> getTermFilter() {
		return termFilter;
	}

	public void setLinkFilter(Filter<?> filter) {
		this.linkFilter = filter;
		for (LinkDatabaseCanvas canvas : canvasList) {
			canvas.setLinkFilter(filter);
		}
	}

	public void setTermFilter(Filter<?> filter) {
		this.termFilter = filter;
		for (LinkDatabaseCanvas canvas : canvasList) {
			canvas.setTermFilter(filter);
		}
	}

	public void addExpansionListener(ExpandCollapseListener listener) {
		expandCollapseListeners.add(listener);
		for (LinkDatabaseCanvas canvas : canvasList) {
			canvas.addExpansionListener(listener);
		}
	}

	public void addSelectionListener(SelectionListener listener) {
		selectionListeners.add(listener);
		for (LinkDatabaseCanvas canvas : canvasList) {
			canvas.addSelectionListener(listener);
		}
	}

	public LinkDatabase getLinkDatabase() {
		return SessionManager.getManager().getSession().getLinkDatabase();
	}

	public RootAlgorithm getRootAlgorithm() {
		return RootAlgorithm.GREEDY;
	}

	public Selection getSelection(MouseEvent e) {
		return SelectionManager.getManager().getSelection();
	}

	public Selection getSelection() {
		return SelectionManager.getManager().getSelection();
	}

	public Collection<PathCapable> getVisibleObjects() {
		Collection<PathCapable> out = new HashSet<PathCapable>();
		for (LinkDatabaseCanvas canvas : canvasList) {
			out.addAll(canvas.getVisibleObjects());
		}
		return out;
	}

	public boolean hasCombinedTermsAndLinks() {
		return false;
	}

	public boolean isLive() {
		return isLive;
	}

	public void setLive(boolean isLive) {
		this.isLive = isLive;
		repaint(); // in order to change background color
	}

	public void removeExpansionListener(ExpandCollapseListener listener) {
		expandCollapseListeners.remove(listener);
		for (LinkDatabaseCanvas canvas : canvasList) {
			canvas.removeExpansionListener(listener);
		}
	}

	public void removeSelectionListener(SelectionListener listener) {
		selectionListeners.remove(listener);
		for (LinkDatabaseCanvas canvas : canvasList) {
			canvas.removeSelectionListener(listener);
		}
	}

	public void select(Selection selection) {
		if (isLive())
			setTerms(selection.getTerms());
	}

	public void addAutomaticObjectRenderer(RenderedFilter pair) {
		automaticObjectRenderers.add(pair);
		for (LinkDatabaseCanvas canvas : canvasList) {
			canvas.addAutomaticObjectRenderer(pair);
		}
	}

	public void addLinkRenderer(RenderedFilter renderer) {
		linkRenderers.add(renderer);
		for (LinkDatabaseCanvas canvas : canvasList) {
			canvas.addLinkRenderer(renderer);
		}
	}

	public void addObjectRenderer(RenderedFilter pair) {
		objectRenderers.add(pair);
		for (LinkDatabaseCanvas canvas : canvasList) {
			canvas.addObjectRenderer(pair);
		}
	}

	public List<RenderedFilter> getAutomaticObjectRenderers() {
		return automaticObjectRenderers;
	}

	public List<RenderedFilter> getLinkRenderers() {
		return linkRenderers;
	}

	public void setNodeLabelProvider(NodeLabelProvider provider) {
		for (LinkDatabaseCanvas canvas : canvasList) {
			canvas.setNodeLabelProvider(provider);
		}
	}

	public NodeLabelProvider getNodeLabelProvider() {
		return labelProvider;
	}

	public List<RenderedFilter> getObjectRenderers() {
		return objectRenderers;
	}

	public void redraw() {
		for (LinkDatabaseCanvas canvas : canvasList) {
			canvas.redraw();
		}
	}

	public void removeAutomaticObjectRenderer(RenderedFilter pair) {
		automaticObjectRenderers.remove(pair);
		for (LinkDatabaseCanvas canvas : canvasList) {
			canvas.removeAutomaticObjectRenderer(pair);
		}
	}

	public void removeLinkRenderer(RenderedFilter renderer) {
		linkRenderers.remove(renderer);
		for (LinkDatabaseCanvas canvas : canvasList) {
			canvas.removeLinkRenderer(renderer);
		}
	}

	public void removeObjectRenderer(RenderedFilter pair) {
		objectRenderers.remove(pair);
		for (LinkDatabaseCanvas canvas : canvasList) {
			canvas.removeObjectRenderer(pair);
		}
	}

	public void setLinkRenderers(List<RenderedFilter> renderers) {
		linkRenderers.clear();
		linkRenderers.addAll(renderers);
		for (LinkDatabaseCanvas canvas : canvasList) {
			canvas.setLinkRenderers(renderers);
		}
	}

	public void setObjectRenderers(List<RenderedFilter> renderers) {
		objectRenderers.clear();
		objectRenderers.addAll(renderers);
		for (LinkDatabaseCanvas canvas : canvasList) {
			canvas.setObjectRenderers(renderers);
		}
	}

	@Override
	public Color getBackground() {
		// Using background color to indicate global vs. local selection mode.
		if (isLive())
			return Color.white;
		else
			return lockGray;
	}

}
