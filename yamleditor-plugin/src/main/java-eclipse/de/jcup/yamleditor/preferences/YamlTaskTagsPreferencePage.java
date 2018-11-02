package de.jcup.yamleditor.preferences;

import de.jcup.eclipse.commons.tasktags.AbstractTaskTagsPreferencePage;
import de.jcup.yamleditor.YamlEditorActivator;

public class YamlTaskTagsPreferencePage extends AbstractTaskTagsPreferencePage{

	public YamlTaskTagsPreferencePage() {
		super(YamlEditorActivator.getDefault().getTaskSupportProvider(), "YAML todos","Define your todos definitions for YAML files");
	}

}
