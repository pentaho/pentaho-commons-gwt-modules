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

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.mantle.client.dialogs.scheduling.RunOnceEditor;

import java.util.Calendar;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith( GwtMockitoTestRunner.class )
public class RunOnceEditorValidatorTest {

  @Test
  public void testIsValid() throws Exception {
    final RunOnceEditor runOnceEditor = mock( RunOnceEditor.class );
    final RunOnceEditorValidator validator = new RunOnceEditorValidator( runOnceEditor );

    when( runOnceEditor.getStartDate() ).thenReturn( null );
    assertFalse( validator.isValid() );

    Calendar calendar = Calendar.getInstance();
    calendar.add( Calendar.SECOND, -1 );
    when( runOnceEditor.getStartDate() ).thenReturn( calendar.getTime() );
    when( runOnceEditor.getStartTime() ).thenReturn( DateTimeFormat.getFormat( "hh:mm:ss a" ).
        format( calendar.getTime() ) );
    assertFalse( validator.isValid() );

    calendar.add( Calendar.MINUTE, 1 );
    when( runOnceEditor.getStartDate() ).thenReturn( calendar.getTime() );
    when( runOnceEditor.getStartTime() ).thenReturn( DateTimeFormat.getFormat( "hh:mm:ss a" ).
        format( calendar.getTime() ) );
    assertTrue( validator.isValid() );
  }
}
