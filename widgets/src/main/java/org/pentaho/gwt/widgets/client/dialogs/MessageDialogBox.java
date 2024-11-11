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

import com.google.gwt.aria.client.Roles;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.pentaho.gwt.widgets.client.messages.Messages;

import static org.pentaho.gwt.widgets.client.utils.ElementUtils.ensureId;

/**
 * The <code>MessageDialogBox</code> class is a dialog box intended to display text messages.
 * <p>
 *   By default, the ARIA role is <code>alertdialog</code>.
 * </p>
 * <p>
 *   By default, the dialog's {@link #getAriaDescribedBy()} is set to the identifier of the dialog's content widget.
 *   A different element can be used, by calling {@link #setAriaDescribedBy(String)},
 *   when the content is more complex than a single text message.
 * </p>
 * <p>
 *   By default, the dialog is in responsive mode, {@link #isResponsive()} and has
 *   a sizing mode, {@link #getSizingMode()}, of {@link DialogSizingMode#SIZE_TO_CONTENT},
 *   and a width category, {@link #getWidthCategory()}, of {@link DialogWidthCategory#TEXT}.
 * </p>
 */
public class MessageDialogBox extends PromptDialogBox {

  public MessageDialogBox( String title, String message, boolean isHTML ) {
    this( title, message, isHTML, false, true );
  }

  public MessageDialogBox( String title, Widget messageContent ) {
    this( title, messageContent, null );
  }

  public MessageDialogBox( String title, String message, boolean isHTML, String okText ) {
    this( title, message, isHTML, false, true, okText );
  }

  public MessageDialogBox( String title, Widget messageContent, String okText ) {
    this( title, messageContent, okText, null );
  }

  public MessageDialogBox( String title, String message, boolean isHTML, String okText, String cancelText ) {
    this( title, message, isHTML, okText, null, cancelText );
  }

  public MessageDialogBox( String title, Widget messageContent, String okText, String cancelText ) {
    this( title, messageContent, okText, null, cancelText );
  }

  public MessageDialogBox( String title, String message, boolean isHTML,
                           String okText, String notOkText, String cancelText ) {
    this( title, message, isHTML, false, true, okText, notOkText, cancelText );
  }

  public MessageDialogBox( String title, Widget messageContent, String okText, String notOkText, String cancelText ) {
    this( title, messageContent, false, true, okText, notOkText, cancelText );
  }

  public MessageDialogBox( String title, String message, boolean isHTML, boolean autoHide, boolean modal ) {
    this( title, message, isHTML, autoHide, modal, Messages.getString( "dialog.button.ok" ), null, null );
  }

  public MessageDialogBox( String title, String message, boolean isHTML, boolean autoHide, boolean modal,
                           String okText ) {
    this( title, message, isHTML, autoHide, modal, okText, null, null );
  }

  public MessageDialogBox( String title, String message, boolean isHTML, boolean autoHide, boolean modal,
                           String okText, String notOkText, String cancelText ) {
    this( title, isHTML ? new HTML( message ) : new Label( message ), autoHide, modal, okText, notOkText, cancelText );
  }

  public MessageDialogBox( String title, Widget messageContent, boolean autoHide, boolean modal,
                           String okText, String notOkText, String cancelText ) {
    super( title, okText, notOkText, cancelText, autoHide, modal, messageContent );

    setResponsive( true );
    setSizingMode( DialogSizingMode.SIZE_TO_CONTENT );
    setWidthCategory( DialogWidthCategory.TEXT );
    setMinimumHeightCategory( DialogMinimumHeightCategory.CONTENT );

    // ARIA
    // Override role from "dialog" to "alertdialog".
    setAriaRole( Roles.getAlertdialogRole() );

    // aria-describedby
    setAriaDescribedBy( ensureId( getContent() ) );
  }
}
