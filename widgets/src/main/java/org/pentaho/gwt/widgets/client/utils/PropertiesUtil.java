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

package org.pentaho.gwt.widgets.client.utils;

import java.util.HashMap;
import org.pentaho.gwt.widgets.client.utils.string.StringUtils;

/**
 * @deprecated Please use org.pentaho.gwt.widgets.client.utils.localization.PropertiesUtil instead. We are gradually
 *             moving to a more logical breakdown of GWT modules for more succinct reuse.
 */
@Deprecated
public class PropertiesUtil {

  /**
   * This method builds a HashMap out of a given input String (which is typically read from a standard java .properties
   * file). The caller may provide a default properties map which will be 'merged' with the new properties. When a
   * collision occurs, the new values always win.
   * 
   * @param text
   *          This is typically the contents of a .properties file in String form
   * @param defaultProperties
   *          A map of default settings which will be overridden if they exist in the provided input
   * @return name/value pairs for each name=value in the .properties file
   */
  @Deprecated
  public static HashMap<String, String> buildProperties( String text, HashMap<String, String> defaultProperties ) {
    // we're going to override existing settings, in this way we can override default values with locale
    // specific variants for example, we do not want to modify the user provided list
    HashMap<String, String> settings = new HashMap<String, String>();
    // add defaults to new list
    if ( defaultProperties != null ) {
      settings.putAll( defaultProperties );
    }
    if ( StringUtils.isEmpty( text ) ) {
      return settings;
    }
    StringTokenizer lineTokenizer = new StringTokenizer( text, '\n' );
    for ( int i = 0; i < lineTokenizer.countTokens(); i++ ) {
      String line = lineTokenizer.tokenAt( i );
      if ( line.indexOf( '=' ) != -1 ) {
        String key = line.substring( 0, line.indexOf( '=' ) ).trim();
        String value = line.substring( line.indexOf( '=' ) + 1 ).trim();
        settings.put( key, value );
      }
    }
    return settings;
  }

  /**
   * This method builds a HashMap out of a given input String (which is typically read from a standard java .properties
   * file).
   * 
   * @param text
   *          This is typically the contents of a .properties file in String form
   * @return name/value pairs for each name=value in the .properties file
   */
  @Deprecated
  public static HashMap<String, String> buildProperties( String text ) {
    return buildProperties( text, null );
  }

}
