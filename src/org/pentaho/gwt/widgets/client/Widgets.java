package org.pentaho.gwt.widgets.client;

import org.pentaho.gwt.widgets.client.buttons.RoundedButton;
import org.pentaho.gwt.widgets.client.i18n.WidgetsLocalizedMessages;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Widgets implements EntryPoint {

  private static final WidgetsLocalizedMessages MSGS = (WidgetsLocalizedMessages)GWT.create(WidgetsLocalizedMessages.class);
  
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    final RoundedButton btn = new RoundedButton("Test");
    RoundedButton btn2 = new RoundedButton("disable", new Command(){

      public void execute() {
        btn.setEnabled(!btn.isEnabled());
      }
      
    });
    HorizontalPanel hbox = new HorizontalPanel();
    hbox.add(btn);
    hbox.add(btn2);
    RootPanel.get("canvas").add(hbox);
  }
  
  public static WidgetsLocalizedMessages getLocalizedMessages() {
    return MSGS;
  }
}
