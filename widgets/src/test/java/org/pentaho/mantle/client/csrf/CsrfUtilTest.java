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
