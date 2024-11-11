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

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;

/**
 * The <code>CsrfRequestBuilder</code> class is a GWT {@link RequestBuilder}
 * which adds a CSRF token header.
 *
 * The CSRF token is obtained, for the URL {@link #getUrl()},
 * right before the actual request is sent,
 * by using {@link CsrfUtil#getCsrfTokenSync(String)}.
 *
 * This class should be used, instead of the normal {@link RequestBuilder},
 * to call plain REST endpoints which are protected against CSRF attacks.
 */
public class CsrfRequestBuilder extends RequestBuilder {

  public CsrfRequestBuilder( Method httpMethod, String url ) {
    super( httpMethod, url );
  }

  @Override
  public Request send() throws RequestException {
    setupCsrfToken();
    return callSuperSend();
  }

  // Visible For Testing
  Request callSuperSend() throws RequestException {
    return super.send();
  }

  @Override
  public Request sendRequest( String requestData, RequestCallback callback ) throws RequestException {
    setupCsrfToken();
    return callSuperSendRequest( requestData, callback );
  }

  // Visible For Testing
  Request callSuperSendRequest( String requestData, RequestCallback callback ) throws RequestException {
    return super.sendRequest( requestData, callback );
  }

  /**
   * Obtains the CSRF token for the current request and, if one is returned,
   * sets the corresponding header for it.
   */
  protected void setupCsrfToken() throws RequestException {
    JsCsrfToken token = getJsCsrfToken();
    if ( token != null ) {
      setHeader( token.getHeader(), token.getToken() );
    }
  }

  /**
   * Obtains the CSRF token for the current request and returns it.
   * @return The CSRF token, if any; <code>null</code>, if none.
   */
  protected JsCsrfToken getJsCsrfToken() throws RequestException {
    try {
      return getJsCsrfTokenSync( getUrl() );
    } catch ( JavaScriptException e ) {
      throw new RequestException( "Error obtaining CSRF token", e );
    }
  }

  // Visible For Testing
  JsCsrfToken getJsCsrfTokenSync( String url ) {
    return CsrfUtil.getCsrfTokenSync( url );
  };
}
