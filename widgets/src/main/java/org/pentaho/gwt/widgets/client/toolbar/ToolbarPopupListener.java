/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.gwt.widgets.client.toolbar;

import com.google.gwt.user.client.ui.PopupPanel;

public interface ToolbarPopupListener {

  void popupOpened( PopupPanel panel );

  void popupClosed( PopupPanel panel );

}
