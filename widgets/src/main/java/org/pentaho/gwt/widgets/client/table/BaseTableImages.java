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


package org.pentaho.gwt.widgets.client.table;

import com.google.gwt.gen2.table.client.AbstractScrollTable;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public interface BaseTableImages extends AbstractScrollTable.ScrollTableImages {

  public AbstractImagePrototype scrollTableFillWidth();

  public AbstractImagePrototype scrollTableAscending();

  public AbstractImagePrototype scrollTableDescending();

}
