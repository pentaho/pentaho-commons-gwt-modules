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
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.gwt.widgets.client.dialogs.PromptDialogBox;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyChar;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ScheduleRecurrenceDialogTest {
  private ScheduleRecurrenceDialog scheduleRecurrenceDialog;

  @Before
  public void setUp() throws Exception {
    scheduleRecurrenceDialog = mock( ScheduleRecurrenceDialog.class );
  }

  @Test
  public void testOnKeyDownPreview() throws Exception {
    doCallRealMethod().when( scheduleRecurrenceDialog ).onKeyDownPreview( anyChar(), anyInt() );

    assertTrue( scheduleRecurrenceDialog.onKeyDownPreview( (char) KeyCodes.KEY_ENTER, -1 ) );
    verify( scheduleRecurrenceDialog, never() ).hide();

    assertTrue( scheduleRecurrenceDialog.onKeyDownPreview( (char) KeyCodes.KEY_ESCAPE, -1 ) );
    verify( scheduleRecurrenceDialog ).hide();
  }

  @Test
  public void testAddCustomPanel() throws Exception {
    doCallRealMethod().when( scheduleRecurrenceDialog ).addCustomPanel( any( Widget.class ), any(
        DockPanel.DockLayoutConstant.class ) );

    scheduleRecurrenceDialog.scheduleEditorWizardPanel = mock( ScheduleEditorWizardPanel.class );

    final DockPanel.DockLayoutConstant position = mock( DockPanel.DockLayoutConstant.class );
    final Widget widget = mock( Widget.class );
    scheduleRecurrenceDialog.addCustomPanel( widget, position );
    verify( scheduleRecurrenceDialog.scheduleEditorWizardPanel ).add( widget, position );
  }

  @Test
  public void testBackClicked() throws Exception {
    doCallRealMethod().when( scheduleRecurrenceDialog ).backClicked();

    doCallRealMethod().when( scheduleRecurrenceDialog ).setParentDialog( any( PromptDialogBox.class ) );

    final PromptDialogBox parentDialog = mock( PromptDialogBox.class );
    scheduleRecurrenceDialog.setParentDialog( parentDialog );
    scheduleRecurrenceDialog.backClicked();
    verify( scheduleRecurrenceDialog ).hide();
    verify( parentDialog ).center();
  }
}
