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

import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyChar;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

@RunWith( GwtMockitoTestRunner.class )
@WithClassesToStub( JSONArray.class )
public class ScheduleParamsDialogTest {
  private ScheduleParamsDialog scheduleParamsDialog;
  private ScheduleParamsWizardPanel scheduleParamsWizardPanel;

  @Before
  public void setUp() throws Exception {
    scheduleParamsDialog = mock( ScheduleParamsDialog.class );
    scheduleParamsWizardPanel = mock( ScheduleParamsWizardPanel.class );
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

  @Test
  public void getScheduleParamsWithoutScheduleParams() {
    JSONObject jobSchedule = mock( JSONObject.class );

    when( scheduleParamsDialog.getScheduleParams( true ) ).thenCallRealMethod();
    when( scheduleParamsWizardPanel.getParams( true ) ).thenReturn( mock( JsArray.class ) );

    setInternalState( scheduleParamsDialog, "scheduleParamsWizardPanel", scheduleParamsWizardPanel );
    setInternalState( scheduleParamsDialog, "jobSchedule", jobSchedule );

    JSONArray params = scheduleParamsDialog.getScheduleParams( true );
    assertEquals( 0, params.size() );
  }

}
