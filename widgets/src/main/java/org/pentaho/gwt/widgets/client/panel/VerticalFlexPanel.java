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
 * Copyright (c) 2002-2023 Hitachi Vantara. All rights reserved.
 */

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
