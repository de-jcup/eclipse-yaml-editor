package de.jcup.yamleditor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;

import de.jcup.eclipse.commons.TodoTasksSupport.TodoTaskDefinition;
import de.jcup.eclipse.commons.TodoTasksSupport.TodoTaskSupportProvider;

class YamlTodoTaskSupportProvider implements TodoTaskSupportProvider{
	
	private List<String> fileExtensions = new ArrayList<>();
	private List<TodoTaskDefinition> definitions = new ArrayList<>();
	
	YamlTodoTaskSupportProvider(){
		definitions.add(new TodoTaskDefinition("TODO", IMarker.PRIORITY_NORMAL));
		definitions.add(new TodoTaskDefinition("FIXME", IMarker.PRIORITY_HIGH));
		
		fileExtensions.add("yaml");
		fileExtensions.add("yml");
	}
	
	@Override
	public void logError(String error, Throwable t) {
		YamlEditorUtil.logError(error, t);
	}

	@Override
	public List<TodoTaskDefinition> getTodoTaskDefinitions() {
		return definitions;
	}
	
	@Override
	public String getTodoTaskPluginId() {
		return YamlEditorActivator.PLUGIN_ID;
	}

	@Override
	public String getTodoTaskMarkerId() {
		return "de.jcup.yamleditor.script.task";
	}

	@Override
	public List<String> getTodoTaskFileExtensions() {
		return fileExtensions;
	}

	@Override
	public boolean isLineCheckforTodoTaskNessary(String line) {
		return line.startsWith("#");
	}
}