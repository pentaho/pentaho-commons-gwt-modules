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

package org.pentaho.gwt.widgets.client.menuitem;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.safehtml.shared.annotations.IsSafeHtml;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

public class CheckBoxMenuItem extends MenuItem {

  boolean checked = true;

  public CheckBoxMenuItem( String text, Command cmd ) {
    this( text, false, cmd );
  }

  public CheckBoxMenuItem( @IsSafeHtml String text, boolean asHTML, Scheduler.ScheduledCommand cmd ) {
    super( text, asHTML, cmd );
    setChecked( true );
  }

  public void setChecked( boolean checked ) {
    this.checked = checked;
    if ( checked ) {
      setStylePrimaryName( "gwt-MenuItem-checkbox-checked" ); //$NON-NLS-1$
    } else {
      setStylePrimaryName( "gwt-MenuItem-checkbox-unchecked" ); //$NON-NLS-1$
    }
  }

  public boolean isChecked() {
    return checked;
  }

}
