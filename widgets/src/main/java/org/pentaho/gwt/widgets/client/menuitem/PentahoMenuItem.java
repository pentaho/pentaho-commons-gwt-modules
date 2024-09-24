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

public class PentahoMenuItem extends MenuItem {

  protected boolean enabled = true;
  protected boolean useCheckUI = false;
  private boolean checked = false;

  public PentahoMenuItem( String text, Command cmd ) {
    this( text, false, cmd );
  }

  public PentahoMenuItem( @IsSafeHtml String text, boolean asHTML, Scheduler.ScheduledCommand cmd ) {
    super( text, asHTML, cmd );
    setEnabled( true );
  }

  public void setEnabled( boolean enabled ) {
    this.enabled = enabled;
    updateStyles();
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setChecked( boolean checked ) {
    this.checked = checked;
    updateStyles();
  }

  public boolean isChecked() {
    return checked;
  }

  public boolean isUseCheckUI() {
    return useCheckUI;
  }

  public void setUseCheckUI( boolean useCheckUI ) {
    this.useCheckUI = useCheckUI;
    updateStyles();
  }

  protected void updateStyles() {
    if ( enabled ) {
      if ( useCheckUI ) {
        if ( checked ) {
          setStyleName( "gwt-MenuItem-checkbox-checked" );
        } else {
          setStyleName( "gwt-MenuItem-checkbox-unchecked" );
        }
      } else {
        setStyleName( "gwt-MenuItem" );
      }
    } else {
      setStyleName( "disabledMenuItem" );
    }
  }

  @Override
  public Command getCommand() {
    if ( isEnabled() ) {
      return super.getCommand();
    } else {
      return null;
    }
  }
}
