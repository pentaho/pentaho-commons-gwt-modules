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

package org.pentaho.gwt.widgets.client.controls;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.gwt.widgets.client.utils.TimeUtil;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class DateTimePickerTest {
  @Test
  public void testSetEnabled() throws Exception {
    DateTimePicker dtp = mock( DateTimePicker.class );
    doCallRealMethod().when( dtp ).setEnabled( anyBoolean() );
    dtp.datePicker = mock( DatePickerEx.class );
    dtp.timePicker = mock( TimePicker.class );
    when( dtp.datePicker.getDatePicker() ).thenReturn( mock( DateBox.class ) );

    dtp.setEnabled( true );
    verify( dtp.datePicker.getDatePicker() ).setEnabled( true );
    verify( dtp.timePicker ).setEnabled( true );
  }

  @Test
  public void testGetDate() throws Exception {
    DateTimePicker dtp = mock( DateTimePicker.class );
    doCallRealMethod().when( dtp ).getDate();
    dtp.datePicker = mock( DatePickerEx.class );
    dtp.datePicker.datePicker = mock( DateBox.class );
    final Date date = DateTimeFormat.getFormat( "yyyy.MM.dd hh:mm aaa" ).parse( "2015.10.19 03:37 PM" );
    when( dtp.datePicker.datePicker.getValue() ).thenReturn( date );
    when( dtp.datePicker.getSelectedDate() ).thenCallRealMethod();

    dtp.timePicker = mock( TimePicker.class );
    when( dtp.timePicker.getTimeOfDay() ).thenReturn( TimeUtil.TimeOfDay.PM );
    when( dtp.timePicker.getHour() ).thenReturn( DateTimeFormat.getFormat( "hh" ).format( date ) );
    when( dtp.timePicker.getMinute() ).thenReturn( DateTimeFormat.getFormat( "mm" ).format( date ) );

    assertEquals( date, dtp.getDate() );
  }
}
