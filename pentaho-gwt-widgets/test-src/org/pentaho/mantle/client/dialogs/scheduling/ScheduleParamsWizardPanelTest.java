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

import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
@WithClassesToStub( Frame.class )
public class ScheduleParamsWizardPanelTest {
  private ScheduleParamsWizardPanel scheduleParamsWizardPanel;

  @Before
  public void setUp() throws Exception {
    scheduleParamsWizardPanel = mock( ScheduleParamsWizardPanel.class );
  }

  @Test
  public void testSetRadioParameterValue() throws Exception {
    doCallRealMethod().when( scheduleParamsWizardPanel ).setRadioParameterValue( anyString() );

    scheduleParamsWizardPanel.setRadioParameterValue( "?no_radio_param_test=true" );
    verify( scheduleParamsWizardPanel, never() ).setRadioButton( anyString() );

    final String value = "value";
    scheduleParamsWizardPanel.setRadioParameterValue( "?REPORT_FORMAT_TYPE=" + value );
    verify( scheduleParamsWizardPanel ).setRadioButton( value );
  }

  @Test
  public void testSchedulerParamsCompleteCallback() throws Exception {
    doCallRealMethod().when( scheduleParamsWizardPanel ).schedulerParamsCompleteCallback( anyBoolean() );

    scheduleParamsWizardPanel.parametersFrame = mock( Frame.class );

    final String url = "url";
    when( scheduleParamsWizardPanel.parametersFrame.getUrl() ).thenReturn( url );
    verifyComplete( url, true );
    final String url1 = "url1";
    when( scheduleParamsWizardPanel.parametersFrame.getUrl() ).thenReturn( url1 );
    verifyComplete( url1, false );
  }

  @Test
  public void testSetParametersUrl() throws Exception {
    doCallRealMethod().when( scheduleParamsWizardPanel ).setParametersUrl( anyString() );

    final Frame frame = mock( Frame.class );
    scheduleParamsWizardPanel.parametersFrame = frame;
    scheduleParamsWizardPanel.scheduleParameterPanel = mock( SimplePanel.class );
    scheduleParamsWizardPanel.setParametersUrl( null );
    verify( scheduleParamsWizardPanel.scheduleParameterPanel ).remove( frame );
    assertNull( scheduleParamsWizardPanel.parametersFrame );

    reset( scheduleParamsWizardPanel.scheduleParameterPanel );
    scheduleParamsWizardPanel.parametersFrame = null;
    final String url = "url";
    scheduleParamsWizardPanel.setParametersUrl( url );
    verify( scheduleParamsWizardPanel.scheduleParameterPanel ).add( notNull( Frame.class ) );
    verify( scheduleParamsWizardPanel ).setRadioParameterValue( anyString() );

    reset( scheduleParamsWizardPanel.scheduleParameterPanel, frame );
    scheduleParamsWizardPanel.parametersFrame = frame;
    when( frame.getUrl() ).thenReturn( url );
    scheduleParamsWizardPanel.setParametersUrl( url );
    verify( scheduleParamsWizardPanel.scheduleParameterPanel, never() ).add( any( Frame.class ) );
    verify( frame, never() ).setUrl( anyString() );

    final String newUrl = "new_url";
    scheduleParamsWizardPanel.setParametersUrl( newUrl );
    verify( scheduleParamsWizardPanel.scheduleParameterPanel, never() ).add( any( Frame.class ) );
    verify( frame ).setUrl( newUrl );
  }

  private void verifyComplete( String url, boolean complete ) {
    scheduleParamsWizardPanel.schedulerParamsCompleteCallback( complete );
    assertEquals( complete, scheduleParamsWizardPanel.parametersComplete );
    verify( scheduleParamsWizardPanel ).setCanContinue( complete );
    verify( scheduleParamsWizardPanel ).setCanFinish( complete );
    verify( scheduleParamsWizardPanel ).setRadioParameterValue( url );
  }
}
