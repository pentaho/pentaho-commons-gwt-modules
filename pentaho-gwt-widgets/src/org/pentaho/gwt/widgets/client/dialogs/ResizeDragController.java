/*
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
 * Portions Copyright 2008-2013 Pentaho Corporation.  All rights reserved.
 * Portions Copyright 2009 - Fred Sauer
 */

package org.pentaho.gwt.widgets.client.dialogs;

import com.allen_sauer.gwt.dnd.client.AbstractDragController;
import com.allen_sauer.gwt.dnd.client.drop.BoundaryDropController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import org.pentaho.gwt.widgets.client.dialogs.WindowPanel.DirectionConstant;

import java.util.HashMap;

final class ResizeDragController extends AbstractDragController {

  private static final int MIN_WIDGET_SIZE = 20;

  private HashMap<Widget, DirectionConstant> directionMap = new HashMap<Widget, DirectionConstant>();

  private WindowPanel windowPanel = null;

  public ResizeDragController( AbsolutePanel boundaryPanel ) {
    super( boundaryPanel );
  }

  public void dragMove() {
    int direction = ( (ResizeDragController) context.dragController ).getDirection( context.draggable ).directionBits;
    if ( ( direction & WindowPanel.DIRECTION_NORTH ) != 0 ) {
      int delta = context.draggable.getAbsoluteTop() - context.desiredDraggableY;
      if ( delta != 0 ) {
        int contentHeight = windowPanel.getContentHeight();
        int newHeight = Math.max( contentHeight + delta, MIN_WIDGET_SIZE );
        if ( newHeight != contentHeight ) {
          windowPanel.moveBy( 0, contentHeight - newHeight );
        }
        windowPanel.setContentSize( windowPanel.getContentWidth(), newHeight );
      }
    } else if ( ( direction & WindowPanel.DIRECTION_SOUTH ) != 0 ) {
      int delta = context.desiredDraggableY - context.draggable.getAbsoluteTop();
      if ( delta != 0 ) {
        windowPanel.setContentSize( windowPanel.getContentWidth(), windowPanel.getContentHeight() + delta );
      }
    }
    if ( ( direction & WindowPanel.DIRECTION_WEST ) != 0 ) {
      int delta = context.draggable.getAbsoluteLeft() - context.desiredDraggableX;
      if ( delta != 0 ) {
        int contentWidth = windowPanel.getContentWidth();
        int newWidth = Math.max( contentWidth + delta, MIN_WIDGET_SIZE );
        if ( newWidth != contentWidth ) {
          windowPanel.moveBy( contentWidth - newWidth, 0 );
        }
        windowPanel.setContentSize( newWidth, windowPanel.getContentHeight() );
      }
    } else if ( ( direction & WindowPanel.DIRECTION_EAST ) != 0 ) {
      int delta = context.desiredDraggableX - context.draggable.getAbsoluteLeft();
      if ( delta != 0 ) {
        windowPanel.setContentSize( windowPanel.getContentWidth() + delta, windowPanel.getContentHeight() );
      }
    }
  }

  @Override
  public void dragStart() {
    super.dragStart();
    windowPanel = (WindowPanel) context.draggable.getParent().getParent();
  }

  public void makeDraggable( Widget widget, WindowPanel.DirectionConstant direction ) {
    super.makeDraggable( widget );
    directionMap.put( widget, direction );
  }

  protected BoundaryDropController newBoundaryDropController( AbsolutePanel boundaryPanel,
      boolean allowDroppingOnBoundaryPanel ) {
    if ( allowDroppingOnBoundaryPanel ) {
      throw new IllegalArgumentException();
    }
    return new BoundaryDropController( boundaryPanel, false );
  }

  private DirectionConstant getDirection( Widget draggable ) {
    return directionMap.get( draggable );
  }
}
