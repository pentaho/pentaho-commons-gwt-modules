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

package org.pentaho.mantle.client.dialogs.scheduling.validators;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.gwt.widgets.client.utils.TimeUtil;
import org.pentaho.mantle.client.dialogs.scheduling.RecurrenceEditor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class RecurrenceEditorValidatorTest {
  private RecurrenceEditor recurrenceEditor;
  private RecurrenceEditorValidator validator;

  @Before
  public void setUp() throws Exception {
    recurrenceEditor = mock( RecurrenceEditor.class );
    validator = new RecurrenceEditorValidator( recurrenceEditor );
    validator.dateRangeEditorValidator = mock( DateRangeEditorValidator.class );
  }

  @Test
  public void testIsValid_Seconds() throws Exception {
    when( recurrenceEditor.getTemporalState() ).thenReturn( RecurrenceEditor.TemporalValue.SECONDS );
    final RecurrenceEditor.SecondlyRecurrenceEditor secondlyRecurrenceEditor =
        mock( RecurrenceEditor.SecondlyRecurrenceEditor.class );
    when( recurrenceEditor.getSecondlyEditor() ).thenReturn( secondlyRecurrenceEditor );

    testIsValid( RecurrenceEditor.TemporalValue.SECONDS, secondlyRecurrenceEditor, TimeUtil.MAX_SECOND_BY_MILLISEC );
  }

  @Test
  public void testIsValid_Minutes() throws Exception {
    final RecurrenceEditor.MinutelyRecurrenceEditor minutelyRecurrenceEditor =
        mock( RecurrenceEditor.MinutelyRecurrenceEditor.class );
    when( recurrenceEditor.getMinutelyEditor() ).thenReturn( minutelyRecurrenceEditor );

    testIsValid( RecurrenceEditor.TemporalValue.MINUTES, minutelyRecurrenceEditor, TimeUtil.MAX_MINUTE_BY_MILLISEC );
  }

  @Test
   public void testIsValid_Hours() throws Exception {
    final RecurrenceEditor.HourlyRecurrenceEditor hourlyRecurrenceEditor =
        mock( RecurrenceEditor.HourlyRecurrenceEditor.class );
    when( recurrenceEditor.getHourlyEditor() ).thenReturn( hourlyRecurrenceEditor );

    testIsValid( RecurrenceEditor.TemporalValue.HOURS, hourlyRecurrenceEditor, TimeUtil.MAX_HOUR_BY_MILLISEC );
  }

  @Test
  public void testIsValid_Daily() throws Exception {
    when( recurrenceEditor.getTemporalState() ).thenReturn( RecurrenceEditor.TemporalValue.DAILY );
    final RecurrenceEditor.DailyRecurrenceEditor dailyRecurrenceEditor =
        mock( RecurrenceEditor.DailyRecurrenceEditor.class );
    when( recurrenceEditor.getDailyEditor() ).thenReturn( dailyRecurrenceEditor );

    when( validator.dateRangeEditorValidator.isValid() ).thenReturn( true );
    when( dailyRecurrenceEditor.isEveryNDays() ).thenReturn( true );
    when( dailyRecurrenceEditor.getRepeatValue() ).thenReturn( "wrong_value" );
    assertFalse( validator.isValid() );

    when( dailyRecurrenceEditor.getRepeatValue() ).thenReturn( "-1" );
    assertFalse( validator.isValid() );

    when( dailyRecurrenceEditor.getRepeatValue() ).thenReturn( "123" );
    when( validator.dateRangeEditorValidator.isValid() ).thenReturn( false );
    assertFalse( validator.isValid() );

    when( validator.dateRangeEditorValidator.isValid() ).thenReturn( true );
    assertTrue( validator.isValid() );

    when( dailyRecurrenceEditor.getRepeatValue() ).thenReturn( "-1" );
    when( dailyRecurrenceEditor.isEveryNDays() ).thenReturn( false );
    assertTrue( validator.isValid() );
  }

  @Test
  public void testIsValid_Weekly() throws Exception {
    when( recurrenceEditor.getTemporalState() ).thenReturn( RecurrenceEditor.TemporalValue.WEEKLY );
    final RecurrenceEditor.WeeklyRecurrenceEditor weeklyRecurrenceEditor =
        mock( RecurrenceEditor.WeeklyRecurrenceEditor.class );
    when( recurrenceEditor.getWeeklyEditor() ).thenReturn( weeklyRecurrenceEditor );
    when( validator.dateRangeEditorValidator.isValid() ).thenReturn( true );

    when( weeklyRecurrenceEditor.getNumCheckedDays() ).thenReturn( -1 );
    assertFalse( validator.isValid() );

    when( weeklyRecurrenceEditor.getNumCheckedDays() ).thenReturn( 0 );
    assertFalse( validator.isValid() );

    when( weeklyRecurrenceEditor.getNumCheckedDays() ).thenReturn( 1 );
    assertTrue( validator.isValid() );


    when( validator.dateRangeEditorValidator.isValid() ).thenReturn( false );
    assertFalse( validator.isValid() );
  }

  @Test
  public void testIsValid_Monthly() throws Exception {
    when( recurrenceEditor.getTemporalState() ).thenReturn( RecurrenceEditor.TemporalValue.MONTHLY );
    final RecurrenceEditor.MonthlyRecurrenceEditor monthlyRecurrenceEditor =
        mock( RecurrenceEditor.MonthlyRecurrenceEditor.class );
    when( recurrenceEditor.getMonthlyEditor() ).thenReturn( monthlyRecurrenceEditor );
    when( validator.dateRangeEditorValidator.isValid() ).thenReturn( true );

    when( monthlyRecurrenceEditor.isDayNOfMonth() ).thenReturn( true );
    when( monthlyRecurrenceEditor.getDayOfMonth() ).thenReturn( "wrong_value" );
    assertFalse( validator.isValid() );

    when( monthlyRecurrenceEditor.getDayOfMonth() ).thenReturn( "-1" );
    assertFalse( validator.isValid() );

    when( monthlyRecurrenceEditor.getDayOfMonth() ).thenReturn( "40" );
    assertFalse( validator.isValid() );

    when( monthlyRecurrenceEditor.isDayNOfMonth() ).thenReturn( false );
    assertTrue( validator.isValid() );
    when( monthlyRecurrenceEditor.isDayNOfMonth() ).thenReturn( true );

    when( monthlyRecurrenceEditor.getDayOfMonth() ).thenReturn( "15" );
    assertTrue( validator.isValid() );

    when( validator.dateRangeEditorValidator.isValid() ).thenReturn( false );
    assertFalse( validator.isValid() );
  }

  @Test
  public void testIsValid_Yearly() throws Exception {
    when( recurrenceEditor.getTemporalState() ).thenReturn( RecurrenceEditor.TemporalValue.YEARLY );
    final RecurrenceEditor.YearlyRecurrenceEditor yearlyRecurrenceEditor =
        mock( RecurrenceEditor.YearlyRecurrenceEditor.class );
    when( recurrenceEditor.getYearlyEditor() ).thenReturn( yearlyRecurrenceEditor );
    when( validator.dateRangeEditorValidator.isValid() ).thenReturn( true );

    when( yearlyRecurrenceEditor.isEveryMonthOnNthDay() ).thenReturn( true );
    when( yearlyRecurrenceEditor.getDayOfMonth() ).thenReturn( "wrong_value" );
    assertFalse( validator.isValid() );

    when( yearlyRecurrenceEditor.getDayOfMonth() ).thenReturn( "-1" );
    assertFalse( validator.isValid() );

    when( yearlyRecurrenceEditor.getDayOfMonth() ).thenReturn( "40" );
    assertFalse( validator.isValid() );

    when( yearlyRecurrenceEditor.isEveryMonthOnNthDay() ).thenReturn( false );
    assertTrue( validator.isValid() );
    when( yearlyRecurrenceEditor.isEveryMonthOnNthDay() ).thenReturn( true );

    when( yearlyRecurrenceEditor.getDayOfMonth() ).thenReturn( "15" );
    assertTrue( validator.isValid() );

    when( validator.dateRangeEditorValidator.isValid() ).thenReturn( false );
    assertFalse( validator.isValid() );
  }

  private void testIsValid( RecurrenceEditor.TemporalValue type, RecurrenceEditor.SimpleRecurrencePanel recurrencePanel,
      int maxValue ) {
    when( recurrenceEditor.getTemporalState() ).thenReturn( type );
    when( validator.dateRangeEditorValidator.isValid() ).thenReturn( true );
    when( recurrencePanel.getValue() ).thenReturn( "wrong_value" );
    assertFalse( validator.isValid() );

    when( recurrencePanel.getValue() ).thenReturn( "-1" );
    assertFalse( validator.isValid() );

    when( recurrencePanel.getValue() ).thenReturn( maxValue + 1 + "" );
    assertFalse( validator.isValid() );

    when( recurrencePanel.getValue() ).thenReturn( "123" );
    when( validator.dateRangeEditorValidator.isValid() ).thenReturn( false );
    assertFalse( validator.isValid() );

    when( validator.dateRangeEditorValidator.isValid() ).thenReturn( true );
    assertTrue( validator.isValid() );
  }

  @Test
  public void testClear() throws Exception {
    when( recurrenceEditor.getSecondlyEditor() ).thenReturn( mock( RecurrenceEditor.SecondlyRecurrenceEditor.class ) );
    when( recurrenceEditor.getMinutelyEditor() ).thenReturn( mock( RecurrenceEditor.MinutelyRecurrenceEditor.class ) );
    when( recurrenceEditor.getHourlyEditor() ).thenReturn( mock( RecurrenceEditor.HourlyRecurrenceEditor.class ) );
    when( recurrenceEditor.getDailyEditor() ).thenReturn( mock( RecurrenceEditor.DailyRecurrenceEditor.class ) );
    when( recurrenceEditor.getWeeklyEditor() ).thenReturn( mock( RecurrenceEditor.WeeklyRecurrenceEditor.class ) );
    when( recurrenceEditor.getMonthlyEditor() ).thenReturn( mock( RecurrenceEditor.MonthlyRecurrenceEditor.class ) );
    when( recurrenceEditor.getYearlyEditor() ).thenReturn( mock( RecurrenceEditor.YearlyRecurrenceEditor.class ) );

    validator.clear();

    verify( recurrenceEditor.getSecondlyEditor() ).setValueError( null );
    verify( recurrenceEditor.getMinutelyEditor() ).setValueError( null );
    verify( recurrenceEditor.getHourlyEditor() ).setValueError( null );
    verify( recurrenceEditor.getDailyEditor() ).setRepeatError( null );
    verify( recurrenceEditor.getWeeklyEditor() ).setEveryDayOnError( null );
    verify( recurrenceEditor.getMonthlyEditor() ).setDayNOfMonthError( null );
    verify( recurrenceEditor.getYearlyEditor() ).setDayOfMonthError( null );
    verify( validator.dateRangeEditorValidator ).clear();
  }
}
