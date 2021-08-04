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
import com.google.gwt.user.client.rpc.RpcRequestBuilder;

/**
 * The <code>CsrfRpcRequestBuilder</code> class is a custom {@link RpcRequestBuilder}
 * which enables calling methods a {@link com.google.gwt.user.client.rpc.RemoteService} which are protected
 * against CSRF.
 *
 * The custom <code>RpcRequestBuilder</code> must be assigned to a
 * {@link com.google.gwt.user.client.rpc.ServiceDefTarget} before the protected calls are made.
 * For example:
 * <pre>
 * ICsvDatasourceServiceAsync service = GWT.create( ICsvDatasourceService.class );
 *
 * // Set URL and the custom CSRF RpcRequestBuilder
 * ServiceDefTarget target = (ServiceDefTarget) service;
 * target.setServiceEntryPoint( "http://localhost:8080/pentaho/gwtrpc/CsvDatasourceService"" )
 * target.setRpcRequestBuilder( new CsrfRpcRequestBuilder() );
 *
 * // Make protected method calls.
 * service.generateDomain( ... );
 * </pre>
 *
 * If not all service method calls are protected against CSRF,
 * then it is best to use {@link CsrfUtil#callProtected(Object, IConsumer)}.
 */
public class CsrfRpcRequestBuilder extends RpcRequestBuilder {
  /**
   * Creates a GWT {@link RequestBuilder} for the given endpoint
   * which makes CSRF protected GWT RPC calls.
   *
   * The returned request builder is a {@link CsrfRequestBuilder} with a <code>POST</code> method.
   *
   * @param serviceEntryPoint The service URL.
   * @return A GWT request builder.
   */
  @Override
  protected RequestBuilder doCreate( String serviceEntryPoint ) {
    return new CsrfRequestBuilder( RequestBuilder.POST, serviceEntryPoint );
  }
}
