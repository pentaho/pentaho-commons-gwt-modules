package org.pentaho.gwt.widgets.client.utils;

/**
 * Emulates the class in java.text enough to capture the error position.
 * @author mbatchelor
 *
 */
public class ParseException extends Exception {

  private final int errorPos;

  public ParseException(String message, int pos) {
    super(message);
    errorPos = pos;
  }

  public ParseException(String message) {
    super(message);
    errorPos = 0;
  }
  
}
