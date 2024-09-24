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

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.UIObject;

import java.util.List;

/**
 * The <code>MenuBarUtils</code> class contains utility methods for
 * dealing with GWT's {@link MenuBar} class.
 * <p>
 *   Most of the methods exist for the purpose of gaining access to this class' private fields or methods,
 *   via JSNI.
 * </p>
 */
public class MenuBarUtils {
  // "Static" class. Prevent instantiation.
  private MenuBarUtils() {
  }

  /**
   * Gets all items of a given menu bar.
   * <p>
   *   Includes both {@link MenuItem} and {@link com.google.gwt.user.client.ui.MenuItemSeparator} items.
   * </p>
   * <p>
   *   This method returns the value of a menu bar's private field <code>allItems</code>.
   * </p>
   * @param menuBar A menu bar.
   * @return A list of items.
   */
  public static native List<UIObject> getAllItems( MenuBar menuBar ) /*-{
    return menuBar.@com.google.gwt.user.client.ui.MenuBar::allItems;
  }-*/;

  /**
   * Gets the popup panel of a given menu bar.
   * <p>
   *   This method returns the value of a menu bar's private field <code>popup</code>.
   * </p>
   * @param menuBar A menu bar.
   * @return The popup panel, if any; <code>null</code>, otherwise.
   */
  public static native DecoratedPopupPanel getPopup( MenuBar menuBar ) /*-{
    return menuBar.@com.google.gwt.user.client.ui.MenuBar::popup;
  }-*/;

  /**
   * Gets the parent menu bar of a given menu bar.
   * <p>
   *   This method returns the value of a menu bar's private field <code>parentMenu</code>.
   * </p>
   * @param menuBar A menu bar.
   * @return The parent menu bar, if any; <code>null</code>, otherwise.
   */
  public static native MenuBar getParentMenu( MenuBar menuBar ) /*-{
    return menuBar.@com.google.gwt.user.client.ui.MenuBar::parentMenu;
  }-*/;

  /**
   * Provides access to MenuBar's
   * <code>com.google.gwt.user.client.ui.MenuBar#doItemAction(MenuItem, boolean, boolean)</code>.
   * @param menuBar A menu bar.
   * @param item A menu item.
   * @param fireCommand Indicates whether the menu item's command, if any, should be fired.
   * @param focus Indicates whether focus should be placed in the sub-menu, if one exists and is opened.
   */
  public static native void doItemAction( MenuBar menuBar, MenuItem item, boolean fireCommand, boolean focus ) /*-{
    menuBar.@com.google.gwt.user.client.ui.MenuBar::doItemAction(Lcom/google/gwt/user/client/ui/MenuItem;ZZ)(item,
        fireCommand, focus);
  }-*/;

  /**
   * Provides access to MenuBar's
   * <code>com.google.gwt.user.client.ui.MenuBar#findItem(Element)</code>.
   * @param menuBar
   * @param hItem
   * @return MenuItem
   */
  public static native MenuItem findItem( MenuBar menuBar, Element hItem) /*-{
    return menuBar.@com.google.gwt.user.client.ui.MenuBar::findItem(*)(hItem);
  }-*/;

  /**
   * Calculates the height of the MenuBar popup
   * @param menuBar
   * @return int
   */
  public static int calculatePopupHeight( MenuBar menuBar ){
    return MenuBarUtils.getAllItems( menuBar ).stream().mapToInt( UIObject::getOffsetHeight ).sum();
  }
}
