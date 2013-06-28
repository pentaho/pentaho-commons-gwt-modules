/*
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
 * Copyright 2008 Pentaho Corporation.  All rights reserved.
 *
 * Created Mar 25, 2008
 * @author Michael D'Amour
 */
package org.pentaho.gwt.widgets.client.dialogs;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

public class MessageDialogBox extends PromptDialogBox {

  public MessageDialogBox(String title, String message, boolean isHTML, boolean autoHide, boolean modal) {
    super(title, "OK", null, autoHide, modal, isHTML?new HTML(message):new Label(message)); //$NON-NLS-1$
  }

  public MessageDialogBox(String title, String message, boolean isHTML, boolean autoHide, boolean modal, String okText) {
      super(title, okText, null, autoHide, modal, isHTML?new HTML(message):new Label(message)); //$NON-NLS-1$
  }
  
  public MessageDialogBox(String title, String message, boolean isHTML, boolean autoHide, boolean modal, String okText, String notOkText, String cancelText) {
    super(title, okText, notOkText, cancelText, autoHide, modal, isHTML?new HTML(message):new Label(message)); //$NON-NLS-1$
  }
}