/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
import static org.mockito.Mockito.anyChar;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
  public void testOnOk() {
    PromptDialogBox box = mock( PromptDialogBox.class );

    final IDialogCallback callback = mock( IDialogCallback.class );
    box.callback = callback;

    final IDialogValidatorCallback validatorCallback = mock( IDialogValidatorCallback.class );
    box.validatorCallback = validatorCallback;

    doCallRealMethod().when( box ).onOk();
    doCallRealMethod().when( box ).onOkValid();

    box.onOk();

    verify( callback, never() ).okPressed();
    verify( validatorCallback ).validate();
    verify( box, never() ).hide();

    when( validatorCallback.validate() ).thenReturn( true );
    box.onOk();

    verify( callback ).okPressed();
    verify( validatorCallback, times( 2 ) ).validate();
    verify( box ).hide();
  }

  @Test
  public void testOnNotOk() {
    PromptDialogBox box = mock( PromptDialogBox.class );
    final IThreeButtonDialogCallback callback = mock( IThreeButtonDialogCallback.class );
    box.callback = callback;
    doCallRealMethod().when( box ).onNotOk();

    box.onNotOk();

    verify( callback ).notOkPressed();
    verify( box ).hide();
  }

  @Test
  public void testOnCancel() {
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
