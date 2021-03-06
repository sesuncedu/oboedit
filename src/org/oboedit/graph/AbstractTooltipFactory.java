package org.oboedit.graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import edu.umd.cs.piccolo.PNode;

public abstract class AbstractTooltipFactory implements TooltipFactory {

	protected Collection<TooltipChangeListener> listeners = new LinkedList<TooltipChangeListener>();

	protected void fireTooltipChanged() {
		Iterator<TooltipChangeListener> it = listeners.iterator();
		while(it.hasNext()) {
			TooltipChangeListener listener = it.next();
			listener.tooltipChanged();
		}
	}
	
	public void addTooltipChangeListener(TooltipChangeListener listener) {
		listeners.add(listener);
	}

	public void destroyTooltip(PNode tooltip) {
	}

	public long getDelay() {
		return 3000;
	}

	public void removeTooltipChangeListener(TooltipChangeListener listener) {
		listeners.remove(listener);
	}

}
