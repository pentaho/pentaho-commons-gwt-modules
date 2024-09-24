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
 * Copyright (c) 2019 Hitachi Vantara. All rights reserved.
 */

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
