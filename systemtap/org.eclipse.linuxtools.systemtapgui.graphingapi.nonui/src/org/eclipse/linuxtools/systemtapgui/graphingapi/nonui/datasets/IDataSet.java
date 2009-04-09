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

import java.io.File;


public interface IDataSet {
	public String[] getTitles();
	public String getID();
	public boolean writeToFile(File file);
	public boolean readFromFile(File file);

	public int getRowCount();
	public int getColCount();
	
	public Object[] getRow(int row);
	public Object[] getColumn(int col);
	public Object[] getColumn(int col, int start, int end);

	public void setData(IDataEntry entry);
	
	public boolean remove(IDataEntry entry);
	
	public static final int COL_ROW_NUM = -1;
}
