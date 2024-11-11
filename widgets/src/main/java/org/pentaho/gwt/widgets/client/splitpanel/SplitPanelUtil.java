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


package org.pentaho.gwt.widgets.client.splitpanel;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.VerticalSplitPanel;

public class SplitPanelUtil {

  // these guys must operate on populated scrollpanels (content added)
  public static void setHorizontalSplitPanelScrolling( HorizontalSplitPanel hsplit, boolean enableLeft,
      boolean enableRight ) {
    Element splitElement = hsplit.getElement();
    Element leftElement = hsplit.getLeftWidget().getElement();
    while ( leftElement != splitElement && leftElement != null ) {
      leftElement = leftElement.getParentElement();
      DOM.setStyleAttribute( (com.google.gwt.user.client.Element) leftElement,
          "overflowX", enableLeft ? "auto" : "hidden" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      DOM.setStyleAttribute( (com.google.gwt.user.client.Element) leftElement,
          "overflowY", enableLeft ? "auto" : "hidden" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    Element rightElement = hsplit.getRightWidget().getElement();
    while ( rightElement != splitElement && rightElement != null ) {
      rightElement = rightElement.getParentElement();
      DOM.setStyleAttribute( (com.google.gwt.user.client.Element) rightElement,
          "overflowX", enableRight ? "auto" : "hidden" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      DOM.setStyleAttribute( (com.google.gwt.user.client.Element) rightElement,
          "overflowY", enableRight ? "auto" : "hidden" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
  }

  // these guys must operate on populated scrollpanels (content added)
  public static void setVerticalSplitPanelScrolling( VerticalSplitPanel vsplit,
                                                     boolean enableTop, boolean enableBottom ) {
    Element splitElement = vsplit.getElement();
    Element topElement = vsplit.getTopWidget().getElement();
    while ( topElement != splitElement && topElement != null ) {
      topElement = topElement.getParentElement();
      DOM.setStyleAttribute( (com.google.gwt.user.client.Element) topElement,
          "overflowX", enableTop ? "auto" : "hidden" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      DOM.setStyleAttribute( (com.google.gwt.user.client.Element) topElement,
          "overflowY", enableTop ? "auto" : "hidden" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    Element bottomElement = vsplit.getBottomWidget().getElement();
    while ( bottomElement != splitElement && bottomElement != null ) {
      bottomElement = bottomElement.getParentElement();
      DOM.setStyleAttribute( (com.google.gwt.user.client.Element) bottomElement,
          "overflowX", enableBottom ? "auto" : "hidden" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      DOM.setStyleAttribute( (com.google.gwt.user.client.Element) bottomElement,
          "overflowY", enableBottom ? "auto" : "hidden" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
  }

}
