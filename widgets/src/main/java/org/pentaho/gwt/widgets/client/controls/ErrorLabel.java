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


package org.pentaho.gwt.widgets.client.controls;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.pentaho.gwt.widgets.client.utils.string.StringUtils;

/**
 * @author Steven Barkdull
 * 
 */

@SuppressWarnings( "deprecation" )
public class ErrorLabel extends VerticalPanel {

  protected Label errorLabel = null;

  public ErrorLabel( Widget w ) {
    errorLabel = new Label();
    errorLabel.setStyleName( "errorLabel" ); //$NON-NLS-1$
    errorLabel.setVisible( false );
    add( errorLabel );

    add( w );
  }

  /**
   * Set an error message to be associated with the label
   * 
   * @param msg
   *          String if null, clear the error message, else set the error message to <param>mgs</param>.
   */
  public void setErrorMsg( String msg ) {
    if ( !StringUtils.isEmpty( msg ) ) {
      errorLabel.setText( msg );
      errorLabel.setVisible( true );
    } else {
      errorLabel.setText( "" ); //$NON-NLS-1$
      errorLabel.setVisible( false );
    }
  }
}
