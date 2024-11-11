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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith( GwtMockitoTestRunner.class )
public class ThemeableImageButtonTest {
  private final String enabledStyle = "enSt";
  private final String disabledStyle = "disSt";

  private ThemeableImageButton button;

  @Test
  public void testSetEnabled() {
    setupButtonSpy();

    button.setEnabled( false );
    verify( button ).addStyleName( disabledStyle );

    button.setEnabled( true );
    verify( button ).addStyleName( enabledStyle );
  }

  @Test
  public void testAddEnabledStyles() {
    setupButtonSpy();

    button.addEnabledStyle( enabledStyle );

    verify( button ).addEnabledStyle( false, enabledStyle );
    verify( button, never() ).updateStyles();
  }

  @Test
  public void testAddEnabledStyles_update() {
    setupButtonSpy();

    button.addEnabledStyle( true, enabledStyle );
    verify( button ).updateStyles();
  }

  @Test
  public void testAddDisabledStyles() {
    setupButtonSpy();

    button.addDisabledStyle( disabledStyle );

    verify( button ).addDisabledStyle( false, disabledStyle );
    verify( button, never() ).updateStyles();
  }

  @Test
  public void testAddDisabledStyles_update() {
    setupButtonSpy();

    button.addDisabledStyle( true, disabledStyle );
    verify( button ).updateStyles();
  }

  private void setupButtonSpy() {
    final String tooltip = "tooltip";

    button = spy( new ThemeableImageButton( enabledStyle, disabledStyle, tooltip ) );
  }
}
