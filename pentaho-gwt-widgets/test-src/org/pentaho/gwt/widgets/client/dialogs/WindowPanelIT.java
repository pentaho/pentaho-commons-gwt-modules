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

package org.pentaho.gwt.widgets.client.dialogs;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;

public class WindowPanelIT extends GWTTestCase {
  public String getModuleName() {
    return "org.pentaho.gwt.widgets.Widgets"; //$NON-NLS-1$
  }

  public void testMoveBy() throws Exception {
    WindowPanel wp = new WindowPanel( new WindowController( new AbsolutePanel() ), "", new HTML(  ), false );
    // wp has to have a parent
    final AbsolutePanel parent = new AbsolutePanel();
    parent.add( wp );

    assertEquals(0, parent.getWidgetLeft( wp ) );
    assertEquals( 0, parent.getWidgetTop( wp ) );
    final int right = 10;
    final int down = 20;

    wp.moveBy( right, down );
    assertEquals( right, parent.getWidgetLeft( wp ) );
    assertEquals( down, parent.getWidgetTop( wp ) );
  }

  public void testSetContentSize() throws Exception {
    WindowPanel wp = new WindowPanel( new WindowController( new AbsolutePanel() ), "", new HTML(  ), false );

    final int width = 10;
    final int height = 20;
    wp.setContentSize( width, height );

    assertEquals( width, wp.contentOrScrollPanelWidget.getOffsetWidth() );
    assertEquals( height, wp.contentOrScrollPanelWidget.getOffsetHeight() );
    assertEquals( width, wp.headerContainer.getOffsetWidth() );
    assertEquals( width, wp.northWidget.getOffsetWidth() );
    assertEquals( height, wp.westWidget.getOffsetHeight() - wp.headerContainer.getOffsetHeight() );
    assertEquals( height, wp.eastWidget.getOffsetHeight() - wp.headerContainer.getOffsetHeight() );
  }
}
