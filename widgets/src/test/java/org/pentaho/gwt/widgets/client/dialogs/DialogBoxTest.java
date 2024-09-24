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

import com.google.gwt.aria.client.Role;
import com.google.gwt.aria.client.Roles;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.gwt.widgets.client.dialogs.DialogBox.DialogMinimumHeightCategory;
import org.pentaho.gwt.widgets.client.dialogs.DialogBox.DialogSizingMode;
import org.pentaho.gwt.widgets.client.dialogs.DialogBox.DialogWidthCategory;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
@SuppressWarnings( "deprecation" )
public class DialogBoxTest {

  private DialogBox createDialogBoxSpyForShowHide() {
    DialogBox boxSpy = spy( new DialogBox() );
    doReturn( true ).when( boxSpy ).isModal();
    doNothing().when( boxSpy ).initializePageBackground();

    Element pageBgElemMock = mock( Element.class );
    doReturn( mock( Style.class ) ).when( pageBgElemMock ).getStyle();

    FocusPanel pageBgPanelSpy = spy( boxSpy.createPageBackground() );
    doReturn( pageBgElemMock ).when( pageBgPanelSpy ).getElement();

    doReturn( pageBgPanelSpy ).when( boxSpy ).getPageBackgroundInternal();

    doNothing().when( boxSpy ).toggleEmbedVisibility( anyBoolean() );

    doReturn( mock( GlassPane.class ) ).when( boxSpy ).getGlassPane();

    return boxSpy;
  }

  @Test
  public void testOnKeyDownPreview() {
    DialogBox boxMock = mock( DialogBox.class );
    doCallRealMethod().when( boxMock ).onPreviewNativeEvent( any( Event.NativePreviewEvent.class ) );

    NativeEvent nativeKeyDownEventMock = mock( NativeEvent.class );
    doReturn( KeyCodes.KEY_ESCAPE ).when( nativeKeyDownEventMock ).getKeyCode();

    Event.NativePreviewEvent keyDownEventMock = mock( Event.NativePreviewEvent.class );
    doReturn( Event.ONKEYDOWN ).when( keyDownEventMock ).getTypeInt();
    doReturn( nativeKeyDownEventMock ).when( keyDownEventMock ).getNativeEvent();

    boxMock.onPreviewNativeEvent( keyDownEventMock );

    verify( boxMock, times( 1 ) ).hide();
  }

  // region focus trapping when clicking on an element
  @Test
  public void testClickOnNonFocusableDialogElementPreventsDefault() {
    DialogBox boxMock = mock( DialogBox.class );
    doCallRealMethod().when( boxMock ).onPreviewNativeEvent( any() );
    doReturn( true ).when( boxMock ).eventTargetsPopupOrPartner( any() );
    doReturn( false ).when( boxMock ).eventTargetIsFocusable( any() );

    NativeEvent nativeMouseDownEventMock = mock( NativeEvent.class );

    Event.NativePreviewEvent mouseDownEventMock = mock( Event.NativePreviewEvent.class );
    doReturn( Event.ONMOUSEDOWN ).when( mouseDownEventMock ).getTypeInt();
    doReturn( false ).when( mouseDownEventMock ).isCanceled();
    doReturn( nativeMouseDownEventMock ).when( mouseDownEventMock ).getNativeEvent();

    boxMock.onPreviewNativeEvent( mouseDownEventMock );

    verify( nativeMouseDownEventMock, times( 1 ) ).preventDefault();
  }

  @Test
  public void testClickOnNonFocusableDialogElementAndAlreadyCanceledEventDoesNotPreventDefault() {
    DialogBox boxMock = mock( DialogBox.class );
    doCallRealMethod().when( boxMock ).onPreviewNativeEvent( any() );
    doReturn( true ).when( boxMock ).eventTargetsPopupOrPartner( any() );
    doReturn( false ).when( boxMock ).eventTargetIsFocusable( any() );

    NativeEvent nativeMouseDownEventMock = mock( NativeEvent.class );

    Event.NativePreviewEvent mouseDownEventMock = mock( Event.NativePreviewEvent.class );
    doReturn( Event.ONMOUSEDOWN ).when( mouseDownEventMock ).getTypeInt();
    doReturn( true ).when( mouseDownEventMock ).isCanceled();
    doReturn( nativeMouseDownEventMock ).when( mouseDownEventMock ).getNativeEvent();

    boxMock.onPreviewNativeEvent( mouseDownEventMock );

    verify( nativeMouseDownEventMock, never() ).preventDefault();
  }

  @Test
  public void testClickOnFocusableDialogElementDoesNotPreventDefault() {
    DialogBox boxMock = mock( DialogBox.class );
    doCallRealMethod().when( boxMock ).onPreviewNativeEvent( any() );
    doReturn( true ).when( boxMock ).eventTargetsPopupOrPartner( any() );
    doReturn( true ).when( boxMock ).eventTargetIsFocusable( any() );

    NativeEvent nativeMouseDownEventMock = mock( NativeEvent.class );

    Event.NativePreviewEvent mouseDownEventMock = mock( Event.NativePreviewEvent.class );
    doReturn( Event.ONMOUSEDOWN ).when( mouseDownEventMock ).getTypeInt();
    doReturn( false ).when( mouseDownEventMock ).isCanceled();
    doReturn( nativeMouseDownEventMock ).when( mouseDownEventMock ).getNativeEvent();

    boxMock.onPreviewNativeEvent( mouseDownEventMock );

    verify( nativeMouseDownEventMock, never() ).preventDefault();
  }

  @Test
  public void testClickOnFocusableElementOutsideDialogDoesNotPreventDefault() {
    DialogBox boxMock = mock( DialogBox.class );
    doCallRealMethod().when( boxMock ).onPreviewNativeEvent( any() );
    doReturn( false ).when( boxMock ).eventTargetsPopupOrPartner( any() );
    doReturn( true ).when( boxMock ).eventTargetIsFocusable( any() );

    NativeEvent nativeMouseDownEventMock = mock( NativeEvent.class );

    Event.NativePreviewEvent mouseDownEventMock = mock( Event.NativePreviewEvent.class );
    doReturn( Event.ONMOUSEDOWN ).when( mouseDownEventMock ).getTypeInt();
    doReturn( false ).when( mouseDownEventMock ).isCanceled();
    doReturn( nativeMouseDownEventMock ).when( mouseDownEventMock ).getNativeEvent();

    boxMock.onPreviewNativeEvent( mouseDownEventMock );

    verify( nativeMouseDownEventMock, never() ).preventDefault();
  }

  @Test
  public void testClickNonFocusableElementOutsideDialogDoesNotPreventDefault() {
    DialogBox boxMock = mock( DialogBox.class );
    doCallRealMethod().when( boxMock ).onPreviewNativeEvent( any() );
    doReturn( false ).when( boxMock ).eventTargetsPopupOrPartner( any() );
    doReturn( false ).when( boxMock ).eventTargetIsFocusable( any() );

    NativeEvent nativeMouseDownEventMock = mock( NativeEvent.class );

    Event.NativePreviewEvent mouseDownEventMock = mock( Event.NativePreviewEvent.class );
    doReturn( Event.ONMOUSEDOWN ).when( mouseDownEventMock ).getTypeInt();
    doReturn( false ).when( mouseDownEventMock ).isCanceled();
    doReturn( nativeMouseDownEventMock ).when( mouseDownEventMock ).getNativeEvent();

    boxMock.onPreviewNativeEvent( mouseDownEventMock );

    verify( nativeMouseDownEventMock, never() ).preventDefault();
  }
  // endregion

  // region focus
  @Test
  public void testSetFocusWidgetRespectsGivenValue() throws Exception {
    DialogBox boxSpy = spy( new DialogBox() );

    // Non-null
    FocusWidget focusWidgetMock = mock( FocusWidget.class );
    when( focusWidgetMock.getElement() ).thenReturn( mock( Element.class ) );

    boxSpy.setFocusWidget( focusWidgetMock );

    assertSame( focusWidgetMock, boxSpy.getFocusWidget() );

    // Null
    boxSpy.setFocusWidget( null );

    assertNull( boxSpy.getFocusWidget() );
  }

  @Test
  public void testSetFocusWidgetCallsDoAutoFocusWhenGivenNonNull() throws Exception {
    DialogBox boxSpy = spy( new DialogBox() );
    doReturn( true ).when( boxSpy ).isShowing();
    doReturn( true ).when( boxSpy ).isVisible();

    FocusWidget focusWidgetMock = mock( FocusWidget.class );
    when( focusWidgetMock.getElement() ).thenReturn( mock( Element.class ) );

    boxSpy.setFocusWidget( focusWidgetMock );

    verify( boxSpy, times( 1 ) ).doAutoFocus();

    assertSame( focusWidgetMock, boxSpy.getFocusWidget() );
  }

  @Test
  public void testSetFocusWidgetDoesNotCallAutoFocusWhenGivenNull() throws Exception {
    DialogBox boxSpy = spy( new DialogBox() );

    boxSpy.setFocusWidget( null );

    verify( boxSpy, never() ).doAutoFocus();
  }

  @Test
  public void testSetFocusWidgetRemovesAutoFocusAttributeFromPreviousElement() throws Exception {
    DialogBox boxSpy = spy( new DialogBox() );

    FocusWidget previousFocusWidgetMock = mock( FocusWidget.class );
    when( previousFocusWidgetMock.getElement() ).thenReturn( mock( Element.class ) );

    boxSpy.setFocusWidget( previousFocusWidgetMock );

    boxSpy.setFocusWidget( null );

    verify( previousFocusWidgetMock.getElement(), times( 1 ) ).removeAttribute( "autofocus" );
  }

  @Test
  public void testSetFocusWidgetSetsAutoFocusAttributeOnNewElement() throws Exception {
    DialogBox boxSpy = spy( new DialogBox() );

    FocusWidget focusWidgetMock = mock( FocusWidget.class );
    when( focusWidgetMock.getElement() ).thenReturn( mock( Element.class ) );

    boxSpy.setFocusWidget( focusWidgetMock );

    verify( focusWidgetMock.getElement(), times( 1 ) ).setAttribute( "autofocus", "" );
  }

  @Test
  public void testDoAutoFocusOpensJsDialogWhenShowingAndVisible() {
    DialogBox boxSpy = spy( new DialogBox() );
    doReturn( true ).when( boxSpy ).isShowing();
    doReturn( true ).when( boxSpy ).isVisible();

    boxSpy.doAutoFocus();

    verify( boxSpy ).autoFocusOpenJsDialog();
  }

  @Test
  public void testDoAutoFocusDoesNotOpensJsDialogWhenNotShowingOrNotVisible() {
    DialogBox boxSpy = spy( new DialogBox() );

    // Case 1
    doReturn( false ).when( boxSpy ).isShowing();
    doReturn( true ).when( boxSpy ).isVisible();

    boxSpy.doAutoFocus();

    verify( boxSpy, never() ).autoFocusOpenJsDialog();

    // Case 2
    doReturn( true ).when( boxSpy ).isShowing();
    doReturn( false ).when( boxSpy ).isVisible();

    boxSpy.doAutoFocus();

    // Case 3
    doReturn( false ).when( boxSpy ).isShowing();
    doReturn( false ).when( boxSpy ).isVisible();

    boxSpy.doAutoFocus();

    verify( boxSpy, never() ).autoFocusOpenJsDialog();
  }

  @Test
  public void testSetRestoreFocus() {
    DialogBox boxSpy = spy( new DialogBox() );

    boxSpy.setRestoreFocus( true );

    verify( boxSpy ).syncOpenJsDialogRestoreFocus();

    assertTrue( boxSpy.isRestoreFocus() );

    // ---

    boxSpy.setRestoreFocus( false );

    assertFalse( boxSpy.isRestoreFocus() );
  }

  @Test
  public void testSetRestoreFocusWidget() {
    DialogBox boxSpy = spy( new DialogBox() );

    final Focusable focusableMock = mock( Focusable.class );
    boxSpy.setRestoreFocusWidget( focusableMock );

    verify( boxSpy ).syncOpenJsDialogRestoreFocus();

    assertSame( focusableMock, boxSpy.getRestoreFocusWidget() );
  }

  @Test
  public void testSetFocusButtons() {
    DialogBox boxSpy = spy( new DialogBox() );

    @SuppressWarnings( "unchecked" )
    List<Focusable> focusButtonsMock = (List<Focusable>) mock( List.class );

    boxSpy.setFocusButtons( focusButtonsMock );

    verify( boxSpy ).syncOpenJsDialogButtons();

    assertThat( boxSpy.getFocusButtons(), is( focusButtonsMock ) );
  }
  // endregion

  // region responsive property
  @Test
  public void testResponsiveDefaultsToFalse() {
    DialogBox box = new DialogBox();

    assertFalse( box.isResponsive() );
  }

  @Test
  public void testSetAndGetResponsive() {
    DialogBox boxSpy = spy( new DialogBox() );

    boxSpy.setResponsive( true );
    assertTrue( boxSpy.isResponsive() );
    verify( boxSpy ).addStyleName( "responsive" );

    boxSpy.setResponsive( false );
    assertFalse( boxSpy.isResponsive() );
    verify( boxSpy ).removeStyleName( "responsive" );
  }

  @Test
  public void testSetResponsiveRespectsTheSpecifiedValue() {
    DialogBox boxSpy = spy( new DialogBox() );

    boxSpy.setResponsive( true );
    assertTrue( boxSpy.isResponsive() );

    boxSpy.setResponsive( false );
    assertFalse( boxSpy.isResponsive() );
  }

  @Test
  public void testSetResponsiveAddsResponsiveStyleNameWhenTrue() {
    DialogBox boxSpy = spy( new DialogBox() );

    boxSpy.setResponsive( true );
    verify( boxSpy ).addStyleName( "responsive" );
  }

  @Test
  public void testSetResponsiveRemovesResponsiveStyleNameWhenFalse() {
    DialogBox boxSpy = spy( new DialogBox() );
    boxSpy.setResponsive( true );

    boxSpy.setResponsive( false );

    verify( boxSpy ).removeStyleName( "responsive" );
  }
  // endregion

  // region sizingMode property
  @Test( expected = IllegalArgumentException.class )
  public void testSetSizingModeThrowsWhenGivenNull() {
    DialogBox box = new DialogBox();
    box.setSizingMode( null );
  }

  @Test
  public void testDefaultSizingModeIsSizeToContent() {
    DialogBox box = new DialogBox();
    assertEquals( DialogSizingMode.SIZE_TO_CONTENT, box.getSizingMode() );
  }

  @Test
  public void testSetSizingModeRespectsSpecifiedValue() {
    DialogBox box = new DialogBox();

    assertNotEquals( box.getSizingMode(), DialogSizingMode.FULL_VIEWPORT );

    box.setSizingMode( DialogSizingMode.FULL_VIEWPORT );

    assertEquals( DialogSizingMode.FULL_VIEWPORT, box.getSizingMode() );
  }

  @Test
  public void testSetSizingModeRemovesCssClassOfPreviousSizingMode() {
    DialogBox box = spy( new DialogBox() );

    box.setSizingMode( DialogSizingMode.FULL_VIEWPORT );

    box.setSizingMode( DialogSizingMode.FILL_VIEWPORT_WIDTH );

    verify( box ).removeStyleName( DialogSizingMode.FULL_VIEWPORT.getStyleName() );
  }

  @Test
  public void testSetSizingModeDoesNotRemovePreviousCssClassWhenItIsNull() {
    DialogBox box = spy( new DialogBox() );

    assertNull( DialogSizingMode.SIZE_TO_CONTENT.getStyleName() );

    box.setSizingMode( DialogSizingMode.SIZE_TO_CONTENT );

    box.setSizingMode( DialogSizingMode.FULL_VIEWPORT );

    verify( box, never() ).removeStyleName( any() );
  }

  @Test
  public void testSetSizingModeAddsCssClassOfNewSizingModeWhenItIsNotNull() {
    DialogBox box = spy( new DialogBox() );

    box.setSizingMode( DialogSizingMode.FILL_VIEWPORT_WIDTH );

    verify( box ).addStyleName( DialogSizingMode.FILL_VIEWPORT_WIDTH.getStyleName() );
  }

  @Test
  public void testSetSizingModeDoesNotAddCssClassOfNewSizingModeWhenItIsNull() {
    DialogBox box = spy( new DialogBox() );

    box.setSizingMode( DialogSizingMode.FILL_VIEWPORT_WIDTH );

    box.setSizingMode( DialogSizingMode.SIZE_TO_CONTENT );
    assertNull( DialogSizingMode.SIZE_TO_CONTENT.getStyleName() );

    verify( box, never() ).addStyleName( DialogSizingMode.SIZE_TO_CONTENT.getStyleName() );
  }
  // endregion

  // region widthCategory property
  @Test( expected = IllegalArgumentException.class )
  public void testSetWidthCategoryThrowsWhenGivenNull() {
    DialogBox box = new DialogBox();
    box.setWidthCategory( null );
  }

  @Test
  public void testDefaultWidthCategoryIsMaximum() {
    DialogBox box = new DialogBox();
    assertEquals( DialogWidthCategory.MAXIMUM, box.getWidthCategory() );
  }

  @Test
  public void testSetWidthCategoryRespectsSpecifiedValue() {
    DialogBox box = new DialogBox();

    box.setWidthCategory( DialogWidthCategory.MAXIMUM );

    box.setWidthCategory( DialogWidthCategory.MINIMUM );

    assertEquals( DialogWidthCategory.MINIMUM, box.getWidthCategory() );
  }

  @Test
  public void testSetWidthCategoryRemovesCssClassOfPreviousWidthCategory() {
    DialogBox box = spy( new DialogBox() );

    box.setWidthCategory( DialogWidthCategory.MINIMUM );

    box.setWidthCategory( DialogWidthCategory.TEXT );

    verify( box ).removeStyleName( DialogWidthCategory.MINIMUM.getStyleName() );
  }

  @Test
  public void testSetWidthCategoryDoesNotRemovePreviousCssClassWhenItIsNull() {
    DialogBox box = spy( new DialogBox() );

    assertNull( DialogWidthCategory.MAXIMUM.getStyleName() );
    box.setWidthCategory( DialogWidthCategory.MAXIMUM );

    box.setWidthCategory( DialogWidthCategory.MINIMUM );

    verify( box, never() ).removeStyleName( any() );
  }

  @Test
  public void testSetWidthCategoryAddsCssClassOfNewWidthCategoryWhenItIsNotNull() {
    DialogBox box = spy( new DialogBox() );

    box.setWidthCategory( DialogWidthCategory.MINIMUM );

    verify( box ).addStyleName( DialogWidthCategory.MINIMUM.getStyleName() );
  }

  @Test
  public void testSetWidthCategoryDoesNotAddCssClassOfNewWidthCategoryWhenItIsNull() {
    DialogBox box = spy( new DialogBox() );

    box.setWidthCategory( DialogWidthCategory.MINIMUM );

    box.setWidthCategory( DialogWidthCategory.MAXIMUM );
    assertNull( DialogWidthCategory.MAXIMUM.getStyleName() );

    verify( box, never() ).addStyleName( DialogWidthCategory.MAXIMUM.getStyleName() );
  }
  // endregion

  // region minimumHeightCategory property
  @Test( expected = IllegalArgumentException.class )
  public void testSetMinimumHeightCategoryThrowsWhenGivenNull() {
    DialogBox box = new DialogBox();
    box.setMinimumHeightCategory( null );
  }

  @Test
  public void testDefaultMinimumHeightCategoryIsMinimum() {
    DialogBox box = new DialogBox();
    assertEquals( DialogMinimumHeightCategory.MINIMUM, box.getMinimumHeightCategory() );
  }

  @Test
  public void testSetMinimumHeightCategoryRespectsSpecifiedValue() {
    DialogBox box = new DialogBox();

    box.setMinimumHeightCategory( DialogMinimumHeightCategory.MINIMUM );

    box.setMinimumHeightCategory( DialogMinimumHeightCategory.CONTENT );

    assertEquals( DialogMinimumHeightCategory.CONTENT, box.getMinimumHeightCategory() );
  }

  @Test
  public void testSetMinimumHeightCategoryRemovesCssClassOfPreviousCategory() {
    DialogBox boxSpy = spy( new DialogBox() );

    boxSpy.setMinimumHeightCategory( DialogMinimumHeightCategory.CONTENT );

    boxSpy.setMinimumHeightCategory( DialogMinimumHeightCategory.MINIMUM );

    verify( boxSpy ).removeStyleName( DialogMinimumHeightCategory.CONTENT.getStyleName() );
  }

  @Test
  public void testSetMinimumHeightCategoryDoesNotRemovePreviousCssClassWhenItIsNull() {
    DialogBox boxSpy = spy( new DialogBox() );

    assertNull( DialogMinimumHeightCategory.MINIMUM.getStyleName() );
    boxSpy.setMinimumHeightCategory( DialogMinimumHeightCategory.MINIMUM );

    boxSpy.setMinimumHeightCategory( DialogMinimumHeightCategory.CONTENT );

    verify( boxSpy, never() ).removeStyleName( any() );
  }

  @Test
  public void testSetMinimumHeightCategoryAddsCssClassOfNewCategoryWhenItIsNotNull() {
    DialogBox boxSpy = spy( new DialogBox() );

    boxSpy.setMinimumHeightCategory( DialogMinimumHeightCategory.CONTENT );

    verify( boxSpy ).addStyleName( DialogMinimumHeightCategory.CONTENT.getStyleName() );
  }

  @Test
  public void testSetMinimumHeightCategoryDoesNotAddCssClassOfNewCategoryWhenItIsNull() {
    DialogBox boxSpy = spy( new DialogBox() );

    boxSpy.setMinimumHeightCategory( DialogMinimumHeightCategory.CONTENT );

    boxSpy.setMinimumHeightCategory( DialogMinimumHeightCategory.MINIMUM );
    assertNull( DialogMinimumHeightCategory.MINIMUM.getStyleName() );

    verify( boxSpy, never() ).addStyleName( DialogMinimumHeightCategory.MINIMUM.getStyleName() );
  }
  // endregion

  // region ariaRole property
  @Test
  public void testSetAriaRoleStringDefaultsToDialogWhenGivenNull() {
    DialogBox boxSpy = spy( new DialogBox() );
    Element elementMock = mock( Element.class );
    doReturn( elementMock ).when( boxSpy ).getElement();

    boxSpy.setAriaRole( (String) null );

    verify( elementMock, times( 1 ) ).setAttribute( "role", "dialog" );
  }

  @Test
  public void testSetAriaRoleStringDefaultsToDialogWhenGivenEmpty() {
    DialogBox boxSpy = spy( new DialogBox() );
    Element elementMock = mock( Element.class );
    doReturn( elementMock ).when( boxSpy ).getElement();

    boxSpy.setAriaRole( "" );

    verify( elementMock, times( 1 ) ).setAttribute( "role", "dialog" );
  }

  @Test
  public void testSetAriaRoleStringRespectsTheGivenValue() {
    DialogBox boxSpy = spy( new DialogBox() );
    Element elementMock = mock( Element.class );
    doReturn( elementMock ).when( boxSpy ).getElement();

    Role role = Roles.getAlertdialogRole();
    boxSpy.setAriaRole( role.getName() );

    verify( elementMock, times( 1 ) ).setAttribute( "role", "alertdialog" );
  }

  @Test
  public void testSetAriaRoleObjectRespectsTheGivenValue() {
    DialogBox boxSpy = spy( new DialogBox() );
    Element elementMock = mock( Element.class );
    doReturn( elementMock ).when( boxSpy ).getElement();

    Role role = Roles.getAlertdialogRole();
    boxSpy.setAriaRole( role );

    verify( elementMock, times( 1 ) ).setAttribute( "role", "alertdialog" );
  }

  @Test
  public void testSetAriaRoleObjectDefaultsToDialogWhenGivenNull() {
    DialogBox boxSpy = spy( new DialogBox() );
    Element elementMock = mock( Element.class );
    doReturn( elementMock ).when( boxSpy ).getElement();

    boxSpy.setAriaRole( (Role) null );

    verify( elementMock, times( 1 ) ).setAttribute( "role", "dialog" );
  }

  @Test
  public void testGetAriaRoleReturnsTheRoleAttributeValue() {
    DialogBox boxSpy = spy( new DialogBox() );

    Element elementMock = mock( Element.class );
    Role role = Roles.getAlertdialogRole();
    doReturn( role.getName() ).when( elementMock ).getAttribute( "role" );

    doReturn( elementMock ).when( boxSpy ).getElement();

    assertEquals( "alertdialog", boxSpy.getAriaRole() );
  }
  // endregion

  // region ariaDescribedBy property
  @Test
  public void testSetAriaDescribedByRemovesTheAttributeWhenGivenNull() {
    DialogBox boxSpy = spy( new DialogBox() );
    Element elementMock = mock( Element.class );
    doReturn( elementMock ).when( boxSpy ).getElement();

    boxSpy.setAriaDescribedBy( null );

    verify( elementMock, times( 1 ) ).removeAttribute( "aria-describedby" );
  }

  @Test
  public void testSetAriaDescribedByRemovesTheAttributeWhenGivenEmpty() {
    DialogBox boxSpy = spy( new DialogBox() );
    Element elementMock = mock( Element.class );
    doReturn( elementMock ).when( boxSpy ).getElement();

    boxSpy.setAriaDescribedBy( "" );

    verify( elementMock, times( 1 ) ).removeAttribute( "aria-describedby" );
  }

  @Test
  public void testSetAriaDescribedBySetsTheAttributeWhenGivenNonEmpty() {
    DialogBox boxSpy = spy( new DialogBox() );
    Element elementMock = mock( Element.class );
    doReturn( elementMock ).when( boxSpy ).getElement();

    String describedById = "description-id";
    boxSpy.setAriaDescribedBy( describedById );

    verify( elementMock, times( 1 ) ).setAttribute( "aria-describedby", describedById );
  }

  @Test
  public void testGetAriaDescribedByGetsTheAttribute() {
    DialogBox boxSpy = spy( new DialogBox() );
    Element elementMock = mock( Element.class );
    String describedById = "description-id";
    doReturn( describedById ).when( elementMock ).getAttribute( "aria-describedby" );

    doReturn( elementMock ).when( boxSpy ).getElement();

    assertEquals( describedById, boxSpy.getAriaDescribedBy() );
  }
  // endregion

  // region modal property
  @Test
  public void testSetModalToTrueAddsTheCssClassModal() {
    DialogBox boxSpy = spy( new DialogBox() );
    Element elementMock = mock( Element.class );
    doReturn( elementMock ).when( boxSpy ).getElement();

    boxSpy.setModal( true );

    verify( boxSpy, times( 1 ) ).addStyleName( "modal" );
  }

  @Test
  public void testSetModalToTrueSetsAriaModalAttributeToTrue() {
    DialogBox boxSpy = spy( new DialogBox() );
    Element elementMock = mock( Element.class );
    doReturn( elementMock ).when( boxSpy ).getElement();

    boxSpy.setModal( true );

    verify( elementMock, times( 1 ) ).setAttribute( "aria-modal", "true" );
  }

  @Test
  public void testSetModalToFalseRemovesTheCssClassModal() {
    DialogBox boxSpy = spy( new DialogBox() );
    Element elementMock = mock( Element.class );
    doReturn( elementMock ).when( boxSpy ).getElement();

    boxSpy.setModal( false );

    verify( boxSpy, times( 1 ) ).removeStyleName( "modal" );
  }

  @Test
  public void testSetModalToFalseSetsAriaModalAttributeToFalse() {
    DialogBox boxSpy = spy( new DialogBox() );
    Element elementMock = mock( Element.class );
    doReturn( elementMock ).when( boxSpy ).getElement();

    boxSpy.setModal( false );

    verify( elementMock, times( 1 ) ).setAttribute( "aria-modal", "false" );
  }
  // endregion

  // region hide()
  @Test
  public void testHideUnregistersResizeHandler() {
    DialogBox boxSpy = createDialogBoxSpyForShowHide();

    boxSpy.hide( false );

    verify( boxSpy ).unregisterResizeHandler();
  }

  @Test
  public void testHideClosesOpenJsDialog() {
    DialogBox boxSpy = createDialogBoxSpyForShowHide();

    boxSpy.hide( false );

    verify( boxSpy ).closeOpenJsDialog();
  }
  // endregion

  // region show()
  @Test
  public void testShowWhenShowingReturnsImmediately() {
    DialogBox boxSpy = createDialogBoxSpyForShowHide();
    doReturn( true ).when( boxSpy ).isShowing();

    boxSpy.show();

    verify( boxSpy, never() ).initializeResizeHandler();
  }

  @Test
  public void testShowInitializesResizeHandler() {
    DialogBox boxSpy = createDialogBoxSpyForShowHide();

    boxSpy.show();

    verify( boxSpy, times( 1 ) ).initializeResizeHandler();
  }

  @Test
  public void testShowWhenModalBlocksBackground() {
    DialogBox boxSpy = createDialogBoxSpyForShowHide();

    boxSpy.show();

    verify( boxSpy, times( 1 ) ).initializePageBackground();
    verify( boxSpy, times( 1 ) ).blockPageBackground();

    FocusPanel pageBgPanelSpy = boxSpy.getPageBackgroundInternal();
    verify( pageBgPanelSpy, times( 1 ) ).setSize( any(), any() );
    verify( pageBgPanelSpy, times( 1 ) ).setVisible( true );
    verify( pageBgPanelSpy.getElement().getStyle(), times( 1 ) ).setDisplay( Style.Display.BLOCK );
  }

  @Test
  public void testShowWhenNotModalDoesNotBlockBackground() {
    DialogBox boxSpy = createDialogBoxSpyForShowHide();
    doReturn( false ).when( boxSpy ).isModal();

    boxSpy.show();

    verify( boxSpy, never() ).initializePageBackground();
    verify( boxSpy, never() ).blockPageBackground();
  }

  @Test
  public void testShowHidesEmbeds() {
    DialogBox boxSpy = createDialogBoxSpyForShowHide();

    boxSpy.show();

    verify( boxSpy, times( 1 ) ).toggleEmbedVisibility( false );
  }

  @Test
  public void testShowCallsShowOnGlassPane() {
    DialogBox boxSpy = createDialogBoxSpyForShowHide();

    boxSpy.show();

    verify( boxSpy.getGlassPane(), times( 1 ) ).show();
  }

  @Test
  public void testShowOpensJsDialog() {
    DialogBox boxSpy = createDialogBoxSpyForShowHide();

    boxSpy.show();

    verify( boxSpy, times( 1 ) ).openJsDialog();
  }
  // endregion

  // region center()
  @Test
  public void testCenterDoesAutoFocus() {
    DialogBox boxSpy = createDialogBoxSpyForShowHide();

    boxSpy.center();

    verify( boxSpy, times( 1 ) ).doAutoFocus();
  }
  // endregion

  // region setPopupPositionAndShow(.)
  @Test
  public void testSetPopupPositionAndShowDoesAutoFocus() {
    DialogBox boxSpy = createDialogBoxSpyForShowHide();

    boxSpy.setPopupPositionAndShow( new PopupPanel.PositionCallback() {
      @Override
      public void setPosition( int offsetWidth, int offsetHeight ) {

      }
    } );

    verify( boxSpy, times( 1 ) ).doAutoFocus();
  }
  // endregion
}
