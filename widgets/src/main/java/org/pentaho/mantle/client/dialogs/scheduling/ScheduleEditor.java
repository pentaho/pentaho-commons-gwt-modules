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

import org.pentaho.gwt.widgets.client.controls.TimePicker;
import org.pentaho.gwt.widgets.client.ui.ICallback;
import org.pentaho.gwt.widgets.client.ui.IChangeHandler;
import org.pentaho.gwt.widgets.client.utils.CronExpression;
import org.pentaho.gwt.widgets.client.utils.CronParseException;
import org.pentaho.gwt.widgets.client.utils.CronParser;
import org.pentaho.gwt.widgets.client.utils.EnumException;
import org.pentaho.gwt.widgets.client.utils.TimeUtil.TimeOfDay;
import org.pentaho.gwt.widgets.client.wizards.AbstractWizardDialog.ScheduleDialogType;

import org.pentaho.mantle.client.dialogs.scheduling.RecurrenceEditor.TemporalValue;
import org.pentaho.mantle.client.messages.Messages;

import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;

/**
 * @author Steven Barkdull
 */
public class ScheduleEditor extends VerticalPanel implements IChangeHandler {

  private static final int DEFAULT_START_HOUR = 12; //$NON-NLS-1$
  private static final int DEFAULT_START_MINUTE = 0; //$NON-NLS-1$

  public static enum ENDS_TYPE {
    TIME, DURATION
  }

  public static class DurationValues {
    public int days = 0;

    public int hours = 0;

    public int minutes = 0;
  }

  public static enum TIME {
    MILLISECOND( 1 ), SECOND( MILLISECOND.time * 1000 ), MINUTE( SECOND.time * 60 ), HOUR( MINUTE.time * 60 ), DAY(
        HOUR.time * 24 );

    private long time;

    TIME( long time ) {
      this.time = time;
    }

    public long getTime() {
      return time;
    }
  }

  protected static final String SCHEDULE_DROPDOWN_ARROW = "schedule-dropdown-arrow";

  protected static final String RECURRENCE_LABEL = "reccurence-label";

  protected static final String SCHEDULE_LABEL = "schedule-label"; //$NON-NLS-1$

  protected static final String SECTION_DIVIDER_TITLE_LABEL = "section-divider-title"; //$NON-NLS-1$

  protected static final String SCHEDULE_EDITOR_CAPTION_PANEL = "schedule-editor-caption-panel"; //$NON-NLS-1$

  protected static final String BLOCKOUT_SELECT = "blockout-select";

  protected static final String BLOCKOUT_LABEL = "blockout-label";

  public enum ScheduleType {
      //@formatter:off
      RUN_ONCE( 0, Messages.getString( "schedule.runOnce" ) ),
      SECONDS( 1, Messages.getString( "schedule.seconds" ) ),
      MINUTES( 2, Messages.getString( "schedule.minutes" ) ),
      HOURS( 3, Messages.getString( "schedule.hours" ) ),
      DAILY( 4, Messages.getString( "schedule.daily" ) ),
      WEEKLY( 5, Messages.getString( "schedule.weekly" ) ),
      MONTHLY( 6, Messages.getString( "schedule.monthly" ) ),
      YEARLY( 7, Messages.getString( "schedule.yearly" ) ),
      CRON( 8, Messages.getString( "schedule.cron" ) );
      //@formatter:on

    private ScheduleType( int value, String name ) {
      this.value = value;
      this.name = name;
    }

    private final int value;

    private final String name;

    private static ScheduleType[] scheduleValue = { RUN_ONCE, SECONDS, MINUTES, HOURS, DAILY, WEEKLY, MONTHLY, YEARLY,
      CRON };

    public int value() {
      return value;
    }

    @Override
    public String toString() {
      return name;
    }

    public static ScheduleType get( int idx ) {
      return scheduleValue[idx];
    }

    public static int length() {
      return scheduleValue.length;
    }

    public static ScheduleType stringToScheduleType( String strSchedule ) throws EnumException {
      for ( ScheduleType v : EnumSet.range( ScheduleType.RUN_ONCE, ScheduleType.CRON ) ) {
        if ( v.toString().equals( strSchedule ) ) {
          return v;
        }
      }
      throw new EnumException( Messages.getString( "schedule.invalidTemporalValue", scheduleValue.toString() ) );
    }
  } /* end enum */

  protected RunOnceEditor runOnceEditor = null;

  protected RecurrenceEditor recurrenceEditor = null;

  protected CronEditor cronEditor = null;

  // TODO sbarkdull, can this be static?
  private final Map<ScheduleType, Panel> scheduleTypeMap = new HashMap<ScheduleType, Panel>();

  private final Map<TemporalValue, ScheduleType> temporalValueToScheduleTypeMap =
      createTemporalValueToScheduleTypeMap();

  private final Map<ScheduleType, TemporalValue> scheduleTypeToTemporalValueMap =
      createScheduleTypeMapToTemporalValue();

  protected ListBox scheduleCombo = null;

  private ICallback<IChangeHandler> onChangeHandler = null;

  private boolean isBlockoutDialog = false;

  private TimePicker startTimePicker = null;

  protected TimePicker blockoutEndTimePicker = null;

  private Widget startTimePanel = null;

  private RadioButton endTimeRadioButton = null;

  private RadioButton durationRadioButton = null;

  protected ListBox daysListBox = null;

  protected ListBox hoursListBox = null;

  protected ListBox minutesListBox = null;

  protected Button blockoutCheckButton = new Button( Messages.getString( "schedule.viewBlockoutTimes" ) );

  protected ListBox timeZonePicker = null;

  private final Date defaultDate = initDefaultDate();

  private String targetTimezone = null;

  @SuppressWarnings( "deprecation" )
  private Date initDefaultDate() {
    Date date = new Date();
    date.setHours( DEFAULT_START_HOUR );
    date.setMinutes( DEFAULT_START_MINUTE );
    CalendarUtil.addDaysToDate( date, 1 );
    return date;
  }

  public ScheduleEditor( ScheduleDialogType type ) {
    super();
    isBlockoutDialog = ( type == ScheduleDialogType.BLOCKOUT );
    startTimePicker = new TimePicker();
    timeZonePicker = new ListBox();

    setStylePrimaryName( "scheduleEditor" ); //$NON-NLS-1$

    scheduleCombo = createScheduleCombo();
    Label l = new Label( Messages.getString( "schedule.recurrenceColon" ) );
    l.getElement().setId( RECURRENCE_LABEL );
    l.setStyleName( SCHEDULE_LABEL );
    add( l );
    add( scheduleCombo );

    SimplePanel hspacer = new SimplePanel();
    hspacer.setWidth( "100px" ); //$NON-NLS-1$

    if ( !isBlockoutDialog ) {
      startTimePanel = createStartTimePanel();
      add( startTimePanel );
    } else {

      // Blockout End TimePicker
      Date blockoutEndDate = new Date( defaultDate.getTime() );
      addMinutes( defaultDate, 1 );
      blockoutEndTimePicker = new TimePicker();
      blockoutEndTimePicker = new TimePicker();
      blockoutEndTimePicker.setTime( blockoutEndDate );

      // Blockout End Caption Panel
      blockoutEndTimePicker.getElement().getStyle().setDisplay( Display.NONE );

      final String[] daysList = new String[365];
      final String[] hoursList = new String[24];
      final String[] minutesList = new String[60];

      // Populate list
      for ( Integer i = 0; i < 365; i++ ) {
        String iStr = i.toString();
        daysList[i] = iStr;

        if ( i < 60 ) {
          minutesList[i] = iStr;
          if ( i < 24 ) {
            hoursList[i] = iStr;
          }
        }
      }

      // Units of time Drop Down
      daysListBox = new ListBox();
      daysListBox.getElement().setId( "daysListBox" ); //$NON-NLS-1$
      daysListBox.addStyleName( BLOCKOUT_SELECT );
      populateListItems( daysListBox, daysList, 0, 365 );

      final Label daysLabel = new Label( Messages.getString( "schedule.dayOrDays" ) );
      daysLabel.addStyleName( BLOCKOUT_LABEL );
      daysLabel.getElement().setAttribute( "for", daysListBox.getElement().getId() ); //$NON-NLS-1$

      hoursListBox = new ListBox();
      hoursListBox.getElement().setId( "hoursListBox" ); //$NON-NLS-1$
      hoursListBox.addStyleName( BLOCKOUT_SELECT );
      populateListItems( hoursListBox, hoursList, 0, 24 );

      final Label hoursLabel = new Label( Messages.getString( "schedule.hourOrHours" ) );
      hoursLabel.addStyleName( BLOCKOUT_LABEL );
      hoursLabel.getElement().setAttribute( "for", hoursListBox.getElement().getId() ); //$NON-NLS-1$

      minutesListBox = new ListBox();
      minutesListBox.getElement().setId( "minutesListBox" ); //$NON-NLS-1$
      minutesListBox.addStyleName( BLOCKOUT_SELECT );
      populateListItems( minutesListBox, minutesList, 0, 60 );
      minutesListBox.setSelectedIndex( 1 ); // default value for Validator

      final Label minutesLabel = new Label( Messages.getString( "schedule.minuteOrMinutes" ) );
      minutesLabel.addStyleName( BLOCKOUT_LABEL );
      minutesLabel.getElement().setAttribute( "for", minutesListBox.getElement().getId() ); //$NON-NLS-1$

      final HorizontalPanel durationPanel = new HorizontalPanel();
      durationPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
      durationPanel.setSpacing( blockoutEndTimePicker.getSpacing() );
      durationPanel.add( daysListBox );
      durationPanel.add( daysLabel );
      durationPanel.add( hoursListBox );
      durationPanel.add( hoursLabel );
      durationPanel.add( minutesListBox );
      durationPanel.add( minutesLabel );

      // Bind change handler
      scheduleCombo.addChangeHandler( new ChangeHandler() {

        @Override
        public void onChange( ChangeEvent event ) {
          String scheduleType = scheduleCombo.getItemText( scheduleCombo.getSelectedIndex() );

          if ( ScheduleType.RUN_ONCE.toString().equals( scheduleType ) ) {
            show( true, daysListBox, daysLabel, hoursListBox, hoursLabel, minutesListBox, minutesLabel );

            populateListItems( daysListBox, daysList, 0, 365 );
            populateListItems( hoursListBox, hoursList, 0, 24 );
            populateListItems( minutesListBox, minutesList, 0, 60 );

          } else if ( ScheduleType.HOURS.toString().equals( scheduleType ) ) {
            hide( true, daysListBox, daysLabel, hoursListBox, hoursLabel );
            show( true, minutesListBox, minutesLabel );

            populateListItems( minutesListBox, minutesList, 0, 60 );

          } else if ( ScheduleType.DAILY.toString().equals( scheduleType ) ) {
            hide( true, daysListBox, daysLabel );
            show( true, hoursListBox, hoursLabel, minutesListBox, minutesLabel );

            populateListItems( hoursListBox, hoursList, 0, 24 );
            populateListItems( minutesListBox, minutesList, 0, 60 );

          } else if ( ScheduleType.WEEKLY.toString().equals( scheduleType ) ) {
            show( true, daysListBox, daysLabel, hoursListBox, hoursLabel, minutesListBox, minutesLabel );

            populateListItems( daysListBox, daysList, 0, 7 );
            populateListItems( hoursListBox, hoursList, 0, 24 );
            populateListItems( minutesListBox, minutesList, 0, 60 );

          } else if ( ScheduleType.MONTHLY.toString().equals( scheduleType ) ) {
            show( true, daysListBox, daysLabel, hoursListBox, hoursLabel, minutesListBox, minutesLabel );

            populateListItems( daysListBox, daysList, 0, 28 );
            populateListItems( hoursListBox, hoursList, 0, 24 );
            populateListItems( minutesListBox, minutesList, 0, 60 );

          } else if ( ScheduleType.YEARLY.toString().equals( scheduleType ) ) {
            show( true, daysListBox, daysLabel, hoursListBox, hoursLabel, minutesListBox, minutesLabel );

            populateListItems( daysListBox, daysList, 0, 365 );
            populateListItems( hoursListBox, hoursList, 0, 24 );
            populateListItems( minutesListBox, minutesList, 0, 60 );
          }
        }
      } );

      /*
       * Radio Buttons for duration
       */
      durationRadioButton = new RadioButton( "durationRadioGroup", "durationRadioButton" ); //$NON-NLS-1$ //$NON-NLS-2$
      durationRadioButton.setText( Messages.getString( "schedule.duration" ) );
      durationRadioButton.setValue( Boolean.TRUE );
      durationRadioButton.addClickHandler( new ClickHandler() {

        @Override
        public void onClick( ClickEvent event ) {
          blockoutEndTimePicker.getElement().getStyle().setDisplay( Display.NONE );
          durationPanel.getElement().getStyle().clearDisplay();
        }
      } );

      endTimeRadioButton = new RadioButton( "durationRadioGroup", "endTimeRadioButton" ); //$NON-NLS-1$ //$NON-NLS-2$
      endTimeRadioButton.setText( Messages.getString( "schedule.endTime" ) );
      endTimeRadioButton.addClickHandler( new ClickHandler() {

        @Override
        public void onClick( ClickEvent event ) {
          blockoutEndTimePicker.getElement().getStyle().clearDisplay();
          durationPanel.getElement().getStyle().setDisplay( Display.NONE );
        }
      } );

      // Radio Buttons Panel
      HorizontalPanel radioButtonsPanel = new HorizontalPanel();
      radioButtonsPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
      radioButtonsPanel.getElement().setId( "radio-buttons-panel" );
      radioButtonsPanel.add( durationRadioButton );
      radioButtonsPanel.add( endTimeRadioButton );

      // Ends Panel
      VerticalPanel endsPanel = new VerticalPanel();
      endsPanel.add( radioButtonsPanel );
      endsPanel.add( blockoutEndTimePicker );
      endsPanel.add( durationPanel );

      // Blockout period
      CaptionPanel blockoutStartCaptionPanel = new CaptionPanel( Messages.getString( "schedule.startTime" ) );
      blockoutStartCaptionPanel.setStyleName( SCHEDULE_EDITOR_CAPTION_PANEL );
      HorizontalPanel blockoutStartPanel = new HorizontalPanel();
      blockoutStartPanel.getElement().setId( "blockout-start-panel" );
      blockoutStartPanel.add( getStartTimePicker() );
      configureTimeZonePicker();
      blockoutStartPanel.add( timeZonePicker );
      timeZonePicker.getElement().getParentElement().getStyle().setPaddingTop( 5, Unit.PX );

      blockoutStartCaptionPanel.add( blockoutStartPanel );
      populateTimeZonePicker();

      // Ends Caption Panel
      CaptionPanel endCaptionPanel = new CaptionPanel( Messages.getString( "schedule.endsCaptionTitle" ) );
      endCaptionPanel.setStyleName( SCHEDULE_EDITOR_CAPTION_PANEL );
      endCaptionPanel.add( endsPanel );

      VerticalPanel blockoutPanel = new VerticalPanel();
      blockoutPanel.setWidth( "100%" ); //$NON-NLS-1$
      blockoutPanel.add( blockoutStartCaptionPanel );
      blockoutPanel.add( endCaptionPanel );

      add( blockoutPanel );
    }

    VerticalPanel vp = new VerticalPanel();
    vp.setWidth( "100%" ); //$NON-NLS-1$
    add( vp );
    setCellHeight( vp, "100%" ); //$NON-NLS-1$

    runOnceEditor = new RunOnceEditor( startTimePicker );
    vp.add( runOnceEditor );
    scheduleTypeMap.put( ScheduleType.RUN_ONCE, runOnceEditor );
    runOnceEditor.setVisible( true );

    recurrenceEditor = new RecurrenceEditor( startTimePicker );
    vp.add( recurrenceEditor );
    scheduleTypeMap.put( ScheduleType.SECONDS, recurrenceEditor );
    scheduleTypeMap.put( ScheduleType.MINUTES, recurrenceEditor );
    scheduleTypeMap.put( ScheduleType.HOURS, recurrenceEditor );
    scheduleTypeMap.put( ScheduleType.DAILY, recurrenceEditor );
    scheduleTypeMap.put( ScheduleType.WEEKLY, recurrenceEditor );
    scheduleTypeMap.put( ScheduleType.MONTHLY, recurrenceEditor );
    scheduleTypeMap.put( ScheduleType.YEARLY, recurrenceEditor );
    recurrenceEditor.setVisible( false );

    cronEditor = new CronEditor();
    scheduleTypeMap.put( ScheduleType.CRON, cronEditor );
    cronEditor.setVisible( false );

    if ( !isBlockoutDialog ) {
      vp.add( cronEditor );

      VerticalPanel blockoutButtonPanel = new VerticalPanel();
      blockoutButtonPanel.setWidth( "100%" ); //$NON-NLS-1$
      // blockoutButtonPanel.setHeight("30%");
      blockoutButtonPanel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
      blockoutButtonPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );

      // We want to add a button to check for blockout conflicts
      blockoutCheckButton.setStyleName( "pentaho-button" ); //$NON-NLS-1$
      blockoutCheckButton.getElement().setId( "blockout-check-button" ); //$NON-NLS-1$
      blockoutCheckButton.setVisible( false );

      hspacer.setHeight( "50px" ); //$NON-NLS-1$
      blockoutButtonPanel.add( hspacer );
      blockoutButtonPanel.add( blockoutCheckButton );

      vp.add( hspacer );
      add( blockoutButtonPanel );
    }

    reset( defaultDate );

    configureOnChangeHandler();
  }

  @SuppressWarnings( "deprecation" )
  private void addMinutes( Date date, int count ) {
    date.setMinutes( date.getMinutes() + count );
  }

  private void show( boolean applyToParent, UIObject... objs ) {
    for ( UIObject obj : objs ) {
      Element ele = obj.getElement();
      if ( applyToParent ) {
        ele = ele.getParentElement();
      }
      ele.getStyle().clearDisplay();
    }
  }

  private void hide( boolean applyToParent, UIObject... objs ) {
    for ( UIObject obj : objs ) {
      Element ele = obj.getElement();
      if ( applyToParent ) {
        ele = ele.getParentElement();
      }
      ele.getStyle().setDisplay( Display.NONE );
    }
  }

  private void populateListItems( ListBox listBox, String[] arr, int startIndex, int howMany ) {

    // Clear items
    listBox.clear();

    // Add itesm
    int endIndex = startIndex + howMany;
    for ( int i = startIndex; i < endIndex; i++ ) {
      listBox.addItem( arr[i] );
    }
  }

  private void populateTimeZonePicker() {

    String url = ScheduleHelper.getFullyQualifiedURL() + "api/system/timezones"; //$NON-NLS-1$
    RequestBuilder timeZonesRequest = new RequestBuilder( RequestBuilder.GET, url );
    timeZonesRequest.setHeader( "accept", "application/json" ); //$NON-NLS-1$ //$NON-NLS-2$
    timeZonesRequest.setHeader( "If-Modified-Since", "01 Jan 1970 00:00:00 GMT" );
    try {
      timeZonesRequest.sendRequest( null, new RequestCallback() {

        @Override
        public void onResponseReceived( Request request, Response response ) {
          timeZonePicker.clear();
          String responseText = response.getText();
          JSONValue value = JSONParser.parseLenient( responseText );
          JSONObject object = value.isObject();
          value = object.get( "timeZones" );
          JSONValue serverTZvalue = object.get( "serverTzId" );
          JSONString serverTZIdString = serverTZvalue.isString();
          String serverTZId = serverTZIdString.stringValue();
          object = value.isObject();
          value = object.get( "entry" );
          JSONArray timeZonesJSONArray = value.isArray();
          for ( int i = 0; i < timeZonesJSONArray.size(); i++ ) {
            JSONValue entryValue = timeZonesJSONArray.get( i );
            JSONObject entryObject = entryValue.isObject();
            JSONValue keyValue = entryObject.get( "key" );
            JSONValue theValue = entryObject.get( "value" );
            String key = keyValue.isString().stringValue();
            String valueForKey = theValue.isString().stringValue();
            timeZonePicker.addItem( valueForKey, key );
          }
          for ( int i = 0; i < timeZonePicker.getItemCount(); i++ ) {
            if ( timeZonePicker.getValue( i ).equalsIgnoreCase( serverTZId ) ) {
              timeZonePicker.setSelectedIndex( i );
              break;
            }
          }
        }

        @Override
        public void onError( Request request, Throwable exception ) {
          // TODO Auto-generated method stub

        }

      } );
    } catch ( RequestException e ) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public ListBox getTimeZonePicker() {
    return timeZonePicker;
  }

  void configureTimeZonePicker() {
    timeZonePicker.setStyleName( "timeZonePicker" );
    timeZonePicker.setVisibleItemCount( 1 );
  }

  public void setBlockoutButtonHandler( final ClickHandler handler ) {
    blockoutCheckButton.addClickHandler( handler );
  }

  public Button getBlockoutCheckButton() {
    return blockoutCheckButton;
  }

  public TimePicker getStartTimePicker() {
    return startTimePicker;
  }

  public TimePicker getBlockoutEndTimePicker() {
    return blockoutEndTimePicker;
  }

  public ENDS_TYPE getBlockoutEndsType() {
    return durationRadioButton.getValue() ? ENDS_TYPE.DURATION : ENDS_TYPE.TIME;
  }

  public DurationValues getDurationValues() {
    DurationValues vals = new DurationValues();

    String displayNone = Display.NONE.getCssName();

    // Days
    if ( !displayNone.equals( daysListBox.getElement().getStyle().getDisplay() ) ) {
      vals.days = Integer.parseInt( daysListBox.getItemText( daysListBox.getSelectedIndex() ) );
    }

    // Hours
    if ( !displayNone.equals( hoursListBox.getElement().getStyle().getDisplay() ) ) {
      vals.hours = Integer.parseInt( hoursListBox.getItemText( hoursListBox.getSelectedIndex() ) );
    }

    // Minutes
    if ( !displayNone.equals( minutesListBox.getElement().getStyle().getDisplay() ) ) {
      vals.minutes = Integer.parseInt( minutesListBox.getItemText( minutesListBox.getSelectedIndex() ) );
    }

    return vals;
  }

  public void setDurationFields( long duration ) {

    long remainder = duration;

    long days = remainder / TIME.DAY.getTime();
    remainder -= days * TIME.DAY.getTime();

    long hours = remainder / TIME.HOUR.getTime();
    remainder -= hours * TIME.HOUR.getTime();

    long minutes = remainder / TIME.MINUTE.getTime();

    daysListBox.setSelectedIndex( new Long( days ).intValue() );
    hoursListBox.setSelectedIndex( new Long( hours ).intValue() );
    minutesListBox.setSelectedIndex( new Long( minutes ).intValue() );

    // Set valid end time if range is within 24hrs
    if ( duration < TIME.DAY.getTime() ) {
      boolean isPM = hours >= 12;
      blockoutEndTimePicker.setHour( new Long( hours + ( isPM ? -12 : 0 ) ).toString() );
      blockoutEndTimePicker.setMinute( new Long( minutes ).toString() );
      blockoutEndTimePicker.setTimeOfDay( isPM ? TimeOfDay.PM : TimeOfDay.AM );
    }
  }

  protected Widget createStartTimePanel() {
    CaptionPanel startTimeGB = new CaptionPanel( Messages.getString( "schedule.startTime" ) );
    startTimeGB.setStyleName( SCHEDULE_EDITOR_CAPTION_PANEL );
    HorizontalPanel scheduleStartPanel = new HorizontalPanel();
    scheduleStartPanel.getElement().setId( "schedule-start-panel" );
    scheduleStartPanel.add( getStartTimePicker() );

    configureTimeZonePicker();
    scheduleStartPanel.add( getTimeZonePicker() );

    startTimeGB.add( scheduleStartPanel );
    populateTimeZonePicker();

    return startTimeGB;
  }

  public void reset( Date now ) {
    runOnceEditor.reset( now );
    recurrenceEditor.reset( now );
    cronEditor.reset( now );

    setScheduleType( ScheduleType.RUN_ONCE );
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
        throw new RuntimeException( Messages.getString( "schedule.invalidRunType", getScheduleType().toString() ) );
    }
  }

  /**
   * 
   * @param cronStr
   * @throws CronParseException
   *           if cronStr is not a valid CRON string.
   */
  public void setCronString( String cronStr ) throws CronParseException {

    // Try original simplistic parser...
    CronParser cp = new CronParser( cronStr );
    String recurrenceStr = null;
    try {
      recurrenceStr = cp.parseToRecurrenceString(); // throws CronParseException
    } catch ( CronParseException e ) {
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
   * @return null if the selected schedule does not support repeat-in-seconds, otherwise return the number of seconds
   *         between schedule execution.
   * @throws RuntimeException
   *           if the temporal value is invalid. This condition occurs as a result of programmer error.
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
    lb.addChangeHandler( new ChangeHandler() {

      @Override
      public void onChange( ChangeEvent event ) {
        localThis.handleScheduleChange();
      }
    } );

    // add all schedule types to the combobox
    for ( ScheduleType schedType : EnumSet.range( ScheduleType.RUN_ONCE, ScheduleType.CRON ) ) {
      if ( !isBlockoutDialog
          || ( schedType != ScheduleType.CRON && schedType != ScheduleType.SECONDS && schedType != ScheduleType.MINUTES && schedType != ScheduleType.HOURS ) ) {
        lb.addItem( schedType.toString() );
      }
    }
    lb.setItemSelected( 0, true );
    lb.getElement().setId( "schedule-main-gwt-listbox" );
    lb.setStyleName( SCHEDULE_DROPDOWN_ARROW );

    return lb;
  }

  public ScheduleType getScheduleType() {
    String selectedValue = scheduleCombo.getValue( scheduleCombo.getSelectedIndex() );
    return ScheduleType.stringToScheduleType( selectedValue );
  }

  public void setScheduleType( ScheduleType scheduleType ) {
    int itemCount = scheduleCombo.getItemCount();
    for ( int i = 0; i < itemCount; i++ ) {
      String itemText = scheduleCombo.getItemText( i );
      if ( itemText.equals( scheduleType.toString() ) ) {
        scheduleCombo.setSelectedIndex( i );
        DomEvent.fireNativeEvent( Document.get().createChangeEvent(), scheduleCombo );
      }
    }
    selectScheduleTypeEditor( scheduleType );
  }

  /**
   * NOTE: should only ever be used by validators. This is a backdoor into this class that shouldn't be here, do not use
   * this method unless you are validating.
   * 
   * @return DateRangeEditor
   */
  public RecurrenceEditor getRecurrenceEditor() {
    return recurrenceEditor;
  }

  /**
   * NOTE: should only ever be used by validators. This is a backdoor into this class that shouldn't be here, do not use
   * this method unless you are validating.
   * 
   * @return DateRangeEditor
   */
  public CronEditor getCronEditor() {
    return cronEditor;
  }

  /**
   * NOTE: should only ever be used by validators. This is a backdoor into this class that shouldn't be here, do not use
   * this method unless you are validating.
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

  public void setBlockoutEndTime( String endTime ) {
    blockoutEndTimePicker.setTime( endTime );
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
        throw new RuntimeException( Messages.getString( "schedule.invalidRunType", getScheduleType().toString() ) );
    }
  }

  public void setStartDate( Date startDate ) {
    runOnceEditor.setStartDate( startDate );
    recurrenceEditor.setStartDate( startDate );
    cronEditor.setStartDate( startDate );
  }

  @SuppressWarnings( "deprecation" )
  public Date getStartDate() {
    switch ( getScheduleType() ) {
      case RUN_ONCE:
        Date startDate = runOnceEditor.getStartDate();
        String startTime = runOnceEditor.getStartTime();
        String[] times = startTime.split( ":" ); //$NON-NLS-1$
        int hour = Integer.parseInt( times[0] );
        int minute = Integer.parseInt( times[1] );
        if ( startTime.indexOf( TimeOfDay.PM.toString() ) >= 0 ) { //$NON-NLS-1$
          hour += 12;
        }

        startDate.setHours( hour );
        startDate.setMinutes( minute );
        startDate.setSeconds( 0 );
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
        throw new RuntimeException( Messages.getString( "schedule.invalidRunType", getScheduleType().toString() ) );
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
        throw new RuntimeException( Messages.getString( "schedule.invalidRunType", getScheduleType().toString() ) );
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

  protected void selectScheduleTypeEditor( ScheduleType scheduleType ) {
    // if we are switching to cron type, then hide the start time panel
    if ( ( isBlockoutDialog == false ) && ( startTimePanel != null ) ) {
      if ( scheduleType == ScheduleType.CRON ) {
        startTimePanel.setVisible( false );
      } else {
        startTimePanel.setVisible( true );
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

  protected ScheduleType temporalValueToScheduleType( TemporalValue tv ) {
    return temporalValueToScheduleTypeMap.get( tv );
  }

  private TemporalValue scheduleTypeToTemporalValue( ScheduleType st ) {
    return scheduleTypeToTemporalValueMap.get( st );
  }

  @Override
  public void setOnChangeHandler( ICallback<IChangeHandler> handler ) {
    onChangeHandler = handler;
  }

  protected void changeHandler() {
    if ( null != onChangeHandler ) {
      onChangeHandler.onHandle( this );
    }
  }

  private void configureOnChangeHandler() {
    final ScheduleEditor localThis = this;

    ICallback<IChangeHandler> handler = new ICallback<IChangeHandler>() {
      @Override
      public void onHandle( IChangeHandler o ) {
        localThis.changeHandler();
      }
    };

    ChangeHandler changeHandler = new ChangeHandler() {
      @Override
      public void onChange( ChangeEvent event ) {
        localThis.changeHandler();
      }
    };

    ClickHandler clickHandler = new ClickHandler() {

      @Override
      public void onClick( ClickEvent event ) {
        localThis.changeHandler();
      }
    };

    scheduleCombo.addChangeHandler( changeHandler );
    runOnceEditor.setOnChangeHandler( handler );
    recurrenceEditor.setOnChangeHandler( handler );
    cronEditor.setOnChangeHandler( handler );

    if ( daysListBox != null ) {
      daysListBox.addChangeHandler( changeHandler );
    }
    if ( hoursListBox != null ) {
      hoursListBox.addChangeHandler( changeHandler );
    }
    if ( minutesListBox != null ) {
      minutesListBox.addChangeHandler( changeHandler );
    }

    if ( startTimePicker != null ) {
      startTimePicker.setOnChangeHandler( handler );
    }
    if ( blockoutEndTimePicker != null ) {
      blockoutEndTimePicker.setOnChangeHandler( handler );
    }

    if ( durationRadioButton != null ) {
      durationRadioButton.addClickHandler( clickHandler );
    }
    if ( endTimeRadioButton != null ) {
      endTimeRadioButton.addClickHandler( clickHandler );
    }
  }

  public boolean isBlockoutDialog() {
    return isBlockoutDialog;
  }

  public String getTargetTimezone() {

    if ( this.getTimeZonePicker() != null ) {
      ListBox tzPicker = this.getTimeZonePicker();
      int selIndex = tzPicker.getSelectedIndex();
      if ( selIndex != -1 ) {
        targetTimezone = tzPicker.getItemText( selIndex );
      }
    }
    return targetTimezone;
  }
}
