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

package org.pentaho.mantle.client.dialogs.scheduling;

import java.util.Date;

import com.google.gwt.user.client.ui.VerticalPanel;
import org.pentaho.gwt.widgets.client.dialogs.IDialogCallback;
import org.pentaho.gwt.widgets.client.dialogs.MessageDialogBox;
import org.pentaho.gwt.widgets.client.dialogs.PromptDialogBox;
import org.pentaho.gwt.widgets.client.formatter.JSDateTextFormatter;
import org.pentaho.gwt.widgets.client.panel.HorizontalFlexPanel;
import org.pentaho.gwt.widgets.client.panel.VerticalFlexPanel;
import org.pentaho.gwt.widgets.client.utils.NameUtils;
import org.pentaho.gwt.widgets.client.utils.string.StringUtils;
import org.pentaho.gwt.widgets.client.wizards.AbstractWizardDialog.ScheduleDialogType;
import org.pentaho.mantle.client.dialogs.WaitPopup;
import org.pentaho.mantle.client.dialogs.folderchooser.SelectFolderDialog;
import org.pentaho.mantle.client.messages.Messages;
import org.pentaho.mantle.client.environment.EnvironmentHelper;
import org.pentaho.mantle.client.workspace.JsJob;
import org.pentaho.mantle.client.workspace.JsJobParam;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public class NewScheduleDialog extends PromptDialogBox {

  private String filePath;
  private IDialogCallback callback;
  private boolean isEmailConfValid;
  private JsJob jsJob;

  private ScheduleRecurrenceDialog recurrenceDialog = null;

  private TextBox scheduleOwnerTextBox = new TextBox();
  private Label scheduleOwnerErrorLabel = new Label();
  private TextBox scheduleNameTextBox = new TextBox();
  private static TextBox scheduleLocationTextBox = new TextBox();
  private CheckBox appendTimeChk = new CheckBox();
  private ListBox timestampLB = new ListBox();
  private CaptionPanel previewCaptionPanel;
  private Label scheduleNamePreviewLabel;
  private CheckBox overrideExistingChk = new CheckBox();
  private static HandlerRegistration changeHandlerReg = null;
  private static HandlerRegistration keyHandlerReg = null;

  static {
    scheduleLocationTextBox.setText( getDefaultSaveLocation() );
  }

  private static native String getDefaultSaveLocation()
  /*-{
      return window.top.HOME_FOLDER;
  }-*/;

  private static native void delete( JsArray<?> array, int index, int count )
  /*-{
      array.splice(index, count);
  }-*/;



  /**
   * @deprecated Need to set callback
   */
  public NewScheduleDialog( JsJob jsJob, IDialogCallback callback, boolean isEmailConfValid ) {
    super( Messages.getString( "editSchedule" ), Messages.getString( "next" ),
      Messages.getString( "cancel" ), false, true );

    this.jsJob = jsJob;
    this.filePath = jsJob.getFullResourceName();
    this.callback = callback;
    this.isEmailConfValid = isEmailConfValid;

    createUI();

    setResponsive( true );
    setSizingMode( DialogSizingMode.FILL_VIEWPORT );
    setWidthCategory( DialogWidthCategory.SMALL );
  }

  public NewScheduleDialog( String filePath, IDialogCallback callback, boolean isEmailConfValid ) {
    super( Messages.getString( "newSchedule" ), Messages.getString( "next" ),
      Messages.getString( "cancel" ), false, true );

    this.filePath = filePath;
    this.callback = callback;
    this.isEmailConfValid = isEmailConfValid;

    createUI();

    setResponsive( true );
    setSizingMode( DialogSizingMode.FILL_VIEWPORT );
    setWidthCategory( DialogWidthCategory.SMALL );
  }

  private void createUI() {
    addStyleName("schedule-output-location-dialog");
    VerticalFlexPanel content = new VerticalFlexPanel();

    HorizontalFlexPanel scheduleNameLabelPanel = new HorizontalFlexPanel();
    Label scheduleNameLabel = new Label( Messages.getString( "scheduleNameColon" ) );
    scheduleNameLabel.addStyleName( "schedule-name" );
    scheduleNameLabel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_LEFT );

    Label scheduleNameInfoLabel = new Label( Messages.getString( "scheduleNameInfo" ) );
    scheduleNameInfoLabel.setStyleName( "msg-Label" );
    scheduleNameInfoLabel.addStyleName( "schedule-name-info" );

    scheduleNameLabelPanel.add( scheduleNameLabel );
    scheduleNameLabelPanel.add( scheduleNameInfoLabel );

    String defaultName = filePath.substring( filePath.lastIndexOf( "/" ) + 1, filePath.lastIndexOf( "." ) );
    scheduleNameTextBox.getElement().setId( "schedule-name-input" );
    scheduleNameTextBox.setText( defaultName );

    content.add( scheduleNameLabelPanel );

    timestampLB.addStyleName( "schedule-timestamp-listbox" );

    timestampLB.addItem( "yyyy-MM-dd" );
    timestampLB.addItem( "yyyyMMdd" );
    timestampLB.addItem( "yyyyMMddHHmmss" );
    timestampLB.addItem( "MM-dd-yyyy" );
    timestampLB.addItem( "MM-dd-yy" );
    timestampLB.addItem( "dd-MM-yyyy" );

    timestampLB.addClickHandler( new ClickHandler() {
      @Override
      public void onClick( ClickEvent event ) {
        int index = ( (ListBox) event.getSource() ).getSelectedIndex();
        scheduleNamePreviewLabel.setText( getPreviewName( index ) );
      }
    } );

    timestampLB.setVisible( false );

    HorizontalFlexPanel scheduleNamePanel = new HorizontalFlexPanel();
    scheduleNamePanel.addStyleName( "schedule-name-panel" );
    scheduleNamePanel.add( scheduleNameTextBox );
    scheduleNamePanel.setCellVerticalAlignment( scheduleNameTextBox, HasVerticalAlignment.ALIGN_MIDDLE );
    scheduleNamePanel.add( timestampLB );

    content.add( scheduleNamePanel );

    appendTimeChk.setText( Messages.getString( "appendTimeToName" ) ); //$NON-NLS-1$
    appendTimeChk.addClickHandler( new ClickHandler() {
      @Override
      public void onClick( ClickEvent event ) {
        boolean checked = ( (CheckBox) event.getSource() ).getValue().booleanValue();
        refreshAppendedTimestamp( checked );
      }
    } );
    content.add( appendTimeChk );

    previewCaptionPanel = new CaptionPanel( Messages.getString( "preview" ) );
    previewCaptionPanel.setStyleName( "schedule-caption-panel" );

    scheduleNamePreviewLabel = new Label( getPreviewName( timestampLB.getSelectedIndex() ) );
    scheduleNamePreviewLabel.addStyleName( "schedule-name-preview" );

    previewCaptionPanel.add( scheduleNamePreviewLabel );
    previewCaptionPanel.setVisible( false );

    content.add( previewCaptionPanel );

    Label scheduleLocationLabel = new Label( Messages.getString( "generatedContentLocation" ) );
    scheduleLocationLabel.setStyleName( ScheduleEditor.SCHEDULE_LABEL );
    content.add( scheduleLocationLabel );

    Button browseButton = new Button( Messages.getString( "select" ) );
    browseButton.addClickHandler( new ClickHandler() {

      public void onClick( ClickEvent event ) {
        final SelectFolderDialog selectFolder = new SelectFolderDialog();
        selectFolder.setCallback( new IDialogCallback() {
          public void okPressed() {
            scheduleLocationTextBox.setText( selectFolder.getSelectedPath() );
          }

          public void cancelPressed() {
          }
        } );
        selectFolder.center();
      }
    } );
    browseButton.setStyleName( "pentaho-button" );
    browseButton.getElement().setId( "schedule-dialog-select-button" );

    ChangeHandler ch = new ChangeHandler() {
      public void onChange( ChangeEvent event ) {
        scheduleNamePreviewLabel.setText( getPreviewName( timestampLB.getSelectedIndex() ) );
        updateButtonState();
      }
    };
    KeyUpHandler kh = new KeyUpHandler() {
      public void onKeyUp( KeyUpEvent event ) {
        scheduleNamePreviewLabel.setText( getPreviewName( timestampLB.getSelectedIndex() ) );
        updateButtonState();
      }
    };

    if ( keyHandlerReg != null ) {
      keyHandlerReg.removeHandler();
    }
    if ( changeHandlerReg != null ) {
      changeHandlerReg.removeHandler();
    }
    keyHandlerReg = scheduleNameTextBox.addKeyUpHandler( kh );
    changeHandlerReg = scheduleLocationTextBox.addChangeHandler( ch );
    scheduleNameTextBox.addChangeHandler( ch );

    scheduleLocationTextBox.getElement().setId( "generated-content-location" );
    HorizontalFlexPanel locationPanel = new HorizontalFlexPanel();
    scheduleLocationTextBox.setEnabled( false );
    locationPanel.add( scheduleLocationTextBox );
    locationPanel.setCellVerticalAlignment( scheduleLocationTextBox, HasVerticalAlignment.ALIGN_MIDDLE );
    locationPanel.add( browseButton );

    content.add( locationPanel );
    content.add( overrideExistingChk );

    content.add( createScheduleOwnerUI() );

    if ( jsJob != null ) {
      scheduleNameTextBox.setText( jsJob.getJobName() );
      scheduleLocationTextBox.setText( jsJob.getOutputPath() );
      String autoCreateUniqueFilename = jsJob.getJobParamValue( "autoCreateUniqueFilename" );
      if ( autoCreateUniqueFilename != null ) {
        boolean autoCreate = Boolean.valueOf( autoCreateUniqueFilename );
        if ( !autoCreate ) {
          overrideExistingChk.setValue( true );
        }
      }

      String appendDateFormat = jsJob.getJobParamValue( "appendDateFormat" );
      if ( appendDateFormat != null ) {
        appendTimeChk.setValue( true );
        for ( int i = 0; i < timestampLB.getItemCount(); i++ ) {
          if ( appendDateFormat.equals( timestampLB.getValue( i ) ) ) {
            timestampLB.setSelectedIndex( i );
            break;
          }
        }
      }
    }

    refreshAppendedTimestamp( appendTimeChk.getValue().booleanValue() );

    setContent( content );
    content.getElement().getStyle().clearHeight();
    content.getParent().setHeight( "100%" );
    content.getElement().getParentElement().getStyle().setVerticalAlign( VerticalAlign.TOP );

    okButton.getParent().getParent().addStyleName( "button-panel" );

    updateButtonState();
    setSize( "650px", "450px" );

    validateScheduleLocationTextBox();
    addStyleName( "new-schedule-dialog" );
  }

  private VerticalPanel createScheduleOwnerUI() {
    VerticalPanel panel = new VerticalFlexPanel();

    Label label = new Label( Messages.getString( "owner" ) );
    label.addStyleName( "schedule-owner" );
    label.setStyleName( ScheduleEditor.SCHEDULE_LABEL );
    panel.add( label );

    scheduleOwnerTextBox.getElement().setId( "schedule-owner-input" );

    final JavaScriptObject ownerCallback = ScheduleHelper.debounce( this::validateScheduleOwner, 300 );
    scheduleOwnerTextBox.addKeyUpHandler( event -> ScheduleHelper.callDebounce( ownerCallback ) );

    if ( jsJob == null ) {
      scheduleOwnerTextBox.setEnabled( false );

      final String currentUserUrl = EnvironmentHelper.getFullyQualifiedURL() + "api/session/userName";
      final RequestBuilder builder = new RequestBuilder( RequestBuilder.GET, currentUserUrl );

      try {
        RequestCallback rc = new RequestCallback() {
          @Override
          public void onResponseReceived( final Request request, final Response response ) {
            if ( response.getStatusCode() == 200 ) {
              scheduleOwnerTextBox.setText( response.getText() );
              updateButtonState();
            }
          }

          @Override
          public void onError( Request request, Throwable exception ) {
            // noop
          }
        };
        builder.sendRequest( "", rc );

      } catch ( RequestException e ) {
        // noop
      }
    } else {
      scheduleOwnerTextBox.setText( jsJob.getUserName() );

      final String isAdminUserUrl = EnvironmentHelper.getFullyQualifiedURL() + "api/mantle/isAdministrator";
      final RequestBuilder builder = new RequestBuilder( RequestBuilder.GET, isAdminUserUrl );

      try {
        RequestCallback rc = new RequestCallback() {
          @Override
          public void onResponseReceived( final Request request, final Response response ) {
            if ( response.getStatusCode() == 200 ) {
              boolean isAdmin = Boolean.parseBoolean( response.getText() );
              scheduleOwnerTextBox.setEnabled( isAdmin );
            }
          }

          @Override
          public void onError( Request request, Throwable exception ) {
            scheduleOwnerTextBox.setEnabled( false );
          }
        };
        builder.sendRequest( "", rc );

      } catch ( RequestException e ) {
        scheduleOwnerTextBox.setEnabled( false );
      }
    }

    scheduleOwnerErrorLabel.addStyleName( "schedule-owner-error" );
    scheduleOwnerErrorLabel.setVisible( false );

    panel.add( scheduleOwnerTextBox );
    panel.add( scheduleOwnerErrorLabel );

    return panel;
  }

  protected void onOk() {
    String name;
    if ( appendTimeChk.getValue().booleanValue() ) {
      name = getPreviewName( timestampLB.getSelectedIndex() );
    } else {
      //trim the name if there is no timestamp appended
      scheduleNameTextBox.setText( scheduleNameTextBox.getText().trim() );

      name = scheduleNameTextBox.getText();
    }

    if ( !NameUtils.isValidFileName( name ) ) {
      MessageDialogBox errorDialog =
          new MessageDialogBox( Messages.getString( "error" ), Messages.getString( "prohibitedNameSymbols", name,
              NameUtils.reservedCharListForDisplay( " " ) ), false, false, true ); //$NON-NLS-1$ //$NON-NLS-2$
      errorDialog.center();
      return;
    }

    // check if has parameterizable
    WaitPopup.getInstance().setVisible( true );
    String urlPath = URL.encodePathSegment( NameUtils.encodeRepositoryPath( filePath ) );

    RequestBuilder scheduleFileRequestBuilder;
    final boolean isXAction;

    if ( ( urlPath != null ) && ( urlPath.endsWith( "xaction" ) ) ) {
      isXAction = true;
      scheduleFileRequestBuilder = new RequestBuilder( RequestBuilder.GET, EnvironmentHelper.getFullyQualifiedURL() + "api/repos/" + urlPath
          + "/parameterUi" );
    } else {
      isXAction = false;
      scheduleFileRequestBuilder = new RequestBuilder( RequestBuilder.GET, EnvironmentHelper.getFullyQualifiedURL() + "api/repo/files/" + urlPath
          + "/parameterizable" );
    }

    scheduleFileRequestBuilder.setHeader( "accept", "text/plain" );
    scheduleFileRequestBuilder.setHeader( "If-Modified-Since", "01 Jan 1970 00:00:00 GMT" );
    try {
      scheduleFileRequestBuilder.sendRequest( null, new RequestCallback() {

        public void onError( Request request, Throwable exception ) {
          WaitPopup.getInstance().setVisible( false );
          MessageDialogBox dialogBox =
              new MessageDialogBox( Messages.getString( "error" ), exception.toString(), false, false, true ); //$NON-NLS-1$
          dialogBox.center();
        }

        public void onResponseReceived( Request request, Response response ) {
          WaitPopup.getInstance().setVisible( false );
          if ( response.getStatusCode() == Response.SC_OK ) {
            String responseMessage = response.getText();
            boolean hasParams;

            if ( isXAction ) {
              int numOfInputs = StringUtils.countMatches( responseMessage, "<input" );
              int NumOfHiddenInputs = StringUtils.countMatches( responseMessage, "type=\"hidden\"" );
              hasParams = numOfInputs - NumOfHiddenInputs > 0 ? true : false;
            } else {
              hasParams = Boolean.parseBoolean( response.getText() );
            }

            boolean overwriteFile = overrideExistingChk.getValue().booleanValue();
            String dateFormat = null;
            if ( appendTimeChk.getValue().booleanValue() ) {
              dateFormat = timestampLB.getValue( timestampLB.getSelectedIndex() );
            }

            if ( jsJob != null ) {
              jsJob.setJobName( scheduleNameTextBox.getText() );
              jsJob.setOutputPath( scheduleLocationTextBox.getText(), scheduleNameTextBox.getText() );

              String scheduleOwner = scheduleOwnerTextBox.getText().trim();
              if ( !StringUtils.isEmpty( scheduleOwner ) && !scheduleOwner.equalsIgnoreCase( jsJob.getUserName() ) ) {
                JsJobParam actionUser = jsJob.getJobParam( "ActionAdapterQuartzJob-ActionUser" );

                if ( actionUser != null ) {
                  if ( !scheduleOwner.equalsIgnoreCase( actionUser.getValue() ) ) {
                    actionUser.setValue( scheduleOwner );
                  }
                } else {
                  JsJobParam jjp = JavaScriptObject.createObject().cast();
                  jjp.setName( "ActionAdapterQuartzJob-ActionUser" );
                  jjp.setValue( scheduleOwner );
                  jsJob.getJobParams().push( jjp );
                }
              }

              if ( jsJob.getJobParamValue( "appendDateFormat" ) != null ) {
                if ( dateFormat != null ) {
                  JsJobParam jp = jsJob.getJobParam( "appendDateFormat" );
                  jp.setValue( dateFormat );
                } else {
                  for ( int j = 0; j < jsJob.getJobParams().length(); j++ ) {
                    JsJobParam jjp = jsJob.getJobParams().get( j );
                    if ( "appendDateFormat".equals( jjp.getName() ) ) {
                      delete( jsJob.getJobParams(), j, 1 );
                    }
                  }
                }
              } else {
                if ( dateFormat != null ) {
                  JsJobParam jjp = (JsJobParam) JavaScriptObject.createObject().cast();
                  jjp.setName( "appendDateFormat" );
                  jjp.setValue( dateFormat );
                  jsJob.getJobParams().set( jsJob.getJobParams().length(), jjp );
                }
              }

              if ( jsJob.getJobParamValue( "autoCreateUniqueFilename" ) != null ) {
                if ( !jsJob.getJobParamValue( "autoCreateUniqueFilename" ).equals( String.valueOf( !overwriteFile ) ) ) {
                  JsJobParam jp = jsJob.getJobParam( "autoCreateUniqueFilename" );
                  jp.setValue( String.valueOf( !overwriteFile ) );
                }
              } else {
                JsJobParam jjp = (JsJobParam) JavaScriptObject.createObject().cast();
                jjp.setName( "autoCreateUniqueFilename" );
                jjp.setValue( String.valueOf( !overwriteFile ) );
                jsJob.getJobParams().set( jsJob.getJobParams().length(), jjp );
              }

              if ( recurrenceDialog == null ) {
                recurrenceDialog = new ScheduleRecurrenceDialog( NewScheduleDialog.this, jsJob, callback,
                  hasParams, isEmailConfValid, ScheduleDialogType.SCHEDULER );
              }
            } else if ( recurrenceDialog == null ) {
              recurrenceDialog = new ScheduleRecurrenceDialog( NewScheduleDialog.this, filePath,
                scheduleLocationTextBox.getText(), scheduleNameTextBox.getText(), dateFormat, overwriteFile, callback,
                hasParams, isEmailConfValid );
            } else {
              recurrenceDialog.scheduleName = scheduleNameTextBox.getText();
              recurrenceDialog.outputLocation = scheduleLocationTextBox.getText();
            }

            recurrenceDialog.setParentDialog( NewScheduleDialog.this );
            recurrenceDialog.center();
            NewScheduleDialog.super.onOk();
          }
        }
      } );
    } catch ( RequestException e ) {
      WaitPopup.getInstance().setVisible( false );
      // showError(e);
    }
  }

  private void updateButtonState() {
    boolean hasLocation = !StringUtils.isEmpty( scheduleLocationTextBox.getText() );
    boolean hasName = !StringUtils.isEmpty( scheduleNameTextBox.getText() );
    boolean hasOwner = !StringUtils.isEmpty( scheduleOwnerTextBox.getText() );
    boolean hasOwnerError = scheduleOwnerErrorLabel.isVisible();

    okButton.setEnabled( hasLocation && hasName && hasOwner && !hasOwnerError );
  }

  public void setFocus() {
    scheduleNameTextBox.setFocus( true );
  }

  public String getScheduleName() {
    return scheduleNameTextBox.getText();
  }

  public String getPreviewName( int index ) {
    JSDateTextFormatter formatter = new JSDateTextFormatter( timestampLB.getValue( index ) );
    Date date = new Date();
    return scheduleNameTextBox.getText() + formatter.format( String.valueOf( date.getTime() ) );
  }

  public void setScheduleName( String scheduleName ) {
    scheduleNameTextBox.setText( scheduleName );
  }

  private void validateScheduleLocationTextBox() {
    final Command errorCallback = new Command() {
      @Override
      public void execute() {
        String previousPath = OutputLocationUtils.getPreviousLocationPath( scheduleLocationTextBox.getText() );
        if ( !previousPath.isEmpty() ) {
          scheduleLocationTextBox.setText( previousPath );
          validateScheduleLocationTextBox();
        } else {
          scheduleLocationTextBox.setText( getDefaultSaveLocation() ); // restore default location
        }
      }
    };
    OutputLocationUtils.validateOutputLocation( scheduleLocationTextBox.getText(), null, errorCallback );
  }

  private void validateScheduleOwner() {
    String owner = scheduleOwnerTextBox.getText();
    if (StringUtils.isEmpty( owner ) ) {
      scheduleOwnerErrorLabel.setVisible( false );
      updateButtonState();
      return;
    }

    String currentUserUrl = EnvironmentHelper.getFullyQualifiedURL() + "api/userrolelist/getRolesForUser?user=" + owner;
    RequestBuilder builder = new RequestBuilder( RequestBuilder.GET, currentUserUrl );

    try {
      RequestCallback requestCallback = new RequestCallback() {
        @Override
        public void onResponseReceived( final Request request, final Response response ) {
          if ( response.getStatusCode() == 200 ) {
            // all users have at least one role (Authenticated or Anonymous),
            // so if no roles are returned, the user doesn't exist.
            boolean userExists = StringUtils.countMatches( response.getText(), "<role>" ) > 0;
            if ( !userExists ) {
              scheduleOwnerErrorLabel.setText( Messages.getString( "schedule.invalidOwner" ) );
            }

            scheduleOwnerErrorLabel.setVisible( !userExists );
            updateButtonState();
          }
        }

        @Override
        public void onError( Request request, Throwable exception ) {
          scheduleOwnerErrorLabel.setVisible( true );
          updateButtonState();
        }
      };
      builder.sendRequest( "", requestCallback );

    } catch ( RequestException e ) {
      scheduleOwnerErrorLabel.setVisible( true );
      updateButtonState();
    }
  }

  /**
   * Refresh Appended Timestamp
   *
   * Refresh the New Schedule UI to update multiple components that change based on whether the timestamp is appended
   * to the schedule name.
   *
   * @param value - true if the timestamp should be appended, otherwise false
   */
  private void refreshAppendedTimestamp( boolean value ) {
    previewCaptionPanel.setVisible( value );
    timestampLB.setVisible( value );
    if ( value ) {
      overrideExistingChk.setText( Messages.getString( "overrideExistingFileAndTime" ) ); //$NON-NLS-1$

      //Update the preview text
      scheduleNamePreviewLabel.setText( getPreviewName( timestampLB.getSelectedIndex() ) );
    } else {
      overrideExistingChk.setText( Messages.getString( "overrideExistingFile" ) ); //$NON-NLS-1$
    }
  }
}
