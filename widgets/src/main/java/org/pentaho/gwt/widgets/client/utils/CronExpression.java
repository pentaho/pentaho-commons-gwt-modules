/*
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This file is a copy from the Quartz project - http://quartz-scheduler.org/ from version 1.5.1
 * with the following changes:
 *   a- Removed all non-GWT friendly classes (Locale, Calendar, StringTokenizer, ObjectOutputStream, etc.
 *   b- Re-implemented the buildExpression method using split instead of StringTokenizer
 *   c- Externalized all strings to the i18n package
 *   d- Code-formatted to meet Hitachi Vantara standards
 *   e- Removed extraneous helper methods not needed for cron validation
 *
 * These changes were expressly made to allow GWT compilation (read Javascript translation) for validation of
 * Quartz-specific cron expressions. All other comments and attributions remain intact.
 *
 * Copyright 2012 - 2017 Hitachi Vantara.  All rights reserved.
 *
 ***********************************************************************************************************************
 */

package org.pentaho.gwt.widgets.client.utils;

import org.pentaho.gwt.widgets.client.i18n.WidgetsLocalizedMessages;
import org.pentaho.gwt.widgets.client.i18n.WidgetsLocalizedMessagesSingleton;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Provides a parser and evaluator for unix-like cron expressions. Cron expressions provide the ability to specify
 * complex time combinations such as &quot;At 8:00am every Monday through Friday&quot; or &quot;At 1:30am every last
 * Friday of the month&quot;.
 * <P>
 * Cron expressions are comprised of 6 required fields and one optional field separated by white space. The fields
 * respectively are described as follows:
 * 
 * <table cellspacing="8">
 * <tr>
 * <th align="left">Field Name</th>
 * <th align="left">&nbsp;</th>
 * <th align="left">Allowed Values</th>
 * <th align="left">&nbsp;</th>
 * <th align="left">Allowed Special Characters</th>
 * </tr>
 * <tr>
 * <td align="left"><code>Seconds</code></td>
 * <td align="left">&nbsp;</th>
 * <td align="left"><code>0-59</code></td>
 * <td align="left">&nbsp;</th>
 * <td align="left"><code>, - * /</code></td>
 * </tr>
 * <tr>
 * <td align="left"><code>Minutes</code></td>
 * <td align="left">&nbsp;</th>
 * <td align="left"><code>0-59</code></td>
 * <td align="left">&nbsp;</th>
 * <td align="left"><code>, - * /</code></td>
 * </tr>
 * <tr>
 * <td align="left"><code>Hours</code></td>
 * <td align="left">&nbsp;</th>
 * <td align="left"><code>0-23</code></td>
 * <td align="left">&nbsp;</th>
 * <td align="left"><code>, - * /</code></td>
 * </tr>
 * <tr>
 * <td align="left"><code>Day-of-month</code></td>
 * <td align="left">&nbsp;</th>
 * <td align="left"><code>1-31</code></td>
 * <td align="left">&nbsp;</th>
 * <td align="left"><code>, - * ? / L W C</code></td>
 * </tr>
 * <tr>
 * <td align="left"><code>Month</code></td>
 * <td align="left">&nbsp;</th>
 * <td align="left"><code>1-12 or JAN-DEC</code></td>
 * <td align="left">&nbsp;</th>
 * <td align="left"><code>, - * /</code></td>
 * </tr>
 * <tr>
 * <td align="left"><code>Day-of-Week</code></td>
 * <td align="left">&nbsp;</th>
 * <td align="left"><code>1-7 or SUN-SAT</code></td>
 * <td align="left">&nbsp;</th>
 * <td align="left"><code>, - * ? / L #</code></td>
 * </tr>
 * <tr>
 * <td align="left"><code>Year (Optional)</code></td>
 * <td align="left">&nbsp;</th>
 * <td align="left"><code>empty, 1970-2099</code></td>
 * <td align="left">&nbsp;</th>
 * <td align="left"><code>, - * /</code></td>
 * </tr>
 * </table>
 * <P>
 * The '*' character is used to specify all values. For example, &quot;*&quot; in the minute field means &quot;every
 * minute&quot;.
 * <P>
 * The '?' character is allowed for the day-of-month and day-of-week fields. It is used to specify 'no specific value'.
 * This is useful when you need to specify something in one of the two fileds, but not the other.
 * <P>
 * The '-' character is used to specify ranges For example &quot;10-12&quot; in the hour field means &quot;the hours 10,
 * 11 and 12&quot;.
 * <P>
 * The ',' character is used to specify additional values. For example &quot;MON,WED,FRI&quot; in the day-of-week field
 * means &quot;the days Monday, Wednesday, and Friday&quot;.
 * <P>
 * The '/' character is used to specify increments. For example &quot;0/15&quot; in the seconds field means &quot;the
 * seconds 0, 15, 30, and 45&quot;. And &quot;5/15&quot; in the seconds field means &quot;the seconds 5, 20, 35, and
 * 50&quot;. Specifying '*' before the '/' is equivalent to specifying 0 is the value to start with. Essentially, for
 * each field in the expression, there is a set of numbers that can be turned on or off. For seconds and minutes, the
 * numbers range from 0 to 59. For hours 0 to 23, for days of the month 0 to 31, and for months 1 to 12. The
 * &quot;/&quot; character simply helps you turn on every &quot;nth&quot; value in the given set. Thus &quot;7/6&quot;
 * in the month field only turns on month &quot;7&quot;, it does NOT mean every 6th month, please note that subtlety.
 * <P>
 * The 'L' character is allowed for the day-of-month and day-of-week fields. This character is short-hand for
 * &quot;last&quot;, but it has different meaning in each of the two fields. For example, the value &quot;L&quot; in the
 * day-of-month field means &quot;the last day of the month&quot; - day 31 for January, day 28 for February on non-leap
 * years. If used in the day-of-week field by itself, it simply means &quot;7&quot; or &quot;SAT&quot;. But if used in
 * the day-of-week field after another value, it means &quot;the last xxx day of the month&quot; - for example
 * &quot;6L&quot; means &quot;the last friday of the month&quot;. When using the 'L' option, it is important not to
 * specify lists, or ranges of values, as you'll get confusing results.
 * <P>
 * The 'W' character is allowed for the day-of-month field. This character is used to specify the weekday
 * (Monday-Friday) nearest the given day. As an example, if you were to specify &quot;15W&quot; as the value for the
 * day-of-month field, the meaning is: &quot;the nearest weekday to the 15th of the month&quot;. So if the 15th is a
 * Saturday, the trigger will fire on Friday the 14th. If the 15th is a Sunday, the trigger will fire on Monday the
 * 16th. If the 15th is a Tuesday, then it will fire on Tuesday the 15th. However if you specify &quot;1W&quot; as the
 * value for day-of-month, and the 1st is a Saturday, the trigger will fire on Monday the 3rd, as it will not 'jump'
 * over the boundary of a month's days. The 'W' character can only be specified when the day-of-month is a single day,
 * not a range or list of days.
 * <P>
 * The 'L' and 'W' characters can also be combined for the day-of-month expression to yield 'LW', which translates to
 * &quot;last weekday of the month&quot;.
 * <P>
 * The '#' character is allowed for the day-of-week field. This character is used to specify &quot;the nth&quot; XXX day
 * of the month. For example, the value of &quot;6#3&quot; in the day-of-week field means the third Friday of the month
 * (day 6 = Friday and &quot;#3&quot; = the 3rd one in the month). Other examples: &quot;2#1&quot; = the first Monday of
 * the month and &quot;4#5&quot; = the fifth Wednesday of the month. Note that if you specify &quot;#5&quot; and there
 * is not 5 of the given day-of-week in the month, then no firing will occur that month.
 * <P>
 * <!--The 'C' character is allowed for the day-of-month and day-of-week fields. This character is short-hand for
 * "calendar". This means values are calculated against the associated calendar, if any. If no calendar is associated,
 * then it is equivalent to having an all-inclusive calendar. A value of "5C" in the day-of-month field means "the first
 * day included by the calendar on or after the 5th". A value of "1C" in the day-of-week field means
 * "the first day included by the calendar on or after sunday".-->
 * <P>
 * The legal characters and the names of months and days of the week are not case sensitive.
 * 
 * <p>
 * <b>NOTES:</b>
 * <ul>
 * <li>Support for specifying both a day-of-week and a day-of-month value is not complete (you'll need to use the '?'
 * character in on of these fields).</li>
 * </ul>
 * </p>
 * 
 * 
 * @author Sharada Jambula, James House
 * @author Contributions from Mads Henderson
 * @author Refactoring from CronTrigger to CronExpression by Aaron Craven
 */
public class CronExpression {

  private static final WidgetsLocalizedMessages MSGS = WidgetsLocalizedMessagesSingleton.getInstance().getMessages();

  protected static final int SECOND = 0;

  protected static final int MINUTE = 1;

  protected static final int HOUR = 2;

  protected static final int DAY_OF_MONTH = 3;

  protected static final int MONTH = 4;

  protected static final int DAY_OF_WEEK = 5;

  protected static final int YEAR = 6;

  protected static final int ALL_SPEC_INT = 99; // '*'

  protected static final int NO_SPEC_INT = 98; // '?'

  protected static final Integer ALL_SPEC = new Integer( ALL_SPEC_INT );

  protected static final Integer NO_SPEC = new Integer( NO_SPEC_INT );

  protected static Map monthMap = new HashMap( 20 );

  protected static Map dayMap = new HashMap( 60 );
  static {
    monthMap.put( "JAN", new Integer( 0 ) );
    monthMap.put( "FEB", new Integer( 1 ) );
    monthMap.put( "MAR", new Integer( 2 ) );
    monthMap.put( "APR", new Integer( 3 ) );
    monthMap.put( "MAY", new Integer( 4 ) );
    monthMap.put( "JUN", new Integer( 5 ) );
    monthMap.put( "JUL", new Integer( 6 ) );
    monthMap.put( "AUG", new Integer( 7 ) );
    monthMap.put( "SEP", new Integer( 8 ) );
    monthMap.put( "OCT", new Integer( 9 ) );
    monthMap.put( "NOV", new Integer( 10 ) );
    monthMap.put( "DEC", new Integer( 11 ) );

    dayMap.put( "SUN", new Integer( 1 ) );
    dayMap.put( "MON", new Integer( 2 ) );
    dayMap.put( "TUE", new Integer( 3 ) );
    dayMap.put( "WED", new Integer( 4 ) );
    dayMap.put( "THU", new Integer( 5 ) );
    dayMap.put( "FRI", new Integer( 6 ) );
    dayMap.put( "SAT", new Integer( 7 ) );
  }

  private String cronExpression = null;

  protected transient TreeSet seconds;

  protected transient TreeSet minutes;

  protected transient TreeSet hours;

  protected transient TreeSet daysOfMonth;

  protected transient TreeSet months;

  protected transient TreeSet daysOfWeek;

  protected transient TreeSet years;

  protected transient boolean lastdayOfWeek = false;

  protected transient int nthdayOfWeek = 0;

  protected transient boolean lastdayOfMonth = false;

  protected transient boolean nearestWeekday = false;

  protected transient boolean calendardayOfWeek = false;

  protected transient boolean calendardayOfMonth = false;

  protected transient boolean expressionParsed = false;

  /**
   * Constructs a new <CODE>CronExpression</CODE> based on the specified parameter.
   * 
   * @param cronExpression
   *          String representation of the cron expression the new object should represent
   * @throws java.text.ParseException
   *           if the string expression cannot be parsed into a valid <CODE>CronExpression</CODE>
   */
  public CronExpression( String cronExpression ) throws ParseException {
    if ( cronExpression == null ) {
      throw new IllegalArgumentException( MSGS.cronExpressionNull() );
    }

    this.cronExpression = cronExpression;

    buildExpression( cronExpression.toUpperCase() );
  }

  /**
   * Returns the string representation of the <CODE>CronExpression</CODE>
   * 
   * @return a string representation of the <CODE>CronExpression</CODE>
   */
  public String toString() {
    return cronExpression;
  }

  /**
   * Indicates whether the specified cron expression can be parsed into a valid cron expression
   * 
   * @param cronExpression
   *          the expression to evaluate
   * @return a boolean indicating whether the given expression is a valid cron expression
   */
  public static boolean isValidExpression( String cronExpression ) {

    try {
      new CronExpression( cronExpression );
    } catch ( ParseException pe ) {
      return false;
    }

    return true;
  }

  // //////////////////////////////////////////////////////////////////////////
  //
  // Expression Parsing Functions
  //
  // //////////////////////////////////////////////////////////////////////////

  protected void buildExpression( String expression ) throws ParseException {
    expressionParsed = true;

    try {

      if ( seconds == null ) {
        seconds = new TreeSet();
      }
      if ( minutes == null ) {
        minutes = new TreeSet();
      }
      if ( hours == null ) {
        hours = new TreeSet();
      }
      if ( daysOfMonth == null ) {
        daysOfMonth = new TreeSet();
      }
      if ( months == null ) {
        months = new TreeSet();
      }
      if ( daysOfWeek == null ) {
        daysOfWeek = new TreeSet();
      }
      if ( years == null ) {
        years = new TreeSet();
      }

      int exprOn = SECOND;

      String[] exprsTok = expression.split( " |\\t" );

      for ( int i = 0; i < exprsTok.length; i++ ) {
        if ( exprOn > YEAR ) {
          break;
        }
        String expr = exprsTok[i];
        String[] vtok = expr.split( "," );
        for ( int j = 0; j < vtok.length; j++ ) {
          String v = vtok[j];
          storeExpressionVals( 0, v, exprOn );
        }
        exprOn++;
      }

      if ( exprOn <= DAY_OF_WEEK ) {
        throw new ParseException( MSGS.cronUnexpectedEndOfExpression(), expression.length() );
      }

      if ( exprOn <= YEAR ) {
        storeExpressionVals( 0, "*", YEAR );
      }

    } catch ( ParseException pe ) {
      throw pe;
    } catch ( Exception e ) {
      throw new ParseException( MSGS.cronIllegalExpressionFormat( e.toString() ), 0 );
    }
  }

  protected int storeExpressionVals( int pos, String s, int type ) throws ParseException {
    int incr = 0;
    int i = skipWhiteSpace( pos, s );
    if ( i >= s.length() ) {
      return i;
    }
    char c = s.charAt( i );
    if ( ( c >= 'A' ) && ( c <= 'Z' ) && ( !s.equals( "L" ) ) && ( !s.equals( "LW" ) ) ) {
      String sub = s.substring( i, i + 3 );
      int sval = -1;
      int eval = -1;
      if ( type == MONTH ) {
        sval = getMonthNumber( sub ) + 1;
        if ( sval < 0 ) {
          throw new ParseException( MSGS.cronInvalidMonthValue( sub ), i );
        }
        if ( s.length() > i + 3 ) {
          c = s.charAt( i + 3 );
          if ( c == '-' ) {
            i += 4;
            sub = s.substring( i, i + 3 );
            eval = getMonthNumber( sub ) + 1;
            if ( eval < 0 ) {
              throw new ParseException( MSGS.cronInvalidMonthValue( sub ), i );
            }
          }
        }
      } else if ( type == DAY_OF_WEEK ) {
        sval = getDayOfWeekNumber( sub );
        if ( sval < 0 ) {
          throw new ParseException( MSGS.cronInvalidDOWValue( sub ), i );
        }
        if ( s.length() > i + 3 ) {
          c = s.charAt( i + 3 );
          if ( c == '-' ) {
            i += 4;
            sub = s.substring( i, i + 3 );
            eval = getDayOfWeekNumber( sub );
            if ( eval < 0 ) {
              throw new ParseException( MSGS.cronInvalidDOWValue( sub ), i );
            }
            if ( sval > eval ) {
              throw new ParseException( MSGS
                  .cronInvalidDOWSequence( Integer.toString( sval ), Integer.toString( eval ) ), i );
            }

          } else if ( c == '#' ) {
            try {
              i += 4;
              nthdayOfWeek = Integer.parseInt( s.substring( i ) );
              if ( nthdayOfWeek < 1 || nthdayOfWeek > 5 ) {
                throw new Exception();
              }
            } catch ( Exception e ) {
              throw new ParseException( MSGS.cronIllegalHashFollowingNumeric(), i );
            }
          } else if ( c == 'L' ) {
            lastdayOfWeek = true;
            i++;
          }
        }

      } else {
        throw new ParseException( MSGS.cronIllegalCharactersForPosition( sub ), i );
      }
      if ( eval != -1 ) {
        incr = 1;
      }
      addToSet( sval, eval, incr, type );
      return ( i + 3 );
    }

    if ( c == '?' ) {
      i++;
      if ( ( i + 1 ) < s.length() && ( s.charAt( i ) != ' ' && s.charAt( i + 1 ) != '\t' ) ) {
        throw new ParseException( MSGS.cronIllegalCharacterAfter( "?", String.valueOf( s.charAt( i ) ) ), i );
      }
      if ( type != DAY_OF_WEEK && type != DAY_OF_MONTH ) {
        throw new ParseException( MSGS.cronIllegalQuestionMark(), i );
      }
      if ( type == DAY_OF_WEEK && !lastdayOfMonth ) {
        int val = ( (Integer) daysOfMonth.last() ).intValue();
        if ( val == NO_SPEC_INT ) {
          throw new ParseException( MSGS.cronIllegalQuestionMark(), i );
        }
      }

      addToSet( NO_SPEC_INT, -1, 0, type );
      return i;
    }

    if ( c == '*' || c == '/' ) {
      if ( c == '*' && ( i + 1 ) >= s.length() ) {
        addToSet( ALL_SPEC_INT, -1, incr, type );
        return i + 1;
      } else if ( c == '/' && ( ( i + 1 ) >= s.length() || s.charAt( i + 1 ) == ' ' || s.charAt( i + 1 ) == '\t' ) ) {
        throw new ParseException( MSGS.cronIllegalSlash(), i );
      } else if ( c == '*' ) {
        i++;
      }
      c = s.charAt( i );
      if ( c == '/' ) { // is an increment specified?
        i++;
        if ( i >= s.length() ) {
          throw new ParseException( MSGS.cronUnexpectedEndOfString(), i );
        }

        incr = getNumericValue( s, i );

        i++;
        if ( incr > 10 ) {
          i++;
        }
        if ( incr > 59 && ( type == SECOND || type == MINUTE ) ) {
          throw new ParseException( MSGS.cronIllegalIncrement( "60", Integer.toString( incr ) ), i );
        } else if ( incr > 23 && ( type == HOUR ) ) {
          throw new ParseException( MSGS.cronIllegalIncrement( "24", Integer.toString( incr ) ), i );
        } else if ( incr > 31 && ( type == DAY_OF_MONTH ) ) {
          throw new ParseException( MSGS.cronIllegalIncrement( "31", Integer.toString( incr ) ), i );
        } else if ( incr > 7 && ( type == DAY_OF_WEEK ) ) {
          throw new ParseException( MSGS.cronIllegalIncrement( "7", Integer.toString( incr ) ), i );
        } else if ( incr > 12 && ( type == MONTH ) ) {
          throw new ParseException( MSGS.cronIllegalIncrement( "12", Integer.toString( incr ) ), i );
        }
      } else {
        incr = 1;
      }

      addToSet( ALL_SPEC_INT, -1, incr, type );
      return i;
    } else if ( c == 'L' ) {
      i++;
      if ( type == DAY_OF_MONTH ) {
        lastdayOfMonth = true;
      }
      if ( type == DAY_OF_WEEK ) {
        addToSet( 7, 7, 0, type );
      }
      if ( type == DAY_OF_MONTH && s.length() > i ) {
        c = s.charAt( i );
        if ( c == 'W' ) {
          nearestWeekday = true;
          i++;
        }
      }
      return i;
    } else if ( c >= '0' && c <= '9' ) {
      int val = Integer.parseInt( String.valueOf( c ) );
      i++;
      if ( i >= s.length() ) {
        addToSet( val, -1, -1, type );
      } else {
        c = s.charAt( i );
        if ( c >= '0' && c <= '9' ) {
          ValueSet vs = getValue( val, s, i );
          val = vs.value;
          i = vs.pos;
        }
        i = checkNext( i, s, val, type );
        return i;
      }
    } else {
      throw new ParseException( MSGS.cronUnexpectedCharacter( String.valueOf( c ) ), i );
    }

    return i;
  }

  protected int checkNext( int pos, String s, int val, int type ) throws ParseException {
    int end = -1;
    int i = pos;

    if ( i >= s.length() ) {
      addToSet( val, end, -1, type );
      return i;
    }

    char c = s.charAt( pos );

    if ( c == 'L' ) {
      if ( type == DAY_OF_WEEK ) {
        lastdayOfWeek = true;
      } else {
        throw new ParseException( MSGS.cronOptionIsNotValidHere( "L", Integer.toString( i ) ), i );
      }
      TreeSet set = getSet( type );
      set.add( new Integer( val ) );
      i++;
      return i;
    }

    if ( c == 'W' ) {
      if ( type == DAY_OF_MONTH ) {
        nearestWeekday = true;
      } else {
        throw new ParseException( MSGS.cronOptionIsNotValidHere( "W", Integer.toString( i ) ), i );
      }
      TreeSet set = getSet( type );
      set.add( new Integer( val ) );
      i++;
      return i;
    }

    if ( c == '#' ) {
      if ( type != DAY_OF_WEEK ) {
        throw new ParseException( MSGS.cronOptionIsNotValidHere( "#", Integer.toString( i ) ), i );
      }
      i++;
      try {
        nthdayOfWeek = Integer.parseInt( s.substring( i ) );
        if ( nthdayOfWeek < 1 || nthdayOfWeek > 5 ) {
          throw new Exception();
        }
      } catch ( Exception e ) {
        throw new ParseException( MSGS.cronIllegalHashFollowingNumeric(), i );
      }

      TreeSet set = getSet( type );
      set.add( new Integer( val ) );
      i++;
      return i;
    }

    if ( c == 'C' ) {
      if ( type == DAY_OF_WEEK ) {
        calendardayOfWeek = true;
      } else if ( type == DAY_OF_MONTH ) {
        calendardayOfMonth = true;
      } else {
        throw new ParseException( MSGS.cronOptionIsNotValidHere( "C", Integer.toString( i ) ), i );
      }
      TreeSet set = getSet( type );
      set.add( new Integer( val ) );
      i++;
      return i;
    }

    if ( c == '-' ) {
      i++;
      c = s.charAt( i );
      int v = Integer.parseInt( String.valueOf( c ) );
      end = v;
      i++;
      if ( i >= s.length() ) {
        addToSet( val, end, 1, type );
        return i;
      }
      c = s.charAt( i );
      if ( c >= '0' && c <= '9' ) {
        ValueSet vs = getValue( v, s, i );
        int v1 = vs.value;
        end = v1;
        i = vs.pos;
      }
      if ( i < s.length() && ( ( c = s.charAt( i ) ) == '/' ) ) {
        i++;
        c = s.charAt( i );
        int v2 = Integer.parseInt( String.valueOf( c ) );
        i++;
        if ( i >= s.length() ) {
          addToSet( val, end, v2, type );
          return i;
        }
        c = s.charAt( i );
        if ( c >= '0' && c <= '9' ) {
          ValueSet vs = getValue( v2, s, i );
          int v3 = vs.value;
          addToSet( val, end, v3, type );
          i = vs.pos;
          return i;
        } else {
          addToSet( val, end, v2, type );
          return i;
        }
      } else {
        addToSet( val, end, 1, type );
        return i;
      }
    }

    if ( c == '/' ) {
      i++;
      c = s.charAt( i );
      int v2 = Integer.parseInt( String.valueOf( c ) );
      i++;
      if ( i >= s.length() ) {
        addToSet( val, end, v2, type );
        return i;
      }
      c = s.charAt( i );
      if ( c >= '0' && c <= '9' ) {
        ValueSet vs = getValue( v2, s, i );
        int v3 = vs.value;
        addToSet( val, end, v3, type );
        i = vs.pos;
        return i;
      } else {
        throw new ParseException( MSGS.cronUnexpectedCharacterAfterSlash( String.valueOf( c ) ), i );
      }
    }

    addToSet( val, end, 0, type );
    i++;
    return i;
  }

  public String getCronExpression() {
    return cronExpression;
  }

  protected int skipWhiteSpace( int i, String s ) {
    for ( ; i < s.length(); i++ ) {
      if ( s.charAt( i ) == ' ' || s.charAt( i ) == '\t' ) {
        break;
      }
    }

    return i;
  }

  protected int findNextWhiteSpace( int i, String s ) {
    for ( ; i < s.length(); i++ ) {
      if ( s.charAt( i ) != ' ' || s.charAt( i ) != '\t' ) {
        break;
      }
    }
    return i;
  }

  protected void addToSet( int val, int end, int incr, int type ) throws ParseException {
    TreeSet set = getSet( type );

    if ( type == SECOND || type == MINUTE ) {
      if ( ( val < 0 || val > 59 || end > 59 ) && ( val != ALL_SPEC_INT ) ) {
        throw new ParseException( MSGS.cronInvalidMinuteSecondValue(), -1 );
      }
    } else if ( type == HOUR ) {
      if ( ( val < 0 || val > 23 || end > 23 ) && ( val != ALL_SPEC_INT ) ) {
        throw new ParseException( MSGS.cronInvalidHourValue(), -1 );
      }
    } else if ( type == DAY_OF_MONTH ) {
      if ( ( val < 1 || val > 31 || end > 31 ) && ( val != ALL_SPEC_INT ) && ( val != NO_SPEC_INT ) ) {
        throw new ParseException( MSGS.cronInvalidDayOfMonthValue(), -1 );
      }
    } else if ( type == MONTH ) {
      if ( ( val < 1 || val > 12 || end > 12 ) && ( val != ALL_SPEC_INT ) ) {
        throw new ParseException( MSGS.cronInvalidMonthValueGeneral(), -1 );
      }
    } else if ( type == DAY_OF_WEEK ) {
      if ( ( val == 0 || val > 7 || end > 7 ) && ( val != ALL_SPEC_INT ) && ( val != NO_SPEC_INT ) ) {
        throw new ParseException( MSGS.cronInvalidDayOfWeekValue(), -1 );
      }
    }

    if ( ( incr == 0 || incr == -1 ) && val != ALL_SPEC_INT ) {
      if ( val != -1 ) {
        set.add( new Integer( val ) );
      } else {
        set.add( NO_SPEC );
      }
      return;
    }

    int startAt = val;
    int stopAt = end;

    if ( val == ALL_SPEC_INT && incr <= 0 ) {
      incr = 1;
      set.add( ALL_SPEC ); // put in a marker, but also fill values
    }

    if ( type == SECOND || type == MINUTE ) {
      if ( stopAt == -1 ) {
        stopAt = 59;
      }
      if ( startAt == -1 || startAt == ALL_SPEC_INT ) {
        startAt = 0;
      }
    } else if ( type == HOUR ) {
      if ( stopAt == -1 ) {
        stopAt = 23;
      }
      if ( startAt == -1 || startAt == ALL_SPEC_INT ) {
        startAt = 0;
      }
    } else if ( type == DAY_OF_MONTH ) {
      if ( stopAt == -1 ) {
        stopAt = 31;
      }
      if ( startAt == -1 || startAt == ALL_SPEC_INT ) {
        startAt = 1;
      }
    } else if ( type == MONTH ) {
      if ( stopAt == -1 ) {
        stopAt = 12;
      }
      if ( startAt == -1 || startAt == ALL_SPEC_INT ) {
        startAt = 1;
      }
    } else if ( type == DAY_OF_WEEK ) {
      if ( stopAt == -1 ) {
        stopAt = 7;
      }
      if ( startAt == -1 || startAt == ALL_SPEC_INT ) {
        startAt = 1;
      }
    } else if ( type == YEAR ) {
      if ( stopAt == -1 ) {
        stopAt = 2099;
      }
      if ( startAt == -1 || startAt == ALL_SPEC_INT ) {
        startAt = 1970;
      }
    }

    for ( int i = startAt; i <= stopAt; i += incr ) {
      set.add( new Integer( i ) );
    }
  }

  protected TreeSet getSet( int type ) {
    switch ( type ) {
      case SECOND:
        return seconds;
      case MINUTE:
        return minutes;
      case HOUR:
        return hours;
      case DAY_OF_MONTH:
        return daysOfMonth;
      case MONTH:
        return months;
      case DAY_OF_WEEK:
        return daysOfWeek;
      case YEAR:
        return years;
      default:
        return null;
    }
  }

  protected ValueSet getValue( int v, String s, int i ) {
    char c = s.charAt( i );
    String s1 = String.valueOf( v );
    while ( c >= '0' && c <= '9' ) {
      s1 += c;
      i++;
      if ( i >= s.length() ) {
        break;
      }
      c = s.charAt( i );
    }
    ValueSet val = new ValueSet();
    if ( i < s.length() ) {
      val.pos = i;
    } else {
      val.pos = i + 1;
    }
    val.value = Integer.parseInt( s1 );
    return val;
  }

  protected int getNumericValue( String s, int i ) {
    int endOfVal = findNextWhiteSpace( i, s );
    String val = s.substring( i, endOfVal );
    return Integer.parseInt( val );
  }

  protected int getMonthNumber( String s ) {
    Integer integer = (Integer) monthMap.get( s );

    if ( integer == null ) {
      return -1;
    }

    return integer.intValue();
  }

  protected int getDayOfWeekNumber( String s ) {
    Integer integer = (Integer) dayMap.get( s );

    if ( integer == null ) {
      return -1;
    }

    return integer.intValue();
  }

}

class ValueSet {
  public int value;

  public int pos;
}
