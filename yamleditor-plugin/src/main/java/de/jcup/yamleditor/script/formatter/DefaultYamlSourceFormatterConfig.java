/*
 * Copyright 2019 Albert Tregnaghi
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
package de.jcup.yamleditor.script.formatter;

public class DefaultYamlSourceFormatterConfig implements YamlSourceFormatterConfig{

    private static final YamlEdtiorFormatterScalarStyle DEFAULT_SCALAR_STYLE = YamlEdtiorFormatterScalarStyle.PLAIN;
    private static final int DEFAULT_MAX_LINE_LENGTH = 80;
    private static final int DEFAULT_INDENT = 2;
    private static final boolean DEFAULT_RESTORE_COMMENTS_ENABLED =true;
    
    private int indent=DEFAULT_INDENT;
    private int maxLineLength=DEFAULT_MAX_LINE_LENGTH;
    private YamlEdtiorFormatterScalarStyle scalarStyle = DEFAULT_SCALAR_STYLE;
    private boolean restoreCommentsEnabled=DEFAULT_RESTORE_COMMENTS_ENABLED;
    private boolean preventTypeConversion;
    
    public void setIndent(int indent) {
        this.indent = indent;
    }

    public void setMaxLineLength(int lineLength) {
        this.maxLineLength = lineLength;
    }

    public void setRestoreCommentsEnabled(boolean restoreCommentsEnabled) {
        this.restoreCommentsEnabled = restoreCommentsEnabled;
    }
    
    @Override
    public int getIndent() {
        return indent;
    }

    @Override
    public int getMaxLineLength() {
        return maxLineLength;
    }

    public void setScalarStyle(YamlEdtiorFormatterScalarStyle scalarStyle) {
        this.scalarStyle = scalarStyle;
    }
    
    @Override
    public YamlEdtiorFormatterScalarStyle getScalarStyle() {
        return scalarStyle;
    }

    @Override
    public boolean isRestoreCommentsEnabled() {
        return restoreCommentsEnabled;
    }

    public void setPreventTypeConversion(boolean preventTypeConversion) {
        this.preventTypeConversion = preventTypeConversion;
    }
    @Override
    public boolean isPreventingTypeConversion() {
        return preventTypeConversion;
    }

}
