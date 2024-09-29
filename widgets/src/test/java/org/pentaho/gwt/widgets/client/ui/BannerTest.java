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
