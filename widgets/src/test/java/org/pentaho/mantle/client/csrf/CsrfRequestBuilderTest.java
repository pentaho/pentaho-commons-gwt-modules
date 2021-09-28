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

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith( GwtMockitoTestRunner.class )
public class CsrfRequestBuilderTest {

  private static final String TEST_URL = "http://localhost/test";
  private static final String TEST_TOKEN = "ABC";
  private static final String TEST_TOKEN_HEADER = "CSRF_HEADER";
  private static final String TEST_REQUEST_DATA = "123";
  private static final RequestCallback TEST_REQUEST_CALLBACk = new RequestCallback() {
    @Override
    public void onResponseReceived( Request request, Response response ) {
    }

    @Override
    public void onError( Request request, Throwable exception ) {
    }
  };

  CsrfRequestBuilder builder;
  Request requestMock;
  JsCsrfToken tokenMock;

  @Before
  public void setUp() throws Exception {
    builder = spy( new CsrfRequestBuilder( RequestBuilder.GET, TEST_URL ) );

    requestMock = mock( Request.class );

    // CSRF Token
    tokenMock = mock( JsCsrfToken.class );
    when( tokenMock.getToken() ).thenReturn( TEST_TOKEN );
    when( tokenMock.getHeader() ).thenReturn( TEST_TOKEN_HEADER );

    // Stub methods.
    doReturn( requestMock ).when( builder ).callSuperSend();
    doReturn( requestMock ).when( builder ).callSuperSendRequest( anyString(), any( RequestCallback.class ) );
    doReturn( tokenMock ).when( builder ).getJsCsrfTokenSync( anyString() );
  }

  @Test
  public void testSendGetsTokenAndSetsHeaderWhenToken() throws Exception {
    Request result = builder.send();

    verify( builder ).getJsCsrfToken();
    verify( builder ).getJsCsrfTokenSync( TEST_URL );
    verify( builder ).setHeader( TEST_TOKEN_HEADER, TEST_TOKEN );
    verify( builder ).callSuperSend();

    assertEquals( requestMock, result );
  }

  @Test
  public void testSendGetsTokenAndNotSetsHeaderWhenNotToken() throws Exception {

    doReturn( null ).when( builder ).getJsCsrfTokenSync( TEST_URL );

    Request result = builder.send();

    verify( builder ).getJsCsrfToken();
    verify( builder ).getJsCsrfTokenSync( TEST_URL );
    verify( builder, never() ).setHeader( TEST_TOKEN_HEADER, TEST_TOKEN );
    verify( builder ).callSuperSend();

    assertEquals( requestMock, result );
  }

  @Test
  public void testSendThrowsRequestExceptionWhenGetTokenFails() throws Exception {

    JavaScriptException jsError = new JavaScriptException( null );

    doThrow( jsError ).when( builder ).getJsCsrfTokenSync( TEST_URL );

    try {
      builder.send();
      fail();
    } catch ( RequestException ex ) {
      assertEquals( jsError, ex.getCause() );
    }
  }

  @Test
  public void testSendRequestGetsTokenAndSetsHeaderWhenToken() throws Exception {

    Request result = builder.sendRequest( TEST_REQUEST_DATA, TEST_REQUEST_CALLBACk );

    verify( builder ).getJsCsrfToken();
    verify( builder ).getJsCsrfTokenSync( TEST_URL );
    verify( builder ).setHeader( TEST_TOKEN_HEADER, TEST_TOKEN );
    verify( builder ).callSuperSendRequest( TEST_REQUEST_DATA, TEST_REQUEST_CALLBACk );

    assertEquals( requestMock, result );

  }

  @Test
  public void testSendRequestGetsTokenAndNotSetsHeaderWhenNotToken() throws Exception {

    doReturn( null ).when( builder ).getJsCsrfTokenSync( TEST_URL );

    Request result = builder.sendRequest( TEST_REQUEST_DATA, TEST_REQUEST_CALLBACk );

    verify( builder ).getJsCsrfToken();
    verify( builder ).getJsCsrfTokenSync( TEST_URL );
    verify( builder, never() ).setHeader( TEST_TOKEN_HEADER, TEST_TOKEN );
    verify( builder ).callSuperSendRequest( TEST_REQUEST_DATA, TEST_REQUEST_CALLBACk );

    assertEquals( requestMock, result );
  }

  @Test
  public void testSendRequestThrowsRequestExceptionWhenGetTokenFails() throws Exception {

    JavaScriptException jsError = new JavaScriptException( null );

    doThrow( jsError ).when( builder ).getJsCsrfTokenSync( TEST_URL );

    try {
      builder.sendRequest( TEST_REQUEST_DATA, TEST_REQUEST_CALLBACk );
      fail();
    } catch ( RequestException ex ) {
      assertEquals( jsError, ex.getCause() );
    }
  }
}
