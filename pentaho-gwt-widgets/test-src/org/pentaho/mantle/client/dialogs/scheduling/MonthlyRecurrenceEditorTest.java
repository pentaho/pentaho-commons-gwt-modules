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

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith( GwtMockitoTestRunner.class )
public class MonthlyRecurrenceEditorTest {
  private RecurrenceEditor.MonthlyRecurrenceEditor monthlyRecurrenceEditor;

  @Before
  public void setUp() throws Exception {
    monthlyRecurrenceEditor = mock( RecurrenceEditor.MonthlyRecurrenceEditor.class );
  }

  @Test
  public void testReset() throws Exception {
    doCallRealMethod().when( monthlyRecurrenceEditor ).reset();

    monthlyRecurrenceEditor.reset();
    verify( monthlyRecurrenceEditor ).setDayNOfMonth();
    verify( monthlyRecurrenceEditor ).setDayOfMonth( "" );
    verify( monthlyRecurrenceEditor ).setWeekOfMonth( TimeUtil.WeekOfMonth.FIRST );
    verify( monthlyRecurrenceEditor ).setDayOfWeek( TimeUtil.DayOfWeek.SUN );
  }

  @Test
  @SuppressWarnings( "deprecation" )
  public void testSetDayNOfMonth() throws Exception {
    doCallRealMethod().when( monthlyRecurrenceEditor ).setDayNOfMonth();

    monthlyRecurrenceEditor.dayNOfMonthRb = mock( RadioButton.class );
    monthlyRecurrenceEditor.nthDayNameOfMonthRb = mock( RadioButton.class );

    monthlyRecurrenceEditor.setDayNOfMonth();
    verify( monthlyRecurrenceEditor.dayNOfMonthRb ).setChecked( true );
    verify( monthlyRecurrenceEditor.nthDayNameOfMonthRb ).setChecked( false );
  }

  @Test
  @SuppressWarnings( "deprecation" )
  public void testSetNthDayNameOfMonth() throws Exception {
    doCallRealMethod().when( monthlyRecurrenceEditor ).setNthDayNameOfMonth();

    monthlyRecurrenceEditor.dayNOfMonthRb = mock( RadioButton.class );
    monthlyRecurrenceEditor.nthDayNameOfMonthRb = mock( RadioButton.class );

    monthlyRecurrenceEditor.setNthDayNameOfMonth();
    verify( monthlyRecurrenceEditor.dayNOfMonthRb ).setChecked( false );
    verify( monthlyRecurrenceEditor.nthDayNameOfMonthRb ).setChecked( true );
  }
}
