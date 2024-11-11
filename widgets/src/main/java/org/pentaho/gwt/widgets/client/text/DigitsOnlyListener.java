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


package org.pentaho.gwt.widgets.client.text;

import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author wseyler
 * 
 */
@SuppressWarnings( "deprecation" )
public class DigitsOnlyListener extends KeyboardListenerAdapter {
  public void onKeyPress( Widget sender, char keyCode, int modifiers ) {
    if ( ( !Character.isDigit( keyCode ) ) && ( keyCode != (char) KEY_TAB ) && ( keyCode != (char) KEY_BACKSPACE )
        && ( keyCode != (char) KEY_DELETE ) && ( keyCode != (char) KEY_ENTER ) && ( keyCode != (char) KEY_HOME )
        && ( keyCode != (char) KEY_END ) && ( keyCode != (char) KEY_LEFT ) && ( keyCode != (char) KEY_UP )
        && ( keyCode != (char) KEY_RIGHT ) && ( keyCode != (char) KEY_DOWN ) ) {
      // TextBox.cancelKey() suppresses the current keyboard event.
      ( (TextBox) sender ).cancelKey();
    }
  }
}
