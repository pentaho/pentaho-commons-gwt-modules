package org.pentaho.gwt.widgets.client.ui;

import com.google.gwt.user.client.ui.Widget;

/**
 * User: nbaker
 * Date: Aug 6, 2010
 */
public interface Draggable {
  Widget makeProxy(Widget ele);
  Object getDragObject();
  void notifyDragFinished();
  void setDropValid(boolean valid);
}
