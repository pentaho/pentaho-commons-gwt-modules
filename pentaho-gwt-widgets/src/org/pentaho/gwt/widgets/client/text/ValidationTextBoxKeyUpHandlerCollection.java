package org.pentaho.gwt.widgets.client.text;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

public class ValidationTextBoxKeyUpHandlerCollection extends ArrayList<KeyUpHandler> {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Fires a onKeyUp event to all handlers
   * 
   * @param event the widget sending the event.
   */
  public void fireOnKeyUp(KeyUpEvent event) {
    for (KeyUpHandler handler : this) {
      handler.onKeyUp(event);
    }
  }
}
