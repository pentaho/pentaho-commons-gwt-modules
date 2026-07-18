/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.gwt.widgets.login.client;

import java.io.Serializable;

public class AuthenticationCanceledException extends Exception implements Serializable {

  private String msg = null;
  /**
     * 
     */
  private static final long serialVersionUID = 69L;

  public AuthenticationCanceledException() {
    super();
  }

  public AuthenticationCanceledException( String message ) {
    super( message );
    msg = message;
  }

  public AuthenticationCanceledException( String message, Throwable cause ) {
    super( message, cause );
    msg = message;
  }

  public AuthenticationCanceledException( Throwable cause ) {
    super( cause );
    msg = cause.getMessage();
  }

  public String getMessage() {
    return msg;
  }
}
