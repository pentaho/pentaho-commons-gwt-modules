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

import com.google.gwt.user.client.ui.ListBox;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.gwt.widgets.client.controls.TimePicker;
import org.pentaho.mantle.client.dialogs.scheduling.validators.ScheduleEditorValidator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith( GwtMockitoTestRunner.class )
public class ScheduleEditorWizardPanelTest {
  private ScheduleEditorWizardPanel scheduleEditorWizardPanel;

  @Before
  public void setUp() throws Exception {
    scheduleEditorWizardPanel = mock( ScheduleEditorWizardPanel.class );
  }

  @Test
  public void testCanContinue() throws Exception {
    doCallRealMethod().when( scheduleEditorWizardPanel ).canContinue();

    scheduleEditorWizardPanel.scheduleEditorValidator = mock( ScheduleEditorValidator.class );

    when( scheduleEditorWizardPanel.scheduleEditorValidator.isValid() ).thenReturn( true );
    assertTrue( scheduleEditorWizardPanel.canContinue() );

    when( scheduleEditorWizardPanel.scheduleEditorValidator.isValid() ).thenReturn( false );
    assertFalse( scheduleEditorWizardPanel.canContinue() );
  }

  @Test
  public void testCanFinish() throws Exception {
    doCallRealMethod().when( scheduleEditorWizardPanel ).canFinish();

    scheduleEditorWizardPanel.scheduleEditorValidator = mock( ScheduleEditorValidator.class );

    when( scheduleEditorWizardPanel.scheduleEditorValidator.isValid() ).thenReturn( true );
    assertTrue( scheduleEditorWizardPanel.canFinish() );

    when( scheduleEditorWizardPanel.scheduleEditorValidator.isValid() ).thenReturn( false );
    assertFalse( scheduleEditorWizardPanel.canFinish() );
  }

  @Test
  public void testGetBlockoutStartTime() throws Exception {
    doCallRealMethod().when( scheduleEditorWizardPanel ).getBlockoutStartTime();

    scheduleEditorWizardPanel.scheduleEditor = mock( ScheduleEditor.class );

    when( scheduleEditorWizardPanel.scheduleEditor.getStartTimePicker() ).thenReturn( null );
    assertNull( scheduleEditorWizardPanel.getBlockoutStartTime() );

    final TimePicker timePicker = mock( TimePicker.class );
    final String time = "time";
    when( timePicker.getTime() ).thenReturn( time );
    when( scheduleEditorWizardPanel.scheduleEditor.getStartTimePicker() ).thenReturn( timePicker );
    assertEquals( time, scheduleEditorWizardPanel.getBlockoutStartTime() );
  }

  @Test
  public void testGetBlockoutEndTime() throws Exception {
    doCallRealMethod().when( scheduleEditorWizardPanel ).getBlockoutEndTime();

    scheduleEditorWizardPanel.scheduleEditor = mock( ScheduleEditor.class );

    when( scheduleEditorWizardPanel.scheduleEditor.getBlockoutEndTimePicker() ).thenReturn( null );
    assertNull( scheduleEditorWizardPanel.getBlockoutEndTime() );

    final TimePicker timePicker = mock( TimePicker.class );
    final String time = "time";
    when( timePicker.getTime() ).thenReturn( time );
    when( scheduleEditorWizardPanel.scheduleEditor.getBlockoutEndTimePicker() ).thenReturn( timePicker );
    assertEquals( time, scheduleEditorWizardPanel.getBlockoutEndTime() );
  }

  @Test
  public void testGetTimeZone() throws Exception {
    doCallRealMethod().when( scheduleEditorWizardPanel ).getTimeZone();

    scheduleEditorWizardPanel.scheduleEditor = mock( ScheduleEditor.class );

    when( scheduleEditorWizardPanel.scheduleEditor.getTimeZonePicker() ).thenReturn( null );
    assertNull( scheduleEditorWizardPanel.getTimeZone() );

    final ListBox listBox = mock( ListBox.class );
    when( scheduleEditorWizardPanel.scheduleEditor.getTimeZonePicker() ).thenReturn( listBox );

    when( listBox.getSelectedIndex() ).thenReturn( -1 );
    assertNull( scheduleEditorWizardPanel.getTimeZone() );

    final int selIndex = 1;
    when( listBox.getSelectedIndex() ).thenReturn( selIndex );
    final String selTZ = "GMT";
    when( listBox.getValue( selIndex ) ).thenReturn( selTZ );
    assertEquals( selTZ, scheduleEditorWizardPanel.getTimeZone() );
  }
}
