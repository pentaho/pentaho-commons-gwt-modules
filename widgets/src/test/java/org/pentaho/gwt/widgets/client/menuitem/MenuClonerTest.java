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

package org.pentaho.gwt.widgets.client.menuitem;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MenuItemSeparator;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith( GwtMockitoTestRunner.class )
public class MenuClonerTest {

  // region helpers
  private static final String DEFAULT_TEXT_HTML = "<b>Test Menu Item HTML</b>";

  private static class MenuBarForTesting extends MenuBar {
    public static MenuBarForTesting create( MenuBar menuBar ) {
      return new MenuBarForTesting();
    }
  }

  private static MenuBar mockMenuBar() {
    return mockMenuBarProps( mock( MenuBar.class ) );
  }

  private static <T extends MenuBar> T mockMenuBarProps( T menuBarMock ) {
    when( menuBarMock.getElement() ).thenReturn( mock( Element.class ) );
    return menuBarMock;
  }

  private static MenuItem mockMenuItem() {
    return mockMenuItemProps( mock( MenuItem.class ) );
  }

  private static <T extends MenuItem> T mockMenuItemProps( T menuItemMock ) {
    when( menuItemMock.getHTML() ).thenReturn( DEFAULT_TEXT_HTML );
    when( menuItemMock.getElement() ).thenReturn( mock( Element.class ) );

    return menuItemMock;
  }

  private static MenuItemSeparator mockMenuItemSeparator() {
    return mock( MenuItemSeparator.class );
  }
  // endregion

  // region constructor
  @Test( expected = IllegalArgumentException.class )
  public void testCloneMenuBarThrowsWhenGivenANullMenuBarCreator() {
    new MenuCloner<MenuBarForTesting>( null );
  }
  // endregion

  // region cloneProps( MenuItem, MenuItem )
  @Test
  public void testClonePropsReturnsMenuItemClone() {
    MenuItem itemMock = mockMenuItem();
    MenuItem itemCloneMock = mockMenuItem();

    MenuCloner<MenuBarForTesting> menuCloner = new MenuCloner<>( MenuBarForTesting::create );
    MenuItem itemResult = menuCloner.cloneProps( itemMock, itemCloneMock );

    assertSame( itemCloneMock, itemResult );
  }

  @Test
  public void testClonePropsClonesIsEnabled() {
    MenuItem itemMock = mockMenuItem();
    MenuItem itemCloneMock = mockMenuItem();

    MenuCloner<MenuBarForTesting> menuCloner = new MenuCloner<>( MenuBarForTesting::create );

    // true value
    when( itemMock.isEnabled() ).thenReturn( true );

    menuCloner.cloneProps( itemMock, itemCloneMock );

    verify( itemCloneMock, times( 1 ) ).setEnabled( true );

    // false value
    when( itemMock.isEnabled() ).thenReturn( false );

    menuCloner.cloneProps( itemMock, itemCloneMock );

    verify( itemCloneMock, times( 1 ) ).setEnabled( true );
  }

  @Test
  public void testClonePropsClonesIsVisible() {
    MenuItem itemMock = mockMenuItem();
    MenuItem itemCloneMock = mockMenuItem();

    MenuCloner<MenuBarForTesting> menuCloner = new MenuCloner<>( MenuBarForTesting::create );

    // true value
    when( itemMock.isVisible() ).thenReturn( true );

    menuCloner.cloneProps( itemMock, itemCloneMock );

    verify( itemCloneMock, times( 1 ) ).setVisible( true );

    // false value
    when( itemMock.isVisible() ).thenReturn( false );

    menuCloner.cloneProps( itemMock, itemCloneMock );

    verify( itemCloneMock, times( 1 ) ).setVisible( true );
  }

  @Test
  public void testClonePropsClonesElementId() {
    MenuItem itemMock = mockMenuItem();

    String ID = "test-id";
    when( itemMock.getElement().getId() ).thenReturn( ID );

    MenuItem itemClone = mockMenuItem();

    MenuCloner<MenuBarForTesting> menuCloner = new MenuCloner<>( MenuBarForTesting::create );
    menuCloner.cloneProps( itemMock, itemClone );

    verify( itemClone.getElement(), times( 1 ) ).setId( ID );
  }

  @Test
  public void testClonePropsClonesSubMenu() {
    MenuItem itemMock = mockMenuItem();
    MenuBar subMenuMock = mockMenuBar();
    when( itemMock.getSubMenu() ).thenReturn( subMenuMock );

    MenuItem itemCloneMock = mockMenuItem();
    MenuBar subMenuCloneMock = mockMenuBar();

    MenuCloner<MenuBarForTesting> menuClonerSpy = spy( new MenuCloner<>( MenuBarForTesting::create ) );
    doReturn( subMenuCloneMock ).when( menuClonerSpy ).clone( subMenuMock );

    menuClonerSpy.cloneProps( itemMock, itemCloneMock );

    verify( menuClonerSpy, times( 1 ) ).clone( subMenuMock );

    // itemClone.getSubMenu() would not work, would always return null due to how GwtMockitoTestRunner handles classes
    // from the GWT packages, so need to _verify_ the call to test the cloned sub-menu.
    verify( itemCloneMock, times( 1 ) ).setSubMenu( subMenuCloneMock );
  }

  @Test
  public void testClonePropsCopiesCommand() {
    MenuItem itemMock = mockMenuItem();

    Scheduler.ScheduledCommand cmdMock = mock( Scheduler.ScheduledCommand.class );
    when( itemMock.getScheduledCommand() ).thenReturn( cmdMock );

    MenuItem itemCloneMock = mockMenuItem();

    MenuCloner<MenuBarForTesting> menuCloner = new MenuCloner<>( MenuBarForTesting::create );

    menuCloner.cloneProps( itemMock, itemCloneMock );

    verify( itemCloneMock, times( 1 ) ).setScheduledCommand( cmdMock );
  }
  // endregion

  // region clone( MenuItem )
  @Test
  public void testCloneMenuItemSupportsBaseMenuItemInstance() {
    MenuItem itemMock = mockMenuItem();
    MenuCloner<MenuBarForTesting> menuCloner = new MenuCloner<>( MenuBarForTesting::create );
    MenuItem itemClone = menuCloner.clone( itemMock );

    assertNotSame( itemMock, itemClone );

    assertSame( MenuItem.class, itemClone.getClass() );
  }

  @Test
  public void testCloneMenuItemCopiesBaseMenuItemHTML() {
    MenuItem itemMock = mockMenuItem();
    MenuCloner<MenuBarForTesting> menuClonerSpy = spy( new MenuCloner<>( MenuBarForTesting::create ) );
    menuClonerSpy.clone( itemMock );
    verify( menuClonerSpy, times( 1 ) ).createGwtMenuItem( DEFAULT_TEXT_HTML );
  }

  @Test
  public void testCloneMenuItemSupportsPentahoMenuItemInstance() {
    PentahoMenuItem itemMock = mockMenuItemProps( mock( PentahoMenuItem.class ) );
    MenuCloner<MenuBarForTesting> menuCloner = new MenuCloner<>( MenuBarForTesting::create );
    MenuItem itemClone = menuCloner.clone( itemMock );

    assertNotSame( itemMock, itemClone );

    assertSame( PentahoMenuItem.class, itemClone.getClass() );
  }

  @Test
  public void testCloneMenuItemCopiesPentahoMenuItemHTML() {
    PentahoMenuItem itemMock = mockMenuItemProps( mock( PentahoMenuItem.class ) );
    MenuCloner<MenuBarForTesting> menuClonerSpy = spy( new MenuCloner<>( MenuBarForTesting::create ) );
    menuClonerSpy.clone( itemMock );
    verify( menuClonerSpy, times( 1 ) ).createPentahoMenuItem( DEFAULT_TEXT_HTML );
  }

  @Test
  public void testCloneMenuItemClonesPentahoMenuItemIsChecked() {
    PentahoMenuItem itemMock = mockMenuItemProps( mock( PentahoMenuItem.class ) );

    MenuCloner<MenuBarForTesting> menuClonerSpy = spy( new MenuCloner<>( MenuBarForTesting::create ) );
    doAnswer( invocation -> spy( invocation.callRealMethod() ) )
      .when( menuClonerSpy ).createPentahoMenuItem( anyString() );

    // true value
    when( itemMock.isChecked() ).thenReturn( true );

    PentahoMenuItem itemCloneSpy = (PentahoMenuItem) menuClonerSpy.clone( itemMock );

    verify( itemCloneSpy, times( 1 ) ).setChecked( true );

    // false value
    when( itemMock.isChecked() ).thenReturn( false );

    itemCloneSpy = (PentahoMenuItem) menuClonerSpy.clone( itemMock );

    verify( itemCloneSpy, times( 1 ) ).setChecked( false );
  }

  @Test
  public void testCloneMenuItemClonesPentahoMenuItemIsUseCheckUI() {
    PentahoMenuItem itemMock = mockMenuItemProps( mock( PentahoMenuItem.class ) );

    MenuCloner<MenuBarForTesting> menuClonerSpy = spy( new MenuCloner<>( MenuBarForTesting::create ) );
    doAnswer( invocation -> spy( invocation.callRealMethod() ) )
      .when( menuClonerSpy ).createPentahoMenuItem( anyString() );

    // true value
    when( itemMock.isUseCheckUI() ).thenReturn( true );

    PentahoMenuItem itemCloneSpy = (PentahoMenuItem) menuClonerSpy.clone( itemMock );

    verify( itemCloneSpy, times( 1 ) ).setUseCheckUI( true );

    // false value
    when( itemMock.isUseCheckUI() ).thenReturn( false );

    itemCloneSpy = (PentahoMenuItem) menuClonerSpy.clone( itemMock );

    verify( itemCloneSpy, times( 1 ) ).setUseCheckUI( false );
  }

  @Test
  public void testCloneMenuItemSupportsCheckBoxMenuItemInstance() {
    CheckBoxMenuItem itemMock = mockMenuItemProps( mock( CheckBoxMenuItem.class ) );
    MenuCloner<MenuBarForTesting> menuCloner = new MenuCloner<>( MenuBarForTesting::create );
    MenuItem itemClone = menuCloner.clone( itemMock );

    assertNotSame( itemMock, itemClone );

    assertSame( CheckBoxMenuItem.class, itemClone.getClass() );
  }

  @Test
  public void testCloneMenuItemCopiesCheckBoxMenuItemHTML() {
    CheckBoxMenuItem itemMock = mockMenuItemProps( mock( CheckBoxMenuItem.class ) );
    MenuCloner<MenuBarForTesting> menuClonerSpy = spy( new MenuCloner<>( MenuBarForTesting::create ) );
    menuClonerSpy.clone( itemMock );
    verify( menuClonerSpy, times( 1 ) ).createCheckBoxMenuItem( DEFAULT_TEXT_HTML );
  }

  @Test
  public void testCloneMenuItemClonesCheckBoxMenuItemIsChecked() {
    CheckBoxMenuItem itemMock = mockMenuItemProps( mock( CheckBoxMenuItem.class ) );

    MenuCloner<MenuBarForTesting> menuClonerSpy = spy( new MenuCloner<>( MenuBarForTesting::create ) );
    doAnswer( invocation -> spy( invocation.callRealMethod() ) )
      .when( menuClonerSpy ).createCheckBoxMenuItem( anyString() );

    // true value
    when( itemMock.isChecked() ).thenReturn( true );

    CheckBoxMenuItem itemCloneSpy = (CheckBoxMenuItem) menuClonerSpy.clone( itemMock );

    verify( itemCloneSpy, times( 1 ) ).setChecked( true );

    // false value
    when( itemMock.isChecked() ).thenReturn( false );

    itemCloneSpy = (CheckBoxMenuItem) menuClonerSpy.clone( itemMock );

    verify( itemCloneSpy, times( 1 ) ).setChecked( false );
  }
  // endregion

  // region clone( MenuItemSeparator )
  @Test
  public void testCloneMenuItemSeparatorSupportsBaseMenuItemSeparatorInstance() {
    MenuItemSeparator itemSepMock = mockMenuItemSeparator();
    MenuCloner<MenuBarForTesting> menuCloner = new MenuCloner<>( MenuBarForTesting::create );
    MenuItemSeparator itemSepClone = menuCloner.clone( itemSepMock );

    assertNotSame( itemSepMock, itemSepClone );

    assertSame( MenuItemSeparator.class, itemSepClone.getClass() );
  }

  @Test
  public void testCloneMenuItemSeparatorSupportsPentahoMenuItemSeparatorInstance() {
    PentahoMenuSeparator itemSepMock = mock( PentahoMenuSeparator.class );
    MenuCloner<MenuBarForTesting> menuCloner = new MenuCloner<>( MenuBarForTesting::create );
    MenuItemSeparator itemSepClone = menuCloner.clone( itemSepMock );

    assertNotSame( itemSepMock, itemSepClone );

    assertSame( PentahoMenuSeparator.class, itemSepClone.getClass() );
  }
  // endregion

  // region clone( MenuBar )
  @Test
  public void testCloneMenuBarReturnsInstanceCreatedByFactory() {
    MenuBar menuBarMock = mockMenuBar();
    MenuBarForTesting menuBarClone = new MenuBarForTesting();

    MenuCloner<MenuBarForTesting> menuCloner = new MenuCloner<>( mb -> menuBarClone );
    MenuBar menuBarResult = menuCloner.clone( menuBarMock );

    assertSame( menuBarClone, menuBarResult );
  }

  @Test
  public void testCloneMenuBarClonesIsAnimationEnabled() {
    MenuBar menuBarMock = mockMenuBar();
    MenuBarForTesting menuBarCloneMock = mockMenuBarProps( mock( MenuBarForTesting.class ) );

    MenuCloner<MenuBarForTesting> menuCloner = new MenuCloner<>( mb -> menuBarCloneMock );

    // true value
    when( menuBarMock.isAnimationEnabled() ).thenReturn( true );

    menuCloner.clone( menuBarMock );

    verify( menuBarCloneMock, times( 1 ) ).setAnimationEnabled( true );

    // false value
    when( menuBarMock.isAnimationEnabled() ).thenReturn( false );

    menuCloner.clone( menuBarMock );

    verify( menuBarCloneMock, times( 1 ) ).setAnimationEnabled( false );
  }

  @Test
  public void testCloneMenuBarClonesAutoOpen() {
    MenuBar menuBarMock = mockMenuBar();
    MenuBarForTesting menuBarCloneMock = mockMenuBarProps( mock( MenuBarForTesting.class ) );

    MenuCloner<MenuBarForTesting> menuCloner = new MenuCloner<>( mb -> menuBarCloneMock );

    // true value
    when( menuBarMock.getAutoOpen() ).thenReturn( true );

    menuCloner.clone( menuBarMock );

    verify( menuBarCloneMock, times( 1 ) ).setAutoOpen( true );

    // false value
    when( menuBarMock.getAutoOpen() ).thenReturn( false );

    menuCloner.clone( menuBarMock );

    verify( menuBarCloneMock, times( 1 ) ).setAutoOpen( false );
  }

  @Test
  public void testCloneMenuBarClonesFocusOnHoverEnabled() {
    MenuBar menuBarMock = mockMenuBar();
    MenuBarForTesting menuBarCloneMock = mockMenuBarProps( mock( MenuBarForTesting.class ) );

    MenuCloner<MenuBarForTesting> menuCloner = new MenuCloner<>( mb -> menuBarCloneMock );

    // true value
    when( menuBarMock.isFocusOnHoverEnabled() ).thenReturn( true );

    menuCloner.clone( menuBarMock );

    verify( menuBarCloneMock, times( 1 ) ).setFocusOnHoverEnabled( true );

    // false value
    when( menuBarMock.isFocusOnHoverEnabled() ).thenReturn( false );

    menuCloner.clone( menuBarMock );

    verify( menuBarCloneMock, times( 1 ) ).setFocusOnHoverEnabled( false );
  }

  @Test
  public void testCloneMenuBarClonesElementId() {
    MenuBar menuBarMock = mockMenuBar();

    String ID = "test-id";
    when( menuBarMock.getElement().getId() ).thenReturn( ID );

    MenuBarForTesting menuBarCloneMock = mockMenuBarProps( mock( MenuBarForTesting.class ) );

    MenuCloner<MenuBarForTesting> menuCloner = new MenuCloner<>( mb -> menuBarCloneMock );

    menuCloner.clone( menuBarMock );

    verify( menuBarCloneMock.getElement(), times( 1 ) ).setId( ID );
  }

  @Test
  public void testCloneMenuBarClonesChildMenuItems() {
    MenuBar menuBarMock = mockMenuBar();

    MenuItem subItem1Mock = mockMenuItem();
    MenuItemSeparator subItem2Mock = mockMenuItemSeparator();
    MenuItem subItem3Mock = mockMenuItem();

    MenuItem subItem1CloneMock = mockMenuItem();
    MenuItemSeparator subItem2CloneMock = mockMenuItemSeparator();
    MenuItem subItem3CloneMock = mockMenuItem();

    List<UIObject> subMenuAllItems = new ArrayList<>( Arrays.asList( subItem1Mock, subItem2Mock, subItem3Mock ) );

    MenuBarForTesting menuBarCloneMock = mockMenuBarProps( mock( MenuBarForTesting.class ) );

    MenuCloner<MenuBarForTesting> menuClonerSpy = spy( new MenuCloner<>( mb -> menuBarCloneMock ) );

    // Override the menu cloner's getAllItemsOf to achieve "mocking" of the private menuBarMock.allItems.
    doAnswer( invocation -> subMenuAllItems ).when( menuClonerSpy ).getAllItemsOf( any( MenuBar.class ) );
    doReturn( subItem1CloneMock ).when( menuClonerSpy ).clone( subItem1Mock );
    doReturn( subItem2CloneMock ).when( menuClonerSpy ).clone( subItem2Mock );
    doReturn( subItem3CloneMock ).when( menuClonerSpy ).clone( subItem3Mock );

    menuClonerSpy.clone( menuBarMock );

    verify( menuBarCloneMock, times( 1 ) ).addItem( subItem1CloneMock );
    verify( menuBarCloneMock, times( 1 ) ).addSeparator( subItem2CloneMock );
    verify( menuBarCloneMock, times( 1 ) ).addItem( subItem1CloneMock );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testCloneMenuBarThrowsForItemOfUnknownClass() {
    MenuBar menuBarMock = mockMenuBar();

    UIObject subItem1Mock = mock( UIObject.class );

    List<UIObject> subMenuAllItems = new ArrayList<>( Arrays.asList( subItem1Mock ) );

    MenuBarForTesting menuBarCloneMock = mockMenuBarProps( mock( MenuBarForTesting.class ) );

    MenuCloner<MenuBarForTesting> menuClonerSpy = spy( new MenuCloner<>( mb -> menuBarCloneMock ) );

    // Override the menu cloner's getAllItemsOf to achieve "mocking" of the private menuBarMock.allItems.
    doAnswer( invocation -> subMenuAllItems ).when( menuClonerSpy ).getAllItemsOf( any( MenuBar.class ) );

    menuClonerSpy.clone( menuBarMock );
  }
  // endregion
}
