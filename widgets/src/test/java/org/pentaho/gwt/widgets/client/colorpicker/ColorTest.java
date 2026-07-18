/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



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
