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

package org.pentaho.gwt.widgets.client.dialogs;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Widget;
import org.pentaho.gwt.widgets.client.panel.HorizontalFlexPanel;

import java.util.ArrayList;
import java.util.List;

import static org.pentaho.gwt.widgets.client.wizards.AbstractWizardDialog.PENTAHO_BUTTON;

@SuppressWarnings( "deprecation" )
public class PromptDialogBox extends DialogBox {

  IDialogCallback callback;
  IDialogValidatorCallback validatorCallback;
  Widget content;
  final FlexTable dialogContent = new FlexTable();

  protected Button okButton;
  protected Button notOkButton = null;
  protected Button cancelButton = null;

  public PromptDialogBox( String title, String okText, String notOkText, String cancelText, boolean autoHide,
                          boolean modal ) {
    super( autoHide, modal );

    setText( title );

    HorizontalPanel dialogButtonPanelWrapper = createButtonPanelWrapper( okText, notOkText, cancelText );

    initializeDialogContent( dialogButtonPanelWrapper );

    setFocusButtons( buildFocusButtons() );
  }

  private HorizontalPanel createButtonPanelWrapper( String okText, String notOkText, String cancelText ) {
    final HorizontalFlexPanel dialogButtonPanel = new HorizontalFlexPanel();
    dialogButtonPanel.addStyleName( "inner-button-wrapper" );

    okButton = new Button( okText );
    okButton.setTitle( okText );
    okButton.setStylePrimaryName( PENTAHO_BUTTON );
    okButton.getElement().setAttribute( "id", "okButton" );
    okButton.addClickListener( sender -> onOk() );
    dialogButtonPanel.add( okButton );

    if ( notOkText != null ) {
      notOkButton = new Button( notOkText );
      notOkButton.setTitle( notOkText );
      notOkButton.setStylePrimaryName( PENTAHO_BUTTON );
      notOkButton.getElement().setAttribute( "id", "notOkButton" );
      notOkButton.addClickListener( sender -> onNotOk() );
      dialogButtonPanel.add( notOkButton );
    }

    if ( cancelText != null ) {
      cancelButton = new Button( cancelText );
      cancelButton.setTitle( cancelText );
      cancelButton.setStylePrimaryName( PENTAHO_BUTTON );
      cancelButton.getElement().setAttribute( "id", "cancelButton" );
      cancelButton.addClickListener( sender -> onCancel() );
      dialogButtonPanel.add( cancelButton );
    }

    HorizontalPanel dialogButtonPanelWrapper = new HorizontalFlexPanel();

    if ( okText != null && cancelText != null ) {
      dialogButtonPanelWrapper.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_RIGHT );
    } else {
      dialogButtonPanelWrapper.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
    }

    dialogButtonPanelWrapper.addStyleName( "button-panel" );
    dialogButtonPanelWrapper.setWidth( "100%" );
    dialogButtonPanelWrapper.add( dialogButtonPanel );

    return dialogButtonPanelWrapper;
  }

  void initializeDialogContent( HorizontalPanel dialogButtonPanelWrapper ) {
    // Init `dialogContent`
    Roles.getPresentationRole().set( dialogContent.getElement() );
    dialogContent.setCellPadding( 0 );
    dialogContent.setCellSpacing( 0 );

    // add button panel
    dialogContent.setWidget( 1, 0, dialogButtonPanelWrapper );

    // Content Cell 0 - 0
    dialogContent.getCellFormatter().setStyleName( 0, 0, "dialog-content" );
    dialogContent.getFlexCellFormatter().setVerticalAlignment( 0, 0, HasVerticalAlignment.ALIGN_MIDDLE );
    dialogContent.getFlexCellFormatter().setHorizontalAlignment( 0, 0, HasHorizontalAlignment.ALIGN_CENTER );
    DOM.setStyleAttribute(
      dialogContent.getCellFormatter().getElement( 0, 0 ),
      "padding",
      "5px 10px 10px 10px" );

    // Actions Cell 1 - 0
    dialogContent.getCellFormatter().setVerticalAlignment( 1, 0, HasVerticalAlignment.ALIGN_BOTTOM );

    dialogContent.setWidth( "100%" );
    setWidget( dialogContent );
  }

  List<Focusable> buildFocusButtons() {
    ArrayList<Focusable> focusButtons = new ArrayList<>();
    if ( cancelButton != null ) {
      focusButtons.add( cancelButton );
    }

    if ( notOkButton != null ) {
      focusButtons.add( notOkButton );
    }

    focusButtons.add( okButton );

    return focusButtons;
  }

  public PromptDialogBox( String title, String okText, String notOkText, String cancelText ) {
    this( title, okText, notOkText, cancelText, false, true );
  }

  public PromptDialogBox( String title, String okText, String notOkText, String cancelText, boolean autoHide,
      boolean modal, Widget content ) {
    this( title, okText, notOkText, cancelText, autoHide, modal );
    setContent( content );
  }

  public PromptDialogBox( String title, String okText, String cancelText, boolean autoHide, boolean modal ) {
    this( title, okText, null, cancelText, autoHide, modal );
  }

  public PromptDialogBox( String title, String okText, String cancelText, boolean autoHide, boolean modal,
                          Widget content ) {

    this( title, okText, cancelText, autoHide, modal );
    setContent( content );
  }

  @Override
  public boolean onKeyDownPreview( char key, int modifiers ) {
    // Use the popup's key preview hooks to close the dialog when either
    // enter or escape is pressed.
    if ( key == KeyboardListener.KEY_ESCAPE ) {
      onCancel();
    }

    return true;
  }

  public IDialogCallback getCallback() {
    return callback;
  }

  public void setContent( Widget content ) {
    this.content = content;

    if ( content != null ) {
      content.setHeight( "100%" );
      content.setWidth( "100%" );
    }

    dialogContent.setWidget( 0, 0, content );

    if ( content instanceof FocusWidget ) {
      setFocusWidget( (FocusWidget) content );
    }
  }

  public Widget getContent() {
    return content;
  }

  public void setCallback( IDialogCallback callback ) {
    this.callback = callback;
  }

  public IDialogValidatorCallback getValidatorCallback() {
    return validatorCallback;
  }

  public void setValidatorCallback( IDialogValidatorCallback validatorCallback ) {
    this.validatorCallback = validatorCallback;
  }

  protected void onOk() {
    if ( validatorCallback == null || validatorCallback.validate() ) {
      onOkValid();
      hide();
    }
  }

  protected void onOkValid() {
    try {
      if ( callback != null ) {
        callback.okPressed();
      }
    } catch ( Exception ex ) {
      // ignore
    }
  }

  protected void onNotOk() {
    try {
      if ( callback instanceof IThreeButtonDialogCallback ) {
        ( (IThreeButtonDialogCallback) callback ).notOkPressed();
      }
    } catch ( Exception ex ) {
      // ignore
    }

    hide();
  }

  protected void onCancel() {
    try {
      if ( callback != null ) {
        callback.cancelPressed();
      }
    } catch ( Exception ex ) {
      // ignore
    }

    hide();
  }
}
