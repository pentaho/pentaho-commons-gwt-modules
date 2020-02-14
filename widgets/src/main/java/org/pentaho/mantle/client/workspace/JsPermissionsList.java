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
 * Copyright (c) 2020 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.mantle.client.workspace;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

import java.util.ArrayList;
import java.util.List;

public class JsPermissionsList extends JavaScriptObject {

  // Overlay types always have protected, zero argument constructors.
  protected JsPermissionsList() {
  }

  public final native JsArray<JsJobParam> getList() /*-{ return this.setting; }-*/;
  public final native void setList( JsArray<JsJobParam> settingList ) /*-{ this.setting = settingList; }-*/;

  public final List<String> getReadableFiles() {
    final List<String> files = new ArrayList<String>();

    final JsArray<JsJobParam> permissionList = getList();

    for ( int idx = 0; idx < permissionList.length(); idx++ ) {
      final JsJobParam param = permissionList.get( idx );

      final String name = param.getName();

      boolean isReadable = "0".equals( param.getValue() );
      if ( isReadable && !files.contains( name ) ) {
        files.add( name );
      }
    }

    return files;
  }
}
