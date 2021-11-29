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
* Copyright (c) 2002-2021 Hitachi Vantara..  All rights reserved.
*/

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
