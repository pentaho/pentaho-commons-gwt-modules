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

package org.pentaho.gwt.widgets.client.colorpicker;

import org.junit.Test;

import static org.junit.Assert.*;

public class ColorTest {
  @Test
  public void testColor() throws Exception {
    Color c = new Color();

    c.setHSV( 116, 99, 91 );
    check( c );

    c.setRGB( 18, 232, 2 );
    check( c );

    c.setHex( "12e802" );
    check( c );
  }

  private void check( Color c ) {
    assertEquals( 18, c.getRed() );
    assertEquals( 232, c.getGreen() );
    assertEquals( 2, c.getBlue() );
    assertEquals( "12e802", c.getHex() );
    assertEquals( 116, c.getHue() );
    assertEquals( 99, c.getSaturation() );
    assertEquals( 91, c.getValue() );
  }
}
