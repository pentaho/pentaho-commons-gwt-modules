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
