/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Jeff Briggs, Henry Hughes, Ryan Morse
 *******************************************************************************/

package org.eclipse.linuxtools.internal.systemtap.ui.ide.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.linuxtools.internal.systemtap.ui.ide.IDEPlugin;
import org.eclipse.linuxtools.internal.systemtap.ui.ide.Localization;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


public class CodeAssistPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	public CodeAssistPreferencePage() {
		super(GRID);
		setPreferenceStore(IDEPlugin.getDefault().getPreferenceStore());
		setDescription(Localization.getString("CodeAssistPreferencePage.CodeAssistPreferenceDescription"));
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	@Override
	public void createFieldEditors() {
		addField(
			new BooleanFieldEditor(
				IDEPreferenceConstants.P_USE_CODE_ASSIST,
				Localization.getString("CodeAssistPreferencePage.UseCodeAssist"),
				getFieldEditorParent()));
		addField(new RadioGroupFieldEditor(
				IDEPreferenceConstants.P_COMPLETION,
				Localization.getString("CodeAssistPreferencePage.HowCodeAdded"),
				1,
				new String[][] { 
						{Localization.getString("CodeAssistPreferencePage.Insert"), IDEPreferenceConstants.P_COMPLETION_INSERT },
						{Localization.getString("CodeAssistPreferencePage.Overwrite"), IDEPreferenceConstants.P_COMPLETION_OVERWRITE }},
				getFieldEditorParent()));
		addField(
				new IntegerFieldEditor(
						IDEPreferenceConstants.P_ACTIVATION_DELAY,
				Localization.getString("CodeAssistPreferencePage.ActivationDelay"),
				getFieldEditorParent()));
		addField(
				new StringFieldEditor(
						IDEPreferenceConstants.P_ACTIVATION_TRIGGER,
				Localization.getString("CodeAssistPreferencePage.ActivationTrigger"),
				getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {
	}

}
