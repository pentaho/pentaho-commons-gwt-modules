package org.pentaho.gwt.widgets.client.text;

import com.google.gwt.user.client.ui.Widget;

public interface IValidationTextBoxListener {

  public void onSuccess(Widget widget);
  
  public void onFailure(Widget widget);
}
