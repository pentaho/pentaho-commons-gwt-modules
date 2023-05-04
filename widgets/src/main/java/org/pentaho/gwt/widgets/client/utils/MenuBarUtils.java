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
 * Copyright (c) 2023 Hitachi Vantara. All rights reserved.
 */

package org.pentaho.gwt.widgets.client.utils;

import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.UIObject;

import java.util.List;

public class MenuBarUtils {
  // "Static" class. Prevent instantiation.
  private MenuBarUtils() {
  }

  // Access to private field allItems
  public static native List<UIObject> getAllItems( MenuBar menuBar ) /*-{
    return menuBar.@com.google.gwt.user.client.ui.MenuBar::allItems;
  }-*/;

  // Access to private field items
  public static native List<MenuItem> getItems( MenuBar menuBar ) /*-{
    return menuBar.@com.google.gwt.user.client.ui.MenuBar::items;
  }-*/;

  // Access to private field popup
  public static native DecoratedPopupPanel getPopup( MenuBar menuBar ) /*-{
    return menuBar.@com.google.gwt.user.client.ui.MenuBar::popup;
  }-*/;

  // Access to private field parentMenu
  public static native MenuBar getParentMenu( MenuBar menuBar ) /*-{
    return menuBar.@com.google.gwt.user.client.ui.MenuBar::parentMenu;
  }-*/;
}
