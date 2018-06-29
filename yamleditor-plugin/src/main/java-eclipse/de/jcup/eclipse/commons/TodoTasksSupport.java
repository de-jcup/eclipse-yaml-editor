/*
 * Copyright 2018 Albert Tregnaghi
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
package de.jcup.eclipse.commons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.texteditor.MarkerUtilities;

/**
 * A full standalone todo task solution which can be copied into a plugin and
 * works... Only markertype and implementation for logging necessary on
 * consumser side. <br>
 * <br>
 * This class is part of de.jcup.eclipse.commons which are a "copy-waste"
 * friendly approach. Any changes shall be done inside 
 * https://github.com/de-jcup/eclipse-commons/blob/master/src/main/java/de/jcup/eclipse/commons/TodoTasksSupport.java
 * and then copied to target plugins again...
 * 
 * @author Albert Tregnaghi
 * @version 1.1 - 2018-06-29
 *
 */
public class TodoTasksSupport implements IResourceChangeListener {

	private TodoTaskSupportProvider provider;

	/**
	 * Creates task support and assert provider provider and their
	 * content is not <code>null</code>
	 * 
	 * @param provider
	 */
	public TodoTasksSupport(TodoTaskSupportProvider provider) {
		if (provider == null) {
			throw new IllegalArgumentException("provider may not be null");
		}
		if (provider.getTodoTaskMarkerId() == null) {
			throw new IllegalArgumentException("provider.getMarkerType() may not result in null");
		}
		if (provider.getTodoTaskMarkerId().isEmpty()) {
			throw new IllegalArgumentException("provider.getMarkerType() may not be empty!");
		}
		if (provider.getTodoTaskDefinitions() == null) {
			throw new IllegalArgumentException("provider.getTodoTaskDefinitions() may not result in null");
		}
		this.provider = provider;
	}

	/**
	 * Installs the support. Should be done in start method of Plugin Activator class
	 */
	public void install() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (workspace == null) {
			return;
		}
		workspace.addResourceChangeListener(this);
	}

	/**
	 * Uninstalls the support. Should be done in end method of Plugin Activator class
	 */
	public void uninstall() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (workspace == null) {
			return;
		}
		workspace.removeResourceChangeListener(this);

	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		int type = event.getType();
		if (type != IResourceChangeEvent.POST_CHANGE) {
			return;
		}
		TodoTasksContext context = new TodoTasksContext();
		IResource resource = event.getResource();
		handleResource(context, resource);
		handleDelta(context, event.getDelta());
		try {
			createTodoMarkers(context);
		} catch (CoreException e) {
			provider.logError("Was not able to create todo markers", e);
		}
	}


	protected void visitLines(TodoTasksContext context, String[] lines, IFile file) throws CoreException {
		context.resourcesToClean.add(file);

		int lineNumber = 0;
		for (String line : lines) {
			lineNumber++;
			if (line==null || line.length() == 0 || !provider.isLineCheckforTodoTaskNessary(line)){
				continue;
			}
			for (TodoTaskDefinition task : provider.getTodoTaskDefinitions()) {
				addCreateMarkerAction(context, task, line, lineNumber, file);
			}
		}
	}

	protected void addCreateMarkerAction(TodoTasksContext context, TodoTaskDefinition definition, String line,
			int lineNumber, IResource editorResource) throws CoreException {
		if (context == null) {
			throw new IllegalArgumentException("context may not be null!");
		}
		if (definition == null) {
			return;
		}
		String taskIdentifier = definition.getIdentifier();
		if (taskIdentifier == null) {
			return;
		}
		int taskIdentifierLength = taskIdentifier.length();
		if (taskIdentifierLength == 0) {
			return;
		}
		int todoIndex = line.indexOf(taskIdentifier);
		if (todoIndex == -1) {
			return;
		}
		int end = todoIndex + taskIdentifierLength;
		String message = line.substring(end);
		if (message.length() == 0) {
			/* no content */
			return;
		}
		if (Character.isLetterOrDigit(message.charAt(0))) {
			/* TODOx is no todo... */
			return;
		}
		CreateMarkerAction action = new CreateMarkerAction();

		action.priority = definition.getPriority();
		action.resource = editorResource;
		action.message = taskIdentifier + " " + message;
		action.lineNumber = lineNumber;

		context.actions.add(action);

	}

	private void createTodoMarkers(TodoTasksContext context) throws CoreException {
		Job job = new Job("Update todos of type" + provider.getTodoTaskMarkerId()) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				List<CreateMarkerAction> actions = context.actions;
				List<IResource> resourcesToClean = context.resourcesToClean;
				int totalTasks = actions.size() + resourcesToClean.size();
				if (totalTasks == 0) {
					return Status.OK_STATUS;
				}
				int worked = 0;
				monitor.beginTask("Updating todo tasks", totalTasks);
				for (IResource resourceToClean : resourcesToClean) {
					removeMarkers(resourceToClean);
					monitor.worked(worked++);
				}
				for (CreateMarkerAction action : actions) {
					try {
						createTaskMarker(action.resource, action.message, action.lineNumber, action.priority);
					} catch (CoreException e) {
						return new Status(IStatus.ERROR, TodoTasksSupport.this.provider.getTodoTaskPluginId(),
								"Failed to create task markers", e);
					}
					monitor.worked(worked++);
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	private void createTaskMarker(IResource resource, String message, int lineNumber, int priority)
			throws CoreException {
		if (lineNumber <= 0)
			lineNumber = 1;
		HashMap<String, Object> map = new HashMap<>();
		map.put(IMarker.PRIORITY, new Integer(priority));
		map.put(IMarker.LOCATION, resource.getFullPath().toOSString());
		map.put(IMarker.MESSAGE, message);
		MarkerUtilities.setLineNumber(map, lineNumber);
		MarkerUtilities.setMessage(map, message);

		IMarker newMarker = resource.createMarker(provider.getTodoTaskMarkerId());
		newMarker.setAttributes(map);

	}

	private IMarker[] removeMarkers(IResource resource) {
		if (resource == null) {
			/* maybe sync problem - guard close */
			return new IMarker[] {};
		}
		IMarker[] tasks = null;
		if (resource != null) {
			try {
				tasks = resource.findMarkers(provider.getTodoTaskMarkerId(), true, IResource.DEPTH_ZERO);
				for (int i = 0; i < tasks.length; i++) {
					tasks[i].delete();
				}

			} catch (CoreException e) {
				provider.logError("Was not able to delete markers", e);
			}
		}
		if (tasks == null) {
			tasks = new IMarker[] {};
		}
		return tasks;
	}

	protected void visitResource(TodoTasksContext context, IFile file) throws CoreException {
		if (file==null){
			return;
		}
		try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getContents(), "UTF-8"))) {
			String line = null;
			List<String> list = new ArrayList<>();
			while ((line = br.readLine()) != null) {
				list.add(line);
			}
			String[] lines = list.toArray(new String[list.size()]);
			visitLines(context, lines, file);
		} catch (RuntimeException | IOException e) {
			throw new CoreException(
					new Status(Status.ERROR, provider.getTodoTaskPluginId(), "Not able to visit resource", e));
		}

	}

	private void handleResource(TodoTasksContext context, IResource resource) {
		if (!(resource instanceof IFile)) {
			return;
		}
		IFile file = (IFile) resource;
		boolean isFileExtensionHandled = isFileExtensionHandled(file);

		if (!isFileExtensionHandled) {
			return;
		}
		try {
			visitResource(context, file);
		} catch (CoreException e) {
			provider.logError("Cannot visit resource:" + file, e);
		}
	}

	protected boolean isFileExtensionHandled(IFile file) {
		String fileExtension = file.getFileExtension();
		List<String> fileExtensions = provider.getTodoTaskFileExtensions();
		for (String supportedFileExtension : fileExtensions) {
			boolean isFileExtensionHandled = supportedFileExtension.equals(fileExtension);
			if (isFileExtensionHandled) {
				return true;
			}
		}
		return false;
	}

	private void handleDelta(TodoTasksContext context, IResourceDelta delta) {
		if (delta == null) {
			return;
		}
		int flags = delta.getFlags();
		if (flags == IResourceDelta.MARKERS) {
			return;
		}
		IResource resource = delta.getResource();
		if (resource instanceof IFile) {
			handleResource(context, resource);
			return;
		}
		for (IResourceDelta childDelta : delta.getAffectedChildren()) {
			handleDelta(context, childDelta);
		}
	}

	public interface TodoTaskSupportProvider {

		public void logError(String error, Throwable t);

		/**
		 * @return todo task definitions. Implementations decide if this is
		 *         configurable by users or a fixed list...
		 */
		List<TodoTaskDefinition> getTodoTaskDefinitions();

		boolean isLineCheckforTodoTaskNessary(String line);

		/**
		 * @return the file extensions (without a dot! e.g. "yaml" to handle all
		 *         "*.yaml" files) which are inspected for _TODO_ analyzing
		 */
		List<String> getTodoTaskFileExtensions();

		/**
		 * @return marker ID which is used for TODOs
		 */
		String getTodoTaskMarkerId();

		/**
		 * 
		 * @return plugin id for the plugin where {@link TodoTasksSupport} is
		 *         used for
		 */
		String getTodoTaskPluginId();
	}

	public static class TodoTaskDefinition {
		private int priority;
		private String identifier;

		public TodoTaskDefinition(String identifier, int priority) {
			this.identifier = identifier;
			this.priority = priority;
		}

		public int getPriority() {
			return priority;
		}

		public void setPriority(int priority) {
			this.priority = priority;
		}

		public String getIdentifier() {
			return identifier;
		}

		public void setIdentifier(String identifier) {
			this.identifier = identifier;
		}

	}

	class TodoTasksContext {
		List<IResource> resourcesToClean = new ArrayList<>();
		List<CreateMarkerAction> actions = new ArrayList<>();
	}

	private static class CreateMarkerAction {

		int priority;
		IResource resource;
		String message;
		int lineNumber;
	}

}