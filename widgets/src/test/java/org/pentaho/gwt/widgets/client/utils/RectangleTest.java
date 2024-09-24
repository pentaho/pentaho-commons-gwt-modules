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

import org.junit.Test;

import static org.junit.Assert.*;

public class RectangleTest {

  @Test
  public void testIntersects() throws Exception {
    Rectangle rectangle = new Rectangle( 20, 20, 10, 10 );
    assertTrue( rectangle.intersects( new Rectangle( 0, 0, 25, 25 ) ) );
    assertTrue( rectangle.intersects( new Rectangle( 20, 20, 5, 5 ) ) );
    assertTrue( rectangle.intersects( new Rectangle( 25, 25, 25, 25 ) ) );
    assertFalse( rectangle.intersects( new Rectangle( 0, 0, 5, 5 ) ) );
    assertFalse( rectangle.intersects( new Rectangle( 35, 35, 25, 25 ) ) );
  }
}
