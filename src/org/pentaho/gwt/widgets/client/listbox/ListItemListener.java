package org.pentaho.gwt.widgets.client.listbox;

import com.google.gwt.user.client.Event;

/**
 * User: nbaker
 * Date: Aug 4, 2010
 */
public interface ListItemListener {

  void itemSelected(ListItem listItem, Event event);

  void doAction(ListItem listItem);
}
