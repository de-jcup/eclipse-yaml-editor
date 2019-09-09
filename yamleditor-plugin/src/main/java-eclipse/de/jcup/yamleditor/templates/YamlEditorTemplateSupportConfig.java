package de.jcup.yamleditor.templates;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.templates.Template;

import de.jcup.eclipse.commons.templates.TemplateSupportConfig;

public class YamlEditorTemplateSupportConfig implements TemplateSupportConfig {

    @Override
    public String getTemplatesKey() {
        return "de.jcup.yamleditor.templates";
    }

    @Override
    public List<String> getContextTypes() {
        return Arrays.asList("de.jcup.yamleditor.template.context");
    }

    @Override
    public String getTemplateImagePath(Template template) {
        return "/icons/yaml-editor.png";
    }

}
