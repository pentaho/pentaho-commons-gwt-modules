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
 * Copyright (c) 2023 Hitachi Vantara. All rights reserved.
 */

package org.pentaho.gwt.widgets.client.panel;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class ScrollFlexPanel extends com.google.gwt.user.client.ui.ScrollPanel {
  public ScrollFlexPanel() {
    super();

    init();
  }

  public ScrollFlexPanel( Widget child ) {
    super( child );

    init();
  }

  protected ScrollFlexPanel( Element root, Element scrollable, Element container ) {
    super( root, scrollable, container );

    init();
  }

  private void init() {
    addStyleName( "pentaho-gwt-ScrollFlexPanel" );
    addStyleName( "flex-column" );
    addStyleName( "with-scroll-child" );

    // Access base class' containerElem
    Element localContainerElem = getElement().getFirstChildElement();
    localContainerElem.addClassName( "flex-column" );
    localContainerElem.addClassName( "with-scroll-child" );
  }
}
