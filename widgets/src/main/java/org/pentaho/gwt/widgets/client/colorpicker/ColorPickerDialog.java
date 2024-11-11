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


package org.pentaho.gwt.widgets.client.colorpicker;

import org.pentaho.gwt.widgets.client.dialogs.PromptDialogBox;
import org.pentaho.gwt.widgets.client.i18n.WidgetsLocalizedMessages;
import org.pentaho.gwt.widgets.client.i18n.WidgetsLocalizedMessagesSingleton;

public class ColorPickerDialog extends PromptDialogBox {

  private static final WidgetsLocalizedMessages MSGS = WidgetsLocalizedMessagesSingleton.getInstance().getMessages();

  public ColorPickerDialog( String startHex ) {
    super( MSGS.colorChooser(), MSGS.ok(), MSGS.cancel(), false, true, new ColorPicker() );
    ColorPicker colorPicker = (ColorPicker) getContent();
    try {
      colorPicker.setHex( startHex );
    } catch ( Exception e ) {
      //ignore
    }
  }

  public String getHexColor() {
    ColorPicker colorPicker = (ColorPicker) getContent();
    return colorPicker.getHexColor();
  }

}
