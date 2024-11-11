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

import java.io.Serializable;

public class AuthenticationFailedException extends Exception implements Serializable {

  private String msg = null;
  /**
     * 
     */
  private static final long serialVersionUID = 69L;

  public AuthenticationFailedException() {
    super();
  }

  public AuthenticationFailedException( String message ) {
    super( message );
    msg = message;
  }

  public AuthenticationFailedException( String message, Throwable cause ) {
    super( message, cause );
    msg = message;
  }

  public AuthenticationFailedException( Throwable cause ) {
    super( cause );
    msg = cause.getMessage();
  }

  public String getMessage() {
    return msg;
  }
}
