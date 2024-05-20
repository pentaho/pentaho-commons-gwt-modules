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
 * Copyright (c) 2024 Hitachi Vantara. All rights reserved.
 */

package org.pentaho.gwt.widgets.client.ui;

import com.google.gwt.user.client.ui.Label;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pentaho.gwt.widgets.client.buttons.ThemeableImageButton;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith( GwtMockitoTestRunner.class )
public class BannerTest {
  private final String mockMessage = "test mock message";

  private Banner banner;

  @Mock
  private ThemeableImageButton icon;
  @Mock
  private Label label;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks( this );
  }

  @Test
  public void testCreateUI() {
    String status = "mock-status";
    setupBannerSpy( new Banner( status, mockMessage ) );

    banner.createUI();

    verify( icon ).addStyleName( "pentaho-" + status + "-button" );
    verify( banner ).add( icon );

    verifyLabel();
  }

  @Test
  public void testCreateUI_nullStatus() {
    setupBannerSpy( new Banner( mockMessage ) );

    banner.createUI();

    verify( banner, never() ).add( icon );

    verifyLabel();
  }

  private void verifyLabel() {
    verify( label ).setText( mockMessage );
    verify( label ).setStyleName( "typography typography-body" );
    verify( banner ).add( label );
  }

  private void setupBannerSpy( Banner banner ) {
    this.banner = spy( banner );

    when( this.banner.getIcon() ).thenReturn( icon );
    when( this.banner.getLabel() ).thenReturn( label );
  }
}
