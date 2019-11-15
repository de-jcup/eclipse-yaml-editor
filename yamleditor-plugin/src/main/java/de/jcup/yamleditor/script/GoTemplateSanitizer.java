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
package de.jcup.yamleditor.script;

import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Pattern;

public class GoTemplateSanitizer implements YamlSanitizer {

    private static final String SANITIZER_MESSAGE = "Did found Go template parts and sanitized them to have valid yaml: Replaced mapping parts internally by ((..._...)) and template only parts as comments to have valid yaml for parsing and ensure AST for outline.\n"
            + "Also syntax highlighting for Go templates was enabled.\n\nIf you do not like this, please disable 'Go Template Support' inside Yaml Editor preferences.";

    private static final String GO_TEMPLATE_START = "{{";
    private static final String GO_TEMPLATE_END = "}}";
    public static final Pattern PATTERN_CURLYBRACES_OPEN = Pattern.compile(Pattern.quote(GO_TEMPLATE_START));
    public static final Pattern PATTERN_CURLYBRACES_CLOSE = Pattern.compile(Pattern.quote(GO_TEMPLATE_END));
    public static final Pattern PATTERN_TO_REDUCED_INSIDE = Pattern.compile("[" + Pattern.quote("\"':-[]!#|>&%@]") + "]");

    public String sanitize(String source, YamlSanitizerContext context) {
        int index = source.indexOf(GO_TEMPLATE_START);
        if (index == -1) {
            /* nothing to do... */
            return source;
        }
        String sourceNoLeading = sanitizeGoTemplatesWithoutContentBefore(source);
        String sourceResult = sanitizeGoTemplatesAfterCharacters(context, sourceNoLeading);

        return sourceResult;
    }

    private String sanitizeGoTemplatesAfterCharacters(YamlSanitizerContext context,String sourceNoLeading) {
        int index = sourceNoLeading.indexOf(GO_TEMPLATE_START);
        StringBuilder sb = new StringBuilder(sourceNoLeading);
        while (index != -1) {
            int end = sb.indexOf(GO_TEMPLATE_END, index);
            if (end == -1) {
                break;
            }
            String part = sb.substring(index, end + 2);
            part = PATTERN_CURLYBRACES_OPEN.matcher(part).replaceAll("((");
            part = PATTERN_CURLYBRACES_CLOSE.matcher(part).replaceAll("))");
            part = PATTERN_TO_REDUCED_INSIDE.matcher(part).replaceAll("_");

            sb.replace(index, end + GO_TEMPLATE_END.length(), part);

            index = sb.indexOf(GO_TEMPLATE_START);
        }
        context.addSanitizerMessage(SANITIZER_MESSAGE);
        String sourceResult = sb.toString();
        return sourceResult;
    }

    private String sanitizeGoTemplatesWithoutContentBefore(String source) {
        /* handle lines beginning with "{{" */
        String[] lines = source.split("\n");
        StringBuilder sb0 = new StringBuilder();
        Iterator<String> lit = Arrays.asList(lines).iterator();
        while (lit.hasNext()) {
            String next = lit.next();
            if (next.trim().startsWith("{{")) {
                sb0.append("#");
            }
            sb0.append(next);
            if (lit.hasNext()) {
                sb0.append("\n");
            }
        }
        String sourceNoLeading = sb0.toString();
        return sourceNoLeading;
    }
}
