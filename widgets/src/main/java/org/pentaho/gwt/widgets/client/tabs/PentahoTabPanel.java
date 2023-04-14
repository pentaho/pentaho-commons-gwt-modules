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
 * Copyright (c) 2002-2023 Hitachi Vantara.  All rights reserved.
 */

package org.pentaho.gwt.widgets.client.tabs;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PentahoTabPanel extends VerticalPanel {

  protected FlowPanel tabBar = new FlowPanel();
  protected DeckPanel tabDeck = new AlwaysVisibleDeckPanel();
  protected PentahoTab selectedTab;

  public PentahoTabPanel() {
    setStylePrimaryName( "pentaho-tab-panel" );
    tabBar.setStylePrimaryName( "pentaho-tab-bar" );
    add( tabBar );
    Roles.getTablistRole().set( tabBar.getElement() );
    add( tabDeck );
    tabDeck.getElement().getParentElement().setClassName( "pentaho-tab-deck-panel" );
    tabDeck.setHeight( "100%" );
  }

  public void addTab( String text, String tooltip, boolean closeable, Widget content ) {
    PentahoTab tab = new PentahoTab( text, tooltip, this, content, closeable );
    tabBar.add( tab );
    tabDeck.add( content );
    if ( selectedTab == null ) {
      selectTab( tab );
    }
  }

  public void closeTab( int index, boolean invokePreTabCloseHook ) {
    closeTab( (PentahoTab) tabBar.getWidget( index ), invokePreTabCloseHook );
  }

  public void closeTab( PentahoTab closeTab, boolean invokePreTabCloseHook ) {
    int index = tabBar.getWidgetIndex( closeTab );
    tabBar.remove( closeTab );
    tabDeck.remove( closeTab.getContent() );
    // the selected tab still exists, keep it open/selected
    if ( tabBar.getWidgetIndex( selectedTab ) != -1 ) {
      return;
    }

    PentahoTab tabToSelect = null;
    int count = tabBar.getWidgetCount();
    if ( count > 0 ) {
      if ( index < count ) {
        tabToSelect = (PentahoTab) tabBar.getWidget( index );
      } else {
        tabToSelect = (PentahoTab) tabBar.getWidget( count - 1 );
      }
    }

    selectTab( tabToSelect );
  }

  public void closeOtherTabs( PentahoTab exceptThisTab ) {
    // remove from 0 -> me
    while ( exceptThisTab != getTab( 0 ) ) {
      closeTab( 0, false );
    }
    // remove from END -> me
    while ( exceptThisTab != getTab( getTabCount() - 1 ) ) {
      closeTab( getTabCount() - 1, false );
    }
    selectTab( exceptThisTab );
  }

  public void closeAllTabs() {
    while ( getTab( 0 ) != null ) {
      closeTab( 0, false );
    }
  }

  public void selectTab( PentahoTab selectedTab ) {
    selectTab( selectedTab, false );
  }

  public void selectTab( PentahoTab selectedTab, boolean setFocus ) {
    this.selectedTab = selectedTab;

    for ( int i = 0; i < tabBar.getWidgetCount(); i++ ) {
      PentahoTab tab = (PentahoTab) tabBar.getWidget( i );
      Widget tabContent = tab.getContent();
      int tabContentIndex = tabDeck.getWidgetIndex( tabContent );
      if ( tab == selectedTab ) {
        tab.setSelected( true );
        tabDeck.showWidget( tabContentIndex );
        tabContent.removeStyleName( "is-hidden" );
        tab.getElement().setTabIndex( 0 );
        if ( setFocus ) {
          tab.getElement().focus();
        }
      } else {
        tab.setSelected( false );
        tabContent.addStyleName( "is-hidden" );
        tab.getElement().setTabIndex( -1 );
      }
    }
  }

  public void selectTab( int index ) {
    selectTab( getTab( index ) );
  }

  public PentahoTab getTab( int index ) {
    if ( index >= 0 && index < tabBar.getWidgetCount() ) {
      return (PentahoTab) tabBar.getWidget( index );
    } else {
      return null;
    }
  }

  public int getTabCount() {
    return tabBar.getWidgetCount();
  }

  public int getSelectedTabIndex() {
    if ( selectedTab != null ) {
      return tabBar.getWidgetIndex( selectedTab );
    }
    return -1;
  }

  protected FlowPanel getTabBar() {
    return tabBar;
  }

  protected void setTabBar( FlowPanel tabBar ) {
    this.tabBar = tabBar;
  }

  protected DeckPanel getTabDeck() {
    return tabDeck;
  }

  protected void setTabDeck( DeckPanel tabDeck ) {
    this.tabDeck = tabDeck;
  }

  public PentahoTab getSelectedTab() {
    return selectedTab;
  }

  public void setSelectedTab( PentahoTab selectedTab ) {
    this.selectedTab = selectedTab;
  }

}
