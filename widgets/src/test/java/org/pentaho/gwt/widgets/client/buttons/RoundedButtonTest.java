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


package org.pentaho.gwt.widgets.client.buttons;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedList;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class RoundedButtonTest {
  @Test
  public void testSetEnabled() throws Exception {
    RoundedButton roundedButton = mock( RoundedButton.class );
    doCallRealMethod().when( roundedButton ).setEnabled( anyBoolean() );

    roundedButton.setEnabled( true );
    verify( roundedButton ).removeStyleDependentName( "disabled" );

    roundedButton.setEnabled( false );
    verify( roundedButton ).addStyleDependentName( "disabled" );
  }

  @Test
  @SuppressWarnings( "deprecation" )
  public void testONMOUSEUP() throws Exception {
    final Command command = mock( Command.class );
    RoundedButton roundedButton = mock( RoundedButton.class );
    roundedButton.command = command;
    final ClickListener clickListener = mock( ClickListener.class );
    roundedButton.listeners = new LinkedList<ClickListener>() { {
        add( clickListener );
      } };

    Event event = prepareRB( roundedButton, Event.ONMOUSEUP );

    verify( clickListener ).onClick( roundedButton );
    verify( command ).execute();
    verify( roundedButton ).removeStyleDependentName( "over" );
    verify( event ).cancelBubble( true );
    verify( event ).preventDefault();
  }

  @Test
  @SuppressWarnings( "deprecation" )
  public void testONDBLCLICK() throws Exception {
    Event event = prepareRB( mock( RoundedButton.class ), Event.ONDBLCLICK );

    verify( event ).cancelBubble( true );
    verify( event ).preventDefault();
  }

  @Test
  public void testONMOUSEOVER() throws Exception {
    final RoundedButton roundedButton = mock( RoundedButton.class );
    prepareRB( roundedButton, Event.ONMOUSEOVER );

    verify( roundedButton ).addStyleDependentName( "over" );
  }

  @Test
  public void testONMOUSEOUT() throws Exception {
    final RoundedButton roundedButton = mock( RoundedButton.class );
    prepareRB( roundedButton, Event.ONMOUSEOUT );

    verify( roundedButton ).removeStyleDependentName( "over" );
  }

  private Event prepareRB( RoundedButton roundedButton, int eventType ) {
    doCallRealMethod().when( roundedButton ).onBrowserEvent( any( Event.class ) );
    final Event event = mock( Event.class );
    when( event.getTypeInt() ).thenReturn( eventType );
    doReturn( true ).when( roundedButton ).isEnabled();

    roundedButton.onBrowserEvent( event );

    return event;
  }
}
