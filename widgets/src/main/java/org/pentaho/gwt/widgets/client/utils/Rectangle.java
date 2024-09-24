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

package org.pentaho.gwt.widgets.client.utils;

public class Rectangle {
  public int x, y, width, height;

  public Rectangle() {
  }

  public Rectangle( int x, int y, int width, int height ) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public boolean intersects( Rectangle r ) {
    return ( pointWithin( new Point( r.x, r.y ) )
        || pointWithin( new Point( r.x + r.width, r.y ) )
        || pointWithin( new Point( r.x, r.y + r.height ) )
        || pointWithin( new Point( r.x + r.width, r.y + r.height ) ) )
        || ( r.pointWithin( new Point( this.x, this.y ) )
            || r.pointWithin( new Point( this.x + this.width, this.y ) )
            || r.pointWithin( new Point( this.x, this.y + this.height ) )
            || r.pointWithin( new Point( this.x + this.width, this.y + this.height ) ) );

  }

  private boolean pointWithin( Point p ) {
    if ( ( p.x > this.x && p.x < this.x + this.width ) && ( p.y > this.y && p.y < this.y + this.height ) ) {
      return true;
    }
    return false;
  }

  private class Point {
    int x, y;

    public Point( int x, int y ) {
      this.x = x;
      this.y = y;
    }
  }
}
