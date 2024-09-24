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
