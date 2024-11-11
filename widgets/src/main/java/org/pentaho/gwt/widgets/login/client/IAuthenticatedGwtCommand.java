/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.gwt.widgets.login.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This interface needs to be implemented by the service using the AuthenticatedGwtService wrapper and put any work then
 * needs to be performed when the user selects the service
 */
public interface IAuthenticatedGwtCommand<T> {
  /**
   * Work to be done.
   * 
   * @param AsyncCallback
   *          - call back
   */
  public void execute( AsyncCallback<T> callback );
}
