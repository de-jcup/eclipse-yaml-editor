package de.jcup.yamleditor.script;

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
    

}
