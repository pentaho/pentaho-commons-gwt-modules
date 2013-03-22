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
 * @created May 19, 2008
 * 
 */
package org.pentaho.gwt.widgets.client.controls.schededitor;


import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import org.pentaho.gwt.widgets.client.controls.TimePicker;
import org.pentaho.gwt.widgets.client.controls.schededitor.RecurrenceEditor.TemporalValue;
import org.pentaho.gwt.widgets.client.i18n.WidgetsLocalizedMessages;
import org.pentaho.gwt.widgets.client.i18n.WidgetsLocalizedMessagesSingleton;
import org.pentaho.gwt.widgets.client.ui.ICallback;
import org.pentaho.gwt.widgets.client.ui.IChangeHandler;
import org.pentaho.gwt.widgets.client.utils.CronExpression;
import org.pentaho.gwt.widgets.client.utils.CronParseException;
import org.pentaho.gwt.widgets.client.utils.CronParser;
import org.pentaho.gwt.widgets.client.utils.EnumException;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.pentaho.gwt.widgets.client.utils.TimeUtil;
import org.pentaho.gwt.widgets.client.wizards.AbstractWizardDialog.ScheduleDialogType;

/**
 *
 * @author Steven Barkdull
 *
 */
@SuppressWarnings("deprecation")
public class ScheduleEditor extends VerticalPanel implements IChangeHandler  {

  private static final WidgetsLocalizedMessages MSGS = WidgetsLocalizedMessagesSingleton.getInstance().getMessages();

  private static final String SCHEDULE_LABEL = "schedule-label"; //$NON-NLS-1$
  protected static final String SCHEDULE_EDITOR_CAPTION_PANEL = "schedule-editor-caption-panel"; //$NON-NLS-1$

  public enum ScheduleType {
    RUN_ONCE(0, MSGS.runOnce()),
    SECONDS(1, MSGS.seconds()),
    MINUTES(2, MSGS.minutes()),
    HOURS(3, MSGS.hours()),
    DAILY(4, MSGS.daily()),
    WEEKLY(5, MSGS.weekly()),
    MONTHLY(6, MSGS.monthly()),
    YEARLY(7, MSGS.yearly()),
    CRON(8, MSGS.cron());

    private ScheduleType(int value, String name) {
      this.value = value;
      this.name = name;
    }

    private final int value;

    private final String name;

    private static ScheduleType[] scheduleValue = {RUN_ONCE,
                                                   SECONDS,
                                                   MINUTES,
                                                   HOURS,
                                                   DAILY,
                                                   WEEKLY,
                                                   MONTHLY,
                                                   YEARLY,
                                                   CRON};

    public int value() {
      return value;
    }

    public String toString() {
      return name;
    }

    public static ScheduleType get(int idx) {
      return scheduleValue[idx];
    }

    public static int length() {
      return scheduleValue.length;
    }

    public static ScheduleType stringToScheduleType( String strSchedule ) throws EnumException {
      for (ScheduleType v : EnumSet.range(ScheduleType.RUN_ONCE, ScheduleType.CRON)) {
        if ( v.toString().equals( strSchedule ) ) {
          return v;
        }
      }
      throw new EnumException( MSGS.invalidTemporalValue( scheduleValue.toString() ) );
    }
  } /* end enum */

  private TextBox scheduleNameTextBox = new TextBox();
  private RunOnceEditor runOnceEditor = null;
  private RecurrenceEditor recurrenceEditor = null;
  private CronEditor cronEditor = null;
  // TODO sbarkdull, can this be static?
  private Map<ScheduleType, Panel> scheduleTypeMap = new HashMap<ScheduleType, Panel>();
  private Map<TemporalValue, ScheduleType> temporalValueToScheduleTypeMap = createTemporalValueToScheduleTypeMap();
  private Map<ScheduleType, TemporalValue> scheduleTypeToTemporalValueMap = createScheduleTypeMapToTemporalValue();

  private ListBox scheduleCombo = null;


  private ICallback<IChangeHandler> onChangeHandler = null;

  private boolean isBlockoutDialog = false;
  private TimePicker startTimePicker = null;
  private Widget startTimePanel = null;
  protected Button blockoutCheckButton = new Button("View Blockout Times");     //$NON-NLS-1$      // TODO: put in message bundle


  public ScheduleEditor(ScheduleDialogType type) {
    super();

    isBlockoutDialog = (type == ScheduleDialogType.BLOCKOUT);
    startTimePicker = new TimePicker();

    setStylePrimaryName( "scheduleEditor" ); //$NON-NLS-1$

    if (isBlockoutDialog == false)
    {
      Label scheduleNameLabel = new Label("Schedule Name:");   //$NON-NLS-1$
      scheduleNameLabel.setStyleName(SCHEDULE_LABEL);
      add( scheduleNameLabel );
      add(scheduleNameTextBox);
    }

    scheduleCombo = createScheduleCombo();
    Label l = new Label( MSGS.recurrenceColon() );
    l.setStyleName(SCHEDULE_LABEL);
    add( l );
    add( scheduleCombo );

    if (isBlockoutDialog == false)
    {
      startTimePanel = createStartTimePanel();
      add(startTimePanel);
    }

    SimplePanel hspacer = new SimplePanel();
    hspacer.setWidth("100px");

    if (isBlockoutDialog)
    {
      HorizontalPanel blockoutPeriodPanel = new HorizontalPanel();
      blockoutPeriodPanel.add(hspacer);

      // Blockout period
      CaptionPanel blockoutPeriodStartCaptionPanel = new CaptionPanel(MSGS.startTime());
      blockoutPeriodStartCaptionPanel.add(getStartTimePicker());

      CaptionPanel blockoutPeriodEndCaptionPanel = new CaptionPanel(MSGS.endTime());

      TimePicker endTimePicker = new TimePicker();
      endTimePicker.setHour( "12" ); //$NON-NLS-1$
      endTimePicker.setMinute( "00" ); //$NON-NLS-1$
      endTimePicker.setTimeOfDay( TimeUtil.TimeOfDay.AM );

      blockoutPeriodEndCaptionPanel.add(endTimePicker);

      blockoutPeriodPanel.add(blockoutPeriodStartCaptionPanel);
      blockoutPeriodPanel.add(hspacer);
      blockoutPeriodPanel.add(blockoutPeriodEndCaptionPanel);
      blockoutPeriodPanel.add(hspacer);
      add(hspacer);
      add(blockoutPeriodPanel);
    }

    VerticalPanel vp = new VerticalPanel();
    vp.setWidth("100%"); //$NON-NLS-1$
    add( vp );
    setCellHeight( vp, "100%" ); //$NON-NLS-1$

    runOnceEditor = new RunOnceEditor();
    vp.add( runOnceEditor );
    scheduleTypeMap.put( ScheduleType.RUN_ONCE, runOnceEditor );
    runOnceEditor.setVisible( true );

    recurrenceEditor = new RecurrenceEditor();
    vp.add( recurrenceEditor );
    scheduleTypeMap.put( ScheduleType.SECONDS, recurrenceEditor );
    scheduleTypeMap.put( ScheduleType.MINUTES, recurrenceEditor );
    scheduleTypeMap.put( ScheduleType.HOURS, recurrenceEditor );
    scheduleTypeMap.put( ScheduleType.DAILY, recurrenceEditor );
    scheduleTypeMap.put( ScheduleType.WEEKLY, recurrenceEditor );
    scheduleTypeMap.put( ScheduleType.MONTHLY, recurrenceEditor );
    scheduleTypeMap.put( ScheduleType.YEARLY, recurrenceEditor );
    recurrenceEditor.setVisible( false );

    // TODO - should we even create cron editor if blockout????
    cronEditor = new CronEditor();
    scheduleTypeMap.put( ScheduleType.CRON, cronEditor );
    cronEditor.setVisible( false );

    if (isBlockoutDialog == false)
    {
      vp.add( cronEditor );

      VerticalPanel blockoutButtonPanel = new VerticalPanel();
      blockoutButtonPanel.setWidth("100%"); //$NON-NLS-1$
      //blockoutButtonPanel.setHeight("30%");
      blockoutButtonPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
      blockoutButtonPanel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);

      // We want to add a button to check for blockout conflicts
      blockoutCheckButton.setStyleName("pentaho-button");
      blockoutCheckButton.getElement().setId("blockout-check-button");
      blockoutCheckButton.addClickListener(new ClickListener() {
        public void onClick(Widget sender) {
          // TODO: Check if there is a conflict of the current schedule with the list of existing blockout periods
          System.out.println("********** Display a list of blockout periods");
        }
      });

      hspacer.setHeight("50px");
      blockoutButtonPanel.add(hspacer);
      blockoutButtonPanel.add(blockoutCheckButton);

      vp.add(hspacer);
      add(blockoutButtonPanel);
    }

    configureOnChangeHandler();
  }


  public TimePicker getStartTimePicker()
  {
    return startTimePicker;
  }


  protected Widget createStartTimePanel() {
    CaptionPanel startTimeGB = new CaptionPanel( MSGS.startTime() );
    startTimeGB.setStyleName(SCHEDULE_EDITOR_CAPTION_PANEL);

    startTimeGB.add(getStartTimePicker());

    return startTimeGB;
  }

  public void reset( Date now ) {
    runOnceEditor.reset( now );
    recurrenceEditor.reset( now );
    cronEditor.reset( now );

    setScheduleType( ScheduleType.RUN_ONCE );
  }

  public String getScheduleName() {
    return scheduleNameTextBox.getText();
  }

  public void setScheduleName(String scheduleName) {
    scheduleNameTextBox.setText(scheduleName);
  }

  public String getCronString() {
    switch ( getScheduleType() ) {
      case RUN_ONCE:
        return null;
      case SECONDS: // fall through
      case MINUTES: // fall through
      case HOURS: // fall through
      case DAILY: // fall through
      case WEEKLY: // fall through
      case MONTHLY: // fall through
      case YEARLY:
        return recurrenceEditor.getCronString();
      case CRON:
        return cronEditor.getCronString();
      default:
        throw new RuntimeException( MSGS.invalidRunType( getScheduleType().toString() ) );
    }
  }
  /**
   *
   * @param cronStr
   * @throws CronParseException if cronStr is not a valid CRON string.
   */
  public void setCronString( String cronStr ) throws CronParseException {

    // Try original simplistic parser...
    CronParser cp = new CronParser( cronStr );
    String recurrenceStr = null;
    try {
      recurrenceStr = cp.parseToRecurrenceString(); // throws CronParseException
    } catch( CronParseException e ) {
      if ( !CronExpression.isValidExpression( cronStr ) ) { // Parse with proper expression parser
        throw e;
      }
      recurrenceStr = null; // valid cronstring, not parse-able to recurrence string
    }

    if ( null != recurrenceStr ) {
      recurrenceEditor.inititalizeWithRecurrenceString( recurrenceStr );
      TemporalValue tv = recurrenceEditor.getTemporalState();
      ScheduleType rt = temporalValueToScheduleType( tv );
      setScheduleType( rt );
    } else {
      // its a cron string that cannot be parsed into a recurrence string, switch to cron string editor.
      setScheduleType( ScheduleType.CRON );
    }

    cronEditor.setCronString( cronStr );
  }


  /**
   *
   * @return null if the selected schedule does not support repeat-in-seconds, otherwise
   * return the number of seconds between schedule execution.
   * @throws RuntimeException if the temporal value is invalid. This
   * condition occurs as a result of programmer error.
   */
  public Long getRepeatInSecs() throws RuntimeException {
    return recurrenceEditor.getRepeatInSecs();
  }

  public void setRepeatInSecs( Integer repeatInSecs ) {
    recurrenceEditor.inititalizeWithRepeatInSecs( repeatInSecs );
    TemporalValue tv = recurrenceEditor.getTemporalState();
    ScheduleType rt = temporalValueToScheduleType( tv );
    setScheduleType( rt );
  }

  private ListBox createScheduleCombo() {
    final ScheduleEditor localThis = this;
    ListBox lb = new ListBox();
    lb.setVisibleItemCount( 1 );
    //lb.setStyleName("scheduleCombo"); //$NON-NLS-1$
    lb.addChangeListener( new ChangeListener() {
      public void onChange(Widget sender) {
        localThis.handleScheduleChange();
      }
    });

    // add all schedule types to the combobox
    for (ScheduleType schedType : EnumSet.range(ScheduleType.RUN_ONCE, ScheduleType.CRON)) {
      if (((isBlockoutDialog == false) || ((isBlockoutDialog)) &&
          ((schedType != ScheduleType.CRON) && (schedType != ScheduleType.SECONDS) && (schedType != ScheduleType.MINUTES))))
      {
        lb.addItem( schedType.toString() );
      }
    }
    lb.setItemSelected( 0, true );

    return lb;
  }

  public ScheduleType getScheduleType() {
    String selectedValue = scheduleCombo.getValue( scheduleCombo.getSelectedIndex() );
    return ScheduleType.stringToScheduleType( selectedValue );
  }

  public void setScheduleType( ScheduleType scheduleType ) {
    scheduleCombo.setSelectedIndex( scheduleType.value() );
    selectScheduleTypeEditor( scheduleType );
  }

  /**
   * NOTE: should only ever be used by validators. This is a backdoor
   * into this class that shouldn't be here, do not use this method
   * unless you are validating.
   *
   * @return DateRangeEditor
   */
  public RecurrenceEditor getRecurrenceEditor() {
    return recurrenceEditor;
  }

  /**
   * NOTE: should only ever be used by validators. This is a backdoor
   * into this class that shouldn't be here, do not use this method
   * unless you are validating.
   *
   * @return DateRangeEditor
   */
  public CronEditor getCronEditor() {
    return cronEditor;
  }

  /**
   * NOTE: should only ever be used by validators. This is a backdoor
   * into this class that shouldn't be here, do not use this method
   * unless you are validating.
   *
   * @return DateRangeEditor
   */

  public RunOnceEditor getRunOnceEditor() {
    return runOnceEditor;
  }

  public void setStartTime( String startTime ) {
    runOnceEditor.setStartTime( startTime );
    recurrenceEditor.setStartTime( startTime );
  }

  public String getStartTime() {
    switch ( getScheduleType() ) {
      case RUN_ONCE:
        return runOnceEditor.getStartTime();
      case SECONDS: // fall through
      case MINUTES: // fall through
      case HOURS: // fall through
      case DAILY: // fall through
      case WEEKLY: // fall through
      case MONTHLY: // fall through
      case YEARLY:
        return recurrenceEditor.getStartTime();
      case CRON:
        return cronEditor.getStartTime();
      default:
        throw new RuntimeException( MSGS.invalidRunType( getScheduleType().toString() ) );
    }
  }

  public void setStartDate( Date startDate ) {
    runOnceEditor.setStartDate( startDate );
    recurrenceEditor.setStartDate( startDate );
    cronEditor.setStartDate( startDate );
  }

  public Date getStartDate() {
    switch ( getScheduleType() ) {
      case RUN_ONCE:
        Date startDate = runOnceEditor.getStartDate();
        String startTime  = runOnceEditor.getStartTime();
        String[] times = startTime.split(":");
        int hour = Integer.parseInt(times[0]);
        int minute = Integer.parseInt(times[1]);
        if (startTime.indexOf("PM") >= 0) {
          hour += 12;
        }

        startDate.setHours(hour);
        startDate.setMinutes(minute);
        startDate.setSeconds(0);
        return startDate;
      case SECONDS: // fall through
      case MINUTES: // fall through
      case HOURS: // fall through
      case DAILY: // fall through
      case WEEKLY: // fall through
      case MONTHLY: // fall through
      case YEARLY:
        return recurrenceEditor.getStartDate();
      case CRON:
        return cronEditor.getStartDate();
      default:
        throw new RuntimeException( MSGS.invalidRunType( getScheduleType().toString() ) );
    }
  }

  public void setEndDate( Date endDate ) {
    recurrenceEditor.setEndDate( endDate );
    cronEditor.setEndDate( endDate );
  }

  public Date getEndDate() {
    switch ( getScheduleType() ) {
      case RUN_ONCE:
        return null;
      case SECONDS: // fall through
      case MINUTES: // fall through
      case HOURS: // fall through
      case DAILY: // fall through
      case WEEKLY: // fall through
      case MONTHLY: // fall through
      case YEARLY:
        return recurrenceEditor.getEndDate();
      case CRON:
        return cronEditor.getEndDate();
      default:
        throw new RuntimeException( MSGS.invalidRunType( getScheduleType().toString() ) );
    }
  }

  public void setNoEndDate() {
    recurrenceEditor.setNoEndDate();
    cronEditor.setNoEndDate();
  }

  public void setEndBy() {
  	cronEditor.setEndBy();
    recurrenceEditor.setEndBy();
  }

  private void handleScheduleChange() throws EnumException {
    ScheduleType schedType = getScheduleType();
    selectScheduleTypeEditor( schedType );
  }


  private void selectScheduleTypeEditor( ScheduleType scheduleType ) {
    // if we are switching to cron type, then hide the start time panel
    if ((isBlockoutDialog == false) && (startTimePanel != null))
    {
      if (scheduleType == ScheduleType.CRON) {
        startTimePanel.setVisible(false);
      } else {
        startTimePanel.setVisible(true);
      }
    }

    // hide all panels
    for ( Map.Entry<ScheduleType, Panel> me : scheduleTypeMap.entrySet() ) {
      me.getValue().setVisible( false );
    }
    // show the selected panel
    Panel p = scheduleTypeMap.get( scheduleType );
    p.setVisible( true );

    TemporalValue tv = scheduleTypeToTemporalValue( scheduleType );
    if ( null != tv ) {
      // force the recurrence editor to display the appropriate ui
      recurrenceEditor.setTemporalState( tv );
    }
  }

  private static Map<TemporalValue, ScheduleType> createTemporalValueToScheduleTypeMap() {
    Map<TemporalValue, ScheduleType> m = new HashMap<TemporalValue, ScheduleType>();

    m.put( TemporalValue.SECONDS, ScheduleType.SECONDS );
    m.put( TemporalValue.MINUTES, ScheduleType.MINUTES );
    m.put( TemporalValue.HOURS, ScheduleType.HOURS );
    m.put( TemporalValue.DAILY, ScheduleType.DAILY );
    m.put( TemporalValue.WEEKLY, ScheduleType.WEEKLY );
    m.put( TemporalValue.MONTHLY, ScheduleType.MONTHLY );
    m.put( TemporalValue.YEARLY, ScheduleType.YEARLY );

    return m;
  }

  private static Map<ScheduleType, TemporalValue> createScheduleTypeMapToTemporalValue() {
    Map<ScheduleType, TemporalValue> m = new HashMap<ScheduleType, TemporalValue>();

    m.put( ScheduleType.SECONDS, TemporalValue.SECONDS );
    m.put( ScheduleType.MINUTES, TemporalValue.MINUTES );
    m.put( ScheduleType.HOURS, TemporalValue.HOURS );
    m.put( ScheduleType.DAILY, TemporalValue.DAILY );
    m.put( ScheduleType.WEEKLY, TemporalValue.WEEKLY );
    m.put( ScheduleType.MONTHLY, TemporalValue.MONTHLY );
    m.put( ScheduleType.YEARLY, TemporalValue.YEARLY );

    return m;
  }

  private ScheduleType temporalValueToScheduleType( TemporalValue tv ) {
    return temporalValueToScheduleTypeMap.get( tv );
  }

  private TemporalValue scheduleTypeToTemporalValue( ScheduleType st ) {
    return scheduleTypeToTemporalValueMap.get( st );
  }

  public void setFocus() {
    scheduleNameTextBox.setFocus( true );
  }

  public void setOnChangeHandler( ICallback<IChangeHandler> handler ) {
    this.onChangeHandler = handler;
  }

  protected void changeHandler() {
    if ( null != onChangeHandler ) {
      onChangeHandler.onHandle(this);
    }
  }

  private void configureOnChangeHandler() {
    final ScheduleEditor localThis = this;
    ChangeListener changeListener = new ChangeListener() {
      public void onChange(Widget sender) {
        localThis.changeHandler();
      }
    };

    ICallback<IChangeHandler> handler = new ICallback<IChangeHandler>() {
      public void onHandle(IChangeHandler o) {
        localThis.changeHandler();
      }
    };
    scheduleCombo.addChangeListener( changeListener );
    runOnceEditor.setOnChangeHandler( handler );
    recurrenceEditor.setOnChangeHandler( handler );
    cronEditor.setOnChangeHandler( handler );
    scheduleNameTextBox.addChangeListener(changeListener);
  }
}
