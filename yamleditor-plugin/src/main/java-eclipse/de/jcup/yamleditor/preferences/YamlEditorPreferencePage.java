package de.jcup.yamleditor.preferences;
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

import static de.jcup.yamleditor.YamlEditorUtil.*;
import static de.jcup.yamleditor.preferences.YamlEditorPreferenceConstants.*;

import java.util.ArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.yamleditor.script.formatter.SnakeYamlSourceFormatterConfig;
import de.jcup.yamleditor.script.formatter.YamlSourceFormatterConfig;

/**
 * Parts are inspired by <a href=
 * "https://github.com/eclipse/eclipse.jdt.ui/blob/master/org.eclipse.jdt.ui/ui/org/eclipse/jdt/internal/ui/preferences/JavaEditorAppearanceConfigurationBlock.java">org.eclipse.jdt.internal.ui.preferences.JavaEditorAppearanceConfigurationBlock
 * </a>
 * 
 * @author Albert Tregnaghi
 *
 */
public class YamlEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	protected static final int INDENT = 20;

	protected static void indent(Control control) {
		((GridData) control.getLayoutData()).horizontalIndent += INDENT;
	}

	private Button bracketHighlightingCheckbox;
	private Button enclosingBracketsRadioButton;
	private Button matchingBracketAndCaretLocationRadioButton;
	private Button matchingBracketRadioButton;

	private ColorFieldEditor matchingBracketsColor;
	private BooleanFieldEditor linkEditorWithOutline;

	private ArrayList<MasterButtonSlaveSelectionListener> masterSlaveListeners = new ArrayList<>();

	private boolean enclosingBrackets;
	private boolean highlightBracketAtCaretLocation;
	private boolean matchingBrackets;
	private BooleanFieldEditor autoCreateEndBrackets;
	private BooleanFieldEditor codeAssistWithYamlKeywords;
	private BooleanFieldEditor codeAssistWithSimpleWords;
	private ColorFieldEditor marginRuleColor;
	private BooleanFieldEditor codeFoldingEnabledOnNewEditors;

	public YamlEditorPreferencePage() {
		super(GRID);
		setPreferenceStore(getPreferences().getPreferenceStore());
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	public void performDefaults() {
		super.performDefaults();
		reloadBracketHighlightingPreferenceDefaults();
	}

	@Override
	public boolean performOk() {
		boolean ok = super.performOk();
		if (ok) {
			setBoolean(P_EDITOR_MATCHING_BRACKETS_ENABLED, matchingBrackets);
			setBoolean(P_EDITOR_HIGHLIGHT_BRACKET_AT_CARET_LOCATION, highlightBracketAtCaretLocation);
			setBoolean(P_EDITOR_ENCLOSING_BRACKETS, enclosingBrackets);
		}
		return ok;
	}

	protected void createDependency(Button master, Control slave) {
		Assert.isNotNull(slave);
		indent(slave);
		MasterButtonSlaveSelectionListener listener = new MasterButtonSlaveSelectionListener(master, slave);
		master.addSelectionListener(listener);
		this.masterSlaveListeners.add(listener);
	}

	@Override
	protected void createFieldEditors() {
		Composite appearanceComposite = new Composite(getFieldEditorParent(), SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		appearanceComposite.setLayout(layout);

		/* --------------------- */
		/* -- Other group -- */
		/* --------------------- */
		Composite otherComposite = new Composite(appearanceComposite, SWT.NONE);
		GridLayout otherLayout = new GridLayout();
		otherLayout.marginWidth = 0;
		otherLayout.marginHeight = 0;
		otherComposite.setLayout(otherLayout);

		/* code folding */
		codeFoldingEnabledOnNewEditors = new BooleanFieldEditor(P_CODE_FOLDING_ENABLED.getId(),
				"New opened editors have code folding enabled", otherComposite);
		codeFoldingEnabledOnNewEditors.getDescriptionControl(otherComposite).setToolTipText(
				"When enabled code foldings is active per default. Can be changed in ruler context menu for each editor instance");
		addField(codeFoldingEnabledOnNewEditors);

		/* linking with outline */
		linkEditorWithOutline = new BooleanFieldEditor(P_LINK_OUTLINE_WITH_EDITOR.getId(),
				"New opened editors are linked with outline", otherComposite);
		linkEditorWithOutline.getDescriptionControl(otherComposite)
				.setToolTipText("Via this setting the default behaviour for new opened outlines is set");
		addField(linkEditorWithOutline);

		/* ---------------------------------------- */
		/* - source formatter + space replacement - */
		/* ---------------------------------------- */
		IntegerFieldEditor replaceTabsBySpacesEditor = new IntegerFieldEditor(P_SPACES_TO_REPLACE_TAB.getId(),
				"Spaces used for tab replacement", otherComposite);
		replaceTabsBySpacesEditor.setValidRange(1, SnakeYamlSourceFormatterConfig.SNAKE_MAX_INDENT);
		addField(replaceTabsBySpacesEditor);
		replaceTabsBySpacesEditor.getLabelControl(otherComposite).setToolTipText(
				"Yaml editor replaces all tab key presses with spaces,because illegal for YAML format.\n"
				+ "This defines the amout of spaces to use.\n\nAlso used by source formatter on indent calculation.");
		
		IntegerFieldEditor lineLengthEditor = new IntegerFieldEditor(P_SOURCE_FORMAT_LINE_LENGTH.getId(),
                "Source formatter max line length", otherComposite);
		lineLengthEditor.setValidRange(40, SnakeYamlSourceFormatterConfig.SNAKE_MAX_LINELENGTH);
        addField(lineLengthEditor);
        lineLengthEditor.getLabelControl(otherComposite).setToolTipText(
                "Line length used by source formatter");
		

        /* ---------------- */
        /* - Margin ruler - */
        /* ---------------- */
		marginRuleColor = new ColorFieldEditor(P_EDITOR_MARGIN_RULE_LINE_COLOR.getId(), "Margin rule line color",
				otherComposite);
		addField(marginRuleColor);

		Label spacer = new Label(appearanceComposite, SWT.LEFT);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;
		gd.heightHint = convertHeightInCharsToPixels(1) / 2;
		spacer.setLayoutData(gd);

		/* BRACKETS */
		/*
		 * Why so ugly implemented and not using field editors ? Because
		 * SourceViewerDecorationSupport needs 3 different preference keys to do its
		 * job, so this preference doing must be same as on Java editor preferences.
		 */
		GridData bracketsGroupLayoutData = new GridData();
		bracketsGroupLayoutData.horizontalSpan = 2;
		bracketsGroupLayoutData.widthHint = 400;

		Group bracketsGroup = new Group(appearanceComposite, SWT.NONE);
		bracketsGroup.setText("Brackets");
		bracketsGroup.setLayout(new GridLayout());
		bracketsGroup.setLayoutData(bracketsGroupLayoutData);

		autoCreateEndBrackets = new BooleanFieldEditor(P_EDITOR_AUTO_CREATE_END_BRACKETS.getId(),
				"Auto create ending brackets", bracketsGroup);
		addField(autoCreateEndBrackets);

		String label = "Bracket highlighting";

		bracketHighlightingCheckbox = addButton(bracketsGroup, SWT.CHECK, label, 0, new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				matchingBrackets = bracketHighlightingCheckbox.getSelection();
			}
		});

		Composite radioComposite = new Composite(bracketsGroup, SWT.NONE);
		GridLayout radioLayout = new GridLayout();
		radioLayout.marginWidth = 0;
		radioLayout.marginHeight = 0;
		radioComposite.setLayout(radioLayout);

		label = "highlight matching bracket";
		matchingBracketRadioButton = addButton(radioComposite, SWT.RADIO, label, 0, new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (matchingBracketRadioButton.getSelection()) {
					highlightBracketAtCaretLocation = false;
				}
			}
		});
		createDependency(bracketHighlightingCheckbox, matchingBracketRadioButton);

		label = "highlight matching bracket and caret location";
		matchingBracketAndCaretLocationRadioButton = addButton(radioComposite, SWT.RADIO, label, 0,
				new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (matchingBracketAndCaretLocationRadioButton.getSelection()) {
							highlightBracketAtCaretLocation = true;
						}
					}
				});
		createDependency(bracketHighlightingCheckbox, matchingBracketAndCaretLocationRadioButton);

		label = "highlight enclosing brackets";
		enclosingBracketsRadioButton = addButton(radioComposite, SWT.RADIO, label, 0, new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean selection = enclosingBracketsRadioButton.getSelection();
				enclosingBrackets = selection;
				if (selection) {
					highlightBracketAtCaretLocation = true;
				}
			}
		});
		createDependency(bracketHighlightingCheckbox, enclosingBracketsRadioButton);

		matchingBracketsColor = new ColorFieldEditor(P_EDITOR_MATCHING_BRACKETS_COLOR.getId(),
				"Matching brackets color", radioComposite);
		addField(matchingBracketsColor);
		createDependency(bracketHighlightingCheckbox, matchingBracketsColor.getLabelControl(radioComposite));
		createDependency(bracketHighlightingCheckbox, matchingBracketsColor.getColorSelector().getButton());

		/* --------------------- */
		/* -- Code assistance -- */
		/* --------------------- */

		GridData codeAssistGroupLayoutData = new GridData();
		codeAssistGroupLayoutData.horizontalSpan = 2;
		codeAssistGroupLayoutData.widthHint = 400;

		Group codeAssistGroup = new Group(appearanceComposite, SWT.NONE);
		codeAssistGroup.setText("Code assistence");
		codeAssistGroup.setLayout(new GridLayout());
		codeAssistGroup.setLayoutData(codeAssistGroupLayoutData);

		codeAssistWithYamlKeywords = new BooleanFieldEditor(P_CODE_ASSIST_ADD_KEYWORDS.getId(),
				"Yaml keywords and external commands", codeAssistGroup);
		codeAssistWithYamlKeywords.getDescriptionControl(codeAssistGroup).setToolTipText(
				"When enabled the standard keywords supported by yaml editor are always automatically available as code proposals");
		addField(codeAssistWithYamlKeywords);

		codeAssistWithSimpleWords = new BooleanFieldEditor(P_CODE_ASSIST_ADD_SIMPLEWORDS.getId(), "Existing words",
				codeAssistGroup);
		codeAssistWithSimpleWords.getDescriptionControl(codeAssistGroup).setToolTipText(
				"When enabled the current source will be scanned for words. The existing words will be available as code proposals");
		addField(codeAssistWithSimpleWords);

	}

	@Override
	protected void initialize() {
		initializeBracketHighlightingPreferences();
		super.initialize();
		updateSlaveComponents();
	}

	private Button addButton(Composite parent, int style, String label, int indentation, SelectionListener listener) {
		Button button = new Button(parent, style);
		button.setText(label);

		GridData gd = new GridData(32);
		gd.horizontalIndent = indentation;
		gd.horizontalSpan = 2;
		button.setLayoutData(gd);
		button.addSelectionListener(listener);

		return button;
	}

	private void setBoolean(YamlEditorPreferenceConstants id, boolean value) {
		getPreferences().setBooleanPreference(id, value);
	}

	private boolean getBoolean(YamlEditorPreferenceConstants id) {
		return getPreferences().getBooleanPreference(id);
	}

	private boolean getDefaultBoolean(YamlEditorPreferenceConstants id) {
		return getPreferences().getDefaultBooleanPreference(id);
	}

	private void initializeBracketHighlightingPreferences() {
		matchingBrackets = getBoolean(P_EDITOR_MATCHING_BRACKETS_ENABLED);
		highlightBracketAtCaretLocation = getBoolean(P_EDITOR_HIGHLIGHT_BRACKET_AT_CARET_LOCATION);
		enclosingBrackets = getBoolean(P_EDITOR_ENCLOSING_BRACKETS);

		updateBracketUI();
	}

	private void reloadBracketHighlightingPreferenceDefaults() {
		matchingBrackets = getDefaultBoolean(P_EDITOR_MATCHING_BRACKETS_ENABLED);
		highlightBracketAtCaretLocation = getDefaultBoolean(P_EDITOR_HIGHLIGHT_BRACKET_AT_CARET_LOCATION);
		enclosingBrackets = getDefaultBoolean(P_EDITOR_ENCLOSING_BRACKETS);

		updateBracketUI();
	}

	private void updateBracketUI() {
		this.bracketHighlightingCheckbox.setSelection(matchingBrackets);

		this.enclosingBracketsRadioButton.setSelection(enclosingBrackets);
		if (!(enclosingBrackets)) {
			this.matchingBracketRadioButton.setSelection(!(highlightBracketAtCaretLocation));
			this.matchingBracketAndCaretLocationRadioButton.setSelection(highlightBracketAtCaretLocation);
		}
		updateSlaveComponents();
	}

	private void updateSlaveComponents() {
		for (MasterButtonSlaveSelectionListener listener : masterSlaveListeners) {
			listener.updateSlaveComponent();
		}
	}

	private class MasterButtonSlaveSelectionListener implements SelectionListener {
		private Button master;
		private Control slave;

		public MasterButtonSlaveSelectionListener(Button master, Control slave) {
			this.master = master;
			this.slave = slave;
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {

		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			updateSlaveComponent();
		}

		private void updateSlaveComponent() {
			boolean state = master.getSelection();
			slave.setEnabled(state);
		}

	}

}
