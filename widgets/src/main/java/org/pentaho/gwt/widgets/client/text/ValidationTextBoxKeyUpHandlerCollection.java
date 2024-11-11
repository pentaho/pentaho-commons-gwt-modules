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


package org.pentaho.gwt.widgets.client.text;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

import java.util.ArrayList;

public class ValidationTextBoxKeyUpHandlerCollection extends ArrayList<KeyUpHandler> {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Fires a onKeyUp event to all handlers
   * 
   * @param event
   *          the widget sending the event.
   */
  public void fireOnKeyUp( KeyUpEvent event ) {
    for ( KeyUpHandler handler : this ) {
      handler.onKeyUp( event );
    }
  }
}
