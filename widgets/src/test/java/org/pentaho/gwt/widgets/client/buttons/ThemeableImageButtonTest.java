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
* Copyright (c) 2002-2024 Hitachi Vantara. All rights reserved.
*/

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
