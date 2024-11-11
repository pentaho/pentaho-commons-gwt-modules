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


package org.pentaho.mantle.client.dialogs.folderchooser;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.pentaho.gwt.widgets.client.dialogs.MessageDialogBox;
import org.pentaho.gwt.widgets.client.ui.ICallback;
import org.pentaho.mantle.client.commands.AbstractCommand;
import org.pentaho.mantle.client.messages.Messages;

import static org.pentaho.mantle.client.environment.EnvironmentHelper.getFullyQualifiedURL;

public class RefreshFolderTreeCommand extends AbstractCommand {
  @Nullable
  private ICallback<Void> callback;

  @Nullable
  public ICallback<Void> getCallback() {
    return callback;
  }

  public void setCallback( @Nullable ICallback<Void> callback ) {
    this.callback = callback;
  }

  @Override
  protected void performOperation() {
    performOperation( true );
  }

  @Override
  protected void performOperation( boolean feedback ) {
    try {
      RequestBuilder requestBuilder = new RequestBuilder( RequestBuilder.DELETE, buildClearCacheUrl() );
      requestBuilder.sendRequest( null, new RequestCallback() {
        public void onError( Request request, Throwable caught ) {
          showRefreshFolderTreeResponseError();
        }

        public void onResponseReceived( Request request, Response response ) {
          if ( response.getStatusCode() == Response.SC_NO_CONTENT ) {
            onFolderTreeRefreshed();
          } else {
            showRefreshFolderTreeResponseError();
          }
        }
      } );
    } catch ( RequestException e ) {
      showRefreshFolderTreeResponseError();
    }
  }

  @NonNull
  private static String buildClearCacheUrl() {
    return getFullyQualifiedURL() + "plugin/scheduler-plugin/api/generic-files/tree/cache";
  }

  private void onFolderTreeRefreshed() {
    if ( callback != null ) {
      callback.onHandle( null );
    }
  }

  private void showRefreshFolderTreeResponseError() {
    showErrorDialog( Messages.getString( "couldNotRefreshFolderTree" ) );
  }

  private static void showErrorDialog( String errorMessage ) {
    MessageDialogBox dialogBox =
      new MessageDialogBox( Messages.getString( "error" ), errorMessage, false, false, true );
    dialogBox.center();
  }
}
