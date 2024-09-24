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
