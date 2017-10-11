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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.mantle.client.dialogs.scheduling;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.gwt.widgets.client.controls.DateRangeEditor;
import org.pentaho.gwt.widgets.client.controls.TimePicker;
import org.pentaho.gwt.widgets.client.utils.CronParser;
import org.pentaho.gwt.widgets.client.utils.TimeUtil;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class RecurrenceEditorTest {
  private RecurrenceEditor recurrenceEditor;

  @Before
  public void setUp() throws Exception {
    recurrenceEditor = mock( RecurrenceEditor.class );
    recurrenceEditor.startTimePicker = mock( TimePicker.class );
  }

  @Test
  public void testReset() throws Exception {
    doCallRealMethod().when( recurrenceEditor ).reset( any( Date.class ) );

    recurrenceEditor.dateRangeEditor = mock( DateRangeEditor.class );
    recurrenceEditor.secondlyEditor = mock( RecurrenceEditor.SecondlyRecurrenceEditor.class );
    recurrenceEditor.minutelyEditor = mock( RecurrenceEditor.MinutelyRecurrenceEditor.class );
    recurrenceEditor.hourlyEditor = mock( RecurrenceEditor.HourlyRecurrenceEditor.class );
    recurrenceEditor.dailyEditor = mock( RecurrenceEditor.DailyRecurrenceEditor.class );
    recurrenceEditor.weeklyEditor = mock( RecurrenceEditor.WeeklyRecurrenceEditor.class );
    recurrenceEditor.monthlyEditor = mock( RecurrenceEditor.MonthlyRecurrenceEditor.class );
    recurrenceEditor.yearlyEditor = mock( RecurrenceEditor.YearlyRecurrenceEditor.class );

    final Date date = new Date();
    recurrenceEditor.reset( date );
    verify( recurrenceEditor.startTimePicker ).setHour( "12" );
    verify( recurrenceEditor.startTimePicker ).setMinute( "00" );
    verify( recurrenceEditor.startTimePicker ).setTimeOfDay( TimeUtil.TimeOfDay.AM );
    verify( recurrenceEditor.dateRangeEditor ).reset( date );
    verify( recurrenceEditor.secondlyEditor ).reset();
    verify( recurrenceEditor.minutelyEditor ).reset();
    verify( recurrenceEditor.hourlyEditor ).reset();
    verify( recurrenceEditor.dailyEditor ).reset();
    verify( recurrenceEditor.weeklyEditor ).reset();
    verify( recurrenceEditor.monthlyEditor ).reset();
    verify( recurrenceEditor.yearlyEditor ).reset();
  }

  @Test
  public void testInititalizeWithRecurrenceString_EveryWeekday() throws Exception {
    doCallRealMethod().when( recurrenceEditor ).inititalizeWithRecurrenceString( anyString() );

    recurrenceEditor.dailyEditor = mock( RecurrenceEditor.DailyRecurrenceEditor.class );

    final String recurrenceStr = CronParser.RecurrenceType.EveryWeekday + " 0 33 18";
    recurrenceEditor.inititalizeWithRecurrenceString( recurrenceStr );
    verify( recurrenceEditor.startTimePicker ).setHour( "6" );
    verify( recurrenceEditor.startTimePicker ).setMinute( "33" );
    verify( recurrenceEditor.startTimePicker ).setTimeOfDay( TimeUtil.TimeOfDay.PM );
    verify( recurrenceEditor ).setTemporalState( RecurrenceEditor.TemporalValue.DAILY );
    verify( recurrenceEditor.dailyEditor ).setEveryWeekday();
  }

  @Test
  public void testInititalizeWithRecurrenceString_WeeklyOn() throws Exception {
    doCallRealMethod().when( recurrenceEditor ).inititalizeWithRecurrenceString( anyString() );

    recurrenceEditor.weeklyEditor = mock( RecurrenceEditor.WeeklyRecurrenceEditor.class );

    final String recurrenceStr = CronParser.RecurrenceType.WeeklyOn + " 0 33 18 5";
    recurrenceEditor.inititalizeWithRecurrenceString( recurrenceStr );
    verify( recurrenceEditor.startTimePicker ).setHour( "6" );
    verify( recurrenceEditor.startTimePicker ).setMinute( "33" );
    verify( recurrenceEditor.startTimePicker ).setTimeOfDay( TimeUtil.TimeOfDay.PM );
    verify( recurrenceEditor ).setTemporalState( RecurrenceEditor.TemporalValue.WEEKLY );
    verify( recurrenceEditor.weeklyEditor ).setCheckedDaysAsString( "5", RecurrenceEditor.VALUE_OF_SUNDAY );
  }

  @Test
  public void testInititalizeWithRecurrenceString_DayNOfMonth() throws Exception {
    doCallRealMethod().when( recurrenceEditor ).inititalizeWithRecurrenceString( anyString() );

    recurrenceEditor.monthlyEditor = mock( RecurrenceEditor.MonthlyRecurrenceEditor.class );

    final String recurrenceStr = CronParser.RecurrenceType.DayNOfMonth + " 0 33 18 5";
    recurrenceEditor.inititalizeWithRecurrenceString( recurrenceStr );
    verify( recurrenceEditor.startTimePicker ).setHour( "6" );
    verify( recurrenceEditor.startTimePicker ).setMinute( "33" );
    verify( recurrenceEditor.startTimePicker ).setTimeOfDay( TimeUtil.TimeOfDay.PM );
    verify( recurrenceEditor ).setTemporalState( RecurrenceEditor.TemporalValue.MONTHLY );
    verify( recurrenceEditor.monthlyEditor ).setDayNOfMonth();
    verify( recurrenceEditor.monthlyEditor ).setDayOfMonth( "5" );
  }

  @Test
  public void testInititalizeWithRecurrenceString_NthDayNameOfMonth() throws Exception {
    doCallRealMethod().when( recurrenceEditor ).inititalizeWithRecurrenceString( anyString() );

    recurrenceEditor.monthlyEditor = mock( RecurrenceEditor.MonthlyRecurrenceEditor.class );

    final String recurrenceStr = CronParser.RecurrenceType.NthDayNameOfMonth + " 0 33 18 5 1";
    recurrenceEditor.inititalizeWithRecurrenceString( recurrenceStr );
    verify( recurrenceEditor.startTimePicker ).setHour( "6" );
    verify( recurrenceEditor.startTimePicker ).setMinute( "33" );
    verify( recurrenceEditor.startTimePicker ).setTimeOfDay( TimeUtil.TimeOfDay.PM );
    verify( recurrenceEditor ).setTemporalState( RecurrenceEditor.TemporalValue.MONTHLY );
    verify( recurrenceEditor.monthlyEditor ).setNthDayNameOfMonth();
    verify( recurrenceEditor.monthlyEditor ).setWeekOfMonth( TimeUtil.WeekOfMonth.FIRST );
    verify( recurrenceEditor.monthlyEditor ).setDayOfWeek( TimeUtil.DayOfWeek.THU );
  }

  @Test
  public void testInititalizeWithRecurrenceString_LastDayNameOfMonth() throws Exception {
    doCallRealMethod().when( recurrenceEditor ).inititalizeWithRecurrenceString( anyString() );

    recurrenceEditor.monthlyEditor = mock( RecurrenceEditor.MonthlyRecurrenceEditor.class );

    final String recurrenceStr = CronParser.RecurrenceType.LastDayNameOfMonth + " 0 33 18 5";
    recurrenceEditor.inititalizeWithRecurrenceString( recurrenceStr );
    verify( recurrenceEditor.startTimePicker ).setHour( "6" );
    verify( recurrenceEditor.startTimePicker ).setMinute( "33" );
    verify( recurrenceEditor.startTimePicker ).setTimeOfDay( TimeUtil.TimeOfDay.PM );
    verify( recurrenceEditor ).setTemporalState( RecurrenceEditor.TemporalValue.MONTHLY );
    verify( recurrenceEditor.monthlyEditor ).setNthDayNameOfMonth();
    verify( recurrenceEditor.monthlyEditor ).setWeekOfMonth( TimeUtil.WeekOfMonth.LAST );
    verify( recurrenceEditor.monthlyEditor ).setDayOfWeek( TimeUtil.DayOfWeek.THU );
  }

  @Test
  public void testInititalizeWithRecurrenceString_EveryMonthNameN() throws Exception {
    doCallRealMethod().when( recurrenceEditor ).inititalizeWithRecurrenceString( anyString() );

    recurrenceEditor.yearlyEditor = mock( RecurrenceEditor.YearlyRecurrenceEditor.class );

    final String recurrenceStr = CronParser.RecurrenceType.EveryMonthNameN + " 0 33 18 5 1";
    recurrenceEditor.inititalizeWithRecurrenceString( recurrenceStr );
    verify( recurrenceEditor.startTimePicker ).setHour( "6" );
    verify( recurrenceEditor.startTimePicker ).setMinute( "33" );
    verify( recurrenceEditor.startTimePicker ).setTimeOfDay( TimeUtil.TimeOfDay.PM );
    verify( recurrenceEditor ).setTemporalState( RecurrenceEditor.TemporalValue.YEARLY );
    verify( recurrenceEditor.yearlyEditor ).setEveryMonthOnNthDay();
    verify( recurrenceEditor.yearlyEditor ).setDayOfMonth( "5" );
    verify( recurrenceEditor.yearlyEditor ).setMonthOfYear0( TimeUtil.MonthOfYear.JAN );
  }

  @Test
  public void testInititalizeWithRecurrenceString_NthDayNameOfMonthName() throws Exception {
    doCallRealMethod().when( recurrenceEditor ).inititalizeWithRecurrenceString( anyString() );

    recurrenceEditor.yearlyEditor = mock( RecurrenceEditor.YearlyRecurrenceEditor.class );

    final String recurrenceStr = CronParser.RecurrenceType.NthDayNameOfMonthName + " 0 33 18 5 1 2";
    recurrenceEditor.inititalizeWithRecurrenceString( recurrenceStr );
    verify( recurrenceEditor.startTimePicker ).setHour( "6" );
    verify( recurrenceEditor.startTimePicker ).setMinute( "33" );
    verify( recurrenceEditor.startTimePicker ).setTimeOfDay( TimeUtil.TimeOfDay.PM );
    verify( recurrenceEditor ).setTemporalState( RecurrenceEditor.TemporalValue.YEARLY );
    verify( recurrenceEditor.yearlyEditor ).setNthDayNameOfMonthName();
    verify( recurrenceEditor.yearlyEditor ).setMonthOfYear1( TimeUtil.MonthOfYear.FEB );
    verify( recurrenceEditor.yearlyEditor ).setWeekOfMonth( TimeUtil.WeekOfMonth.FIRST );
    verify( recurrenceEditor.yearlyEditor ).setDayOfWeek( TimeUtil.DayOfWeek.THU );
  }

  @Test
  public void testInititalizeWithRecurrenceString_LastDayNameOfMonthName() throws Exception {
    doCallRealMethod().when( recurrenceEditor ).inititalizeWithRecurrenceString( anyString() );

    recurrenceEditor.yearlyEditor = mock( RecurrenceEditor.YearlyRecurrenceEditor.class );

    final String recurrenceStr = CronParser.RecurrenceType.LastDayNameOfMonthName + " 0 33 18 5 1";
    recurrenceEditor.inititalizeWithRecurrenceString( recurrenceStr );
    verify( recurrenceEditor.startTimePicker ).setHour( "6" );
    verify( recurrenceEditor.startTimePicker ).setMinute( "33" );
    verify( recurrenceEditor.startTimePicker ).setTimeOfDay( TimeUtil.TimeOfDay.PM );
    verify( recurrenceEditor ).setTemporalState( RecurrenceEditor.TemporalValue.YEARLY );
    verify( recurrenceEditor.yearlyEditor ).setNthDayNameOfMonthName();
    verify( recurrenceEditor.yearlyEditor ).setMonthOfYear1( TimeUtil.MonthOfYear.JAN );
    verify( recurrenceEditor.yearlyEditor ).setWeekOfMonth( TimeUtil.WeekOfMonth.LAST );
    verify( recurrenceEditor.yearlyEditor ).setDayOfWeek( TimeUtil.DayOfWeek.THU );
  }

  @Test
  public void testInititalizeWithRepeatInSecs() throws Exception {
    doCallRealMethod().when( recurrenceEditor ).inititalizeWithRepeatInSecs( anyInt() );

    recurrenceEditor.dailyEditor = mock( RecurrenceEditor.DailyRecurrenceEditor.class );
    recurrenceEditor.temporalPanelMap = mock( Map.class );

    final int count = 2;
    recurrenceEditor.inititalizeWithRepeatInSecs( count * TimeUtil.HOURS_IN_DAY * TimeUtil.MINUTES_IN_HOUR
        * TimeUtil.SECONDS_IN_MINUTE );
    verify( recurrenceEditor.dailyEditor ).setRepeatValue( String.valueOf( count ) );
    verify( recurrenceEditor ).setTemporalState( RecurrenceEditor.TemporalValue.DAILY );

    final RecurrenceEditor.SimpleRecurrencePanel recurrencePanel = mock( RecurrenceEditor.SimpleRecurrencePanel.class );
    when( recurrenceEditor.temporalPanelMap.get( RecurrenceEditor.TemporalValue.HOURS ) ).
        thenReturn( recurrencePanel );
    recurrenceEditor.inititalizeWithRepeatInSecs( count * TimeUtil.MINUTES_IN_HOUR
        * TimeUtil.SECONDS_IN_MINUTE );
    verify( recurrencePanel ).setValue( String.valueOf( count ) );
    verify( recurrenceEditor ).setTemporalState( RecurrenceEditor.TemporalValue.HOURS );

    when( recurrenceEditor.temporalPanelMap.get( RecurrenceEditor.TemporalValue.MINUTES ) ).
        thenReturn( recurrencePanel );
    recurrenceEditor.inititalizeWithRepeatInSecs( count * TimeUtil.SECONDS_IN_MINUTE );
    verify( recurrencePanel, times( 2 ) ).setValue( String.valueOf( count ) );
    verify( recurrenceEditor ).setTemporalState( RecurrenceEditor.TemporalValue.MINUTES );

    when( recurrenceEditor.temporalPanelMap.get( RecurrenceEditor.TemporalValue.SECONDS ) ).
        thenReturn( recurrencePanel );
    recurrenceEditor.inititalizeWithRepeatInSecs( count );
    verify( recurrencePanel, times( 3 ) ).setValue( String.valueOf( count ) );
    verify( recurrenceEditor ).setTemporalState( RecurrenceEditor.TemporalValue.SECONDS );
  }

  @Test
  public void testGetRepeatInSecs() throws Exception {
    doCallRealMethod().when( recurrenceEditor ).getRepeatInSecs();

    recurrenceEditor.secondlyEditor = mock( RecurrenceEditor.SecondlyRecurrenceEditor.class );
    final Long secondlyEditorValue = 22222l;
    when( recurrenceEditor.secondlyEditor.getValue() ).thenReturn( secondlyEditorValue.toString() );
    recurrenceEditor.minutelyEditor = mock( RecurrenceEditor.MinutelyRecurrenceEditor.class );
    final Long minutelyEditorValue = 333333l;
    when( recurrenceEditor.minutelyEditor.getValue() ).thenReturn( minutelyEditorValue.toString() );
    recurrenceEditor.hourlyEditor = mock( RecurrenceEditor.HourlyRecurrenceEditor.class );
    final Long hourlyEditorValue = 4444444l;
    when( recurrenceEditor.hourlyEditor.getValue() ).thenReturn( hourlyEditorValue.toString() );
    recurrenceEditor.dailyEditor = mock( RecurrenceEditor.DailyRecurrenceEditor.class );
    final Long dailyEditorValue = 55555555l;
    when( recurrenceEditor.dailyEditor.getRepeatValue() ).thenReturn( dailyEditorValue.toString() );

    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.WEEKLY;
    assertNull( recurrenceEditor.getRepeatInSecs() );
    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.MONTHLY;
    assertNull( recurrenceEditor.getRepeatInSecs() );
    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.YEARLY;
    assertNull( recurrenceEditor.getRepeatInSecs() );

    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.SECONDS;
    assertEquals( secondlyEditorValue, recurrenceEditor.getRepeatInSecs() );

    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.MINUTES;
    assertEquals( 19999980l, recurrenceEditor.getRepeatInSecs().longValue() );

    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.HOURS;
    assertEquals( 15999998400l, recurrenceEditor.getRepeatInSecs().longValue() );

    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.DAILY;
    assertEquals( 4799999952000l, recurrenceEditor.getRepeatInSecs().longValue() );

    recurrenceEditor.temporalState = null;
    try {
      recurrenceEditor.getRepeatInSecs();
      fail();
    } catch ( RuntimeException e ) {
      // expected
    }
  }

  @Test
  public void testGetCronString() throws Exception {
    doCallRealMethod().when( recurrenceEditor ).getCronString();

    final String dailyCronString = "dailyCronString";
    when( recurrenceEditor.getDailyCronString() ).thenReturn( dailyCronString );
    final String weeklyCronString = "weeklyCronString";
    when( recurrenceEditor.getWeeklyCronString() ).thenReturn( weeklyCronString );
    final String monthlyCronString = "monthlyCronString";
    when( recurrenceEditor.getMonthlyCronString() ).thenReturn( monthlyCronString );
    final String yearlyCronString = "yearlyCronString";
    when( recurrenceEditor.getYearlyCronString() ).thenReturn( yearlyCronString );

    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.SECONDS;
    assertNull( recurrenceEditor.getCronString() );
    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.MINUTES;
    assertNull( recurrenceEditor.getCronString() );
    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.HOURS;
    assertNull( recurrenceEditor.getCronString() );

    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.DAILY;
    assertEquals( dailyCronString, recurrenceEditor.getCronString() );

    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.WEEKLY;
    assertEquals( weeklyCronString, recurrenceEditor.getCronString() );

    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.MONTHLY;
    assertEquals( monthlyCronString, recurrenceEditor.getCronString() );

    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.YEARLY;
    assertEquals( yearlyCronString, recurrenceEditor.getCronString() );
  }

  @Test
  public void testIsEveryNDays() throws Exception {
    doCallRealMethod().when( recurrenceEditor ).isEveryNDays();

    recurrenceEditor.dailyEditor = mock( RecurrenceEditor.DailyRecurrenceEditor.class );

    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.YEARLY;
    assertFalse( recurrenceEditor.isEveryNDays() );
    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.MONTHLY;
    assertFalse( recurrenceEditor.isEveryNDays() );
    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.WEEKLY;
    assertFalse( recurrenceEditor.isEveryNDays() );
    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.HOURS;
    assertFalse( recurrenceEditor.isEveryNDays() );
    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.MINUTES;
    assertFalse( recurrenceEditor.isEveryNDays() );
    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.SECONDS;
    assertFalse( recurrenceEditor.isEveryNDays() );

    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.DAILY;
    when( recurrenceEditor.dailyEditor.isEveryNDays() ).thenReturn( false );
    assertFalse( recurrenceEditor.isEveryNDays() );

    when( recurrenceEditor.dailyEditor.isEveryNDays() ).thenReturn( true );
    assertTrue( recurrenceEditor.isEveryNDays() );
  }

  @Test
  public void testGetSelectedMonth() throws Exception {
    doCallRealMethod().when( recurrenceEditor ).getSelectedMonth();

    recurrenceEditor.yearlyEditor = mock( RecurrenceEditor.YearlyRecurrenceEditor.class );
    final TimeUtil.MonthOfYear monthOfYear1 = TimeUtil.MonthOfYear.APR;
    when( recurrenceEditor.yearlyEditor.getMonthOfYear1() ).thenReturn( monthOfYear1 );
    final TimeUtil.MonthOfYear monthOfYear0 = TimeUtil.MonthOfYear.NOV;
    when( recurrenceEditor.yearlyEditor.getMonthOfYear0() ).thenReturn( monthOfYear0 );

    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.DAILY;
    assertNull( recurrenceEditor.getSelectedMonth() );
    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.MONTHLY;
    assertNull( recurrenceEditor.getSelectedMonth() );
    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.WEEKLY;
    assertNull( recurrenceEditor.getSelectedMonth() );
    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.HOURS;
    assertNull( recurrenceEditor.getSelectedMonth() );
    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.MINUTES;
    assertNull( recurrenceEditor.getSelectedMonth() );
    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.SECONDS;
    assertNull( recurrenceEditor.getSelectedMonth() );

    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.YEARLY;
    when( recurrenceEditor.yearlyEditor.isNthDayNameOfMonthName() ).thenReturn( true );
    assertEquals( monthOfYear1, recurrenceEditor.getSelectedMonth() );

    when( recurrenceEditor.yearlyEditor.isNthDayNameOfMonthName() ).thenReturn( false );
    assertNull( recurrenceEditor.getSelectedMonth() );

    when( recurrenceEditor.yearlyEditor.isEveryMonthOnNthDay() ).thenReturn( true );
    assertEquals( monthOfYear0, recurrenceEditor.getSelectedMonth() );
  }

  @Test
  public void testGetSelectedDaysOfWeek() throws Exception {
    doCallRealMethod().when( recurrenceEditor ).getSelectedDaysOfWeek();

    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.DAILY;
    recurrenceEditor.dailyEditor = mock( RecurrenceEditor.DailyRecurrenceEditor.class );
    when( recurrenceEditor.dailyEditor.isEveryNDays() ).thenReturn( false );
    List<TimeUtil.DayOfWeek> selectedDaysOfWeek = recurrenceEditor.getSelectedDaysOfWeek();

    assertEquals( 5, selectedDaysOfWeek.size() );
    assertTrue( selectedDaysOfWeek.contains( TimeUtil.DayOfWeek.MON ) );
    assertTrue( selectedDaysOfWeek.contains( TimeUtil.DayOfWeek.TUE ) );
    assertTrue( selectedDaysOfWeek.contains( TimeUtil.DayOfWeek.WED ) );
    assertTrue( selectedDaysOfWeek.contains( TimeUtil.DayOfWeek.THU ) );
    assertTrue( selectedDaysOfWeek.contains( TimeUtil.DayOfWeek.FRI ) );

    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.WEEKLY;
    recurrenceEditor.weeklyEditor = mock( RecurrenceEditor.WeeklyRecurrenceEditor.class );
    when( recurrenceEditor.weeklyEditor.getCheckedDays() ).thenReturn( new LinkedList<TimeUtil.DayOfWeek>()  { {
        add( TimeUtil.DayOfWeek.MON );
        add( TimeUtil.DayOfWeek.FRI );
        add( TimeUtil.DayOfWeek.SUN );
      } } );
    selectedDaysOfWeek = recurrenceEditor.getSelectedDaysOfWeek();
    assertEquals( 3, selectedDaysOfWeek.size() );
    assertTrue( selectedDaysOfWeek.contains( TimeUtil.DayOfWeek.MON ) );
    assertTrue( selectedDaysOfWeek.contains( TimeUtil.DayOfWeek.FRI ) );
    assertTrue( selectedDaysOfWeek.contains( TimeUtil.DayOfWeek.SUN ) );

    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.MONTHLY;
    recurrenceEditor.monthlyEditor = mock( RecurrenceEditor.MonthlyRecurrenceEditor.class );
    when( recurrenceEditor.monthlyEditor.isNthDayNameOfMonth() ).thenReturn( true );
    final TimeUtil.DayOfWeek dayOfWeek = TimeUtil.DayOfWeek.SAT;
    when( recurrenceEditor.monthlyEditor.getDayOfWeek() ).thenReturn( dayOfWeek );
    selectedDaysOfWeek = recurrenceEditor.getSelectedDaysOfWeek();
    assertEquals( 1, selectedDaysOfWeek.size() );
    assertTrue( selectedDaysOfWeek.contains( dayOfWeek ) );

    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.YEARLY;
    recurrenceEditor.yearlyEditor = mock( RecurrenceEditor.YearlyRecurrenceEditor.class );
    when( recurrenceEditor.yearlyEditor.isNthDayNameOfMonthName() ).thenReturn( true );
    when( recurrenceEditor.yearlyEditor.getDayOfWeek() ).thenReturn( dayOfWeek );
    selectedDaysOfWeek = recurrenceEditor.getSelectedDaysOfWeek();
    assertEquals( 1, selectedDaysOfWeek.size() );
    assertTrue( selectedDaysOfWeek.contains( dayOfWeek ) );
  }

  @Test
  public void testGetSelectedWeekOfMonth() throws Exception {
    doCallRealMethod().when( recurrenceEditor ).getSelectedWeekOfMonth();

    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.MONTHLY;
    recurrenceEditor.monthlyEditor = mock( RecurrenceEditor.MonthlyRecurrenceEditor.class );
    when( recurrenceEditor.monthlyEditor.isNthDayNameOfMonth() ).thenReturn( true );
    final TimeUtil.WeekOfMonth weekOfMonth = TimeUtil.WeekOfMonth.SECOND;
    when( recurrenceEditor.monthlyEditor.getWeekOfMonth() ).thenReturn( weekOfMonth );
    assertEquals( weekOfMonth, recurrenceEditor.getSelectedWeekOfMonth() );

    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.YEARLY;
    recurrenceEditor.yearlyEditor = mock( RecurrenceEditor.YearlyRecurrenceEditor.class );
    when( recurrenceEditor.yearlyEditor.isNthDayNameOfMonthName() ).thenReturn( true );
    when( recurrenceEditor.yearlyEditor.getWeekOfMonth() ).thenReturn( weekOfMonth );
    assertEquals( weekOfMonth, recurrenceEditor.getSelectedWeekOfMonth() );
  }

  @Test
  public void testGetSelectedDayOfMonth() throws Exception {
    doCallRealMethod().when( recurrenceEditor ).getSelectedDayOfMonth();

    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.MONTHLY;
    recurrenceEditor.monthlyEditor = mock( RecurrenceEditor.MonthlyRecurrenceEditor.class );
    when( recurrenceEditor.monthlyEditor.isDayNOfMonth() ).thenReturn( true );
    final Integer dayOfMonth = 18;
    when( recurrenceEditor.monthlyEditor.getDayOfMonth() ).thenReturn( dayOfMonth.toString() );
    assertEquals( dayOfMonth, recurrenceEditor.getSelectedDayOfMonth() );

    recurrenceEditor.temporalState = RecurrenceEditor.TemporalValue.YEARLY;
    recurrenceEditor.yearlyEditor = mock( RecurrenceEditor.YearlyRecurrenceEditor.class );
    when( recurrenceEditor.yearlyEditor.isEveryMonthOnNthDay() ).thenReturn( true );
    when( recurrenceEditor.yearlyEditor.getDayOfMonth() ).thenReturn( dayOfMonth.toString() );
    assertEquals( dayOfMonth, recurrenceEditor.getSelectedDayOfMonth() );
  }
}
