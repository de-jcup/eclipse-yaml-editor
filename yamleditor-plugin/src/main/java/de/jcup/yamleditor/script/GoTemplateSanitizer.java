package de.jcup.yamleditor.script;

import java.util.regex.Pattern;

public class GoTemplateSanitizer implements YamlSanitizer{

    private static final String SANITIZER_MESSAGE = "Did found Go template parts.\n"
            + "Replaced them internally by ((..._...)) to have valid yaml for parsing and ensure AST for outline.\n"
            + "Also syntax highlighting for Go templates is enabled.\n\nIf you do not like this, please disable 'Go Template Support' inside Yaml Editor preferences.";
    
    private static final String GO_TEMPLATE_START = "{{";
    private static final String GO_TEMPLATE_END = "}}";
    public static final Pattern PATTERN_CURLYBRACES_OPEN = Pattern.compile(Pattern.quote(GO_TEMPLATE_START));
    public static final Pattern PATTERN_CURLYBRACES_CLOSE = Pattern.compile(Pattern.quote(GO_TEMPLATE_END));
    public static final Pattern PATTERN_TO_REDUCED_INSIDE = Pattern.compile("["+Pattern.quote("\"':-[]!#|>&%@]")+"]");
    
    public String sanitize(String source, YamlSanitizerContext context) {
        int index = source.indexOf(GO_TEMPLATE_START);
        if (index==-1) {
            /* nothing to do...*/
            return source;
        }
        StringBuilder sb = new StringBuilder(source);
        while(index!=-1) {
            int end = sb.indexOf(GO_TEMPLATE_END,index);
            if (end==-1) {
                break;
            }
            String part = sb.substring(index,end+2);
            part =PATTERN_CURLYBRACES_OPEN.matcher(part).replaceAll("((");
            part =PATTERN_CURLYBRACES_CLOSE.matcher(part).replaceAll("))");
            part =PATTERN_TO_REDUCED_INSIDE.matcher(part).replaceAll("_");
            
            sb.replace(index, end+GO_TEMPLATE_END.length(), part);
            
            index = sb.indexOf(GO_TEMPLATE_START);
        }
        context.addSanitizerMessage(SANITIZER_MESSAGE);
        
        return sb.toString();
    }
}
