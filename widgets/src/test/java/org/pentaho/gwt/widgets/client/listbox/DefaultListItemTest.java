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

package org.pentaho.gwt.widgets.client.listbox;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class DefaultListItemTest {

  private DefaultListItem defaultListItem;

  @Before
  public void setUp() throws Exception {
    defaultListItem = mock( DefaultListItem.class );
  }

  @Test
  public void testSetStylePrimaryName() throws Exception {
    doCallRealMethod().when( defaultListItem ).setStylePrimaryName( anyString() );

    defaultListItem.dropWidget = mock( Widget.class );
    final String test = "test";
    defaultListItem.setStylePrimaryName( test );
    verify( defaultListItem.dropWidget ).setStylePrimaryName( anyString() );
  }

  @Test
  public void testOnBrowserEvent() throws Exception {
    doCallRealMethod().when( defaultListItem ).onBrowserEvent( any( Event.class ) );

    final Event event = mock( Event.class );

    when( event.getTypeInt() ).thenReturn( Event.ONMOUSEOVER );
    defaultListItem.onBrowserEvent( event );
    verify( defaultListItem ).addStyleDependentName( DefaultListItem.HOVER );

    when( event.getTypeInt() ).thenReturn( Event.ONMOUSEOUT );
    defaultListItem.onBrowserEvent( event );
    verify( defaultListItem ).removeStyleDependentName( DefaultListItem.HOVER );

    when( event.getTypeInt() ).thenReturn( Event.ONMOUSEUP );
    defaultListItem.listItemListener = mock( ListItemListener.class );
    defaultListItem.onBrowserEvent( event );
    verify( defaultListItem, times( 2 ) ).removeStyleDependentName( DefaultListItem.HOVER );
    verify( defaultListItem.listItemListener ).itemSelected( defaultListItem, event );

    when( event.getTypeInt() ).thenReturn( Event.ONDBLCLICK );
    defaultListItem.onBrowserEvent( event );
    verify( defaultListItem.listItemListener ).doAction( defaultListItem );
  }

  @Test
  public void testOnSelect() throws Exception {
    doCallRealMethod().when( defaultListItem ).onSelect();

    defaultListItem.widget = mock( Widget.class );
    defaultListItem.onSelect();
    verify( defaultListItem.widget ).addStyleDependentName( DefaultListItem.SELECTED );
  }

  @Test
  public void testOnDeselect() throws Exception {
    doCallRealMethod().when( defaultListItem ).onDeselect();

    defaultListItem.widget = mock( Widget.class );
    defaultListItem.onDeselect();
    verify( defaultListItem.widget ).removeStyleDependentName( DefaultListItem.SELECTED );
  }

  @Test
  public void testSetDropValid() throws Exception {
    doCallRealMethod().when( defaultListItem ).setDropValid( anyBoolean() );

    defaultListItem.dragIndicator = mock( Image.class );

    defaultListItem.setDropValid( true );
    verify( defaultListItem ).addStyleDependentName( DefaultListItem.PROXY_VALID );
    verify( defaultListItem.dragIndicator ).setUrl( anyString() );

    defaultListItem.setDropValid( false );
    verify( defaultListItem ).removeStyleDependentName( DefaultListItem.PROXY_VALID );
    verify( defaultListItem.dragIndicator, times( 2 ) ).setUrl( anyString() );
  }
}
