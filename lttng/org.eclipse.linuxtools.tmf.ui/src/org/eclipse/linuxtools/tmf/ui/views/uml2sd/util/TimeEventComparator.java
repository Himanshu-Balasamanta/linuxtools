/**********************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * Copyright (c) 2011, 2012 Ericsson.
 *
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 * Bernd Hufmann - Updated for TMF
 **********************************************************************/
package org.eclipse.linuxtools.tmf.ui.views.uml2sd.util;

import java.io.Serializable;
import java.util.Comparator;

import org.eclipse.linuxtools.tmf.ui.views.uml2sd.core.SDTimeEvent;

/**
 * Time event comparator
 *
 * @version 1.0
 * @author sveyrier
 */
public class TimeEventComparator implements Comparator<SDTimeEvent>, Serializable {

    // ------------------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------------------

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 5885497718872575669L;

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /*
     * (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(SDTimeEvent arg0, SDTimeEvent arg1) {
        SDTimeEvent t1 = (SDTimeEvent) arg0;
        SDTimeEvent t2 = (SDTimeEvent) arg1;
        if (t1.getEvent() > t2.getEvent()) {
            return 1;
        }
        else if (t1.getEvent() == t2.getEvent()) {
            return 0;
        }
        return -1;
    }
}
