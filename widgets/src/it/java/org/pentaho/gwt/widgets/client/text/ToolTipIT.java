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
* Copyright (c) 2002-2021 Hitachi Vantara..  All rights reserved.
*/

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
