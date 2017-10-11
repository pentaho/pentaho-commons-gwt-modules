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

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.gwt.widgets.client.utils.TimeUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class WeeklyRecurrenceEditorTest {
  private RecurrenceEditor.WeeklyRecurrenceEditor weeklyRecurrenceEditor;

  @Before
  public void setUp() throws Exception {
    weeklyRecurrenceEditor = mock( RecurrenceEditor.WeeklyRecurrenceEditor.class );
  }

  @Test
  @SuppressWarnings( "deprecation" )
  public void testReset() throws Exception {
    doCallRealMethod().when( weeklyRecurrenceEditor ).reset();

    final CheckBox checkBox = mock( CheckBox.class );
    weeklyRecurrenceEditor.dayToCheckBox = new HashMap<TimeUtil.DayOfWeek, CheckBox>() { {
        put( TimeUtil.DayOfWeek.MON, checkBox );
        put( TimeUtil.DayOfWeek.FRI, checkBox );
      } };

    weeklyRecurrenceEditor.reset();
    verify( checkBox, times( 2 ) ).setChecked( false );
  }

  @Test
  @SuppressWarnings( "deprecation" )
  public void testGetCheckedDays() throws Exception {
    doCallRealMethod().when( weeklyRecurrenceEditor ).getCheckedDays();

    final TimeUtil.DayOfWeek checkedDay = TimeUtil.DayOfWeek.FRI;
    final CheckBox checkBox1 = mock( CheckBox.class );
    when( checkBox1.isChecked() ).thenReturn( false );
    final CheckBox checkBox2 = mock( CheckBox.class );
    when( checkBox2.isChecked() ).thenReturn( true );
    weeklyRecurrenceEditor.dayToCheckBox = new HashMap<TimeUtil.DayOfWeek, CheckBox>() { {
        put( TimeUtil.DayOfWeek.SUN, checkBox1 );
        put( TimeUtil.DayOfWeek.MON, checkBox1 );
        put( TimeUtil.DayOfWeek.TUE, checkBox1 );
        put( TimeUtil.DayOfWeek.WED, checkBox1 );
        put( TimeUtil.DayOfWeek.THU, checkBox1 );
        put( checkedDay, checkBox2 );
        put( TimeUtil.DayOfWeek.SAT, checkBox1 );
      } };

    final List<TimeUtil.DayOfWeek> checkedDays = weeklyRecurrenceEditor.getCheckedDays();
    assertEquals( 1, checkedDays.size() );
    assertTrue( checkedDays.contains( checkedDay ) );
  }

  @Test
  public void testGetCheckedDaysAsString() throws Exception {
    doCallRealMethod().when( weeklyRecurrenceEditor ).getCheckedDaysAsString( anyInt() );

    when( weeklyRecurrenceEditor.getCheckedDays() ).thenReturn( new LinkedList<TimeUtil.DayOfWeek>() { {
        add( TimeUtil.DayOfWeek.MON );
        add( TimeUtil.DayOfWeek.FRI );
      } } );

    assertEquals( "2,6", weeklyRecurrenceEditor.getCheckedDaysAsString( 1 ) );
  }

  @Test
  @SuppressWarnings( "deprecation" )
  public void testSetCheckedDaysAsString() throws Exception {
    doCallRealMethod().when( weeklyRecurrenceEditor ).setCheckedDaysAsString( anyString(), anyInt() );

    final CheckBox checkBox1 = mock( CheckBox.class );
    weeklyRecurrenceEditor.dayToCheckBox = new HashMap<TimeUtil.DayOfWeek, CheckBox>() { {
        put( TimeUtil.DayOfWeek.SUN, null );
        put( TimeUtil.DayOfWeek.MON, checkBox1 );
        put( TimeUtil.DayOfWeek.TUE, null );
        put( TimeUtil.DayOfWeek.WED, null );
        put( TimeUtil.DayOfWeek.THU, null );
        put( TimeUtil.DayOfWeek.FRI, checkBox1 );
        put( TimeUtil.DayOfWeek.SAT, null );
      } };

    weeklyRecurrenceEditor.setCheckedDaysAsString( "2,6", 1 );
    verify( checkBox1, times( 2 ) ).setChecked( true );
  }

  @Test
  @SuppressWarnings( "deprecation" )
  public void testGetNumCheckedDays() throws Exception {
    doCallRealMethod().when( weeklyRecurrenceEditor ).getNumCheckedDays();

    final CheckBox checkBox1 = mock( CheckBox.class );
    when( checkBox1.isChecked() ).thenReturn( false );
    final CheckBox checkBox2 = mock( CheckBox.class );
    when( checkBox2.isChecked() ).thenReturn( true );
    weeklyRecurrenceEditor.dayToCheckBox = new HashMap<TimeUtil.DayOfWeek, CheckBox>() { {
        put( TimeUtil.DayOfWeek.SUN, checkBox1 );
        put( TimeUtil.DayOfWeek.MON, checkBox1 );
        put( TimeUtil.DayOfWeek.TUE, checkBox2 );
        put( TimeUtil.DayOfWeek.WED, checkBox1 );
        put( TimeUtil.DayOfWeek.THU, checkBox1 );
        put( TimeUtil.DayOfWeek.FRI, checkBox2 );
        put( TimeUtil.DayOfWeek.SAT, checkBox1 );
      } };

    assertEquals( 2, weeklyRecurrenceEditor.getNumCheckedDays() );
  }
}
