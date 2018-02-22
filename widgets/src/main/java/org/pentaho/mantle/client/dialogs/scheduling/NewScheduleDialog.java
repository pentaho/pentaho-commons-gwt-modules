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
 * Copyright (c) 2002-2018 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.mantle.client.dialogs.scheduling;

import java.util.Date;

import org.pentaho.gwt.widgets.client.dialogs.IDialogCallback;
import org.pentaho.gwt.widgets.client.dialogs.MessageDialogBox;
import org.pentaho.gwt.widgets.client.dialogs.PromptDialogBox;
import org.pentaho.gwt.widgets.client.formatter.JSDateTextFormatter;
import org.pentaho.gwt.widgets.client.utils.NameUtils;
import org.pentaho.gwt.widgets.client.utils.string.StringUtils;
import org.pentaho.gwt.widgets.client.wizards.AbstractWizardDialog.ScheduleDialogType;
import org.pentaho.mantle.client.dialogs.WaitPopup;
import org.pentaho.mantle.client.dialogs.folderchooser.SelectFolderDialog;
import org.pentaho.mantle.client.messages.Messages;
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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class NewScheduleDialog extends PromptDialogBox {

  private String filePath;
  private IDialogCallback callback;
  private boolean isEmailConfValid;
  private JsJob jsJob;

  private ScheduleRecurrenceDialog recurrenceDialog = null;

  private TextBox scheduleNameTextBox = new TextBox();
  private static TextBox scheduleLocationTextBox = new TextBox();
  private CheckBox appendTimeChk = new CheckBox();
  private ListBox timestampLB = new ListBox();
  private CaptionPanel previewCaptionPanel;
  private Label scheduleNamePreviewLabel;
  private CheckBox overrideExistingChk = new CheckBox();
  private static HandlerRegistration changeHandlerReg = null;
  private static HandlerRegistration keyHandlerReg = null;
  private Label runOptionsLabel;
  private CheckBox useWorkerNodesChk = new CheckBox();

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
    super(
        Messages.getString( "newSchedule" ), Messages.getString( "next" ), Messages.getString( "cancel" ), false, true ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    this.jsJob = jsJob;
    this.filePath = jsJob.getFullResourceName();
    this.callback = callback;
    this.isEmailConfValid = isEmailConfValid;
    createUI();
  }

  public NewScheduleDialog( String filePath, IDialogCallback callback, boolean isEmailConfValid ) {

    super(
        Messages.getString( "newSchedule" ), Messages.getString( "next" ), Messages.getString( "cancel" ), false, true ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    this.filePath = filePath;
    this.callback = callback;
    this.isEmailConfValid = isEmailConfValid;
    createUI();
  }

  private void createUI() {
    VerticalPanel content = new VerticalPanel();

    HorizontalPanel scheduleNameLabelPanel = new HorizontalPanel();
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

    HorizontalPanel scheduleNamePanel = new HorizontalPanel();
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
    HorizontalPanel locationPanel = new HorizontalPanel();
    scheduleLocationTextBox.setEnabled( false );
    locationPanel.add( scheduleLocationTextBox );
    locationPanel.setCellVerticalAlignment( scheduleLocationTextBox, HasVerticalAlignment.ALIGN_MIDDLE );
    locationPanel.add( browseButton );

    content.add( locationPanel );
    content.add( overrideExistingChk );

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

    runOptionsLabel = new Label( Messages.getString( "runOptions" ) );
    runOptionsLabel.setStyleName( ScheduleEditor.SECTION_DIVIDER_TITLE_LABEL );
    useWorkerNodesChk.setText( Messages.getString( "useWorkerNodes" ) ); //$NON-NLS-1$
    useWorkerNodesChk.setValue( ScheduleHelper.DEFAULT_DISTRIBUTE_LOAD_VIA_WORKER_NODES_SETTING );
    ScheduleHelper.showOptionToDistributeLoadViaWorkerNodes( runOptionsLabel, useWorkerNodesChk, filePath );
    content.add( runOptionsLabel );
    content.add( useWorkerNodesChk );

    setContent( content );
    content.getElement().getParentElement().addClassName( "schedule-dialog-content" );
    content.getElement().getParentElement().addClassName( "schedule-dialog-content-crystal" );
    content.getElement().getParentElement().addClassName( "schedule-dialog-content-onyx" );
    content.getElement().getParentElement().removeClassName( "dialog-content" );
    content.getElement().getParentElement().getStyle().clearPadding();
    content.getElement().getStyle().clearHeight();
    content.getParent().setHeight( "100%" );
    content.getElement().getParentElement().getStyle().setVerticalAlign( VerticalAlign.TOP );

    okButton.getParent().getParent().setStyleName( "button-panel" );

    updateButtonState();
    setSize( "650px", "450px" );

    validateScheduleLocationTextBox();
    addStyleName( "new-schedule-dialog" );
  }

  protected void onOk() {
    String name = scheduleNameTextBox.getText();
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
      scheduleFileRequestBuilder = new RequestBuilder( RequestBuilder.GET, ScheduleHelper.getFullyQualifiedURL() + "api/repos/" + urlPath
          + "/parameterUi" );
    } else {
      isXAction = false;
      scheduleFileRequestBuilder = new RequestBuilder( RequestBuilder.GET, ScheduleHelper.getFullyQualifiedURL() + "api/repo/files/" + urlPath
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

              if ( useWorkerNodesChk != null && useWorkerNodesChk.isVisible() ) {
                JsJobParam jjp = (JsJobParam) JavaScriptObject.createObject().cast();
                jjp.setName( "useWorkerNodes" );
                jjp.setValue( String.valueOf( useWorkerNodesChk.getValue().booleanValue() ) );
                jsJob.getJobParams().set( jsJob.getJobParams().length(), jjp );
              }

              if ( recurrenceDialog == null ) {
                recurrenceDialog =
                    new ScheduleRecurrenceDialog( NewScheduleDialog.this, jsJob, callback, hasParams, isEmailConfValid,
                        ScheduleDialogType.SCHEDULER );
              }
            } else if ( recurrenceDialog == null ) {

              String useWorkerNodes = useWorkerNodesChk != null && useWorkerNodesChk.isVisible()
                      ? String.valueOf( useWorkerNodesChk.getValue().booleanValue() ) : "";

              recurrenceDialog =
                  new ScheduleRecurrenceDialog( NewScheduleDialog.this, filePath, scheduleLocationTextBox.getText(),
                      scheduleNameTextBox.getText(), dateFormat, overwriteFile, callback, hasParams, isEmailConfValid, useWorkerNodes );
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
    okButton.setEnabled( hasLocation && hasName );
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
        scheduleLocationTextBox.setText( getDefaultSaveLocation() ); // restore default location
      }
    };
    OutputLocationUtils.validateOutputLocation( scheduleLocationTextBox.getText(), null, errorCallback );
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
