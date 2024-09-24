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

import java.util.Date;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith( GwtMockitoTestRunner.class )
public class JSDateTextFormatterTest {
  private JSDateTextFormatter formatter;

  @Before
  public void setUp() throws Exception {
    formatter = new JSDateTextFormatter( "yyyy" );
    formatter.dateFormat = spy( formatter.dateFormat );
  }

  @Test
  public void testFormat() throws Exception {
    Date date = new Date();

    assertNull( formatter.format( null ) );

    try {
      formatter.format( date.getTime() );
      fail();
    } catch ( IllegalArgumentException e ) {
      // it's ok, only String accepted
    }

    formatter.format( date.getTime() + "" );

    verify( formatter.dateFormat ).format( date );
  }

  @Test
  public void testParse() throws Exception {
    assertNull( formatter.parse( null ) );
    assertNull( formatter.parse( "" ) );

    final String dateStr = "2015";
    formatter.parse( dateStr );

    verify( formatter.dateFormat ).parse( dateStr );
  }
}
