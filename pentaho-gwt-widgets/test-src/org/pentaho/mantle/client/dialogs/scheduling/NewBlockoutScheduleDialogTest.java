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

import com.google.gwt.json.client.JSONObject;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.gwt.widgets.client.dialogs.IDialogCallback;
import org.pentaho.mantle.client.workspace.JsJob;
import org.pentaho.mantle.client.workspace.JsJobTrigger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class NewBlockoutScheduleDialogTest {

  @Test
  public void testOnFinish() throws Exception {
    final NewBlockoutScheduleDialog dialog = mock( NewBlockoutScheduleDialog.class );
    doCallRealMethod().when( dialog ).onFinish();

    final JsJobTrigger jsJobTrigger = mock( JsJobTrigger.class );
    when( dialog.getJsJobTrigger() ).thenReturn( jsJobTrigger );
    final JSONObject schedule = mock( JSONObject.class );
    when( dialog.getSchedule() ).thenReturn( schedule );
    final IDialogCallback callback = mock( IDialogCallback.class );
    when( dialog.getCallback() ).thenReturn( callback );

    dialog.updateMode = false;
    assertTrue( dialog.onFinish() );
    verify( dialog ).addBlockoutPeriod( eq( schedule ), eq( jsJobTrigger ), contains( "add" ) );
    verify( callback ).okPressed();

    dialog.updateMode = true;
    dialog.editJob = mock( JsJob.class );
    when( dialog.editJob.getJobId() ).thenReturn( "jobID" );
    assertTrue( dialog.onFinish() );
    verify( dialog ).addBlockoutPeriod( eq( schedule ), eq( jsJobTrigger ), contains( "update" ) );
    verify( callback, times( 2 ) ).okPressed();
  }

  @Test
  public void testSetUpdateMode() throws Exception {
    final NewBlockoutScheduleDialog dialog = mock( NewBlockoutScheduleDialog.class );
    doCallRealMethod().when( dialog ).setUpdateMode();

    dialog.updateMode = false;
    dialog.setUpdateMode();
    assertTrue( dialog.updateMode );
    verify( dialog ).setNewSchedule( dialog.updateMode );
  }
}
