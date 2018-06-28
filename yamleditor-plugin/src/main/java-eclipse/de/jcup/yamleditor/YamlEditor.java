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

import static de.jcup.yamleditor.preferences.YamlEditorPreferenceConstants.*;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.CursorLinePainter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.ISourceViewerExtension2;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.junit.FixMethodOrder;

import de.jcup.yamleditor.YamlMarginRulePainter.MarginPaintSetup;
import de.jcup.yamleditor.document.YamlFileDocumentProvider;
import de.jcup.yamleditor.document.YamlTextFileDocumentProvider;
import de.jcup.yamleditor.outline.Item;
import de.jcup.yamleditor.outline.YamlEditorContentOutlinePage;
import de.jcup.yamleditor.outline.YamlEditorTreeContentProvider;
import de.jcup.yamleditor.outline.YamlQuickOutlineDialog;
import de.jcup.yamleditor.preferences.YamlEditorPreferenceConstants;
import de.jcup.yamleditor.preferences.YamlEditorPreferences;
import de.jcup.yamleditor.script.YamlError;
import de.jcup.yamleditor.script.YamlScriptModel;
import de.jcup.yamleditor.script.YamlScriptModelBuilder;

@AdaptedFromEGradle
/**
 * Inspiredby BashEditor, EGradleEditor and additonally by my older stuff:
 * https://sourceforge.net/p/yamli/code/HEAD/tree/de.jcup.yaml.editor/src/java/de/jcup/yaml/editor/YamlEditor.java
 * 
 * @author Albert Tregnaghi
 *
 */
public class YamlEditor extends TextEditor implements StatusMessageSupport, IResourceChangeListener {

	/** The COMMAND_ID of this editor as defined in plugin.xml */
	public static final String EDITOR_ID = "yamleditor.editors.YamlEditor";
	/** The COMMAND_ID of the editor context menu */
	public static final String EDITOR_CONTEXT_MENU_ID = EDITOR_ID + ".context";
	/** The COMMAND_ID of the editor ruler context menu */
	public static final String EDITOR_RULER_CONTEXT_MENU_ID = EDITOR_CONTEXT_MENU_ID + ".ruler";

	private YamlBracketsSupport bracketMatcher = new YamlBracketsSupport();
	private SourceViewerDecorationSupport additionalSourceViewerSupport;
	private YamlEditorContentOutlinePage outlinePage;
	private YamlScriptModelBuilder modelBuilder;
	private Object monitor = new Object();
	private boolean quickOutlineOpened;
	private int lastCaretPosition;

	public YamlEditor() {
		setSourceViewerConfiguration(new YamlSourceViewerConfiguration(this));
		this.modelBuilder = new YamlScriptModelBuilder();
	}

	public void resourceChanged(IResourceChangeEvent event) {
		if (isMarkerChangeForThisEditor(event)) {
			int severity = getSeverity();

			setTitleImageDependingOnSeverity(severity);
		}
	}

	/**
	 * Opens quick outline
	 */
	public void openQuickOutline() {
		synchronized (monitor) {
			if (quickOutlineOpened) {
				/*
				 * already opened - this is in future the anker point for
				 * ctrl+o+o...
				 */
				return;
			}
			quickOutlineOpened = true;
		}
		Shell shell = getEditorSite().getShell();
		YamlScriptModel model = buildModelWithoutValidation();
		YamlQuickOutlineDialog dialog = new YamlQuickOutlineDialog(this, shell, "Quick outline");
		dialog.setInput(model);

		dialog.open();
		synchronized (monitor) {
			quickOutlineOpened = false;
		}
	}

	private YamlScriptModel buildModelWithoutValidation() {
		String text = getDocumentText();

		YamlScriptModel model = modelBuilder.build(text);
		return model;
	}

	void setTitleImageDependingOnSeverity(int severity) {
		EclipseUtil.safeAsyncExec(new Runnable(){

			@Override
			public void run() {
				if (severity == IMarker.SEVERITY_ERROR) {
					setTitleImage(EclipseUtil.getImage("icons/yaml-editor-with-error.png", YamlEditorActivator.PLUGIN_ID));
				} else {
					setTitleImage(EclipseUtil.getImage("icons/yaml-editor.png", YamlEditorActivator.PLUGIN_ID));
				}
			}
			
		});
	}

	private int getSeverity() {
		IEditorInput editorInput = getEditorInput();
		if (editorInput == null) {
			return IMarker.SEVERITY_INFO;
		}
		try {
			final IResource resource = ResourceUtil.getResource(editorInput);
			if (resource == null) {
				return IMarker.SEVERITY_INFO;
			}
			int severity = resource.findMaxProblemSeverity(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
			return severity;
		} catch (CoreException e) {
			// Might be a project that is not open
		}
		return IMarker.SEVERITY_INFO;
	}

	private void addErrorMarkers(YamlScriptModel model, int severity) {
		if (model == null) {
			return;
		}
		IDocument document = getDocument();
		if (document == null) {
			return;
		}
		Collection<YamlError> errors = model.getErrors();
		for (YamlError error : errors) {
			int startPos = error.getStart();
			int line;
			try {
				line = document.getLineOfOffset(startPos);
			} catch (BadLocationException e) {
				EclipseUtil.logError("Cannot get line offset for " + startPos, e);
				line = 0;
			}
			YamlEditorUtil.addScriptError(this, line, error, severity);
		}

	}

	public void setErrorMessage(String message) {
		super.setStatusLineErrorMessage(message);
	}

	public YamlBracketsSupport getBracketMatcher() {
		return bracketMatcher;
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		Control adapter = getAdapter(Control.class);
		if (adapter instanceof StyledText) {
			StyledText text = (StyledText) adapter;
			text.addCaretListener(new YamlEditorCaretListener());
		}

		activateYamlEditorContext();

		installAdditionalSourceViewerSupport();

		StyledText styledText = getSourceViewer().getTextWidget();
		styledText.addKeyListener(new YamlBracketInsertionCompleter(this));

		/*
		 * register as resource change listener to provide marker change
		 * listening
		 */
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);

		setTitleImageInitial();
	}

	public YamlEditorContentOutlinePage getOutlinePage() {
		if (outlinePage == null) {
			outlinePage = new YamlEditorContentOutlinePage(this);
		}
		return outlinePage;
	}

	/**
	 * Installs an additional source viewer support which uses editor
	 * preferences instead of standard text preferences. If standard source
	 * viewer support would be set with editor preferences all standard
	 * preferences would be lost or had to be reimplmented. To avoid this
	 * another source viewer support is installed...
	 */
	private void installAdditionalSourceViewerSupport() {

		additionalSourceViewerSupport = new SourceViewerDecorationSupport(getSourceViewer(), getOverviewRuler(),
				getAnnotationAccess(), getSharedColors());
		additionalSourceViewerSupport.setCharacterPairMatcher(bracketMatcher);
		additionalSourceViewerSupport.setMatchingCharacterPainterPreferenceKeys(
				P_EDITOR_MATCHING_BRACKETS_ENABLED.getId(), P_EDITOR_MATCHING_BRACKETS_COLOR.getId(),
				P_EDITOR_HIGHLIGHT_BRACKET_AT_CARET_LOCATION.getId(), P_EDITOR_ENCLOSING_BRACKETS.getId());

		IPreferenceStore preferenceStoreForDecorationSupport = YamlEditorUtil.getPreferences().getPreferenceStore();
		additionalSourceViewerSupport.install(preferenceStoreForDecorationSupport);
	}

	@Override
	public void dispose() {
		super.dispose();

		if (additionalSourceViewerSupport != null) {
			additionalSourceViewerSupport.dispose();
		}
		if (bracketMatcher != null) {
			bracketMatcher.dispose();
			bracketMatcher = null;
		}
		if (marginRulePainter != null) {
			marginRulePainter.dispose();
			marginRulePainter = null;
		}
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	public String getBackGroundColorAsWeb() {
		ensureColorsFetched();
		return bgColor;
	}

	public String getForeGroundColorAsWeb() {
		ensureColorsFetched();
		return fgColor;
	}

	private void ensureColorsFetched() {
		if (bgColor == null || fgColor == null) {

			ISourceViewer sourceViewer = getSourceViewer();
			if (sourceViewer == null) {
				return;
			}
			StyledText textWidget = sourceViewer.getTextWidget();
			if (textWidget == null) {
				return;
			}

			/*
			 * TODO ATR, 03.02.2017: there should be an easier approach to get
			 * editors back and foreground, without syncexec
			 */
			EclipseUtil.getSafeDisplay().syncExec(new Runnable() {

				@Override
				public void run() {
					bgColor = ColorUtil.convertToHexColor(textWidget.getBackground());
					fgColor = ColorUtil.convertToHexColor(textWidget.getForeground());
				}
			});
		}

	}

	private String bgColor;
	private String fgColor;
	private boolean ignoreNextCaretMove;
	private ProjectionViewer viewer;
	private YamlMarginRulePainter marginRulePainter;
	private VerifyKeyListener verifier;

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (YamlEditor.class.equals(adapter)) {
			return (T) this;
		}
		if (IContentOutlinePage.class.equals(adapter)) {
			return (T) getOutlinePage();
		}
		if (ColorManager.class.equals(adapter)) {
			return (T) getColorManager();
		}
		if (IFile.class.equals(adapter)) {
			IEditorInput input = getEditorInput();
			if (input instanceof IFileEditorInput) {
				IFileEditorInput feditorInput = (IFileEditorInput) input;
				return (T) feditorInput.getFile();
			}
			return null;
		}
		if (ISourceViewer.class.equals(adapter)) {
			return (T) getSourceViewer();
		}
		if (StatusMessageSupport.class.equals(adapter)) {
			return (T) this;
		}
		if (ITreeContentProvider.class.equals(adapter) || YamlEditorTreeContentProvider.class.equals(adapter)) {
			if (outlinePage == null) {
				return null;
			}
			return (T) outlinePage.getContentProvider();
		}
		return super.getAdapter(adapter);
	}

	/**
	 * Jumps to the matching bracket.
	 */
	public void gotoMatchingBracket() {

		bracketMatcher.gotoMatchingBracket(this);
	}

	/**
	 * Get document text - safe way.
	 * 
	 * @return string, never <code>null</code>
	 */
	String getDocumentText() {
		IDocument doc = getDocument();
		if (doc == null) {
			return "";
		}
		return doc.get();
	}

	@Override
	protected void doSetInput(IEditorInput input) throws CoreException {
		setDocumentProvider(createDocumentProvider(input));
		super.doSetInput(input);

		rebuildOutline();
	}

	@Override
	protected void editorSaved() {
		super.editorSaved();
		rebuildOutline();
	}
 
	/**
	 * Does rebuild the outline - this is done asynchronous
	 */
	public void rebuildOutline() {
		String text = getDocumentText();
		
		
		
		EclipseUtil.safeAsyncExec(new Runnable() {

			@Override
			public void run() {
				YamlEditorUtil.removeScriptErrors(YamlEditor.this);

				YamlScriptModel model = modelBuilder.build(text);

				getOutlinePage().rebuild(model);

				if (model.hasErrors()) {
					addErrorMarkers(model, IMarker.SEVERITY_ERROR);
				}
				/* FIXME ATR: remove call when unnecessay because of listener!*/
//				try{
//					todoTasksSupport.updateTasksFor(text, getEditorInput());
//				}catch(CoreException e){
//					YamlEditorUtil.logError("Update task not possible", e);
//				}
			}
		});
	}

	/**
	 * Set initial title image dependent on current marker severity. This will
	 * mark error icon on startup time which is not handled by resource change
	 * handling, because having no change...
	 */
	private void setTitleImageInitial() {
		IResource resource = resolveResource();
		if (resource != null) {
			try {
				int maxSeverity = resource.findMaxProblemSeverity(null, true, IResource.DEPTH_INFINITE);
				setTitleImageDependingOnSeverity(maxSeverity);
			} catch (CoreException e) {
				/* ignore */
			}
		}
	}

	/**
	 * Resolves resource from current editor input.
	 * 
	 * @return file resource or <code>null</code>
	 */
	private IResource resolveResource() {
		IEditorInput input = getEditorInput();
		if (!(input instanceof IFileEditorInput)) {
			return null;
		}
		return ((IFileEditorInput) input).getFile();
	}

	private boolean isMarkerChangeForThisEditor(IResourceChangeEvent event) {
		IResource resource = ResourceUtil.getResource(getEditorInput());
		if (resource == null) {
			return false;
		}
		IPath path = resource.getFullPath();
		if (path == null) {
			return false;
		}
		IResourceDelta eventDelta = event.getDelta();
		if (eventDelta == null) {
			return false;
		}
		IResourceDelta delta = eventDelta.findMember(path);
		if (delta == null) {
			return false;
		}
		boolean isMarkerChangeForThisResource = (delta.getFlags() & IResourceDelta.MARKERS) != 0;
		return isMarkerChangeForThisResource;
	}

	private IDocumentProvider createDocumentProvider(IEditorInput input) {
		if (input instanceof FileStoreEditorInput) {
			return new YamlTextFileDocumentProvider();
		} else {
			return new YamlFileDocumentProvider();
		}
	}

	public IDocument getDocument() {
		return getDocumentProvider().getDocument(getEditorInput());
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		if (site == null) {
			return;
		}
		IWorkbenchPage page = site.getPage();
		if (page == null) {
			return;
		}

		// workaround to show action set for block mode etc.
		// https://www.eclipse.org/forums/index.php/t/366630/
		page.showActionSet("org.eclipse.ui.edit.text.actionSet.presentation");

	}

	@Override
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
		fAnnotationAccess = getAnnotationAccess();
		fOverviewRuler = createOverviewRuler(getSharedColors());

		viewer = new ProjectionViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles);

		MarginPaintSetup setup = new MarginPaintSetup();
		marginRulePainter = new YamlMarginRulePainter(viewer, setup);
		RGB lineColor = YamlEditorPreferences.getInstance()
				.getColor(YamlEditorPreferenceConstants.P_EDITOR_MARGIN_RULE_LINE_COLOR);
		setup.lineColor = getColorManager().getColor(lineColor);
		setup.lineStyle = SWT.LINE_DASH;

		verifier = new VerifyKeyListener() {

			public void verifyKey(VerifyEvent event) {
				/* we do not allow tab in any case ! */
				if (event.character == '\t') {

					event.doit = false;
					
					EclipseUtil.safeAsyncExec(new Runnable() {

						public void run() {

							ISelection selection = getSelectionProvider().getSelection();
							if (! (selection instanceof ITextSelection)) {
								return;
							}
							ITextSelection ts = (ITextSelection) selection;
							IDocumentProvider dp = getDocumentProvider();
							IDocument doc = dp.getDocument(getEditorInput());
							int offset = ts.getOffset();
							if (offset==-1){
								offset = lastCaretPosition;
							}
							try {
								String toInsert ="   ";
								int toInsertLength = toInsert.length();
								doc.replace(offset, ts.getLength(), toInsert);
								Control control = getAdapter(Control.class);
								if (control instanceof StyledText){
									StyledText t = (StyledText) control;
									t.setCaretOffset(offset+toInsertLength);
								}
							} catch (BadLocationException e) {
								EclipseUtil.logError("Cannot insert tab replacement at " + offset, e);
							}
						}
					});
				}
			}
		};

		viewer.getTextWidget().addVerifyKeyListener(verifier);

		CursorLinePainter cursorLinePainter = new CursorLinePainter(viewer);
		viewer.addPainter(cursorLinePainter);
		viewer.addPainter(marginRulePainter);

		ProjectionSupport support = new ProjectionSupport(viewer, getAnnotationAccess(), getSharedColors());
		support.install();
		getSourceViewerDecorationSupport(viewer);
		return viewer;
	}

	public void moveMargineLineIfNecessary() {
		int caretXPosition = viewer.getTextWidget().getCaret().getLocation().x;
		marginRulePainter.setX(caretXPosition);

	}

	@Override
	protected void initializeEditor() {
		super.initializeEditor();
		setEditorContextMenuId(EDITOR_CONTEXT_MENU_ID);
		setRulerContextMenuId(EDITOR_RULER_CONTEXT_MENU_ID);
	}

	private void activateYamlEditorContext() {
		IContextService contextService = getSite().getService(IContextService.class);
		if (contextService != null) {
			contextService.activateContext(EDITOR_CONTEXT_MENU_ID);
		}
	}

	private ColorManager getColorManager() {
		return YamlEditorActivator.getDefault().getColorManager();
	}

	public void handleColorSettingsChanged() {
		// done like in TextEditor for spelling
		ISourceViewer viewer = getSourceViewer();
		SourceViewerConfiguration configuration = getSourceViewerConfiguration();
		if (viewer instanceof ISourceViewerExtension2) {
			ISourceViewerExtension2 viewerExtension2 = (ISourceViewerExtension2) viewer;
			viewerExtension2.unconfigure();
			if (configuration instanceof YamlSourceViewerConfiguration) {
				YamlSourceViewerConfiguration gconf = (YamlSourceViewerConfiguration) configuration;
				gconf.updateTextScannerDefaultColorToken();
			}
			viewer.configure(configuration);
		}
	}

	/**
	 * Toggles comment of current selected lines
	 */
	public void toggleComment() {
		ISelection selection = getSelectionProvider().getSelection();
		if (!(selection instanceof TextSelection)) {
			return;
		}
		IDocumentProvider dp = getDocumentProvider();
		IDocument doc = dp.getDocument(getEditorInput());
		TextSelection ts = (TextSelection) selection;
		int startLine = ts.getStartLine();
		int endLine = ts.getEndLine();

		/* do comment /uncomment */
		for (int i = startLine; i <= endLine; i++) {
			IRegion info;
			try {
				info = doc.getLineInformation(i);
				int offset = info.getOffset();
				String line = doc.get(info.getOffset(), info.getLength());
				StringBuilder foundCode = new StringBuilder();
				StringBuilder whitespaces = new StringBuilder();
				for (int j = 0; j < line.length(); j++) {
					char ch = line.charAt(j);
					if (Character.isWhitespace(ch)) {
						if (foundCode.length() == 0) {
							whitespaces.append(ch);
						}
					} else {
						foundCode.append(ch);
					}
					if (foundCode.length() > 0) {
						break;
					}
				}
				int whitespaceOffsetAdd = whitespaces.length();
				if ("#".equals(foundCode.toString())) {
					/* comment before */
					doc.replace(offset + whitespaceOffsetAdd, 2, "");
				} else {
					/* not commented */
					doc.replace(offset, 0, "# ");
				}

			} catch (BadLocationException e) {
				/* ignore and continue */
				continue;
			}

		}
		/* reselect */
		int selectionStartOffset;
		try {
			selectionStartOffset = doc.getLineOffset(startLine);
			int endlineOffset = doc.getLineOffset(endLine);
			int endlineLength = doc.getLineLength(endLine);
			int endlineLastPartOffset = endlineOffset + endlineLength;
			int length = endlineLastPartOffset - selectionStartOffset;

			ISelection newSelection = new TextSelection(selectionStartOffset, length);
			getSelectionProvider().setSelection(newSelection);
		} catch (BadLocationException e) {
			/* ignore */
		}
	}

	public void openSelectedTreeItemInEditor(ISelection selection, boolean grabFocus) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			Object firstElement = ss.getFirstElement();
			if (firstElement instanceof Item) {
				Item item = (Item) firstElement;
				int offset = item.getOffset();
				int length = item.getLength();
				if (length == 0) {
					/* fall back */
					length = 1;
				}
				ignoreNextCaretMove = true;
				selectAndReveal(offset, length);
				if (grabFocus) {
					setFocus();
				}
			}
		}
	}

	public Item getItemAtCarretPosition() {
		return getItemAt(lastCaretPosition);
	}

	public Item getItemAt(int offset) {
		if (outlinePage == null) {
			return null;
		}
		YamlEditorTreeContentProvider contentProvider = outlinePage.getContentProvider();
		if (contentProvider == null) {
			return null;
		}
		Item item = contentProvider.tryToFindByOffset(offset);
		return item;
	}

	public void selectFunction(String text) {
		System.out.println("should select functin:" + text);

	}

	public YamlEditorPreferences getPreferences() {
		return YamlEditorPreferences.getInstance();
	}

	private class YamlEditorCaretListener implements CaretListener {

		@Override
		public void caretMoved(CaretEvent event) {
			if (event == null) {
				return;
			}
			lastCaretPosition = event.caretOffset;
			if (ignoreNextCaretMove) {
				ignoreNextCaretMove = false;
				return;
			}
			if (outlinePage == null) {
				return;
			}
			outlinePage.onEditorCaretMoved(event.caretOffset);
		}

	}
}
