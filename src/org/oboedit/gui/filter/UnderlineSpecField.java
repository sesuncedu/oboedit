package org.oboedit.gui.filter;

public class UnderlineSpecField implements GeneralRendererSpecField<Boolean> {

	public static final UnderlineSpecField FIELD = new UnderlineSpecField();

	public String getID() {
		return "underline";
	}

	public String getName() {
		return "Underline";
	}

	public Boolean merge(Boolean a, Boolean b) {
		return a || b;
	}

	public int getHTMLType() {
		return HTML;
	}

	public void renderHTML(Boolean value, StringBuffer in) {
		if (value.booleanValue()) {
			in.insert(0, "<u>");
			in.append("</u>");
		}
	}

	public GeneralRendererSpecFieldEditor<Boolean> getEditor() {
		return new EmptyBooleanEditor();
	}

}
