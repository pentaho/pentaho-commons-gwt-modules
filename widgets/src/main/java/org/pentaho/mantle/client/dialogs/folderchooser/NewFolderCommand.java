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
 * Copyright (c) 2002-2024 Hitachi Vantara. All rights reserved.
 */

package org.pentaho.mantle.client.dialogs.folderchooser;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.pentaho.gwt.widgets.client.dialogs.IDialogCallback;
import org.pentaho.gwt.widgets.client.dialogs.MessageDialogBox;
import org.pentaho.gwt.widgets.client.dialogs.PromptDialogBox;
import org.pentaho.gwt.widgets.client.genericfile.GenericFile;
import org.pentaho.gwt.widgets.client.genericfile.GenericFileNameUtils;
import org.pentaho.gwt.widgets.client.ui.ICallback;
import org.pentaho.mantle.client.commands.AbstractCommand;
import org.pentaho.mantle.client.csrf.CsrfRequestBuilder;
import org.pentaho.mantle.client.messages.Messages;

import java.util.Objects;

import static org.pentaho.mantle.client.environment.EnvironmentHelper.getFullyQualifiedURL;

public class NewFolderCommand extends AbstractCommand {
  private static class FolderNamePromptDialog extends PromptDialogBox {

    private final TextBox folderNameTextBox;

    public FolderNamePromptDialog() {
      super( Messages.getString( "newFolder" ),
        Messages.getString( "ok" ),
        Messages.getString( "cancel" ),
        false,
        true );

      folderNameTextBox = new TextBox();
      folderNameTextBox.setVisibleLength( 40 );

      VerticalPanel vp = new VerticalPanel();
      vp.add( new Label( Messages.getString( "newFolderName" ) ) );
      vp.add( folderNameTextBox );

      setContent( vp );
    }

    public String getFolderName() {
      return folderNameTextBox.getText();
    }
  }

  @NonNull
  private final GenericFile parentFolder;

  @Nullable
  private ICallback<String> callback;

  public NewFolderCommand( @NonNull GenericFile parentFolder ) {
    Objects.requireNonNull( parentFolder );
    this.parentFolder = parentFolder;
  }

  @Nullable
  public ICallback<String> getCallback() {
    return callback;
  }

  public void setCallback( @Nullable ICallback<String> callback ) {
    this.callback = callback;
  }

  protected void performOperation() {
    performOperation( true );
  }

  protected void performOperation( boolean feedback ) {
    FolderNamePromptDialog folderNameDialog = new FolderNamePromptDialog();
    folderNameDialog.setCallback( new IDialogCallback() {
      public void okPressed() {
        onFolderDialogOk( folderNameDialog.getFolderName() );
      }

      public void cancelPressed() {
        folderNameDialog.hide();
      }
    } );
    folderNameDialog.center();
  }

  private void onFolderDialogOk( String folderName ) {
    // TODO: validate name on the server as part of create instead, so that logic can depend on provider.
    if ( !GenericFileNameUtils.isValidFolderName( folderName ) ) {
      showInvalidFolderNameError( folderName );
      return;
    }

    String folderPath = GenericFileNameUtils.buildPath( parentFolder.getPath(), folderName );

    String createDirUrl = buildCreateFolderUrl( folderPath );

    RequestBuilder createDirRequestBuilder = new CsrfRequestBuilder( RequestBuilder.POST, createDirUrl );
    try {
      createDirRequestBuilder.sendRequest( null, new RequestCallback() {
        @Override
        public void onError( Request createFolderRequest, Throwable exception ) {
          showCreateFolderResponseError( -1, folderName );
        }

        @Override
        public void onResponseReceived( Request createFolderRequest, Response createFolderResponse ) {
          if ( createFolderResponse.getStatusCode() == Response.SC_CREATED ) {
            onFolderCreated( folderPath );
          } else {
            showCreateFolderResponseError( createFolderResponse.getStatusCode(), folderName );
          }
        }
      } );
    } catch ( RequestException e ) {
      Window.alert( e.getLocalizedMessage() );
    }
  }

  @NonNull
  private static String buildCreateFolderUrl( @NonNull String folderPath ) {
    return getFullyQualifiedURL()
      + "plugin/scheduler-plugin/api/generic-files/folders/"
      + GenericFileNameUtils.encodePath( folderPath );
  }

  private void onFolderCreated( @NonNull String folderPath ) {
    if ( callback != null ) {
      callback.onHandle( folderPath );
    }

    // TODO: not distinguishing between providers, repo or others.
    setRepositoriesDirty( true );
  }

  private static native void setRepositoriesDirty( boolean isDirty ) /*-{
    // Notify FileChooserDialog
    $wnd.top.mantle_setIsRepoDirty(isDirty);

    // Notify Browse Files perspective
    $wnd.top.mantle_isBrowseRepoDirty = isDirty;
  }-*/;

  private void showInvalidFolderNameError( String folderName ) {
    showCreateFolderResponseError( Response.SC_BAD_REQUEST, folderName );
  }

  private void showCreateFolderResponseError( int statusCode, @NonNull String folderName ) {
    showErrorDialog( getErrorMessage( statusCode, folderName ) );
  }

  private String getErrorMessage( int statusCode, @NonNull String folderName ) {
    switch ( statusCode ) {
      case Response.SC_BAD_REQUEST:
        // This error message is very specific, referring to illegal characters.
        // While there are other types of invalid paths / bad requests
        // (for example, trying to create a folder on a non-existing VFS connection),
        // they are highly unlikely to occur when the service is used from the UI.
        return Messages.getString( "containsIllegalCharacters", folderName );

      case Response.SC_FORBIDDEN:
        // This resource string id is the legacy one and does not reflect the fact that
        // the text says there is lack of permissions to create the folder.
        // Keeping the id, due to compatibility with existing translations,
        // but defined the `couldNotCreateFolderGeneral` for the general error case.
        return Messages.getString( "couldNotCreateFolder", folderName );

      case Response.SC_CONFLICT:
        return Messages.getString( "couldNotCreateFolderDuplicate", folderName );

      default:
        // General error message. Can also pass -1 to surely end up here.
        return Messages.getString( "couldNotCreateFolderGeneral", folderName );
    }
  }

  private static void showErrorDialog( String errorMessage ) {
    MessageDialogBox dialogBox =
      new MessageDialogBox( Messages.getString( "error" ), errorMessage, false, false, true );
    dialogBox.center();
  }
}
