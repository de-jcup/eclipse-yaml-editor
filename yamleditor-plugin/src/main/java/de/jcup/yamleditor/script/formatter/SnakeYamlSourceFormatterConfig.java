package de.jcup.yamleditor.script.formatter;

import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;

class SnakeYamlSourceFormatterConfig implements YamlSourceFormatterConfig {
    private boolean explicitStart = false;
    private boolean explicitEnd = false;
    private int indent = 2;
    private int lineLength = 120;
    private FlowStyle flowStyle = FlowStyle.BLOCK;
    private boolean prettyFlow = false;
    private ScalarStyle scalarStyle = ScalarStyle.PLAIN;
    boolean multiDocFileStartingWithSeparator=false;

    public SnakeYamlSourceFormatterConfig() {
    }

    public SnakeYamlSourceFormatterConfig(YamlSourceFormatterConfig toCopy) {
        if (toCopy==null) {
            return;
        }
        this.indent=toCopy.getIndent();
        this.lineLength=toCopy.getLineLength();
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

    @Override
    public int getIndent() {
        return indent;
    }

    @Override
    public int getLineLength() {
        return lineLength;
    }

    public YamlSourceFormatterConfig explicitStart(boolean explicitStart) {
        this.explicitStart = explicitStart;
        return this;
    }

    public YamlSourceFormatterConfig explicitEnd(boolean explicitEnd) {
        this.explicitEnd = explicitEnd;
        return this;
    }

    public YamlSourceFormatterConfig indent(int indent) {
        this.indent = indent;
        return this;
    }

    public YamlSourceFormatterConfig flowStyle(FlowStyle flowStyle) {
        this.flowStyle = flowStyle;
        return this;
    }

    public YamlSourceFormatterConfig prettyFlow(boolean prettyFlow) {
        this.prettyFlow = prettyFlow;
        return this;
    }

    public YamlSourceFormatterConfig scalarStyle(ScalarStyle scalarStyle) {
        this.scalarStyle = scalarStyle;
        return this;
    }

}