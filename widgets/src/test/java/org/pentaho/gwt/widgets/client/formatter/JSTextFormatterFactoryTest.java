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
