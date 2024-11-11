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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RpcRequestBuilder;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * This class exposes utilities for achieving CSRF protection.
 */
public class CsrfUtil {

  private CsrfUtil() {
  }

  /**
   * Calls the given <code>serviceCaller</code>, with the given <code>service</code> as argument,
   * after preparing the service so that any methods called within send a CSRF protection token.
   *
   * @param service       The service whose CSRF protected methods are to be called.
   *                      Must be an instance of a class which implements {@link ServiceDefTarget}.
   * @param serviceCaller The function which will call methods which are protected against CSRF attacks.
   * @see ServiceDefTarget#setRpcRequestBuilder(RpcRequestBuilder)
   * @see CsrfRpcRequestBuilder
   */
  public static <T> void callProtected( T service, IConsumer<T> serviceCaller ) {
    ServiceDefTarget target = (ServiceDefTarget) service;
    target.setRpcRequestBuilder( new CsrfRpcRequestBuilder() );
    try {
      serviceCaller.accept( service );
    } finally {
      target.setRpcRequestBuilder( null );
    }
  }

  /**
   * Obtains a CSRF token which can be used for issuing a CSRF protected HTTP request to a given URL.
   *
   * @param url      The URL of the protected endpoint.
   * @param callback An asynchronous callback which is called with the CSRF token.
   */
  public static final native void getCsrfToken( String url, final AsyncCallback<JsCsrfToken> callback ) /*-{

    $wnd.require(["pentaho/csrf/service"], function(csrfService) {
      var token = csrfService.getToken(url);

      callback.@com.google.gwt.user.client.rpc.AsyncCallback::onSuccess(Ljava/lang/Object;)(token);
    }, function(error) {
      var ex = @com.google.gwt.core.client.JavaScriptException::new(Ljava/lang/Object;)(error);
      callback.@com.google.gwt.user.client.rpc.AsyncCallback::onFailure(Ljava/lang/Throwable;)(ex);
    });

  }-*/;

  /**
   * Obtains a CSRF token synchronously which can be used for issuing a CSRF protected HTTP request to a given URL.
   *
   * @param url The URL of the protected endpoint.
   * @return The CSRF token or {@code null}.
   */
  public static final native JsCsrfToken getCsrfTokenSync( String url ) /*-{
    return $wnd.pho.csrfUtil.getToken(url);
  }-*/;
}
