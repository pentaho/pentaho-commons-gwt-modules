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

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@RunWith( GwtMockitoTestRunner.class )
public class ElementUtilsTest {

  // region Helpers
  public static Widget mockFocusableWidget() {
    Widget widgetMock = mock(
      Widget.class,
      withSettings().extraInterfaces( Focusable.class, HasEnabled.class ) );

    when( ( (Focusable) widgetMock ).getTabIndex() ).thenReturn( 0 );
    when( ( (HasEnabled) widgetMock ).isEnabled() ).thenReturn( true );
    when( widgetMock.isVisible() ).thenReturn( true );

    return widgetMock;
  }

  public static Widget mockContainerWidget() {
    Widget widgetMock = mock(
      Widget.class,
      withSettings().extraInterfaces( HasWidgets.class ) );

    Iterator<Widget> iteratorMock = (Iterator<Widget>) mock( Iterator.class );
    when( ( (HasWidgets) widgetMock ).iterator() ).thenReturn( iteratorMock );
    when( widgetMock.isVisible() ).thenReturn( true );

    return widgetMock;
  }

  private static Widget mockFocusableChildInContainer( HasWidgets widgetMock ) {
    Iterator<Widget> iteratorMock = widgetMock.iterator();
    when( iteratorMock.hasNext() ).thenReturn( true );

    Widget childMock = mockFocusableWidget();
    when( iteratorMock.next() ).thenReturn( mock( Widget.class ), childMock );
    return childMock;
  }
  // endregion

  // region isEnabled
  @Test
  public void testIsEnabledWhenWidgetImplementsHasEnabledDelegatesToIt() {
    Widget widgetMock = mock( Widget.class, withSettings().extraInterfaces( HasEnabled.class ) );

    // When disabled.
    when( ( (HasEnabled) widgetMock ).isEnabled() ).thenReturn( false );

    assertFalse( ElementUtils.isEnabled( widgetMock ) );

    verify( (HasEnabled) widgetMock, times( 1 ) ).isEnabled();

    // When enabled.
    when( ( (HasEnabled) widgetMock ).isEnabled() ).thenReturn( true );

    assertTrue( ElementUtils.isEnabled( widgetMock ) );

    verify( (HasEnabled) widgetMock, times( 2 ) ).isEnabled();
  }

  @Test
  public void testIsEnabledWhenWidgetDoesNotImplementHasEnabledChecksElementProperty() {
    Widget widgetMock = mock( Widget.class );

    Element elementMock = mock( Element.class );
    when( widgetMock.getElement() ).thenReturn( elementMock );

    // When disabled.
    when( elementMock.getPropertyBoolean( "disabled" ) ).thenReturn( true );

    assertFalse( ElementUtils.isEnabled( widgetMock ) );

    verify( elementMock, times( 1 ) ).getPropertyBoolean( "disabled" );

    // When enabled.
    when( elementMock.getPropertyBoolean( "disabled" ) ).thenReturn( false );

    assertTrue( ElementUtils.isEnabled( widgetMock ) );

    verify( elementMock, times( 2 ) ).getPropertyBoolean( "disabled" );
  }
  // endregion

  // region isKeyboardFocusableLocal
  @Test
  public void testIsKeyboardFocusableLocalReturnsTrueWhenMeetsAllCriteria() {
    Widget widgetMock = mockFocusableWidget();

    assertTrue( ElementUtils.isKeyboardFocusableLocal( widgetMock ) );
  }

  @Test
  public void testIsKeyboardFocusableLocalReturnsTrueWhenTabIndexIsPositive() {
    Widget widgetMock = mockFocusableWidget();

    when( ( (Focusable) widgetMock ).getTabIndex() ).thenReturn( 1 );

    assertTrue( ElementUtils.isKeyboardFocusableLocal( widgetMock ) );
  }

  @Test
  public void testIsKeyboardFocusableLocalReturnsFalseWhenWidgetIsNotInstanceOfFocusable() {
    Widget widgetMock = mock(
      Widget.class,
      withSettings().extraInterfaces( HasEnabled.class ) );

    when( ( (HasEnabled) widgetMock ).isEnabled() ).thenReturn( true );
    when( widgetMock.isVisible() ).thenReturn( true );

    assertFalse( ElementUtils.isKeyboardFocusableLocal( widgetMock ) );
  }

  @Test
  public void testIsKeyboardFocusableLocalReturnsFalseWhenWidgetHasNegativeTabIndex() {
    Widget widgetMock = mockFocusableWidget();
    when( ( (Focusable) widgetMock ).getTabIndex() ).thenReturn( -1 );

    assertFalse( ElementUtils.isKeyboardFocusableLocal( widgetMock ) );
  }

  @Test
  public void testIsKeyboardFocusableLocalReturnsFalseWhenWidgetIsNotEnabled() {
    Widget widgetMock = mockFocusableWidget();
    when( ( (HasEnabled) widgetMock ).isEnabled() ).thenReturn( false );

    assertFalse( ElementUtils.isKeyboardFocusableLocal( widgetMock ) );
  }

  @Test
  public void testIsKeyboardFocusableLocalReturnsFalseWhenWidgetIsNotVisible() {
    Widget widgetMock = mockFocusableWidget();
    when( widgetMock.isVisible() ).thenReturn( false );

    assertFalse( ElementUtils.isKeyboardFocusableLocal( widgetMock ) );
  }
  // endregion

  // region findFirstKeyboardFocusableDescendant
  @Test
  public void testFindFirstKeyboardFocusableDescendantReturnsSelfWhenVisibleAndKeyboardFocusable() {
    Widget widgetMock = mockFocusableWidget();

    assertEquals( widgetMock, ElementUtils.findFirstKeyboardFocusableDescendant( widgetMock ) );
  }

  @Test
  public void testFindFirstKeyboardFocusableDescendantReturnsNullWhenNotVisible() {
    Widget widgetMock = mockFocusableWidget();
    when( widgetMock.isVisible() ).thenReturn( false );

    assertNull( ElementUtils.findFirstKeyboardFocusableDescendant( widgetMock ) );
  }

  @Test
  public void testFindFirstKeyboardFocusableDescendantReturnsNullWhenVisibleAndNotFocusableAndNotHasWidgets() {
    Widget widgetMock = mock( Widget.class );
    when( widgetMock.isVisible() ).thenReturn( true );

    assertNull( ElementUtils.findFirstKeyboardFocusableDescendant( widgetMock ) );
  }

  @Test
  public void testFindFirstKeyboardFocusableDescendantReturnsNullWhenVisibleAndNotFocusableAndHasZeroWidgets() {
    Widget widgetMock = mockContainerWidget();

    assertEquals( null, ElementUtils.findFirstKeyboardFocusableDescendant( widgetMock ) );
  }

  @Test
  public void testFindFirstKeyboardFocusableDescendantReturnsFocusableChild() {
    Widget widgetMock = mockContainerWidget();
    Widget childMock = mockFocusableChildInContainer( (HasWidgets) widgetMock );

    assertEquals( childMock, ElementUtils.findFirstKeyboardFocusableDescendant( widgetMock ) );
  }

  @Test
  public void testFindFirstKeyboardFocusableDescendantReturnsNullWhenNoFocusableChildren() {
    Widget widgetMock = mockContainerWidget();
    Iterator<Widget> iteratorMock = ( (HasWidgets) widgetMock ).iterator();
    when( iteratorMock.hasNext() ).thenReturn( true, true, false );

    when( iteratorMock.next() ).thenReturn( mock( Widget.class ), mock( Widget.class ) );

    assertNull( ElementUtils.findFirstKeyboardFocusableDescendant( widgetMock ) );
  }

  @Test
  public void testFindFirstKeyboardFocusableDescendantWhenFlexTableReturnsFirstDocumentOrderFocusableChild() {
    FlexTable tableMock = mock( FlexTable.class );
    when( tableMock.isVisible() ).thenReturn( true );

    when( tableMock.getRowCount() ).thenReturn( 4 );
    when( tableMock.getCellCount( anyInt() ) ).thenReturn( 2 );

    Widget child1Mock = mock( Widget.class );
    Widget child2Mock = mock( Widget.class );
    Widget child3Mock = mockFocusableWidget();
    Widget child4Mock = mock( Widget.class );
    Widget child5Mock = mockFocusableWidget();

    when( tableMock.getWidget( 1, 1 ) ).thenReturn( child1Mock );
    when( tableMock.getWidget( 1, 2 ) ).thenReturn( child2Mock );
    when( tableMock.getWidget( 2, 1 ) ).thenReturn( child3Mock );
    when( tableMock.getWidget( 2, 2 ) ).thenReturn( child4Mock );
    when( tableMock.getWidget( 3, 0 ) ).thenReturn( child5Mock );

    assertEquals( child3Mock, ElementUtils.findFirstKeyboardFocusableDescendant( tableMock ) );
  }
  // endregion
}
