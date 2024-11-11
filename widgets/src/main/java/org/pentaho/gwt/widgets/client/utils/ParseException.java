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

/**
 * Emulates the class in java.text enough to capture the error position.
 * 
 * @author mbatchelor
 * 
 */
public class ParseException extends Exception {

  public ParseException( String message, int pos ) {
    super( message );
  }

  public ParseException( String message ) {
    super( message );
  }

}
