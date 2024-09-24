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
