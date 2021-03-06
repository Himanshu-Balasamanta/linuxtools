/*******************************************************************************
 * Copyright (c) 2015, 2018 Red Hat Inc. and others.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Red Hat - Initial Contribution
 *******************************************************************************/
package org.eclipse.linuxtools.internal.docker.ui.wizards;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainerElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.linuxtools.docker.ui.Activator;
import org.eclipse.linuxtools.internal.docker.ui.SWTImagesFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class ImageBuildPage extends WizardPage {

	private final static String NAME = "ImageBuild.name"; //$NON-NLS-1$
	private final static String TITLE = "ImageBuild.title"; //$NON-NLS-1$
	private final static String DESC = "ImageBuild.desc"; //$NON-NLS-1$
	private final static String NAME_LABEL = "ImageBuildName.label"; //$NON-NLS-1$
	private final static String NAME_TOOLTIP = "ImageBuildName.toolTip"; //$NON-NLS-1$
	private final static String DIRECTORY_LABEL = "ImageBuildDirectory.label"; //$NON-NLS-1$
	private final static String DIRECTORY_TOOLTIP = "ImageBuildDirectory.toolTip"; //$NON-NLS-1$
	private final static String BROWSE_LABEL = "BrowseButton.label"; //$NON-NLS-1$
	private final static String EDIT_LABEL = "EditButton.label"; //$NON-NLS-1$
	private final static String NONEXISTENT_DIRECTORY = "ErrorNonexistentDirectory.msg"; //$NON-NLS-1$
	private final static String INVALID_DIRECTORY = "ErrorInvalidDirectory.msg"; //$NON-NLS-1$
	private final static String UNREADABLE_DIRECTORY = "ErrorUnreadableDirectory.msg"; //$NON-NLS-1$
	private final static String INVALID_ID = "ErrorInvalidImageId.msg"; //$NON-NLS-1$
	private final static String NO_DOCKER_FILE = "ErrorNoDockerFile.msg"; //$NON-NLS-1$
	private final static String IMAGE_NAME_EMPTY = "ImageBuild.name.empty"; //$NON-NLS-1$

	private Text nameText;
	private Text directoryText;
	private Button editButton;
	private Set<IEditorPart> editors = new HashSet<>();

	/*
	 * For now just hold the last directory path used here. If this page gets
	 * more complicated (and ends up requiring a model) we should consider using
	 * a launch configuration to store all other previous values.
	 */
	private static String lastDirectoryPath;

	public ImageBuildPage() {
		super(WizardMessages.getString(NAME));
		setDescription(WizardMessages.getString(DESC));
		setTitle(WizardMessages.getString(TITLE));
		setImageDescriptor(SWTImagesFactory.DESC_WIZARD);
	}

	public String getImageName() {
		return nameText.getText();
	}

	public String getDirectory() {
		return directoryText.getText();
	}

	@Override
	public void dispose() {
		super.dispose();
		for (IEditorPart p : editors) {
			p.getEditorSite().getPage().closeEditor(p, true);

		}
		editors.clear();
	}

	private ModifyListener Listener = e -> validate();

	private void validate() {
		boolean complete = true;
		boolean error = false;

		setMessage(null);
		String name = nameText.getText();

		if (name.length() > 0 && name.charAt(name.length() - 1) == ':') {
			setErrorMessage(WizardMessages.getString(INVALID_ID));
			error = true;
		} else {
			if (name.contains(":")) { //$NON-NLS-1$
				if (name.substring(name.indexOf(':') + 1).contains(":")) { //$NON-NLS-1$
					setErrorMessage(WizardMessages.getString(INVALID_ID));
					error = true;
				}
			} else if (name.isEmpty()) {
				setMessage(WizardMessages.getString(IMAGE_NAME_EMPTY), WARNING);
			}
		}

		if (!error) {
			String dir = directoryText.getText();
			if (dir.length() == 0) {
				editButton.setEnabled(false);
				complete = false;
			} else {
				IFileStore fileStore = EFS.getLocalFileSystem().getStore(
						new Path(dir));
				IFileInfo info = fileStore.fetchInfo();
				if (!info.exists()) {
					error = true;
					setErrorMessage(WizardMessages
							.getString(NONEXISTENT_DIRECTORY));
				} else if (!info.isDirectory()) {
					error = true;
					setErrorMessage(WizardMessages.getString(INVALID_DIRECTORY));
				} else if (!Files.isReadable(Paths.get(dir))) {
					error = true;
					setErrorMessage(
							WizardMessages.getString(UNREADABLE_DIRECTORY));
				} else {
					editButton.setEnabled(true);
					IFileStore dockerStore = fileStore.getChild("Dockerfile"); //$NON-NLS-1$
					if (!dockerStore.fetchInfo().exists()) {
						complete = false;
						setMessage(WizardMessages.getString(NO_DOCKER_FILE),
								IMessageProvider.INFORMATION);
					} else {
						lastDirectoryPath = dir;
					}

				}
			}
		}

		if (!error) {
			setErrorMessage(null);
		} else {
			editButton.setEnabled(false);
		}

		setPageComplete(complete && !error);
	}

	@Override
	public void createControl(Composite parent) {
		final Composite container = new Composite(parent, SWT.NULL);
		FormLayout layout = new FormLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		container.setLayout(layout);

		Label label = new Label(container, SWT.NULL);

		Label nameLabel = new Label(container, SWT.NULL);
		nameLabel.setText(WizardMessages.getString(NAME_LABEL));

		nameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		nameText.addModifyListener(Listener);
		nameText.setToolTipText(WizardMessages.getString(NAME_TOOLTIP));

		Label dirLabel = new Label(container, SWT.NULL);
		dirLabel.setText(WizardMessages.getString(DIRECTORY_LABEL));

		directoryText = new Text(container, SWT.BORDER | SWT.SINGLE);
		directoryText.addModifyListener(Listener);
		directoryText.setToolTipText(WizardMessages
				.getString(DIRECTORY_TOOLTIP));

		Button browse = new Button(container, SWT.NULL);
		browse.setText(WizardMessages.getString(BROWSE_LABEL));
		browse.addSelectionListener(
				SelectionListener.widgetSelectedAdapter(e -> {
					DirectoryDialog d = new DirectoryDialog(
							container.getShell());
					String k = d.open();
					if (k != null)
						directoryText.setText(k);
				}));

		editButton = new Button(container, SWT.NULL);
		editButton.setText(WizardMessages.getString(EDIT_LABEL));
		editButton.setEnabled(false);
		editButton.addSelectionListener(
				SelectionListener.widgetSelectedAdapter(e -> {
					String dir = directoryText.getText();
					IFileStore fileStore = EFS.getLocalFileSystem()
							.getStore(new Path(dir).append("Dockerfile")); //$NON-NLS-1$
					java.nio.file.Path filePath = Paths.get(dir, "Dockerfile"); //$NON-NLS-1$
					if (!Files.exists(filePath)) {
				try {
							Files.createFile(filePath);
						} catch (IOException e1) {
							// File won't exist, and directory should be
							// writable
				}
					}
					IWorkbenchPage page = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage();
					try {
						IEditorPart dockerFileEditor = IDE
								.openEditorOnFileStore(page, fileStore);
						IWorkbenchPartSite site = page.getActivePart()
								.getSite();
						EModelService s = site.getService(EModelService.class);
						MPartSashContainerElement p = site
								.getService(MPart.class);
						s.detach(p, 100, 100, 500, 375);
						dockerFileEditor.getEditorSite().getShell()
								.setText(WizardMessages
										.getString("ImageBuild.editor.name")); //$NON-NLS-1$
						editors.add(dockerFileEditor);
					} catch (PartInitException e1) {
						Activator.log(e1);
					}
					validate();
				}));

		Point p1 = label.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Point p2 = directoryText.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Point p3 = browse.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		int centering = (p2.y - p1.y + 1) / 2;
		int centering2 = (p3.y - p2.y + 1) / 2;

		FormData f = new FormData();
		f.top = new FormAttachment(0);
		label.setLayoutData(f);

		f = new FormData();
		f.top = new FormAttachment(label, 11 + centering + centering2);
		f.left = new FormAttachment(0, 0);
		nameLabel.setLayoutData(f);

		f = new FormData();
		f.top = new FormAttachment(label, 11 + centering2);
		f.left = new FormAttachment(nameLabel, 5);
		f.right = new FormAttachment(browse, -10);
		nameText.setLayoutData(f);

		f = new FormData();
		f.top = new FormAttachment(nameLabel, 11 + centering + centering2);
		f.left = new FormAttachment(0, 0);
		dirLabel.setLayoutData(f);

		f = new FormData();
		f.top = new FormAttachment(nameLabel, 11);
		f.right = new FormAttachment(100);
		editButton.setLayoutData(f);

		f = new FormData();
		f.top = new FormAttachment(nameLabel, 11);
		f.right = new FormAttachment(editButton, -10);
		browse.setLayoutData(f);

		f = new FormData();
		f.top = new FormAttachment(nameLabel, 11 + centering2);
		f.left = new FormAttachment(nameLabel, 5);
		f.right = new FormAttachment(browse, -10);
		directoryText.setLayoutData(f);

		if (lastDirectoryPath != null) {
			directoryText.setText(lastDirectoryPath);
		}

		setControl(container);
		setPageComplete(false);
	}
}
