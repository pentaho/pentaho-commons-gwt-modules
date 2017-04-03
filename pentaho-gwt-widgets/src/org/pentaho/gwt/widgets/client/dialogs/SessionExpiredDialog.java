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
 * Copyright (c) 2017 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.gwt.widgets.client.dialogs;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.pentaho.gwt.widgets.client.messages.Messages;


public class SessionExpiredDialog extends PromptDialogBox {

  public SessionExpiredDialog() {
    super( Messages.getString( "dialog.sessionExpired.title" ), Messages.getString( "dialog.button.ok" ), null,
      Messages.getString( "dialog.sessionExpired.close" ), false, true );
    this.okButton.setVisible( false );
    final VerticalPanel content = new VerticalPanel();
    final Label contentString = new Label( Messages.getString( "dialog.sessionExpired.content" ) );
    content.add( contentString );
    setContent( content );
    setCallback( new SessionExpiredDialogCallback() );
  }

  private class SessionExpiredDialogCallback implements IDialogCallback {

    @Override public void okPressed() {
      //OK button is always hidden
    }

    @Override public void cancelPressed() {
      redirectToPUCLogin();
    }

    private void redirectToPUCLogin() {
      final String[] pathArray = Window.Location.getPath().split( "/" );
      if ( null != pathArray && pathArray.length > 1 ) {
        Window.Location.assign(  "/" + pathArray[ 1 ] + "/Login"  );
      }
    }
  }


}
