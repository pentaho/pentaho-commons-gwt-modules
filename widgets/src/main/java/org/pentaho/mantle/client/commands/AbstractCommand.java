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
 * Copyright (c) 2002-2019 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.mantle.client.commands;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.pentaho.gwt.widgets.client.dialogs.IDialogCallback;
import org.pentaho.gwt.widgets.client.dialogs.MessageDialogBox;
import org.pentaho.mantle.client.messages.Messages;
import org.pentaho.mantle.client.environment.EnvironmentHelper;
import org.pentaho.mantle.login.client.MantleLoginDialog;

/**
 * This abstract class needs to be extend when using any command that requires server authentication. The execute
 * method first check whether the user is authenticated or not and then if the user is authentication is performs
 * the command described in the performOperation method, otherwise the login screen will be displayed back to the
 * user
 *
 * @author rmansoor
 */
public abstract class AbstractCommand implements Command {

  private CommandResultCallback commandResultCallback;

  /**
   * Checks if the user is logged in, if the user is then it perform operation other wise user if ask to perform
   * the login operation again
   *
   * @param feedback if the feedback needs to be sent back to the caller. Not used currently
   */
  public void execute( final boolean feedback ) {
    execute( (CommandResultCallback) null, feedback );
  }

  /**
   * Checks if the user is logged in, if the user is then it perform operation other wise user if ask to perform
   * the login operation again.
   * <p>
   * After the operation is executed, the CommandCallback object receives an afterExecute() notification.
   *
   * @param commandCallback CommandCallback object to receive execution notification.
   */
  public void execute( CommandCallback commandCallback ) {
    execute( commandCallback, false );
  }

  /**
   * Checks if the user is logged in, if the user is then it perform operation other wise user if ask to perform
   * the login operation again.
   * <p>
   * After the operation is executed, the CommandCallback object receives an afterExecute() notification.
   *
   * @param commandCallback CommandCallback object to receive execution notification.
   * @param feedback        if the feedback needs to be sent back to the caller. Not used currently
   */
  public void execute( final CommandCallback commandCallback, final boolean feedback ) {
    execute( createResultCallback( commandCallback ), feedback );
  }

  /**
   * Checks if the user is logged in, if the user is then it perform operation other wise user if ask to perform
   * the login operation again.
   * <p>
   * After the operation is executed, the CommandResultCallback object receives an afterExecute() notification.
   *
   * @param commandResultCallback CommandResultCallback object to receive execution notification.
   * @param feedback              if the feedback needs to be sent back to the caller. Not used currently
   */
  public void execute( final CommandResultCallback commandResultCallback, final boolean feedback ) {
    this.commandResultCallback = commandResultCallback;

    try {
      final String url = EnvironmentHelper.getFullyQualifiedURL() + "api/mantle/isAuthenticated"; //$NON-NLS-1$
      RequestBuilder requestBuilder = new RequestBuilder( RequestBuilder.GET, url );
      requestBuilder.setHeader( "If-Modified-Since", "01 Jan 1970 00:00:00 GMT" );
      requestBuilder.setHeader( "accept", "text/plain" );
      requestBuilder.sendRequest( null, new RequestCallback() {

        public void onError( Request request, Throwable caught ) {
          doLogin( feedback );
        }

        public void onResponseReceived( Request request, Response response ) {
          performOperationAsync( feedback, AbstractCommand.this.commandResultCallback );
        }

      } );
    } catch ( RequestException e ) {
      Window.alert( e.getMessage() );
    }

  }

  /**
   * Checks if the user is logged in, if the user is then it perform operation other wise user if ask to perform
   * the login operation again
   */
  public void execute() {
    this.commandResultCallback = null;
    try {
      final String url = EnvironmentHelper.getFullyQualifiedURL() + "api/mantle/isAuthenticated"; //$NON-NLS-1$
      RequestBuilder requestBuilder = new RequestBuilder( RequestBuilder.GET, url );
      requestBuilder.setHeader( "If-Modified-Since", "01 Jan 1970 00:00:00 GMT" );
      requestBuilder.setHeader( "accept", "text/plain" );
      requestBuilder.sendRequest( null, new RequestCallback() {

        public void onError( Request request, Throwable caught ) {
          doLogin();
        }

        public void onResponseReceived( Request request, Response response ) {
          performOperation();
        }

      } );
    } catch ( RequestException e ) {
      Window.alert( e.getMessage() );
    }
  }

  /**
   * Display the login screen and validate the credentials supplied by the user if the credentials are correct,
   * the execute method is being invoked otherwise error dialog is being display. On clicking ok button on the
   * dialog box, login screen is displayed again and process is repeated until the user click cancel or user is
   * successfully authenticated
   *
   * @param feedback if the feedback needs to be sent back to the caller. Not used currently
   */
  private void doLogin( final boolean feedback ) {
    MantleLoginDialog.performLogin( new AsyncCallback<Boolean>() {

      public void onFailure( Throwable caught ) {
        MessageDialogBox dialogBox =
          new MessageDialogBox( Messages.getString( "error" ), Messages.getString( "invalidLogin" ), false, false,
            true ) {

          }; //$NON-NLS-1$ //$NON-NLS-2$

        dialogBox.setCallback( new IDialogCallback() {
          public void cancelPressed() {
            if ( AbstractCommand.this.commandResultCallback != null ) {
              AbstractCommand.this.commandResultCallback.onCanceled();
            }
          }

          public void okPressed() {
            doLogin( feedback );
          }
        } );

        dialogBox.center();
      }

      public void onSuccess( Boolean result ) {
        // Preserve any existing command callback.
        execute( commandResultCallback, feedback );
      }
    } );
  }

  /**
   * Display the login screen and and validate the credentials supplied by the user if the credentials are correct,
   * the execute method is being invoked other wise error dialog is being display. On clicking ok button on the
   * dialog box, login screen is displayed again and process is repeated until the user click cancel or user is
   * successfully authenticated
   */
  private void doLogin() {
    Timer t = new Timer() {

      @Override
      public void run() {

        MantleLoginDialog.performLogin( new AsyncCallback<Boolean>() {

          public void onFailure( Throwable caught ) {
            MessageDialogBox dialogBox =
              new MessageDialogBox(
                Messages.getString( "error" ), Messages.getString( "invalidLogin" ), false, false,
                true ); //$NON-NLS-1$ //$NON-NLS-2$
            dialogBox.setCallback( new IDialogCallback() {
              public void cancelPressed() {
              }

              public void okPressed() {
                doLogin();
              }

            } );
            dialogBox.center();
          }

          public void onSuccess( Boolean result ) {
            // There's never a command callback, when this method version is called (doLogin with no arguments).
            execute();
          }
        } );
      }
    };
    t.schedule( 1 );
  }

  /**
   * This is an abstract method which the extending class with implement with logic of performing specific
   * operation
   */
  protected abstract void performOperation();

  /**
   * This is an abstract method which the extending class with implement with logic of performing specific
   * operation
   *
   * @param feedback if the feedback needs to be sent back to the caller. Not used currently
   */
  protected abstract void performOperation( final boolean feedback );

  /**
   * Performs the operation asynchronously.
   * <p>
   * The default implementation calls the synchronous method, performOperation and then calls callback.onSuccess.
   * It does not handle errors, to preserve full compatibility.
   * <p>
   * Actual implementations should handle all states and, ideally, should not throw.
   *
   * @param feedback if the feedback needs to be sent back to the caller. Not used currently
   * @param callback when not null, allows the result of the operation to be reported to the caller.
   */
  protected void performOperationAsync( final boolean feedback, CommandResultCallback callback ) {

    performOperation( feedback );

    if ( callback != null ) {
      callback.onSuccess();
    }
  }

  /**
   * Creates a result callback from a legacy command callback.
   */
  private CommandResultCallback createResultCallback( final CommandCallback callback ) {

    if ( callback == null ) {
      return null;
    }

    return new CommandResultCallback() {
      @Override
      public void onSuccess() {
        callback.afterExecute();
      }

      @Override
      public void onCanceled() {
      }

      @Override
      public void onError( Throwable error ) {
      }
    };
  }
}
