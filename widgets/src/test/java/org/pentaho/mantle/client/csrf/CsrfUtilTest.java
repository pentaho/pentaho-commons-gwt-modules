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

package org.pentaho.mantle.client.csrf;

import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith( GwtMockitoTestRunner.class )
public class CsrfUtilTest {

  @Test
  public void testCallProtectedSetsACsrfRpcRequestBuilderBefore() {

    ServiceDefTarget serviceMock = mock( ServiceDefTarget.class );
    IConsumer<ServiceDefTarget> consumerSpy = spy( new IConsumer<ServiceDefTarget>() {
      @Override
      public void accept( ServiceDefTarget value ) {
        verify( serviceMock, times( 1 ) ).setRpcRequestBuilder( any() );
        verify( serviceMock, times( 1 ) ).setRpcRequestBuilder( any( CsrfRpcRequestBuilder.class ) );
      }
    } );

    CsrfUtil.callProtected( serviceMock, consumerSpy );

    verify( consumerSpy ).accept( serviceMock );
  }

  @Test
  public void testCallProtectedSetsRpcRequestBuilderToNullAfter() {
    ServiceDefTarget serviceMock = mock( ServiceDefTarget.class );
    IConsumer<ServiceDefTarget> consumerSpy = spy( new IConsumer<ServiceDefTarget>() {
      @Override
      public void accept( ServiceDefTarget value ) {
      }
    } );

    CsrfUtil.callProtected( serviceMock, consumerSpy );

    verify( consumerSpy ).accept( serviceMock );
    verify( serviceMock, times( 1 ) ).setRpcRequestBuilder( null );
  }
}
