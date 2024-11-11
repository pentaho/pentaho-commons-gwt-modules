/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.gwt.widgets.client.utils;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.junit.Assert.assertEquals;

@RunWith( GwtMockitoTestRunner.class )
public class TimeUtilTest {
  @Test
  public void testGetDayVariance_timezoneIdFormat() {
    String timezoneIdFormat = "Eastern Daylight Time (UTC-0500)";
    int dayVariance = TimeUtil.getDayVariance( 3, 0, timezoneIdFormat );
    assertEquals( -1, dayVariance );

    timezoneIdFormat = "Japan Daylight Time (UTC+0900)";
    dayVariance = TimeUtil.getDayVariance( 20, 0, timezoneIdFormat );
    assertEquals( 1, dayVariance );
  }

  @Test
  public void testGetDayVariance_dateTimeFormat() {
    String dateTimeFormat = "2018-01-01T07:30:00-05:00";
    int dayVariance = TimeUtil.getDayVariance( 3, 0, dateTimeFormat );
    assertEquals( 1, dayVariance );

    dateTimeFormat = "2018-01-01T07:30:00+05:00";
    dayVariance = TimeUtil.getDayVariance( 20, 0, dateTimeFormat );
    assertEquals( -1, dayVariance );

    dateTimeFormat = "2020-11-05T05:30:00-05:30";
    dayVariance = TimeUtil.getDayVariance( 6, 29, dateTimeFormat );
    assertEquals( 0, dayVariance );

    dateTimeFormat = "2020-11-05T05:30:00-05:30";
    dayVariance = TimeUtil.getDayVariance( 0, 31, dateTimeFormat );
    assertEquals( 1, dayVariance );
  }

  @Test
  public void testGetNextLogTest() {
    TimeUtil.LogLevel logLevel = TimeUtil.LogLevel.MINIMAL;
    int log = logLevel.getNext();
    assertEquals( TimeUtil.LogLevel.NOTHING.value(), log );
  }

  @Test
  public void testGetNextLog_next() {
    int nextLogLevel = TimeUtil.getLogLevel( TimeUtil.LogLevel.NOTHING, 3 );
    assertEquals( TimeUtil.LogLevel.DETAILED.ordinal(), nextLogLevel );
  }

  @Test
  public void testGetNextLog_previous() {
    int nextLogLevel = TimeUtil.getLogLevel( TimeUtil.LogLevel.NOTHING, -1 );
    assertEquals( TimeUtil.LogLevel.MINIMAL.ordinal(), nextLogLevel );
  }

  @Test
  public void getNextDayWrapTest() {
    TimeUtil.DayOfWeek day = TimeUtil.DayOfWeek.SAT;
    int nextDay = day.getNext();
    assertEquals( TimeUtil.DayOfWeek.SUN.value(), nextDay );
  }

  @Test
  public void testGetDayOfWeek_next() {
    int nextDayOfWeek = TimeUtil.getDayOfWeek( TimeUtil.DayOfWeek.MON, 1 );
    assertEquals( TimeUtil.DayOfWeek.TUE.ordinal(), nextDayOfWeek );
  }

  @Test
  public void testGetDayOfWeek_previous() {
    int previousDayOfWeek = TimeUtil.getDayOfWeek( TimeUtil.DayOfWeek.MON, -1 );
    assertEquals( TimeUtil.DayOfWeek.SUN.ordinal(), previousDayOfWeek );
  }

  @Test
  public void getTargetOffsetFromTimezoneStringTest() {
    double timeOffset = TimeUtil.getTargetOffsetFromTimezoneString( "Eastern Daylight Time (UTC-0500)" );
    assertEquals( -5.0, timeOffset, 0 );
    timeOffset = TimeUtil.getTargetOffsetFromTimezoneString( "Atlantic Daylight Time (UTC-400)" );
    assertEquals( -4.0, timeOffset, 0 );
    timeOffset = TimeUtil.getTargetOffsetFromTimezoneString( "India Daylight Time (UTC+0530)" );
    assertEquals( 5.5, timeOffset, 0 );
    timeOffset = TimeUtil.getTargetOffsetFromTimezoneString( "Nepal Summer Time (UTC+0545)" );
    assertEquals( 5.75, timeOffset, 0 );
  }

  @Test
  public void getTargetOffsetFromTimezoneStringInvalidValuesTest() {
    double timeOffset = TimeUtil.getTargetOffsetFromTimezoneString( "Eastern Daylight Time (UTC0500)" );
    assertEquals( 0.0, timeOffset, 0 );
    timeOffset = TimeUtil.getTargetOffsetFromTimezoneString( "Eastern Daylight Time (UTC--0500)" );
    assertEquals( 0.0, timeOffset, 0 );
    timeOffset = TimeUtil.getTargetOffsetFromTimezoneString( "2020-12-01T10:30:00-05:45" );
    assertEquals( 0.0, timeOffset, 0 );
  }

  @Test
  public void getTargetOffsetFromDateTimeStringTest() {
    double timeOffset = TimeUtil.getTargetOffsetFromDatetimeString( "2018-02-27T07:30:00-05:00" );
    assertEquals( -5.0, timeOffset, 0 );
    timeOffset = TimeUtil.getTargetOffsetFromDatetimeString( "2020-12-01T10:30:00-05:45" );
    assertEquals( -5.75, timeOffset, 0 );
    timeOffset = TimeUtil.getTargetOffsetFromDatetimeString( "2020-12-01 10:30:00+01:00" );
    assertEquals( 1.0, timeOffset, 0 );
    timeOffset = TimeUtil.getTargetOffsetFromDatetimeString( "2020-12-01 10:30:00+11:30" );
    assertEquals( 11.5, timeOffset, 0 );
  }

  @Test
  public void getTargetOffsetFromDateTimeStringInvalidValuesTest() {
    double timeOffset = TimeUtil.getTargetOffsetFromDatetimeString( "2018-02-27X07:30:00-05:00" );
    assertEquals( 0.0, timeOffset, 0 );
    timeOffset = TimeUtil.getTargetOffsetFromDatetimeString( "2020-12T10:30:00-05:45" );
    assertEquals( 0.0, timeOffset, 0 );
    timeOffset = TimeUtil.getTargetOffsetFromDatetimeString( "2020-12-01 10:30:00+01" );
    assertEquals( 0.0, timeOffset, 0 );
    timeOffset = TimeUtil.getTargetOffsetFromDatetimeString( "Eastern Daylight Time (UTC-0500)" );
    assertEquals( 0.0, timeOffset, 0 );
  }
}