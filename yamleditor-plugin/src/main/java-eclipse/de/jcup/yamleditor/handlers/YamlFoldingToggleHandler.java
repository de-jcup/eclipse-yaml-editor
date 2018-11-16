package de.jcup.yamleditor.handlers;

import de.jcup.yamleditor.YamlEditor;

public class YamlFoldingToggleHandler extends AbstractYamlEditorHandler {

	public YamlFoldingToggleHandler(){
	}
	
	@Override
	protected void executeOnYamlEditor(YamlEditor yamlEditor) {
		yamlEditor.toggleFolding();
	}

}
