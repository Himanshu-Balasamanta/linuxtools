/*******************************************************************************
 * Copyright (c) 2008, 2018 Red Hat, Inc.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Elliott Baron <ebaron@redhat.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.linuxtools.profiling.launch;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.cdt.launch.ui.CArgumentsTab;
import org.eclipse.cdt.launch.ui.CMainTab2;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;

public abstract class ProfileLaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup {

    @Override
    public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
        ArrayList<AbstractLaunchConfigurationTab> tabs = new ArrayList<>();
        tabs.add(new CMainTab2());
        tabs.add(new CArgumentsTab());

        tabs.addAll(Arrays.asList(getProfileTabs()));

        tabs.add(new EnvironmentTab());
        tabs.add(new SourceLookupTab());
        tabs.add(new CommonTab());

        setTabs(tabs.toArray(new AbstractLaunchConfigurationTab[tabs.size()]));
    }

    public abstract AbstractLaunchConfigurationTab[] getProfileTabs();

}