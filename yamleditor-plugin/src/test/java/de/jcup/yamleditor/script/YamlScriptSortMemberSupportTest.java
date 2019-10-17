package de.jcup.yamleditor.script;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class YamlScriptSortMemberSupportTest {
    
    private YamlScriptSortMemberSupport supportToTest;

    @Before
    public void before() {
        supportToTest = new YamlScriptSortMemberSupport();
    }

    @Test
    public void simple_sort_ascending_first_level() {
        /* @formatter:off*/
        
        
        /* prepare */
        String input = "x:\n" + 
        "  c: 1\n" + 
        "  b: 2\n" + 
        "a:\n" + 
        "  d: 3\n" + 
        "  a: 4";

        String expected = "a:\n" + 
                "  d: 3\n" + 
                "  a: 4\n" + 
                "x:\n" + 
                "  c: 1\n" + 
                "  b: 2\n";
        
        /* execute */
        String result = supportToTest.sortFirstMembers(input);
        
        /* test */
        assertEquals(expected,result);
        
        /* @formatter:on*/
    }

}
