package org.pentaho.gwt.widgets.client.tabs;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: nbaker
 * Date: 2/7/13
 */
public class AlwaysVisibleDeckPanel extends DeckPanel {
  Widget curWidget;
  public AlwaysVisibleDeckPanel() {

  }

  public void showWidget(int index) {
    checkIndexBoundsForAccess(index);
    Widget oldWidget = curWidget;
    curWidget = getWidget(index);

    if (curWidget != oldWidget && oldWidget != null){
      moveOffscreen(oldWidget);
    }

    curWidget.getElement().getParentElement().getStyle().setProperty("position", "relative");
    curWidget.getElement().getParentElement().getStyle().setProperty("left", "0");

  }


  private void moveOffscreen(Widget w){
    if(w.getElement() == null || w.getElement().getParentElement() == null){ // old active widget was removed.
      return;
    }
    w.getElement().getParentElement().getStyle().setProperty("position", "absolute");
    w.getElement().getParentElement().getStyle().setProperty("left", "-5000px");
  }

  @Override
  public int getVisibleWidget() {
    return this.getWidgetIndex(curWidget);
  }

  @Override
  public void add(Widget w) {
    super.add(w);
    w.setVisible(true);
    w.getElement().getParentElement().getStyle().setProperty("display", "");
    moveOffscreen(w);
  }
}
