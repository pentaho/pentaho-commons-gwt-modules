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
