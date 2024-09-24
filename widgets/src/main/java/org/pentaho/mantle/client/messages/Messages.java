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

package org.pentaho.mantle.client.messages;

import org.pentaho.gwt.widgets.client.utils.i18n.ResourceBundle;

public class Messages {

  private static ResourceBundle messageBundle;

  public static String getString( String key ) {
    if ( messageBundle == null ) {
      return key;
    }
    return messageBundle.getString( key );
  }

  public static String getString( String key, String... parameters ) {
    if ( messageBundle == null ) {
      return key;
    }
    return messageBundle.getString( key, null, parameters );
  }

  public static ResourceBundle getResourceBundle() {
    return messageBundle;
  }

  public static void setResourceBundle( ResourceBundle messageBundle ) {
    Messages.messageBundle = messageBundle;
  }

}
