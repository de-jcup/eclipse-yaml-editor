package de.jcup.yamleditor.handlers;

import de.jcup.yamleditor.YamlEditor;

public class YamlFoldingCollapseAllHandler extends AbstractYamlEditorFoldingHandler{

	@Override
	protected void executeOnYamlEditor(YamlEditor yamlEditor) {
		yamlEditor.collapseAllFoldings();
	}

}
