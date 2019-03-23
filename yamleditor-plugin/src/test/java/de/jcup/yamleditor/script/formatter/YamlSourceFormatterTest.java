package de.jcup.yamleditor.script.formatter;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.jcup.yamleditor.script.formatter.YamlSourceFormatter;

public class YamlSourceFormatterTest {

    private YamlSourceFormatter formatterToTest;

    @Before
    public void before() {
        formatterToTest = new YamlSourceFormatter();
    }
    
    @Test
    public void plainVariantKept() {
        /* prepare */
        String source = "apiVersion: v1\nkind: Pod\nmetadata:\n  name: busybox-sleep";
        String expected = "apiVersion: v1\nkind: Pod\nmetadata:\n  name: busybox-sleep";
        
        /* execute */
        String result = formatterToTest.format(source);
        
        /* test */
        assertEquals(expected,result);
        
    }
    
    @Test
    public void spaces_are_reduced() {
        /* prepare */
        String source = "apiVersion: v1\nkind: Pod\nmetadata:\n  name:                busybox-sleep";
        String expected = "apiVersion: v1\nkind: Pod\nmetadata:\n  name: busybox-sleep";
        
        /* execute */
        String result = formatterToTest.format(source);
        
        /* test */
        assertEquals(expected,result);
        
    }
    
    @Test
    public void multi_docs_are_working() {
        /* prepare */
        String source = "apiVersion: v1\nkind: Pod\nmetadata:\n  name: busybox-sleep\n---\napiVersion: v2\nkind: Role";
        String expected = "apiVersion: v1\nkind: Pod\nmetadata:\n  name: busybox-sleep\n---\napiVersion: v2\nkind: Role";
        
        /* execute */
        String result = formatterToTest.format(source);
        
        /* test */
        assertEquals(expected,result);
        
    }
    
    @Test
    public void multi_docs_are_working2() {
        /* prepare */
        String source = "---\napiVersion: v1\nkind: Pod\nmetadata:\n  name: busybox-sleep\n---\napiVersion: v2\nkind: Role";
        String expected = "---\napiVersion: v1\nkind: Pod\nmetadata:\n  name: busybox-sleep\n---\napiVersion: v2\nkind: Role";
        
        /* execute */
        String result = formatterToTest.format(source);
        
        /* test */
        assertEquals(expected,result);
        
    }
    
    @Test
    public void line_with_full_comment_restored() {
        /* prepare */
        String source = "# I am line 1\napiVersion: v1\n# I am line 3\nkind: Pod\nmetadata:\n  name: busybox-sleep";
        String expected = "# I am line 1\napiVersion: v1\n# I am line 3\nkind: Pod\nmetadata:\n  name: busybox-sleep";
        
        /* execute */
        String result = formatterToTest.format(source);
        
        /* test */
        assertEquals(expected,result);
        
    }
    
    @Test
    public void line_with_ending_comment_restored() {
        /* prepare */
        String source   = "apiVersion: v1       # I am commenting api version\nkind: Pod\nmetadata:\n  name: busybox-sleep";
        String expected = "apiVersion: v1 # I am commenting api version\nkind: Pod\nmetadata:\n  name: busybox-sleep";
        
        /* execute */
        String result = formatterToTest.format(source);
        
        /* test */
        assertEquals(expected,result);
        
    }
    
    @Test
    public void lines_with_full_comment_restored() {
        /* prepare */
        String source = "# I am line 1\n# and in line2!\napiVersion: v1\n# I am line 4\n# I am line 5\nkind: Pod\nmetadata:\n  name: busybox-sleep";
        String expected = "# I am line 1\n# and in line2!\napiVersion: v1\n# I am line 4\n# I am line 5\nkind: Pod\nmetadata:\n  name: busybox-sleep";
        
        /* execute */
        String result = formatterToTest.format(source);
        
        /* test */
        assertEquals(expected,result);
        
    }
    
    @Test
    public void lines_with_full_comment_and_ending_restored() {
        /* prepare */
        String source = "# I am line 1\n# and in line2!\napiVersion:      v1 # Kubernetes rocks\n# I am line 4\n# I am line 5\nkind: Pod\nmetadata:\n  name: busybox-sleep";
        String expected = "# I am line 1\n# and in line2!\napiVersion: v1 # Kubernetes rocks\n# I am line 4\n# I am line 5\nkind: Pod\nmetadata:\n  name: busybox-sleep";
        
        /* execute */
        String result = formatterToTest.format(source);
        
        /* test */
        assertEquals(expected,result);
        
    }
    
    
    @Test
    public void line_with_ending_comment_where_line_has_changed_will_be_inside_changed_one() {
        /* prepare */
        String source = "a:\n\n b: value1 #comment of b";
        String expected = "a:\n  b: value1 #comment of b";
        
        /* execute */
        String result = formatterToTest.format(source);
        
        /* test */
        assertEquals(expected,result);
        
    }
    
    @Test
    public void line_with_ending_comment_where_line_and_format_has_changed_will_be_inside_changed_one() {
        /* prepare */
        String source = "a:\n\n b:    value1 #comment of b";
        String expected = "a:\n  b: value1 #comment of b";
        
        /* execute */
        String result = formatterToTest.format(source);
        
        /* test */
        assertEquals(expected,result);
        
    }
    
    
    @Test
    public void line_with_ending_comment_where_line_has_changed_will_be_inside_changed_one_even_when_there_was_same_line_without_comment_before() {
        /* prepare */
        String source = "a:\n\n b: value1\n"+"---\na:\n\n b: value1 #comment of b";
        String expected = "a:\n  b: value1\n"+"---\na:\n  b: value1 #comment of b";
        
        /* execute */
        String result = formatterToTest.format(source);
        
        /* test */
        assertEquals(expected,result);
        
    }
    

}
