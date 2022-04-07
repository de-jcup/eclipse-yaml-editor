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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.yamleditor.YamlEditorUtil;
import de.jcup.yamleditor.script.formatter.SnakeYamlConfig;
import de.jcup.yamleditor.script.formatter.YamlEdtiorFormatterScalarStyle;

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
                "Indent / Tab replacement", parent);
        indentEditor.setValidRange(2, SnakeYamlConfig.SNAKE_MAX_INDENT);
        addField(indentEditor);
        indentEditor.getLabelControl(parent).setToolTipText(
                "Amount of spaces used by source formatter to make indention.\nWill also be used when inserting spaces while pressing TAB key.");
			
		IntegerFieldEditor lineLengthEditor = new IntegerFieldEditor(P_SOURCE_FORMAT_LINE_LENGTH.getId(),
                "Max line length", parent);
        lineLengthEditor.setValidRange(40, SnakeYamlConfig.SNAKE_MAX_LINELENGTH);
        addField(lineLengthEditor);
        lineLengthEditor.getLabelControl(parent).setToolTipText(
                "Maximum lne length used by source formatter");
        
        String labelText = "Scalar style";
        YamlEdtiorFormatterScalarStyle[] allStyles = YamlEdtiorFormatterScalarStyle.values();
        String[][] entryNamesAndValues= new String[allStyles.length][2];
        int index=0;
        for (YamlEdtiorFormatterScalarStyle style: allStyles) {
            entryNamesAndValues[index++]=new String[] {
                    style.getText(),style.getId()
            };
        }
        ChangeableComboFieldEditor comboEditor = new ChangeableComboFieldEditor(P_SOURCE_SCALAR_STYLE_ID.getId(), labelText, entryNamesAndValues, parent);
		addField(comboEditor);
        
		BooleanFieldEditor rescueCommentsEditor = new BooleanFieldEditor(P_SOURCE_FORMAT_RESCUE_COMMENTS_ENABLED.getId(),
                "Rescue comments", parent);
        addField(rescueCommentsEditor);
        rescueCommentsEditor.getDescriptionControl(parent).setToolTipText(
                "When enabled comments will be rescued at formatting time.\n"
                + "(Full line comments will be at same line number as before, comments\n"
                + "at end of a yaml line are tried to be added at end of those lines again)");
        
        BooleanFieldEditor clearBlankLinesEditor = new BooleanFieldEditor(P_SOURCE_FORMAT_CLEAR_ALL_BLANK_LINES_ENABLED.getId(),
                "Clear all blank lines", parent);
        addField(clearBlankLinesEditor);
        clearBlankLinesEditor.getDescriptionControl(parent).setToolTipText(
                "When enabled blank lines will be removed.");
        
        BooleanFieldEditor tcpOnFormatEditor = new BooleanFieldEditor(P_PREVENT_TYPE_CONVERSION_ON_FORMAT_ENABLED.getId(), "Prevent type conversion", parent);
        addField(tcpOnFormatEditor);
        tcpOnFormatEditor.getDescriptionControl(parent)
        .setToolTipText("When enabled source formatter will try to keep data as is. \n"
                + "For example: 'on' is recognized as a boolean and is normally transformed into `true`.\n"
                + "This is prevented by having this option enabled.\n");

       
	}
	
}