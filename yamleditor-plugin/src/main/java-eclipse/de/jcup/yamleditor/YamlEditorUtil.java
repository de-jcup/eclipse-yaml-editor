/*
 * Copyright 2017 Albert Tregnaghi
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
package de.jcup.yamleditor;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

import de.jcup.yamleditor.preferences.YamlEditorPreferences;
import de.jcup.yamleditor.script.YamlError;

public class YamlEditorUtil {

	public static YamlEditorPreferences getPreferences() {
		return YamlEditorPreferences.getInstance();
	}

	private static UnpersistedMarkerHelper scriptProblemMarkerHelper = new UnpersistedMarkerHelper(
			"de.jcup.yamleditor.script.problem");
	
	public static void logInfo(String info) {
		getLog().log(new Status(IStatus.INFO, YamlEditorActivator.PLUGIN_ID, info));
	}

	public static void logWarning(String warning) {
		getLog().log(new Status(IStatus.WARNING, YamlEditorActivator.PLUGIN_ID, warning));
	}

	public static void logError(String error, Throwable t) {
		getLog().log(new Status(IStatus.ERROR, YamlEditorActivator.PLUGIN_ID, error, t));
	}

	public static void removeScriptErrors(IEditorPart editor) {
		if (editor == null) {
			return;
		}
		IEditorInput input = editor.getEditorInput();
		if (input == null) {
			return;
		}
		IResource editorResource = input.getAdapter(IResource.class);
		if (editorResource == null) {
			return;
		}
		scriptProblemMarkerHelper.removeMarkers(editorResource);
	}

	public static void addScriptInfo(IEditorPart editor, int line, String message) {
	    addScriptMarker(editor, line, IMarker.SEVERITY_INFO, message, -1,-1);
	}
	
	public static void addScriptError(IEditorPart editor, int line, YamlError error, int severity) {
		if (error == null) {
			return;
		}
		String message = error.getMessage();
		int start = error.getStart();
		int end = error.getEnd();

		addScriptMarker(editor, line, severity, message, start, end);
	}

    private static void addScriptMarker(IEditorPart editor, int line, int severity, String message, int start, int end) {
        if (editor == null) {
		    return;
		}
		IEditorInput input = editor.getEditorInput();
		if (input == null) {
			return;
		}
		IResource editorResource = input.getAdapter(IResource.class);
		if (editorResource == null) {
			return;
		}
		try {
            scriptProblemMarkerHelper.createScriptMarker(severity, editorResource, message, line, start,
					+ end);
		} catch (CoreException e){
			logError("Was not able to add error markers", e);
		}
    }

	private static ILog getLog() {
		ILog log = YamlEditorActivator.getDefault().getLog();
		return log;
	}

}
