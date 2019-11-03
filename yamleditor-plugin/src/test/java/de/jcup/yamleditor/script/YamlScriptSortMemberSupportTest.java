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
    public void is_containing_comments_test() {

        /* test */
        assertFalse(supportToTest.isHavingCommentsInside(null));
        assertFalse(supportToTest.isHavingCommentsInside(""));
        assertTrue(supportToTest.isHavingCommentsInside("# i am a comment"));
        assertFalse(supportToTest.isHavingCommentsInside("abc: '#'"));
        assertFalse(supportToTest.isHavingCommentsInside("abc: 'xx#'"));
        assertFalse(supportToTest.isHavingCommentsInside("abc: \"#\""));
        assertFalse(supportToTest.isHavingCommentsInside("abc: \"ab #\""));
        assertTrue(supportToTest.isHavingCommentsInside("a: 'xx' # i am a comment"));
        assertTrue(supportToTest.isHavingCommentsInside("abc: \"ab\"\n # bla"));

    }

    @Test
    public void simple_sort_ascending_all_on_empty_string() {
        /* @formatter:off*/
        
        
        /* prepare */
        String input = "";

        String expected = "" ; 
        
        /* execute */
        String result = supportToTest.sortAscending(input);
        
        /* test */
        assertEquals(expected,result);
        
        /* @formatter:on*/
    }

    @Test
    public void simple_sort_ascending_all() {
        /* @formatter:off*/
        
        
        /* prepare */
        String input = "x:\n" + 
        "  c: 1\n" + 
        "  b: 2\n" + 
        "a:\n" + 
        "  d: 3\n" + 
        "  a: 4";

        String expected = "a:\n" + 
                "  a: 4\n" + 
                "  d: 3\n" + 
                "x:\n" + 
                "  b: 2\n" +
                "  c: 1\n" ; 
        
        /* execute */
        String result = supportToTest.sortAscending(input);
        
        /* test */
        assertEquals(expected,result);
        
        /* @formatter:on*/
    }

    @Test
    public void simple_sort_ascending_all_when_blocks() {
        /* @formatter:off*/
        
        
        /* prepare */
        String input = "x:\n" + 
        "  c: 1\n" + 
        "  b: 2\n" + 
        "a:\n" + 
        "  d: 3\n" + 
        "  a: 4\n" +
        "---\n"+
        "h:\n"+
        "  c: 1\n" + 
        "  b: 2\n" + 
        "g:\n" + 
        "  d: 3\n" + 
        "  a: 4\n";

        String expected = "a:\n" + 
                "  a: 4\n" + 
                "  d: 3\n" + 
                "x:\n" + 
                "  b: 2\n" +
                "  c: 1\n" +
                "---\n"+
                "g:\n" + 
                "  a: 4\n" + 
                "  d: 3\n" + 
                "h:\n" + 
                "  b: 2\n" +
                "  c: 1\n" ; 
        
        /* execute */
        String result = supportToTest.sortAscending(input);
        
        /* test */
        assertEquals(expected,result);
        
        /* @formatter:on*/
    }

}
