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


package org.pentaho.gwt.widgets.client.tabs;

import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Label;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class PentahoTabTest {

  private PentahoTab pentahoTab;

  @Before
  public void setUp() throws Exception {
    pentahoTab = mock( PentahoTab.class );
  }

  @Test
  public void testSetupLabel() throws Exception {
    doCallRealMethod().when( pentahoTab ).setupLabel( anyString(), anyString() );

    final String text = "text";
    final String tip = "tip";
    pentahoTab.label = mock( Label.class );
    pentahoTab.setupLabel( text, tip );
    verify( pentahoTab.label ).setText( text );
    verify( pentahoTab.label ).setTitle( tip );
    verify( pentahoTab.label ).setStylePrimaryName( anyString() );
  }

  @Test
  public void testOnBrowserEventDoubleClick() {
    doCallRealMethod().when( pentahoTab ).onBrowserEvent( any( Event.class ) );

    final Event event = mock( Event.class );
    when( event.getTypeInt() ).thenReturn( Event.ONDBLCLICK );
    pentahoTab.onBrowserEvent( event );
    verify( pentahoTab ).onDoubleClick( event );
  }

  @Test
  public void testOnBrowserEventMouseUp() {
    doCallRealMethod().when( pentahoTab ).onBrowserEvent( any( Event.class ) );

    final Event event = mock( Event.class );
    when( event.getTypeInt() ).thenReturn( Event.ONMOUSEUP );

    when( event.getButton() ).thenReturn( Event.BUTTON_RIGHT );
    pentahoTab.onBrowserEvent( event );
    verify( pentahoTab ).onRightClick( event );

    when( event.getButton() ).thenReturn( Event.BUTTON_LEFT );
    final EventTarget eventTarget = mock( EventTarget.class );
    when( eventTarget.toString() ).thenReturn( "image" );
    when( event.getEventTarget() ).thenReturn( eventTarget );
    pentahoTab.onBrowserEvent( event );
    verify( pentahoTab, never() ).fireTabSelected();

    when( eventTarget.toString() ).thenReturn( "" );
    pentahoTab.onBrowserEvent( event );
    verify( pentahoTab ).fireTabSelected();
  }

  @Test
  public void testOnBrowserEventKeyDown() {
    doCallRealMethod().when( pentahoTab ).onBrowserEvent( any( Event.class ) );

    final Event event = mock( Event.class );
    when( event.getTypeInt() ).thenReturn( Event.ONKEYDOWN );

    when( event.getKeyCode() ).thenReturn( KeyCodes.KEY_LEFT );
    pentahoTab.onBrowserEvent( event );
    verify( pentahoTab ).moveFocusToPreviousTab();

    when( event.getKeyCode() ).thenReturn( KeyCodes.KEY_RIGHT );
    pentahoTab.onBrowserEvent( event );
    verify( pentahoTab ).moveFocusToNextTab();
  }

  @Test
  public void testSetSelected() throws Exception {
    doCallRealMethod().when( pentahoTab ).setSelected( anyBoolean() );

    pentahoTab.setSelected( true );
    verify( pentahoTab ).addStyleDependentName( PentahoTab.SELECTED );

    pentahoTab.setSelected( false );
    verify( pentahoTab ).removeStyleDependentName( PentahoTab.SELECTED );
  }
}
