package org.pentaho.gwt.widgets.client.controls.schededitor;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.pentaho.gwt.widgets.client.controls.DateRangeEditor;
import org.pentaho.gwt.widgets.client.controls.ErrorLabel;
import org.pentaho.gwt.widgets.client.controls.TimePicker;
import org.pentaho.gwt.widgets.client.i18n.WidgetsLocalizedMessages;
import org.pentaho.gwt.widgets.client.i18n.WidgetsLocalizedMessagesSingleton;
import org.pentaho.gwt.widgets.client.ui.ICallback;
import org.pentaho.gwt.widgets.client.ui.IChangeHandler;
import org.pentaho.gwt.widgets.client.utils.EnumException;
import org.pentaho.gwt.widgets.client.utils.TimeUtil;

abstract public class AbstractScheduleEditor extends VerticalPanel implements IChangeHandler
{
  protected static final WidgetsLocalizedMessages MSGS = WidgetsLocalizedMessagesSingleton.getInstance().getMessages();

  private TimePicker startTimePicker = null;

  private SecondlyRecurrenceEditor secondlyEditor = null;

  private MinutelyRecurrenceEditor minutelyEditor = null;

  private HourlyRecurrenceEditor hourlyEditor = null;

  private DailyRecurrenceEditor dailyEditor = null;

  private WeeklyRecurrenceEditor weeklyEditor = null;

  private MonthlyRecurrenceEditor monthlyEditor = null;

  private YearlyRecurrenceEditor yearlyEditor = null;

  private DateRangeEditor dateRangeEditor = null;

  protected static int VALUE_OF_SUNDAY = 1;
  private ICallback<IChangeHandler> onChangeHandler;

  protected static final String SPACE = " "; //$NON-NLS-1$

  private static final String DAILY_RB_GROUP = "daily-group"; //$NON-NLS-1$
  private static final String MONTHLY_RB_GROUP = "monthly-group"; //$NON-NLS-1$
  protected static final String SCHEDULE_EDITOR_CAPTION_PANEL = "schedule-editor-caption-panel"; //$NON-NLS-1$
  private static final String DOW_CHECKBOX = "day-of-week-checkbox"; //$NON-NLS-1$

  protected Map<TemporalValue, Panel> temporalPanelMap = new LinkedHashMap<TemporalValue, Panel>();

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

    private static ScheduleType[] scheduleValue = {
                                                          RUN_ONCE,
                                                          SECONDS,
                                                          MINUTES,
                                                          HOURS,
                                                          DAILY,
                                                          WEEKLY,
                                                          MONTHLY,
                                                          YEARLY,
                                                          CRON
    };

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

  public enum TemporalValue {
    SECONDS(0, MSGS.seconds()),
    MINUTES(1, MSGS.minutes()),
    HOURS(2, MSGS.hours()),
    DAILY(3, MSGS.daily()),
    WEEKLY(4, MSGS.weekly()),
    MONTHLY(5, MSGS.monthly()),
    YEARLY(6, MSGS.yearly());

    private TemporalValue(int value, String name) {
      this.value = value;
      this.name = name;
    }

    private final int value;

    private final String name;

    private static TemporalValue[] temporalValues = {SECONDS,
                                                     MINUTES,
                                                     HOURS,
                                                     DAILY,
                                                     WEEKLY,
                                                     MONTHLY,
                                                     YEARLY
    };

    public int value() {
      return value;
    }

    public String toString() {
      return name;
    }

    public static TemporalValue get(int idx) {
      return temporalValues[idx];
    }

    public static int length() {
      return temporalValues.length;
    }

    public static TemporalValue stringToTemporalValue( String temporalValue ) throws EnumException
    {
      for (TemporalValue v : EnumSet.range(TemporalValue.SECONDS, TemporalValue.YEARLY)) {
        if ( v.toString().equals( temporalValue ) ) {
          return v;
        }
      }
      throw new EnumException( MSGS.invalidTemporalValue( temporalValue ) );
    }
  } /* end enum */


  public AbstractScheduleEditor()
  {
    secondlyEditor = new SecondlyRecurrenceEditor();
    minutelyEditor = new MinutelyRecurrenceEditor();
    hourlyEditor = new HourlyRecurrenceEditor();
    dailyEditor = new DailyRecurrenceEditor();
    weeklyEditor = new WeeklyRecurrenceEditor();
    monthlyEditor = new MonthlyRecurrenceEditor();
    yearlyEditor = new YearlyRecurrenceEditor();

    startTimePicker = new TimePicker();

    Date now = new Date();
    dateRangeEditor = new DateRangeEditor( now );
  }

  public void reset( Date d ) {
    TimePicker startTimePicker = getStartTimePicker();
    startTimePicker.setHour( "12" ); //$NON-NLS-1$
    startTimePicker.setMinute( "00" ); //$NON-NLS-1$
    startTimePicker.setTimeOfDay( TimeUtil.TimeOfDay.AM );

    getDateRangeEditor().reset( d );

    getSecondlyEditor().reset();
    getMinutelyEditor().reset();
    getHourlyEditor().reset();
    getDailyEditor().reset();
    getWeeklyEditor().reset();
    getMonthlyEditor().reset();
    getYearlyEditor().reset();
  }


  public TimePicker getStartTimePicker()
  {
    return startTimePicker;
  }

  /**
   * NOTE: should only ever be used by validators. This is a backdoor
   * into this class that shouldn't be here, do not use this method
   * unless you are validating.
   *
   * @return DateRangeEditor
   */
  public DateRangeEditor getDateRangeEditor() {
    return dateRangeEditor;
  }

  /**
   * NOTE: should only ever be used by validators. This is a backdoor
   * into this class that shouldn't be here, do not use this method
   * unless you are validating.
   *
   * @return SecondlyRecurrencePanel
   */
  public SecondlyRecurrenceEditor getSecondlyEditor() {
    return secondlyEditor;
  }

  /**
   * NOTE: should only ever be used by validators. This is a backdoor
   * into this class that shouldn't be here, do not use this method
   * unless you are validating.
   *
   * @return MinutelyRecurrencePanel
   */
  public MinutelyRecurrenceEditor getMinutelyEditor() {
    return minutelyEditor;
  }

  /**
   * NOTE: should only ever be used by validators. This is a backdoor
   * into this class that shouldn't be here, do not use this method
   * unless you are validating.
   *
   * @return HourlyRecurrencePanel
   */
  public HourlyRecurrenceEditor getHourlyEditor() {
    return hourlyEditor;
  }

  /**
   * NOTE: should only ever be used by validators. This is a backdoor
   * into this class that shouldn't be here, do not use this method
   * unless you are validating.
   *
   * @return DailyRecurrencePanel
   */
  public DailyRecurrenceEditor getDailyEditor() {
    return dailyEditor;
  }

  /**
   * NOTE: should only ever be used by validators. This is a backdoor
   * into this class that shouldn't be here, do not use this method
   * unless you are validating.
   *
   * @return WeeklyRecurrencePanel
   */
  public WeeklyRecurrenceEditor getWeeklyEditor() {
    return weeklyEditor;
  }

  /**
   * NOTE: should only ever be used by validators. This is a backdoor
   * into this class that shouldn't be here, do not use this method
   * unless you are validating.
   *
   * @return MonthlyRecurrencePanel
   */
  public MonthlyRecurrenceEditor getMonthlyEditor() {
    return monthlyEditor;
  }

  /**
   * NOTE: should only ever be used by validators. This is a backdoor
   * into this class that shouldn't be here, do not use this method
   * unless you are validating.
   *
   * @return YearlyRecurrencePanel
   */
  public YearlyRecurrenceEditor getYearlyEditor() {
    return yearlyEditor;
  }


  public void setOnChangeHandler(final ICallback<IChangeHandler> handler)
  {
    this.onChangeHandler = handler;
  }

  abstract protected void changeHandler();

  public ICallback<IChangeHandler> getOnChangeHandler()
  {
    return onChangeHandler;
  }


  public class SecondlyRecurrenceEditor extends SimpleRecurrencePanel {
    public SecondlyRecurrenceEditor() {
      super( MSGS.seconds() );
    }
  }

  public class MinutelyRecurrenceEditor extends SimpleRecurrencePanel {
    public MinutelyRecurrenceEditor() {
      super( MSGS.minutesLabel() );
    }
  }

  public class HourlyRecurrenceEditor extends SimpleRecurrencePanel {
    public HourlyRecurrenceEditor() {
      super( MSGS.hoursLabel() );
    }
  }

  public class DailyRecurrenceEditor extends VerticalPanel implements IChangeHandler {

    private TextBox repeatValueTb = new TextBox();
    private RadioButton everyNDaysRb = new RadioButton(DAILY_RB_GROUP, MSGS.every() );
    private RadioButton everyWeekdayRb = new RadioButton(DAILY_RB_GROUP, MSGS.everyWeekDay() );
    private ErrorLabel repeatLabel = null;
    private ICallback<IChangeHandler> onChangeHandler;

    public DailyRecurrenceEditor() {
      HorizontalPanel hp = new HorizontalPanel();
      everyNDaysRb.setStyleName("recurrenceRadioButton"); //$NON-NLS-1$
      everyNDaysRb.setChecked(true);
      hp.add(everyNDaysRb);

      repeatValueTb.setWidth("3em"); //$NON-NLS-1$
      repeatValueTb.setTitle( MSGS.numDaysToRepeat() );
      hp.add(repeatValueTb);

      Label l = new Label( MSGS.daysLabel() );
      l.setStyleName("endLabel"); //$NON-NLS-1$
      hp.add(l);
      repeatLabel = new ErrorLabel( hp );
      add( repeatLabel );

      everyWeekdayRb.setStyleName("recurrenceRadioButton"); //$NON-NLS-1$
      add(everyWeekdayRb);
      configureOnChangeHandler();
    }

    public void reset() {
      setRepeatValue( "" ); //$NON-NLS-1$
      setEveryNDays();
    }

    public String getRepeatValue() {
      return repeatValueTb.getText();
    }

    public void setRepeatValue( String repeatValue ) {
      repeatValueTb.setText( repeatValue );
    }

    public void setEveryNDays() {
      everyNDaysRb.setChecked( true );
      everyWeekdayRb.setChecked( false );
    }

    public boolean isEveryNDays() {
      return everyNDaysRb.isChecked();
    }

    public void setEveryWeekday() {
      everyWeekdayRb.setChecked( true );
      everyNDaysRb.setChecked( false );
    }

    public boolean isEveryWeekday() {
      return everyWeekdayRb.isChecked();
    }

    public void setRepeatError( String errorMsg ) {
      repeatLabel.setErrorMsg( errorMsg );
    }

    public void setOnChangeHandler( ICallback<IChangeHandler> handler ) {
      this.onChangeHandler = handler;
    }

    private void changeHandler() {
      if ( null != onChangeHandler ) {
        onChangeHandler.onHandle( this );
      }
    }

    private void configureOnChangeHandler() {
      final DailyRecurrenceEditor localThis = this;

      KeyboardListener keyboardListener = new KeyboardListener() {
        public void onKeyDown(Widget sender, char keyCode, int modifiers) {
        }
        public void onKeyPress(Widget sender, char keyCode, int modifiers) {
        }
        public void onKeyUp(Widget sender, char keyCode, int modifiers) {
          localThis.changeHandler();
        }
      };

      ClickListener clickListener = new ClickListener() {
        public void onClick(Widget sender) {
          localThis.changeHandler();
        }
      };

      repeatValueTb.addKeyboardListener( keyboardListener );
      everyNDaysRb.addClickListener( clickListener );
      everyNDaysRb.addKeyboardListener( keyboardListener );
      everyWeekdayRb.addClickListener( clickListener );
      everyWeekdayRb.addKeyboardListener( keyboardListener );
    }
  }

  public class WeeklyRecurrenceEditor extends VerticalPanel implements IChangeHandler {

    private Map<TimeUtil.DayOfWeek, CheckBox> dayToCheckBox = new HashMap<TimeUtil.DayOfWeek, CheckBox>();
    private ErrorLabel everyWeekOnLabel = null;
    private ICallback<IChangeHandler> onChangeHandler;

    public WeeklyRecurrenceEditor() {
      setStyleName("weeklyRecurrencePanel"); //$NON-NLS-1$

      Label l = new Label( MSGS.recurEveryWeek() );
      everyWeekOnLabel = new ErrorLabel( l );
      l.setStyleName("startLabel"); //$NON-NLS-1$
      add( everyWeekOnLabel );

      FlexTable gp = new FlexTable();
      gp.setCellPadding(0);
      gp.setCellSpacing(0);
      // add Sun - Wed
      final int ITEMS_IN_ROW = 4;
      for (int ii = 0; ii < ITEMS_IN_ROW; ++ii) {
        TimeUtil.DayOfWeek day = TimeUtil.DayOfWeek.get(ii);
        CheckBox cb = new CheckBox(day.toString());
        cb.setStylePrimaryName(DOW_CHECKBOX);
        gp.setWidget(0, ii, cb);
        dayToCheckBox.put(day, cb);
      }
      // Add Thur - Sat
      for (int ii = ITEMS_IN_ROW; ii < TimeUtil.DayOfWeek.length(); ++ii) {
        TimeUtil.DayOfWeek day = TimeUtil.DayOfWeek.get(ii);
        CheckBox cb = new CheckBox(day.toString());
        cb.setStylePrimaryName(DOW_CHECKBOX);
        gp.setWidget(1, ii - 4, cb);
        dayToCheckBox.put(day, cb);
      }
      add(gp);
      configureOnChangeHandler();
    }

    public void reset() {
      for ( TimeUtil.DayOfWeek d : dayToCheckBox.keySet() ) {
        CheckBox cb = dayToCheckBox.get( d );
        cb.setChecked( false );
      }
    }

    public List<TimeUtil.DayOfWeek> getCheckedDays() {
      ArrayList<TimeUtil.DayOfWeek> checkedDays = new ArrayList<TimeUtil.DayOfWeek>();
      for ( TimeUtil.DayOfWeek d : EnumSet.range(TimeUtil.DayOfWeek.SUN, TimeUtil.DayOfWeek.SAT) ) {
        CheckBox cb = dayToCheckBox.get( d );
        if ( cb.isChecked() ) {
          checkedDays.add(d);
        }
      }
      return checkedDays;
    }
    /**
     *
     * @param valueOfSunday int used to adjust the starting point of the weekday sequence.
     * If this value is 0, Sun-Sat maps to 0-6, if this value is 1, Sun-Sat maps to 1-7, etc.
     * @return String comma separated list of numeric days of the week.
     */
    public String getCheckedDaysAsString( int valueOfSunday ) {
      StringBuilder sb = new StringBuilder();
      for ( TimeUtil.DayOfWeek d : getCheckedDays()) {
        sb.append( Integer.toString( d.value()+valueOfSunday ) ).append( "," ); //$NON-NLS-1$
      }
      sb.deleteCharAt( sb.length()-1 );
      return sb.toString();
    }

    /**
     *
     * @param valueOfSunday int used to adjust the starting point of the weekday sequence.
     * If this value is 0, Sun-Sat maps to 0-6, if this value is 1, Sun-Sat maps to 1-7, etc.
     * @return String comma separated list of numeric days of the week.
     */
    public void setCheckedDaysAsString( String strDays, int valueOfSunday ) {
      String[] days = strDays.split( "," ); //$NON-NLS-1$
      for ( String day : days ) {
        int intDay = Integer.parseInt( day ) - valueOfSunday;
        TimeUtil.DayOfWeek dayOfWeek = TimeUtil.DayOfWeek.get(intDay);
        CheckBox cb = dayToCheckBox.get( dayOfWeek );
        cb.setChecked( true );
      }
    }

    public int getNumCheckedDays() {
      int numCheckedDays = 0;
      //for ( DayOfWeek d : EnumSet.range( DayOfWeek.SUN, DayOfWeek.SAT) ) {
      for ( Map.Entry<TimeUtil.DayOfWeek, CheckBox> cbEntry : dayToCheckBox.entrySet() ) {
        if ( cbEntry.getValue().isChecked() ) {
          numCheckedDays++;
        }
      }
      return numCheckedDays;
    }

    public void setEveryDayOnError( String errorMsg ) {
      everyWeekOnLabel.setErrorMsg( errorMsg );
    }

    public void setOnChangeHandler( ICallback<IChangeHandler> handler ) {
      this.onChangeHandler = handler;
    }

    private void changeHandler() {
      if ( null != onChangeHandler ) {
        onChangeHandler.onHandle( this );
      }
    }

    private void configureOnChangeHandler() {

      final WeeklyRecurrenceEditor localThis = this;

      KeyboardListener keyboardListener = new KeyboardListener() {
        public void onKeyDown(Widget sender, char keyCode, int modifiers) {
        }
        public void onKeyPress(Widget sender, char keyCode, int modifiers) {
        }
        public void onKeyUp(Widget sender, char keyCode, int modifiers) {
          localThis.changeHandler();
        }
      };

      ClickListener clickListener = new ClickListener() {
        public void onClick(Widget sender) {
          localThis.changeHandler();
        }
      };
      for ( TimeUtil.DayOfWeek d : dayToCheckBox.keySet() ) {
        CheckBox cb = dayToCheckBox.get( d );
        cb.addClickListener( clickListener );
        cb.addKeyboardListener( keyboardListener );
      }
    }
  }

  private ListBox createDayOfWeekListBox() {
    ListBox l = new ListBox();
    for (int ii = 0; ii < TimeUtil.DayOfWeek.length(); ++ii) {
      TimeUtil.DayOfWeek day = TimeUtil.DayOfWeek.get(ii);
      l.addItem(day.toString());
    }
    return l;
  }

  private ListBox createMonthOfYearListBox() {

    ListBox l = new ListBox();
    for (int ii = 0; ii < TimeUtil.MonthOfYear.length(); ++ii) {
      TimeUtil.MonthOfYear month = TimeUtil.MonthOfYear.get(ii);
      l.addItem(month.toString());
    }

    return l;
  }

  private ListBox createWhichWeekListBox() {

    ListBox l = new ListBox();
    for ( TimeUtil.WeekOfMonth week : EnumSet.range(TimeUtil.WeekOfMonth.FIRST, TimeUtil.WeekOfMonth.LAST)) {
      l.addItem( week.toString() );
    }

    return l;
  }


  public class MonthlyRecurrenceEditor extends VerticalPanel implements IChangeHandler {

    private RadioButton dayNOfMonthRb = new RadioButton(MONTHLY_RB_GROUP, MSGS.day());
    private RadioButton nthDayNameOfMonthRb = new RadioButton(MONTHLY_RB_GROUP, MSGS.the() );
    private TextBox dayOfMonthTb = new TextBox();
    private ListBox whichWeekLb = createWhichWeekListBox();
    private ListBox dayOfWeekLb = createDayOfWeekListBox();
    private ErrorLabel dayNOfMonthLabel = null;
    private ICallback<IChangeHandler> onChangeHandler;

    public MonthlyRecurrenceEditor() {
      setSpacing(6);

      HorizontalPanel hp = new HorizontalPanel();
      dayNOfMonthRb.setStyleName("recurrenceRadioButton"); //$NON-NLS-1$
      dayNOfMonthRb.setChecked( true );
      hp.add(dayNOfMonthRb);
      dayOfMonthTb.setWidth("3em"); //$NON-NLS-1$
      hp.add(dayOfMonthTb);
      Label l = new Label( MSGS.ofEveryMonth() );
      l.setStyleName("endLabel"); //$NON-NLS-1$
      hp.add(l);

      dayNOfMonthLabel = new ErrorLabel( hp );
      add( dayNOfMonthLabel );

      hp = new HorizontalPanel();
      nthDayNameOfMonthRb.setStyleName("recurrenceRadioButton"); //$NON-NLS-1$
      hp.add(nthDayNameOfMonthRb);
      hp.add(whichWeekLb);

      hp.add(dayOfWeekLb);
      l = new Label( MSGS.ofEveryMonth() );
      l.setStyleName("endLabel"); //$NON-NLS-1$
      hp.add(l);
      add(hp);
      configureOnChangeHandler();
    }

    public void reset() {
      setDayNOfMonth();
      setDayOfMonth( "" ); //$NON-NLS-1$
      setWeekOfMonth( TimeUtil.WeekOfMonth.FIRST );
      setDayOfWeek( TimeUtil.DayOfWeek.SUN );
    }

    public void setDayNOfMonth() {
      dayNOfMonthRb.setChecked( true );
      nthDayNameOfMonthRb.setChecked( false );
    }

    public boolean isDayNOfMonth() {
      return dayNOfMonthRb.isChecked();
    }

    public void setNthDayNameOfMonth() {
      nthDayNameOfMonthRb.setChecked( true );
      dayNOfMonthRb.setChecked( false );
    }

    public boolean isNthDayNameOfMonth() {
      return nthDayNameOfMonthRb.isChecked();
    }

    public String getDayOfMonth() {
      return dayOfMonthTb.getText();
    }

    public void setDayOfMonth( String dayOfMonth ) {
      dayOfMonthTb.setText( dayOfMonth );
    }

    public TimeUtil.WeekOfMonth getWeekOfMonth() {
      return TimeUtil.WeekOfMonth.get(whichWeekLb.getSelectedIndex());
    }

    public void setWeekOfMonth( TimeUtil.WeekOfMonth week ) {
      whichWeekLb.setSelectedIndex( week.value() );
    }

    public TimeUtil.DayOfWeek getDayOfWeek() {
      return TimeUtil.DayOfWeek.get(dayOfWeekLb.getSelectedIndex());
    }

    public void setDayOfWeek( TimeUtil.DayOfWeek day ) {
      dayOfWeekLb.setSelectedIndex( day.value() );
    }

    public void setDayNOfMonthError( String errorMsg ) {
      dayNOfMonthLabel.setErrorMsg( errorMsg );
    }

    public void setOnChangeHandler( ICallback<IChangeHandler> handler ) {
      this.onChangeHandler = handler;
    }

    private void changeHandler() {
      if ( null != onChangeHandler ) {
        onChangeHandler.onHandle( this );
      }
    }

    private void configureOnChangeHandler() {
      final MonthlyRecurrenceEditor localThis = this;

      KeyboardListener keyboardListener = new KeyboardListener() {
        public void onKeyDown(Widget sender, char keyCode, int modifiers) {
        }
        public void onKeyPress(Widget sender, char keyCode, int modifiers) {
        }
        public void onKeyUp(Widget sender, char keyCode, int modifiers) {
          localThis.changeHandler();
        }
      };

      ClickListener clickListener = new ClickListener() {
        public void onClick(Widget sender) {
          localThis.changeHandler();
        }
      };

      ChangeListener changeListener = new ChangeListener() {
        public void onChange(Widget sender) {
          localThis.changeHandler();
        }
      };
      dayNOfMonthRb.addClickListener( clickListener );
      dayNOfMonthRb.addKeyboardListener( keyboardListener );
      nthDayNameOfMonthRb.addClickListener( clickListener );
      nthDayNameOfMonthRb.addKeyboardListener( keyboardListener );
      dayOfMonthTb.addKeyboardListener( keyboardListener );
      whichWeekLb.addChangeListener( changeListener );
      dayOfWeekLb.addChangeListener( changeListener );
    }
  }

  public class YearlyRecurrenceEditor extends VerticalPanel implements IChangeHandler {

    private RadioButton everyMonthOnNthDayRb = new RadioButton(YEARLY_RB_GROUP, MSGS.every() );
    private RadioButton nthDayNameOfMonthNameRb = new RadioButton(YEARLY_RB_GROUP, MSGS.the() );
    private TextBox dayOfMonthTb = new TextBox();
    private ListBox monthOfYearLb0 = createMonthOfYearListBox();
    private ListBox monthOfYearLb1 = createMonthOfYearListBox();
    private ListBox whichWeekLb = createWhichWeekListBox();
    private ListBox dayOfWeekLb = createDayOfWeekListBox();
    private ErrorLabel dayOfMonthLabel = null;
    private ICallback<IChangeHandler> onChangeHandler;

    private static final String YEARLY_RB_GROUP = "yearly-group"; //$NON-NLS-1$
    public YearlyRecurrenceEditor() {
      setSpacing(6);

      HorizontalPanel p = new HorizontalPanel();
      everyMonthOnNthDayRb.setStyleName("recurrenceRadioButton"); //$NON-NLS-1$
      everyMonthOnNthDayRb.setChecked(true);
      p.add(everyMonthOnNthDayRb);
      p.add( monthOfYearLb0 );
      dayOfMonthTb.setStylePrimaryName("DAY_OF_MONTH_TB"); //$NON-NLS-1$
      dayOfMonthTb.setWidth("3em"); //$NON-NLS-1$
      p.add(dayOfMonthTb);
      dayOfMonthLabel = new ErrorLabel( p );
      add(dayOfMonthLabel);

      p = new HorizontalPanel();
      nthDayNameOfMonthNameRb.setStyleName("recurrenceRadioButton"); //$NON-NLS-1$
      p.add(nthDayNameOfMonthNameRb);
      p.add(whichWeekLb);
      p.add(dayOfWeekLb);
      Label l = new Label( MSGS.of() );
      l.setStyleName("middleLabel"); //$NON-NLS-1$
      p.add(l);
      p.add( monthOfYearLb1 );
      add(p);
      configureOnChangeHandler();
    }

    public void reset() {
      setEveryMonthOnNthDay();
      setMonthOfYear0( TimeUtil.MonthOfYear.JAN );
      setDayOfMonth( "" ); //$NON-NLS-1$
      setWeekOfMonth( TimeUtil.WeekOfMonth.FIRST );
      setDayOfWeek( TimeUtil.DayOfWeek.SUN );
      setMonthOfYear1( TimeUtil.MonthOfYear.JAN );
    }

    public boolean isEveryMonthOnNthDay() {
      return everyMonthOnNthDayRb.isChecked();
    }

    public void setEveryMonthOnNthDay() {
      everyMonthOnNthDayRb.setChecked( true );
      nthDayNameOfMonthNameRb.setChecked( false );
    }

    public boolean isNthDayNameOfMonthName() {
      return nthDayNameOfMonthNameRb.isChecked();
    }

    public void setNthDayNameOfMonthName() {
      nthDayNameOfMonthNameRb.setChecked( true );
      everyMonthOnNthDayRb.setChecked( false );
    }

    public String getDayOfMonth() {
      return dayOfMonthTb.getText();
    }

    public void setDayOfMonth( String dayOfMonth ) {
      dayOfMonthTb.setText( dayOfMonth );
    }

    public TimeUtil.WeekOfMonth getWeekOfMonth() {
      return TimeUtil.WeekOfMonth.get(whichWeekLb.getSelectedIndex());
    }

    public void setWeekOfMonth( TimeUtil.WeekOfMonth week ) {
      whichWeekLb.setSelectedIndex( week.value() );
    }

    public TimeUtil.DayOfWeek getDayOfWeek() {
      return TimeUtil.DayOfWeek.get(dayOfWeekLb.getSelectedIndex());
    }

    public void setDayOfWeek( TimeUtil.DayOfWeek day ) {
      dayOfWeekLb.setSelectedIndex( day.value() );
    }

    public TimeUtil.MonthOfYear getMonthOfYear0() {
      return TimeUtil.MonthOfYear.get(monthOfYearLb0.getSelectedIndex());
    }

    public void setMonthOfYear0( TimeUtil.MonthOfYear month ) {
      monthOfYearLb0.setSelectedIndex( month.value() );
    }

    public TimeUtil.MonthOfYear getMonthOfYear1() {
      return TimeUtil.MonthOfYear.get(monthOfYearLb1.getSelectedIndex());
    }

    public void setMonthOfYear1( TimeUtil.MonthOfYear month ) {
      monthOfYearLb1.setSelectedIndex( month.value() );
    }

    public void setDayOfMonthError( String errorMsg ) {
      dayOfMonthLabel.setErrorMsg( errorMsg );
    }

    public void setOnChangeHandler( ICallback<IChangeHandler> handler ) {
      this.onChangeHandler = handler;
    }

    private void changeHandler() {
      if ( null != onChangeHandler ) {
        onChangeHandler.onHandle( this );
      }
    }

    private void configureOnChangeHandler() {

      final YearlyRecurrenceEditor localThis = this;

      KeyboardListener keyboardListener = new KeyboardListener() {
        public void onKeyDown(Widget sender, char keyCode, int modifiers) {
        }
        public void onKeyPress(Widget sender, char keyCode, int modifiers) {
        }
        public void onKeyUp(Widget sender, char keyCode, int modifiers) {
          localThis.changeHandler();
        }
      };

      ClickListener clickListener = new ClickListener() {
        public void onClick(Widget sender) {
          localThis.changeHandler();
        }
      };

      ChangeListener changeListener = new ChangeListener() {
        public void onChange(Widget sender) {
          localThis.changeHandler();
        }
      };

      everyMonthOnNthDayRb.addClickListener( clickListener );
      everyMonthOnNthDayRb.addKeyboardListener( keyboardListener );

      nthDayNameOfMonthNameRb.addClickListener( clickListener );
      nthDayNameOfMonthNameRb.addKeyboardListener( keyboardListener );

      dayOfMonthTb.addKeyboardListener( keyboardListener );

      monthOfYearLb0.addChangeListener( changeListener );
      monthOfYearLb1.addChangeListener( changeListener );
      whichWeekLb.addChangeListener( changeListener );
      dayOfWeekLb.addChangeListener( changeListener );
    }
  }

  protected class SimpleRecurrencePanel extends VerticalPanel implements IChangeHandler {
    private TextBox valueTb = new TextBox();
    private ErrorLabel valueLabel = null;
    private ICallback<IChangeHandler> onChangeHandler;

    public SimpleRecurrencePanel( String strLabel ) {

      HorizontalPanel hp = new HorizontalPanel();
      Label l = new Label( MSGS.every() );
      l.setStyleName("startLabel"); //$NON-NLS-1$
      hp.add(l);

      valueTb.setWidth( "3em" ); //$NON-NLS-1$
      valueTb.setTitle( MSGS.numberOfXToRepeat( strLabel ) );
      hp.add(valueTb);

      l = new Label( strLabel );
      l.setStyleName( "endLabel" ); //$NON-NLS-1$
      hp.add(l);

      valueLabel = new ErrorLabel( hp );
      add( valueLabel );

      configureOnChangeHandler();
    }

    public String getValue() {
      return valueTb.getText();
    }

    public void setValue( String val ) {
      valueTb.setText( val );
    }

    public void reset() {
      setValue( "" ); //$NON-NLS-1$
    }

    public void setValueError( String errorMsg ) {
      valueLabel.setErrorMsg( errorMsg );
    }

    public void setOnChangeHandler( ICallback<IChangeHandler> handler ) {
      this.onChangeHandler = handler;
    }

    private void changeHandler() {
      if ( null != onChangeHandler ) {
        onChangeHandler.onHandle( this );
      }
    }

    private void configureOnChangeHandler() {
      final SimpleRecurrencePanel localThis = this;

      KeyboardListener keyboardListener = new KeyboardListener() {
        public void onKeyDown(Widget sender, char keyCode, int modifiers) {
        }
        public void onKeyPress(Widget sender, char keyCode, int modifiers) {
        }
        public void onKeyUp(Widget sender, char keyCode, int modifiers) {
          localThis.changeHandler();
        }
      };

      valueTb.addKeyboardListener( keyboardListener );
    }
  }

}
