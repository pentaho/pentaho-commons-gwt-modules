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

package org.pentaho.gwt.widgets.client.colorpicker;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.CustomButton;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/***
 * Copyright Hitachi Vantara
 * 
 * @author cboyden
 * 
 */
@SuppressWarnings( "deprecation" )
public class ColorPalette extends Composite implements ClickListener, KeyboardListener, ChangeListener {
  protected String DEFAULT_COLOR = "FFFFFF"; //$NON-NLS-1$

  protected int cols = 12;
  protected int rows = 4;

  protected String colorBoxWidth = "5px"; //$NON-NLS-1$
  protected String colorBoxHeight = "5px"; //$NON-NLS-1$

  // Spacing between colorBoxes
  protected int horzSpacing = 5;
  protected int vertSpacing = 5;

  protected String colorBoxBorderColor = "D0D0D0"; //$NON-NLS-1$

  // Toggle ability to create custom colors with a color picker
  protected boolean advancedMode = false;

  protected Color[] colorArray = null;

  VerticalPanel mainPanel = null;
  VerticalPanel palettePanel = null;

  public ColorPalette() throws Exception {
    mainPanel = new VerticalPanel();
    mainPanel.setVisible( false );

    refreshPalette();
    refreshDisplay();

    mainPanel.setVisible( true );
  }

  protected void refreshDisplay() {
    // VerticalPanel tempPalettePanel = new VerticalPanel();

    Widget paletteItem = null;
    while ( mainPanel.getWidgetCount() > 0 ) {
      mainPanel.remove( 0 );
    }

    // Generate the palette panel
    for ( int row = 0; row < rows; row++ ) {
      for ( int col = 0; col < cols; col++ ) {
        paletteItem = new CustomButton() {
          {
            this.setHTML( "<div style=\"color: #ff0000; background-color: #ff0000;\">test</div>" ); //$NON-NLS-1$

          }
        };
        mainPanel.add( paletteItem );
      }
    }
  }

  /**
   * Refreshes the color palette, making sure the palette is the correct size required dimensions and filling in
   * defaults as necessary.
   * 
   * @throws Exception
   */
  protected void refreshPalette() throws Exception {
    Color[] tempColorArray = colorArray;

    int colorArrayIndex = 0;

    // Create and fill proper color array
    colorArray = new Color[cols * rows];

    if ( ( tempColorArray != null ) && ( tempColorArray.length != ( cols * rows ) ) ) {

      // Copy over existing colors
      for ( int i = 0; ( ( i < tempColorArray.length ) && ( colorArrayIndex < colorArray.length ) ); i++ ) {
        if ( tempColorArray[i] != null ) {
          colorArray[colorArrayIndex++] = tempColorArray[i];
        }
      }
    }

    // Populate remaining space with default colors
    for ( ; colorArrayIndex < colorArray.length; colorArrayIndex++ ) {
      colorArray[colorArrayIndex] = new Color();
      colorArray[colorArrayIndex].setHex( DEFAULT_COLOR );
    }
  }

  public void onAttach() {
    // Called when we are shown (from being hidden)
    super.onAttach();
  }

  public void onClick( Widget sender ) {
    // TODO Auto-generated method stub

  }

  public void onKeyDown( Widget sender, char keyCode, int modifiers ) {
    // TODO Auto-generated method stub

  }

  public void onKeyPress( Widget sender, char keyCode, int modifiers ) {

    switch ( (int) keyCode ) {
    // Okay and Cancel key equiv
      case KeyboardListener.KEY_ENTER: {
        break;
      }
      case KeyboardListener.KEY_ESCAPE: {
        break;
      }

      // Navigate the color palette
      case KeyboardListener.KEY_UP: {
        break;
      }
      case KeyboardListener.KEY_DOWN: {
        break;
      }
      case KeyboardListener.KEY_LEFT: {
        break;
      }
      case KeyboardListener.KEY_RIGHT: {
        break;
      }
      case KeyboardListener.KEY_HOME: {
        break;
      }
      case KeyboardListener.KEY_END: {
        break;
      }
    }
  }

  public void onKeyUp( Widget sender, char keyCode, int modifiers ) {
    // TODO Auto-generated method stub
  }

  public void onChange( Widget sender ) {
    // TODO Auto-generated method stub
  }
}
