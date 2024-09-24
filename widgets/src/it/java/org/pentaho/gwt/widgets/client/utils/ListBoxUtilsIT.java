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

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.ListBox;

public class ListBoxUtilsIT extends GWTTestCase {
  public String getModuleName() {
    return "org.pentaho.gwt.widgets.Widgets"; //$NON-NLS-1$
  }

  public void testRemoveAll() throws Exception {
    final ListBox listBox = new ListBox();
    listBox.addItem( "l1" );
    listBox.addItem( "l2" );
    listBox.addItem( "l3" );
    assertEquals( 3, listBox.getItemCount() );
    ListBoxUtils.removeAll( listBox );
    assertEquals( 0, listBox.getItemCount() );
  }
}
