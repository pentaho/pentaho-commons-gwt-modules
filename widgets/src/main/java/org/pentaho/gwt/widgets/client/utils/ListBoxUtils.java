/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.gwt.widgets.client.utils;

import com.google.gwt.user.client.ui.ListBox;

public class ListBoxUtils {
  private ListBoxUtils() {

  }

  public static void removeAll( ListBox l ) {
    for ( int ii = l.getItemCount() - 1; ii >= 0; ii-- ) {
      l.removeItem( ii );
    }
  }
}
