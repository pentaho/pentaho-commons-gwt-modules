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

package org.pentaho.gwt.widgets.client.dialogs;

import com.google.gwt.user.client.ui.Button;
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
public class PromptDialogBoxTest {
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
  @SuppressWarnings( "deprecation" )
  public void testOnKeyDownPreview() {
    PromptDialogBox box = mock( PromptDialogBox.class );
    doCallRealMethod().when( box ).onKeyDownPreview( anyChar(), anyInt() );

    box.onKeyDownPreview( (char) KeyboardListener.KEY_ESCAPE, 0 );
    verify( box ).onCancel();
  }

  @Test
  public void testOnOk() throws Exception {
    PromptDialogBox box = mock( PromptDialogBox.class );
    final IDialogCallback callback = mock( IDialogCallback.class );
    box.callback = callback;
    final IDialogValidatorCallback validatorCallback = mock( IDialogValidatorCallback.class );
    box.validatorCallback = validatorCallback;
    doCallRealMethod().when( box ).onOk();

    box.onOk();

    verify( callback, never() ).okPressed();
    verify( validatorCallback ) .validate();

    when( validatorCallback.validate() ).thenReturn( true );
    box.onOk();

    verify( callback ).okPressed();
    verify( validatorCallback, times( 2 ) ).validate();
  }

  @Test
  public void testOnNotOk() throws Exception {
    PromptDialogBox box = mock( PromptDialogBox.class );
    final IThreeButtonDialogCallback callback = mock( IThreeButtonDialogCallback.class );
    box.callback = callback;
    doCallRealMethod().when( box ).onNotOk();

    box.onNotOk();

    verify( callback ).notOkPressed();
  }

  @Test
  public void testOnCancel() throws Exception {
    PromptDialogBox box = mock( PromptDialogBox.class );
    final IDialogCallback callback = mock( IDialogCallback.class );
    box.callback = callback;
    doCallRealMethod().when( box ).onCancel();

    box.onCancel();

    verify( callback ).cancelPressed();
  }

  // region getDefaultAutoFocusContentWidget()
  @Test
  public void testGetDefaultAutoFocusContentWidgetReturnsNullIfNoContent() {
    PromptDialogBox box = spy( new PromptDialogBox( "title", "ok", "not-ok", "cancel" ) );

    doReturn( null ).when( box ).getContent();

    assertEquals( null, box.getDefaultAutoFocusContentWidget() );
  }

  @Test
  public void testGetDefaultAutoFocusContentWidgetReturnsFirstFocusableOfContentIfAny() {
    PromptDialogBox box = spy( new PromptDialogBox( "title", "ok", "not-ok", "cancel" ) );

    Widget contentMock = mockContainerWidget();
    Widget childMock = mockFocusableChildInContainer( (HasWidgets) contentMock );

    doReturn( contentMock ).when( box ).getContent();

    assertEquals( childMock, box.getDefaultAutoFocusContentWidget() );
  }
  // endregion

  // region getDefaultAutoFocusButton()
  private static Button mockButton( boolean visible, boolean enabled ) {
    Button buttonMock = mock( Button.class );
    doReturn( visible ).when( buttonMock ).isVisible();
    doReturn( enabled ).when( buttonMock ).isEnabled();

    return buttonMock;
  }
  @Test
  public void testGetDefaultAutoFocusButtonReturnsNullIfAllButtonsAreDisabled() {
    PromptDialogBox box = spy( new PromptDialogBox( "title", "ok", "not-ok", "cancel" ) );
    Button cancelButtonMock = mockButton( true, false );
    Button notOkButtonMock = mockButton( true, false );
    Button okButtonMock = mockButton( true, false );

    doReturn( cancelButtonMock ).when( box ).getCancelButton();
    doReturn( notOkButtonMock ).when( box ).getNotOkButton();
    doReturn( okButtonMock ).when( box ).getOkButton();

    assertEquals( null, box.getDefaultAutoFocusButton() );
  }

  @Test
  public void testGetDefaultAutoFocusButtonReturnsNullIfAllButtonsAreHidden() {
    PromptDialogBox box = spy( new PromptDialogBox( "title", "ok", "not-ok", "cancel" ) );

    Button cancelButtonMock = mockButton( false, true );
    Button notOkButtonMock = mockButton( false, true );
    Button okButtonMock = mockButton( false, true );

    doReturn( cancelButtonMock ).when( box ).getCancelButton();
    doReturn( notOkButtonMock ).when( box ).getNotOkButton();
    doReturn( okButtonMock ).when( box ).getOkButton();

    assertEquals( null, box.getDefaultAutoFocusButton() );
  }

  @Test
  public void testGetDefaultAutoFocusButtonReturnsOkButtonIfOnlyButton() {
    PromptDialogBox box = spy( new PromptDialogBox( "title", "ok", null, null ) );

    Button okButtonMock = mockButton( true, true );

    doReturn( null ).when( box ).getCancelButton();
    doReturn( null ).when( box ).getNotOkButton();
    doReturn( okButtonMock ).when( box ).getOkButton();

    assertEquals( okButtonMock, box.getDefaultAutoFocusButton() );
  }

  @Test
  public void testGetDefaultAutoFocusButtonReturnsCancelButtonIfAvailable() {
    PromptDialogBox box = spy( new PromptDialogBox( "title", "ok", null, null ) );

    Button cancelButtonMock = mockButton( true, true );
    Button notOkButtonMock = mockButton( true, true );
    Button okButtonMock = mockButton( true, true );

    doReturn( cancelButtonMock ).when( box ).getCancelButton();
    doReturn( notOkButtonMock ).when( box ).getNotOkButton();
    doReturn( okButtonMock ).when( box ).getOkButton();

    assertEquals( cancelButtonMock, box.getDefaultAutoFocusButton() );
  }

  @Test
  public void testGetDefaultAutoFocusButtonReturnsNotOkButtonIfAvailableAndCancelIsNot() {
    PromptDialogBox box = spy( new PromptDialogBox( "title", "ok", null, null ) );

    Button notOkButtonMock = mockButton( true, true );
    Button okButtonMock = mockButton( true, true );

    doReturn( null ).when( box ).getCancelButton();
    doReturn( notOkButtonMock ).when( box ).getNotOkButton();
    doReturn( okButtonMock ).when( box ).getOkButton();

    assertEquals( notOkButtonMock, box.getDefaultAutoFocusButton() );
  }

  // endregion

  // region getDefaultAutoFocusWidget()
  @Test
  public void testGetDefaultAutoFocusWidgetDelegatesToGetDefaultAutoFocusContentWidget() {
    PromptDialogBox box = spy( new PromptDialogBox( "title", "ok", "not-ok", "cancel" ) );

    Focusable focusableMock = mock( Focusable.class );
    doReturn( focusableMock ).when( box ).getDefaultAutoFocusContentWidget();

    assertEquals( focusableMock, box.getDefaultAutoFocusWidget() );
  }

  @Test
  public void testGetDefaultAutoFocusWidgetDelegatesToGetDefaultAutoFocusButtonIfNoContentFocusable() {
    PromptDialogBox box = spy( new PromptDialogBox( "title", "ok", "not-ok", "cancel" ) );

    Button buttonMock = mock( Button.class );
    doReturn( null ).when( box ).getDefaultAutoFocusContentWidget();
    doReturn( buttonMock ).when( box ).getDefaultAutoFocusButton();

    assertEquals( buttonMock, box.getDefaultAutoFocusWidget() );
  }
  // endregion
}
