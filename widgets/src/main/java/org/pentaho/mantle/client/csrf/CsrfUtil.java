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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This class exposes utilities for achieving CSRF protection.
 */
public class CsrfUtil {

  protected CsrfUtil() {
  }

  /**
   * Obtains a CSRF token which can be used for issuing a CSRF protected HTTP request to a given URL.
   *
   * @param url
   *          The URL of the protected endpoint.
   * @param callback
   *          An asynchronous callback which is called with the CSRF token.
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
}
