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
import org.pentaho.mantle.client.dialogs.scheduling.ScheduleEditor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ScheduleEditorValidatorTest {

  @Test
  public void testIsValid() throws Exception {
    final ScheduleEditorValidator scheduleEditorValidator = mock( ScheduleEditorValidator.class );
    scheduleEditorValidator.scheduleEditor = mock( ScheduleEditor.class );
    scheduleEditorValidator.recurrenceEditorValidator = mock( RecurrenceEditorValidator.class );
    scheduleEditorValidator.runOnceEditorValidator = mock( RunOnceEditorValidator.class );
    scheduleEditorValidator.cronEditorValidator = mock( CronEditorValidator.class );
    scheduleEditorValidator.blockoutValidator = mock( BlockoutValidator.class );
    doCallRealMethod().when( scheduleEditorValidator ).isValid();

    testRecurrenceEditorValidator( scheduleEditorValidator, scheduleEditorValidator.runOnceEditorValidator,
        ScheduleEditor.ScheduleType.RUN_ONCE );
    testRecurrenceEditorValidator( scheduleEditorValidator, scheduleEditorValidator.recurrenceEditorValidator,
        ScheduleEditor.ScheduleType.SECONDS );
    testRecurrenceEditorValidator( scheduleEditorValidator, scheduleEditorValidator.recurrenceEditorValidator,
        ScheduleEditor.ScheduleType.MINUTES );
    testRecurrenceEditorValidator( scheduleEditorValidator, scheduleEditorValidator.recurrenceEditorValidator,
        ScheduleEditor.ScheduleType.HOURS );
    testRecurrenceEditorValidator( scheduleEditorValidator, scheduleEditorValidator.recurrenceEditorValidator,
        ScheduleEditor.ScheduleType.DAILY );
    testRecurrenceEditorValidator( scheduleEditorValidator, scheduleEditorValidator.recurrenceEditorValidator,
        ScheduleEditor.ScheduleType.WEEKLY );
    testRecurrenceEditorValidator( scheduleEditorValidator, scheduleEditorValidator.recurrenceEditorValidator,
        ScheduleEditor.ScheduleType.MONTHLY );
    testRecurrenceEditorValidator( scheduleEditorValidator, scheduleEditorValidator.recurrenceEditorValidator,
        ScheduleEditor.ScheduleType.YEARLY );
    testRecurrenceEditorValidator( scheduleEditorValidator, scheduleEditorValidator.cronEditorValidator,
        ScheduleEditor.ScheduleType.CRON );
  }

  private void testRecurrenceEditorValidator( ScheduleEditorValidator scheduleEditorValidator, IUiValidator validator,
      ScheduleEditor.ScheduleType type ) {
    when( scheduleEditorValidator.scheduleEditor.isBlockoutDialog() ).thenReturn( false );
    when( scheduleEditorValidator.scheduleEditor.getScheduleType() ).thenReturn( type );
    when( validator.isValid() ).thenReturn( false );
    assertFalse( scheduleEditorValidator.isValid() );

    when( validator.isValid() ).thenReturn( true );
    assertTrue( scheduleEditorValidator.isValid() );

    when( scheduleEditorValidator.scheduleEditor.isBlockoutDialog() ).thenReturn( true );
    assertFalse( scheduleEditorValidator.isValid() );
  }

  @Test
  public void testClear() throws Exception {
    final ScheduleEditorValidator scheduleEditorValidator = mock( ScheduleEditorValidator.class );
    scheduleEditorValidator.recurrenceEditorValidator = mock( RecurrenceEditorValidator.class );
    scheduleEditorValidator.runOnceEditorValidator = mock( RunOnceEditorValidator.class );
    scheduleEditorValidator.cronEditorValidator = mock( CronEditorValidator.class );

    doCallRealMethod().when( scheduleEditorValidator ).clear();

    scheduleEditorValidator.clear();

    verify( scheduleEditorValidator.recurrenceEditorValidator ).clear();
    verify( scheduleEditorValidator.runOnceEditorValidator ).clear();
    verify( scheduleEditorValidator.cronEditorValidator ).clear();
  }
}
