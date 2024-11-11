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
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith( GwtMockitoTestRunner.class )
public class JSTextFormatterFactoryTest {

  @Test
  public void testCreateFormatter() throws Exception {
    final String[] numberTypes = {
        "number", "java.lang.Number", "java.lang.Byte", "java.lang.Short", "java.lang.Integer", "java.lang.Long",
        "java.lang.Float", "java.lang.Double",
        "java.math.BigDecimal", "java.math.BigInteger"
    };
    final String[] dateTypes = {
        "date", "java.util.Date", "java.sql.Date", "java.sql.Time", "java.sql.Timestamp"
    };

    for ( String numberType : numberTypes ) {
      assertNotNull( JSTextFormatterFactory.createFormatter( numberType, "#" ) );
    }
    for ( String dateType : dateTypes ) {
      assertNotNull( JSTextFormatterFactory.createFormatter( dateType, "yyyy" ) );
    }

    assertNull( JSTextFormatterFactory.createFormatter( "fake formatter type", "" ) );
  }
}
