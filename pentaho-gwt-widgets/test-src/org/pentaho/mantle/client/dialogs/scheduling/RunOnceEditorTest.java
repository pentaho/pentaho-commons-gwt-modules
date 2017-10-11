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

import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.gwt.widgets.client.controls.DatePickerEx;
import org.pentaho.gwt.widgets.client.controls.TimePicker;
import org.pentaho.gwt.widgets.client.utils.TimeUtil;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class RunOnceEditorTest {
  private RunOnceEditor runOnceEditor;

  @Before
  public void setUp() throws Exception {
    runOnceEditor = mock( RunOnceEditor.class );
  }

  @Test
  @SuppressWarnings( "deprecation" )
  public void testReset() throws Exception {
    doCallRealMethod().when( runOnceEditor ).reset( any( Date.class ) );

    runOnceEditor.startTimePicker = mock( TimePicker.class );
    runOnceEditor.startDatePicker = mock( DatePickerEx.class );
    final DateBox dateBox = mock( DateBox.class );
    when( runOnceEditor.startDatePicker.getDatePicker() ).thenReturn( dateBox );

    final Date date = new Date();
    runOnceEditor.reset( date );
    verify( runOnceEditor.startTimePicker ).setTimeOfDay( TimeUtil.getTimeOfDayBy0To23Hour( date.getHours() ) );
    verify( runOnceEditor.startTimePicker ).setHour( TimeUtil.to12HourClock( date.getHours() ) );
    verify( runOnceEditor.startTimePicker ).setMinute( date.getMinutes() );
    verify( dateBox ).setValue( date );
  }
}
