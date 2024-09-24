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

package org.pentaho.gwt.widgets.client.formatter;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith( GwtMockitoTestRunner.class )
public class JSNumberTextFormatterTest {

  private JSNumberTextFormatter formatter;

  @Before
  public void setUp() throws Exception {
    formatter = spy( new JSNumberTextFormatter( "###.000" ) );
  }

  @Test
  public void testParse() throws Exception {
    assertNull( formatter.parse( null ) );
    assertNull( formatter.parse( "" ) );

    formatter.parse( "123" );
    verify( formatter ).parse( "123" );
  }

  @Test
  public void testFormat() throws Exception {
    assertNull( formatter.format( null ) );
    try {
      formatter.format( 123 );
      fail();
    } catch ( IllegalArgumentException e ) {
      // it's OK
    }

    formatter.format( "123" );
    verify( formatter ).format( "123" );

  }
}
