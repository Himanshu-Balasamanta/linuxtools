/*******************************************************************************
 * Copyright (c) 2011-2012 Ericsson, Ecole Polytechnique de Montreal and others
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Matthew Khouzam - Initial API and implementation
 * Contributors: Simon Marchi - Initial API and implementation
 *******************************************************************************/

package org.eclipse.linuxtools.ctf.core.event.types;

import org.eclipse.linuxtools.internal.ctf.core.event.io.BitBuffer;

/**
 * <b><u>EnumDefinition</u></b><br>
 * The definition of a enum point basic data type. It will take the data
 * from a trace and store it (and make it fit) as an integer and a string.
 */
public class EnumDefinition extends Definition {

    // ------------------------------------------------------------------------
    // Attributes
    // ------------------------------------------------------------------------

    private final EnumDeclaration declaration;

    private final IntegerDefinition integerValue;

    private String value;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Constructor
     * @param declaration the parent declaration
     * @param definitionScope the parent scope
     * @param fieldName the field name
     */
    public EnumDefinition(EnumDeclaration declaration,
            IDefinitionScope definitionScope, String fieldName) {
        super(definitionScope, fieldName);

        this.declaration = declaration;

        integerValue = declaration.getContainerType().createDefinition(
                definitionScope, fieldName);
        value = ((Long) integerValue.getValue()).toString();
    }

    // ------------------------------------------------------------------------
    // Getters/Setters/Predicates
    // ------------------------------------------------------------------------

    /**
     * Gets the value of the enum in string format so "Enum a{DAY="0", NIGHT="1"}; will return "DAY"
     * @return the value of the enum.
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets the value of the enum in string format so "Enum a{DAY="0", NIGHT="1"}; will return 0
     * @return the value of the enum.
     */
    public long getIntegerValue() {
        return integerValue.getValue();
    }

    /**
     * Sets the value of the enum in string format so "Enum a{DAY="0", NIGHT="1"}; will set 0
     * @param Value The value of the enum.
     */
    public void setIntegerValue(long Value) {
        integerValue.setValue(Value);
        value = ((Long) integerValue.getValue()).toString();
    }

    @Override
    public EnumDeclaration getDeclaration() {
        return declaration;
    }

    // ------------------------------------------------------------------------
    // Operations
    // ------------------------------------------------------------------------

    @Override
    public void read(BitBuffer input) {
        int align = (int) declaration.getAlignment();
        int pos = input.position() + ((align-(input.position() % align))%align);
        input.position(pos);
        integerValue.read(input);
        long val = integerValue.getValue();

        // TODO: what to do if the integer value maps to no string for this
        // integer ?
        value = declaration.query(val);
    }

}
