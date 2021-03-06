package org.oboedit.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.bbop.dataadapter.DataAdapterException;
import org.bbop.dataadapter.FileAdapterConfiguration;
import org.bbop.io.IOUtil;
import org.obo.dataadapter.DefaultHistoryDumper;
import org.obo.dataadapter.OBOAdapter;
import org.obo.dataadapter.OBOFileAdapter;
import org.obo.dataadapter.XMLHistoryAdapter;
import org.obo.datamodel.OBOSession;
import org.obo.history.HistoryGenerator;
import org.obo.history.HistoryList;

public class OBODiff {

	//initialize logger
	protected final static Logger logger = Logger.getLogger(OBODiff.class);

	public static void main(String[] args) throws IOException,
	DataAdapterException {
            try {
                IOUtil.setUpLogging();
            } catch (Exception e) {}

		Vector<String> filelist = new Vector<String>();
		OBOAdapter historyAdapter = null;
		String outPath = null;
		File outFile = null;

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-t")) {
				i++;
				if (i >= args.length) {
					printUsage();
					System.exit(1);
				} else if (args[i].equals("text")) {
					// it's the default, so we'll handle it later
				} else if (args[i].equals("xml")) {
					historyAdapter = new XMLHistoryAdapter();
				}
			} else if (args[i].equals("-o")) {
				i++;
				if (i >= args.length) {
					printUsage();
					System.exit(1);
				}
				outPath = args[i];
			} else
				filelist.add(args[i]);
		}
		if (filelist.size() != 2)
			printUsage();
		else {
			OBOSession a = getHistory(filelist.get(0));
			OBOSession b = getHistory(filelist.get(1));
			HistoryList changes = HistoryGenerator.getHistory(a, b, null);

			if (historyAdapter == null)
				historyAdapter = new DefaultHistoryDumper();
			boolean printResults = false;
			if (outPath == null) {
				outFile = File.createTempFile("history", ".dump");
				outPath = outFile.getAbsolutePath();
				outFile.deleteOnExit();
				printResults = true;
			}
			FileAdapterConfiguration adapterConfig = new FileAdapterConfiguration();
			adapterConfig.setWritePath(outPath);
			historyAdapter.doOperation(OBOAdapter.WRITE_HISTORY,
					adapterConfig, changes);
			if (printResults) {
				FileInputStream fis = new FileInputStream(outFile);
				IOUtil.dumpAndClose(fis, System.out);
				outFile.delete();

			}
		}
	}

	public static OBOSession getHistory(String path)
	throws DataAdapterException {
		OBOFileAdapter adapter = new OBOFileAdapter();
		OBOFileAdapter.OBOAdapterConfiguration config = new OBOFileAdapter.OBOAdapterConfiguration();
		config.getReadPaths().add(path);
		Object out = adapter.doOperation(OBOAdapter.READ_ONTOLOGY, config, null);
		return (OBOSession) out;
	}

	public static void printUsage() {
		logger.info("Usage: obodiff file1 file2");
	}
}
