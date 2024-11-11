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


package org.pentaho.mantle.client.dialogs;

import org.pentaho.mantle.client.messages.Messages;

import com.google.gwt.user.client.ui.SimplePanel;

public class WaitPopup extends SimplePanel {

  private static WaitPopup instance = new WaitPopup();

  private WaitPopup() {
  }

  public static WaitPopup getInstance() {
    return instance;
  }

  @Override
  public void setVisible( boolean visible ) {
    if ( visible ) {
      showBusyIndicator( Messages.getString( "pleaseWait" ), Messages.getString( "waitMessage" ) );
    } else {
      hideBusyIndicator();
    }
  }

  public void setVisibleById( boolean visible, String id ) {
    if ( visible ) {
      showBusyIndicatorById( Messages.getString( "pleaseWait" ), Messages.getString( "waitMessage" ),
          id );
    } else {
      hideBusyIndicatorById( id );
    }
  }


  public static native void showBusyIndicator( String title, String message )
  /*-{
      $wnd.require([
              "common-ui/util/BusyIndicator"
          ],

          function (busy) {
              busy.show(title, message);
          });
  }-*/;

  public static native void hideBusyIndicator()
  /*-{
      $wnd.require([
              "common-ui/util/BusyIndicator"
          ],

          function (busy) {
              busy.hide();
          });
  }-*/;

  public static native void showBusyIndicatorById( String title, String message, String id )
  /*-{
      $wnd.require([
              "common-ui/util/BusyIndicator"
          ],

          function (busy) {
              busy.show(title, message, id);
          });
  }-*/;

  public static native void hideBusyIndicatorById( String id )
  /*-{
      $wnd.require([
              "common-ui/util/BusyIndicator"
          ],

          function (busy) {
              busy.hide(id);
          });
  }-*/;

}
