/*
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
 * Copyright 2008 Pentaho Corporation.  All rights reserved.
 *
 * @created Aug 8, 2008 
 * @author wseyler
 */
package org.pentaho.gwt.widgets.client.wizards.panels;

import java.util.Date;
import java.util.HashMap;

import org.pentaho.gwt.widgets.client.controls.schededitor.ScheduleEditor;
import org.pentaho.gwt.widgets.client.controls.schededitor.ScheduleEditor.ScheduleType;
import org.pentaho.gwt.widgets.client.i18n.WidgetsLocalizedMessages;
import org.pentaho.gwt.widgets.client.i18n.WidgetsLocalizedMessagesSingleton;
import org.pentaho.gwt.widgets.client.ui.ICallback;
import org.pentaho.gwt.widgets.client.ui.IChangeHandler;
import org.pentaho.gwt.widgets.client.wizards.AbstractWizardPanel;
import org.pentaho.gwt.widgets.client.wizards.panels.validators.ScheduleEditorValidator;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author wseyler
 *
 */
public class ScheduleEditorWizardPanel extends AbstractWizardPanel {

  private static final WidgetsLocalizedMessages MSGS = WidgetsLocalizedMessagesSingleton.getInstance().getMessages();
  
  private static final String PENTAHO_SCHEDULE = "pentaho-schedule-create"; //$NON-NLS-1$
  
  ScheduleEditor scheduleEditor = new ScheduleEditor();
  ScheduleEditorValidator scheduleEditorValidator;
  boolean parametersComplete = true;
  CaptionPanel parametersCaptionPanel = new CaptionPanel(MSGS.parameters());
  Frame parametersFrame;
  String scheduledFilePath;
  
  
  public ScheduleEditorWizardPanel(String scheduledFile) {
    super();
    scheduledFilePath = scheduledFile;
    scheduleEditorValidator = new ScheduleEditorValidator(scheduleEditor);
    init();
    layout();
    ScheduleEditorWizardPanel thisInstance = this;
    registerSchedulingCallbacks(thisInstance);
  }

  /**
   * 
   */
  private void init() {
    ICallback<IChangeHandler> chHandler = new ICallback<IChangeHandler>() {
      public void onHandle(IChangeHandler se ) {
        panelWidgetChanged(ScheduleEditorWizardPanel.this);
      }
    };
    scheduleEditor.setOnChangeHandler( chHandler );
  }
  
  public native JsArray<JsSchedulingParameter> getParams() /*-{
    var params = $doc.getElementById('schedulerParamsFrame').contentWindow.getParams();
    var paramEntries = new Array();
    for (var key in params) {
      var type = null;
      var value = null;
      if (typeof params[key] == 'number') {
        type = "number";
        value = '' + params[key];
      } else if (typeof params[key] == 'boolean') {
        type = "boolean";
        value = (params[key] ? "true" : "false");
      } else if (params[key] instanceof Date) {
        type = "date";
        value DateTimeFormat.getFormat(PredefinedFormat.ISO_8601).format(params[key]);
      } else if (params[key] instanceof String) {
        type = "string";
        value = params[key];
      }
      if (type != null) {
        paramEntries.push({
          name: key,
          value: params[key],
          type: type
        });
      }
    }
    return paramEntries;
  }-*/;
  
  public native void schedulerParamsLoadedCallback(String filePath) /*-{
    $doc.getElementById('schedulerParamsFrame').contentWindow.initSchedulingParams(filePath, $wnd.schedulerParamsCompleteCallback);
  }-*/;
  
  public void schedulerParamsCompleteCallback(boolean complete) {
    parametersComplete = complete;
    setCanContinue(scheduleEditorValidator.isValid() && complete);
    setCanFinish(scheduleEditorValidator.isValid() && complete);
  }
  
  private native void registerSchedulingCallbacks(ScheduleEditorWizardPanel thisInstance)/*-{
    $wnd.schedulerParamsLoadedCallback = function(filePath) {thisInstance.@org.pentaho.gwt.widgets.client.wizards.panels.ScheduleEditorWizardPanel::schedulerParamsLoadedCallback(Ljava/lang/String;)(filePath)};
    $wnd.schedulerParamsCompleteCallback = function(flag) {thisInstance.@org.pentaho.gwt.widgets.client.wizards.panels.ScheduleEditorWizardPanel::schedulerParamsCompleteCallback(Z)(flag)};
  }-*/;
  
  /**
   * 
   */
  private void layout() {
    this.addStyleName(PENTAHO_SCHEDULE);
    this.add(scheduleEditor, WEST);
    this.add(parametersCaptionPanel, CENTER);
    panelWidgetChanged(null);
  }

  /* (non-Javadoc)
   * @see org.pentaho.gwt.widgets.client.wizards.IWizardPanel#getName()
   */
  public String getName() {
    // TODO Auto-generated method stub
    return MSGS.scheduleEdit();
  }

  protected void panelWidgetChanged(Widget changedWidget) {
//    System.out.println("Widget Changed: " + changedWidget + " can continue: " + scheduleEditorValidator.isValid());
    setCanContinue(scheduleEditorValidator.isValid() && parametersComplete);
    setCanFinish(scheduleEditorValidator.isValid() && parametersComplete);
  }
  
  public ScheduleType getScheduleType() {
    return scheduleEditor.getScheduleType();
  }
  
  public ScheduleEditor getScheduleEditor() {
    return scheduleEditor;
  }
  
  /**
   * @return
   */
  public String getCronString() {
    return scheduleEditor.getCronString();
  }
  
  public Date getStartDate() {
    return scheduleEditor.getStartDate();
  }
  
  public String getStartTime() {
    return scheduleEditor.getStartTime();
  }
  
  public Date getEndDate() {
    return scheduleEditor.getEndDate();
  }
  
  public int getRepeatCount() {
    // Repeate forever
    return -1;
  }
  public String getRepeatInterval() {
    return scheduleEditor.getRepeatInSecs().toString();
  }

  public void setFocus() {
    scheduleEditor.setFocus();
  }
  
  public void setParametersUrl(String url) {
    if (url == null) {
      if (parametersFrame != null) {
        parametersCaptionPanel.remove(parametersFrame);
        parametersFrame = null;
      }
    } else {
      if (parametersFrame == null) {
        parametersFrame = new Frame();
        parametersCaptionPanel.add(parametersFrame);
        DOM.setElementAttribute(parametersFrame.getElement(), "onload", "schedulerParamsLoadedCallback('" + scheduledFilePath + "')");//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        DOM.setElementAttribute(parametersFrame.getElement(), "id", "schedulerParamsFrame"); //$NON-NLS-1$ //$NON-NLS-2$
        parametersFrame.setUrl(url);
      } else if (!url.equals(parametersFrame.getUrl())) {
        parametersFrame.setUrl(url);
      }
    }
  }
}
