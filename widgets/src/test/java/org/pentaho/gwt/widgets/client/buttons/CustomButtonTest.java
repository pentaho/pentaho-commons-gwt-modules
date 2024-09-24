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
