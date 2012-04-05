package org.pentaho.gwt.widgets.client.text;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.Widget;

public class ValidationTextBoxListenerCollection extends ArrayList<IValidationTextBoxListener> {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Fires a onSuccess event to all listener
   * 
   * @param Widget that the event is being fired.
   */
  public void fireOnSuccess(Widget widget) {
    for (IValidationTextBoxListener listener : this) {
      listener.onSuccess(widget);
    }
  }
  
  /**
   * Fires a onFailure event to all listener
   * 
   * @param Widget that the event is being fired.
   */
  public void fireOnFailure(Widget widget) {
    for (IValidationTextBoxListener listener : this) {
      listener.onFailure(widget);
    }
  }
}
