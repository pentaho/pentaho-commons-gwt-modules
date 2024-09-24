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

package org.pentaho.gwt.widgets.client.listbox;

import com.google.gwt.user.client.Event;

/**
 * User: nbaker Date: Aug 4, 2010
 */
public interface ListItemListener {

  void itemSelected( ListItem listItem, Event event );

  void doAction( ListItem listItem );
}
