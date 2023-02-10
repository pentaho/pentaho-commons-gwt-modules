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
 * Copyright (c) 2023 Hitachi Vantara. All rights reserved.
 */

package org.pentaho.gwt.widgets.client.utils;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class FlexTableDocumentOrderIterator implements Iterator<Widget> {

  private static class Cell {
    public final int row;
    public final int column;
    public final Widget widget;

    public Cell( int row, int column, Widget widget ) {
      assert row >= 0;
      assert column >= 0;
      assert widget != null;

      this.row = row;
      this.column = column;
      this.widget = widget;
    }
  }

  private final FlexTable table;
  private Cell lastCell;
  private Cell nextCell;

  public FlexTableDocumentOrderIterator( FlexTable table ) {
    if ( table == null ) {
      throw new IllegalArgumentException( "table cannot be null" );
    }

    this.table = table;
    this.nextCell = findNextCell( -1, -1 );
  }

  private Cell findNextCell( int row, int column ) {
    while ( true ) {
      // When initializing, row is -1.
      if ( row >= 0 ) {
        // Exhaust current row's cells.
        Cell nextCellOfRow = findNextCellOfRow( row, column );
        if ( nextCellOfRow != null ) {
          return nextCellOfRow;
        }
      }

      // Advance to next row.
      if ( ++row >= table.getRowCount() ) {
        // There is no next row.
        return null;
      }

      // Reset column.
      column = -1;
    }
  }

  private Cell findNextCellOfRow( final int row, int col ) {
    int colCount = table.getCellCount( row );
    while ( ++col < colCount ) {
      Widget widget = table.getWidget( row, col );
      if ( widget != null ) {
        return new Cell( row, col, widget );
      }
    }

    return null;
  }

  @Override
  public boolean hasNext() {
    return nextCell != null;
  }

  @Override
  public Widget next() {
    if ( nextCell == null ) {
      throw new NoSuchElementException();
    }

    lastCell = nextCell;
    nextCell = findNextCell( nextCell.row, nextCell.column );

    return lastCell.widget;
  }

  @Override
  public void remove() {
    if ( lastCell == null ) {
      throw new IllegalStateException();
    }

    table.remove( lastCell.widget );
    lastCell = null;
  }
}
