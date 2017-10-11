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

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class AbstractWizardDialogTest {
  private AbstractWizardDialog abstractWizardDialog;

  @Before
  public void setUp() throws Exception {
    abstractWizardDialog = mock( AbstractWizardDialog.class );
    abstractWizardDialog.wizardDeckPanel = mock( DeckPanel.class );
  }

  @Test
  @SuppressWarnings( "deprecation" )
  public void testInit() throws Exception {
    doCallRealMethod().when( abstractWizardDialog ).init();

    abstractWizardDialog.backButton = mock( Button.class );
    when( abstractWizardDialog.backButton.getElement() ).thenReturn( mock( Element.class ) );
    abstractWizardDialog.nextButton = mock( Button.class );
    when( abstractWizardDialog.nextButton.getElement() ).thenReturn( mock( Element.class ) );
    abstractWizardDialog.cancelButton = mock( Button.class );
    when( abstractWizardDialog.cancelButton.getElement() ).thenReturn( mock( Element.class ) );
    abstractWizardDialog.finishButton = mock( Button.class );
    when( abstractWizardDialog.finishButton.getElement() ).thenReturn( mock( Element.class ) );
    abstractWizardDialog.steps = mock( ListBox.class );

    abstractWizardDialog.init();
    verify( abstractWizardDialog.backButton ).setStyleName( AbstractWizardDialog.PENTAHO_BUTTON );
    verify( abstractWizardDialog.nextButton ).setStyleName( AbstractWizardDialog.PENTAHO_BUTTON );
    verify( abstractWizardDialog.cancelButton ).setStyleName( AbstractWizardDialog.PENTAHO_BUTTON );
    verify( abstractWizardDialog.finishButton ).setStyleName( AbstractWizardDialog.PENTAHO_BUTTON );
    verify( abstractWizardDialog.backButton ).addClickListener( any( ClickListener.class ) );
    verify( abstractWizardDialog.nextButton ).addClickListener( any( ClickListener.class ) );
    verify( abstractWizardDialog.cancelButton ).addClickListener( any( ClickListener.class ) );
    verify( abstractWizardDialog.finishButton ).addClickListener( any( ClickListener.class ) );
    verify( abstractWizardDialog.steps ).setEnabled( false );
  }

  @Test
  public void testEnableNext() throws Exception {
    doCallRealMethod().when( abstractWizardDialog ).enableNext( anyInt() );

    final AbstractWizardPanel panel = mock( AbstractWizardPanel.class );
    final int index = 5;
    when( abstractWizardDialog.wizardDeckPanel.getWidget( index ) ).thenReturn( panel );

    when( panel.canContinue() ).thenReturn( false );
    assertFalse( abstractWizardDialog.enableNext( index ) );

    when( panel.canContinue() ).thenReturn( true );
    when( abstractWizardDialog.wizardDeckPanel.getWidgetCount() ).thenReturn( 3 );
    assertFalse( abstractWizardDialog.enableNext( index ) );

    when( abstractWizardDialog.wizardDeckPanel.getWidgetCount() ).thenReturn( 7 );
    assertTrue( abstractWizardDialog.enableNext( index ) );
  }

  @Test
  public void testEnableFinish() throws Exception {
    doCallRealMethod().when( abstractWizardDialog ).enableFinish( anyInt() );

    final AbstractWizardPanel panel = mock( AbstractWizardPanel.class );
    final int index = 5;
    when( abstractWizardDialog.wizardDeckPanel.getWidget( index ) ).thenReturn( panel );

    when( panel.canFinish() ).thenReturn( false );
    assertFalse( abstractWizardDialog.enableFinish( index ) );

    when( panel.canFinish() ).thenReturn( true );
    assertTrue( abstractWizardDialog.enableFinish( index ) );
  }

  @Test
  public void testEnableBack() throws Exception {
    doCallRealMethod().when( abstractWizardDialog ).enableBack( anyInt() );

    assertFalse( abstractWizardDialog.enableBack( 0 ) );
    assertFalse( abstractWizardDialog.enableBack( -1 ) );
    assertTrue( abstractWizardDialog.enableBack( 1 ) );
    assertTrue( abstractWizardDialog.enableBack( 10 ) );
  }

  @Test
  public void testShowNext() throws Exception {
    doCallRealMethod().when( abstractWizardDialog ).showNext( anyInt() );

    final int index = 5;

    when( abstractWizardDialog.wizardDeckPanel.getWidgetCount() ).thenReturn( 0 );
    assertFalse( abstractWizardDialog.showNext( index ) );

    when( abstractWizardDialog.wizardDeckPanel.getWidgetCount() ).thenReturn( 1 );
    assertFalse( abstractWizardDialog.showNext( index ) );

    when( abstractWizardDialog.wizardDeckPanel.getWidgetCount() ).thenReturn( 2 );
    assertTrue( abstractWizardDialog.showNext( index ) );

    when( abstractWizardDialog.wizardDeckPanel.getWidgetCount() ).thenReturn( 20 );
    assertTrue( abstractWizardDialog.showNext( index ) );
  }

  @Test
  public void testShowBack() throws Exception {
    doCallRealMethod().when( abstractWizardDialog ).showBack( anyInt() );

    final int index = 5;

    when( abstractWizardDialog.wizardDeckPanel.getWidgetCount() ).thenReturn( 0 );
    assertFalse( abstractWizardDialog.showBack( index ) );

    when( abstractWizardDialog.wizardDeckPanel.getWidgetCount() ).thenReturn( 1 );
    assertFalse( abstractWizardDialog.showBack( index ) );

    when( abstractWizardDialog.wizardDeckPanel.getWidgetCount() ).thenReturn( 2 );
    assertTrue( abstractWizardDialog.showBack( index ) );

    when( abstractWizardDialog.wizardDeckPanel.getWidgetCount() ).thenReturn( 20 );
    assertTrue( abstractWizardDialog.showBack( index ) );
  }

  @Test
  public void testShowFinish() throws Exception {
    doCallRealMethod().when( abstractWizardDialog ).showFinish( anyInt() );

    assertTrue( abstractWizardDialog.showFinish( -1 ) );
    assertTrue( abstractWizardDialog.showFinish( 0 ) );
    assertTrue( abstractWizardDialog.showFinish( 1 ) );
    assertTrue( abstractWizardDialog.showFinish( 5 ) );
    assertTrue( abstractWizardDialog.showFinish( 100 ) );
  }

  @Test
  public void testGetIndex() throws Exception {
    doCallRealMethod().when( abstractWizardDialog ).getIndex();

    final int index = 5;
    when( abstractWizardDialog.wizardDeckPanel.getVisibleWidget() ).thenReturn( index );

    assertEquals( index, abstractWizardDialog.getIndex() );
  }

  @Test
  public void testNextClicked() throws Exception {
    doCallRealMethod().when( abstractWizardDialog ).nextClicked();

    abstractWizardDialog.steps = mock( ListBox.class );
    final int index = 5;
    when( abstractWizardDialog.steps.getSelectedIndex() ).thenReturn( index );

    final AbstractWizardPanel nextWidget = mock( AbstractWizardPanel.class );
    when( abstractWizardDialog.wizardDeckPanel.getWidget( index + 1 ) ).thenReturn( nextWidget );
    final AbstractWizardPanel prevWidget = mock( AbstractWizardPanel.class );
    when( abstractWizardDialog.wizardDeckPanel.getWidget( index ) ).thenReturn( prevWidget );

    when( abstractWizardDialog.onNext( nextWidget, prevWidget ) ).thenReturn( false );
    abstractWizardDialog.nextClicked();
    verify( prevWidget, never() ).removeWizardPanelListener( any( IWizardPanelListener.class ) );
    verify( nextWidget, never() ).addWizardPanelListener( any( IWizardPanelListener.class ) );
    verify( abstractWizardDialog, never() ).updateGUI( anyInt() );

    when( abstractWizardDialog.onNext( nextWidget, prevWidget ) ).thenReturn( true );
    abstractWizardDialog.nextClicked();
    verify( prevWidget ).removeWizardPanelListener( abstractWizardDialog );
    verify( nextWidget ).addWizardPanelListener( abstractWizardDialog );
    verify( abstractWizardDialog ).updateGUI( index + 1 );
  }

  @Test
  public void testBackClicked() throws Exception {
    doCallRealMethod().when( abstractWizardDialog ).backClicked();

    final int index = 5;
    when( abstractWizardDialog.getIndex() ).thenReturn( index );
    final AbstractWizardPanel currWidget = mock( AbstractWizardPanel.class );
    when( abstractWizardDialog.wizardDeckPanel.getWidget( index ) ).thenReturn( currWidget );
    final AbstractWizardPanel prevWidget = mock( AbstractWizardPanel.class );
    when( abstractWizardDialog.wizardDeckPanel.getWidget( index - 1 ) ).thenReturn( prevWidget );

    when( abstractWizardDialog.onPrevious( prevWidget, currWidget ) ).thenReturn( false );
    abstractWizardDialog.backClicked();
    verify( currWidget, never() ).removeWizardPanelListener( any( IWizardPanelListener.class ) );
    verify( prevWidget, never() ).addWizardPanelListener( any( IWizardPanelListener.class ) );
    verify( abstractWizardDialog, never() ).updateGUI( anyInt() );

    when( abstractWizardDialog.onPrevious( prevWidget, currWidget ) ).thenReturn( true );
    abstractWizardDialog.backClicked();
    verify( currWidget ).removeWizardPanelListener( abstractWizardDialog );
    verify( prevWidget ).addWizardPanelListener( abstractWizardDialog );
    verify( abstractWizardDialog ).updateGUI( index - 1 );
  }

  @Test
  public void testFinishClicked() throws Exception {
    doCallRealMethod().when( abstractWizardDialog ).finishClicked();

    when( abstractWizardDialog.onFinish() ).thenReturn( false );
    abstractWizardDialog.finishClicked();
    verify( abstractWizardDialog, never() ).hide();

    when( abstractWizardDialog.onFinish() ).thenReturn( true );
    abstractWizardDialog.finishClicked();
    verify( abstractWizardDialog ).hide();
  }

  @Test
  public void testCancelClicked() throws Exception {
    doCallRealMethod().when( abstractWizardDialog ).cancelClicked();

    abstractWizardDialog.cancelClicked();
    verify( abstractWizardDialog ).hide();
  }

  @Test
  public void testUpdateGUI() throws Exception {
    doCallRealMethod().when( abstractWizardDialog ).updateGUI( anyInt() );

    abstractWizardDialog.stepsList = mock( VerticalPanel.class );
    abstractWizardDialog.finishButton = mock( Button.class );
    abstractWizardDialog.backButton = mock( Button.class );
    abstractWizardDialog.nextButton = mock( Button.class );
    abstractWizardDialog.steps = mock( ListBox.class );

    final int index = 5;
    abstractWizardDialog.updateGUI( index );
    verify( abstractWizardDialog.stepsList ).setVisible( anyBoolean() );
    verify( abstractWizardDialog ).showFinish( index );
    verify( abstractWizardDialog.finishButton ).setVisible( anyBoolean() );
    verify( abstractWizardDialog ).showBack( index );
    verify( abstractWizardDialog.backButton ).setVisible( anyBoolean() );
    verify( abstractWizardDialog ).showNext( index );
    verify( abstractWizardDialog.nextButton ).setVisible( anyBoolean() );
    verify( abstractWizardDialog.steps ).setSelectedIndex( index );
    verify( abstractWizardDialog.wizardDeckPanel ).showWidget( index );
    verify( abstractWizardDialog ).enableFinish( index );
    verify( abstractWizardDialog.finishButton ).setEnabled( anyBoolean() );
    verify( abstractWizardDialog ).enableBack( index );
    verify( abstractWizardDialog.backButton ).setEnabled( anyBoolean() );
    verify( abstractWizardDialog ).enableNext( index );
    verify( abstractWizardDialog.nextButton ).setEnabled( anyBoolean() );
  }

  @Test
  public void testSetWizardPanels() throws Exception {
    doCallRealMethod().when( abstractWizardDialog ).setWizardPanels( any( IWizardPanel[].class ) );

    abstractWizardDialog.steps = mock( ListBox.class );
    abstractWizardDialog.finishButton = mock( Button.class );

    final AbstractWizardPanel wizardPanel = mock( AbstractWizardPanel.class );
    final String wpName = "wpName";
    when( wizardPanel.getName() ).thenReturn( wpName );
    when( abstractWizardDialog.wizardDeckPanel.getWidget( 0 ) ).thenReturn( wizardPanel );

    abstractWizardDialog.setWizardPanels( new IWizardPanel[0] );
    verify( abstractWizardDialog.steps ).clear();
    verify( abstractWizardDialog.wizardDeckPanel ).clear();
    verify( abstractWizardDialog, never() ).updateGUI( anyInt() );

    abstractWizardDialog.setWizardPanels( new AbstractWizardPanel[] { wizardPanel } );
    verify( abstractWizardDialog.steps, times( 2 ) ).clear();
    verify( abstractWizardDialog.wizardDeckPanel, times( 2 ) ).clear();
    verify( abstractWizardDialog.steps ).addItem( wpName );
    verify( abstractWizardDialog.wizardDeckPanel ).add( wizardPanel );
    verify( wizardPanel ).addWizardPanelListener( abstractWizardDialog );
    verify( abstractWizardDialog.finishButton ).setText( anyString() );
    verify( abstractWizardDialog ).updateGUI( 0 );
  }

  @Test
  public void testPanelUpdated() throws Exception {
    doCallRealMethod().when( abstractWizardDialog ).panelUpdated( any( IWizardPanel.class ) );

    final int index = 5;
    when( abstractWizardDialog.getIndex() ).thenReturn( index );
    abstractWizardDialog.nextButton = mock( Button.class );
    abstractWizardDialog.finishButton = mock( Button.class );

    abstractWizardDialog.panelUpdated( mock( IWizardPanel.class ) );
    verify( abstractWizardDialog ).enableNext( index );
    verify( abstractWizardDialog.nextButton ).setEnabled( anyBoolean() );
    verify( abstractWizardDialog ).enableFinish( index );
    verify( abstractWizardDialog.finishButton ).setEnabled( anyBoolean() );
  }
}
