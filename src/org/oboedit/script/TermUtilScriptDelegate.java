package org.oboedit.script;


import org.obo.datamodel.IdentifiedObject;
import org.obo.datamodel.LinkedObject;
import org.obo.util.TermUtil;

import org.apache.log4j.*;

public class TermUtilScriptDelegate {

	//initialize logger
	protected final static Logger logger = Logger.getLogger(TermUtilScriptDelegate.class);
	public static boolean isProperty(IdentifiedObject io) {
		return TermUtil.isProperty(io);
	}
	
	public static boolean isObsolete(IdentifiedObject io) {
		return TermUtil.isObsolete(io);
	}
	
	public static boolean isIntersection(IdentifiedObject io) {
		if (io instanceof LinkedObject)
			return TermUtil.isIntersection((LinkedObject) io);
		else
			return false;
	}
	
	public static boolean isTerm(IdentifiedObject io) {
		return TermUtil.isClass(io);
	}
	
	public static LinkedObject getRoot(LinkedObject lo) {
		return TermUtil.getRoot(lo);
	}
}
