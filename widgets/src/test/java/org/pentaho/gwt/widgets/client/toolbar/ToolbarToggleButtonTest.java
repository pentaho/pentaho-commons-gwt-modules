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
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ToolbarToggleButtonTest {
  private ToolbarToggleButton toolbarToggleButton;

  @Before
  public void setUp() throws Exception {
    toolbarToggleButton = mock( ToolbarToggleButton.class );
  }

  @Test
  public void testSetSelected() throws Exception {
    doCallRealMethod().when( toolbarToggleButton ).setSelected( anyBoolean(), anyBoolean() );

    final Command command = mock( Command.class );
    toolbarToggleButton.command = command;

    toolbarToggleButton.setSelected( true, false );
    verify( command, never() ).execute();

    toolbarToggleButton.setSelected( true, true );
    verify( command ).execute();

    verify( toolbarToggleButton, times( 2 ) ).updateSelectedStyle();
  }

  @Test
  public void testUpdateSelectedStyle() throws Exception {
    doCallRealMethod().when( toolbarToggleButton ).updateSelectedStyle();

    final DockPanel button = mock( DockPanel.class );
    toolbarToggleButton.button = button;
    toolbarToggleButton.downImage = mock( Image.class );

    toolbarToggleButton.selected = true;
    toolbarToggleButton.updateSelectedStyle();
    verify( button ).addStyleName( anyString() );
    verify( button ).remove( any( Image.class ) );
    verify( button ).add( any( Image.class ), eq( DockPanel.CENTER ) );

    toolbarToggleButton.selected = false;
    toolbarToggleButton.updateSelectedStyle();
    verify( button, times( 2 ) ).remove( any( Image.class ) );
    verify( button, times( 2 ) ).add( any( Image.class ), eq( DockPanel.CENTER ) );
    verify( button, times( 2 ) ).removeStyleName( anyString() );
  }

  @Test
  @SuppressWarnings( "deprecation" )
  public void testAddStyleMouseListener() throws Exception {
    doCallRealMethod().when( toolbarToggleButton ).addStyleMouseListener();

    toolbarToggleButton.eventWrapper = mock( FocusPanel.class );
    toolbarToggleButton.addStyleMouseListener();
    verify( toolbarToggleButton.eventWrapper ).addClickListener( any( ClickListener.class ) );
    verify( toolbarToggleButton.eventWrapper ).addMouseListener( any( MouseListener.class ) );
  }
}
