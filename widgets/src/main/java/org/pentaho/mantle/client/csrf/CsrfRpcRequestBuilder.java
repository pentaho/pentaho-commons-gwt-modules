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
