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

import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.gwt.widgets.client.utils.TimeUtil;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class YearlyRecurrenceEditorTest {
  private RecurrenceEditor.YearlyRecurrenceEditor yearlyRecurrenceEditor;

  @Before
  public void setUp() throws Exception {
    yearlyRecurrenceEditor = mock( RecurrenceEditor.YearlyRecurrenceEditor.class );
  }

  @Test
  public void testReset() throws Exception {
    doCallRealMethod().when( yearlyRecurrenceEditor ).reset();

    yearlyRecurrenceEditor.reset();
    verify( yearlyRecurrenceEditor ).setEveryMonthOnNthDay();
    verify( yearlyRecurrenceEditor ).setMonthOfYear0( TimeUtil.MonthOfYear.JAN );
    verify( yearlyRecurrenceEditor ).setDayOfMonth( "" );
    verify( yearlyRecurrenceEditor ).setWeekOfMonth( TimeUtil.WeekOfMonth.FIRST );
    verify( yearlyRecurrenceEditor ).setDayOfWeek( TimeUtil.DayOfWeek.SUN );
    verify( yearlyRecurrenceEditor ).setMonthOfYear1( TimeUtil.MonthOfYear.JAN );
  }

  @Test
  @SuppressWarnings( "deprecation" )
  public void testSetEveryMonthOnNthDay() throws Exception {
    doCallRealMethod().when( yearlyRecurrenceEditor ).setEveryMonthOnNthDay();

    yearlyRecurrenceEditor.everyMonthOnNthDayRb = mock( RadioButton.class );
    yearlyRecurrenceEditor.nthDayNameOfMonthNameRb = mock( RadioButton.class );

    yearlyRecurrenceEditor.setEveryMonthOnNthDay();
    verify( yearlyRecurrenceEditor.everyMonthOnNthDayRb ).setChecked( true );
    verify( yearlyRecurrenceEditor.nthDayNameOfMonthNameRb ).setChecked( false );
  }

  @Test
  @SuppressWarnings( "deprecation" )
  public void testSetNthDayNameOfMonthName() throws Exception {
    doCallRealMethod().when( yearlyRecurrenceEditor ).setNthDayNameOfMonthName();

    yearlyRecurrenceEditor.everyMonthOnNthDayRb = mock( RadioButton.class );
    yearlyRecurrenceEditor.nthDayNameOfMonthNameRb = mock( RadioButton.class );

    yearlyRecurrenceEditor.setNthDayNameOfMonthName();
    verify( yearlyRecurrenceEditor.everyMonthOnNthDayRb ).setChecked( false );
    verify( yearlyRecurrenceEditor.nthDayNameOfMonthNameRb ).setChecked( true );
  }
}
