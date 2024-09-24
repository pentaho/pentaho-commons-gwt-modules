/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.gwt.widgets.client.formatter;

/**
 * Object that can format text to/from an object.
 */
public interface JSTextFormatter {
  public String format( Object value ) throws IllegalArgumentException;

  public Object parse( String value ) throws IllegalArgumentException;
}
