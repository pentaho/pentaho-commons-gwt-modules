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
 */
package org.pentaho.gwt.widgets.client.controls.schededitor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import org.pentaho.gwt.widgets.client.controls.TimePicker;
import org.pentaho.gwt.widgets.client.utils.CronParseException;
import org.pentaho.gwt.widgets.client.utils.CronParser;
import org.pentaho.gwt.widgets.client.utils.CronParser.RecurrenceType;
import org.pentaho.gwt.widgets.client.utils.EnumException;
import org.pentaho.gwt.widgets.client.utils.StringUtils;
import org.pentaho.gwt.widgets.client.utils.TimeUtil;
import org.pentaho.gwt.widgets.client.utils.TimeUtil.DayOfWeek;
import org.pentaho.gwt.widgets.client.utils.TimeUtil.MonthOfYear;
import org.pentaho.gwt.widgets.client.utils.TimeUtil.TimeOfDay;
import org.pentaho.gwt.widgets.client.utils.TimeUtil.WeekOfMonth;


/**
 * @author Steven Barkdull
 *
 */

@SuppressWarnings("deprecation")
public class RecurrenceEditor extends AbstractScheduleEditor {




  private TemporalValue temporalState = null;
  private DeckPanel deckPanel = null;
  

  public RecurrenceEditor() {
    super();
    this.setWidth("100%"); //$NON-NLS-1$

    Widget p = createStartTimePanel();
    add(p);

    p = createRecurrencePanel();
    add(p);

    add(getDateRangeEditor());
  }
  

  /**
   * 
   * @param recurrenceStr
   * @throws EnumException thrown if recurrenceTokens[0] is not a valid ScheduleType String.
   */
  public void inititalizeWithRecurrenceString( String recurrenceStr ) throws EnumException {
    String[] recurrenceTokens = recurrenceStr.split( "\\s" ); //$NON-NLS-1$
    
    setStartTime( recurrenceTokens[1], recurrenceTokens[2], recurrenceTokens[3] );
    
    RecurrenceType rt = RecurrenceType.stringToScheduleType( recurrenceTokens[0] );

    switch( rt ) {
      case EveryWeekday:
        setEveryWeekdayRecurrence( recurrenceTokens );
        break;
      case WeeklyOn:
        setWeeklyOnRecurrence( recurrenceTokens );
        break;
      case DayNOfMonth:
        setDayNOfMonthRecurrence( recurrenceTokens );
        break;
      case NthDayNameOfMonth:
        setNthDayNameOfMonthRecurrence( recurrenceTokens );
        break;
      case LastDayNameOfMonth:
        setLastDayNameOfMonthRecurrence( recurrenceTokens );
        break;
      case EveryMonthNameN:
        setEveryMonthNameNRecurrence( recurrenceTokens );
        break;
      case NthDayNameOfMonthName:
        setNthDayNameOfMonthNameRecurrence( recurrenceTokens );
        break;
      case LastDayNameOfMonthName:
        setLastDayNameOfMonthNameRecurrence( recurrenceTokens );
        break;
      default:
    }
  }
  
  private void setStartTime( String seconds, String minutes, String hours ) {
    TimeOfDay td = TimeUtil.getTimeOfDayBy0To23Hour( hours );
    int intHours = Integer.parseInt( hours );
    int intTwelveHour = TimeUtil.to12HourClock( intHours ); // returns 0..11

    TimePicker startTimePicker = getStartTimePicker();
    startTimePicker.setHour( Integer.toString( TimeUtil.map0Through11To12Through11( intTwelveHour ) ) );
    startTimePicker.setMinute( minutes );
    startTimePicker.setTimeOfDay( td );
  }
  
  private void setEveryWeekdayRecurrence( String[] recurrenceTokens ) {
    setTemporalState( TemporalValue.DAILY );
    getDailyEditor().setEveryWeekday();
  }
  
  private void setWeeklyOnRecurrence( String[] recurrenceTokens ) {
    setTemporalState( TemporalValue.WEEKLY );
    String days = recurrenceTokens[4];
    getWeeklyEditor().setCheckedDaysAsString( days, VALUE_OF_SUNDAY );
  }
  
  private void setDayNOfMonthRecurrence( String[] recurrenceTokens ) {
    setTemporalState( TemporalValue.MONTHLY );

    MonthlyRecurrenceEditor monthlyEditor = getMonthlyEditor();
    monthlyEditor.setDayNOfMonth();
    String dayNOfMonth = recurrenceTokens[4];
    monthlyEditor.setDayOfMonth( dayNOfMonth );
  }
  
  private void setNthDayNameOfMonthRecurrence( String[] recurrenceTokens ) {
    setTemporalState( TemporalValue.MONTHLY );

    MonthlyRecurrenceEditor monthlyEditor = getMonthlyEditor();
    monthlyEditor.setNthDayNameOfMonth();
    monthlyEditor.setWeekOfMonth( WeekOfMonth.get( Integer.parseInt( recurrenceTokens[5])-1 ) );
    monthlyEditor.setDayOfWeek( DayOfWeek.get( Integer.parseInt( recurrenceTokens[4])-1 ) );
  }
  
  private void setLastDayNameOfMonthRecurrence( String[] recurrenceTokens ) {
    setTemporalState( TemporalValue.MONTHLY );

    MonthlyRecurrenceEditor monthlyEditor = getMonthlyEditor();
    monthlyEditor.setNthDayNameOfMonth();
    monthlyEditor.setWeekOfMonth( WeekOfMonth.LAST );
    monthlyEditor.setDayOfWeek( DayOfWeek.get( Integer.parseInt( recurrenceTokens[4])-1 ) );
  }
  
  private void setEveryMonthNameNRecurrence( String[] recurrenceTokens ) {
    setTemporalState( TemporalValue.YEARLY );

    YearlyRecurrenceEditor yearlyEditor = getYearlyEditor();
    yearlyEditor.setEveryMonthOnNthDay();
    yearlyEditor.setDayOfMonth( recurrenceTokens[4] );
    yearlyEditor.setMonthOfYear0( MonthOfYear.get( Integer.parseInt( recurrenceTokens[5] )-1 ) );
  }
  
  private void setNthDayNameOfMonthNameRecurrence( String[] recurrenceTokens ) {
    setTemporalState( TemporalValue.YEARLY );

    YearlyRecurrenceEditor yearlyEditor = getYearlyEditor();
    yearlyEditor.setNthDayNameOfMonthName();
    yearlyEditor.setMonthOfYear1( MonthOfYear.get( Integer.parseInt( recurrenceTokens[6] )-1 ) );
    yearlyEditor.setWeekOfMonth( WeekOfMonth.get( Integer.parseInt( recurrenceTokens[5])-1 ) );
    yearlyEditor.setDayOfWeek( DayOfWeek.get( Integer.parseInt( recurrenceTokens[4])-1 ) );
  }
  
  private void setLastDayNameOfMonthNameRecurrence( String[] recurrenceTokens ) {
    setTemporalState( TemporalValue.YEARLY );

    YearlyRecurrenceEditor yearlyEditor = getYearlyEditor();
    yearlyEditor.setNthDayNameOfMonthName();
    yearlyEditor.setMonthOfYear1( MonthOfYear.get( Integer.parseInt( recurrenceTokens[5] )-1 ) );
    yearlyEditor.setWeekOfMonth( WeekOfMonth.LAST );
    yearlyEditor.setDayOfWeek( DayOfWeek.get( Integer.parseInt( recurrenceTokens[4])-1 ) );
  }
  
  /**
   * 
   * @param strRepeatInSecs
   */
  public void inititalizeWithRepeatInSecs( int repeatInSecs ) {

    TemporalValue currentVal;
    long repeatTime;
    if ( TimeUtil.isSecondsWholeDay( repeatInSecs ) ) {
      repeatTime = TimeUtil.secsToDays( repeatInSecs );
      currentVal = TemporalValue.DAILY;
      getDailyEditor().setRepeatValue( Long.toString( repeatTime ) );
    } else { 
      SimpleRecurrencePanel p = null;
      if ( TimeUtil.isSecondsWholeHour( repeatInSecs ) ) {
        repeatTime = TimeUtil.secsToHours( repeatInSecs );
        currentVal = TemporalValue.HOURS;
      } else if ( TimeUtil.isSecondsWholeMinute( repeatInSecs ) ) {
        repeatTime = TimeUtil.secsToMinutes( repeatInSecs );
        currentVal = TemporalValue.MINUTES;
      } else {
        // the repeat time is seconds
        repeatTime = repeatInSecs;
        currentVal = TemporalValue.SECONDS;
      }
      p = (SimpleRecurrencePanel)temporalPanelMap.get(currentVal);
      p.setValue( Long.toString( repeatTime ) );
    }
    setTemporalState( currentVal );
  }

  
  private Widget createStartTimePanel() {
    CaptionPanel startTimeGB = new CaptionPanel( MSGS.startTime() );
    startTimeGB.setStyleName(SCHEDULE_EDITOR_CAPTION_PANEL);
    
    startTimeGB.add(getStartTimePicker());

    return startTimeGB;
  }

  private Widget createRecurrencePanel() {

    CaptionPanel recurrenceGB = new CaptionPanel(MSGS.recurrencePattern() );
    recurrenceGB.setStyleName(SCHEDULE_EDITOR_CAPTION_PANEL);

    deckPanel = new DeckPanel();
    recurrenceGB.add(deckPanel);


    createTemporalMap();

    deckPanel.add(getSecondlyEditor());
    deckPanel.add(getMinutelyEditor());
    deckPanel.add(getHourlyEditor());
    
    deckPanel.add(getDailyEditor());
    deckPanel.add(getWeeklyEditor());
    deckPanel.add(getMonthlyEditor());
    deckPanel.add(getYearlyEditor());
    
    deckPanel.showWidget(0);

    return recurrenceGB;
  }

  private void createTemporalMap() {
    // must come after creation of temporal panels
    assert getDailyEditor() != null : "Temporal panels must be initialized before calling createTemporalCombo."; //$NON-NLS-1$

    temporalPanelMap.put( TemporalValue.SECONDS, getSecondlyEditor());
    temporalPanelMap.put( TemporalValue.MINUTES, getMinutelyEditor());
    temporalPanelMap.put( TemporalValue.HOURS, getHourlyEditor());
    temporalPanelMap.put( TemporalValue.DAILY, getDailyEditor());
    temporalPanelMap.put( TemporalValue.WEEKLY, getWeeklyEditor());
    temporalPanelMap.put( TemporalValue.MONTHLY, getMonthlyEditor());
    temporalPanelMap.put( TemporalValue.YEARLY, getYearlyEditor());
  }



  private void selectTemporalPanel(TemporalValue selectedTemporalValue) {
    int i = 0;
    for ( Map.Entry<TemporalValue, Panel> me : temporalPanelMap.entrySet() ) {
      if (me.getKey().equals( selectedTemporalValue )) {
        deckPanel.showWidget(i);
        break;
      }
      i++;
    }
  }
  
  /**
   * 
   * @return null if the selected schedule does not support repeat-in-seconds, otherwise
   * return the number of seconds between schedule execution.
   * @throws RuntimeException if the temporal value (tv) is invalid. This
   * condition occurs as a result of programmer error.
   */
  public Long getRepeatInSecs() throws RuntimeException {
    switch ( temporalState ) {
      case WEEKLY:
        // fall through
      case MONTHLY:
        // fall through
      case YEARLY:
        return null;
      case SECONDS:
        return Long.parseLong( getSecondlyEditor().getValue() );
      case MINUTES:
        return TimeUtil.minutesToSecs( Long.parseLong( getMinutelyEditor().getValue() ) );
      case HOURS:
        return TimeUtil.hoursToSecs( Long.parseLong( getHourlyEditor().getValue() ) );
      case DAILY:
        return TimeUtil.daysToSecs( Long.parseLong( getDailyEditor().getRepeatValue() ) );
      default:
        throw new RuntimeException( MSGS.invalidTemporalValueInGetRepeatInSecs( temporalState.toString() ) );
    }
  }

  /**
   * 
   * @return null if the selected schedule does not support CRON, otherwise
   * return the CRON string.
   * @throws RuntimeException if the temporal value (tv) is invalid. This
   * condition occurs as a result of programmer error.
   */
  public String getCronString() throws RuntimeException {
    switch ( temporalState ) {
      case SECONDS:
        // fall through
      case MINUTES:
        // fall through
      case HOURS:
        return null;
      case DAILY:
        return getDailyCronString();
      case WEEKLY:
        return getWeeklyCronString();
      case MONTHLY:
        return getMonthlyCronString();
      case YEARLY:
        return getYearlyCronString();
      default:
        throw new RuntimeException( MSGS.invalidTemporalValueInGetCronString( temporalState.toString() ) );
    }
  }
  
  public boolean isEveryNDays() {
    return (temporalState == TemporalValue.DAILY) && getDailyEditor().isEveryNDays();
  }
  
  public MonthOfYear getSelectedMonth() {
    MonthOfYear selectedMonth = null;
    YearlyRecurrenceEditor yearlyEditor = getYearlyEditor();
    if ((temporalState == TemporalValue.YEARLY) && yearlyEditor.isNthDayNameOfMonthName()) {
      selectedMonth = yearlyEditor.getMonthOfYear1();
    } else if ((temporalState == TemporalValue.YEARLY) && yearlyEditor.isEveryMonthOnNthDay()) {
      selectedMonth = yearlyEditor.getMonthOfYear0();
    }
    return selectedMonth;
  }
  
  public List<DayOfWeek> getSelectedDaysOfWeek() {
    ArrayList<DayOfWeek> selectedDaysOfWeek = new ArrayList<DayOfWeek>();
    if ((temporalState == TemporalValue.DAILY) && !getDailyEditor().isEveryNDays()) {
      selectedDaysOfWeek.add(DayOfWeek.MON);
      selectedDaysOfWeek.add(DayOfWeek.TUE);
      selectedDaysOfWeek.add(DayOfWeek.WED);
      selectedDaysOfWeek.add(DayOfWeek.THU);
      selectedDaysOfWeek.add(DayOfWeek.FRI);
    } else if (temporalState == TemporalValue.WEEKLY) {
      selectedDaysOfWeek.addAll(getWeeklyEditor().getCheckedDays());
    } else if ((temporalState == TemporalValue.MONTHLY) && getMonthlyEditor().isNthDayNameOfMonth()) {
      selectedDaysOfWeek.add(getMonthlyEditor().getDayOfWeek());
    } else if ((temporalState == TemporalValue.YEARLY) && getYearlyEditor().isNthDayNameOfMonthName()) {
      selectedDaysOfWeek.add(getYearlyEditor().getDayOfWeek());
    }
    return selectedDaysOfWeek;
  }
  
  public WeekOfMonth getSelectedWeekOfMonth() {
    WeekOfMonth selectedWeekOfMonth = null;
    if ((temporalState == TemporalValue.MONTHLY) && getMonthlyEditor().isNthDayNameOfMonth()) {
      selectedWeekOfMonth = getMonthlyEditor().getWeekOfMonth();
    } else if ((temporalState == TemporalValue.YEARLY) && getYearlyEditor().isNthDayNameOfMonthName()) {
      selectedWeekOfMonth = getYearlyEditor().getWeekOfMonth();
    }
    return selectedWeekOfMonth;
  }
  
  public Integer getSelectedDayOfMonth() {
    Integer selectedDayOfMonth = null;    
    if ((temporalState == TemporalValue.MONTHLY) && getMonthlyEditor().isDayNOfMonth()) {
      try {
        selectedDayOfMonth = Integer.parseInt(getMonthlyEditor().getDayOfMonth());
      } catch (Exception ex) {
        
      }
    } else if ((temporalState == TemporalValue.YEARLY) && getYearlyEditor().isEveryMonthOnNthDay()) {
      try {
        selectedDayOfMonth = Integer.parseInt(getYearlyEditor().getDayOfMonth());
      } catch (Exception ex) {
        
      }
    }
    return selectedDayOfMonth;
  }
  
  /**
   * 
   * @return
   * @throws RuntimeException
   */
  private String getDailyCronString() throws RuntimeException {
    String cronStr;
    StringBuilder recurrenceSb = new StringBuilder();
    if ( getDailyEditor().isEveryNDays() ) {
      return null;
    } else {
      // must be every weekday
      recurrenceSb.append( RecurrenceType.EveryWeekday ).append( SPACE )
        .append( getTimeOfRecurrence() );
      try {
        cronStr = CronParser.recurrenceStringToCronString( recurrenceSb.toString() );
      } catch (CronParseException e) {
        throw new RuntimeException( MSGS.invalidRecurrenceString( recurrenceSb.toString() ) );
      }
      return cronStr;
    }
  }
  
  private String getWeeklyCronString() throws RuntimeException {
    String cronStr;
    StringBuilder recurrenceSb = new StringBuilder();
    // WeeklyOn 0 33 6 1,3,5
    recurrenceSb.append( RecurrenceType.WeeklyOn ).append( SPACE )
      .append( getTimeOfRecurrence() ).append( SPACE )
      .append( getWeeklyEditor().getCheckedDaysAsString(VALUE_OF_SUNDAY) );
    try {
      cronStr = CronParser.recurrenceStringToCronString( recurrenceSb.toString() );
    } catch (CronParseException e) {
      throw new RuntimeException( MSGS.invalidRecurrenceString( recurrenceSb.toString() ) );
    }
    return cronStr;
    
  }
  
  private String getMonthlyCronString() throws RuntimeException {
    String cronStr;
    StringBuilder recurrenceSb = new StringBuilder();
    MonthlyRecurrenceEditor monthlyEditor = getMonthlyEditor();
    if ( monthlyEditor.isDayNOfMonth() ) {
      recurrenceSb.append( RecurrenceType.DayNOfMonth ).append( SPACE )
        .append( getTimeOfRecurrence() ).append( SPACE )
        .append( monthlyEditor.getDayOfMonth() );
    } else if ( monthlyEditor.isNthDayNameOfMonth() ) {
      if ( monthlyEditor.getWeekOfMonth() != WeekOfMonth.LAST ) {
        String weekOfMonth = Integer.toString( monthlyEditor.getWeekOfMonth().value() + 1 );
        String dayOfWeek = Integer.toString( monthlyEditor.getDayOfWeek().value() + 1 );
        recurrenceSb.append( RecurrenceType.NthDayNameOfMonth ).append( SPACE )
          .append( getTimeOfRecurrence() ).append( SPACE )
          .append( dayOfWeek ).append( SPACE )
          .append( weekOfMonth );
      } else {
        String dayOfWeek = Integer.toString( monthlyEditor.getDayOfWeek().value() + 1 );
        recurrenceSb.append( RecurrenceType.LastDayNameOfMonth ).append( SPACE )
          .append( getTimeOfRecurrence() ).append( SPACE )
          .append( dayOfWeek );
      }
    } else {
      throw new RuntimeException( MSGS.noRadioBtnsSelected() );
    }
    try {
      cronStr = CronParser.recurrenceStringToCronString( recurrenceSb.toString() );
    } catch (CronParseException e) {
      throw new RuntimeException(  MSGS.invalidRecurrenceString( recurrenceSb.toString() ) );
    }
    return cronStr;
  }
  
  private String getYearlyCronString() throws RuntimeException {
    String cronStr;
    StringBuilder recurrenceSb = new StringBuilder();
    YearlyRecurrenceEditor yearlyEditor = getYearlyEditor();
    if ( yearlyEditor.isEveryMonthOnNthDay() ) {
      String monthOfYear = Integer.toString( yearlyEditor.getMonthOfYear0().value() + 1 );
      recurrenceSb.append( RecurrenceType.EveryMonthNameN ).append( SPACE )
      .append( getTimeOfRecurrence() ).append( SPACE )
      .append( yearlyEditor.getDayOfMonth() ).append( SPACE )
      .append( monthOfYear );
    } else if ( yearlyEditor.isNthDayNameOfMonthName() ) {
      if ( yearlyEditor.getWeekOfMonth() != WeekOfMonth.LAST ) {
        String monthOfYear = Integer.toString( yearlyEditor.getMonthOfYear1().value() + 1 );
        String dayOfWeek = Integer.toString( yearlyEditor.getDayOfWeek().value() + 1 );
        String weekOfMonth = Integer.toString( yearlyEditor.getWeekOfMonth().value() + 1 );
        recurrenceSb.append( RecurrenceType.NthDayNameOfMonthName ).append( SPACE )
          .append( getTimeOfRecurrence() ).append( SPACE )
          .append( dayOfWeek ).append( SPACE )
          .append( weekOfMonth ).append( SPACE )
          .append( monthOfYear );
      } else {
        String monthOfYear = Integer.toString( yearlyEditor.getMonthOfYear1().value() + 1 );
        String dayOfWeek = Integer.toString( yearlyEditor.getDayOfWeek().value() + 1 );
        recurrenceSb.append( RecurrenceType.LastDayNameOfMonthName ).append( SPACE )
          .append( getTimeOfRecurrence() ).append( SPACE )
          .append( dayOfWeek ).append( SPACE )
          .append( monthOfYear );
      }
    } else {
      throw new RuntimeException( MSGS.noRadioBtnsSelected() );
    }
    try {
      cronStr = CronParser.recurrenceStringToCronString( recurrenceSb.toString() );
    } catch (CronParseException e) {
      throw new RuntimeException(  MSGS.invalidRecurrenceString( recurrenceSb.toString() ) );
    }
    return cronStr;
  }
  
  private StringBuilder getTimeOfRecurrence() {
    TimePicker startTimePicker = getStartTimePicker();
    int timeOfDayAdjust = ( startTimePicker.getTimeOfDay().equals( TimeUtil.TimeOfDay.AM ) )
      ? TimeUtil.MIN_HOUR   // 0
      : TimeUtil.MAX_HOUR;  // 12
    String strHour = StringUtils.addStringToInt( startTimePicker.getHour(), timeOfDayAdjust );
    return new StringBuilder().append( "00" ).append( SPACE ) //$NON-NLS-1$
    .append( startTimePicker.getMinute() ).append( SPACE )
    .append( strHour );
  }

  // TODO sbarkdull
  //private static DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
  
  public void setStartTime( String startTime ) {
    getStartTimePicker().setTime(startTime);
  }
  
  public String getStartTime() {
    return getStartTimePicker().getTime();
  }
  
  public void setStartDate( Date startDate ) {
    getDateRangeEditor().setStartDate(startDate);
  }
  
  public Date getStartDate() {
    return getDateRangeEditor().getStartDate();
  }
  
  public void setEndDate( Date endDate ) {
    getDateRangeEditor().setEndDate(endDate);
  }
  
  public Date getEndDate() {
    return getDateRangeEditor().getEndDate();
  }
  
  public void setNoEndDate() {
    getDateRangeEditor().setNoEndDate();
  }
  
  public void setEndBy() {
    getDateRangeEditor().setEndBy();
  }
  
  public TemporalValue getTemporalState() {
    return temporalState;
  }

  public void setTemporalState(TemporalValue temporalState) {
    this.temporalState = temporalState;
    selectTemporalPanel( temporalState );
  }

  protected void changeHandler() {
    if ( null != getOnChangeHandler() ) {
      getOnChangeHandler().onHandle(this);
    }
  }
}
