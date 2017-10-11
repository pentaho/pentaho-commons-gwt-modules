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

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.json.client.JSONArray;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyChar;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ScheduleParamsDialogTest {
  private ScheduleParamsDialog scheduleParamsDialog;

  @Before
  public void setUp() throws Exception {
    scheduleParamsDialog = mock( ScheduleParamsDialog.class );
  }

  @Test
  public void testOnKeyDownPreview() throws Exception {
    doCallRealMethod().when( scheduleParamsDialog ).onKeyDownPreview( anyChar(), anyInt() );

    assertTrue( scheduleParamsDialog.onKeyDownPreview( (char) KeyCodes.KEY_ENTER, -1 ) );
    verify( scheduleParamsDialog, never() ).hide();

    assertTrue( scheduleParamsDialog.onKeyDownPreview( (char) KeyCodes.KEY_ESCAPE, -1 ) );
    verify( scheduleParamsDialog ).hide();
  }

  @Test
  public void testBackClicked() throws Exception {
    doCallRealMethod().when( scheduleParamsDialog ).backClicked();

    scheduleParamsDialog.parentDialog = mock( ScheduleRecurrenceDialog.class );
    final JSONArray jsonArray = mock( JSONArray.class );
    when( scheduleParamsDialog.getScheduleParams( true ) ).thenReturn( jsonArray );
    scheduleParamsDialog.backClicked();
    assertEquals( jsonArray, scheduleParamsDialog.scheduleParams );
    verify( scheduleParamsDialog.parentDialog ).center();
    verify( scheduleParamsDialog ).hide();
  }
}
