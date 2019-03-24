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

import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;

public class SnakeYamlConfig {

    public static final int SNAKE_MAX_LINELENGTH = 400;// snake max is ?
    public static final int SNAKE_MAX_INDENT = 10;// snake max is 10

    private boolean explicitStart = false;
    private boolean explicitEnd = false;
    private int indent = 2;
    private int lineLength = 120;
    private FlowStyle flowStyle = FlowStyle.BLOCK;
    private boolean prettyFlow = false;
    private ScalarStyle scalarStyle = ScalarStyle.PLAIN;
    boolean multiDocFileStartingWithSeparator = false;

    public SnakeYamlConfig() {
    }

    public SnakeYamlConfig(YamlSourceFormatterConfig toCopy) {
        if (toCopy == null) {
            return;
        }
        
        this.indent = toCopy.getIndent();
        if (this.indent > SNAKE_MAX_INDENT) {
            this.indent = SNAKE_MAX_INDENT;
        }
        
        this.lineLength = toCopy.getMaxLineLength();
        if (this.lineLength > SNAKE_MAX_LINELENGTH) {
            this.lineLength = SNAKE_MAX_LINELENGTH;
        }
        YamlEdtiorFormatterScalarStyle yamlEdtiorFormatterScalarStyle = toCopy.getScalarStyle();
        if (yamlEdtiorFormatterScalarStyle == null) {
            return;
        }

        /* handle supported styles */
        switch (yamlEdtiorFormatterScalarStyle) {
        case DOUBLE_QUOTED:
            this.scalarStyle = ScalarStyle.DOUBLE_QUOTED;
            this.flowStyle = FlowStyle.BLOCK;
            break;
        case SINGLE_QUOTED:
            this.scalarStyle = ScalarStyle.SINGLE_QUOTED;
            this.flowStyle = FlowStyle.BLOCK;
            break;
        default:
        case PLAIN:
            this.scalarStyle = ScalarStyle.PLAIN;
            this.flowStyle = FlowStyle.BLOCK;
            break;
        }
    }

    public int getIndent() {
        return indent;
    }

    public int getLineLength() {
        return lineLength;
    }

    public boolean isExplicitEnd() {
        return explicitEnd;
    }

    public boolean isExplicitStart() {
        return explicitStart;
    }

    public boolean isPrettyFlow() {
        return prettyFlow;
    }

    public FlowStyle getFlowStyle() {
        return flowStyle;
    }

    public ScalarStyle getScalarStyle() {
        return scalarStyle;
    }

    public void setExplicitStart(boolean explicitStart) {
        this.explicitStart = explicitStart;
    }

    public void setExplicitEnd(boolean explicitEnd) {
        this.explicitEnd = explicitEnd;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    public void setFlowStyle(FlowStyle flowStyle) {
        this.flowStyle = flowStyle;
    }

    public void setPrettyFlow(boolean prettyFlow) {
        this.prettyFlow = prettyFlow;
    }

    public void setScalarStyle(ScalarStyle scalarStyle) {
        this.scalarStyle = scalarStyle;
    }

}