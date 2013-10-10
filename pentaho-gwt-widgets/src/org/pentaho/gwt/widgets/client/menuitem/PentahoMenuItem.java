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
 * Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.gwt.widgets.client.menuitem;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

public class PentahoMenuItem extends MenuItem {

  private boolean enabled = true;
  private boolean useCheckUI = false;
  private boolean checked = false;

  public PentahoMenuItem( String text, Command cmd ) {
    super( text, cmd );
    setEnabled( enabled );
  }

  public void setEnabled( boolean enabled ) {
    this.enabled = enabled;
    if ( enabled ) {
      setStyleName( "gwt-MenuItem" ); //$NON-NLS-1$
    } else {
      setStyleName( "disabledMenuItem" ); //$NON-NLS-1$
    }
    if ( useCheckUI ) {
      setChecked( checked );
    }
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setChecked( boolean checked ) {
    this.checked = checked;
    if ( enabled ) {
      if ( checked ) {
        setStyleName( "gwt-MenuItem-checkbox-checked" ); //$NON-NLS-1$
      } else {
        setStyleName( "gwt-MenuItem-checkbox-unchecked" ); //$NON-NLS-1$
      }
    } else {
      setStyleName( "disabledMenuItem" ); //$NON-NLS-1$
    }
  }

  public boolean isChecked() {
    return checked;
  }

  public boolean isUseCheckUI() {
    return useCheckUI;
  }

  public void setUseCheckUI( boolean useCheckUI ) {
    this.useCheckUI = useCheckUI;
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
