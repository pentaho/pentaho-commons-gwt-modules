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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith( GwtMockitoTestRunner.class )
public class TimePickerTest {
  @Test
  public void testSetTimeDate() throws Exception {
    TimePicker timePicker = spy( new TimePicker() );
    timePicker.hourLB = mock( ListBox.class );
    timePicker.minuteLB = mock( ListBox.class );
    timePicker.timeOfDayLB = mock( ListBox.class );

    Date date = DateTimeFormat.getFormat( "HH:mm" ).parse( "16:44" );
    timePicker.setTime( date );

    verify( timePicker.hourLB ).setSelectedIndex( 3 );
    verify( timePicker.minuteLB ).setSelectedIndex( 44 );
    verify( timePicker.timeOfDayLB ).setSelectedIndex( 1 );
  }

  @Test
  public void testSetTimeString() throws Exception {
    TimePicker timePicker = spy( new TimePicker() );
    timePicker.hourLB = mock( ListBox.class );
    timePicker.minuteLB = mock( ListBox.class );
    timePicker.timeOfDayLB = mock( ListBox.class );

    timePicker.setTime( "7:12:28 PM" );

    verify( timePicker.hourLB ).setSelectedIndex( 6 );
    verify( timePicker.minuteLB ).setSelectedIndex( 12 );
    verify( timePicker.timeOfDayLB ).setSelectedIndex( 1 );
  }
}
