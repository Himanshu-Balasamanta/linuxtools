/**********************************************************************
 * Copyright (c) 2012 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   Bernd Hufmann - Initial API and implementation
 **********************************************************************/
package org.eclipse.linuxtools.lttng.ui.views.control.model;

/**
 * <b><u>ITraceInfo</u></b>
 * <p>
 * Interface for retrieve trace comon information.
 * </p>
 */

public interface ITraceInfo {
    /**
     * @return the name of the information element.
     */
    public String getName();
    
    /**
     * Sets the name of the information element.
     * @param name
     */
    public void setName(String name);
    
    /** 
     * @return a formated (readable) String with content.
     */
    public String formatString();
}
