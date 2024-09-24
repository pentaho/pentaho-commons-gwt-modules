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

package org.pentaho.gwt.widgets.client.utils;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.ListBox;

public class ListBoxUtilsIT extends GWTTestCase {
  public String getModuleName() {
    return "org.pentaho.gwt.widgets.Widgets"; //$NON-NLS-1$
  }

  public void testRemoveAll() throws Exception {
    final ListBox listBox = new ListBox();
    listBox.addItem( "l1" );
    listBox.addItem( "l2" );
    listBox.addItem( "l3" );
    assertEquals( 3, listBox.getItemCount() );
    ListBoxUtils.removeAll( listBox );
    assertEquals( 0, listBox.getItemCount() );
  }
}
