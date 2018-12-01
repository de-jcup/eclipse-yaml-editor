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

import static de.jcup.yamleditor.YamlEditorUtil.*;
import static de.jcup.yamleditor.preferences.YamlEditorPreferenceConstants.*;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

class YamlBracketInsertionCompleter extends KeyAdapter {

	private final YamlEditor yamlEditor;

	/**
	 * @param yamlEditor
	 */
	YamlBracketInsertionCompleter(YamlEditor yamlEditor) {
		this.yamlEditor = yamlEditor;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		InsertClosingBracketsSupport insertClosingBracketsSupport = getInsertionSupport(e);
		if (insertClosingBracketsSupport == null) {
			return;
		}
		/*
		 * do not use last caret start - because the listener ordering could be
		 * different
		 */
		ISelectionProvider selectionProvider = this.yamlEditor.getSelectionProvider();
		if (selectionProvider == null) {
			return;
		}
		ISelection selection = selectionProvider.getSelection();
		if (!(selection instanceof ITextSelection)) {
			return;
		}
		boolean enabled = getPreferences().getBooleanPreference(P_EDITOR_AUTO_CREATE_END_BRACKETS);
		if (!enabled) {
			return;
		}
		ITextSelection textSelection = (ITextSelection) selection;
		int offset = textSelection.getOffset();

		try {
			IDocument document = this.yamlEditor.getDocument();
			if (document == null) {
				return;
			}
			insertClosingBracketsSupport.insertClosingBrackets(document, selectionProvider, offset);
		} catch (BadLocationException e1) {
			/* ignore */
			return;
		}

	}

	protected InsertClosingBracketsSupport getInsertionSupport(KeyEvent e) {
		if (e.character == '[') {
			return new EdgeBracketInsertionSupport();
		}
		if (e.character == '{') {
			return new CurlyBracketInsertionSupport();
		}
		return null;
	}

	private abstract class InsertClosingBracketsSupport {
		protected abstract void insertClosingBrackets(IDocument document, ISelectionProvider selectionProvider, int offset)
				throws BadLocationException;
	}

	private class EdgeBracketInsertionSupport extends InsertClosingBracketsSupport {

		@Override
		protected void insertClosingBrackets(IDocument document, ISelectionProvider selectionProvider, int offset)
				throws BadLocationException {
			document.replace(offset - 1, 1, "[ ]");
			selectionProvider.setSelection(new TextSelection(offset + 1, 0));

		}

	}
	
	private class CurlyBracketInsertionSupport extends InsertClosingBracketsSupport {

		@Override
		protected void insertClosingBrackets(IDocument document, ISelectionProvider selectionProvider, int offset)
				throws BadLocationException {
			document.replace(offset - 1, 1, "{ }");
			selectionProvider.setSelection(new TextSelection(offset + 1, 0));

		}

	}
}