package de.jcup.yamleditor.handlers;

import de.jcup.yamleditor.YamlEditor;

public abstract class AbstractYamlEditorFoldingHandler extends AbstractYamlEditorHandler {

	public AbstractYamlEditorFoldingHandler(){
		setBaseEnabled(getYamlEditor().isCodeFoldingEnabled());
	}
	
	@Override
	public void setEnabled(Object evaluationContext) {
		YamlEditor yamlEditor = getYamlEditor();
		if (yamlEditor==null) {
			return;
		}
		setBaseEnabled(yamlEditor.isCodeFoldingEnabled());
	}

}