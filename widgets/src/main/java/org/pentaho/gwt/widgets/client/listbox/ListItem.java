/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
