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
 * Copyright (c) 2024 Hitachi Vantara. All rights reserved.
 */

package org.pentaho.gwt.widgets.client.ui;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import org.pentaho.gwt.widgets.client.panel.HorizontalFlexPanel;

import static org.pentaho.gwt.widgets.client.utils.ImageUtil.getThemeableImage;

public class Banner extends HorizontalFlexPanel {
  public static final String CLASSNAME = "gwt-banner flex-center-v";

  private final String status;
  private final String message;

  private final Image icon = getThemeableImage( "icon-zoomable" );
  private final Label label = new Label();

  public Banner( String message ) {
    this( null, message );
  }

  public Banner( String status, String message ) {
    super();

    this.status = status;
    this.message = message;

    createUI();

    addStyleName( CLASSNAME );
    if ( status != null ) {
      addStyleName( "status-" + status );
    }

    setWidth( "100%" );
  }

  /* Visible for testing */
  Image getIcon() {
    return icon;
  }

  /* Visible for testing */
  Label getLabel() {
    return label;
  }

  /* Visible for testing */
  void createUI() {
    if ( status != null ) {
      Image statusIcon = getIcon();
      statusIcon.addStyleName( "pentaho-" + status + "-button" );

      add( statusIcon );
    }

    Label bannerMessage = getLabel();
    bannerMessage.setText( message );
    bannerMessage.setStyleName( "typography typography-body" );

    add( bannerMessage );
  }
}
