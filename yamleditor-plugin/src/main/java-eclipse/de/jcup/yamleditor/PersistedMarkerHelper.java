package de.jcup.yamleditor;

import org.eclipse.core.resources.IFile;

public class PersistedMarkerHelper extends AbstractMarkerHelper {

	public PersistedMarkerHelper(String markerType) {
		this.markerType = markerType;
	}

	public void removeAllMarkers(IFile file) {
		super.removeMarkers(file);
	}

}