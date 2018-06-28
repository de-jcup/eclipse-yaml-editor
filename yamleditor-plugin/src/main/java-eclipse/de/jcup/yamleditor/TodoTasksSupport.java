package de.jcup.yamleditor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IEditorInput;

/**
 * 
 * @author Albert Tregnaghi
 *
 */
public class TodoTasksSupport /* extends IncrementalProjectBuilder */ implements IResourceChangeListener {

	public static TodoTasksSupport INSTANCE = new TodoTasksSupport();

	private TodoTasksSupport() {

	}

	public void install() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (workspace == null) {
			return;
		}
		workspace.addResourceChangeListener(this);
	}

	public void uninstall() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (workspace == null) {
			return;
		}
		workspace.removeResourceChangeListener(this);

	}

	public static class TodoTaskDefinition {
		public TodoTaskDefinition(String identifier, int priority) {
			this.identifier = identifier;
			this.priority = priority;
		}

		private int priority;
		private String identifier;
	}

	private static List<TodoTaskDefinition> todoTaskDefinitions = new ArrayList<>();
	private static List<TodoTaskDefinition> unmodifiableTodoTaskDefintiosn = Collections
			.unmodifiableList(todoTaskDefinitions);
	static {
		todoTaskDefinitions.add(new TodoTaskDefinition("TODO", IMarker.PRIORITY_NORMAL));
		todoTaskDefinitions.add(new TodoTaskDefinition("FIXME", IMarker.PRIORITY_HIGH));
	}

	public static List<TodoTaskDefinition> getDefinedTasks() {
		return unmodifiableTodoTaskDefintiosn;
	}

	private PersistedMarkerHelper helper = new PersistedMarkerHelper("de.jcup.yamleditor.script.task");

	public void updateTasksFor(String sourceCode, IEditorInput editorInput) throws CoreException {
		// if (editorInput == null) {
		// return;
		// }
		// IResource editorResource = editorInput.getAdapter(IResource.class);
		// updateTasksFor(sourceCode, editorResource);
	}

	public void updateTasksFor(String sourceCode, IResource resource) throws CoreException {
		// if (resource == null) {
		// return;
		// }
		// /* rebuild */
		// String[] lines = sourceCode.split("\n");
		// updateTasksFor(resource, lines);
	}

	protected void updateTasksFor(IResource resource, String[] lines) throws CoreException {
//		IWorkspace workspace = ResourcesPlugin.getWorkspace();
//		   final IProject project = workspace.getRoot().getProject("My Project");
//		   IWorkspaceRunnable operation = new IWorkspaceRunnable() {
//		      public void run(IProgressMonitor monitor) throws CoreException {
//		    	  /* FIXME ATR: collect the data and do ONE operation */
//		    	  helper.removeMarkers(resource);
//		    	  handleLines(lines, resource);
////		         int fileCount = 10;
////		         project.create(null);
////		         project.open(null);
////		         for (int i = 0; i < fileCount; i++) {
////		            IFile file = project.getFile("File" + i);
////		            file.create(null, IResource.NONE, null);
////		         }
//		      }
//		   };
		   EclipseUtil.safeAsyncExec(new Runnable() {
			
			@Override
			public void run() {
				  helper.removeMarkers(resource);
		    	  try {
					handleLines(lines, resource);
				} catch (CoreException e) {
					logError("cannot update markers", e);
				}
			}
		});
//		   workspace.run(operation, null);
	}

	protected void handleLines(String[] lines, IResource editorResource) throws CoreException {
		int lineNumber = 0;
		for (String line : lines) {
			lineNumber++;
			if (!line.startsWith("#")) {
				continue;
			}
			/*
			 * FIXME ATR, 27.06.2018: rebuild problem! what happens when
			 * todos/fixmes are created outside the editor, what about builds,
			 * unopened files?
			 */
			/* TODO ATR, 2018-06-27: make the taskIdentifiers configurable */
			/* @formatter:off
			 * In CDT there is a complete own implementation for todo and fixme handling etc. see 
			 * some see https://github.cIResourceChangeListenerom/eclipse/cdt/blob/12681f780730c11fbf777ad2b53158fe34d12714/core/org.eclipse.cdt.ui/src/org/eclipse/cdt/internal/ui/preferences/TodoTaskConfigurationBlock.java
			 * @formatter:on
			 */
			for (TodoTaskDefinition task : todoTaskDefinitions) {
				addMarker(task, line, lineNumber, editorResource);
			}
		}
	}

	protected void addMarker(TodoTaskDefinition taskDefinition, String line, int lineNumber, IResource editorResource)
			throws CoreException {
		if (taskDefinition == null) {
			return;
		}
		String taskIdentifier = taskDefinition.identifier;
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

		helper.createTaskMarker(taskDefinition.priority, editorResource, taskIdentifier + " " + message, lineNumber, -1,
				-1);

	}

	// @Override
	// protected IProject[] build(int kind, Map<String, String> args,
	// IProgressMonitor monitor) throws CoreException {
	// if (kind == FULL_BUILD) {
	// fullBuild(monitor);
	// } else {
	// IResourceDelta delta = getDelta(getProject());
	// if (delta == null) {
	// fullBuild(monitor);
	// } else {
	// incrementalBuild(delta, monitor);
	// }
	// }
	// return null;
	// }

	// private void incrementalBuild(IResourceDelta delta, IProgressMonitor
	// monitor) {
	// try {
	// delta.accept(new TodoTaskResourceDeltaVisitor(monitor));
	// } catch (CoreException e) {
	// logError("incremental build failed", e);
	// }
	// }

	private void logError(String message, Exception e) {
		YamlEditorUtil.logError(message, e);

	}

	// private void fullBuild(IProgressMonitor monitor) {
	// try {
	// getProject().accept(new TodoTaskResourceVisitor(monitor));
	// } catch (CoreException e) {
	// logError("fullbuild failed", e);
	// }
	// }
	protected void visitResource(IResource resource) throws CoreException {
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getContents(), "UTF-8"))) {
				String line = null;
				List<String> list = new ArrayList<>();
				while ((line = br.readLine()) != null) {
					list.add(line);
				}
				updateTasksFor(resource, list.toArray(new String[list.size()]));
			} catch (RuntimeException | IOException e) {
				throw new CoreException(
						new Status(Status.ERROR, YamlEditorActivator.PLUGIN_ID, "Not able to visit resource", e));
			}

		}
	}

	// private class AbstractTodoTaskResourceVisitor{
	// protected IProgressMonitor monitor;
	//
	// public AbstractTodoTaskResourceVisitor(IProgressMonitor monitor) {
	// this.monitor=monitor;
	// if (this.monitor==null){
	// this.monitor=new NullProgressMonitor();
	// }
	//// @Override
	//// protected IProject[] build(int kind, Map<String, String> args,
	// IProgressMonitor monitor) throws CoreException {
	//// if (kind == FULL_BUILD) {
	//// fullBuild(monitor);
	//// } else {
	//// IResourceDelta delta = getDelta(getProject());
	//// if (delta == null) {
	//// fullBuild(monitor);
	//// } else {
	//// incrementalBuild(delta, monitor);
	//// }
	//// }
	//// return null;"
	//// }
	// }
	//
	// }

	// private class TodoTaskResourceVisitor extends
	// AbstractTodoTaskResourceVisitor implements IResourceVisitor{
	//
	//
	// public TodoTaskResourceVisitor(IProgressMonitor monitor) {
	// super(monitor);
	// }
	//
	// @Override
	// public boolean visit(IResource resource) throws CoreException {
	// if (monitor.isCanceled()){
	// return false;
	// }
	// visitResource(resource);
	// return true;
	// }
	//
	// }
	//
	//
	// private class TodoTaskResourceDeltaVisitor extends
	// AbstractTodoTaskResourceVisitor implements IResourceDeltaVisitor{
	//
	// public TodoTaskResourceDeltaVisitor(IProgressMonitor monitor) {
	// super(monitor);
	// }
	//
	// @Override
	// public boolean visit(IResourceDelta delta) throws CoreException {
	// if (monitor.isCanceled()){
	// return false;
	// }
	// if ( (delta.getKind() & IResourceDelta.ADDED ) == IResourceDelta.ADDED){
	// visitResource(delta.getResource());
	// }
	// return true;
	// }
	//
	//
	// }

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IResource resource = event.getResource();
		handleResource(resource);
		handleDelta(event.getDelta());
	}

	private void handleResource(IResource resource) {
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			String fileExtension = file.getFileExtension();
			boolean isYaml = "yaml".equals(fileExtension);
			isYaml = isYaml || "yml".equals(fileExtension);

			if (!isYaml) {
				return;
			}
			try {
				visitResource(file);
			} catch (CoreException e) {
				YamlEditorUtil.logError("Cannot visit resource:" + file, e);
			}
		}
	}

	private void handleDelta(IResourceDelta delta) {
		if (delta== null){
			return;
		}
		IResource resource = delta.getResource();
		if (resource instanceof IFile) {
			handleResource(resource);
			return;
		}
		for (IResourceDelta childDelta : delta.getAffectedChildren()) {
			handleDelta(childDelta);
		}
	}

}
