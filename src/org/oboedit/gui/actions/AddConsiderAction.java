package org.oboedit.gui.actions;


import java.util.*;

import javax.swing.KeyStroke;

import org.obo.datamodel.*;
import org.obo.history.*;
import org.oboedit.controller.SelectionManager;
import org.oboedit.gui.*;
import org.oboedit.util.GUIUtil;

public class AddConsiderAction implements DropMenuAction {

	protected boolean isLegal = false;

	protected ObsoletableObject target;
	protected Selection selection;
	protected GestureTarget destItem;
	protected Collection<ObsoletableObject> considerTerms = new LinkedList<ObsoletableObject>();

	public String getName() {
		return "Add consider term";
	}

	public String getDesc() {
		return "Add consider term";
	}

	public List getSubActions() {
		return null;
	}

	public void dropInit(Selection selection, GestureTarget destItem) {
		considerTerms.clear();
		this.selection = selection;

		if (selection.isEmpty()) {
			isLegal = false;
			return;
		}
		this.destItem = destItem;
		LinkedObject lo = destItem.getTerm();
		if (lo != null && lo instanceof ObsoletableObject)
			target = (ObsoletableObject) lo;
		else {
			isLegal = false;
			return;
		}

		for (LinkedObject term : selection.getTerms()) {
			if (term instanceof ObsoletableObject) {
				ObsoletableObject consider = (ObsoletableObject) term;
				if (!target.getConsiderReplacements().contains(consider))
					considerTerms.add(consider);
			}
		}

		if (considerTerms.size() < 1) {
			isLegal = false;
			return;
		}

		isLegal = true;
	}

	public boolean isLegal() {
		return isLegal;
	}

	public HistoryItem execute() {
		TermMacroHistoryItem item = new TermMacroHistoryItem(
				"add obsolete consider term");

		Iterator it = considerTerms.iterator();
		while (it.hasNext()) {
			ObsoletableObject considerTerm = (ObsoletableObject) it.next();
			AddConsiderHistoryItem addItem = new AddConsiderHistoryItem(target,
					considerTerm);
			item.addItem(addItem);
		}

		GUIUtil.setSelections(item, selection, SelectionManager
				.createSelectionFromTarget(destItem));

		return item;
	}

	public KeyStroke getKeyStroke() {
		return null;
	}
}
