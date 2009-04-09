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

package org.eclipse.linuxtools.systemtapgui.graphingapi.nonui.datasets;

import org.eclipse.linuxtools.systemtapgui.graphingapi.nonui.filters.IDataSetFilter;

public interface IFilteredDataSet extends IDataSet {
	public void addFilter(IDataSetFilter filter);
	public IDataSetFilter[] getFilters();
	public void clearFilters();
	public boolean removeFilter(IDataSetFilter filter);
}
