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

package org.pentaho.mantle.client.dialogs.scheduling.validators;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.gwt.widgets.client.controls.TimePicker;
import org.pentaho.gwt.widgets.client.utils.TimeUtil;
import org.pentaho.mantle.client.dialogs.scheduling.ScheduleEditor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith( GwtMockitoTestRunner.class )
public class BlockoutValidatorTest {

  @Test
  public void testIsValid() throws Exception {
    final ScheduleEditor scheduleEditor = mock( ScheduleEditor.class );
    final BlockoutValidator validator = new BlockoutValidator( scheduleEditor );

    when( scheduleEditor.getBlockoutEndsType() ).thenReturn( ScheduleEditor.ENDS_TYPE.DURATION );
    final ScheduleEditor.DurationValues durationValues = new ScheduleEditor.DurationValues();
    when( scheduleEditor.getDurationValues() ).thenReturn( durationValues );
    assertFalse( validator.isValid() );

    durationValues.days = 1;
    assertTrue( validator.isValid() );

    when( scheduleEditor.getBlockoutEndsType() ).thenReturn( ScheduleEditor.ENDS_TYPE.TIME );
    final TimePicker startTimePicker = mock( TimePicker.class );
    when( startTimePicker.getHour() ).thenReturn( "8" );
    when( startTimePicker.getMinute() ).thenReturn( "8" );
    when( startTimePicker.getTimeOfDay() ).thenReturn( TimeUtil.TimeOfDay.PM );
    when( scheduleEditor.getStartTimePicker() ).thenReturn( startTimePicker );

    final TimePicker endTimePicker = mock( TimePicker.class );
    when( endTimePicker.getHour() ).thenReturn( "8" );
    when( endTimePicker.getMinute() ).thenReturn( "8" );
    when( endTimePicker.getTimeOfDay() ).thenReturn( TimeUtil.TimeOfDay.PM );
    when( scheduleEditor.getBlockoutEndTimePicker() ).thenReturn( endTimePicker );
    assertFalse( validator.isValid() ); // equal start/end

    when( endTimePicker.getMinute() ).thenReturn( "7" );
    assertFalse( validator.isValid() ); // start after end

    when( endTimePicker.getMinute() ).thenReturn( "9" );
    assertTrue( validator.isValid() ); // end after start
  }
}
