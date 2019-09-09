package de.jcup.yamleditor.templates;

import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.TemplateContextType;

public class YamlEditorTemplateContextType extends TemplateContextType{

        public YamlEditorTemplateContextType() {
            addResolver(new GlobalTemplateVariables.Cursor());
            addResolver(new GlobalTemplateVariables.WordSelection());
            addResolver(new GlobalTemplateVariables.LineSelection());
            addResolver(new GlobalTemplateVariables.Dollar());
            addResolver(new GlobalTemplateVariables.Date());
            addResolver(new GlobalTemplateVariables.Year());
            addResolver(new GlobalTemplateVariables.Time());
            addResolver(new GlobalTemplateVariables.User());
        }
}
