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

import com.google.gwt.core.client.JavaScriptObject;

/**
 * This class exposes the information required to specify a CSRF token in an HTTP operation.
 */
public class JsCsrfToken extends JavaScriptObject {

  protected JsCsrfToken() {
  }

  /**
   * Gets the name of the request header to use to send the token in, when an HTTP request header is used.
   */
  public final native String getHeader() /*-{ return this.header; }-*/;

  /**
   * Sets the name of the request header to use to send the token in, when an HTTP request header is used.
   *
   * @param header
   *          The header name.
   */
  public final native void setHeader( String header ) /*-{ this.header = header; }-*/;

  /**
   * Gets the name of the request parameter to use to send the token in, when an HTTP request parameter is used.
   */
  public final native String getParameter() /*-{ return this.parameter; }-*/;

  /**
   * Sets the name of the request parameter to use to send the token in, when an HTTP request parameter is used.
   *
   * @param parameter
   *          The parameter name.
   */
  public final native void setParameter( String parameter ) /*-{ this.parameter = parameter; }-*/;

  /**
   * Gets the CSRF token value.
   */
  public final native String getToken() /*-{ return this.token; }-*/;

  /**
   * Sets the CSRF token value.
   *
   * @param token
   *          Then token value.
   */
  public final native void setToken( String token ) /*-{ this.token = token; }-*/;
}
