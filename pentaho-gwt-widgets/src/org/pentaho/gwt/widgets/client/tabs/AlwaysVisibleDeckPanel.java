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

    if (curWidget != oldWidget) {
      if(oldWidget != null){
        moveOffscreen(oldWidget);
      }


      curWidget.getElement().getParentElement().getStyle().setProperty("position", "relative");
      curWidget.getElement().getParentElement().getStyle().setProperty("left", "0");

    }
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

  //
//  @Override
//  public void showWidget(int index) {
//    Widget widgetToShow = this.getWidget(index);
//    int curIdx = this.getVisibleWidget();
//    if(curIdx > -1){
//      Widget currentWidget = this.getWidget(this.getVisibleWidget());
//      DOM.setStyleAttribute(currentWidget.getParent().getElement(), "left", "-5000px");
//    }
//    DOM.setStyleAttribute(widgetToShow.getParent().getElement(), "left", "0px");
//    curWidget = widgetToShow;
//  }
//
//  @Override
//  public int getVisibleWidget() {
//    return this.getWidgetIndex(curWidget);
//  }
//
//  private Element createWidgetWrapper() {
//    Element div = DOM.createDiv();
//    div.getStyle().setProperty("width", "100%");
//    div.getStyle().setProperty("height", "0px");
//    div.getStyle().setProperty("padding", "0px");
//    div.getStyle().setProperty("margin", "0px");
//    return div;
//  }
//
//  @Override
//  public void add(Widget w) {
//
//    Element wrapper = createWidgetWrapper();
//    getElement().appendChild(wrapper);
//
//    super.add(w, wrapper);
//
//    DOM.setStyleAttribute(wrapper, "height", "100%");
//    DOM.setStyleAttribute(wrapper, "position", "absolute");
//    DOM.setStyleAttribute(wrapper, "zIndex", "12345");
//    w.setSize("100%", "100%");
//    DOM.setStyleAttribute(wrapper, "height", "100%");
//
//  }
}
