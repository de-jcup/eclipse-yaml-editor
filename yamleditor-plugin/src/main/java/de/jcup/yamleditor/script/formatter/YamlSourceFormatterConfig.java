package de.jcup.yamleditor.script.formatter;

public interface YamlSourceFormatterConfig {

    int getIndent();

    int getMaxLineLength();
    
    YamlEdtiorFormatterScalarStyle getScalarStyle();

    boolean isRestoreCommentsEnabled();
    
}