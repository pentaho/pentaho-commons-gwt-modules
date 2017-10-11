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

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith( GwtMockitoTestRunner.class )
public class DatePickerExTest {
  @Test
  public void testGetSelectedDate() throws Exception {
    DatePickerEx dp = mock( DatePickerEx.class );
    doCallRealMethod().when( dp ).getSelectedDate();
    dp.datePicker = mock( DateBox.class );

    when( dp.datePicker.getValue() ).thenReturn( null );
    assertNull( dp.getSelectedDate() );

    when( dp.datePicker.getValue() ).thenReturn( new Date() );
    final Date selectedDate = dp.getSelectedDate();
    assertNotNull( selectedDate );
    assertEquals( "00:00:00", DateTimeFormat.getFormat( "HH:mm:ss" ).format( selectedDate ) );
  }
}
