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
