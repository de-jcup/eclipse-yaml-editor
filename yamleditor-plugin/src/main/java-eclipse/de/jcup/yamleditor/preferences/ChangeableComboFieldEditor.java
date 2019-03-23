/*
 * Copyright 2019 Albert Tregnaghi
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
package de.jcup.yamleditor.preferences;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * A field editor for a combo box that allows the drop-down selection of one of
 * a list of items.
 * 
 * adopted from EGradle
 */
public class ChangeableComboFieldEditor extends FieldEditor {

	/**
	 * The <code>Combo</code> widget.
	 */
	private Combo fCombo;

	/**
	 * The value (not the name) of the currently selected item in the Combo
	 * widget.
	 */
	private String fValue;

	/**
	 * The names (labels) and underlying values to populate the combo widget.
	 * These should be arranged as: { {name1, value1}, {name2, value2}, ...}
	 */
	private String[][] fEntryNamesAndValues;

	/**
	 * Create the combo box field editor.
	 *
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param entryNamesAndValues
	 *            the names (labels) and underlying values to populate the combo
	 *            widget. These should be arranged as: { {name1, value1},
	 *            {name2, value2}, ...}
	 * @param parent
	 *            the parent composite
	 */
	public ChangeableComboFieldEditor(String name, String labelText, String[][] entryNamesAndValues, Composite parent) {
		init(name, labelText);
		Assert.isTrue(checkArray(entryNamesAndValues));
		fEntryNamesAndValues = entryNamesAndValues;
		createControl(parent);
	}

	/**
	 * Checks whether given <code>String[][]</code> is of "type"
	 * <code>String[][2]</code>.
	 *
	 * @return <code>true</code> if it is ok, and <code>false</code> otherwise
	 */
	private boolean checkArray(String[][] table) {
		if (table == null) {
			return false;
		}
		for (int i = 0; i < table.length; i++) {
			String[] array = table[i];
			if (array == null || array.length != 2) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {
		if (numColumns > 1) {
			Control control = getLabelControl();
			int left = numColumns;
			if (control != null) {
				((GridData) control.getLayoutData()).horizontalSpan = 1;
				left = left - 1;
			}
			((GridData) fCombo.getLayoutData()).horizontalSpan = left;
		} else {
			Control control = getLabelControl();
			if (control != null) {
				((GridData) control.getLayoutData()).horizontalSpan = 1;
			}
			((GridData) fCombo.getLayoutData()).horizontalSpan = 1;
		}
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		int comboC = 1;
		if (numColumns > 1) {
			comboC = numColumns - 1;
		}
		Control control = getLabelControl(parent);
		GridData gd = new GridData();
		gd.horizontalSpan = 1;
		control.setLayoutData(gd);
		control = getComboBoxControl(parent);
		gd = new GridData();
		gd.horizontalSpan = comboC;
		gd.horizontalAlignment = GridData.FILL;
		control.setLayoutData(gd);
		control.setFont(parent.getFont());
	}

	@Override
	protected void doLoad() {
		updateComboForValue(getPreferenceStore().getString(getPreferenceName()));
	}

	@Override
	protected void doLoadDefault() {
		updateComboForValue(getPreferenceStore().getDefaultString(getPreferenceName()));
	}

	@Override
	protected void doStore() {
		if (fValue == null) {
			getPreferenceStore().setToDefault(getPreferenceName());
			return;
		}
		getPreferenceStore().setValue(getPreferenceName(), fValue);
	}

	@Override
	public int getNumberOfControls() {
		return 2;
	}

	public Combo getComboBoxControl(Composite parent) {
		if (fCombo == null) {
			fCombo = new Combo(parent, SWT.READ_ONLY);
			fCombo.setFont(parent.getFont());
			for (int i = 0; i < fEntryNamesAndValues.length; i++) {
				fCombo.add(fEntryNamesAndValues[i][0], i);
			}

			fCombo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent evt) {
					String oldValue = fValue;
					String name = fCombo.getText();
					fValue = getValueForName(name);
					setPresentsDefaultValue(false);
					fireValueChanged(VALUE, oldValue, fValue);
				}
			});
		}
		return fCombo;
	}

	/*
	 * Given the name (label) of an entry, return the corresponding value.
	 */
	private String getValueForName(String name) {
		for (int i = 0; i < fEntryNamesAndValues.length; i++) {
			String[] entry = fEntryNamesAndValues[i];
			if (name.equals(entry[0])) {
				return entry[1];
			}
		}
		return fEntryNamesAndValues[0][0];
	}

	/*
	 * Set the name in the combo widget to match the specified value.
	 */
	private void updateComboForValue(String value) {
		fValue = value;
		for (int i = 0; i < fEntryNamesAndValues.length; i++) {
			if (value.equals(fEntryNamesAndValues[i][1])) {
				fCombo.setText(fEntryNamesAndValues[i][0]);
				return;
			}
		}
		if (fEntryNamesAndValues.length > 0) {
			fValue = fEntryNamesAndValues[0][1];
			fCombo.setText(fEntryNamesAndValues[0][0]);
		}
	}

	@Override
	public void setEnabled(boolean enabled, Composite parent) {
		super.setEnabled(enabled, parent);
		getComboBoxControl(parent).setEnabled(enabled);
	}

	/**
	 * Returns the value of the current selected entry (not the label)
	 * 
	 * @return value
	 */
	public String getStringValue() {
		return fValue;
	}

	/**
	 * Returns label of current selected entry
	 * 
	 * @return value
	 */
	public String getLabel() {
		return getValueForName(fValue);
	}

	/**
	 * Set the value (not the label) of wanted new current value
	 * 
	 * @param value
	 */
	public void setStringValue(String value) {
		updateComboForValue(value);
	}
}
