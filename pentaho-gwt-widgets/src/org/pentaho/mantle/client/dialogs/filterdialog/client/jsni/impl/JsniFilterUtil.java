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

package org.pentaho.mantle.client.dialogs.filterdialog.client.jsni.impl;

import com.google.gwt.core.client.JavaScriptObject;
import org.pentaho.mantle.client.dialogs.filterdialog.client.EditFilterController;
import org.pentaho.mantle.client.dialogs.filterdialog.client.EditFilterModel;
import org.pentaho.mantle.client.dialogs.filterdialog.client.EditFilterModel.EditType;
import org.pentaho.mantle.client.dialogs.filterdialog.client.IPentahoFilterFactory;
import org.pentaho.mantle.client.dialogs.filterdialog.client.jsni.FilterDialogConfiguration;
import org.pentaho.mantle.client.dialogs.filterdialog.client.jsni.IPentahoFilter;
import org.pentaho.mantle.client.dialogs.filterdialog.client.jsni.JsniFilterSaveCallback;

public class JsniFilterUtil implements java.io.Serializable {

  private static EditFilterController editPovController;
  private static JsniFilterSaveCallback saveCallback = new JsniFilterSaveCallback();

  private static final IPentahoFilterFactory FILTER_FACTORY = new PentahoFilterFactory();

  public static void setEditFilterController( EditFilterController e ) {
    editPovController = e;
    editPovController.addCallback( saveCallback );
  }

  public static void addFilter( final JavaScriptObject preSave, final JavaScriptObject onSave,
                                final JavaScriptObject onCancel, final FilterDialogConfiguration configuration ) {
    EditFilterController.setConfiguration( configuration );
    EditFilterModel model = new EditFilterModel( configuration, FILTER_FACTORY );
    model.setEditType( EditType.ADD );
    saveCallback.setCallback( preSave, onSave, onCancel );
    editPovController.showFilterDialog( model );
  }

  public static void editFilter( final IPentahoFilter filter, final JavaScriptObject preSave,
                                 final JavaScriptObject onSave, final JavaScriptObject onCancel,
                                 final FilterDialogConfiguration configuration ) {
    EditFilterController.setConfiguration( configuration );
    EditFilterModel model = new EditFilterModel( configuration, FILTER_FACTORY, filter );
    model.setEditType( EditType.EDIT );
    saveCallback.setCallback( preSave, onSave, onCancel );
    editPovController.showFilterDialog( model );
  }
}
