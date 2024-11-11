/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


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
