package de.jcup.yamleditor.script.formatter;

public class DefaultYamlSourceFormatterConfig implements YamlSourceFormatterConfig{

    private int indent;
    private int lineLength;
    
    public void setIndent(int indent) {
        this.indent = indent;
    }

    public void setLineLength(int lineLength) {
        this.lineLength = lineLength;
    }
    
    @Override
    public int getIndent() {
        return indent;
    }

    @Override
    public int getLineLength() {
        return lineLength;
    }

}
