package org.pentaho.gwt.widgets.client.buttons;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ProgressIndicatorWidget extends SimplePanel {

  public static final int STOP_DELAY_TIMER = 500;
  public static final int EXPIRATION_DURATION = 1000 * 60;
  public static final int PRESSED_DECK = 1;
  public static final int UNPRESSED_DECK = 0;
  protected int currentShownWidget; 
  protected Widget managedObject;
  protected DeckPanel buttonPanel;
  

  public ProgressIndicatorWidget(Widget widget) {
    managedObject = widget;
    buttonPanel = new DeckPanel();
    buttonPanel.add(managedObject);
    buttonPanel.setWidth("100%");
    buttonPanel.setHeight("100%");
    managedObject.setWidth("auto");
    managedObject.setStylePrimaryName("pentaho-button");
    Image image = new Image(GWT.getModuleBaseURL() + "images/progress_spinner.gif"); //$NON-NLS-1$
    image.setStylePrimaryName("progress-image");
    image.setWidth("auto");
    image.setHeight("auto");
    buttonPanel.add(image);
    buttonPanel.setStylePrimaryName("progress-indicator-button");
    this.setWidget(buttonPanel);
    this.setHeight("100%");
    this.setWidth("auto");
    buttonPanel.showWidget(UNPRESSED_DECK);
  }
  public void inProgress(boolean inProgress) {
    if(inProgress) {
      start();
    } else {
      stop();
    }
  }
  private void start() {
    if(currentShownWidget == UNPRESSED_DECK) {
      buttonPanel.showWidget(PRESSED_DECK);
      setCurrentShownWidget(PRESSED_DECK);
      Timer timer = new Timer() {
        public void run() {
          stop();
        }
      };
      timer.schedule(EXPIRATION_DURATION);
    }
  }
  
  private void stop() {
    Timer delay = new Timer(){
      @Override
      public void run() {
        if(currentShownWidget == PRESSED_DECK) {
          buttonPanel.showWidget(UNPRESSED_DECK);
          setCurrentShownWidget(UNPRESSED_DECK);
        }
      }
    };
    delay.schedule(STOP_DELAY_TIMER);
  }

  private void setCurrentShownWidget(int currentShownWidget) {
    this.currentShownWidget = currentShownWidget;
  }
  
  public Widget getManagedObject() {
    return managedObject;
  }
}
