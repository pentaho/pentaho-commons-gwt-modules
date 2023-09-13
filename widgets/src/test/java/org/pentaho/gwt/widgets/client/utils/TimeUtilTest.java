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
 * Copyright (c) 2002-2020 Hitachi Vantara..  All rights reserved.
 */

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