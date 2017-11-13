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

package org.pentaho.mantle.client.dialogs.filterdialog.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Filter Dialog SQL Support that disables the SQL support for the dialog.
 *
 * @author Jordan Ganoff (jganoff@pentaho.com)
 */
public class DisabledSQLSupport implements IFilterSQLSupport, java.io.Serializable {

  public boolean hasSqlFilterPerms() {
    return false;
  }

  @Override
  public String getSqlListLabel() {
    return null;
  }

  @Override
  public JavaScriptObject parseTestResults( String results, boolean includeHeaders ) {
    return null;
  }

  @Override
  public int getLength( JavaScriptObject obj ) {
    return -1;
  }

  @Override
  public int getRowLength( JavaScriptObject obj, int i ) {
    return -1;
  }

  @Override
  public String getValue( JavaScriptObject obj, int i, int j ) {
    return null;
  }

  @Override
  public String getParameterDefault( String parameter ) {
    return null;
  }
}
