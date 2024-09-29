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


package org.pentaho.gwt.widgets.client.utils;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.Image;

public class ImageUtilIT extends GWTTestCase {
  public String getModuleName() {
    return "org.pentaho.gwt.widgets.Widgets"; //$NON-NLS-1$
  }

  public void testGetThemeableImage() throws Exception {
    final String style1 = "style1";
    final String style2 = "style2";
    final Image themeableImage = ImageUtil.getThemeableImage( style1, style2 );
    assertTrue( themeableImage.getStyleName().contains( style1 ) );
    assertTrue( themeableImage.getStyleName().contains( style2 ) );
    assertTrue( themeableImage.getUrl().endsWith( ImageUtil.BLANK_IMAGE_PATH ) );
  }
}
