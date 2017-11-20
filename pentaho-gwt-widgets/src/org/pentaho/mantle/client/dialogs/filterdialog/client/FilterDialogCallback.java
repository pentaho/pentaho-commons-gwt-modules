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

import org.pentaho.mantle.client.dialogs.filterdialog.client.jsni.JsniFilterPresaveResult;

/**
 * Provides a feedback mechanism for the Filter Dialog.
 *
 * @author Jordan Ganoff (jganoff@pentaho.com)
 */
public interface FilterDialogCallback {
  /**
   * Allows the callback to veto a save operation before any save operations are called. Returning a valid {@link
   * JsniFilterPresaveResult} will prevent saving and show a dialog to the user if the title and message are present.
   *
   * @param model Current model.
   * @return {@link JsniFilterPresaveResult} to prevent saving, {@code null} to allow saving
   */
  JsniFilterPresaveResult preSave( EditFilterModel model );

  /**
   * Called when the filter dialog's OK button is pressed and the model is ready to be interrogated for new values.
   *
   * @param model Current model
   */
  void onSave( EditFilterModel model );

  /**
   * Called when the filter dialog's Canel button is pressed.
   *
   * @param model Current model
   */
  void onCancel( EditFilterModel model );
}
