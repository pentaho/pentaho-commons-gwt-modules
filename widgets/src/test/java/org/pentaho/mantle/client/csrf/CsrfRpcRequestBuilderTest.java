/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
