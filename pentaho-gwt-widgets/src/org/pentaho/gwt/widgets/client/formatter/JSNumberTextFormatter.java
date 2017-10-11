/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
