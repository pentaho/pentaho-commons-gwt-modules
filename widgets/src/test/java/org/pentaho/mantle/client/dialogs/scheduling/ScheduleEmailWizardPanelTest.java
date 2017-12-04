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

import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ScheduleEmailWizardPanelTest {
  private ScheduleEmailWizardPanel scheduleEmailWizardPanel;

  @Before
  public void setUp() throws Exception {
    scheduleEmailWizardPanel = mock( ScheduleEmailWizardPanel.class );
  }

  @Test
  public void testGetEmailParams() throws Exception {
    doCallRealMethod().when( scheduleEmailWizardPanel ).getEmailParams();

    scheduleEmailWizardPanel.yes = mock( RadioButton.class );
    scheduleEmailWizardPanel.toAddressTextBox = mock( TextBox.class );
    scheduleEmailWizardPanel.subjectTextBox = mock( TextBox.class );
    scheduleEmailWizardPanel.messageTextArea = mock( TextArea.class );
    scheduleEmailWizardPanel.attachmentNameTextBox = mock( TextBox.class );

    when( scheduleEmailWizardPanel.yes.getValue() ).thenReturn( false );
    assertNull( scheduleEmailWizardPanel.getEmailParams() );

    when( scheduleEmailWizardPanel.yes.getValue() ).thenReturn( true );
    assertNotNull( scheduleEmailWizardPanel.getEmailParams() );
  }

  @Test
  public void testIsValidConfig() throws Exception {
    doCallRealMethod().when( scheduleEmailWizardPanel ).isValidConfig();

    scheduleEmailWizardPanel.no = mock( RadioButton.class );

    when( scheduleEmailWizardPanel.no.getValue() ).thenReturn( true );
    assertTrue( scheduleEmailWizardPanel.isValidConfig() );

    when( scheduleEmailWizardPanel.no.getValue() ).thenReturn( false );
    scheduleEmailWizardPanel.toAddressTextBox = mock( TextBox.class );
    when( scheduleEmailWizardPanel.toAddressTextBox.getText() ).thenReturn( "" );
    assertFalse( scheduleEmailWizardPanel.isValidConfig() );

    when( scheduleEmailWizardPanel.toAddressTextBox.getText() ).thenReturn( "wrong_email" );
    assertFalse( scheduleEmailWizardPanel.isValidConfig() );

    when( scheduleEmailWizardPanel.toAddressTextBox.getText() ).thenReturn( "correct@email" );
    assertTrue( scheduleEmailWizardPanel.isValidConfig() );
  }

  @Test
  public void testPanelWidgetChanged() throws Exception {
    doCallRealMethod().when( scheduleEmailWizardPanel ).panelWidgetChanged( any( Widget.class ) );

    checkValidConfig( true );
    checkValidConfig( false );
  }

  private void checkValidConfig( boolean value ) {
    when( scheduleEmailWizardPanel.isValidConfig() ).thenReturn( value );
    scheduleEmailWizardPanel.panelWidgetChanged( mock( Widget.class ) );
    verify( scheduleEmailWizardPanel ).setCanContinue( value );
    verify( scheduleEmailWizardPanel ).setCanFinish( value );
  }
}
