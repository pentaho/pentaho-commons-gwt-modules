/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
