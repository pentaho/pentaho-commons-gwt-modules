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
 * Copyright (c) 2002-2023 Hitachi Vantara. All rights reserved.
 */

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
