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

import com.google.gwt.core.client.EntryPoint;
import org.pentaho.mantle.client.dialogs.filterdialog.client.jsni.impl.JsniFilterUtil;
import org.pentaho.gwt.widgets.client.utils.module.ModuleUtil;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.gwt.GwtXulRunner;
import org.pentaho.ui.xul.gwt.util.IXulLoaderCallback;

/**
 * Entry point for the Filter Dialog GWT Module when run in standalone mode.
 *
 * @author Jordan Ganoff (jganoff@pentaho.com)
 */
public class FilterDialogEntryPoint implements EntryPoint, IXulLoaderCallback, java.io.Serializable {

  public static final String MODULE_NAME = "filterdialog"; //$NON-NLS-1$

  private FilterDialog dialog;

  @Override
  public void onModuleLoad() {
    registerNamespaces();
    registerNativeFunctions( this );

    // Required for MQL DataSource functionality (until a proper refactor is done)
    registerDashboardsEnableWaitCursor();

    dialog = new FilterDialog(); // XUL_FILTER_DIALOG, MESSAGE_BUNDLE_LOCATION);
    dialog.setCallback( this );
    dialog.load();
  }

  public native void registerNamespaces() /*-{
    if (typeof ($wnd.pentaho) == 'undefined') {
      $wnd.pentaho = {};
    }
    if (typeof ($wnd.pentaho.filterdialog) == 'undefined') {
      $wnd.pentaho.filterdialog = {};
    }

  }-*/;

  private native void registerDashboardsEnableWaitCursor()/*-{
    if (typeof($wnd.pho) == 'undefined') {
      $wnd.pho = {};
    }
    if (typeof($wnd.pho.dashboards) == 'undefined') {
      $wnd.pho.dashboards = {};
    }
    $wnd.pho.dashboards.waitCursorTimerId = null;
    $wnd.pho.dashboards.enableWaitCursor = function (enable) {
      if (enable) {
        document.body.style.cursor = "wait";
        // set a timer to put the cursor back to default in the case something goes wrong or code doesn't reset it
        clearTimeout($wnd.pho.dashboards.waitCursorTimerId);
        $wnd.pho.dashboards.waitCursorTimerId = setTimeout('document.body.style.cursor = "default"', 30000);
      } else {
        document.body.style.cursor = "default";
        clearTimeout($wnd.pho.dashboards.waitCursorTimerId);
      }
    }
  }-*/;

  private native void registerNativeFunctions( FilterDialogEntryPoint o )/*-{
    $wnd.pentaho.filterdialog.init = function () {
      o.@org.pentaho.mantle.client.dialogs.filterdialog.client.FilterDialogEntryPoint::init()();
    }

    $wnd.pentaho.filterdialog.createConfig = function () {
      return @org.pentaho.mantle.client.dialogs.filterdialog.client.jsni.FilterDialogConfiguration::create()();
    }

    $wnd.pentaho.filterdialog.addFilter = function (preSave, onSave, onCancel, config) {
      // CHECKSTYLE IGNORE LineLength FOR NEXT 1 LINES
      @org.pentaho.mantle.client.dialogs.filterdialog.client.jsni.impl.JsniFilterUtil::addFilter(Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;Lorg/pentaho/mantle/client/dialogs/filterdialog/client/jsni/FilterDialogConfiguration;)(preSave, onSave, onCancel, config);
    }

    $wnd.pentaho.filterdialog.editFilter = function (filter, preSave, onSave, onCancel, config) {
      // CHECKSTYLE IGNORE LineLength FOR NEXT 1 LINES
      @org.pentaho.mantle.client.dialogs.filterdialog.client.jsni.impl.JsniFilterUtil::editFilter(Lorg/pentaho/mantle/client/dialogs/filterdialog/client/jsni/IPentahoFilter;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;Lorg/pentaho/mantle/client/dialogs/filterdialog/client/jsni/FilterDialogConfiguration;)(filter, preSave, onSave, onCancel, config);
    }
  }-*/;

  public void init() throws XulException {
    dialog.init();
  }

  @Override
  public void xulLoaded( GwtXulRunner runner ) {
    dialog.getController().setSQLSupport( new DisabledSQLSupport() );
    JsniFilterUtil.setEditFilterController( dialog.getController() );
    ModuleUtil.fireModuleLoaded( MODULE_NAME );
  }

  @Override
  public void overlayLoaded() {
  }

  @Override
  public void overlayRemoved() {
  }
}
