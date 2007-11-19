package org.oboedit.example;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.bbop.io.IOUtil;
import org.obo.datamodel.IdentifiedObject;
import org.oboedit.graph.AbstractFetchTask;
import org.oboedit.graph.OENode;
import org.oboedit.gui.components.LinkDatabaseCanvas;
import org.oboedit.gui.filter.BackgroundColorSpecField;
import org.oboedit.gui.filter.BoldSpecField;
import org.oboedit.gui.filter.GeneralRendererSpec;
import org.oboedit.gui.filter.HTMLSpecField;
import org.oboedit.gui.filter.HeatmapColor;

public class LineNumberFetchBehaviorTask extends AbstractFetchTask<Integer> {

	public LineNumberFetchBehaviorTask() {
		super(Integer.class);
	}

	@Override
	protected String getBehaviorID() {
		return "line_number_fetch";
	}

	protected String goFileLoc = "/Users/jrichter/ontology/gene_ontology_edit.obo";

	@Override
	protected Integer getValue(IdentifiedObject io) {
		int lineNum = 0;
		boolean found = false;
		try {
			BufferedReader reader = new BufferedReader(
					new FileReader(goFileLoc));
			String line;
			while ((line = reader.readLine()) != null) {
				lineNum++;
				if (line.startsWith("id: " + io.getID())) {
					found = true;
					break;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (!found)
			lineNum = -1;
		return lineNum;
	}

	@Override
	public void install(LinkDatabaseCanvas canvas) {
		super.install(canvas);
	}

	@Override
	protected GeneralRendererSpec getFetchedRenderer(LinkDatabaseCanvas canvas) {
		return new GeneralRendererSpec(HTMLSpecField.FIELD,
				"$term$<hr><center><font color=white>defined on line $"
						+ getValueVarName() + "$</font></center>",
				BackgroundColorSpecField.FIELD, new HeatmapColor(Color.yellow,
						Color.red, getValueVarName()));
	}

	@Override
	protected GeneralRendererSpec getPendingRenderer(LinkDatabaseCanvas canvas) {
		return new GeneralRendererSpec(HTMLSpecField.FIELD,
				"$term$<hr><center><font color=white><i>Loading...</i></font></center>");
	}
}
