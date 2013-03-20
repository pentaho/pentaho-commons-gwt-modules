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

    if (curWidget != oldWidget && oldWidget != null) {
      moveOffscreen(oldWidget);
    }

    // set the sizes to 100% to account for any window resizing.
    curWidget.getElement().getParentElement().getStyle().setProperty("width", "100%");
    curWidget.getElement().getParentElement().getStyle().setProperty("height", "100%");
    curWidget.getElement().getParentElement().getStyle().setProperty("position", "relative");
    curWidget.getElement().getParentElement().getStyle().setProperty("left", "0");
  }


  private void moveOffscreen(Widget w) {
    if (w.getElement() == null || w.getElement().getParentElement() == null) { // old active widget was removed.
      return;
    }

    if (w.getParent().getOffsetWidth() > 0 && w.getParent().getOffsetHeight() > 0) {
      int width = w.getElement().getParentElement().getOffsetWidth();
      int height = w.getElement().getParentElement().getOffsetHeight();

      // don't let the switching between relative and absolute positioning modify our size.
      // force the size with inline styles, we'll set it back to 100% when we show it again
      w.getElement().getParentElement().getStyle().setProperty("width", width + "px");
      w.getElement().getParentElement().getStyle().setProperty("height", height + "px");
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
