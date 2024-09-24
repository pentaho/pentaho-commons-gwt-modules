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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.gwt.widgets.client.utils.ButtonHelper;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class CustomButtonTest {
  private CustomButton customButton;
  private Command command;

  public String getModuleName() {
    return "org.pentaho.gwt.widgets.Widgets"; //$NON-NLS-1$
  }

  @Before
  public void setUp() throws Exception {
    final Image image = mock( Image.class );
    command = mock( Command.class );
    customButton = spy( new CustomButton( image, "text", ButtonHelper.ButtonLabelType.TEXT_ONLY, command ) );
    customButton.buttonPanel = mock( HorizontalPanel.class );
  }

  @Test
  public void testSetStylePrimaryName() throws Exception {
    final String stylePrimaryName = "test";
    customButton.setStylePrimaryName( stylePrimaryName );

    verify( customButton.buttonPanel ).setStylePrimaryName( stylePrimaryName );
  }

  @Test
  public void testAddStyleDependentName() throws Exception {
    final String style = "test";
    customButton.addStyleDependentName( style );

    verify( customButton.buttonPanel ).addStyleDependentName( style );
  }

  @Test
  public void testRemoveStyleDependentName() throws Exception {
    final String style = "test";
    customButton.removeStyleDependentName( style );

    verify( customButton.buttonPanel ).removeStyleDependentName( style );
  }

  @Test
  public void testSetEnabled() throws Exception {
    customButton.setEnabled( false );

    verify( customButton ).addStyleDependentName( anyString() );
    verify( customButton.buttonPanel ).addStyleDependentName( anyString() );

    customButton.setEnabled( true );
    verify( customButton ).removeStyleDependentName( anyString() );
    verify( customButton.buttonPanel ).removeStyleDependentName( anyString() );
  }

  @Test
  public void testOnBrowserEventDblClick() throws Exception {
    final Event event = createEventOnBrowserEvent( Event.ONDBLCLICK );

    verify( command ).execute();
    verify( event ).cancelBubble( true );
    verify( event ).preventDefault();
  }

  @Test
  public void testOnBrowserEventClick() throws Exception {
    final Event event = createEventOnBrowserEvent( Event.ONCLICK );

    verify( command ).execute();
    verify( event ).cancelBubble( true );
    verify( event ).preventDefault();
  }

  @Test
  public void testOnBrowserEventMouseUp() throws Exception {
    final Event event = createEventOnBrowserEvent( Event.ONMOUSEUP );

    verify( command ).execute();
    verify( event ).cancelBubble( true );
    verify( event ).preventDefault();
  }

  @Test
  public void testOnBrowserEventMouseOver() throws Exception {
    createEventOnBrowserEvent( Event.ONMOUSEOVER );

    verify( customButton ).addStyleDependentName( "hover" );
  }

  @Test
  public void testOnBrowserEventMouseOut() throws Exception {
    createEventOnBrowserEvent( Event.ONMOUSEOUT );

    verify( customButton ).removeStyleDependentName( "hover" );
  }

  @Test
  public void testOnBrowserEventMouseDown() throws Exception {
    createEventOnBrowserEvent( Event.ONMOUSEDOWN );

    verify( customButton ).addStyleDependentName( "down" );
  }

  private Event createEventOnBrowserEvent( int type ) {
    final Event event = mock( Event.class );
    when( event.getTypeInt() ).thenReturn( type );
    customButton.onBrowserEvent( event );

    return event;
  }
}
