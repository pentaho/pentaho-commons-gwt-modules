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

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ToolbarTest {
  Toolbar toolbar;

  @Before
  public void setUp() throws Exception {
    toolbar = mock( Toolbar.class );
    toolbar.bar = mock( HorizontalPanel.class );
  }

  @Test
  public void testAdd_ToolbarGroup() throws Exception {
    doCallRealMethod().when( toolbar ).add( any( ToolbarGroup.class ) );

    toolbar.groups = mock( List.class );
    final ToolbarGroup toolbarGroup = mock( ToolbarGroup.class );
    final Image leadingSeparator = mock( Image.class );
    when( toolbarGroup.getLeadingSeparator() ).thenReturn( leadingSeparator );
    final String label = "label";
    when( toolbarGroup.getLabel() ).thenReturn( label );
    final Label groupLabel = mock( Label.class );
    when( toolbarGroup.getGroupLabel() ).thenReturn( groupLabel );
    final ToolbarButton button1 = mock( ToolbarButton.class );
    when( button1.getPushButton() ).thenReturn( mock( FocusPanel.class ) );
    final ToolbarButton button2 = mock( ToolbarButton.class );
    when( button2.getPushButton() ).thenReturn( mock( FocusPanel.class ) );
    when( toolbarGroup.getButtons() ).thenReturn( new LinkedList<ToolbarButton>() { {
        add( button1 );
        add( button2 );
      } } );
    final Image trailingSeparator = mock( Image.class );
    when( toolbarGroup.getTrailingSeparator() ).thenReturn( trailingSeparator );

    when( toolbar.bar.getWidgetCount() ).thenReturn( 5 );
    when( toolbar.bar.getWidget( anyInt() ) ).thenReturn( mock( Widget.class ) ); // not Image
    toolbar.add( toolbarGroup );

    verify( toolbar.bar ).add( leadingSeparator );
    verify( toolbar.bar ).add( groupLabel );
    verify( toolbar.bar ).add( button1.getPushButton() );
    verify( toolbar.bar ).add( button2.getPushButton() );
    verify( toolbar.bar ).add( trailingSeparator );
    verify( toolbar.bar, times( 3 ) ).setCellVerticalAlignment( any( Widget.class ), eq( HasVerticalAlignment.ALIGN_MIDDLE ) );
    verify( toolbar.groups ).add( toolbarGroup );
  }

  @Test
  public void testAdd_Panel() throws Exception {
    doCallRealMethod().when( toolbar ).add( any( Panel.class ) );

    final Panel panel = mock( SimplePanel.class );
    final Element element = mock( Element.class );
    when( element.getAttribute( "flex" ) ).thenReturn( "-1" );
    when( panel.getElement() ).thenReturn( element );
    toolbar.add( panel );
    verify( toolbar.bar ).add( panel );
    verify( toolbar.bar, never() ).setCellWidth( eq( panel ), anyString() );

    when( element.getAttribute( "flex" ) ).thenReturn( "1" );
    toolbar.add( panel );
    verify( toolbar.bar, times( 2 ) ).add( panel );
    verify( toolbar.bar ).setCellWidth( eq( panel ), anyString() );
  }

  @Test
  public void testAdd_ToolbarButton() throws Exception {
    doCallRealMethod().when( toolbar ).add( any( ToolbarButton.class ) );

    toolbar.buttons = mock( List.class );

    final ToolbarComboButton button = mock( ToolbarComboButton.class );
    final FocusPanel pushButton = mock( FocusPanel.class );
    when( button.getPushButton() ).thenReturn( pushButton );
    toolbar.add( button );
    verify( toolbar.bar ).add( pushButton );
    verify( toolbar.buttons ).add( button );
    verify( button ).addPopupPanelListener( toolbar );
  }

  @Test
  public void testAdd_Int() throws Exception {
    doCallRealMethod().when( toolbar ).add( anyInt() );

    toolbar.add( Toolbar.SEPARATOR );
    verify( toolbar.bar ).add( any( Widget.class ) );
    verify( toolbar.bar ).setCellVerticalAlignment( any( Image.class ), eq( HasVerticalAlignment.ALIGN_MIDDLE ) );

    toolbar.add( Toolbar.GLUE );
    verify( toolbar.bar, times( 2 ) ).add( any( Widget.class ) );
    verify( toolbar.bar ).setCellWidth( any( SimplePanel.class ), anyString() );
  }

  @Test
  public void testAddSpacer() throws Exception {
    doCallRealMethod().when( toolbar ).addSpacer( anyInt() );

    final int spacerAmount = 20;
    toolbar.addSpacer( spacerAmount );
    verify( toolbar.bar ).add( any( SimplePanel.class ) );
    verify( toolbar.bar ).setCellWidth( any( SimplePanel.class ), contains( String.valueOf( spacerAmount ) ) );
  }

  @Test
  public void testSetEnabled() throws Exception {
    doCallRealMethod().when( toolbar ).setEnabled( anyBoolean() );

    final ToolbarButton button = mock( ToolbarButton.class );
    toolbar.buttons = new LinkedList<ToolbarButton>() { {
        add( button );
      } };
    final ToolbarGroup group = mock( ToolbarGroup.class );
    toolbar.groups = new LinkedList<ToolbarGroup>() { {
        add( group );
      } };

    final boolean enabled = true;
    toolbar.setEnabled( enabled );
    verify( button ).setEnabled( enabled );
    verify( button ).setTempDisabled( !enabled );
    verify( group ).setEnabled( enabled );
    verify( group ).setTempDisabled( !enabled );

    doThrow( Exception.class ).when( button ).setEnabled( anyBoolean() );
    try {
      toolbar.setEnabled( true );
    } catch ( Exception e ) {
      fail();
    }
  }

  @Test
  public void testPopupClosed() throws Exception {
    doCallRealMethod().when( toolbar ).popupClosed( any( PopupPanel.class ) );

    final ToolbarPopupListener listener = mock( ToolbarPopupListener.class );
    toolbar.popupListeners = new LinkedList<ToolbarPopupListener>() { {
        add( listener );
      } };

    final PopupPanel popupPanel = mock( PopupPanel.class );
    toolbar.popupClosed( popupPanel );
    verify( listener ).popupClosed( popupPanel );
  }

  @Test
  public void testPopupOpened() throws Exception {
    doCallRealMethod().when( toolbar ).popupOpened( any( PopupPanel.class ) );

    final ToolbarPopupListener listener = mock( ToolbarPopupListener.class );
    toolbar.popupListeners = new LinkedList<ToolbarPopupListener>() { {
        add( listener );
      } };

    final PopupPanel popupPanel = mock( PopupPanel.class );
    toolbar.popupOpened( popupPanel );
    verify( listener ).popupOpened( popupPanel );
  }

  @Test
  public void testRemoveAll() throws Exception {
    doCallRealMethod().when( toolbar ).removeAll();

    toolbar.buttons = mock( List.class );
    toolbar.removeAll();
    verify( toolbar.bar ).clear();
    verify( toolbar.buttons ).clear();
  }
}
