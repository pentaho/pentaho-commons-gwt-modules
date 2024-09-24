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
* Copyright (c) 2002-2023 Hitachi Vantara. All rights reserved.
*/

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
