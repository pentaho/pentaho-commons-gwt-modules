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
import org.pentaho.mantle.client.dialogs.scheduling.CronEditor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith( GwtMockitoTestRunner.class )
public class CronEditorValidatorTest {

  @Test
  public void testIsValid() throws Exception {
    final CronEditor editor = mock( CronEditor.class );
    CronEditorValidator cronEditorValidator = new CronEditorValidator( editor );
    cronEditorValidator.dateRangeEditorValidator = mock( DateRangeEditorValidator.class );

    when( editor.getCronString() ).thenReturn( "" );
    when( cronEditorValidator.dateRangeEditorValidator.isValid() ).thenReturn( true );
    assertFalse( cronEditorValidator.isValid() );

    when( editor.getCronString() ).thenReturn( "0 33 6 ? * 1" );
    when( cronEditorValidator.dateRangeEditorValidator.isValid() ).thenReturn( false );
    assertFalse( cronEditorValidator.isValid() );

    when( cronEditorValidator.dateRangeEditorValidator.isValid() ).thenReturn( true );
    assertTrue( cronEditorValidator.isValid() );
  }

  @Test
  public void testClear() throws Exception {
    final CronEditor editor = mock( CronEditor.class );
    CronEditorValidator cronEditorValidator = new CronEditorValidator( editor );
    cronEditorValidator.dateRangeEditorValidator = mock( DateRangeEditorValidator.class );

    cronEditorValidator.clear();
    verify( editor ).setCronError( null );
    verify( cronEditorValidator.dateRangeEditorValidator ).clear();
  }
}
