package org.pentaho.gwt.widgets.client.panel;

import org.adamtacy.client.ui.effects.core.NMorphStyle;
import org.adamtacy.client.ui.effects.impl.css.Rule;

import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;


public class ActionBar extends SimplePanel {

  public static final int DEFAULT_HEIGHT = 50;
  public static final double DURATION = 0.5;
  public enum State {
    EXPAND, COLLAPSE
  }
  private HorizontalPanel buttonPanel;
  NMorphStyle collapseEffect;
  NMorphStyle expandEffect;
  int height = -1;
  private State state;



  public ActionBar()
  {
    buttonPanel = new HorizontalPanel();
    setStylePrimaryName("action-bar");
    add(buttonPanel);
    buttonPanel.setHeight("100%");
    buttonPanel.setWidth("100%");
    buttonPanel.setStylePrimaryName("action-button-bar");
  }


  public void addWidget(Widget widget, HorizontalAlignmentConstant align) {
    buttonPanel.add(widget);
    buttonPanel.setCellHorizontalAlignment(widget, align);
    buttonPanel.setCellVerticalAlignment(widget, HorizontalPanel.ALIGN_MIDDLE);
  }
 
  public void collapse(int delay) {
    if(state != State.COLLAPSE) {
      height = this.getOffsetHeight();
      if(height <= 0) {
        height = DEFAULT_HEIGHT;
      }
      collapseEffect = new NMorphStyle(new Rule("start{height:" + (height) + "px;}"), 
          new Rule("end{height: 0px;}"));    
      collapseEffect.setEffectElement(this.getElement());
      collapseEffect.setDuration(DURATION);
      collapseEffect.play(delay);
      setState(state.COLLAPSE);
    }
  }

  public void expand(int delay) {
    if(state != State.EXPAND) {
      height = this.getOffsetHeight();
      if(height <= 0) {
        height = DEFAULT_HEIGHT;
      }
      expandEffect = new NMorphStyle(new Rule("start{height: 0px;}"), 
          new Rule("end{height:" + (height) + "px;}"));
      expandEffect.setEffectElement(this.getElement());
      expandEffect.setDuration(DURATION);
      expandEffect.play(delay);
      setState(state.EXPAND);
    }
  }
  
  
  public State getState() {
    return state;
  }


  public void setState(State state) {
    this.state = state;
  }

}

