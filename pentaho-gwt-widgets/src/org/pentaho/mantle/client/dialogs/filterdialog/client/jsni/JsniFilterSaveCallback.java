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

package org.pentaho.mantle.client.dialogs.filterdialog.client.jsni;

import com.google.gwt.core.client.JavaScriptObject;
import org.pentaho.mantle.client.dialogs.filterdialog.client.EditFilterModel;
import org.pentaho.mantle.client.dialogs.filterdialog.client.FilterDialogCallback;

/**
 * Wraps native JavaScript functions so that they can be notified when the filter dialog's save or cancel methods are
 * called. <p/> <p> For example: <p/>
 * <pre>
 * onSave = function(filter) {
 *   doSomethingWithSaved(filter);
 * }
 * </pre>
 * <p/> </p>
 *
 * @author Jordan Ganoff (jganoff@pentaho.com)
 */
public class JsniFilterSaveCallback implements FilterDialogCallback, java.io.Serializable {

  private JavaScriptObject preSaveHook;

  private JavaScriptObject onSave;

  private JavaScriptObject onCancel;

  /**
   * Set the function callbacks for the next invocation. These will be removed when either callback method has been
   * invoked.
   *
   * @param onSave   Function to call when the filter dialog's save button is pressed.
   * @param onCancel Function to call when the filter dialog's cancel button is pressed.
   */
  public void setCallback( JavaScriptObject preSaveHook, JavaScriptObject onSave, JavaScriptObject onCancel ) {
    this.preSaveHook = preSaveHook;
    this.onSave = onSave;
    this.onCancel = onCancel;
  }

  private void removeCallbacks() {
    this.preSaveHook = null;
    this.onSave = null;
    this.onCancel = null;
  }

  @Override
  public JsniFilterPresaveResult preSave( EditFilterModel model ) {
    if ( preSaveHook != null ) {
      IPentahoFilter filter = model.applyTo();
      return callPresaveCallback( preSaveHook, filter );
    }
    return null;
  }

  @Override
  public void onSave( EditFilterModel model ) {
    if ( onSave != null ) {
      IPentahoFilter filter = model.applyTo();
      callCallback( onSave, filter );
    }
    removeCallbacks();
  }

  @Override
  public void onCancel( EditFilterModel model ) {
    if ( onCancel != null ) {
      callCallback( onCancel, null );
    }
    removeCallbacks();
  }

  private static native JsniFilterPresaveResult callPresaveCallback( JavaScriptObject callback,
                                                                     IPentahoFilter filter )/*-{
    return callback.call(callback, filter);
  }-*/;

  private static native boolean callCallback( JavaScriptObject callback, IPentahoFilter filter )/*-{
    return callback.call(callback, filter);
  }-*/;
}
