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
 * Copyright (c) 2021 Hitachi Vantara. All rights reserved.
 */

package org.pentaho.mantle.client.csrf;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;

@RunWith( GwtMockitoTestRunner.class )
public class CsrfRpcRequestBuilderTest {

  private static final String TEST_URL = "http://localhost/test";

  @Test
  public void testDoCreateReturnsACsrfRequestBuilderWithPOSTAndGivenURL() {
    // Wasn't able to spy on CsrfRpcRequestBuilder and then call finish.
    // The "unstubbable" doFinish would run and throw due to environmental problems.
    CsrfRpcRequestBuilder rpcBuilder = mock( CsrfRpcRequestBuilder.class );

    doCallRealMethod().when( rpcBuilder ).doCreate( TEST_URL );

    RequestBuilder builder = rpcBuilder.doCreate( TEST_URL );

    assertNotNull( builder );
    assertTrue( builder instanceof CsrfRequestBuilder );
    assertEquals( RequestBuilder.POST.toString(), builder.getHTTPMethod() );
    assertEquals( TEST_URL, builder.getUrl() );
  }
}
