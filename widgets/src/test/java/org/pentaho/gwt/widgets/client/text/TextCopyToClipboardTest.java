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


package org.pentaho.gwt.widgets.client.text;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pentaho.gwt.widgets.client.buttons.ThemeableImageButton;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith( GwtMockitoTestRunner.class )
public class TextCopyToClipboardTest {
  private final String mockValue = "test mock value";

  private TextCopyToClipboard textCopyToClipboard;

  @Mock
  private ThemeableImageButton copyButton;
  @Mock
  private Label copyLabel;
  @Mock
  private VerticalPanel valueContainerPanel;
  @Mock
  private ScrollPanel valueScrollPanel;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks( this );
    textCopyToClipboard = spy( new TextCopyToClipboard( mockValue ) );

    when( textCopyToClipboard.getCopyButton() ).thenReturn( copyButton );
    when( textCopyToClipboard.getCopyLabel() ).thenReturn( copyLabel );
    when( textCopyToClipboard.getValueContainerPanel() ).thenReturn( valueContainerPanel );
    when( textCopyToClipboard.getValueScrollPanel() ).thenReturn( valueScrollPanel );
  }

  @Test
  public void testCreateUI() {
    textCopyToClipboard.createUI();

    verify( copyButton ).addEnabledStyle( true, "pentaho-copy-text-button", "icon-zoomable" );
    verify( copyButton ).setTitle( "copy" );
    verify( copyButton ).addClickHandler( any( ClickHandler.class ) );
    verify( textCopyToClipboard ).add( copyButton );

    verify( valueContainerPanel ).addStyleName( "value-container" );
    verify( copyLabel ).setText( mockValue );
    verify( copyLabel ).setStyleName( "typography typography-body" );
    verify( valueContainerPanel ).add( copyLabel );
    verify( valueScrollPanel ).add( valueContainerPanel );
  }
}
