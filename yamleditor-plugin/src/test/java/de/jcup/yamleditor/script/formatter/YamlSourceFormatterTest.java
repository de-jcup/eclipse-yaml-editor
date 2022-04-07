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

import org.junit.Before;
import org.junit.Test;

public class YamlSourceFormatterTest {

    private YamlSourceFormatter formatterToTest;
    private DefaultYamlSourceFormatterConfig config;

    @Before
    public void before() {
        formatterToTest = new YamlSourceFormatter();
        config = new DefaultYamlSourceFormatterConfig();
    }

    @Test
    public void plainVariantKept_keep_blank_lines_enabled() {
        /* prepare */
        String source = "apiVersion: v1\nkind: Pod\nmetadata:\n  name: busybox-sleep";
        String expected = "apiVersion: v1\nkind: Pod\nmetadata:\n  name: busybox-sleep";

        config.setKeepBlankLines(true);

        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);

    }

    @Test
    public void plainVariantKept_keep_blank_lines_disabled() {
        /* prepare */
        String source = "apiVersion: v1\nkind: Pod\nmetadata:\n  name: busybox-sleep";
        String expected = "apiVersion: v1\nkind: Pod\nmetadata:\n  name: busybox-sleep";

        config.setKeepBlankLines(false);

        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);

    }

    @Test
    public void spaces_are_reduced_keep_blank_lines_enabled() {
        /* prepare */
        String source = "apiVersion: v1\nkind: Pod\nmetadata:\n  name:                busybox-sleep";
        String expected = "apiVersion: v1\nkind: Pod\nmetadata:\n  name: busybox-sleep";

        config.setKeepBlankLines(true);

        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);

    }

    @Test
    public void spaces_are_reduced_keep_blank_lines_disabled() {
        /* prepare */
        String source = "apiVersion: v1\nkind: Pod\nmetadata:\n  name:                busybox-sleep";
        String expected = "apiVersion: v1\nkind: Pod\nmetadata:\n  name: busybox-sleep";

        config.setKeepBlankLines(false);

        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);

    }

    @Test
    public void blank_lines_are_removed_keep_blank_lines_enabled() {
        /* prepare */
        String source = "apiVersion: v1\n\n\nkind: Pod\nmetadata:\n  name: busybox-sleep";
        String expected = "apiVersion: v1\n\n\nkind: Pod\nmetadata:\n  name: busybox-sleep";

        config.setKeepBlankLines(true);
        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);
    }

    @Test
    public void blank_lines_are_removed_keep_blank_lines_disabled() {
        /* prepare */
        String source = "apiVersion: v1\n\n\nkind: Pod\nmetadata:\n  name: busybox-sleep";
        String expected = "apiVersion: v1\nkind: Pod\nmetadata:\n  name: busybox-sleep";

        config.setKeepBlankLines(false);
        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);
    }

    @Test
    public void blank_lines_are_NOT_removed_when_keep_blank_lines() {
        /* prepare */
        String source = "apiVersion: v1\n\n\nkind: Pod\n\nmetadata:\n  name: busybox-sleep";
        String expected = "apiVersion: v1\n\n\nkind: Pod\n\nmetadata:\n  name: busybox-sleep";

        config.setKeepBlankLines(true);

        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);
    }

    @Test
    public void multi_docs_are_working_when_keep_blank_lines_enbled() {
        /* prepare */
        String source = "apiVersion: v1\nkind: Pod\nmetadata:\n  name: busybox-sleep\n---\napiVersion: v2\nkind: Role";
        String expected = "apiVersion: v1\nkind: Pod\nmetadata:\n  name: busybox-sleep\n---\napiVersion: v2\nkind: Role";

        config.setKeepBlankLines(true);

        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);

    }

    @Test
    public void multi_docs_are_working_when_keep_blank_lines_disabled() {
        /* prepare */
        String source = "apiVersion: v1\nkind: Pod\nmetadata:\n  name: busybox-sleep\n---\napiVersion: v2\nkind: Role";
        String expected = "apiVersion: v1\nkind: Pod\nmetadata:\n  name: busybox-sleep\n---\napiVersion: v2\nkind: Role";

        config.setKeepBlankLines(false);

        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);

    }

    @Test
    public void multi_docs_are_working2_when_keep_blank_lines_disabled() {
        /* prepare */
        String source = "---\napiVersion: v1\nkind: Pod\nmetadata:\n  name: busybox-sleep\n---\napiVersion: v2\nkind: Role";
        String expected = "---\napiVersion: v1\nkind: Pod\nmetadata:\n  name: busybox-sleep\n---\napiVersion: v2\nkind: Role";

        config.setKeepBlankLines(false);

        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);

    }

    @Test
    public void multi_docs_are_working2_when_keep_blank_lines_enabled() {
        /* prepare */
        String source = "---\napiVersion: v1\nkind: Pod\nmetadata:\n  name: busybox-sleep\n---\napiVersion: v2\nkind: Role";
        String expected = "---\napiVersion: v1\nkind: Pod\nmetadata:\n  name: busybox-sleep\n---\napiVersion: v2\nkind: Role";

        config.setKeepBlankLines(true);

        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);

    }

    @Test
    public void line_with_full_comment_restored_when_keep_blank_lines_enabled() {
        /* prepare */
        String source = "# I am line 1\napiVersion: v1\n# I am line 3\nkind: Pod\nmetadata:\n  name: busybox-sleep";
        String expected = "# I am line 1\napiVersion: v1\n# I am line 3\nkind: Pod\nmetadata:\n  name: busybox-sleep";

        config.setKeepBlankLines(true);

        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);

    }

    @Test
    public void line_with_full_comment_restored_when_keep_blank_lines_disabled() {
        /* prepare */
        String source = "# I am line 1\napiVersion: v1\n# I am line 3\nkind: Pod\nmetadata:\n  name: busybox-sleep";
        String expected = "# I am line 1\napiVersion: v1\n# I am line 3\nkind: Pod\nmetadata:\n  name: busybox-sleep";

        config.setKeepBlankLines(false);

        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);

    }

    @Test
    public void line_with_ending_comment_restored_when_keep_blank_lines_enabled() {
        /* prepare */
        String source = "apiVersion: v1       # I am commenting api version\nkind: Pod\nmetadata:\n  name: busybox-sleep";
        String expected = "apiVersion: v1 # I am commenting api version\nkind: Pod\nmetadata:\n  name: busybox-sleep";

        config.setKeepBlankLines(true);

        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);

    }

    @Test
    public void line_with_ending_comment_restored_when_keep_blank_lines_disabled() {
        /* prepare */
        String source = "apiVersion: v1       # I am commenting api version\nkind: Pod\nmetadata:\n  name: busybox-sleep";
        String expected = "apiVersion: v1 # I am commenting api version\nkind: Pod\nmetadata:\n  name: busybox-sleep";

        config.setKeepBlankLines(false);

        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);

    }

    @Test
    public void lines_with_full_comment_restored_when_keep_blank_lines_enabled() {
        /* prepare */
        String source = "# I am line 1\n# and in line2!\napiVersion: v1\n# I am line 4\n# I am line 5\nkind: Pod\nmetadata:\n  name: busybox-sleep";
        String expected = "# I am line 1\n# and in line2!\napiVersion: v1\n# I am line 4\n# I am line 5\nkind: Pod\nmetadata:\n  name: busybox-sleep";

        config.setKeepBlankLines(true);

        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);

    }

    @Test
    public void lines_with_full_comment_restored_when_keep_blank_lines_disabled() {
        /* prepare */
        String source = "# I am line 1\n# and in line2!\napiVersion: v1\n# I am line 4\n# I am line 5\nkind: Pod\nmetadata:\n  name: busybox-sleep";
        String expected = "# I am line 1\n# and in line2!\napiVersion: v1\n# I am line 4\n# I am line 5\nkind: Pod\nmetadata:\n  name: busybox-sleep";

        config.setKeepBlankLines(false);

        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);

    }

    @Test
    public void lines_with_full_comment_and_ending_restored_when_keep_blank_lines_enabled() {
        /* prepare */
        String source = "# I am line 1\n# and in line2!\napiVersion:      v1 # Kubernetes rocks\n# I am line 4\n# I am line 5\nkind: Pod\nmetadata:\n  name: busybox-sleep";
        String expected = "# I am line 1\n# and in line2!\napiVersion: v1 # Kubernetes rocks\n# I am line 4\n# I am line 5\nkind: Pod\nmetadata:\n  name: busybox-sleep";

        config.setKeepBlankLines(true);

        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);

    }

    @Test
    public void lines_with_full_comment_and_ending_restored_when_keep_blank_lines_disabled() {
        /* prepare */
        String source = "# I am line 1\n# and in line2!\napiVersion:      v1 # Kubernetes rocks\n# I am line 4\n# I am line 5\nkind: Pod\nmetadata:\n  name: busybox-sleep";
        String expected = "# I am line 1\n# and in line2!\napiVersion: v1 # Kubernetes rocks\n# I am line 4\n# I am line 5\nkind: Pod\nmetadata:\n  name: busybox-sleep";

        config.setKeepBlankLines(false);

        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);

    }

    @Test
    public void line_with_ending_comment_where_line_has_changed_will_be_inside_changed_one_keep_blank_lines() {
        /* prepare */
        String source = "a:\n\n b: value1 #comment of b";
        String expected = "a:\n\n  b: value1 #comment of b";

        config.setKeepBlankLines(true);

        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);

    }

    @Test
    public void line_with_ending_comment_where_line_has_changed_will_be_inside_changed_one_not_keep_blank_lines() {
        /* prepare */
        String source = "a:\n\n b: value1 #comment of b";
        String expected = "a:\n  b: value1 #comment of b";

        config.setKeepBlankLines(false);

        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);

    }

    @Test
    public void line_with_ending_comment_where_line_and_format_has_changed_will_be_inside_changed_one_keep_blank_lines() {
        /* prepare */
        String source = "a:\n\n b:    value1 #comment of b";
        String expected = "a:\n\n  b: value1 #comment of b";

        config.setKeepBlankLines(true);

        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);

    }

    @Test
    public void line_with_ending_comment_where_line_and_format_has_changed_will_be_inside_changed_one_not_keep_blank_lines() {
        /* prepare */
        String source = "a:\n\n b:    value1 #comment of b";
        String expected = "a:\n  b: value1 #comment of b";

        config.setKeepBlankLines(false);

        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);

    }

    @Test
    public void line_with_ending_comment_where_line_has_changed_will_be_inside_changed_one_even_when_there_was_same_line_without_comment_before_kbl_yes() {
        /* prepare */
        String source = "a:\n\n b: value1\n" + "---\na:\n\n b: value1 #comment of b";
        String expected = "a:\n\n  b: value1\n" + "---\na:\n\n  b: value1 #comment of b";

        config.setKeepBlankLines(true);

        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);

    }

    @Test
    public void line_with_ending_comment_where_line_has_changed_will_be_inside_changed_one_even_when_there_was_same_line_without_comment_before_kbl_no() {
        /* prepare */
        String source = "a:\n\n b: value1\n" + "---\na:\n\n b: value1 #comment of b";
        String expected = "a:\n  b: value1\n" + "---\na:\n  b: value1 #comment of b";

        config.setKeepBlankLines(false);

        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);

    }

    @Test
    public void line_with_fullcomment_when_structure_changes_kbl_yes() {
        /* prepare */
        String source = "a:\n\n b: value1\n\n\n" + "#This is here!\n---\na:\n\n b: value1 #comment of b";
        String expected = "a:\n\n  b: value1\n\n\n" + "#This is here!\n---\na:\n\n  b: value1 #comment of b";

        config.setKeepBlankLines(true);

        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);

    }

    @Test
    public void line_with_fullcomment_when_structure_changes_kbl_no() {
        /* prepare */
        String source = "a:\n\n b: value1\n\n\n" + "#This is here!\n---\na:\n\n b: value1 #comment of b";
        String expected = "a:\n  b: value1\n" + "#This is here!\n---\na:\n  b: value1 #comment of b";

        config.setKeepBlankLines(false);

        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);

    }

    @Test
    public void line_with_fullcomment_when_empty_lines_before_structure_changes_kbl_yes() {
        /* prepare */
        String source = "\n#In line2\na: value1";
        String expected = "\n#In line2\na: value1";

        config.setKeepBlankLines(true);

        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);

    }

    @Test
    public void line_with_fullcomment_when_empty_lines_before_structure_changes_kbl_no() {
        /* prepare */
        String source = "\n#In line2\na: value1";
        String expected = "#In line2\na: value1";

        config.setKeepBlankLines(false);

        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);

    }

    @Test
    public void line_with_fullcomment_when_empty_lines_before_structure_changes_keep_empty_lines_enabled() {
        /* prepare */
        String source = "\n#In line2\na: value1";
        String expected = "\n#In line2\na: value1";

        config.setKeepBlankLines(true);

        /* execute */
        String result = formatterToTest.format(source, config);

        /* test */
        assertEquals(expected, result);

    }

}
