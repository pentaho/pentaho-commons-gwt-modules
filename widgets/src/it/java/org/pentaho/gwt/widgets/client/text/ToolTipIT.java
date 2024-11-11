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


package org.pentaho.gwt.widgets.client.text;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;

public class ToolTipIT extends GWTTestCase {

  private final int delay = 200;

  public String getModuleName() {
    return "org.pentaho.gwt.widgets.Widgets"; //$NON-NLS-1$
  }

  public void testOnMouseDown() throws Exception {
    ToolTip toolTip = new ToolTip( "test", delay );

    toolTip.show();
    assertTrue( toolTip.isShowing() );
    toolTip.onMouseDown( null, 0, 0 );
    assertFalse( toolTip.isShowing() );
  }

  public void testOnMouseEnter() throws Exception {
    final ToolTip toolTip = new ToolTip( "test", delay );

    assertFalse( toolTip.isShowing() );
    toolTip.onMouseEnter( null );
    assertFalse( toolTip.isShowing() );
    new Timer() {
      @Override
      public void run() {
        assertTrue( toolTip.isShowing() );
        finishTest();
      }
    }.schedule( delay );
    delayTestFinish( delay * 2 );
  }

  public void testOnMouseLeave() throws Exception {
    ToolTip toolTip = new ToolTip( "test", delay );

    toolTip.show();
    assertTrue( toolTip.isShowing() );
    toolTip.onMouseDown( null, 0, 0 );
    assertFalse( toolTip.isShowing() );
  }

  public void testOnMouseEnterLeave() {
    final ToolTip toolTip = new ToolTip( "test", (int) ( delay * 1.5 ) );

    assertFalse( toolTip.isShowing() );
    toolTip.onMouseEnter( null );
    assertFalse( toolTip.isShowing() );
    new Timer() {
      @Override
      public void run() {
        // the toolTip is not showing yet
        assertFalse( toolTip.isShowing() );
        toolTip.onMouseLeave( null );

        new Timer() {
          @Override
          public void run() {
            // showing of the toolTip was cancelled
            assertFalse( toolTip.isShowing() );
            finishTest();
          }
        }.schedule( delay );
      }
    }.schedule( delay );
    delayTestFinish( delay * 3 );
  }
}
