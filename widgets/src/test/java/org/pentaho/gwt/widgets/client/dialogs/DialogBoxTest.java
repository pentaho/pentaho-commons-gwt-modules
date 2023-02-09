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
* Copyright (c) 2002-2023 Hitachi Vantara. All rights reserved.
*/

package org.pentaho.gwt.widgets.client.dialogs;

import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
@SuppressWarnings( "deprecation" )
public class DialogBoxTest {

  // region Helpers
  private static Widget mockContainerWidget() {
    Widget widgetMock = mock(
      Widget.class,
      withSettings().extraInterfaces( HasWidgets.class ) );

    Iterator<Widget> iteratorMock = (Iterator<Widget>) mock( Iterator.class );
    when( ( (HasWidgets) widgetMock ).iterator() ).thenReturn( iteratorMock );
    when( widgetMock.isVisible() ).thenReturn( true );

    return widgetMock;
  }

  private static Widget mockFocusableWidget() {
    Widget widgetMock = mock(
      Widget.class,
      withSettings().extraInterfaces( Focusable.class, HasEnabled.class ) );

    when( ( (Focusable) widgetMock ).getTabIndex() ).thenReturn( 0 );
    when( ( (HasEnabled) widgetMock ).isEnabled() ).thenReturn( true );
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

  @Test
  public void testOnKeyDownPreview() {
    DialogBox box = mock( DialogBox.class );
    doCallRealMethod().when( box ).onKeyDownPreview( anyChar(), anyInt() );

    box.onKeyDownPreview( (char) KeyboardListener.KEY_ESCAPE, 0 );
    verify( box, times( 1 ) ).hide();
  }

  // region setFocusWidget
  @Test
  public void testSetFocusWidgetRespectsTheSpecifiedValue() {
    DialogBox box = new DialogBox( false, false );

    FocusWidget focusWidget = mock( FocusWidget.class );

    box.setFocusWidget( focusWidget );

    assertEquals( focusWidget, box.getFocusWidget() );
  }

  @Test
  public void testSetFocusWidgetCallsDoAutoFocus() {
    DialogBox box = spy( new DialogBox( false, false ) );

    FocusWidget focusWidget = mock( FocusWidget.class );

    box.setFocusWidget( focusWidget );

    verify( box, times( 1 ) ).doAutoFocus();
  }
  // endregion

  // region doAutoFocus()
  @Test
  public void testDoAutoFocusSetsFocusOnAutoFocusWidgetWhenDialogIsShowingAndVisible() {
    DialogBox box = spy( new DialogBox( false, false ) );
    doReturn( true ).when( box ).isShowing();
    doReturn( true ).when( box ).isVisible();

    Focusable focusWidget = mock( Focusable.class );
    doReturn( focusWidget ).when( box ).getAutoFocusWidget();

    box.doAutoFocus();

    verify( focusWidget ).setFocus( true );
  }

  @Test
  public void testDoAutoFocusDoesNotSetFocusOnWidgetWhenDialogNotShowing() {
    DialogBox box = spy( new DialogBox( false, false ) );
    doReturn( false ).when( box ).isShowing();
    doReturn( true ).when( box ).isVisible();

    final FocusWidget focusWidget = mock( FocusWidget.class );
    box.setFocusWidget( focusWidget );

    verify( focusWidget, never() ).setFocus( true );
  }

  @Test
  public void testDoAutoFocusDoesNotSetFocusOnWidgetWhenDialogShowingButNotVisible() {
    DialogBox box = spy( new DialogBox( false, false ) );
    doReturn( true ).when( box ).isShowing();
    doReturn( false ).when( box ).isVisible();

    final FocusWidget focusWidget = mock( FocusWidget.class );
    box.setFocusWidget( focusWidget );

    verify( focusWidget, never() ).setFocus( true );
  }

  @Test
  public void testDoAutoFocusDoesNothingWhenThereIsNoAutoFocusWidget() {
    DialogBox box = spy( new DialogBox( false, false ) );
    doReturn( true ).when( box ).isShowing();
    doReturn( true ).when( box ).isVisible();
    doReturn( null ).when( box ).getAutoFocusWidget();

    box.doAutoFocus();
  }
  // endregion

  // region getAutoFocusWidget()
  @Test
  public void testGetAutoFocusWidgetReturnsFocusWidgetWhenSet() {
    DialogBox box = spy( new DialogBox( false, false ) );

    final FocusWidget focusWidget = mock( FocusWidget.class );
    box.setFocusWidget( focusWidget );

    assertEquals( focusWidget, box.getAutoFocusWidget() );
  }

  @Test
  public void testGetAutoFocusWidgetReturnsGetDefaultAutoFocusWidgetWhenFocusWidgetNotSet() {
    DialogBox box = spy( new DialogBox( false, false ) );
    doReturn( null ).when( box ).getWidget();

    final Focusable focusWidget = mock( Focusable.class );
    doReturn( focusWidget ).when( box ).getDefaultAutoFocusWidget();

    assertEquals( focusWidget, box.getAutoFocusWidget() );
  }
  // endregion

  // region getDefaultAutoFocusWidget()
  @Test
  public void testGetDefaultAutoFocusWidgetReturnsNullWhenNotGetWidget() {
    DialogBox box = spy( new DialogBox( false, false ) );

    doReturn( null ).when( box ).getWidget();

    assertNull( box.getDefaultAutoFocusWidget() );
  }

  @Test
  public void testGetDefaultAutoFocusWidgetReturnsFirstFocusableWidget() {
    DialogBox box = spy( new DialogBox( false, false ) );

    Widget widgetMock = mockContainerWidget();
    Widget childMock = mockFocusableChildInContainer( (HasWidgets) widgetMock );

    doReturn( widgetMock ).when( box ).getWidget();

    assertEquals( childMock, box.getDefaultAutoFocusWidget() );
  }

  // endregion
}
