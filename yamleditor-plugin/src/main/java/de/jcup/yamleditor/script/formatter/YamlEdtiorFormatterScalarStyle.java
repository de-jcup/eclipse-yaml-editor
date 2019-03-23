package de.jcup.yamleditor.script.formatter;

/**
 * Origin yaml format supports multiple different styles:
 * <br><br>
 * <pre>
 * - PLAIN (flow style)
 * - DOUBLE QUOTED (flow style)
 * - DOUBLE QUOTED (flow style)
 * - LITERAL (block style)
 * - FOLDING (block style)
 * </pre>
 * Nevertheless this editor supports not a highlighting for literal or folding
 * 100 % and this format seems to be not used very often.
 * <br><br> Also the used snake parser does pretty print in a ... own way.
 * E.g. having "plain" and "flow" active the results look very bad for kubernetes 
 * files. having "plain" and "block" active it works well. This is little bit strange,
 * because the plain style is a flow type, but the results look not well when using 
 * flow style in snake.. <br>
 * Also the comment rescue logic would not apply to those pretty printed parts of
 * literal and folding.
 * <br>
 * To prevent users having to play around with settings we provide only
 * PLAIN, DOUBLE_QUOTED and SINGLE_QUOTEd.
 * @see <a href="http://yaml.org/spec/1.1/#id903915">Chapter 9. Scalar
 *      Styles</a>
 * @see <a href="http://yaml.org/spec/1.1/#id858081">2.3. Scalars</a>
 */
public enum YamlEdtiorFormatterScalarStyle {

    PLAIN("scalarStylePlain",  "Plain"),

    DOUBLE_QUOTED("scalarStyleDoubleQuoted",  "Double quotes"),
    
    SINGLE_QUOTED("scalarStyleSingleQuoted",  "Single quotes"),

    ;

    private String id;
    private String text;
    
    private YamlEdtiorFormatterScalarStyle(String id, String text) {
        this.id = id;
        this.text=text;
    }
    
    public String getId() {
        return id;
    }
    
    public String getText() {
        return text;
    }

    /**
     * @param id
     * @return scalar style or if not found <code>null</code>
     */
    public static YamlEdtiorFormatterScalarStyle fromId(String id) {
        if (id==null) {
            return null;
        }
        for (YamlEdtiorFormatterScalarStyle style: YamlEdtiorFormatterScalarStyle.values()){
            if (id.equalsIgnoreCase(style.getId())){
                return style;
            }
        }
        return null;
    }
}
