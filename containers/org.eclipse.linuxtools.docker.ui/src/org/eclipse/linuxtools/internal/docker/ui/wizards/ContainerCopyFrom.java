/*******************************************************************************
 * Copyright (c) 2016, 2018 Red Hat.
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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.linuxtools.docker.core.EnumDockerStatus;
import org.eclipse.linuxtools.docker.core.IDockerConnection;
import org.eclipse.linuxtools.docker.core.IDockerContainer;
import org.eclipse.linuxtools.docker.core.IDockerContainerInfo;
import org.eclipse.linuxtools.docker.ui.Activator;
import org.eclipse.linuxtools.internal.docker.core.ContainerFileProxy;
import org.eclipse.linuxtools.internal.docker.core.DockerConnection;
import org.eclipse.linuxtools.internal.docker.ui.ContainerFileSystemProvider;
import org.eclipse.linuxtools.internal.docker.ui.MinimizedFileSystemElement;
import org.eclipse.linuxtools.internal.docker.ui.PopulateContainerFilesOperation;

public class ContainerCopyFrom extends Wizard {

	private DockerConnection connection;
	private IDockerContainer container;
	private String target;
	private List<ContainerFileProxy> sources;
	private ContainerCopyFromPage mainPage;

	public ContainerCopyFrom(IDockerConnection connection,
			IDockerContainer container) {
		this.connection = (DockerConnection) connection;
		this.container = container;
	}

	public String getTarget() {
		return target;
	}

	public List<ContainerFileProxy> getSources() {
		return sources;
	}

	@Override
	public void addPages() {
		ProgressMonitorDialog pd = new ProgressMonitorDialog(
				Activator.getActiveWorkbenchShell());
		ContainerFileSystemProvider provider = new ContainerFileSystemProvider(
				connection, container.id());
		PopulateContainerFilesOperation sfo = new PopulateContainerFilesOperation(
				new ContainerFileProxy("", "", true), //$NON-NLS-1$ //$NON-NLS-2$
				null, provider);
		try {
			pd.run(true, true, sfo);
		} catch (InvocationTargetException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		boolean isRunning = EnumDockerStatus.fromStatusMessage(
				container.status()) == EnumDockerStatus.RUNNING;

		// we only want to browse a Container's file system if
		// it is running and it has specified to use a tty
		boolean usingTTY = false;
		if (isRunning) {
			IDockerContainerInfo info = connection
					.getContainerInfo(container.id());
			if (info != null) {
				usingTTY = info.config().tty();
			}
		}

		mainPage = new ContainerCopyFromPage(sfo.getResult(), provider,
				container.name(), isRunning && usingTTY);
		addPage(mainPage);
	}

	@Override
	public boolean canFinish() {
		return mainPage.isPageComplete();
	}

	@Override
	public boolean performFinish() {
		target = mainPage.getTarget();
		@SuppressWarnings("rawtypes")
		Iterator iterator = mainPage.getValueIterator();
		ArrayList<ContainerFileProxy> copyList = new ArrayList<>();
		while (iterator.hasNext()) {
			MinimizedFileSystemElement e = (MinimizedFileSystemElement) iterator
					.next();
			ContainerFileProxy p = (ContainerFileProxy) e.getFileSystemObject();
			copyList.add(p);
		}
		sources = copyList;
		return true;
	}
}
