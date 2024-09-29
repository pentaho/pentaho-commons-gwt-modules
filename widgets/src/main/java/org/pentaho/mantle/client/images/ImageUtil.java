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


package org.pentaho.mantle.client.images;

/**
 * User: RFellows Date: 5/8/13
 */
public class ImageUtil extends org.pentaho.gwt.widgets.client.utils.ImageUtil {

  private static final String BLANK_IMAGE_PATH = "mantle/images/spacer.gif";

  @Override
  public String getBlankImagePath() {
    return BLANK_IMAGE_PATH;
  }

}
