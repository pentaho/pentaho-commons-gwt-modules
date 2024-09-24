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

package org.pentaho.gwt.widgets.client.buttons;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;

public class ProgressIndicatorWidgetIT extends GWTTestCase {
  public String getModuleName() {
    return "org.pentaho.gwt.widgets.Widgets"; //$NON-NLS-1$
  }

  /**
   * ignore this test - it runs more than 1 minute
   */
  public void ignore_testInProgressTrue() throws Exception {
    final ProgressIndicatorWidget progressIndicatorWidget = new ProgressIndicatorWidget( new Label() );

    assertEquals( ProgressIndicatorWidget.UNPRESSED_DECK, progressIndicatorWidget.currentShownWidget );
    progressIndicatorWidget.inProgress( true );
    assertEquals( ProgressIndicatorWidget.PRESSED_DECK, progressIndicatorWidget.buttonPanel.getVisibleWidget() );
    assertEquals( ProgressIndicatorWidget.PRESSED_DECK, progressIndicatorWidget.currentShownWidget );

    new Timer() {
      @Override
      public void run() {
        assertEquals( ProgressIndicatorWidget.UNPRESSED_DECK, progressIndicatorWidget.buttonPanel.getVisibleWidget() );
        assertEquals( ProgressIndicatorWidget.UNPRESSED_DECK, progressIndicatorWidget.currentShownWidget );
        finishTest();
      }
    }.schedule( ProgressIndicatorWidget.EXPIRATION_DURATION + ProgressIndicatorWidget.STOP_DELAY_TIMER + 10 );

    delayTestFinish( ProgressIndicatorWidget.EXPIRATION_DURATION + ProgressIndicatorWidget.STOP_DELAY_TIMER + 20 );
  }

  public void testInProgressFalse() throws Exception {
    final ProgressIndicatorWidget progressIndicatorWidget = new ProgressIndicatorWidget( new Label() );

    progressIndicatorWidget.currentShownWidget = ProgressIndicatorWidget.PRESSED_DECK;
    progressIndicatorWidget.inProgress( false );

    new Timer() {
      @Override
      public void run() {
        assertEquals( ProgressIndicatorWidget.UNPRESSED_DECK, progressIndicatorWidget.buttonPanel.getVisibleWidget() );
        assertEquals( ProgressIndicatorWidget.UNPRESSED_DECK, progressIndicatorWidget.currentShownWidget );
        finishTest();
      }
    }.schedule( ProgressIndicatorWidget.STOP_DELAY_TIMER + 10 );

    delayTestFinish( ProgressIndicatorWidget.STOP_DELAY_TIMER + 20 );
  }
}
