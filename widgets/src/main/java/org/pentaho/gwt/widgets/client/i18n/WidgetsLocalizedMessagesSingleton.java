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


package org.pentaho.gwt.widgets.client.i18n;

import com.google.gwt.core.client.GWT;

public class WidgetsLocalizedMessagesSingleton {

  private WidgetsLocalizedMessages MSGS = GWT.create( WidgetsLocalizedMessages.class );
  private static WidgetsLocalizedMessagesSingleton instance = new WidgetsLocalizedMessagesSingleton();

  private WidgetsLocalizedMessagesSingleton() {
  }

  public WidgetsLocalizedMessages getMessages() {
    return MSGS;
  }

  public static WidgetsLocalizedMessagesSingleton getInstance() {
    return instance;
  }
}
