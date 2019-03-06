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
package de.jcup.yamleditor;

import java.util.ArrayList;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.texteditor.IDocumentProvider;

import de.jcup.yamleditor.preferences.YamlEditorPreferences;

class ReplaceTabBySpacesVerifyKeyListener implements VerifyKeyListener {

    private final YamlEditor yamlEditor;

    ReplaceTabBySpacesVerifyKeyListener(YamlEditor yamlEditor) {
        if (yamlEditor == null) {
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

                    boolean isMultiline = ts.getStartLine() != -1 && ts.getEndLine() > ts.getStartLine();
                    boolean doIndent = event.stateMask == 0;
                    boolean doOutdent = (event.stateMask & SWT.SHIFT) == SWT.SHIFT;
                    
                    if (!doIndent && !doOutdent) {
                        return;
                    }

                    try {
                        int numSpaces = YamlEditorPreferences.getInstance().getAmountOfSpacesToReplaceTab();
                        if (numSpaces < 1) {
                            return;
                        }

                        String tabReplacement = createTabReplacement(numSpaces);

                        if (isMultiline) {
                            handleMultiLineSelection(ts, doc, doIndent, numSpaces, tabReplacement);
                        } else {
                            handleSingleLineSelection(ts, doc, offset, doIndent, numSpaces, tabReplacement);
                        }

                    } catch (BadLocationException e) {
                        EclipseUtil.logError("Cannot insert tab replacement at " + offset, e);
                    }
                }

                private void handleSingleLineSelection(ITextSelection ts, IDocument doc, int offset, boolean doIndent, int numSpaces, String tabReplacement) throws BadLocationException {
                    int newCaretPosition;
                
                    if (doIndent) {
                        // replace the selected text with our TAB-equivalent string:
                        doc.replace(offset, ts.getLength(), tabReplacement);
                        newCaretPosition = offset + numSpaces;
                    } else {
                        // special behavior: for single-line outdent the logic is similar to the
                        // multiline outdent except for the fact that the replaced block shall not be
                        // selected entirely, instead only the caret will be adjusted:
                
                        int offsetBlockStart = doc.getLineOffset(ts.getStartLine());
                        int offsetBlockEnd = doc.getLineOffset(ts.getEndLine()) + doc.getLineLength(ts.getEndLine());
                        int lengthBlock = offsetBlockEnd - offsetBlockStart;
                        if (lengthBlock <= 0) {
                            return; // should never happen - just in case
                        }
                
                        String line = doc.get(offsetBlockStart, lengthBlock);
                        String replacement = outdent(line, numSpaces);
                
                        doc.replace(offsetBlockStart, lengthBlock, replacement);
                
                        if (offset > offsetBlockStart + numSpaces) {
                            // there is enough space to move left the caret on the current line:
                            newCaretPosition = offset - numSpaces;
                        } else {
                            // don't place the caret on the line before the current one:
                            newCaretPosition = offsetBlockStart;
                        }
                    }
                
                    Control control = yamlEditor.getAdapter(Control.class);
                    if (control instanceof StyledText) {
                        StyledText t = (StyledText) control;
                        t.setCaretOffset(newCaretPosition);
                    }
                }

                private void handleMultiLineSelection(ITextSelection ts, IDocument doc, boolean doIndent, int numSpaces, String tabReplacement) throws BadLocationException {
                    // in case there is a multiline selection, we mimic the Eclipse behavior
                    // (which is quite standard across editors) and thus we:
                    // - discard the actual selected text and consider instead only the
                    // start/end line of the selection
                    // - then we indent/outdent the whole block of lines

                    int offsetBlockStart = doc.getLineOffset(ts.getStartLine());
                    int offsetBlockEnd = doc.getLineOffset(ts.getEndLine()) + doc.getLineLength(ts.getEndLine());
                    int lengthBlock = offsetBlockEnd - offsetBlockStart;
                    if (lengthBlock <= 0) {
                        return; // should never happen - just in case
                    }

                    String lineBlock = doc.get(offsetBlockStart, lengthBlock);

                    // split each line and insert the additional indent in each line:
                    String lines[] = lineBlock.split("\\r?\\n");
                    ArrayList<String> replacement = new ArrayList<String>();
                    for (String line : lines) {
                        if (doIndent) {
                            replacement.add(indent(line, tabReplacement));
                        }
                        else {
                            replacement.add(outdent(line, numSpaces));
                        }
                    }

                    String strReplacement = String.join("\n", replacement);
                    doc.replace(offsetBlockStart, lengthBlock, strReplacement);

                    // select the whole block we just indented/outdented:
                    yamlEditor.selectAndReveal(offsetBlockStart, strReplacement.length());
                }
            });
        }

    }

    private String createTabReplacement(int spaces) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < spaces; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }

    private String outdent(String line, int spaces) {
        int numLeadingSpaces = 0;
        for (int i = 0; i < spaces && i < line.length(); i++) {
            if (line.charAt(i) == ' ')
                numLeadingSpaces++;
            else
                break;
        }

        // most editors, including default Eclipse text editor, will outdent a line even
        // if
        // the number of leading spaces is smaller than the configured TAB length:
        // in such a case they simply remove all leading spaces:
        return line.substring(Math.min(numLeadingSpaces, spaces));
    }

    // just for symmetry with outdent():
    private String indent(String line, String toInsert) {
        return toInsert + line;
    }
}