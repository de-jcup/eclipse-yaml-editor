/*
 * Copyright 2018 Albert Tregnaghi
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
package de.jcup.yamleditor.handlers;

import de.jcup.yamleditor.YamlEditor;
import de.jcup.yamleditor.preferences.YamlEditorPreferences;

public abstract class AbstractYamlEditorFoldingHandler extends AbstractYamlEditorHandler {

    public AbstractYamlEditorFoldingHandler() {
        handleCodeFoldingEnabled();
    }

    @Override
    public void setEnabled(Object evaluationContext) {
        handleCodeFoldingEnabled();
    }

    private void handleCodeFoldingEnabled() {
        YamlEditor yamlEditor = getYamlEditor();
        boolean codeFoldingEnabled;
        
        if (yamlEditor == null) {
            codeFoldingEnabled = YamlEditorPreferences.getInstance().isCodeFoldingEnabledOnEditorStartup();
        } else {
            codeFoldingEnabled = yamlEditor.isCodeFoldingEnabled();
        }
        setBaseEnabled(codeFoldingEnabled);
        
    }
}