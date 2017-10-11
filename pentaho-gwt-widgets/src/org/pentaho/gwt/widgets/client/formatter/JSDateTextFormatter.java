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
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;

import java.util.Date;

/**
 * Formatter to handle converting JavaScript Dates to/from a String. To bridge the gap between GWT and JavaScript the
 * value is passed as a String between the two and converted on the other side.
 */
public class JSDateTextFormatter implements JSTextFormatter {
  protected DateTimeFormat dateFormat;

  public JSDateTextFormatter( final String pattern ) {
    this( DateTimeFormat.getFormat( pattern ) );
  }

  public JSDateTextFormatter( final DateTimeFormat format ) {
    this.dateFormat = format;
    setupNativeFunctions( getInstance() );
  }

  /**
   * Create a date formatter using the default "long date" format. This is predefined per locale.
   */
  public static JSDateTextFormatter createDefaultDateFormatter() {
    return new JSDateTextFormatter( DateTimeFormat.getFormat( PredefinedFormat.DATE_LONG ) );
  }

  private static native void setupNativeFunctions( final JavaScriptObject obj )/*-{
    obj.format = function(date) {
      var d = date ? '' + date.getTime() : null;
      return this.@org.pentaho.gwt.widgets.client.formatter.JSDateTextFormatter::format(Ljava/lang/Object;)(d);
    }
    obj.parse = function(s) {
      var d = parseFloat(this.@org.pentaho.gwt.widgets.client.formatter.JSDateTextFormatter::parse(Ljava/lang/String;)
          (s));
      return new Date(d);
    }
  }-*/;

  /**
   * Formats a date stored as the milliseconds from Jan. 1, 1970 as a String. A String is used to get around the
   * JavaScript long 53-bit maximum precision restriction. The {@link java.util.Date} is created by parsing the String
   * to a Long (emulated in GWT by two 32-bit integers) then passing it to {@link java.util.Date#Date(long)}.
   */
  public String format( final Object value ) throws IllegalArgumentException {
    if ( value == null ) {
      return null;
    }
    if ( value instanceof String == false ) {
      throw new IllegalArgumentException();
    }

    return dateFormat.format( new Date( Long.parseLong( (String) value ) ) );
  }

  public String parse( final String value ) throws IllegalArgumentException {
    if ( value == null || value.length() == 0 ) {
      return null;
    }
    return String.valueOf( dateFormat.parse( value ).getTime() );
  }

  public native JavaScriptObject getInstance()/*-{
                                              return this;
                                              }-*/;
}
