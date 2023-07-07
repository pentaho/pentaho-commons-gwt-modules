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

import org.pentaho.gwt.widgets.client.dialogs.IDialogCallback;
import org.pentaho.gwt.widgets.client.dialogs.MessageDialogBox;
import org.pentaho.gwt.widgets.client.wizards.AbstractWizardDialog;
import org.pentaho.gwt.widgets.client.wizards.IWizardPanel;
import org.pentaho.mantle.client.messages.Messages;
import org.pentaho.mantle.client.workspace.JsJob;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

public class ScheduleEmailDialog extends AbstractWizardDialog {

  IDialogCallback callback;

  ScheduleEmailWizardPanel scheduleEmailWizardPanel;
  AbstractWizardDialog parentDialog;
  String filePath;
  JSONObject jobSchedule;

  JSONArray scheduleParams;
  JsJob editJob;

  Boolean done = false;

  private boolean newSchedule = true;

  public ScheduleEmailDialog( AbstractWizardDialog parentDialog, String filePath, JSONObject jobSchedule,
      JSONArray scheduleParams, JsJob editJob ) {
    super( ScheduleDialogType.SCHEDULER, Messages.getString( editJob == null ? "newSchedule" : "editSchedule" ),
      null, false, true );

    this.parentDialog = parentDialog;
    this.filePath = filePath;
    this.jobSchedule = jobSchedule;
    this.scheduleParams = scheduleParams;
    this.editJob = editJob;

    initDialog();
  }

  public void initDialog() {
    scheduleEmailWizardPanel = new ScheduleEmailWizardPanel( filePath, jobSchedule, editJob, scheduleParams );
    IWizardPanel[] wizardPanels = { scheduleEmailWizardPanel };
    this.setWizardPanels( wizardPanels );
    setPixelSize( 635, 375 );
    wizardDeckPanel.setHeight( "100%" );
    setSize( "650px", "450px" );
    addStyleName( "schedule-email-dialog" );
    setResponsive( true );
    setSizingMode( DialogSizingMode.FILL_VIEWPORT );
    setWidthCategory( DialogWidthCategory.SMALL );
  }

  public boolean onKeyDownPreview( char key, int modifiers ) {
    if ( key == KeyCodes.KEY_ESCAPE ) {
      hide();
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.pentaho.gwt.widgets.client.wizards.AbstractWizardDialog#finish()
   */
  @Override
  protected boolean onFinish() {
    final JSONObject scheduleRequest = (JSONObject) JSONParser.parseStrict( jobSchedule.toString() );
    JsArray<JsSchedulingParameter> emailParams = scheduleEmailWizardPanel.getEmailParams();

    if ( scheduleParams == null ) {
      scheduleParams = new JSONArray();
    }

    if ( emailParams != null ) {
      int index = scheduleParams.size();
      for ( int i = 0; i < emailParams.length(); i++ ) {
        scheduleParams.set( index++, new JSONObject( emailParams.get( i ) ) );
      }
    }

    if ( editJob != null ) {
      scheduleParams.set( scheduleParams.size(), ScheduleParamsHelper.generateActionUser( editJob ) );

      scheduleParams.set( scheduleParams.size(), ScheduleParamsHelper.generateLineageId( editJob ) );
    }

    scheduleRequest.put( ScheduleParamsHelper.JOB_PARAMETERS_KEY, scheduleParams );

    RequestBuilder scheduleFileRequestBuilder = ScheduleHelper.buildRequestForJob( editJob, scheduleRequest );

    try {
      scheduleFileRequestBuilder.sendRequest( scheduleRequest.toString(), new RequestCallback() {

        @Override
        public void onError( Request request, Throwable exception ) {
          MessageDialogBox dialogBox =
              new MessageDialogBox( Messages.getString( "error" ), exception.toString(), false, false, true );
          dialogBox.center();
          setDone( false );
        }

        @Override
        public void onResponseReceived( Request request, Response response ) {
          if ( response.getStatusCode() == 200 ) {
            setDone( true );
            ScheduleEmailDialog.this.hide();
            if ( callback != null ) {
              callback.okPressed();
            }
          } else {
            String errorMessage = Messages.getString( "serverErrorColon" ) + " " + response.getStatusCode();

            MessageDialogBox dialogBox = new MessageDialogBox( Messages.getString( "error" ), errorMessage,
              false, false, true );
            dialogBox.center();
            setDone( false );
          }
        }
      } );
    } catch ( RequestException e ) {
      MessageDialogBox dialogBox = new MessageDialogBox( Messages.getString( "error" ), e.toString(),
          false, false, true );
      dialogBox.center();
      setDone( false );
    }
    setDone( true );
    return true;
  }

  public Boolean getDone() {
    return done;
  }

  public void setDone( Boolean done ) {
    this.done = done;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.pentaho.gwt.widgets.client.wizards.AbstractWizardDialog#onNext(org.pentaho.gwt.widgets.client.wizards.
   * IWizardPanel, org.pentaho.gwt.widgets.client.wizards.IWizardPanel)
   */
  @Override
  protected boolean onNext( IWizardPanel nextPanel, IWizardPanel previousPanel ) {
    // TODO Auto-generated method stub
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.pentaho.gwt.widgets.client.wizards.AbstractWizardDialog#onPrevious(org.pentaho.gwt.widgets.client.wizards
   * .IWizardPanel, org.pentaho.gwt.widgets.client.wizards.IWizardPanel)
   */
  @Override
  protected void backClicked() {
    parentDialog.center();
    hide();
  }

  @Override
  public void center() {
    super.center();
    scheduleEmailWizardPanel.setFocus();
  }

  @Override
  protected boolean onPrevious( IWizardPanel previousPanel, IWizardPanel currentPanel ) {
    return true;
  }

  @Override
  protected boolean showBack( int index ) {
    return parentDialog != null;
  }

  @Override
  protected boolean showFinish( int index ) {
    return true;
  }

  @Override
  protected boolean showNext( int index ) {
    return false;
  }

  @Override
  protected boolean enableBack( int index ) {
    return true;
  }

  public void setCallback( IDialogCallback callback ) {
    this.callback = callback;
  }

  public IDialogCallback getCallback() {
    return callback;
  }

  public AbstractWizardDialog getParentDialog() {
    return parentDialog;
  }

  public void setParentDialog( AbstractWizardDialog parentDialog ) {
    this.parentDialog = parentDialog;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath( String filePath ) {
    this.filePath = filePath;
  }

  public JSONObject getJobSchedule() {
    return jobSchedule;
  }

  public void setJobSchedule( JSONObject jobSchedule ) {
    this.jobSchedule = jobSchedule;
  }

  public JSONArray getScheduleParams() {
    return scheduleParams;
  }

  public void setScheduleParams( JSONArray scheduleParams ) {
    this.scheduleParams = scheduleParams;
    this.scheduleEmailWizardPanel.setScheduleParams( scheduleParams );
    this.scheduleEmailWizardPanel.panelWidgetChanged( this );
  }

  public JsJob getEditJob() {
    return editJob;
  }

  public void setEditJob( JsJob editJob ) {
    this.editJob = editJob;
  }

  public void setNewSchedule( boolean newSchedule ) {
    this.newSchedule = newSchedule;
  }
}
