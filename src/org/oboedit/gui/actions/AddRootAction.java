package org.oboedit.gui.actions;


import java.util.*;
import org.obo.history.*;
import org.oboedit.gui.*;

import javax.swing.*;

import org.apache.log4j.*;

public class AddRootAction implements ClickMenuAction {

	//initialize logger
	protected final static Logger logger = Logger.getLogger(AddRootAction.class);

	protected List<EditAction> subActions = new Vector<EditAction>();

	public AddRootAction() {
		subActions.add(new TypedAddAction(false));
		subActions.add(new TypedAddAction(true));
	}

	public KeyStroke getKeyStroke() {
		return null;
	}

	public String getName() {
		return "Add root";
	}

	public String getDesc() {
		return null;
	}

	public List<EditAction> getSubActions() {
		return subActions;
	}

	public void clickInit(Selection paths, GestureTarget destItem) {
	}

	public boolean isLegal() {
		return true;
	}

	public HistoryItem execute() {
		return null;
	}
}
