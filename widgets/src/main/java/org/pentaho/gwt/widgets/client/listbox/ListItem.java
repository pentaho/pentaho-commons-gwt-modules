/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.gwt.widgets.client.listbox;

import org.pentaho.gwt.widgets.client.ui.Draggable;

import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by IntelliJ IDEA. User: Nick Baker Date: Mar 9, 2009 Time: 11:17:00 AM
 */
public interface ListItem<T> extends HasAllMouseHandlers, Draggable {

  /**
   * Returns the widget representation of this ListItem.
   * 
   * CAUTION: Because this may be represented in a dropdown and in it's popup, getWidget() will be called more than
   * once. As a single widget can't be two places on the DOM, this method must return a clone every time.
   * 
   * @return Cloned widget representation
   */
  Widget getWidget();

  Widget getWidgetForDropdown();

  T getValue();

  void setValue( T t );

  void onHoverEnter();

  void onHoverExit();

  void onSelect();

  void onDeselect();

  void setStylePrimaryName( String style );

  void setListItemListener( ListItemListener listener );

  String getText();

}
