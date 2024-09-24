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

package org.pentaho.gwt.widgets.client.utils;

import com.google.gwt.junit.client.GWTTestCase;

import java.util.Date;


public class TimeUtilIT extends GWTTestCase {
  public String getModuleName() {
    return "org.pentaho.gwt.widgets.Widgets"; //$NON-NLS-1$
  }

  public void testDaysToSecs() throws Exception {
    assertEquals( 0, TimeUtil.daysToSecs( 0 ) );
    assertEquals( 86400, TimeUtil.daysToSecs( 1 ) );
    assertEquals( 432000, TimeUtil.daysToSecs( 5 ) );
  }

  public void testHoursToSecs() throws Exception {
    assertEquals( 0, TimeUtil.hoursToSecs( 0 ) );
    assertEquals( 3600, TimeUtil.hoursToSecs( 1 ) );
    assertEquals( 54000, TimeUtil.hoursToSecs( 15 ) );
  }

  public void testMinutesToSecs() throws Exception {
    assertEquals( 0, TimeUtil.minutesToSecs( 0 ) );
    assertEquals( 60, TimeUtil.minutesToSecs( 1 ) );
    assertEquals( 900, TimeUtil.minutesToSecs( 15 ) );
  }

  public void testSecsToMillisecs() throws Exception {
    assertEquals( 0, TimeUtil.secsToMillisecs( 0 ) );
    assertEquals( 1000, TimeUtil.secsToMillisecs( 1 ) );
    assertEquals( 15000, TimeUtil.secsToMillisecs( 15 ) );
  }

  public void testSecsToDays() throws Exception {
    assertEquals( 0, TimeUtil.secsToDays( 0 ) );
    assertEquals( 0, TimeUtil.secsToDays( 1 ) );
    assertEquals( 5, TimeUtil.secsToDays( 432000 ) );
  }

  public void testSecsToHours() throws Exception {
    assertEquals( 0, TimeUtil.secsToHours( 0 ) );
    assertEquals( 1, TimeUtil.secsToHours( 3600 ) );
    assertEquals( 120, TimeUtil.secsToHours( 432000 ) );
  }

  public void testSecsToMinutes() throws Exception {
    assertEquals( 0, TimeUtil.secsToMinutes( 0 ) );
    assertEquals( 60, TimeUtil.secsToMinutes( 3600 ) );
    assertEquals( 7200, TimeUtil.secsToMinutes( 432000 ) );
  }

  public void testIsSecondsWholeDay() throws Exception {
    assertTrue( TimeUtil.isSecondsWholeDay( 0 ) );
    assertTrue( TimeUtil.isSecondsWholeDay( 86400 ) );
    assertTrue( TimeUtil.isSecondsWholeDay( 432000 ) );
    assertFalse( TimeUtil.isSecondsWholeDay( 431999 ) );
    assertFalse( TimeUtil.isSecondsWholeDay( 432001 ) );
  }

  public void testIsSecondsWholeHour() throws Exception {
    assertTrue( TimeUtil.isSecondsWholeHour( 0 ) );
    assertTrue( TimeUtil.isSecondsWholeHour( 86400 ) );
    assertTrue( TimeUtil.isSecondsWholeHour( 432000 ) );
    assertFalse( TimeUtil.isSecondsWholeHour( 431999 ) );
    assertFalse( TimeUtil.isSecondsWholeHour( 432001 ) );
  }

  public void testIsSecondsWholeMinute() throws Exception {
    assertTrue( TimeUtil.isSecondsWholeMinute( 0 ) );
    assertTrue( TimeUtil.isSecondsWholeMinute( 86400 ) );
    assertTrue( TimeUtil.isSecondsWholeMinute( 432000 ) );
    assertFalse( TimeUtil.isSecondsWholeMinute( 431999 ) );
    assertFalse( TimeUtil.isSecondsWholeMinute( 432001 ) );
  }

  public void testGetTimeOfDayBy0To23Hour() throws Exception {
    assertEquals( TimeUtil.TimeOfDay.AM, TimeUtil.getTimeOfDayBy0To23Hour( 0 ) );
    assertEquals( TimeUtil.TimeOfDay.AM, TimeUtil.getTimeOfDayBy0To23Hour( 8 ) );
    assertEquals( TimeUtil.TimeOfDay.PM, TimeUtil.getTimeOfDayBy0To23Hour( 12 ) );
    assertEquals( TimeUtil.TimeOfDay.PM, TimeUtil.getTimeOfDayBy0To23Hour( 20 ) );
  }

  public void testGetTimeOfDayBy0To23Hour1() throws Exception {
    assertEquals( TimeUtil.TimeOfDay.AM, TimeUtil.getTimeOfDayBy0To23Hour( "0" ) );
    assertEquals( TimeUtil.TimeOfDay.AM, TimeUtil.getTimeOfDayBy0To23Hour( "8" ) );
    assertEquals( TimeUtil.TimeOfDay.PM, TimeUtil.getTimeOfDayBy0To23Hour( "12" ) );
    assertEquals( TimeUtil.TimeOfDay.PM, TimeUtil.getTimeOfDayBy0To23Hour( "20" ) );
  }

  public void testTo12HourClock() throws Exception {
    assertEquals( 0, TimeUtil.to12HourClock( 0 ) );
    assertEquals( 8, TimeUtil.to12HourClock( 8 ) );
    assertEquals( 0, TimeUtil.to12HourClock( 12 ) );
    assertEquals( 8, TimeUtil.to12HourClock( 20 ) );
    try {
      TimeUtil.to12HourClock( 24 );
      fail();
    } catch ( AssertionError e ) {
      // ok
    }
    try {
      TimeUtil.to12HourClock( -1 );
      fail();
    } catch ( AssertionError e ) {
      // ok
    }
  }

  public void testTo12HourClock1() throws Exception {
    assertEquals( "12:00:00", TimeUtil.to12HourClock( "00:00:00" ) );
    assertEquals( "8:05:03", TimeUtil.to12HourClock( "08:05:03" ) );
    assertEquals( "12:00:00", TimeUtil.to12HourClock( "12:00:00" ) );
    assertEquals( "8:00:00", TimeUtil.to12HourClock( "20:00:00" ) );
  }

  public void testMap0Through11To12Through11() throws Exception {
    assertEquals( 12, TimeUtil.map0Through11To12Through11( 0 ) );
    assertEquals( 12, TimeUtil.map0Through11To12Through11( 12 ) );
    assertEquals( 7, TimeUtil.map0Through11To12Through11( 7 ) );
  }

  public void testMillsecondsToSecs() throws Exception {
    assertEquals( 0, TimeUtil.millsecondsToSecs( 0 ) );
    assertEquals( 1, TimeUtil.millsecondsToSecs( 1000 ) );
    assertEquals( 324243, TimeUtil.millsecondsToSecs( 324243242 ) );
  }

  public void testGetDateTimeString() throws Exception {
    assertEquals( "4 8, 2015 7:22:44 PM", TimeUtil.getDateTimeString( "4", "8", "2015", "7", "22", "44", TimeUtil.TimeOfDay.PM ) );
  }

  public void testGetDateTime() throws Exception {
    final Date date = new Date();
    final Date dateTime = TimeUtil.getDateTime( "8:22:33 PM", date );
    assertFalse( date.equals( dateTime ) );
  }

  public void testGetDate() throws Exception {
    assertNotNull( TimeUtil.getDate( "Nov 05, 2015 5:22:33 PM" ) );
  }

  public void testGetTimePart() throws Exception {
    final String time = "5:22:33 PM";
    assertEquals( time, TimeUtil.getTimePart( "Nov 05, 2015 " + time ) );
  }

  public void testGetTimePart1() throws Exception {
    assertNotNull( TimeUtil.getTimePart( new Date() ) );
  }

  public void testGetDatePart() throws Exception {
    final String date = "Nov 05, 2015";
    assertTrue( date.equals( TimeUtil.getDatePart( date + " 5:22:33 PM" ) ) );
  }

  public void testZeroTimePart() throws Exception {
    String date = "Nov 05, 2015";
    String time = "5:22:33 PM";
    assertTrue( "Nov 05, 2015 12:00:00 AM".equals( TimeUtil.zeroTimePart( date + " " + time ) ) );
  }

  public void testZeroTimePart1() throws Exception {
    final Date date = new Date();
    assertFalse( date.equals( TimeUtil.zeroTimePart( date ) ) );
  }

  public void testGetDateString() throws Exception {
    assertTrue( "4 12, 2015".equals( TimeUtil.getDateString( "4", "12", "2015" ) ) );
  }

  public void testIsValidNumOfDaysForMonth() throws Exception {
    assertTrue( TimeUtil.isValidNumOfDaysForMonth( 31, TimeUtil.MonthOfYear.JAN ) );
    assertTrue( TimeUtil.isValidNumOfDaysForMonth( 29, TimeUtil.MonthOfYear.FEB ) );
    assertTrue( TimeUtil.isValidNumOfDaysForMonth( 31, TimeUtil.MonthOfYear.MAR ) );
    assertTrue( TimeUtil.isValidNumOfDaysForMonth( 30, TimeUtil.MonthOfYear.APR ) );
    assertTrue( TimeUtil.isValidNumOfDaysForMonth( 31, TimeUtil.MonthOfYear.MAY ) );
    assertTrue( TimeUtil.isValidNumOfDaysForMonth( 30, TimeUtil.MonthOfYear.JUN ) );
    assertTrue( TimeUtil.isValidNumOfDaysForMonth( 31, TimeUtil.MonthOfYear.JUL ) );
    assertTrue( TimeUtil.isValidNumOfDaysForMonth( 31, TimeUtil.MonthOfYear.AUG ) );
    assertTrue( TimeUtil.isValidNumOfDaysForMonth( 30, TimeUtil.MonthOfYear.SEPT ) );
    assertTrue( TimeUtil.isValidNumOfDaysForMonth( 31, TimeUtil.MonthOfYear.OCT ) );
    assertTrue( TimeUtil.isValidNumOfDaysForMonth( 30, TimeUtil.MonthOfYear.NOV ) );
    assertTrue( TimeUtil.isValidNumOfDaysForMonth( 31, TimeUtil.MonthOfYear.DEC ) );

    assertFalse( TimeUtil.isValidNumOfDaysForMonth( 5, TimeUtil.MonthOfYear.DEC ) );
  }

  public void testIsDayOfMonth() throws Exception {
    assertTrue( TimeUtil.isDayOfMonth( 1 ) );
    assertTrue( TimeUtil.isDayOfMonth( 15 ) );
    assertTrue( TimeUtil.isDayOfMonth( 31 ) );
    assertFalse( TimeUtil.isDayOfMonth( 0 ) );
    assertFalse( TimeUtil.isDayOfMonth( 100 ) );
  }

  public void testIsDayOfWeek() throws Exception {
    assertTrue( TimeUtil.isDayOfWeek( 1 ) );
    assertTrue( TimeUtil.isDayOfWeek( 3 ) );
    assertTrue( TimeUtil.isDayOfWeek( 7 ) );
    assertFalse( TimeUtil.isDayOfWeek( 0 ) );
    assertFalse( TimeUtil.isDayOfWeek( 10 ) );
  }

  public void testIsWeekOfMonth() throws Exception {
    assertTrue( TimeUtil.isWeekOfMonth( 1 ) );
    assertTrue( TimeUtil.isWeekOfMonth( 3 ) );
    assertTrue( TimeUtil.isWeekOfMonth( 4 ) );
    assertFalse( TimeUtil.isWeekOfMonth( 0 ) );
    assertFalse( TimeUtil.isWeekOfMonth( 8 ) );
  }

  public void testIsMonthOfYear() throws Exception {
    assertTrue( TimeUtil.isMonthOfYear( 1 ) );
    assertTrue( TimeUtil.isMonthOfYear( 8 ) );
    assertTrue( TimeUtil.isMonthOfYear( 12 ) );
    assertFalse( TimeUtil.isMonthOfYear( 0 ) );
    assertFalse( TimeUtil.isMonthOfYear( 100 ) );
  }

  public void testIsSecond() throws Exception {
    assertTrue( TimeUtil.isSecond( 0 ) );
    assertTrue( TimeUtil.isSecond( 15 ) );
    assertTrue( TimeUtil.isSecond( 59 ) );
    assertFalse( TimeUtil.isSecond( 60 ) );
    assertFalse( TimeUtil.isSecond( -1 ) );
  }

  public void testIsMinute() throws Exception {
    assertTrue( TimeUtil.isMinute( 0 ) );
    assertTrue( TimeUtil.isMinute( 15 ) );
    assertTrue( TimeUtil.isMinute( 59 ) );
    assertFalse( TimeUtil.isMinute( 60 ) );
    assertFalse( TimeUtil.isMinute( -1 ) );
  }

  public void testIsHour() throws Exception {
    assertTrue( TimeUtil.isHour( 0 ) );
    assertTrue( TimeUtil.isHour( 15 ) );
    assertTrue( TimeUtil.isHour( 23 ) );
    assertFalse( TimeUtil.isHour( 24 ) );
    assertFalse( TimeUtil.isHour( -1 ) );
  }
}
