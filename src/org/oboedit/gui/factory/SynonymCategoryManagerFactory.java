package org.oboedit.gui.factory;

import org.bbop.framework.AbstractComponentFactory;
import org.oboedit.gui.components.SynonymCategoryManager;

public class SynonymCategoryManagerFactory extends
		AbstractComponentFactory<SynonymCategoryManager> {
	public SynonymCategoryManagerFactory() {
	}

	public String getID() {
		return "SYNONYM_CATEGORY_MANAGER";
	}

	public SynonymCategoryManager doCreateComponent(String id) {
		return new SynonymCategoryManager(id);
	}

	public String getName() {
		return "Synonym Category Manager";
	}

	public FactoryCategory getCategory() {
		return FactoryCategory.METADATA;
	}
}
