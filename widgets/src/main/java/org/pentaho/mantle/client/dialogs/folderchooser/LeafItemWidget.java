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


package org.pentaho.mantle.client.dialogs.folderchooser;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import org.pentaho.mantle.client.environment.EnvironmentHelper;

/**
 * Used on leaf folder nodes, so that these do not show the expand/collapse arrow icon.
 */
public class LeafItemWidget extends Composite {
  Image leafImage;
  Label leafLabel;

  public LeafItemWidget( String title, String... styleName ) {
    HorizontalPanel widget = new HorizontalPanel();
    initWidget( widget );

    leafImage =
      new Image( EnvironmentHelper.getFullyQualifiedURL() + "content/common-ui/resources/themes/images/spacer.gif" );
    for ( String style : styleName ) {
      leafImage.addStyleName( style );
    }
    widget.add( leafImage );

    leafLabel = new Label( title );
    leafLabel.removeStyleName( "gwt-Label" );
    widget.add( leafLabel );
    widget.setCellWidth( leafLabel, "100%" );
  }

  public Image getLeafImage() {
    return leafImage;
  }

  public Label getLeafLabel() {
    return leafLabel;
  }
}
