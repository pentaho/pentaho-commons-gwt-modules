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

import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;

public class ValidationTextBoxListenerCollection extends ArrayList<IValidationTextBoxListener> {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Fires a onSuccess event to all listener
   * 
   * @param Widget
   *          that the event is being fired.
   */
  public void fireOnSuccess( Widget widget ) {
    for ( IValidationTextBoxListener listener : this ) {
      listener.onSuccess( widget );
    }
  }

  /**
   * Fires a onFailure event to all listener
   * 
   * @param Widget
   *          that the event is being fired.
   */
  public void fireOnFailure( Widget widget ) {
    for ( IValidationTextBoxListener listener : this ) {
      listener.onFailure( widget );
    }
  }
}
