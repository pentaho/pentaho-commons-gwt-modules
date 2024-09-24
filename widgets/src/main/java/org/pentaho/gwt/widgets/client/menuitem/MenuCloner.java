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

package org.pentaho.gwt.widgets.client.menuitem;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MenuItemSeparator;
import com.google.gwt.user.client.ui.UIObject;
import org.pentaho.gwt.widgets.client.utils.MenuBarUtils;

import java.util.List;
import java.util.function.Function;

/**
 * The <code>MenuCloner</code> class offers functionality for cloning GWT menu-related objects.
 *
 * <p>
 * More specifically, this class supports cloning menu bar, menu item and menu separator objects,
 * derived from {@link MenuBar}, {@link MenuItem} and {@link MenuItemSeparator}, respectively.
 * </p>
 * <p>
 * This class supports Pentaho's GWT menu-related classes: {@link PentahoMenuItem},
 * {@link CheckBoxMenuItem} and {@link PentahoMenuSeparator}.
 * </p>
 *
 * @param <R> The menubar class of the menu bar clones.
 */
public class MenuCloner<R extends MenuBar> {

  private Function<MenuBar, R> menuBarCreator;

  /**
   * Creates a menu cloner with a given menu bar creator function.
   *
   * @param menuBarCreator The menu bar clone constructor function. Receives the menu bar to be cloned and
   *                       returns a cloned menubar instance of an appropriate class.
   */
  public MenuCloner( Function<MenuBar, R> menuBarCreator ) {
    if ( menuBarCreator == null ) {
      throw new IllegalArgumentException( "Argument 'menuBarCreator' cannot be null." );
    }

    this.menuBarCreator = menuBarCreator;
  }

  /**
   * Clones a given menu bar.
   *
   * @param menuBar The menu bar to clone.
   * @return The menu bar clone.
   */
  public R clone( MenuBar menuBar ) {

    R menuBarClone = menuBarCreator.apply( menuBar );

    menuBarClone.setAnimationEnabled( menuBar.isAnimationEnabled() );
    menuBarClone.setAutoOpen( menuBar.getAutoOpen() );
    menuBarClone.setFocusOnHoverEnabled( menuBar.isFocusOnHoverEnabled() );

    menuBarClone.getElement().setId( menuBar.getElement().getId() );

    for ( UIObject uio : getAllItemsOf( menuBar ) ) {
      if ( uio instanceof MenuItemSeparator ) {
        menuBarClone.addSeparator( clone( (MenuItemSeparator) uio ) );
      } else if ( uio instanceof MenuItem ) {
        menuBarClone.addItem( clone( (MenuItem) uio ) );
      } else {
        throw new IllegalArgumentException( "Logic not implemented to copy menu related object: " + uio.getClass() );
      }
    }

    return menuBarClone;
  }

  /**
   * Clones a given menu item.
   *
   * @param menuItem The menu item to clone.
   * @return The menu item clone.
   */
  public MenuItem clone( MenuItem menuItem ) {
    MenuItem menuItemClone;
    if ( menuItem instanceof PentahoMenuItem ) {
      menuItemClone = cloneHead( (PentahoMenuItem) menuItem );
    } else if ( menuItem instanceof CheckBoxMenuItem ) {
      menuItemClone = cloneHead( (CheckBoxMenuItem) menuItem );
    } else {
      menuItemClone = cloneHead( menuItem );
    }

    return cloneProps( menuItem, menuItemClone );
  }

  /**
   * Clones the common properties of a given menu item to those of a given menu item clone.
   *
   * @param menuItem      The menu item to clone.
   * @param menuItemClone The menu item clone.
   * @return The menu item clone specified in <code>menuItemClone</code>.
   */
  public MenuItem cloneProps( MenuItem menuItem, MenuItem menuItemClone ) {
    menuItemClone.setEnabled( menuItem.isEnabled() );
    menuItemClone.setVisible( menuItem.isVisible() );

    menuItemClone.getElement().setId( menuItem.getElement().getId() );

    MenuBar subMenuBar = menuItem.getSubMenu();
    if ( subMenuBar != null ) {
      menuItemClone.setSubMenu( clone( subMenuBar ) );
    }

    // Strangely, it seems menu items with submenus can also have a command.
    if ( menuItem.getScheduledCommand() != null ) {
      menuItemClone.setScheduledCommand( menuItem.getScheduledCommand() );
    }

    return menuItemClone;
  }

  /**
   * Clones a given menu item separator.
   *
   * @param menuItemSeparator The menu item separator to clone.
   * @return The menu item separator clone.
   */
  public MenuItemSeparator clone( MenuItemSeparator menuItemSeparator ) {
    MenuItemSeparator menuItemSeparatorClone;
    if ( menuItemSeparator instanceof PentahoMenuSeparator ) {
      menuItemSeparatorClone = new PentahoMenuSeparator();
    } else {
      menuItemSeparatorClone = new MenuItemSeparator();
    }

    menuItemSeparatorClone.setVisible( menuItemSeparator.isVisible() );

    return menuItemSeparatorClone;
  }

  /**
   * Clones the specifics of a given menu item of class {@link MenuItem}.
   *
   * @param menuItem The menu item to clone.
   * @return The menu item clone.
   */
  private MenuItem cloneHead( MenuItem menuItem ) {
    return createGwtMenuItem( menuItem.getHTML() );
  }

  /**
   * Clones the specifics of a given menu item of class {@link PentahoMenuItem}.
   *
   * @param menuItem The menu item to clone.
   * @return The menu item clone.
   */
  private PentahoMenuItem cloneHead( PentahoMenuItem menuItem ) {
    PentahoMenuItem menuItemClone = createPentahoMenuItem( menuItem.getHTML() );
    menuItemClone.setUseCheckUI( menuItem.isUseCheckUI() );
    menuItemClone.setChecked( menuItem.isChecked() );
    return menuItemClone;
  }

  /**
   * Clones the specifics of a given menu item of class {@link CheckBoxMenuItem}.
   *
   * @param menuItem The menu item to clone.
   * @return The menu item clone.
   */
  private CheckBoxMenuItem cloneHead( CheckBoxMenuItem menuItem ) {
    CheckBoxMenuItem menuItemClone = createCheckBoxMenuItem( menuItem.getHTML() );
    menuItemClone.setChecked( menuItem.isChecked() );
    return menuItemClone;
  }

  // Visible for testing
  List<UIObject> getAllItemsOf( MenuBar menuBar ) {
    return MenuBarUtils.getAllItems( menuBar );
  }

  // Visible for testing
  MenuItem createGwtMenuItem( String html ) {
    return new MenuItem( html, true, (Scheduler.ScheduledCommand) null );
  }

  // Visible for testing
  PentahoMenuItem createPentahoMenuItem( String html ) {
    return new PentahoMenuItem( html, true, null );
  }

  // Visible for testing
  CheckBoxMenuItem createCheckBoxMenuItem( String html ) {
    return new CheckBoxMenuItem( html, true, null );
  }
}
