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


package org.pentaho.gwt.widgets.login.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.pentaho.gwt.widgets.client.dialogs.IDialogCallback;
import org.pentaho.gwt.widgets.client.dialogs.MessageDialogBox;
import org.pentaho.gwt.widgets.client.messages.Messages;
import org.pentaho.gwt.widgets.client.utils.i18n.IResourceBundleLoadCallback;
import org.pentaho.gwt.widgets.client.utils.i18n.ResourceBundle;

public class LoginEntryPoint implements EntryPoint, IResourceBundleLoadCallback {

  private LoginDialog loginDialog;
  private String returnLocation;

  private Timer popupWarningTimer = new Timer() {
    public void run() {
      MessageDialogBox message =
          new MessageDialogBox( Messages.getString( "error" ), Messages.getString( "popupWarning" ), true, false, true ); //$NON-NLS-1$ //$NON-NLS-2$
      message.center();
    }
  };

  public void onModuleLoad() {
    ResourceBundle messages = new ResourceBundle();
    Messages.setResourceBundle( messages );
    messages.loadBundle( GWT.getModuleBaseForStaticFiles() + "messages/", "LoginMessages", true, LoginEntryPoint.this ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public void bundleLoaded( String bundleName ) {
    AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

      public void onFailure( Throwable err ) {
        MessageDialogBox dialog =
            new MessageDialogBox( Messages.getString( "error" ), Messages.getString( "loginError" ), false, true, true ); //$NON-NLS-1$ //$NON-NLS-2$
        dialog.setCallback( new IDialogCallback() {
          public void cancelPressed() {
          }

          public void okPressed() {
            loginDialog.show();
          }
        } );
        dialog.center();
      }

      public void onSuccess( Boolean newWindow ) {
        if ( newWindow ) {
          String URL =
              ( !returnLocation.equals( "" ) ) ? returnLocation : Window.Location.getPath().replace( "Login", "Home" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

          Window.open( URL, "puc", "menubar=no,location=no,resizable=yes,scrollbars=yes,status=no" ); //$NON-NLS-1$ //$NON-NLS-2$
          // Schedule checking of new Window (Popup checker).
          popupWarningTimer.schedule( 5000 );

        } else if ( !returnLocation.equals( "" ) ) { //$NON-NLS-1$
          Window.Location.assign( returnLocation );
        } else {
          Window.Location.replace( Window.Location.getPath().replace( "Login", "Home" ) ); //$NON-NLS-1$ //$NON-NLS-2$
        }
      }

    };

    loginDialog = new LoginDialog( callback, true );

    setupNativeHooks( loginDialog, this );
  }

  public void setReturnLocation( String str ) {
    returnLocation = str;
    loginDialog.setReturnLocation( returnLocation );
  }

  public void cancelPopupAlertTimer() {
    popupWarningTimer.cancel();
    Window.Location.reload();
  }

  public native void setupNativeHooks( LoginDialog dialog, LoginEntryPoint entry )
  /*-{
     $wnd.openLoginDialog = function(location) {
       entry.@org.pentaho.gwt.widgets.login.client.LoginEntryPoint::setReturnLocation(Ljava/lang/String;)(location);
       dialog.@org.pentaho.gwt.widgets.login.client.LoginDialog::center()();
     }
       
     $wnd.reportWindowOpened = function(){
       entry.@org.pentaho.gwt.widgets.login.client.LoginEntryPoint::cancelPopupAlertTimer()();
     }
   }-*/;

}
