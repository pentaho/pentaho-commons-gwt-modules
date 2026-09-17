/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.gwt.widgets.client.dialogs;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

public class WindowPanelIT extends GWTTestCase {
  public String getModuleName() {
    return "org.pentaho.gwt.widgets.Widgets"; //$NON-NLS-1$
  }

  public void testMoveBy() throws Exception {
    WindowPanel wp = new WindowPanel( new WindowController( new AbsolutePanel() ), "", new HTML(  ), false );
    // wp has to have a parent
    final AbsolutePanel parent = new AbsolutePanel();
    parent.add( wp );

    // getWidgetLeft/getWidgetTop read the rendered position, which is computed only for
    // elements attached to the document
    RootPanel.get().add( parent );
    try {
      assertEquals( 0, parent.getWidgetLeft( wp ) );
      assertEquals( 0, parent.getWidgetTop( wp ) );
      final int right = 10;
      final int down = 20;

      wp.moveBy( right, down );
      assertEquals( right, parent.getWidgetLeft( wp ) );
      assertEquals( down, parent.getWidgetTop( wp ) );
    } finally {
      RootPanel.get().remove( parent );
    }
  }

  public void testSetContentSize() throws Exception {
    WindowPanel wp = new WindowPanel( new WindowController( new AbsolutePanel() ), "", new HTML(  ), false );

    // getOffsetWidth/getOffsetHeight read the rendered size, which is computed only for
    // elements attached to the document
    RootPanel.get().add( wp );
    try {
      final int width = 10;
      final int height = 20;
      wp.setContentSize( width, height );

      assertEquals( width, wp.contentOrScrollPanelWidget.getOffsetWidth() );
      assertEquals( height, wp.contentOrScrollPanelWidget.getOffsetHeight() );
      assertEquals( width, wp.headerContainer.getOffsetWidth() );
      assertEquals( width, wp.northWidget.getOffsetWidth() );
      assertEquals( height, wp.westWidget.getOffsetHeight() - wp.headerContainer.getOffsetHeight() );
      assertEquals( height, wp.eastWidget.getOffsetHeight() - wp.headerContainer.getOffsetHeight() );
    } finally {
      RootPanel.get().remove( wp );
    }
  }
}
