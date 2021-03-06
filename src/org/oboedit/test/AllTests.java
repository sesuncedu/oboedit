package org.oboedit.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.obo.test.ReasonerTest;

public class AllTests extends TestCase {

	//initialize logger
	protected final static Logger logger = Logger.getLogger(AllTests.class);

	public static Test suite() {
		TestSuite out = new TestSuite();


		out.addTestSuite(SimpleOBORoundtripTest.class);

		out.addTestSuite(OBO2FlatTest.class);
		out.addTestSuite(OBO2OBOTest.class);

		out.addTestSuite(SimpleSerialRoundtripTest.class);

		out.addTestSuite(DatamodelTest.class);

		out.addTestSuite(CloneTest.class);
		out.addTestSuite(AddActionTest.class);

		out.addTestSuite(TermScopeSynonymTest.class);
		out.addTestSuite(TermRemoveSynonymTest.class);
		out.addTestSuite(TermSynonymTest.class);
		out.addTestSuite(TermSecondaryIDTest.class);
		out.addTestSuite(TermRemoveSecondaryIDTest.class);
		out.addTestSuite(TermCommentTest.class);
		out.addTestSuite(TermNamespaceTest.class);
		out.addTestSuite(TermNoNamespaceTest.class);
		out.addTestSuite(TermTextTest.class);
		out.addTestSuite(TermDefTest.class);

		out.addTestSuite(HistoryTest.class);

		out.addTestSuite(ReasonerTest.class);
		return out;
	}
}
