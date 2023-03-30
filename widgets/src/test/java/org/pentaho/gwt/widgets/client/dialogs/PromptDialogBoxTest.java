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

import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class PromptDialogBoxTest {

  public static class PromptDialogBoxForTesting extends PromptDialogBox {
    public PromptDialogBoxForTesting( String title, String okText, String notOkText, String cancelText ) {
      super( title, okText, notOkText, cancelText );
    }

    @Override
    void initializeDialogContent( HorizontalPanel dialogButtonPanelWrapper ) {
      // Does not behave well with testing.
    }
  }

  @Test
  @SuppressWarnings( "deprecation" )
  public void testOnKeyDownPreview() throws Exception {
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

  // region Focus Buttons
  @Test
  public void testSetFocusButtonsOrderIsCancelThenNotOkThenOk() {
    PromptDialogBox boxSpy = new PromptDialogBoxForTesting( "title", "ok", "not ok", "cancel" );

    List<Focusable> focusButtons = boxSpy.getFocusButtons();
    assertEquals( 3, focusButtons.size() );
    assertSame( boxSpy.cancelButton, focusButtons.get( 0 ) );
    assertSame( boxSpy.notOkButton, focusButtons.get( 1 ) );
    assertSame( boxSpy.okButton, focusButtons.get( 2 ) );
  }

  @Test
  public void testSetFocusButtonsOnlyContainsDefinedButtons() {
    // Cancel, Ok
    PromptDialogBox box = new PromptDialogBoxForTesting( "title", "ok", null, "cancel" );

    List<Focusable> focusButtons = box.getFocusButtons();
    assertEquals( 2, focusButtons.size() );
    assertSame( box.cancelButton, focusButtons.get( 0 ) );
    assertSame( box.okButton, focusButtons.get( 1 ) );

    // NotOk, Ok
    box = new PromptDialogBoxForTesting( "title", "ok", "not ok", null );

    focusButtons = box.getFocusButtons();
    assertEquals( 2, focusButtons.size() );
    assertSame( box.notOkButton, focusButtons.get( 0 ) );
    assertSame( box.okButton, focusButtons.get( 1 ) );

    // Ok
    box = new PromptDialogBoxForTesting( "title", "ok", null, null );

    focusButtons = box.getFocusButtons();
    assertEquals( 1, focusButtons.size() );
    assertSame( box.okButton, focusButtons.get( 0 ) );
  }
  // endregion
}
