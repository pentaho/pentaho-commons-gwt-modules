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


public class ButtonHelperIT extends GWTTestCase {
  public void testCreateButtonLabel() {
    final String text = "text";
    final Image img = new Image();
    final String cssName = "cssName";
    assertEquals( text,
        ButtonHelper.createButtonLabel( img, text, ButtonHelper.ButtonLabelType.TEXT_ONLY, cssName ) );
    assertEquals( "<table cellspacing=\"0\" cellpadding=\"0\" class=\"cssName\"><tbody><tr><td align=\"left\" style=\"vertical-align: middle;\"><div class=\"gwt-HTML gwt-HTML-cssName\" style=\"white-space: nowrap;\">text</div></td><td align=\"left\" style=\"vertical-align: top;\"><div class=\"gwt-HTML\"> </div></td><td align=\"left\" style=\"vertical-align: middle;\"><img class=\"gwt-Image gwt-Image-cssName\"></td></tr></tbody></table>",
        ButtonHelper.createButtonLabel( img, text, ButtonHelper.ButtonLabelType.TEXT_ON_LEFT, cssName ) );
    assertEquals( "<table cellspacing=\"0\" cellpadding=\"0\" class=\"cssName\"><tbody><tr><td align=\"left\" style=\"vertical-align: middle;\"><img class=\"gwt-Image gwt-Image-cssName\"></td><td align=\"left\" style=\"vertical-align: top;\"><div class=\"gwt-HTML\"> </div></td><td align=\"left\" style=\"vertical-align: middle;\"><div class=\"gwt-HTML gwt-HTML-cssName\" style=\"white-space: nowrap;\">text</div></td></tr></tbody></table>",
        ButtonHelper.createButtonLabel( img, text, ButtonHelper.ButtonLabelType.TEXT_ON_RIGHT, cssName ) );
    assertEquals( "<table cellspacing=\"0\" cellpadding=\"0\"><tbody><tr><td align=\"center\" style=\"vertical-align: top;\"><div class=\"gwt-HTML gwt-HTML-cssName\" style=\"white-space: nowrap;\">text</div></td></tr><tr><td align=\"center\" style=\"vertical-align: top;\"><img class=\"gwt-Image gwt-Image-cssName\"></td></tr></tbody></table>",
        ButtonHelper.createButtonLabel( img, text, ButtonHelper.ButtonLabelType.TEXT_ON_TOP, cssName ) );
    assertEquals( "<table cellspacing=\"0\" cellpadding=\"0\"><tbody><tr><td align=\"center\" style=\"vertical-align: top;\"><img class=\"gwt-Image gwt-Image-cssName\"></td></tr><tr><td align=\"center\" style=\"vertical-align: top;\"><div class=\"gwt-HTML gwt-HTML-cssName\" style=\"white-space: nowrap;\">text</div></td></tr></tbody></table>",
        ButtonHelper.createButtonLabel( img, text, ButtonHelper.ButtonLabelType.TEXT_ON_BOTTOM, cssName ) );
  }

  public void testCreateButtonElement() {
    final Image image = new Image();
    final String text = "text";
    final String cssName = "cssName";
    assertEquals( "<div><div class=\"gwt-HTML gwt-HTML-cssName\" style=\"white-space: nowrap;\">text</div></div>",
        ButtonHelper.createButtonElement( image, text, ButtonHelper.ButtonLabelType.TEXT_ONLY, cssName ).toString() );
    assertEquals( "<table cellspacing=\"0\" cellpadding=\"0\" class=\"cssName\"><tbody><tr><td align=\"left\" style=\"vertical-align: middle;\"><div class=\"gwt-HTML gwt-HTML-cssName\" style=\"white-space: nowrap;\">text</div></td><td align=\"left\" style=\"vertical-align: top;\"><div class=\"gwt-HTML\"> </div></td><td align=\"left\" style=\"vertical-align: middle;\"><img class=\"gwt-Image gwt-Image-cssName\"></td></tr></tbody></table>",
        ButtonHelper.createButtonElement( image, text, ButtonHelper.ButtonLabelType.TEXT_ON_LEFT, cssName ).toString() );
    assertEquals( "<table cellspacing=\"0\" cellpadding=\"0\" class=\"cssName\"><tbody><tr><td align=\"left\" style=\"vertical-align: middle;\"><img class=\"gwt-Image gwt-Image-cssName\"></td><td align=\"left\" style=\"vertical-align: top;\"><div class=\"gwt-HTML\"> </div></td><td align=\"left\" style=\"vertical-align: middle;\"><div class=\"gwt-HTML gwt-HTML-cssName\" style=\"white-space: nowrap;\">text</div></td></tr></tbody></table>",
        ButtonHelper.createButtonElement( image, text, ButtonHelper.ButtonLabelType.TEXT_ON_RIGHT, cssName ).toString() );
    assertEquals( "<table cellspacing=\"0\" cellpadding=\"0\"><tbody><tr><td align=\"center\" style=\"vertical-align: top;\"><div class=\"gwt-HTML gwt-HTML-cssName\" style=\"white-space: nowrap;\">text</div></td></tr><tr><td align=\"center\" style=\"vertical-align: top;\"><img class=\"gwt-Image gwt-Image-cssName\"></td></tr></tbody></table>",
        ButtonHelper.createButtonElement( image, text, ButtonHelper.ButtonLabelType.TEXT_ON_TOP, cssName ).toString() );
    assertEquals( "<table cellspacing=\"0\" cellpadding=\"0\"><tbody><tr><td align=\"center\" style=\"vertical-align: top;\"><img class=\"gwt-Image gwt-Image-cssName\"></td></tr><tr><td align=\"center\" style=\"vertical-align: top;\"><div class=\"gwt-HTML gwt-HTML-cssName\" style=\"white-space: nowrap;\">text</div></td></tr></tbody></table>",
        ButtonHelper.createButtonElement( image, text, ButtonHelper.ButtonLabelType.TEXT_ON_BOTTOM, cssName ).toString() );
  }

  @Override
  public String getModuleName() {
    return "org.pentaho.gwt.widgets.Widgets";
  }
}
