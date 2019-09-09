package de.jcup.yamleditor.preferences;

import de.jcup.eclipse.commons.templates.TemplateSupportPreferencePage;
import de.jcup.yamleditor.YamlEditorActivator;

public class YamlEditorTemplatePreferencePage extends TemplateSupportPreferencePage{

    public YamlEditorTemplatePreferencePage() {
        super(YamlEditorActivator.getDefault().getTemplateSupportProvider());
    }

}
