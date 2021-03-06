package org.oboedit.graph;

import java.awt.Shape;
import java.awt.geom.GeneralPath;

import org.bbop.swing.ShapeUtil;
import org.obo.datamodel.PathCapable;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import org.apache.log4j.*;

public class PCNode extends PPath {

	//initialize logger
	protected final static Logger logger = Logger.getLogger(PCNode.class);

	public static final Object PATH_NODE = new Object();
	protected NamedChildProvider provider;

	protected PathCapable lo;
	protected PPath pathNode;

	public void initialize(PathCapable pc, NamedChildProvider provider, Shape s) {
		setOffset(0, 0);
		setProvider(provider);
		setObject(pc);
		setPickable(true);
		pathNode = new PPath();
		pathNode.setPickable(false);
		setNamedChild(PATH_NODE, pathNode);
		setPaint(null);
		setStroke(null);
		setStrokePaint(null);
		setShape(s);
		// setBounds(getPathDelegate().getBounds());
	}

	public void setShape(Shape s) {
		GeneralPath newShape = new GeneralPath();
		ShapeUtil.normalize(s, newShape);
		pathNode.setPathTo(newShape);
		setPathTo(newShape);
		//System.out.println(this+" "+newShape+" offset="+getXOffset() + s.getBounds2D().getX()+" , "+ getYOffset()
		//		+ s.getBounds2D().getY());
		setOffset(getXOffset() + s.getBounds2D().getX(), getYOffset()
				+ s.getBounds2D().getY());
		// setBounds(getPathDelegate().getBounds());
	}

	public void setTooltipFactory(TooltipFactory factory) {
		addAttribute(TooltipFactory.KEY, factory);
	}

	public PPath getPathDelegate() {
		return (PPath) getNamedChild(PATH_NODE);
	}

	public void setObject(PathCapable lo) {
		this.lo = lo;
	}

	public PathCapable getObject() {
		return lo;
	}

	public void setProvider(NamedChildProvider provider) {
		this.provider = provider;
	}

	public PNode getNamedChild(Object key) {
		return provider.getNamedChild(key, this);
	}

	public void setNamedChild(Object name, PNode value) {
		provider.setNamedChild(name, this, value);
	}

	public NamedChildProvider getProvider() {
		return provider;
	}

}
