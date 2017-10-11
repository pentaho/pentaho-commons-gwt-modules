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
