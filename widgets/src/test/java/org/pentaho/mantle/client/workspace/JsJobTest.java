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
 * Copyright (c) 2002-2018 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.mantle.client.workspace;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith( GwtMockitoTestRunner.class )
public class JsJobTest {

  private JsJob jsJob;

  @Before
  public void setUp() throws Exception {
    jsJob = mock( JsJob.class );
  }

  @Test
  public void getFullResourceName_nullParam() {

    doCallRealMethod().when( jsJob ).getFullResourceName();

    // test JobParamValue = null
    when( jsJob.getJobParamValue( "ActionAdapterQuartzJob-StreamProvider" ) ).thenReturn( null );
    String resourceName = jsJob.getFullResourceName();

    verify( jsJob, times( 1 ) ).getJobName();
    assertNull( resourceName );
  }

  @Test
  public void getFullResourceName_validParam() {

    doCallRealMethod().when( jsJob ).getFullResourceName();

    // test JobParamValue = some_valid_value
    when( jsJob.getJobParamValue( "ActionAdapterQuartzJob-StreamProvider" ) )
      .thenReturn( ":inputFile = /some_valid_value:outputFile = /another_valid_value" );
    String resourceName = jsJob.getFullResourceName();
    
    assertEquals( "/some_valid_value", resourceName );
  }
}
