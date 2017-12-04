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
import org.pentaho.gwt.widgets.client.controls.DateRangeEditor;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith( GwtMockitoTestRunner.class )
public class DateRangeEditorValidatorTest {

  @Test
  public void testIsValid() throws Exception {
    final DateRangeEditor dateRangeEditor = mock( DateRangeEditor.class );
    final DateRangeEditorValidator validator = new DateRangeEditorValidator( dateRangeEditor );

    when( dateRangeEditor.getStartDate() ).thenReturn( null );
    assertFalse( validator.isValid() );

    when( dateRangeEditor.getStartDate() ).thenReturn( new Date() );
    when( dateRangeEditor.isEndBy() ).thenReturn( true );
    when( dateRangeEditor.getEndDate() ).thenReturn( null );
    assertFalse( validator.isValid() );

    when( dateRangeEditor.getEndDate() ).thenReturn( new Date() );
    assertTrue( validator.isValid() );

    when( dateRangeEditor.isEndBy() ).thenReturn( false );
    when( dateRangeEditor.getEndDate() ).thenReturn( null );
    assertTrue( validator.isValid() );
  }

  @Test
  public void testClear() throws Exception {
    final DateRangeEditor dateRangeEditor = mock( DateRangeEditor.class );
    final DateRangeEditorValidator validator = new DateRangeEditorValidator( dateRangeEditor );

    validator.clear();
    verify( dateRangeEditor ).setStartDateError( null );
    verify( dateRangeEditor ).setEndByError( null );
  }
}
