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

package org.pentaho.gwt.widgets.client.tabs;

import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class PentahoTabPanelTest {
  private PentahoTabPanel pentahoTabPanel;

  @Before
  public void setUp() throws Exception {
    pentahoTabPanel = mock( PentahoTabPanel.class );
    pentahoTabPanel.tabBar = mock( FlowPanel.class );
    pentahoTabPanel.tabDeck = mock( DeckPanel.class );
  }

  @Test
  public void testAddTab() throws Exception {
    doCallRealMethod().when( pentahoTabPanel ).addTab( anyString(), anyString(), anyBoolean(), any( Widget.class ) );

    final Widget content = mock( Widget.class );
    pentahoTabPanel.addTab( "text", "tip", true, content );
    verify( pentahoTabPanel.tabBar ).add( any( PentahoTab.class ) );
    verify( pentahoTabPanel.tabDeck ).add( content );
    verify( pentahoTabPanel ).selectTab( any( PentahoTab.class ) );
  }

  @Test
  public void testCloseTab() throws Exception {
    doCallRealMethod().when( pentahoTabPanel ).closeTab( any( PentahoTab.class ), anyBoolean() );
    pentahoTabPanel.selectedTab = mock( PentahoTab.class );

    final PentahoTab pentahoTab = mock( PentahoTab.class );
    final Widget content = mock( Widget.class );
    when( pentahoTab.getContent() ).thenReturn( content );
    final int index = 5;
    when( pentahoTabPanel.tabBar.getWidgetIndex( pentahoTab ) ).thenReturn( index );
    when( pentahoTabPanel.tabBar.getWidgetIndex( pentahoTabPanel.selectedTab ) ).thenReturn( 1 );

    pentahoTabPanel.closeTab( pentahoTab, true );
    verify( pentahoTabPanel.tabBar ).remove( pentahoTab );
    verify( pentahoTabPanel.tabDeck ).remove( content );
    verify( pentahoTabPanel, never() ).selectTab( any( PentahoTab.class ) );

    when( pentahoTabPanel.tabBar.getWidgetIndex( pentahoTabPanel.selectedTab ) ).thenReturn( -1 );
    when( pentahoTabPanel.tabBar.getWidgetCount() ).thenReturn( 8 );
    final PentahoTab newSelWidget = mock( PentahoTab.class );
    when( pentahoTabPanel.tabBar.getWidget( index ) ).thenReturn( newSelWidget );
    pentahoTabPanel.closeTab( pentahoTab, true );
    verify( pentahoTabPanel.tabBar, times( 2 ) ).remove( pentahoTab );
    verify( pentahoTabPanel.tabDeck, times( 2 ) ).remove( content );
    verify( pentahoTabPanel ).selectTab( newSelWidget );

    when( pentahoTabPanel.tabBar.getWidgetCount() ).thenReturn( 3 );
    final PentahoTab lastWidget = mock( PentahoTab.class );
    when( pentahoTabPanel.tabBar.getWidget( 2 ) ).thenReturn( lastWidget ); // last in order
    pentahoTabPanel.closeTab( pentahoTab, true );
    verify( pentahoTabPanel.tabBar, times( 3 ) ).remove( pentahoTab );
    verify( pentahoTabPanel.tabDeck, times( 3 ) ).remove( content );
    verify( pentahoTabPanel ).selectTab( lastWidget );
  }

  @Test
  public void testCloseOtherTabs() throws Exception {
    doCallRealMethod().when( pentahoTabPanel ).closeOtherTabs( any( PentahoTab.class ) );

    final PentahoTab tab = mock( PentahoTab.class );
    when( pentahoTabPanel.getTab( 0 ) ).thenReturn( mock( PentahoTab.class ), mock( PentahoTab.class ), tab );
    when( pentahoTabPanel.getTab( 2 ) ).thenReturn( mock( PentahoTab.class ) );
    when( pentahoTabPanel.getTab( 1 ) ).thenReturn( mock( PentahoTab.class ) );
    when( pentahoTabPanel.getTabCount() ).thenReturn( 3, 3, 2, 2, 1, 1 );
    pentahoTabPanel.closeOtherTabs( tab );
    verify( pentahoTabPanel, times( 2 ) ).closeTab( 0, false );
    verify( pentahoTabPanel ).closeTab( 2, false );
    verify( pentahoTabPanel ).closeTab( 1, false );
    verify( pentahoTabPanel ).selectTab( tab );
  }

  @Test
  public void testCloseAllTabs() throws Exception {
    doCallRealMethod().when( pentahoTabPanel ).closeAllTabs();

    when( pentahoTabPanel.getTab( 0 ) ).thenReturn( mock( PentahoTab.class ), mock( PentahoTab.class ),
        mock( PentahoTab.class ), null );
    pentahoTabPanel.closeAllTabs();
    verify( pentahoTabPanel, times( 3 ) ).closeTab( 0, false );
  }

  @Test
  public void testSelectTab() throws Exception {
    doCallRealMethod().when( pentahoTabPanel ).selectTab( any( PentahoTab.class ) );

    final PentahoTab tab = mock( PentahoTab.class );
    when( pentahoTabPanel.tabBar.getWidgetCount() ).thenReturn( 3 );
    final PentahoTab tab1 = mock( PentahoTab.class );
    final PentahoTab tab2 = mock( PentahoTab.class );
    when( pentahoTabPanel.tabBar.getWidget( anyInt() ) ).thenReturn( tab1, tab, tab2 );
    pentahoTabPanel.selectTab( tab );
    verify( tab1 ).setSelected( false );
    verify( tab2 ).setSelected( false );
    verify( tab ).setSelected( true );
    verify( pentahoTabPanel.tabDeck ).showWidget( anyInt() );
  }

  @Test
  public void testGetTab() throws Exception {
    doCallRealMethod().when( pentahoTabPanel ).getTab( anyInt() );

    when( pentahoTabPanel.tabBar.getWidgetCount() ).thenReturn( 3 );
    final PentahoTab tab1 = mock( PentahoTab.class );
    final PentahoTab tab2 = mock( PentahoTab.class );
    when( pentahoTabPanel.tabBar.getWidget( anyInt() ) ).thenReturn( tab1 );
    final int index = 2;
    when( pentahoTabPanel.tabBar.getWidget( index ) ).thenReturn( tab2 );
    assertEquals( tab2, pentahoTabPanel.getTab( index ) );
    assertNull( pentahoTabPanel.getTab( -1 ) );
    assertNull( pentahoTabPanel.getTab( 5 ) );
  }

  @Test
  public void testGetSelectedTabIndex() throws Exception {
    doCallRealMethod().when( pentahoTabPanel ).getSelectedTabIndex();

    assertEquals( -1, pentahoTabPanel.getSelectedTabIndex() );

    final PentahoTab tab = mock( PentahoTab.class );
    pentahoTabPanel.selectedTab = tab;
    final int index = 2;
    when( pentahoTabPanel.tabBar.getWidgetIndex( tab ) ).thenReturn( index );
    assertEquals(index, pentahoTabPanel.getSelectedTabIndex() );
  }
}
