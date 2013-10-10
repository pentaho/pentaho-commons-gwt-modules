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
 * Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.gwt.widgets.client.utils;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class ButtonHelper {

  public static enum ButtonLabelType {
    TEXT_ON_TOP, TEXT_ON_RIGHT, TEXT_ON_BOTTOM, TEXT_ON_LEFT, TEXT_ONLY, NO_TEXT
  }

  public static String createButtonLabel( Image image, String text, ButtonLabelType type ) {
    return createButtonLabel( image, text, type, null );
  }

  public static Panel createButtonElement( Image image, String text, ButtonLabelType type ) {
    return createButtonElement( image, text, type, null );
  }

  public static String createButtonLabel( Image img, String text, ButtonLabelType type, String cssName ) {
    final HTML html = new HTML( text, false );
    if ( cssName != null ) {
      html.addStyleDependentName( cssName );
      img.addStyleDependentName( cssName );
    }
    if ( type == ButtonLabelType.TEXT_ONLY ) {
      return text;
    } else if ( type == ButtonLabelType.TEXT_ON_LEFT || type == ButtonLabelType.TEXT_ON_RIGHT ) {
      HorizontalPanel hpanel = new HorizontalPanel();
      if ( cssName != null ) {
        hpanel.addStyleName( cssName );
      }
      if ( type == ButtonLabelType.TEXT_ON_LEFT ) {
        hpanel.add( html );
        hpanel.add( new HTML( "&nbsp;" ) ); //$NON-NLS-1$
        hpanel.add( img );
      } else {
        hpanel.add( img );
        hpanel.add( new HTML( "&nbsp;" ) ); //$NON-NLS-1$
        hpanel.add( html );
      }
      hpanel.setCellVerticalAlignment( html, HasVerticalAlignment.ALIGN_MIDDLE );
      hpanel.setCellVerticalAlignment( img, HasVerticalAlignment.ALIGN_MIDDLE );
      return hpanel.getElement().getString();
    } else {
      VerticalPanel vpanel = new VerticalPanel();
      if ( type == ButtonLabelType.TEXT_ON_TOP ) {
        vpanel.add( html );
        vpanel.add( img );
      } else {
        vpanel.add( img );
        vpanel.add( html );
      }
      vpanel.setCellHorizontalAlignment( html, HasHorizontalAlignment.ALIGN_CENTER );
      vpanel.setCellHorizontalAlignment( img, HasHorizontalAlignment.ALIGN_CENTER );
      return vpanel.getElement().getString();
    }
  }

  public static Panel createButtonElement( Image img, String text, ButtonLabelType type, String cssName ) {
    final HTML html = new HTML( text, false );
    if ( cssName != null ) {
      html.addStyleDependentName( cssName );
      img.addStyleDependentName( cssName );
    }
    if ( type == ButtonLabelType.TEXT_ONLY ) {
      SimplePanel sp = new SimplePanel();
      sp.add( html );
      return sp;
    } else if ( type == ButtonLabelType.TEXT_ON_LEFT || type == ButtonLabelType.TEXT_ON_RIGHT ) {
      HorizontalPanel hpanel = new HorizontalPanel();
      if ( cssName != null ) {
        hpanel.addStyleName( cssName );
      }
      if ( type == ButtonLabelType.TEXT_ON_LEFT ) {
        hpanel.add( html );
        hpanel.add( new HTML( "&nbsp;" ) ); //$NON-NLS-1$
        hpanel.add( img );
      } else {
        hpanel.add( img );
        hpanel.add( new HTML( "&nbsp;" ) ); //$NON-NLS-1$
        hpanel.add( html );
      }
      hpanel.setCellVerticalAlignment( html, HasVerticalAlignment.ALIGN_MIDDLE );
      hpanel.setCellVerticalAlignment( img, HasVerticalAlignment.ALIGN_MIDDLE );
      return hpanel;
    } else {
      VerticalPanel vpanel = new VerticalPanel();
      if ( type == ButtonLabelType.TEXT_ON_TOP ) {
        vpanel.add( html );
        vpanel.add( img );
      } else {
        vpanel.add( img );
        vpanel.add( html );
      }
      vpanel.setCellHorizontalAlignment( html, HasHorizontalAlignment.ALIGN_CENTER );
      vpanel.setCellHorizontalAlignment( img, HasHorizontalAlignment.ALIGN_CENTER );
      return vpanel;
    }
  }
}
