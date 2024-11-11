/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
