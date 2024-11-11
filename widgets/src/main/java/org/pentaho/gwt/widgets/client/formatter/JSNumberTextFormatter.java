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
import com.google.gwt.i18n.client.NumberFormat;

/**
 * Formatter to handle converting JavaScript numbers to/from a String. To bridge the gap between GWT and JavaScript the
 * value is passed as a String between the two and converted on the other side.
 */
public class JSNumberTextFormatter implements JSTextFormatter {
  protected NumberFormat formatter;

  public JSNumberTextFormatter( final String pattern ) {
    formatter = NumberFormat.getFormat( pattern );
    setupNativeFunctions( getInstance() );
  }

  private static native void setupNativeFunctions( final JavaScriptObject obj )/*-{
    obj.format = function(number) {
      var n = number ? '' + number : null;
      return this.@org.pentaho.gwt.widgets.client.formatter.JSNumberTextFormatter::format(Ljava/lang/Object;)(n);
    }
    obj.parse = function(s) {
      return parseFloat(this.@org.pentaho.gwt.widgets.client.formatter.JSNumberTextFormatter::parse(Ljava/lang/String;)
          (s));
    }
  }-*/;

  public String format( final Object value ) throws IllegalArgumentException {
    if ( value == null ) {
      return null;
    }
    if ( value instanceof String == false ) {
      throw new IllegalArgumentException();
    }
    return formatter.format( Double.parseDouble( (String) value ) );
  }

  public String parse( final String value ) throws NumberFormatException {
    if ( value == null || value.length() == 0 ) {
      return null;
    }
    return String.valueOf( formatter.parse( value ) );
  }

  public native JavaScriptObject getInstance()/*-{
    return this;
  }-*/;
}
