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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.gwt.widgets.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import org.pentaho.gwt.widgets.client.messages.Messages;
import org.pentaho.gwt.widgets.client.utils.i18n.IResourceBundleLoadCallback;
import org.pentaho.gwt.widgets.client.utils.i18n.ResourceBundle;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */

public class WidgetModule implements EntryPoint, IResourceBundleLoadCallback {

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    ResourceBundle messages = new ResourceBundle();
    Messages.setResourceBundle( messages );
    messages.loadBundle( GWT.getModuleBaseForStaticFiles() + "messages/", "WidgetsMessages", true, WidgetModule.this ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  @Override
  public void bundleLoaded( String bundleName ) {

  }
}
