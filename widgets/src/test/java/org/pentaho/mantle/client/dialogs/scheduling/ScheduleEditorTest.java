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

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.gwt.widgets.client.controls.TimePicker;
import org.pentaho.gwt.widgets.client.utils.CronParseException;
import org.pentaho.gwt.widgets.client.utils.TimeUtil;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith( GwtMockitoTestRunner.class )
public class ScheduleEditorTest {
  private ScheduleEditor scheduleEditor;

  @Before
  public void setUp() throws Exception {
    scheduleEditor = mock( ScheduleEditor.class );
  }

  @Test
  public void testGetDurationValues() throws Exception {
    doCallRealMethod().when( scheduleEditor ).getDurationValues();

    final int daysListBoxItemText = 1;
    final int hoursListBoxItemText = 2;
    final int minutesListBoxItemText = 3;

    // block

    final String block = Style.Display.BLOCK.getCssName();
    scheduleEditor.daysListBox = prepareLB( daysListBoxItemText, block );
    scheduleEditor.hoursListBox = prepareLB( hoursListBoxItemText, block );
    scheduleEditor.minutesListBox = prepareLB( minutesListBoxItemText, block );

    ScheduleEditor.DurationValues durationValues = scheduleEditor.getDurationValues();

    assertEquals( daysListBoxItemText, durationValues.days );
    assertEquals( hoursListBoxItemText, durationValues.hours );
    assertEquals( minutesListBoxItemText, durationValues.minutes );

    // none

    final String none = Style.Display.NONE.getCssName();
    scheduleEditor.daysListBox = prepareLB( daysListBoxItemText, none );
    scheduleEditor.hoursListBox = prepareLB( hoursListBoxItemText, none );
    scheduleEditor.minutesListBox = prepareLB( minutesListBoxItemText, none );

    durationValues = scheduleEditor.getDurationValues();

    assertEquals( 0, durationValues.days );
    assertEquals( 0, durationValues.hours );
    assertEquals( 0, durationValues.minutes );
  }

  @Test
  public void testSetDurationFields() throws Exception {
    doCallRealMethod().when( scheduleEditor ).setDurationFields( anyLong() );

    scheduleEditor.daysListBox = mock( ListBox.class );
    scheduleEditor.hoursListBox = mock( ListBox.class );
    scheduleEditor.minutesListBox = mock( ListBox.class );
    scheduleEditor.blockoutEndTimePicker = mock( TimePicker.class );

    int days = 2;
    int hours = 15;
    int minutes = 20;
    scheduleEditor.setDurationFields( days * ScheduleEditor.TIME.DAY.getTime()
        + hours * ScheduleEditor.TIME.HOUR.getTime() + minutes * ScheduleEditor.TIME.MINUTE.getTime() );
    verify( scheduleEditor.daysListBox ).setSelectedIndex( days );
    verify( scheduleEditor.hoursListBox ).setSelectedIndex( hours );
    verify( scheduleEditor.minutesListBox ).setSelectedIndex( minutes );
    verify( scheduleEditor.blockoutEndTimePicker, never() ).setHour( anyInt() );

    scheduleEditor.setDurationFields( hours * ScheduleEditor.TIME.HOUR.getTime()
        + minutes * ScheduleEditor.TIME.MINUTE.getTime() );
    verify( scheduleEditor.daysListBox ).setSelectedIndex( 0 );
    verify( scheduleEditor.hoursListBox, times( 2 ) ).setSelectedIndex( hours );
    verify( scheduleEditor.minutesListBox, times( 2 ) ).setSelectedIndex( minutes );
    verify( scheduleEditor.blockoutEndTimePicker ).setHour( "3" );
    verify( scheduleEditor.blockoutEndTimePicker ).setMinute( String.valueOf( minutes ) );
    verify( scheduleEditor.blockoutEndTimePicker ).setTimeOfDay( TimeUtil.TimeOfDay.PM );
  }

  @Test
  public void testReset() throws Exception {
    doCallRealMethod().when( scheduleEditor ).reset( any( Date.class ) );

    scheduleEditor.runOnceEditor = mock( RunOnceEditor.class );
    scheduleEditor.recurrenceEditor = mock( RecurrenceEditor.class );
    scheduleEditor.cronEditor = mock( CronEditor.class );

    final Date now = new Date();
    scheduleEditor.reset( now );
    verify( scheduleEditor.runOnceEditor ).reset( now );
    verify( scheduleEditor.recurrenceEditor ).reset( now );
    verify( scheduleEditor.cronEditor ).reset( now );
    verify( scheduleEditor ).setScheduleType( ScheduleEditor.ScheduleType.RUN_ONCE );
  }

  @Test
  public void testGetCronString() throws Exception {
    doCallRealMethod().when( scheduleEditor ).getCronString();

    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.RUN_ONCE );
    assertNull( scheduleEditor.getCronString() );

    scheduleEditor.recurrenceEditor = mock( RecurrenceEditor.class );
    final String reCronString = "reCronString";
    when( scheduleEditor.recurrenceEditor.getCronString() ).thenReturn( reCronString );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.SECONDS );
    assertEquals( reCronString, scheduleEditor.getCronString() );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.MINUTES );
    assertEquals( reCronString, scheduleEditor.getCronString() );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.HOURS );
    assertEquals( reCronString, scheduleEditor.getCronString() );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.DAILY );
    assertEquals( reCronString, scheduleEditor.getCronString() );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.WEEKLY );
    assertEquals( reCronString, scheduleEditor.getCronString() );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.MONTHLY );
    assertEquals( reCronString, scheduleEditor.getCronString() );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.YEARLY );
    assertEquals( reCronString, scheduleEditor.getCronString() );

    scheduleEditor.cronEditor = mock( CronEditor.class );
    final String ceCronString = "ceCronString";
    when( scheduleEditor.cronEditor.getCronString() ).thenReturn( ceCronString );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.CRON );
    assertEquals( ceCronString, scheduleEditor.getCronString() );

    when( scheduleEditor.getScheduleType() ).thenReturn( null );
    try {
      scheduleEditor.getCronString();
      fail();
    } catch ( RuntimeException re ) {
      // expected
    }
  }

  @Test
  public void testSetCronString() throws Exception {
    doCallRealMethod().when( scheduleEditor ).setCronString( anyString() );

    try {
      scheduleEditor.setCronString( "cronString" );
      fail();
    } catch ( CronParseException e ) {
      // expected
    }

    scheduleEditor.recurrenceEditor = mock( RecurrenceEditor.class );
    final RecurrenceEditor.TemporalValue temporalValue = RecurrenceEditor.TemporalValue.DAILY;
    when( scheduleEditor.recurrenceEditor.getTemporalState() ).thenReturn( temporalValue );
    final ScheduleEditor.ScheduleType scheduleType = ScheduleEditor.ScheduleType.DAILY;
    when( scheduleEditor.temporalValueToScheduleType( temporalValue ) ).thenReturn( scheduleType );
    scheduleEditor.cronEditor = mock( CronEditor.class );

    final String cronStr = "0 33 6 ? * 1";
    scheduleEditor.setCronString( cronStr );
    verify( scheduleEditor.recurrenceEditor ).inititalizeWithRecurrenceString( anyString() );
    verify( scheduleEditor ).setScheduleType( scheduleType );
    verify( scheduleEditor.cronEditor ).setCronString( cronStr );

    final String cronStr1 = cronStr + " 1";
    scheduleEditor.setCronString( cronStr1 );
    verify( scheduleEditor.recurrenceEditor ).inititalizeWithRecurrenceString( anyString() ); // prev, not at this time
    verify( scheduleEditor ).setScheduleType( ScheduleEditor.ScheduleType.CRON );
    verify( scheduleEditor.cronEditor ).setCronString( cronStr1 );
  }

  @Test
  public void testSetRepeatInSecs() throws Exception {
    doCallRealMethod().when( scheduleEditor ).setRepeatInSecs( anyInt() );

    scheduleEditor.recurrenceEditor = mock( RecurrenceEditor.class );
    final RecurrenceEditor.TemporalValue temporalValue = RecurrenceEditor.TemporalValue.DAILY;
    when( scheduleEditor.recurrenceEditor.getTemporalState() ).thenReturn( temporalValue );
    final ScheduleEditor.ScheduleType scheduleType = ScheduleEditor.ScheduleType.DAILY;
    when( scheduleEditor.temporalValueToScheduleType( temporalValue ) ).thenReturn( scheduleType );

    final int repeatInSecs = 5;
    scheduleEditor.setRepeatInSecs( repeatInSecs );
    verify( scheduleEditor.recurrenceEditor ).inititalizeWithRepeatInSecs( repeatInSecs );
    verify( scheduleEditor ).setScheduleType( scheduleType );
  }

  @Test
  public void testGetScheduleType() throws Exception {
    doCallRealMethod().when( scheduleEditor ).getScheduleType();

    scheduleEditor.scheduleCombo = mock( ListBox.class );
    final ScheduleEditor.ScheduleType scheduleType = ScheduleEditor.ScheduleType.DAILY;
    final String selectedTypeStr = scheduleType.toString();
    when( scheduleEditor.scheduleCombo.getValue( anyInt() ) ).thenReturn( selectedTypeStr );

    assertEquals( scheduleType, scheduleEditor.getScheduleType() );
  }

  @Test
  public void testSetScheduleType() throws Exception {
    doCallRealMethod().when( scheduleEditor ).setScheduleType( any( ScheduleEditor.ScheduleType.class ) );

    final ScheduleEditor.ScheduleType scheduleType = ScheduleEditor.ScheduleType.DAILY;

    scheduleEditor.scheduleCombo = mock( ListBox.class );
    when( scheduleEditor.scheduleCombo.getItemCount() ).thenReturn( 5 );
    when( scheduleEditor.scheduleCombo.getItemText( anyInt() ) ).thenReturn( "" );
    final int index = 3;
    when( scheduleEditor.scheduleCombo.getItemText( index ) ).thenReturn( scheduleType.toString() );

    scheduleEditor.setScheduleType( scheduleType );
    verify( scheduleEditor.scheduleCombo ).setSelectedIndex( index );
  }

  @Test
  public void testSetStartTime() throws Exception {
    doCallRealMethod().when( scheduleEditor ).setStartTime( anyString() );

    scheduleEditor.runOnceEditor = mock( RunOnceEditor.class );
    scheduleEditor.recurrenceEditor = mock( RecurrenceEditor.class );

    final String startTime = "startTime";
    scheduleEditor.setStartTime( startTime );
    verify( scheduleEditor.runOnceEditor ).setStartTime( startTime );
    verify( scheduleEditor.recurrenceEditor ).setStartTime( startTime );
  }

  @Test
  public void testGetStartTime() throws Exception {
    doCallRealMethod().when( scheduleEditor ).getStartTime();

    final String startTimeROE = "startTimeROE";
    scheduleEditor.runOnceEditor = mock( RunOnceEditor.class );
    when( scheduleEditor.runOnceEditor.getStartTime() ).thenReturn( startTimeROE );
    final String startTimeRE = "startTimeRE";
    scheduleEditor.recurrenceEditor = mock( RecurrenceEditor.class );
    when( scheduleEditor.recurrenceEditor.getStartTime() ).thenReturn( startTimeRE );
    final String startTimeCE = "startTimeC";
    scheduleEditor.cronEditor = mock( CronEditor.class );
    when( scheduleEditor.cronEditor.getStartTime() ).thenReturn( startTimeCE );

    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.RUN_ONCE );
    assertEquals( startTimeROE, scheduleEditor.getStartTime() );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.SECONDS );
    assertEquals( startTimeRE, scheduleEditor.getStartTime() );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.MINUTES );
    assertEquals( startTimeRE, scheduleEditor.getStartTime() );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.HOURS );
    assertEquals( startTimeRE, scheduleEditor.getStartTime() );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.DAILY );
    assertEquals( startTimeRE, scheduleEditor.getStartTime() );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.WEEKLY );
    assertEquals( startTimeRE, scheduleEditor.getStartTime() );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.MONTHLY );
    assertEquals( startTimeRE, scheduleEditor.getStartTime() );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.YEARLY );
    assertEquals( startTimeRE, scheduleEditor.getStartTime() );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.CRON );
    assertEquals( startTimeCE, scheduleEditor.getStartTime() );
    when( scheduleEditor.getScheduleType() ).thenReturn( null );
    try {
      scheduleEditor.getStartTime();
      fail();
    } catch ( RuntimeException e ) {
      // expected
    }
  }

  @Test
  public void testSetStartDate() throws Exception {
    doCallRealMethod().when( scheduleEditor ).setStartDate( any( Date.class ) );

    scheduleEditor.runOnceEditor = mock( RunOnceEditor.class );
    scheduleEditor.recurrenceEditor = mock( RecurrenceEditor.class );
    scheduleEditor.cronEditor = mock( CronEditor.class );

    final Date startDate = new Date();
    scheduleEditor.setStartDate( startDate );
    verify( scheduleEditor.runOnceEditor ).setStartDate( startDate );
    verify( scheduleEditor.recurrenceEditor ).setStartDate( startDate );
    verify( scheduleEditor.cronEditor ).setStartDate( startDate );
  }

  @Test
  public void testGetStartDate() throws Exception {
    doCallRealMethod().when( scheduleEditor ).getStartDate();

    Date date = new Date();

    scheduleEditor.runOnceEditor = mock( RunOnceEditor.class );
    Calendar calendarRequest = Calendar.getInstance();
    calendarRequest.setTime( date );
    when( scheduleEditor.runOnceEditor.getStartDate() ).thenReturn( calendarRequest.getTime() );
    when( scheduleEditor.runOnceEditor.getStartTime() ).thenReturn( "2:50:33 " + TimeUtil.TimeOfDay.PM.toString() );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.RUN_ONCE );
    final Date startDate = scheduleEditor.getStartDate();
    Calendar calendarResponse = Calendar.getInstance();
    calendarResponse.setTime( startDate );
    assertEquals( calendarRequest.get( Calendar.YEAR ), calendarResponse.get( Calendar.YEAR ) );
    assertEquals( calendarRequest.get( Calendar.MONTH ), calendarResponse.get( Calendar.MONTH ) );
    assertEquals( calendarRequest.get( Calendar.DATE ), calendarResponse.get( Calendar.DATE ) );
    assertEquals( 14, calendarResponse.get( Calendar.HOUR_OF_DAY ) );
    assertEquals( 50, calendarResponse.get( Calendar.MINUTE ) );
    assertEquals( 0, calendarResponse.get( Calendar.SECOND ) );

    date = new Date();
    scheduleEditor.recurrenceEditor = mock( RecurrenceEditor.class );
    when( scheduleEditor.recurrenceEditor.getStartDate() ).thenReturn( date );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.SECONDS );
    assertEquals( date, scheduleEditor.getStartDate() );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.MINUTES );
    assertEquals( date, scheduleEditor.getStartDate() );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.HOURS );
    assertEquals( date, scheduleEditor.getStartDate() );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.DAILY );
    assertEquals( date, scheduleEditor.getStartDate() );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.WEEKLY );
    assertEquals( date, scheduleEditor.getStartDate() );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.MONTHLY );
    assertEquals( date, scheduleEditor.getStartDate() );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.YEARLY );
    assertEquals( date, scheduleEditor.getStartDate() );

    date = new Date();
    scheduleEditor.cronEditor = mock( CronEditor.class );
    when( scheduleEditor.cronEditor.getStartDate() ).thenReturn( date );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.CRON );
    assertEquals( date, scheduleEditor.getStartDate() );
  }

  @Test
  public void testSetEndDate() throws Exception {
    doCallRealMethod().when( scheduleEditor ).setEndDate( any( Date.class ) );

    scheduleEditor.recurrenceEditor = mock( RecurrenceEditor.class );
    scheduleEditor.cronEditor = mock( CronEditor.class );

    final Date endDate = new Date();
    scheduleEditor.setEndDate( endDate );
    verify( scheduleEditor.recurrenceEditor ).setEndDate( endDate );
    verify( scheduleEditor.cronEditor ).setEndDate( endDate );
  }

  @Test
  public void testGetEndDate() throws Exception {
    doCallRealMethod().when( scheduleEditor ).getEndDate();

    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.RUN_ONCE );
    assertNull( scheduleEditor.getEndDate() );

    scheduleEditor.recurrenceEditor = mock( RecurrenceEditor.class );
    Date date = new Date();
    when( scheduleEditor.recurrenceEditor.getEndDate() ).thenReturn( date );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.SECONDS );
    assertEquals( date, scheduleEditor.getEndDate() );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.MINUTES );
    assertEquals( date, scheduleEditor.getEndDate() );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.HOURS );
    assertEquals( date, scheduleEditor.getEndDate() );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.DAILY );
    assertEquals( date, scheduleEditor.getEndDate() );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.MONTHLY );
    assertEquals( date, scheduleEditor.getEndDate() );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.WEEKLY );
    assertEquals( date, scheduleEditor.getEndDate() );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.YEARLY );
    assertEquals( date, scheduleEditor.getEndDate() );

    date = new Date();
    scheduleEditor.cronEditor = mock( CronEditor.class );
    when( scheduleEditor.cronEditor.getEndDate() ).thenReturn( date );
    when( scheduleEditor.getScheduleType() ).thenReturn( ScheduleEditor.ScheduleType.CRON );
    assertEquals( date, scheduleEditor.getEndDate() );
  }

  @Test
  public void testSetNoEndDate() throws Exception {
    doCallRealMethod().when( scheduleEditor ).setNoEndDate();

    scheduleEditor.recurrenceEditor = mock( RecurrenceEditor.class );
    scheduleEditor.cronEditor = mock( CronEditor.class );

    scheduleEditor.setNoEndDate();
    verify( scheduleEditor.recurrenceEditor ).setNoEndDate();
    verify( scheduleEditor.cronEditor ).setNoEndDate();
  }

  @Test
  public void testSetEndBy() throws Exception {
    doCallRealMethod().when( scheduleEditor ).setEndBy();

    scheduleEditor.recurrenceEditor = mock( RecurrenceEditor.class );
    scheduleEditor.cronEditor = mock( CronEditor.class );

    scheduleEditor.setEndBy();
    verify( scheduleEditor.recurrenceEditor ).setEndBy();
    verify( scheduleEditor.cronEditor ).setEndBy();
  }


  @Test
  public void testConfigureTimeZonePicker() {
    doCallRealMethod().when( scheduleEditor ).configureTimeZonePicker();

    scheduleEditor.timeZonePicker = mock( ListBox.class );
    scheduleEditor.configureTimeZonePicker( );

    assertNotNull( scheduleEditor.timeZonePicker );
    verify( scheduleEditor.timeZonePicker ).setStyleName( "timeZonePicker" );
    verify( scheduleEditor.timeZonePicker ).setVisibleItemCount( 1 );
  }

  @Test
  public void testGetTargetTimezone() {

    final String testTargetTimezone = "Eastern Daylight Time (UTC-0500)";

    doCallRealMethod().when( scheduleEditor ).getTargetTimezone();

    scheduleEditor.timeZonePicker = mock( ListBox.class );
    when( scheduleEditor.getTimeZonePicker() ).thenReturn( scheduleEditor.timeZonePicker );
    when( scheduleEditor.timeZonePicker.getSelectedIndex() ).thenReturn( 1 );
    when( scheduleEditor.timeZonePicker.getItemText( anyInt() ) ).thenReturn( testTargetTimezone );

    final String targetTimezone = scheduleEditor.getTargetTimezone();

    assertNotNull( targetTimezone );
    assertEquals( testTargetTimezone, targetTimezone );
  }

  private ListBox prepareLB( int itemText, String display ) {
    final ListBox lb = mock( ListBox.class );
    final Element element = mock( Element.class );
    final Style style = mock( Style.class );
    when( style.getDisplay() ).thenReturn( display );
    when( element.getStyle() ).thenReturn( style );
    when( lb.getElement() ).thenReturn( element );
    when( lb.getItemText( anyInt() ) ).thenReturn( String.valueOf( itemText ) );
    return lb;
  }
}
