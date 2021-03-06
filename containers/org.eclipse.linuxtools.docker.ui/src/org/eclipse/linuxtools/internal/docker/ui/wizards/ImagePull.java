/*******************************************************************************
 * Copyright (c) 2015, 2018 Red Hat.
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

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.linuxtools.docker.core.DockerException;
import org.eclipse.linuxtools.docker.core.IDockerConnection;
import org.eclipse.linuxtools.docker.core.IDockerImage;
import org.eclipse.linuxtools.docker.core.IRegistry;
import org.eclipse.linuxtools.internal.docker.core.DockerImage;
import org.eclipse.linuxtools.internal.docker.ui.wizards.ImageNameValidator.ImageNameStatus;

/**
 * 
 */
public class ImagePull extends Wizard {

	private final ImagePullPage imagePullPage;

	/**
	 * Constructor when an {@link IDockerConnection} has been selected to run an
	 * {@link IDockerImage}.
	 * 
	 * @param connection
	 *            the {@link IDockerConnection} pointing to a specific Docker
	 *            daemon/host.
	 * @throws DockerException
	 */
	public ImagePull(final IDockerConnection connection) {
		super();
		setWindowTitle(WizardMessages.getString("ImagePull.title")); //$NON-NLS-1$
		this.imagePullPage = new ImagePullPage(connection);
	}

	@Override
	public void addPages() {
		addPage(imagePullPage);
	}

	@Override
	public boolean canFinish() {
		return this.imagePullPage.isPageComplete();
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	public String getSelectedImageName() {
		// append the ':latest' tag on the image name if no tag was specified
		final String selectedImageName = this.imagePullPage
				.getSelectedImageName();
		if (ImageNameValidator
				.getStatus(selectedImageName) == ImageNameStatus.TAG_MISSING) {
			return selectedImageName + ':' + DockerImage.LATEST_TAG; // $NON-NLS-1$
		}
		return selectedImageName;
	}

	public IRegistry getSelectedRegistryAccount() {
		return this.imagePullPage.getSelectedRegistryAccount();
	}

}
