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

package org.pentaho.gwt.widgets.client.utils.string;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringTokenizerTest {

  @Test
  public void testCountTokens() throws Exception {
    assertEquals( 6, new StringTokenizer( "shqwhsiu,erteryt,tryt,rytuty.utyu,tyuty", ",." ).countTokens() );
    assertEquals( 5, new StringTokenizer( "2e3_324_er_$5$_#3re", '_' ).countTokens() );
  }

  @Test
  public void testTokenAt() throws Exception {
    assertEquals( "try.t", new StringTokenizer( "shqwhsiu,erteryt,try.t,rytuty.utyu,tyuty", "," ).tokenAt( 2 ) );
  }
}
