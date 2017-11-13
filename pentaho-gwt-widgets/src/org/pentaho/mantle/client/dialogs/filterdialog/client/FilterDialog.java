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

package org.pentaho.mantle.client.dialogs.filterdialog.client;

import com.google.gwt.core.client.GWT;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.binding.BindingFactory;
import org.pentaho.ui.xul.gwt.GwtXulDomContainer;
import org.pentaho.ui.xul.gwt.GwtXulRunner;
import org.pentaho.ui.xul.gwt.binding.GwtBindingFactory;
import org.pentaho.ui.xul.gwt.util.AsyncXulLoader;
import org.pentaho.ui.xul.gwt.util.IXulLoaderCallback;

public class FilterDialog implements IXulLoaderCallback, java.io.Serializable {

  public static final String XUL_FILTER_DIALOG = "filterDialog.xul"; //$NON-NLS-1$

  public static final String MESSAGE_BUNDLE_LOCATION = "messages/filterdialog"; //$NON-NLS-1$

  // private String xulLocation;

  // private String messageBundleLocation;

  private EditFilterController editFilterController;

  private IXulLoaderCallback callback;

  private GwtXulRunner runner;

  private boolean initCalledBeforeLoad = false; // init can be called on dashboard load before the xul has been loaded

  public FilterDialog() {
    editFilterController = new EditFilterController();
  }

  public void setCallback( IXulLoaderCallback callback ) {
    this.callback = callback;
  }

  /**
   * Load the XUL for this dialog.
   */
  public void load() {
    try {
      AsyncXulLoader.loadXulFromUrl( GWT.getModuleBaseURL() + XUL_FILTER_DIALOG, GWT.getModuleBaseURL()
        + MESSAGE_BUNDLE_LOCATION, this );
    } catch ( Exception e ) {
      e.printStackTrace();
    }
  }

  /**
   * Initialize the dialog. Should only be called after the XUL document has been loaded.
   */
  public void init() throws XulException {
    if ( runner == null ) {
      this.initCalledBeforeLoad = true;
      return;
    }
    runner.initialize();
    editFilterController.init();
  }

  @Override
  public void xulLoaded( GwtXulRunner runner ) {
    this.runner = runner;
    try {
      GwtXulDomContainer container = (GwtXulDomContainer) runner.getXulDomContainers().get( 0 );

      BindingFactory bf = new GwtBindingFactory( container.getDocumentRoot() );

      // begin EditFilterController setup
      editFilterController.setBindingFactory( bf );
      container.addEventHandler( editFilterController );
      // end EditFilterController setup

      if ( callback != null ) {
        callback.xulLoaded( runner );
      }

      // if init was called before load, call it now
      if ( this.initCalledBeforeLoad ) {
        this.init();
      }
    } catch ( Exception e ) {
      e.printStackTrace();
    }
  }

  @Override
  public void overlayLoaded() {
    if ( callback != null ) {
      callback.overlayLoaded();
    }
  }

  @Override
  public void overlayRemoved() {
    if ( callback != null ) {
      callback.overlayRemoved();
    }
  }

  /**
   * @return the controller for the dialog and its associated components
   */
  public EditFilterController getController() {
    return editFilterController;
  }
}
