package de.jcup.yamleditor.handlers;

import de.jcup.yamleditor.YamlEditor;

public class YamlFoldingExpandAllHandler extends AbstractYamlEditorFoldingHandler{
	
	@Override
	protected void executeOnYamlEditor(YamlEditor yamlEditor) {
		yamlEditor.expandAllFoldings();
	}
	
	
}
