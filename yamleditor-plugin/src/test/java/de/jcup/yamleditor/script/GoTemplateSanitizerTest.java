package de.jcup.yamleditor.script;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class GoTemplateSanitizerTest {

    private GoTemplateSanitizer supportToTest;

    @Before
    public void before() {
        supportToTest = new GoTemplateSanitizer();
    }

    @Test
    public void empty_text_still_empty_text() {
        assertEquals("", supportToTest.sanitize("", new YamlSanitizerContext()));
    }

    @Test
    public void normal_text_is_not_changed() {
        assertEquals("normal text", supportToTest.sanitize("normal text", new YamlSanitizerContext()));
    }

    @Test
    public void normal_text_is_not_has_no_sanitizer_message() {
        /* prepare */
        YamlSanitizerContext context = new YamlSanitizerContext();

        /* execute */
        supportToTest.sanitize("normal text", context);

        /* test */
        assertFalse(context.hasSanitizerMessage());
        assertEquals(0, context.getSanitizerMessages().size());
    }
    
    @Test
    public void sanitized_text_contains_one_sanitizer_message() {
        /* prepare */
        YamlSanitizerContext context = new YamlSanitizerContext();

        /* execute */
        supportToTest.sanitize("{{.A_VARIABLE}}", context);

        /* test */
        assertTrue(context.hasSanitizerMessage());
        assertEquals(1, context.getSanitizerMessages().size());
    }

    @Test
    public void normal_templateVariable1_text_is_changed() {
        assertEquals("normal (( .TEMPLATE_VARIABLE1 ))-text", supportToTest.sanitize("normal {{ .TEMPLATE_VARIABLE1 }}-text", new YamlSanitizerContext()));
    }

    @Test
    public void complex_part_with_double_quotes_iniide_will_be_reduced() {
        /*
         * we must quote " inside because otherwise we have problems with yaml parsing
         * as well
         */
        assertEquals("chart: \"(( .Chart.Name ))-(( .Chart.Version _ replace _+_ ___ ))\"",
                supportToTest.sanitize("chart: \"{{ .Chart.Name }}-{{ .Chart.Version | replace \"+\" \"_\" }}\"", new YamlSanitizerContext()));
    }

}
