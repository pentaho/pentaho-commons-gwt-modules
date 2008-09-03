package org.pentaho.gwt.widgets.client.buttons;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class RoundedButton extends Widget {

  private String text = "";
  private String baseStyleName = "roundedbutton";  //$NON-NLS-1$
  private Label label = new Label();
  private Command command;
  private SimplePanel leftPanel = new SimplePanel();
  private SimplePanel rightPanel = new SimplePanel();
  private boolean enabled = true;
  
  public RoundedButton(){
    createElement();
    this.setStylePrimaryName(baseStyleName);
    sinkEvents(Event.MOUSEEVENTS); 
    sinkEvents(Event.ONDBLCLICK); 
  }
  
  public RoundedButton(final String text){
    this();
    setText(text);
  }
  

  public RoundedButton(final String text, Command command){
    this(text);
    this.command = command;
  }
  
  private void createElement(){
    
    
    HorizontalPanel hbox = new HorizontalPanel();
    this.setElement(hbox.getElement());
    hbox.setStyleName(baseStyleName);
    
    super.setStylePrimaryName(baseStyleName);
    
    // Add in placeholder simplepanels
    leftPanel.setStylePrimaryName(this.getStylePrimaryName());
    leftPanel.addStyleDependentName("left");
    hbox.add(leftPanel);
    hbox.add(label);
    rightPanel.setStylePrimaryName(this.getStylePrimaryName());
    rightPanel.addStyleDependentName("right");
    hbox.add(rightPanel);
    
    // Set styles
    leftPanel.setStylePrimaryName(this.getStylePrimaryName());
    rightPanel.setStylePrimaryName(this.getStylePrimaryName());
    label.getElement().getParentElement().setClassName(this.getStylePrimaryName()+"-slice");
    label.setStylePrimaryName(this.getStylePrimaryName());
    label.addStyleDependentName("label");

    //prevent double-click from selecting text
    preventTextSelection(label.getElement());
    
  }
  
  


  @Override
  public void setStylePrimaryName(String style) {
    super.setStylePrimaryName(style);
    baseStyleName = style;
    
    label.setStylePrimaryName(style+"-label");
    rightPanel.setStylePrimaryName(style+"-right");
    leftPanel.setStylePrimaryName(style+"-left");
    label.getElement().getParentElement().setClassName(style+"-slice");
  }
  
  @Override
  public void addStyleDependentName(String style){
    super.addStyleDependentName(style);
        
    label.addStyleDependentName(style);
    
    rightPanel.addStyleDependentName(style);
    leftPanel.addStyleDependentName(style);
    label.getElement().getParentElement().setClassName(this.getStylePrimaryName()+"-slice-"+style);
  }
  
  @Override
  public void removeStyleDependentName(String style) {
    super.removeStyleDependentName(style);
        
    label.removeStyleDependentName(style);
    
    rightPanel.removeStyleDependentName(style);
    leftPanel.removeStyleDependentName(style);
    label.getElement().getParentElement().setClassName(this.getStylePrimaryName()+"-slice");
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
    label.setText(text);
  }
  
  public void setEnabled(boolean enabled){
    boolean prevVal = this.enabled;
    this.enabled = enabled;

    if(prevVal && enabled){
      return;
    } else if(prevVal && !enabled){
      this.addStyleDependentName("disabled");
    } else {
      this.removeStyleDependentName("disabled");
    }
  }
  
  public boolean isEnabled(){
    return enabled;
  }
  
  public void onBrowserEvent(Event event){
    switch(event.getTypeInt()){
      case Event.ONMOUSEDOWN:
        if(RoundedButton.this.isEnabled()){
          RoundedButton.this.command.execute();
          event.cancelBubble(true);
          event.preventDefault();
        }
        break;
      case Event.ONDBLCLICK:
        event.cancelBubble(true);
        event.preventDefault();
        break;
      case Event.ONMOUSEOVER:
        if(RoundedButton.this.isEnabled()){
          RoundedButton.this.addStyleDependentName("over");
        }
        break;
      case Event.ONMOUSEOUT:
        if(RoundedButton.this.isEnabled()){
          RoundedButton.this.removeStyleDependentName("over");
        }
        break;
        
    }
  }
  

  private static native void preventTextSelection(Element ele) /*-{
    ele.onselectstart=function() {return false};
  }-*/;
  
}

  