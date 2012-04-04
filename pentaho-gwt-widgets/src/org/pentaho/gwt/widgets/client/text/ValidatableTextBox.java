package org.pentaho.gwt.widgets.client.text;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

public class ValidatableTextBox extends HorizontalPanel implements IValidatableTextBox{

  private TextBox textBox;
  
  private PopupPanel popupPanel;
  
  private Label validationMessageLabel;
  
  private String validationMessage;
  
  private SimplePanel imagePanel;
  
  private Image image;
  
  private ValidatableTextBoxKeyUpHandlerCollection handlers;
  
  private ValidatableTextBoxListenerCollection listeners;
  
  private static final int DEFAULT_OFFSET = 5;
  
  public ValidatableTextBox() {
    textBox = new TextBox();
    textBox.addKeyUpHandler(new KeyUpHandler() {
      
      @Override
      public void onKeyUp(KeyUpEvent event) {
        performValidation();
        fireOnKeyUp(event);
      }
    });

    imagePanel = new SimplePanel();
    imagePanel.setStylePrimaryName("validation-image-panel"); //$NON-NLS-1$
    this.add(textBox);
    textBox.setWidth("100%");
    this.setCellWidth(textBox, "100%");
    this.setStylePrimaryName("custom-text-box"); //$NON-NLS-1$
    SimplePanel hSpacer = new SimplePanel();
    hSpacer.setWidth("10px");
    this.add(hSpacer);
    validationMessageLabel = new Label();
    validationMessageLabel.setStyleName("validation-message-label"); //$NON-NLS-1$
    image = new Image(GWT.getModuleBaseURL() + "images/spacer.gif"); //$NON-NLS-1$
    image.setStylePrimaryName("validation-image");
    imagePanel.add(image);
    imagePanel.addStyleDependentName("invalid");
    this.add(imagePanel);
  }
  
  public void addKeyUpHandler(KeyUpHandler handler) {
    if (handlers == null) {
      handlers = new ValidatableTextBoxKeyUpHandlerCollection();
    }
    handlers.add(handler);
  }

  public void removeKeyUpHandler(KeyUpHandler handler) {
    if (handlers != null) {
      handlers.remove(handler);
    }
  }

  /**
   * Fire all current {@link KeyUpHandler}.
   */
  void fireOnKeyUp(KeyUpEvent event) {

    if (handlers != null) {
      handlers.fireOnKeyUp(event);
    }
  }

  public void addValidatableTextBoxListener(IValidatableTextBoxListener listener) {
    if (listeners == null) {
      listeners = new ValidatableTextBoxListenerCollection();
    }
    listeners.add(listener);
  }

  public void removeValidatableTextBoxListener(IValidatableTextBoxListener listener) {
    if (listeners != null) {
      listeners.remove(listener);
    }
  }

  /**
   * Fire all current {@link IValidatableTextBoxListener}.
   */
  void fireOnSuccess() {
    if (listeners != null) {
      listeners.fireOnSuccess(this);
    }
  }

  /**
   * Fire all current {@link IValidatableTextBoxListener}.
   */
  void fireOnFailure() {
    if (listeners != null) {
      listeners.fireOnFailure(this);
    }
  }
  
  private void performValidation() {
    if(!validate()) {
      fireOnFailure();
      imagePanel.removeStyleDependentName("valid");
      imagePanel.addStyleDependentName("invalid");
      validationMessageLabel.setText(getValidationMessage());
      popupPanel = new PopupPanel(true, false);
      popupPanel.setWidget(validationMessageLabel);
      popupPanel.setPopupPositionAndShow(new PositionCallback() {
        public void setPosition(int offsetWidth, int offsetHeight) {
          int absLeft = -1;
          int absTop = -1;
          int offHeight = -1;
          absLeft = textBox.getAbsoluteLeft();
          absTop = textBox.getAbsoluteTop();
          offHeight = textBox.getOffsetHeight();
          popupPanel.setPopupPosition(absLeft, absTop - offHeight - DEFAULT_OFFSET >= 0 ? absTop - offHeight - DEFAULT_OFFSET: absTop);
        }
      });      
      popupPanel.show();
    } else {
      fireOnSuccess();
      imagePanel.removeStyleDependentName("invalid");
      imagePanel.addStyleDependentName("valid"); 
      popupPanel.hide();
    }
  }
  
  @Override
  public boolean validate() {
    // TODO Auto-generated method stub
    return false;
  }
  
  public String getValidationMessage() {
    return validationMessage;
  }
  public void setValidationMessage(String validationMessage) {
    this.validationMessage = validationMessage;
  }
  public TextBox getManagedObject() {
    return textBox;
  }
  
  public String getValue() {
    return textBox.getValue();
  }
  
  public void setValue(String value) {
    textBox.setValue(value);
    performValidation();
  }
  
  public String getText() {
    return textBox.getText();
  }
}
