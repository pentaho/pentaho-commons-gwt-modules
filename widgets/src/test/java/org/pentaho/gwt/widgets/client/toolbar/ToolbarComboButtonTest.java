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


package org.pentaho.gwt.widgets.client.toolbar;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ToolbarComboButtonTest {
  private ToolbarComboButton toolbarComboButton;

  @Before
  public void setUp() throws Exception {
    toolbarComboButton = mock( ToolbarComboButton.class );

  }

  @Test( expected = UnsupportedOperationException.class )
  public void testSetCommand() throws Exception {
    doCallRealMethod().when( toolbarComboButton ).setCommand( any( Command.class ) );

    toolbarComboButton.setCommand( mock( Command.class ) );
  }

  @Test
  @SuppressWarnings( "deprecation" )
  public void testAddStyleMouseListener() throws Exception {
    doCallRealMethod().when( toolbarComboButton ).addStyleMouseListener();

    final FocusPanel eventWrapper = mock( FocusPanel.class );
    toolbarComboButton.eventWrapper = eventWrapper;
    toolbarComboButton.addStyleMouseListener();
    verify( eventWrapper ).addClickListener( any( ClickListener.class ) );
    verify( eventWrapper ).addMouseListener( any( MouseListener.class ) );
  }

  @Test
  public void testSetMenu() throws Exception {
    doCallRealMethod().when( toolbarComboButton ).setMenu( any( MenuBar.class ) );

    toolbarComboButton.popup = mock( PopupPanel.class );
    final MenuBar menuBar = mock( MenuBar.class );
    toolbarComboButton.setMenu( menuBar );
    verify( toolbarComboButton.popup ).setWidget( menuBar );
  }

  @Test
  public void testAddPopupPanelListener() throws Exception {
    doCallRealMethod().when( toolbarComboButton ).addPopupPanelListener( any( ToolbarPopupListener.class ) );

    final List listeners = mock( List.class );
    toolbarComboButton.popupListeners = listeners;
    final ToolbarPopupListener listener = mock( ToolbarPopupListener.class );

    when( listeners.contains( listener ) ).thenReturn( true );
    toolbarComboButton.addPopupPanelListener( listener );
    verify( listeners, never() ).add( listener );

    when( listeners.contains( listener ) ).thenReturn( false );
    toolbarComboButton.addPopupPanelListener( listener );
    verify( listeners ).add( listener );
  }

  @Test
  public void testRemovePopupPanelListener() throws Exception {
    doCallRealMethod().when( toolbarComboButton ).removePopupPanelListener( any( ToolbarPopupListener.class ) );

    final List listeners = mock( List.class );
    toolbarComboButton.popupListeners = listeners;
    final ToolbarPopupListener listener = mock( ToolbarPopupListener.class );

    when( listeners.contains( listener ) ).thenReturn( false );
    toolbarComboButton.removePopupPanelListener( listener );
    verify( listeners, never() ).remove( listener );

    when( listeners.contains( listener ) ).thenReturn( true );
    toolbarComboButton.removePopupPanelListener( listener );
    verify( listeners ).remove( listener );
  }

  @Test
  public void testNotifyPopupListeners() throws Exception {
    doCallRealMethod().when( toolbarComboButton ).notifyPopupListeners( any( PopupPanel.class ), anyBoolean() );

    final ToolbarPopupListener listener = mock( ToolbarPopupListener.class );
    toolbarComboButton.popupListeners = new LinkedList<ToolbarPopupListener>() { {
        add( listener );
      } };
    final PopupPanel popupPanel = mock( PopupPanel.class );

    toolbarComboButton.notifyPopupListeners( popupPanel, true );
    verify( listener ).popupOpened( popupPanel );

    toolbarComboButton.notifyPopupListeners( popupPanel, false );
    verify( listener ).popupClosed( popupPanel );
  }

  @Test
  public void testOnPopupClosed() throws Exception {
    doCallRealMethod().when( toolbarComboButton ).onPopupClosed( any( PopupPanel.class ), anyBoolean() );

    final PopupPanel popupPanel = mock( PopupPanel.class );

    toolbarComboButton.onPopupClosed( popupPanel, true );
    verify( toolbarComboButton ).notifyPopupListeners( popupPanel, false );

    toolbarComboButton.onPopupClosed( popupPanel, false );
    verify( toolbarComboButton, times( 2 ) ).notifyPopupListeners( popupPanel, false );
  }
}
