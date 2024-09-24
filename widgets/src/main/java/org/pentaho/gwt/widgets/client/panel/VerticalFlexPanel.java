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

package org.pentaho.gwt.widgets.client.panel;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A flexible version of the framework class {@link VerticalPanel}.
 * <p>
 * To enable the flexible behavior, the panel must be rendered within an HTML container
 * which has the <code>responsive</code> CSS class set.
 * This allows for the panel to be used in base classes and for its flexible behavior only taking effect
 * when the subclass opts into it. When not enabled, the panel is fully backwards compatible.
 * This allows for incremental development.
 * For example, the <code>PromptDialogBox</code> class may use this flexible panel, however,
 * its flexible behavior only takes effect when the dialog is marked responsive,
 * by calling its <code>PromptDialogBox#setResponsive(boolean)</code> method.
 * </p>
 */
public class VerticalFlexPanel extends VerticalPanel {

  public static final String STYLE_NAME = "flex-column gwt-h-v-panel gwt-v-panel";

  public VerticalFlexPanel() {
    addStyleName( STYLE_NAME );

    Roles.getPresentationRole().set( getElement() );
  }
}
