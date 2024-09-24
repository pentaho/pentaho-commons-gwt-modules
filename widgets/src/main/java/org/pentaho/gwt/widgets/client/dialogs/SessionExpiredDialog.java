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
 * Copyright (c) 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.gwt.widgets.client.dialogs;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FocusPanel;
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
  }

  @Override public void center() {
    super.center();
    this.getElement().getStyle().setZIndex( Integer.MAX_VALUE );
    final FocusPanel background = getPageBackground();
    if ( background != null ) {
      background.getElement().getStyle().setZIndex( Integer.MAX_VALUE - 1 );
    }
    setEventListener( this.cancelButton.getElement() );
  }

  /**
   * When data-access or other some plugin is loaded, its GWT module sets its own event listener with capture=true.
   * This might prevent Mantle's listener to be executed.
   *
   * We have to add our listener manually, also with capture=true,
   * and intercept an event before it goes to plugin' listener which cannot handle it.
   */
  public native void setEventListener( Element element )
  /*-{
    var funct = function(event) {
      if (event.target === element) {
        event.stopPropagation();
        var pathArray = $wnd.location.pathname.split( '/' );
        var redirect = 'Login'
        if ( this.ssoEnabled !== 'undefined' && this.ssoEnabled ) {
          redirect = 'Home'
        }
        if ( null != pathArray && pathArray.length > 1 ) {
          $wnd.location.assign( '/' + pathArray[ 1 ] + '/' + redirect );
        }
      }
    };
    $wnd.addEventListener( 'click', funct, true );
  }-*/;

}
