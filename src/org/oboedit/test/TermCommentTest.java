package org.oboedit.test;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.obo.datamodel.OBOClass;
import org.obo.history.CommentChangeHistoryItem;
import org.obo.history.HistoryItem;

public class TermCommentTest extends OperationTest {

	//initialize logger
	protected final static Logger logger = Logger.getLogger(TermCommentTest.class);

	@Override
	public Collection<TestBundle> getTestBundles() {
		Collection<TestBundle> testBundles = new LinkedList<TestBundle>();
		OBOClass oboClass = testUtil.getRandomClass();
		OBOClass resultClass = (OBOClass) oboClass.clone();
		String newText = TestUtil.getRandomString(40);
		resultClass.setComment(newText);

		HistoryItem item = new CommentChangeHistoryItem(oboClass, newText);
		ObjectPair pair = new ObjectPair(oboClass, resultClass);
		Collection<ObjectPair> pairs = new LinkedList<ObjectPair>();
		pairs.add(pair);
		TestBundle out = new TestBundle(item, pairs);
		testBundles.add(out);
		return testBundles;
	}
}
