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

package org.pentaho.gwt.widgets.client.wizards;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class AbstractWizardPanelTest {
  private AbstractWizardPanel abstractWizardPanel;

  @Before
  public void setUp() throws Exception {
    abstractWizardPanel = mock( AbstractWizardPanel.class );

  }

  @Test
  public void testAddWizardPanelListener() throws Exception {
    doCallRealMethod().when( abstractWizardPanel ).addWizardPanelListener( any( IWizardPanelListener.class ) );

    abstractWizardPanel.wizardPanelListenerCollection = null;
    final IWizardPanelListener listener = mock( IWizardPanelListener.class );
    abstractWizardPanel.addWizardPanelListener( listener );
    assertNotNull( abstractWizardPanel.wizardPanelListenerCollection );
    assertEquals( listener, abstractWizardPanel.wizardPanelListenerCollection.get( 0 ) );
  }

  @Test
  public void testRemoveWizardPanelListener() throws Exception {
    doCallRealMethod().when( abstractWizardPanel ).removeWizardPanelListener( any( IWizardPanelListener.class ) );

    abstractWizardPanel.wizardPanelListenerCollection = null;
    final IWizardPanelListener listener = mock( IWizardPanelListener.class );
    abstractWizardPanel.removeWizardPanelListener( listener ); // no exception thrown

    abstractWizardPanel.wizardPanelListenerCollection = mock( WizardPanelListenerCollection.class );
    abstractWizardPanel.removeWizardPanelListener( listener );
    verify( abstractWizardPanel.wizardPanelListenerCollection ).remove( listener );
  }

  @Test
  public void testSetCanContinue() throws Exception {
    doCallRealMethod().when( abstractWizardPanel ).setCanContinue( anyBoolean() );

    abstractWizardPanel.wizardPanelListenerCollection = mock( WizardPanelListenerCollection.class );
    abstractWizardPanel.canContinue = false;
    abstractWizardPanel.setCanContinue( false );
    verify( abstractWizardPanel.wizardPanelListenerCollection, never() ).fireWizardPanelChanged( abstractWizardPanel );

    abstractWizardPanel.canContinue = true;
    abstractWizardPanel.setCanContinue( true );
    verify( abstractWizardPanel.wizardPanelListenerCollection, never() ).fireWizardPanelChanged( abstractWizardPanel );

    abstractWizardPanel.canContinue = false;
    abstractWizardPanel.setCanContinue( true );
    verify( abstractWizardPanel.wizardPanelListenerCollection ).fireWizardPanelChanged( abstractWizardPanel );

    abstractWizardPanel.canContinue = true;
    abstractWizardPanel.setCanContinue( false );
    verify( abstractWizardPanel.wizardPanelListenerCollection, times( 2 ) ).fireWizardPanelChanged( abstractWizardPanel );
  }

  @Test
  public void testSetCanFinish() throws Exception {
    doCallRealMethod().when( abstractWizardPanel ).setCanFinish( anyBoolean() );

    abstractWizardPanel.wizardPanelListenerCollection = mock( WizardPanelListenerCollection.class );
    abstractWizardPanel.canFinish = false;
    abstractWizardPanel.setCanFinish( false );
    verify( abstractWizardPanel.wizardPanelListenerCollection, never() ).fireWizardPanelChanged( abstractWizardPanel );

    abstractWizardPanel.canFinish = true;
    abstractWizardPanel.setCanFinish( true );
    verify( abstractWizardPanel.wizardPanelListenerCollection, never() ).fireWizardPanelChanged( abstractWizardPanel );

    abstractWizardPanel.canFinish = false;
    abstractWizardPanel.setCanFinish( true );
    verify( abstractWizardPanel.wizardPanelListenerCollection ).fireWizardPanelChanged( abstractWizardPanel );

    abstractWizardPanel.canFinish = true;
    abstractWizardPanel.setCanFinish( false );
    verify( abstractWizardPanel.wizardPanelListenerCollection, times( 2 ) ).fireWizardPanelChanged( abstractWizardPanel );
  }

  @Test
  public void testPanelChanged() throws Exception {
    doCallRealMethod().when( abstractWizardPanel ).panelChanged();

    abstractWizardPanel.wizardPanelListenerCollection = mock( WizardPanelListenerCollection.class );
    abstractWizardPanel.panelChanged();
    verify( abstractWizardPanel.wizardPanelListenerCollection ).fireWizardPanelChanged( abstractWizardPanel );
  }
}
