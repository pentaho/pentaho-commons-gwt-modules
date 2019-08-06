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
 * Copyright (c) 2002-2019 Hitachi Vantara.  All rights reserved.
 */

package org.pentaho.gwt.widgets.client.tabs;

import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: nbaker Date: 2/7/13
 */
public class AlwaysVisibleDeckPanel extends DeckPanel {
  Widget curWidget;

  public AlwaysVisibleDeckPanel() {

  }

  public void showWidget( int index ) {
    checkIndexBoundsForAccess( index );
    Widget oldWidget = curWidget;
    curWidget = getWidget( index );

    if ( curWidget != oldWidget && oldWidget != null ) {
      moveOffscreen( oldWidget );
    }

    // set the sizes to 100% to account for any window resizing.
    curWidget.getElement().getParentElement().getStyle().setProperty( "width", "100%" );
    curWidget.getElement().getParentElement().getStyle().setProperty( "height", "100%" );
    curWidget.getElement().getParentElement().getStyle().setProperty( "position", "relative" );
    curWidget.getElement().getParentElement().getStyle().setProperty( "left", "0" );
  }

  protected void moveOffscreen( Widget w ) {
    if ( w.getElement() == null || w.getElement().getParentElement() == null ) { // old active widget was removed.
      return;
    }

    if ( w.getParent().getOffsetWidth() > 0 && w.getParent().getOffsetHeight() > 0 ) {
      int width = w.getElement().getParentElement().getOffsetWidth();
      int height = w.getElement().getParentElement().getOffsetHeight();

      // don't let the switching between relative and absolute positioning modify our size.
      // force the size with inline styles, we'll set it back to 100% when we show it again
      w.getElement().getParentElement().getStyle().setProperty( "width", width + "px" );
      w.getElement().getParentElement().getStyle().setProperty( "height", height + "px" );
    }

    w.getElement().getParentElement().getStyle().setProperty( "position", "absolute" );
    w.getElement().getParentElement().getStyle().setProperty( "left", "-5000px" );
  }

  @Override
  public int getVisibleWidget() {
    return curWidget != null ? this.getWidgetIndex( curWidget ) : -1;
  }

  @Override
  public void add( Widget w ) {
    super.add( w );
    w.setVisible( true );
    w.getElement().getParentElement().getStyle().setProperty( "display", "" );
    moveOffscreen( w );
  }

  @Override
  public boolean remove( Widget w ) {

    if ( curWidget == w ) {
      curWidget = null;
    }

    return super.remove( w );
  }
}
