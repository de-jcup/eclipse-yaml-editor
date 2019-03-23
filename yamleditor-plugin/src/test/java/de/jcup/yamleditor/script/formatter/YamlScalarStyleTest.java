package de.jcup.yamleditor.script.formatter;

import static org.junit.Assert.*;

import org.junit.Test;

public class YamlScalarStyleTest {

    @Test
    public void to_id_works_for_plain() {
        assertEquals(YamlEdtiorFormatterScalarStyle.PLAIN, YamlEdtiorFormatterScalarStyle.fromId("scalarStylePlain"));
    }

    @Test
    public void to_id_works_for_plain_also_when_uppercased() {
        assertEquals(YamlEdtiorFormatterScalarStyle.PLAIN, YamlEdtiorFormatterScalarStyle.fromId("SCALARSTYLEPLAIN"));
    }

    @Test
    public void to_id_works_for_plain_also_when_lowercased() {
        assertEquals(YamlEdtiorFormatterScalarStyle.PLAIN, YamlEdtiorFormatterScalarStyle.fromId("scalarstyleplain"));
    }

    @Test
    public void to_id_works_for_double_quoted() {
        assertEquals(YamlEdtiorFormatterScalarStyle.DOUBLE_QUOTED, YamlEdtiorFormatterScalarStyle.fromId("scalarStyleDoubleQuoted"));
    }

    @Test
    public void to_id_works_for_single_quoted() {
        assertEquals(YamlEdtiorFormatterScalarStyle.SINGLE_QUOTED, YamlEdtiorFormatterScalarStyle.fromId("scalarStyleSingleQuoted"));
    }


}
