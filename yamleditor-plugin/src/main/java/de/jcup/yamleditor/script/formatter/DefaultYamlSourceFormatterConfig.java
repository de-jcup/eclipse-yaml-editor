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

}
