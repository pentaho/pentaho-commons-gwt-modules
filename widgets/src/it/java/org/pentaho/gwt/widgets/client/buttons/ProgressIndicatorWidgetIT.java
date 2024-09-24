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
