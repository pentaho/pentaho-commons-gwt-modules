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

import com.google.gwt.core.client.JsArray;
import org.pentaho.gwt.widgets.client.dialogs.IDialogCallback;
import org.pentaho.gwt.widgets.client.dialogs.IDialogValidatorCallback;
import org.pentaho.gwt.widgets.client.dialogs.MessageDialogBox;
import org.pentaho.gwt.widgets.client.dialogs.PromptDialogBox;
import org.pentaho.gwt.widgets.client.formatter.JSDateTextFormatter;
import org.pentaho.gwt.widgets.client.utils.NameUtils;
import org.pentaho.gwt.widgets.client.utils.string.StringUtils;
import org.pentaho.mantle.client.dialogs.folderchooser.SelectFolderDialog;
import org.pentaho.mantle.client.messages.Messages;

import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
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

public abstract class ScheduleOutputLocationDialog extends PromptDialogBox {
  private String filePath;

  private TextBox scheduleNameTextBox = new TextBox();
  private Label scheduleNameLabel;
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

  public ScheduleOutputLocationDialog( final String filePath ) {
    super(
        Messages.getString( "runInBackground" ), Messages.getString( "next" ), Messages.getString( "cancel" ), false, true ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    this.filePath = filePath;
    createUI();
    setupCallbacks();
  }

  private void createUI() {
    VerticalPanel content = new VerticalPanel();

    HorizontalPanel scheduleNameLabelPanel = new HorizontalPanel();
    scheduleNameLabel = new Label( Messages.getString( "generatedContentName" ) );
    scheduleNameLabel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_LEFT );

    scheduleNameLabelPanel.add( scheduleNameLabel );

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

    refreshAppendedTimestamp( appendTimeChk.getValue().booleanValue() );

    runOptionsLabel = new Label( Messages.getString( "runOptions" ) );
    runOptionsLabel.setStyleName( ScheduleEditor.SECTION_DIVIDER_TITLE_LABEL );
    useWorkerNodesChk.setText( Messages.getString( "useWorkerNodes" ) ); //$NON-NLS-1$
    useWorkerNodesChk.setValue( ScheduleHelper.DEFAULT_DISTRIBUTE_LOAD_VIA_WORKER_NODES_SETTING );
    ScheduleHelper.showOptionToDistributeLoadViaWorkerNodes( runOptionsLabel, useWorkerNodesChk, filePath );
    content.add( runOptionsLabel );
    content.add( useWorkerNodesChk );

    setContent( content );
    content.getElement().getStyle().clearHeight();
    content.getElement().getParentElement().getStyle().setVerticalAlign( VerticalAlign.TOP );
    content.getParent().setHeight( "100%" );

    okButton.getParent().getParent().setStyleName( "button-panel" );

    updateButtonState();
    setSize( "650px", "450px" );

    validateScheduleLocationTextBox();
  }

  private void setupCallbacks() {
    setValidatorCallback( new IDialogValidatorCallback() {
      @Override
      public boolean validate() {
        String name = scheduleNameTextBox.getText();
        boolean isValid = NameUtils.isValidFileName( name );
        if ( !isValid ) {
          MessageDialogBox errorDialog =
              new MessageDialogBox(
                  Messages.getString( "error" ), Messages.getString( "prohibitedNameSymbols", name, NameUtils.reservedCharListForDisplay( " " ) ), false, false, true ); //$NON-NLS-1$ //$NON-NLS-2$
          errorDialog.center();
        }
        return isValid;
      }
    } );

    setCallback( new IDialogCallback() {
      @Override
      public void okPressed() {
        boolean overwriteFile = false;
        if ( overrideExistingChk != null ) {
          overwriteFile = overrideExistingChk.getValue();
        }

        String dateFormat = "";
        if ( appendTimeChk != null ) {
          if ( appendTimeChk.getValue().booleanValue() ) {
            dateFormat = timestampLB.getValue( timestampLB.getSelectedIndex() );
          }
        }

        onSelect( scheduleNameTextBox.getText(), scheduleLocationTextBox.getText(),
                ( useWorkerNodesChk != null && useWorkerNodesChk.isVisible()
                        ? String.valueOf( useWorkerNodesChk.getValue().booleanValue() ) : null ), overwriteFile, dateFormat );
      }

      @Override
      public void cancelPressed() {
      }
    } );
  }

  private void updateButtonState() {
    boolean hasLocation = !StringUtils.isEmpty( scheduleLocationTextBox.getText() );
    boolean hasName = !StringUtils.isEmpty( scheduleNameTextBox.getText() );
    okButton.setEnabled( hasLocation && hasName );
  }

  public String getPreviewName( int index ) {
    JSDateTextFormatter formatter = new JSDateTextFormatter( timestampLB.getValue( index ) );
    Date date = new Date();
    return scheduleNameTextBox.getText() + formatter.format( String.valueOf( date.getTime() ) );
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

  protected abstract void onSelect( String name, String outputLocationPath, String useWorkerNodes, boolean overwriteFile, String dateFormat );

  public void setOkButtonText( String text ) {
    okButton.setText( text );
  }

  public void setScheduleNameText( String text ) {
    scheduleNameLabel.setText( text );
  }
}
