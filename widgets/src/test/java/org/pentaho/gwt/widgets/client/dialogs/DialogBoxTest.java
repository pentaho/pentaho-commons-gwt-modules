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
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.gwt.widgets.client.dialogs.DialogBox.DialogMinimumHeightCategory;
import org.pentaho.gwt.widgets.client.dialogs.DialogBox.DialogSizingMode;
import org.pentaho.gwt.widgets.client.dialogs.DialogBox.DialogWidthCategory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
@SuppressWarnings( "deprecation" )
public class DialogBoxTest {
  @Test
  public void testOnKeyDownPreview() throws Exception {
    DialogBox box = mock( DialogBox.class );
    doCallRealMethod().when( box ).onKeyDownPreview( anyChar(), anyInt() );

    box.onKeyDownPreview( (char) KeyboardListener.KEY_ESCAPE, 0 );
    verify( box, times( 1 ) ).hide();
  }

  @Test
  public void testSetFocusWidget() throws Exception {
    DialogBox box = mock( DialogBox.class );
    doCallRealMethod().when( box ).setFocusWidget( any( FocusWidget.class ) );
    doCallRealMethod().when( box ).getAutoFocusWidget();
    doCallRealMethod().when( box ).doAutoFocus();

    doReturn( true ).when( box ).isShowing();
    doReturn( true ).when( box ).isVisible();

    final FocusWidget focusWidget = mock( FocusWidget.class );
    box.setFocusWidget( focusWidget );
    verify( focusWidget ).setFocus( true );
  }

  // region responsive property
  @Test
  public void testResponsiveDefaultsToFalse() {
    DialogBox box = new DialogBox();

    assertFalse( box.isResponsive() );
  }

  @Test
  public void testSetAndGetResponsive() {
    DialogBox box = spy( new DialogBox() );

    box.setResponsive( true );
    assertTrue( box.isResponsive() );
    verify( box ).addStyleName( "responsive" );

    box.setResponsive( false );
    assertFalse( box.isResponsive() );
    verify( box ).removeStyleName( "responsive" );
  }

  @Test
  public void testSetResponsiveRespectsTheSpecifiedValue() {
    DialogBox box = spy( new DialogBox() );

    box.setResponsive( true );
    assertTrue( box.isResponsive() );

    box.setResponsive( false );
    assertFalse( box.isResponsive() );
  }

  @Test
  public void testSetResponsiveAddsResponsiveStyleNameWhenTrue() {
    DialogBox box = spy( new DialogBox() );

    box.setResponsive( true );
    verify( box ).addStyleName( "responsive" );
  }

  @Test
  public void testSetResponsiveRemovesResponsiveStyleNameWhenFalse() {
    DialogBox box = spy( new DialogBox() );
    box.setResponsive( true );

    box.setResponsive( false );

    verify( box ).removeStyleName( "responsive" );
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
    DialogBox box = spy( new DialogBox() );

    box.setMinimumHeightCategory( DialogMinimumHeightCategory.CONTENT );

    box.setMinimumHeightCategory( DialogMinimumHeightCategory.MINIMUM );

    verify( box ).removeStyleName( DialogMinimumHeightCategory.CONTENT.getStyleName() );
  }

  @Test
  public void testSetMinimumHeightCategoryDoesNotRemovePreviousCssClassWhenItIsNull() {
    DialogBox box = spy( new DialogBox() );

    assertNull( DialogMinimumHeightCategory.MINIMUM.getStyleName() );
    box.setMinimumHeightCategory( DialogMinimumHeightCategory.MINIMUM );

    box.setMinimumHeightCategory( DialogMinimumHeightCategory.CONTENT );

    verify( box, never() ).removeStyleName( any() );
  }

  @Test
  public void testSetMinimumHeightCategoryAddsCssClassOfNewCategoryWhenItIsNotNull() {
    DialogBox box = spy( new DialogBox() );

    box.setMinimumHeightCategory( DialogMinimumHeightCategory.CONTENT );

    verify( box ).addStyleName( DialogMinimumHeightCategory.CONTENT.getStyleName() );
  }

  @Test
  public void testSetMinimumHeightCategoryDoesNotAddCssClassOfNewCategoryWhenItIsNull() {
    DialogBox box = spy( new DialogBox() );

    box.setMinimumHeightCategory( DialogMinimumHeightCategory.CONTENT );

    box.setMinimumHeightCategory( DialogMinimumHeightCategory.MINIMUM );
    assertNull( DialogMinimumHeightCategory.MINIMUM.getStyleName() );

    verify( box, never() ).addStyleName( DialogMinimumHeightCategory.MINIMUM.getStyleName() );
  }
  // endregion
}
