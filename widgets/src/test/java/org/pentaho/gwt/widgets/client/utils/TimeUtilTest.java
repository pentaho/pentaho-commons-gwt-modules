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
    assertEquals( dayVariance, -1 );

    timezoneIdFormat = "Japan Daylight Time (UTC+0900)";
    dayVariance = TimeUtil.getDayVariance( 20, 0, timezoneIdFormat );
    assertEquals( dayVariance, 1 );
  }

  @Test
  public void testGetDayVariance_dateTimeFormat() {
    String dateTimeFormat = "2018-01-01T07:30:00-05:00";
    int dayVariance = TimeUtil.getDayVariance( 3, 0, dateTimeFormat );
    assertEquals( dayVariance, 1 );

    dateTimeFormat = "2018-01-01T07:30:00+05:00";
    dayVariance = TimeUtil.getDayVariance( 20, 0, dateTimeFormat );
    assertEquals( dayVariance, -1 );

    dateTimeFormat = "2020-11-05T05:30:00-05:30";
    dayVariance = TimeUtil.getDayVariance( 5, 29, dateTimeFormat );
    assertEquals( dayVariance, 0 );

    dateTimeFormat = "2020-11-05T05:30:00-05:30";
    dayVariance = TimeUtil.getDayVariance( 0, 31, dateTimeFormat );
    assertEquals( dayVariance, 1 );
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
}
