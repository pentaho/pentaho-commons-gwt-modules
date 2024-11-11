/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.gwt.widgets.client.ui;

import com.google.gwt.user.client.ui.Widget;

/**
 * User: nbaker Date: Aug 6, 2010
 */
public interface Draggable {
  Widget makeProxy( Widget ele );

  Object getDragObject();

  void notifyDragFinished();

  void setDropValid( boolean valid );
}
