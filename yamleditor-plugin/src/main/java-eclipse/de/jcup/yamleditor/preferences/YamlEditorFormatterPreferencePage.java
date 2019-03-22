package de.jcup.yamleditor.preferences;
/*
 * Copyright 2017 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */

import static de.jcup.yamleditor.preferences.YamlEditorPreferenceConstants.*;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.yamleditor.YamlEditorUtil;
import de.jcup.yamleditor.script.formatter.SnakeYamlSourceFormatterConfig;

public class YamlEditorFormatterPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public YamlEditorFormatterPreferencePage() {
		setPreferenceStore(YamlEditorUtil.getPreferences().getPreferenceStore());
	}
	
	@Override
	public void init(IWorkbench workbench) {
		
	}

	@Override
	protected void createFieldEditors() {
		Composite parent = getFieldEditorParent();
		  
        IntegerFieldEditor indentEditor = new IntegerFieldEditor(P_SOURCE_FORMAT_INDENT.getId(),
                "Indent", parent);
        indentEditor.setValidRange(3, SnakeYamlSourceFormatterConfig.SNAKE_MAX_INDENT);
        addField(indentEditor);
        indentEditor.getLabelControl(parent).setToolTipText(
                "Indention used by source formatter");
			
		IntegerFieldEditor lineLengthEditor = new IntegerFieldEditor(P_SOURCE_FORMAT_LINE_LENGTH.getId(),
                "Max line length", parent);
        lineLengthEditor.setValidRange(40, SnakeYamlSourceFormatterConfig.SNAKE_MAX_LINELENGTH);
        addField(lineLengthEditor);
        lineLengthEditor.getLabelControl(parent).setToolTipText(
                "Maximum lne length used by source formatter");
		
	}
	
}