package de.jcup.yamleditor.script;

import java.util.List;

public interface YamlSanitizer {

    /**
     * Does escape given source to a way which can be handled by YAML parsers
     * @param source
     * @param context never <code>null</code>
     * @return either unchanged source or when template detected, an escaped variant.
     */
    public String sanitize(String source, YamlSanitizerContext context);
}
