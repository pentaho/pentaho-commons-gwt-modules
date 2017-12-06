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

package org.pentaho.mantle.client.workspace;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.mantle.client.dialogs.scheduling.ScheduleEditor;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith( GwtMockitoTestRunner.class )
public class JsJobTriggerTest {
  private JsJobTrigger jsJobTrigger;

  @Before
  public void setUp() throws Exception {
    jsJobTrigger = mock( JsJobTrigger.class );
  }

  @Test
  public void testConvertJsArrayStringToIntArray() throws Exception {
    doCallRealMethod().when( jsJobTrigger ).convertJsArrayStringToIntArray( any( JsArrayString.class ) );

    assertNull( jsJobTrigger.convertJsArrayStringToIntArray( null ) );

    final JsArrayString jsArrayString = mock( JsArrayString.class );
    final int length = 5;
    when( jsArrayString.length() ).thenReturn( length );
    when( jsArrayString.toString() ).thenReturn( "1,2,3,4,5" );
    final int[] ints = jsJobTrigger.convertJsArrayStringToIntArray( jsArrayString );

    assertEquals( length, ints.length );
    assertEquals( 1, ints[0] );
    assertEquals( 2, ints[1] );
    assertEquals( 3, ints[2] );
    assertEquals( 4, ints[3] );
    assertEquals( 5, ints[4] );
  }

  @Test
  public void testOldGetScheduleType() throws Exception {
    doCallRealMethod().when( jsJobTrigger ).oldGetScheduleType();

    when( jsJobTrigger.getType() ).thenReturn( "complexJobTrigger" );
    when( jsJobTrigger.getMonthlyRecurrences() ).thenReturn( new int[] { 1 } );
    when( jsJobTrigger.getDayOfMonthRecurrences() ).thenReturn( new int[] {} );
    assertEquals( "YEARLY", jsJobTrigger.oldGetScheduleType() );

    when( jsJobTrigger.getMonthlyRecurrences() ).thenReturn( new int[] {} );
    when( jsJobTrigger.getDayOfMonthRecurrences() ).thenReturn( new int[] { 1 } );
    assertEquals( "MONTHLY", jsJobTrigger.oldGetScheduleType() );

    when( jsJobTrigger.getMonthlyRecurrences() ).thenReturn( new int[] {} );
    when( jsJobTrigger.getDayOfMonthRecurrences() ).thenReturn( new int[] {} );
    when( jsJobTrigger.isQualifiedDayOfWeekRecurrence() ).thenReturn( true );
    assertEquals( "MONTHLY", jsJobTrigger.oldGetScheduleType() );

    when( jsJobTrigger.isQualifiedDayOfWeekRecurrence() ).thenReturn( false );
    when( jsJobTrigger.getDayOfWeekRecurrences() ).thenReturn( new int[2] );
    assertEquals( "WEEKLY", jsJobTrigger.oldGetScheduleType() );

    when( jsJobTrigger.getDayOfWeekRecurrences() ).thenReturn( new int[0] );
    assertNull( jsJobTrigger.oldGetScheduleType() );

    when( jsJobTrigger.getType() ).thenReturn( "simpleJobTrigger" );
    when( jsJobTrigger.getRepeatInterval() ).thenReturn( 86400 - 1 );
    assertEquals( "HOURLY", jsJobTrigger.oldGetScheduleType() );

    when( jsJobTrigger.getRepeatInterval() ).thenReturn( 604800 - 1 );
    assertEquals( "DAILY", jsJobTrigger.oldGetScheduleType() );

    when( jsJobTrigger.getRepeatInterval() ).thenReturn( 604800 );
    assertEquals( "WEEKLY", jsJobTrigger.oldGetScheduleType() );

    when( jsJobTrigger.getRepeatInterval() ).thenReturn( 604800 + 1 );
    assertNull( jsJobTrigger.oldGetScheduleType() );

    when( jsJobTrigger.getType() ).thenReturn( "" );
    assertNull( jsJobTrigger.oldGetScheduleType() );
  }

  @Test
  public void testCalcScheduleType() throws Exception {
    doCallRealMethod().when( jsJobTrigger ).calcScheduleType();

    when( jsJobTrigger.getType() ).thenReturn( "complexJobTrigger" );
    when( jsJobTrigger.getMonthlyRecurrences() ).thenReturn( new int[] { 1 } );
    when( jsJobTrigger.getDayOfMonthRecurrences() ).thenReturn( new int[] {} );
    assertEquals( ScheduleEditor.ScheduleType.YEARLY, jsJobTrigger.calcScheduleType() );

    when( jsJobTrigger.getMonthlyRecurrences() ).thenReturn( new int[] {} );
    when( jsJobTrigger.getDayOfMonthRecurrences() ).thenReturn( new int[] { 1 } );
    assertEquals( ScheduleEditor.ScheduleType.MONTHLY, jsJobTrigger.calcScheduleType() );

    when( jsJobTrigger.getMonthlyRecurrences() ).thenReturn( new int[] {} );
    when( jsJobTrigger.getDayOfMonthRecurrences() ).thenReturn( new int[] {} );
    when( jsJobTrigger.isQualifiedDayOfWeekRecurrence() ).thenReturn( true );
    assertEquals( ScheduleEditor.ScheduleType.MONTHLY, jsJobTrigger.calcScheduleType() );

    when( jsJobTrigger.isQualifiedDayOfWeekRecurrence() ).thenReturn( false );
    when( jsJobTrigger.isWorkDaysInWeek() ).thenReturn( true );
    assertEquals( ScheduleEditor.ScheduleType.DAILY, jsJobTrigger.calcScheduleType() );

    when( jsJobTrigger.isWorkDaysInWeek() ).thenReturn( false );
    assertEquals( ScheduleEditor.ScheduleType.WEEKLY, jsJobTrigger.calcScheduleType() );

    when( jsJobTrigger.getType() ).thenReturn( "simpleJobTrigger" );
    when( jsJobTrigger.getRepeatInterval() ).thenReturn( 0 );
    assertEquals( ScheduleEditor.ScheduleType.RUN_ONCE, jsJobTrigger.calcScheduleType() );

    when( jsJobTrigger.getRepeatInterval() ).thenReturn( 604800 );
    assertEquals( ScheduleEditor.ScheduleType.WEEKLY, jsJobTrigger.calcScheduleType() );

    when( jsJobTrigger.getRepeatInterval() ).thenReturn( 86400 );
    assertEquals( ScheduleEditor.ScheduleType.DAILY, jsJobTrigger.calcScheduleType() );

    when( jsJobTrigger.getRepeatInterval() ).thenReturn( 3600 );
    assertEquals( ScheduleEditor.ScheduleType.HOURS, jsJobTrigger.calcScheduleType() );

    when( jsJobTrigger.getRepeatInterval() ).thenReturn( 60 );
    assertEquals( ScheduleEditor.ScheduleType.MINUTES, jsJobTrigger.calcScheduleType() );

    when( jsJobTrigger.getRepeatInterval() ).thenReturn( 5 );
    assertEquals( ScheduleEditor.ScheduleType.SECONDS, jsJobTrigger.calcScheduleType() );

    when( jsJobTrigger.getType() ).thenReturn( "" );
    assertEquals( ScheduleEditor.ScheduleType.CRON, jsJobTrigger.calcScheduleType() );
  }

  @Test
  public void testIsWorkDaysInWeek() throws Exception {
    doCallRealMethod().when( jsJobTrigger ).isWorkDaysInWeek();

    when( jsJobTrigger.getDayOfWeekRecurrences() ).thenReturn( new int[6] );
    assertFalse( jsJobTrigger.isWorkDaysInWeek() );

    when( jsJobTrigger.getDayOfWeekRecurrences() ).thenReturn( new int[] { 1, 2, 3, 4, 5 } );
    assertFalse( jsJobTrigger.isWorkDaysInWeek() );

    when( jsJobTrigger.getDayOfWeekRecurrences() ).thenReturn( new int[] { 2, 3, 4, 5, 6 } );
    assertTrue( jsJobTrigger.isWorkDaysInWeek() );
  }
}
