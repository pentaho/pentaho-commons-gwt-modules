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
