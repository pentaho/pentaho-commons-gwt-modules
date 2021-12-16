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
 * Copyright (c) 2002-2021 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.mantle.client.environment;

public class EnvironmentHelper {
  private EnvironmentHelper() {
    // Disallow instantiation.
  }

  /**
   * Gets the context path of the Pentaho application, relative to its web server.
   * Example: <code>/pentaho/</code>
   * @return The context path of the Pentaho application.
   */
  public static native String getContextPath()/*-{
    return $wnd.CONTEXT_PATH || "";
  }-*/;

  /**
   * Gets the fully qualified base URL of the Pentaho server environment.
   * Example: <code>http://locahost:8080/pentaho/</code>
   * @return The fully qualified base URL.
   */
  public static native String getFullyQualifiedURL()/*-{
    return $wnd.location.protocol + "//" + $wnd.location.host + $wnd.CONTEXT_PATH;
  }-*/;
}
