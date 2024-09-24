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
