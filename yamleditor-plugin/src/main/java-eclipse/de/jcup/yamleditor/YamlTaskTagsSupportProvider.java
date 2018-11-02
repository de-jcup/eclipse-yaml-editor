package de.jcup.yamleditor;

import org.eclipse.core.resources.IFile;

import de.jcup.eclipse.commons.tasktags.AbstractConfigurableTaskTagsSupportProvider;

public class YamlTaskTagsSupportProvider extends AbstractConfigurableTaskTagsSupportProvider{

	public YamlTaskTagsSupportProvider(YamlEditorActivator plugin) {
		super(plugin);
	}

	@Override
	public boolean isLineCheckforTodoTaskNessary(String line, int lineNumber, String[] lines) {
		if (line==null){
			return false;
		}
		return line.startsWith("#");
	}

	@Override
	public String getTodoTaskMarkerId() {
		return "de.jcup.yamleditor.script.task";
	}

	@Override
	public boolean isFileHandled(IFile file) {
		if (file==null){
			return false;
		}
		String fileExtension = file.getFileExtension();
		if (fileExtension==null){
			return false;
		}
		return fileExtension.equals("yaml")||fileExtension.equals("yml");
	}

}
