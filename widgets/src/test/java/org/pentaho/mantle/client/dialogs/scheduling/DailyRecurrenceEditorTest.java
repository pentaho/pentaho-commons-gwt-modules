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
* Copyright (c) 2002-2022 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.mantle.client.dialogs.scheduling;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith( GwtMockitoTestRunner.class )
public class DailyRecurrenceEditorTest {
  private RecurrenceEditor.DailyRecurrenceEditor dailyRecurrenceEditor;

  @Before
  public void setUp() throws Exception {
    dailyRecurrenceEditor = mock( RecurrenceEditor.DailyRecurrenceEditor.class );
  }

  @Test
  public void testReset() throws Exception {
    doCallRealMethod().when( dailyRecurrenceEditor ).reset();

    dailyRecurrenceEditor.reset();
    verify( dailyRecurrenceEditor ).setDailyRepeatValue( "" );
    verify( dailyRecurrenceEditor ).setEveryNDays();
  }

  @Test
   @SuppressWarnings( "deprecation" )
   public void testSetEveryNDays() throws Exception {
    doCallRealMethod().when( dailyRecurrenceEditor ).setEveryNDays();

    dailyRecurrenceEditor.everyNDaysRb = mock( RadioButton.class );
    dailyRecurrenceEditor.everyWeekdayRb = mock( RadioButton.class );
    dailyRecurrenceEditor.ignoreDTSCb = mock( CheckBox.class );

    dailyRecurrenceEditor.setEveryNDays();
    verify( dailyRecurrenceEditor.everyNDaysRb ).setChecked( true );
    verify( dailyRecurrenceEditor.everyWeekdayRb ).setChecked( false );
  }

  @Test
  @SuppressWarnings( "deprecation" )
  public void testSetEveryWeekday() throws Exception {
    doCallRealMethod().when( dailyRecurrenceEditor ).setEveryWeekday();

    dailyRecurrenceEditor.everyNDaysRb = mock( RadioButton.class );
    dailyRecurrenceEditor.everyWeekdayRb = mock( RadioButton.class );
    dailyRecurrenceEditor.ignoreDTSCb = mock( CheckBox.class );
    dailyRecurrenceEditor.setEveryWeekday();
    verify( dailyRecurrenceEditor.everyNDaysRb ).setChecked( false );
    verify( dailyRecurrenceEditor.everyWeekdayRb ).setChecked( true );
  }
}
