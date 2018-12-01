package de.jcup.yamleditor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.texteditor.IDocumentProvider;

import de.jcup.yamleditor.preferences.YamlEditorPreferences;

class ReplaceTabBySpacesVerifyKeyListener implements VerifyKeyListener {

	private final YamlEditor yamlEditor;

	ReplaceTabBySpacesVerifyKeyListener(YamlEditor yamlEditor) {
		if (yamlEditor==null) {
			throw new IllegalArgumentException();
		}
		this.yamlEditor = yamlEditor;
	}

	public void verifyKey(VerifyEvent event) {
		/* we do not allow tab in any case ! */
		if (event.character == '\t') {

			event.doit = false;

			EclipseUtil.safeAsyncExec(new Runnable() {

				public void run() {

					ISelection selection = yamlEditor.getSelectionProvider().getSelection();
					if (!(selection instanceof ITextSelection)) {
						return;
					}
					ITextSelection ts = (ITextSelection) selection;
					IDocumentProvider dp = yamlEditor.getDocumentProvider();
					IDocument doc = dp.getDocument(yamlEditor.getEditorInput());
					int offset = ts.getOffset();
					if (offset == -1) {
						offset = yamlEditor.lastCaretPosition;
					}
					try {
						int spaces = YamlEditorPreferences.getInstance().getAmountOfSpacesToReplaceTab();
						String toInsert = createTabReplacement(spaces);
						int toInsertLength = toInsert.length();
						doc.replace(offset, ts.getLength(), toInsert);
						Control control = yamlEditor.getAdapter(Control.class);
						if (control instanceof StyledText) {
							StyledText t = (StyledText) control;
							t.setCaretOffset(offset + toInsertLength);
						}
					} catch (BadLocationException e) {
						EclipseUtil.logError("Cannot insert tab replacement at " + offset, e);
					}
				}
			});
		}
		
	}
	
	private String createTabReplacement(int spaces) {
		if (spaces<1) {
			spaces=1;
		}
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<spaces;i++) {
			sb.append(' ');
		}
		return sb.toString();
	}
}