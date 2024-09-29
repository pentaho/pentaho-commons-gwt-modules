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

import com.google.gwt.core.client.JavaScriptObject;

public class JSTextFormatterFactory {
  public static JavaScriptObject createFormatter( final String type, final String pattern )
    throws IllegalArgumentException {
    if ( "number".equals( type ) || //$NON-NLS-1$
        Number.class.getName().equals( type ) || Byte.class.getName().equals( type )
        || Short.class.getName().equals( type ) || Integer.class.getName().equals( type )
        || Long.class.getName().equals( type ) || Float.class.getName().equals( type )
        || Double.class.getName().equals( type ) || "java.math.BigDecimal".equals( type ) || //$NON-NLS-1$
        "java.math.BigInteger".equals( type ) ) {
      return new JSNumberTextFormatter( pattern ).getInstance();
    } else if ( "date".equals( type ) || //$NON-NLS-1$
        java.util.Date.class.getName().equals( type ) || java.sql.Date.class.getName().equals( type )
        || java.sql.Time.class.getName().equals( type ) || java.sql.Timestamp.class.getName().equals( type ) ) {
      return new JSDateTextFormatter( pattern ).getInstance();
    }
    return null;
  }
}
