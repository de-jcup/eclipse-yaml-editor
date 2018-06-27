package de.jcup.yamleditor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;

/**
 * 
 * @author Albert Tregnaghi
 *
 */
public class TodoTasksSupport {

	public static class TodoTaskDefinition{
		public TodoTaskDefinition(String identifier, int priority) {
			this.identifier=identifier;
			this.priority=priority;
		}
		private int priority;
		private String identifier;
	}
	
	private static List<TodoTaskDefinition> todoTaskDefinitions = new ArrayList<>();
	private static List<TodoTaskDefinition> unmodifiableTodoTaskDefintiosn = Collections.unmodifiableList(todoTaskDefinitions);
	static{
		todoTaskDefinitions.add(new TodoTaskDefinition("TODO",IMarker.PRIORITY_NORMAL));
		todoTaskDefinitions.add(new TodoTaskDefinition("FIXME", IMarker.PRIORITY_HIGH));
	}
	
	public static List<TodoTaskDefinition> getDefinedTasks(){
		return unmodifiableTodoTaskDefintiosn;
	}
	
	private PersistedMarkerHelper helper = new PersistedMarkerHelper("de.jcup.yamleditor.script.task");
	public void updateTasksFor(String sourceCode, IEditorInput editorInput) throws CoreException {
		if (editorInput == null) {
			return;
		}
		IResource editorResource = editorInput.getAdapter(IResource.class);
		updateTasksFor(sourceCode, editorResource);
	}
	public void updateTasksFor(String sourceCode, IResource resource) throws CoreException {
		if (resource == null) {
			return;
		}
		/* rebuild */
		helper.removeMarkers(resource);
		handleLines(sourceCode, resource);
	}

	protected void handleLines(String text, IResource editorResource) throws CoreException {
		String[] lines = text.split("\n");
		int lineNumber = 0;
		for (String line : lines) {
			lineNumber++;
			if (!line.startsWith("#")) {
				continue;
			}
			/* FIXME ATR, 27.06.2018: rebuild problem! what happens when todos/fixmes are created outside the editor, what about builds, unopened files? */
			/* TODO ATR, 2018-06-27: make the taskIdentifiers configurable*/
			/* @formatter:off
			 * In CDT there is a complete own implementation for todo and fixme handling etc. see 
			 * some see https://github.com/eclipse/cdt/blob/12681f780730c11fbf777ad2b53158fe34d12714/core/org.eclipse.cdt.ui/src/org/eclipse/cdt/internal/ui/preferences/TodoTaskConfigurationBlock.java
			 * @formatter:on
			 */
			for (TodoTaskDefinition task: todoTaskDefinitions){
				addMarker(task, line, lineNumber,editorResource);
			}
		}
	}

	protected void addMarker(TodoTaskDefinition taskDefinition, String line, int lineNumber, IResource editorResource) throws CoreException {
		if (taskDefinition==null){
			return;
		}
		String taskIdentifier = taskDefinition.identifier;
		if (taskIdentifier==null){
			return;
		}
		int taskIdentifierLength = taskIdentifier.length();
		if (taskIdentifierLength==0){
			return;
		}
		int todoIndex = line.indexOf(taskIdentifier);
		if (todoIndex == -1) {
			return;
		}
		int end = todoIndex + taskIdentifierLength;
		String message = line.substring(end);
		if (message.length()==0){
			/* no content*/
			return;
		}
		if (Character.isLetterOrDigit(message.charAt(0))){
			/* TODOx is no todo...*/
			return;
		}
		
		helper.createTaskMarker(taskDefinition.priority, editorResource, taskIdentifier+" "+message, lineNumber, -1,-1);
		
	}

}
