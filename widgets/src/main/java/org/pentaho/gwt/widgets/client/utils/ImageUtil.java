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

package org.pentaho.gwt.widgets.client.utils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;

/**
 * User: RFellows Date: 5/13/13
 */
public class ImageUtil {

  private static final ImageUtil INSTANCE = new ImageUtil();

  protected ImageUtil() {
  }

  public static final String BLANK_IMAGE_PATH = GWT.getHostPageBaseURL()
      + "content/common-ui/resources/themes/images/spacer.gif";

  public static ImageUtil getInstance() {
    return INSTANCE;
  }

  protected String getBlankImagePath() {
    return BLANK_IMAGE_PATH;
  }

  /**
   * Returns a GWT Image with the src value set to a blank image and applies the provided css style to the element to
   * allow for a background image to be used instead.
   * 
   * @param cssStyleName
   * @return
   */
  public static Image getThemeableImage( String... cssStyleName ) {
    Image image = new Image( getInstance().getBlankImagePath() );

    for ( String style : cssStyleName ) {
      image.addStyleName( style );
    }

    return image;
  }

}
