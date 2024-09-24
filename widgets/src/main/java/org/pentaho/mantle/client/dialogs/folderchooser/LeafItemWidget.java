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
 * Copyright (c) 2002-2023 Hitachi Vantara. All rights reserved.
 */

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
