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
