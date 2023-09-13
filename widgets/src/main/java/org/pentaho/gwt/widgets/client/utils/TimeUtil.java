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
 * Copyright (c) 2002-2023 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.gwt.widgets.client.utils;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import org.pentaho.gwt.widgets.client.i18n.WidgetsLocalizedMessages;
import org.pentaho.gwt.widgets.client.i18n.WidgetsLocalizedMessagesSingleton;

import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.i18n.client.DateTimeFormat;

public class TimeUtil {

  private static final WidgetsLocalizedMessages MSGS = WidgetsLocalizedMessagesSingleton.getInstance().getMessages();
  public static final int HOURS_IN_DAY = 24;

  public static final int MINUTES_IN_HOUR = 60;

  public static final int SECONDS_IN_MINUTE = 60;

  public static final int MILLISECS_IN_SECONDS = 1000;

  public static final int MIN_HOUR = 0;
  public static final int MAX_HOUR = HOURS_IN_DAY / 2;

  public static final int MAX_MINUTE = 60;

  public static final int MAX_SECOND_BY_MILLISEC = Integer.MAX_VALUE / MILLISECS_IN_SECONDS;
  public static final int MAX_MINUTE_BY_MILLISEC = MAX_SECOND_BY_MILLISEC / SECONDS_IN_MINUTE;
  public static final int MAX_HOUR_BY_MILLISEC = MAX_MINUTE_BY_MILLISEC / MINUTES_IN_HOUR;

  public enum TimeOfDay {
    AM( 0, MSGS.am() ), PM( 1, MSGS.pm() );

    TimeOfDay( int value, String name ) {
      this.value = value;
      this.name = name;
    }

    private final int value;

    private final String name;

    private static TimeOfDay[] timeOfDay = { AM, PM };

    public int value() {
      return value;
    }

    public String toString() {
      return name;
    }

    public static TimeOfDay get( int idx ) {
      return timeOfDay[idx];
    }

    public static int length() {
      return timeOfDay.length;
    }

    public static TimeOfDay stringToTimeOfDay( String timeOfDay ) throws EnumException {
      for ( TimeOfDay v : EnumSet.range( TimeOfDay.AM, TimeOfDay.PM ) ) {
        if ( v.toString().equalsIgnoreCase( timeOfDay ) ) {
          return v;
        }
      }
      throw new EnumException( MSGS.invalidStringForTimeOfDay( timeOfDay ) );
    }
  } // end enum TimeOfDay

  /**
   * Names of enum are used as key in resource bundle.
   */
  public enum DayOfWeek {
    SUN( 0, "sunday" ), MON( 1, "monday" ), TUE( 2, "tuesday" ), WED( 3, "wednesday" ), THU( 4,
            "thursday" ), FRI( 5, "friday" ), SAT( 6, "saturday" );

    DayOfWeek( int value, String name ) {
      this.value = value;
      this.name = name;
    }

    private final int value;

    private final String name;

    private static DayOfWeek[] week = { SUN, MON, TUE, WED, THU, FRI, SAT };

    public int value() {
      return value;
    }

    public String toString() {
      return name;
    }

    public static DayOfWeek get( int idx ) {
      return week[idx];
    }

    public static int length() {
      return week.length;
    }

    public int getNext() {
      return ( ordinal() + 1 ) % values().length;
    }

    public int getPrevious() {
      return ( this == SUN ) ? SAT.ordinal() : ordinal() - 1;
    }
  } /* end enum */

  public enum LogLevel {
    BASIC( 0, "Basic" ), ERROR( 1, "Error" ), MINIMAL( 2, "Minimal" ), NOTHING( 3, "Nothing" ), DETAILED( 4,
        "Detailed" ), DEBUG( 5, "Debug" ), ROWLEVEL( 6, "Row Level(very detailed)" );

    LogLevel( int value, String name ) {
      this.value = value;
      this.name = name;
    }

    private final int value;

    private final String name;

    private static LogLevel[] log = { BASIC, ERROR, MINIMAL, NOTHING, DETAILED, DEBUG, ROWLEVEL };

    public int value() {
      return value;
    }

    public String toString() {
      return name;
    }

    public static LogLevel get( int idx ) {
      return log[idx];
    }

    public static int length() {
      return log.length;
    }

    public int getNext() {
      return ( ordinal() + 1 ) % values().length;
    }

    public int getPrevious() {
      return ( this == BASIC ) ? ROWLEVEL.ordinal() : ordinal() - 1;
    }
  } /* end enum */

  public enum MonthOfYear {
    JAN( 0, "january" ),
    FEB( 1, "february" ),
    MAR( 2, "march" ),
    APR( 3, "april" ),
    MAY( 4, "may" ),
    JUN( 5, "june" ),
    JUL( 6, "july" ),
    AUG( 7, "august" ),
    SEPT( 8, "september" ),
    OCT( 9, "october" ),
    NOV( 10, "november" ),
    DEC( 11, "december" );

    MonthOfYear( int value, String name ) {
      this.value = value;
      this.name = name;
    }

    private final int value;

    private final String name;

    private static MonthOfYear[] year = { JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEPT, OCT, NOV, DEC };

    public int value() {
      return value;
    }

    public String toString() {
      return name;
    }

    public static MonthOfYear get( int idx ) {
      return year[idx];
    }

    public static int length() {
      return year.length;
    }
  } /* end enum */

  public enum WeekOfMonth {
    FIRST( 0, "first_num" ), SECOND( 1, "second_num" ), THIRD( 2, "third_num" ), FOURTH( 3, "fourth_num" ), LAST( 4,
        "last_num" );

    WeekOfMonth( int value, String name ) {
      this.value = value;
      this.name = name;
    }

    private final int value;

    private final String name;

    private static WeekOfMonth[] week = { FIRST, SECOND, THIRD, FOURTH, LAST };

    public int value() {
      return value;
    }

    public String toString() {
      return name;
    }

    public static WeekOfMonth get( int idx ) {
      return week[idx];
    }

    public static int length() {
      return week.length;
    }

    public static WeekOfMonth stringToWeekOfMonth( String weekOfMonth ) throws EnumException {
      for ( WeekOfMonth v : EnumSet.range( WeekOfMonth.FIRST, WeekOfMonth.LAST ) ) {
        if ( v.toString().equals( weekOfMonth ) ) {
          return v;
        }
      }
      throw new EnumException( MSGS.invalidStringForWeekOfMonth( weekOfMonth ) );
    }
  } // end enum WeekOfMonth

  private static Map<MonthOfYear, Integer> validNumDaysOfMonth = new HashMap<MonthOfYear, Integer>();
  static {
    validNumDaysOfMonth.put( MonthOfYear.JAN, 31 );
    validNumDaysOfMonth.put( MonthOfYear.FEB, 29 );
    validNumDaysOfMonth.put( MonthOfYear.MAR, 31 );
    validNumDaysOfMonth.put( MonthOfYear.APR, 30 );
    validNumDaysOfMonth.put( MonthOfYear.MAY, 31 );
    validNumDaysOfMonth.put( MonthOfYear.JUN, 30 );
    validNumDaysOfMonth.put( MonthOfYear.JUL, 31 );
    validNumDaysOfMonth.put( MonthOfYear.AUG, 31 );
    validNumDaysOfMonth.put( MonthOfYear.SEPT, 30 );
    validNumDaysOfMonth.put( MonthOfYear.OCT, 31 );
    validNumDaysOfMonth.put( MonthOfYear.NOV, 30 );
    validNumDaysOfMonth.put( MonthOfYear.DEC, 31 );
  }

  private TimeUtil() {
  } // cannot create instance, static class

  public static long daysToSecs( long days ) {
    return hoursToSecs( days * HOURS_IN_DAY );
  }

  public static long hoursToSecs( long hours ) {
    return minutesToSecs( hours * MINUTES_IN_HOUR );
  }

  public static long minutesToSecs( long minutes ) {
    return minutes * SECONDS_IN_MINUTE;
  }

  public static long secsToMillisecs( long secs ) {
    return secs * MILLISECS_IN_SECONDS;
  }

  public static long secsToDays( long secs ) {
    return secs / HOURS_IN_DAY / MINUTES_IN_HOUR / SECONDS_IN_MINUTE;
  }

  public static long secsToHours( long secs ) {
    return secs / MINUTES_IN_HOUR / SECONDS_IN_MINUTE;
  }

  public static long secsToMinutes( long secs ) {
    return secs / SECONDS_IN_MINUTE;
  }

  public static boolean isSecondsWholeDay( long secs ) {
    return ( daysToSecs( secsToDays( secs ) ) ) == secs;
  }

  public static boolean isSecondsWholeHour( long secs ) {
    return ( hoursToSecs( secsToHours( secs ) ) ) == secs;
  }

  public static boolean isSecondsWholeMinute( long secs ) {
    return ( minutesToSecs( secsToMinutes( secs ) ) ) == secs;
  }

  /**
   * Time of day is element of {AM, PM}
   * 
   * @param hour
   * @return
   */
  public static TimeOfDay getTimeOfDayBy0To23Hour( String hour ) {
    return getTimeOfDayBy0To23Hour( Integer.parseInt( hour ) );
  }

  public static TimeOfDay getTimeOfDayBy0To23Hour( int hour ) {
    return hour < MAX_HOUR ? TimeOfDay.AM : TimeOfDay.PM;
  }

  /**
   * Convert a 24 hour time, where hours are 0-23, to a twelve hour time, where 0-23 maps to 0...11 AM and 0...11 PM
   * 
   * @param int0To23hour
   *          int integer in the range 0..23
   * @return int integer in the range 0..11
   */
  public static int to12HourClock( int int0To23hour ) {
    assert int0To23hour >= 0 && int0To23hour <= 23 : "int0To23hour is out of range"; //$NON-NLS-1$

    int int0To11 = int0To23hour < MAX_HOUR ? int0To23hour : int0To23hour - MAX_HOUR;
    return int0To11;
  }

  /**
   * @param time
   *          String it will look like: '17:17:00 PM' for 5:17 PM
   */
  public static String to12HourClock( String time ) {
    String[] parts = time.split( ":" ); //$NON-NLS-1$
    int hour = Integer.parseInt( parts[0] );
    if ( hour > MAX_HOUR ) {
      hour -= MAX_HOUR;
    }
    if ( hour == 0 ) {
      hour = MAX_HOUR;
    }
    return Integer.toString( hour ) + ":" + parts[1] + ":" + parts[2]; //$NON-NLS-1$//$NON-NLS-2$
  }

  /**
   * map 0..11 to 12,1..11
   */
  public static int map0Through11To12Through11( int int0To11 ) {
    return int0To11 == 0 ? 12 : int0To11;
  }

  // NOTE: this method will produce rounding errors, since it doesn't round, it truncates
  public static long millsecondsToSecs( long milliseconds ) {
    return milliseconds / MILLISECS_IN_SECONDS;
  }

  /**
   * TODO sbarkdull, gwt 1.5 has a DateTimeFormatter, change this method to use it.
   * 
   * sample output: May 21, 2008 8:29:21 PM This is consistent with the formatter constructed like this: DateFormat
   * formatter = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM, Locale.getDefault());
   */
  public static String getDateTimeString( String month, String dayInMonth, String year, String hour, String minute,
      String second, TimeOfDay timeOfDay ) {
    return new StringBuilder().append( getDateString( month, dayInMonth, year ) ).append( " " )
        .append( hour ).append( ":" )
        .append( minute ).append( ":" )
        .append( second ).append( " " )
        .append( timeOfDay.toString() ).toString();
  }

  // this code runs in a single thread, so it is ok to share these two DateTimeFormats
  private static DateTimeFormat dateFormatter = DateTimeFormat.getFormat( MSGS.dateFormat() );
  private static DateTimeFormat dateTimeFormatter = DateTimeFormat.getFormat( MSGS.dateFormatLongMedium() );

  public static Date getDateTime( String time, Date date ) {
    String strDate = dateFormatter.format( date );
    return dateTimeFormatter.parse( strDate + " " + time ); //$NON-NLS-1$
  }

  /**
   * 
   * @param strDate
   *          String representing the date, in this format: MMM dd, yyyy HH:mm:ss a
   * @return
   */
  public static Date getDate( String strDate ) {
    return dateTimeFormatter.parse( strDate );
  }

  /**
   * Get the time part of a date string.
   * 
   * @param dateTime
   *          String in this format: MMM dd, yyyy HH:mm:ss a
   * @return String HH:mm:ss a
   */
  public static String getTimePart( String dateTime ) {
    String[] parts = dateTime.split( "\\s" ); //$NON-NLS-1$

    // TODO sbarkdull, use StringBuilder
    return parts[3] + " " + parts[4]; //$NON-NLS-1$
  }

  /**
   * Get the time part of a date string.
   * 
   * @param dateTime
   *          Date
   * @return String HH:mm:ss a
   */
  public static String getTimePart( Date dateTime ) {
    String strDateTime = dateTimeFormatter.format( dateTime );
    return to12HourClock( getTimePart( strDateTime ) );
  }

  /**
   * Get the time part of a date string.
   * 
   * @param dateTime
   *          String in this format: MMM dd, yyyy HH:mm:ss a
   * @return String HH:mm:ss a
   */
  public static String getDatePart( String dateTime ) {
    String[] parts = dateTime.split( "\\s" ); //$NON-NLS-1$
    // TODO sbarkdull, use StringBuilder
    return parts[0] + " " + parts[1] + " " + parts[2]; //$NON-NLS-1$ //$NON-NLS-2$
  }

  public static String get0thTime() {
    // TODO sbarkdull, use StringBuilder
    return "12:00:00 " + TimeOfDay.AM.toString(); //$NON-NLS-1$
  }

  public static String zeroTimePart( String dateTime ) {
    // TODO sbarkdull, use StringBuilder
    return getDatePart( dateTime ) + " " + get0thTime(); //$NON-NLS-1$
  }

  public static Date zeroTimePart( Date dateTime ) {
    // TODO sbarkdull, use StringBuilder
    Date newDate = (Date) dateTime.clone();
    newDate.setHours( 0 );
    newDate.setSeconds( 0 );
    newDate.setMinutes( 0 );
    return newDate;
  }

  /**
   * TODO sbarkdull, gwt 1.5 has a DateTimeFormatter, change this method to use it.
   * 
   * sample output: May 21, 2008 This is consistent with the formatter constructed like this: DateFormat formatter =
   * DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
   */
  public static String getDateString( String month, String dayInMonth, String year ) {
    return new StringBuilder().append( month ).append( " " ) //$NON-NLS-1$
        .append( dayInMonth ).append( ", " ) //$NON-NLS-1$
        .append( year ).toString();
  }

  public static boolean isValidNumOfDaysForMonth( int numDays, MonthOfYear month ) {
    if ( numDays < 1 ) {
      return false;
    } else {
      return validNumDaysOfMonth.get( month ) <= numDays;
    }
  }

  /**
   * Is <param>num</param> between <param>low</param> and <param>high</param>, inclusive.
   * 
   * @param low
   * @param num
   * @param high
   * @return boolean true if <param>num</param> between <param>low</param> and <param>high</param>, inclusive, else
   *         false.
   */
  private static boolean isNumBetween( long low, long num, long high ) {
    return num >= low && num <= high;
  }

  public static boolean isDayOfMonth( int num ) {
    return isNumBetween( 1, num, 31 );
  }

  public static boolean isDayOfWeek( int num ) {
    return isNumBetween( 1, num, 7 );
  }

  public static boolean isWeekOfMonth( int num ) {
    return isNumBetween( 1, num, 4 );
  }

  public static boolean isMonthOfYear( int num ) {
    return isNumBetween( 1, num, 12 );
  }

  public static boolean isSecond( int num ) {
    return isNumBetween( 0, num, 59 );
  }

  public static boolean isMinute( int num ) {
    return isNumBetween( 0, num, 59 );
  }

  public static boolean isHour( int num ) {
    return isNumBetween( 0, num, 23 );
  }

  // private static final String MATCH_DATE_STRING_RE = "^[0-9]{1,2}$";
  // public boolean isDateStr( String strInt ) {
  // return MATCH_DATE_STRING_RE.matches( strInt );
  // }

  public static void main( String[] args ) {
    assert daysToSecs( 13 ) == 1123200 : ""; //$NON-NLS-1$
    assert daysToSecs( 13 ) != 1123201 : ""; //$NON-NLS-1$
    assert daysToSecs( 13 ) != 1123199 : ""; //$NON-NLS-1$

    assert hoursToSecs( 13 ) == 46800 : ""; //$NON-NLS-1$
    assert hoursToSecs( 13 ) != 46801 : ""; //$NON-NLS-1$
    assert hoursToSecs( 13 ) != 46799 : ""; //$NON-NLS-1$

    assert minutesToSecs( 13 ) == 780 : ""; //$NON-NLS-1$
    assert minutesToSecs( 13 ) != 781 : ""; //$NON-NLS-1$
    assert minutesToSecs( 13 ) != 779 : ""; //$NON-NLS-1$

    assert secsToDays( 1123200 ) == 13 : ""; //$NON-NLS-1$
    assert secsToDays( 1123201 ) != 13 : ""; //$NON-NLS-1$
    assert secsToDays( 1123199 ) != 13 : ""; //$NON-NLS-1$

    assert secsToHours( 46800 ) == 13 : ""; //$NON-NLS-1$
    assert secsToHours( 46801 ) != 13 : ""; //$NON-NLS-1$
    assert secsToHours( 46799 ) != 13 : ""; //$NON-NLS-1$

    assert secsToMinutes( 780 ) == 13 : ""; //$NON-NLS-1$
    assert secsToMinutes( 781 ) != 13 : ""; //$NON-NLS-1$
    assert secsToMinutes( 779 ) != 13 : ""; //$NON-NLS-1$

    assert isSecondsWholeDay( 1123200 ) : ""; //$NON-NLS-1$
    assert !isSecondsWholeDay( 1123201 ) : ""; //$NON-NLS-1$
    assert !isSecondsWholeDay( 1123199 ) : ""; //$NON-NLS-1$

    assert isSecondsWholeHour( 46800 ) : ""; //$NON-NLS-1$
    assert !isSecondsWholeHour( 46801 ) : ""; //$NON-NLS-1$
    assert !isSecondsWholeHour( 46799 ) : ""; //$NON-NLS-1$

    assert isSecondsWholeMinute( 780 ) : ""; //$NON-NLS-1$
    assert !isSecondsWholeMinute( 781 ) : ""; //$NON-NLS-1$
    assert !isSecondsWholeMinute( 779 ) : ""; //$NON-NLS-1$

    assert getTimeOfDayBy0To23Hour( "0" ) == TimeOfDay.AM : "hour 0 is AM"; //$NON-NLS-1$ //$NON-NLS-2$
    assert getTimeOfDayBy0To23Hour( "11" ) == TimeOfDay.AM : "hour 11 is AM"; //$NON-NLS-1$ //$NON-NLS-2$
    assert getTimeOfDayBy0To23Hour( "12" ) == TimeOfDay.PM : "hour 12 is PM"; //$NON-NLS-1$ //$NON-NLS-2$
    assert getTimeOfDayBy0To23Hour( "13" ) == TimeOfDay.PM : "hour 13 is PM"; //$NON-NLS-1$ //$NON-NLS-2$
    assert getTimeOfDayBy0To23Hour( "23" ) == TimeOfDay.PM : "hour 23 is PM"; //$NON-NLS-1$ //$NON-NLS-2$

    assert to12HourClock( 0 ) == ( 1 ) : "0 is 1"; //$NON-NLS-1$
    assert to12HourClock( 11 ) == ( 12 ) : "11 is 12"; //$NON-NLS-1$
    assert to12HourClock( 12 ) == ( 1 ) : "12 is 1"; //$NON-NLS-1$
    assert to12HourClock( 23 ) == ( 11 ) : "23 is 11"; //$NON-NLS-1$

    System.out.println( "done" ); //$NON-NLS-1$
  }

  /**
   * Calculates day variance based on target timezone information.
   *
   * There are two different types of target timezone information which should be handled:
   * timezone id - e.g. "Eastern Daylight Time (UTC-0500)"
   * dateTime format - e.g. "2018-02-27T07:30:00-05:00"
   *
   * @param selectedHour The hour selected by the user which is compared against the timezone diff between client and target
   * @param selectedMinute The minute selected by the user which is compared against the timezone diff between client and target
   * @param targetTimezoneInfo The target timezone information in the formats described above
   * @return The calculated day variance
   */
  public static int getDayVariance( int selectedHour, int selectedMinute, String targetTimezoneInfo ) {

    boolean isTimezoneId = targetTimezoneInfo.contains( "UTC" );

    double targetOffset = targetTimezoneInfo.endsWith( "Z" ) ? 0 : getTargetOffset( targetTimezoneInfo, isTimezoneId );
    double clientOffset = -getClientOffsetTimeZone() / MINUTES_IN_HOUR;
    double selectedTime = selectedHour + ( selectedMinute != 0 ? selectedMinute / (double) MINUTES_IN_HOUR : 0 );

    // if client side has the timezone ahead of target's timezone, then we should compare against client's start of the day
    // client2target -> -1 and target2client -> +1
    if ( clientOffset > targetOffset ) {
      double timezoneDiff = targetOffset - clientOffset;
      if ( selectedTime + timezoneDiff < 0 ) {
        return isTimezoneId ? -1 : 1;
      }
    // if client side has the timezone behind of target's timezone, then we should compare against client's end of the day
    // client2target -> +1 and target2client -> -1
    } else {
      double timezoneDiff = clientOffset - targetOffset;
      if ( selectedTime - timezoneDiff >= HOURS_IN_DAY ) {
        return isTimezoneId ? 1 : -1;
      }
    }
    return 0;
  }

  /**
   * Retrieves the target timezone offset based on the target's timezone information.
   * targetTimezoneInfo should be in timezone id format - e.g. "Eastern Daylight Time (UTC-0500)"
   * @param targetTimezoneInfo The target timezone information.
   * @return The target's timezone offset
   */
  public static double getTargetOffsetFromTimezoneString( String targetTimezoneInfo ) {
    return getTargetOffsetFromString( targetTimezoneInfo, "UTC([+-])(\\d{1,2})(\\d{2})", 1, 2, 3 );
  }

  /**
   * Retrieves the target timezone offset based on the target's timezone information.
   * targetTimezoneInfo should be in dateTime format - e.g. "2018-02-27T07:30:00-05:00"
   * @param targetTimezoneInfo The target timezone information.
   * @return The target's timezone offset
   */
  public static double getTargetOffsetFromDatetimeString( String targetTimezoneInfo ) {
    return getTargetOffsetFromString( targetTimezoneInfo, "(\\d{4})-(\\d{2})-(\\d{2})[T ](\\d{2}):(\\d{2}):(\\d{2})([+-])(\\d{2}):(\\d{2})", 7, 8, 9 );
  }

  private static double getTargetOffsetFromString( String targetTimezoneInfo, String regex, int signGroup, int hoursGroup, int minutesGroup ) {
    MatchResult match = RegExp.compile( regex ).exec( targetTimezoneInfo );
    double timeOffset = 0.0;
    if ( match != null ) {
      String sign = match.getGroup( signGroup );
      int hours = Integer.parseInt( match.getGroup( hoursGroup ) );
      int minutes = Integer.parseInt( match.getGroup( minutesGroup ) );
      timeOffset = hours + minutes / (double) MINUTES_IN_HOUR;
      return sign.equals( "+" ) ? timeOffset : -timeOffset;
    }
    return timeOffset;
  }

  /**
   * Retrieves the target timezone offset based on the target's timezone information.
   *
   * Note: The following two types of timezone information are supported:
   * timezone id - e.g. "Eastern Daylight Time (UTC-0500)"
   * dateTime format - e.g. "2018-02-27T07:30:00-05:00"
   *
   * @param targetTimezoneInfo The target timezone information.
   * @param isTimezoneId True if the format is timezone id, false if the format is dateTime.
   * @return The target's timezone offset
   */
  private static double getTargetOffset( String targetTimezoneInfo, boolean isTimezoneId ) {
    return isTimezoneId ? getTargetOffsetFromTimezoneString( targetTimezoneInfo ) : getTargetOffsetFromDatetimeString( targetTimezoneInfo );
  }

  /**
   * Given the current day of week, calculate the new day of week based on the variance.
   *
   * @param currentDay the current day
   * @param dayVariance the day variance which will be applied on the current day
   * @return the calculated day of week
   */
  public static int getDayOfWeek( DayOfWeek currentDay, int dayVariance ) {

    if ( dayVariance == 0 ) {
      return currentDay.ordinal();
    }
    return ( dayVariance > 0 ) ? currentDay.getNext() : currentDay.getPrevious();
  }

  public static int getLogLevel( LogLevel currentLog, int logVariance ) {

    if ( logVariance == 0 ) {
      return currentLog.ordinal();
    }
    return ( logVariance > 0 ) ? currentLog.getNext() : currentLog.getPrevious();
  }


  public static native double getClientOffsetTimeZone() /*-{
      return new Date().getTimezoneOffset();
  }-*/;
}
