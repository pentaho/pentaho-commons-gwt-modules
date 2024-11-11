/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.gwt.widgets.client.wizards;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * @author wseyler
 * 
 *         Abstract class for implenenting a wizard panel (step). Each step has it's own instance of this class. So a
 *         wizard with 5 steps will have 5 distince implementations of this class to display for each step.
 */
public abstract class AbstractWizardPanel extends DockPanel implements IWizardPanel, RequiresResize {

  private static final String WIZARD_PANEL = "pentaho-wizard-panel"; //$NON-NLS-1$
  protected boolean canContinue = false;
  protected boolean canFinish = false;
  private Object userData;

  protected WizardPanelListenerCollection wizardPanelListenerCollection = new WizardPanelListenerCollection();

  public AbstractWizardPanel() {
    super();
    addStyleName( WIZARD_PANEL );
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.pentaho.gwt.widgets.client.wizards.SourcesWizardEvents#addWizardPanelListener(org.pentaho.gwt.widgets.client
   * .wizards.IWizardPanelListener)
   */
  @Override
  public void addWizardPanelListener( IWizardPanelListener listener ) {
    if ( wizardPanelListenerCollection == null ) {
      wizardPanelListenerCollection = new WizardPanelListenerCollection();
    }
    wizardPanelListenerCollection.add( listener );
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.pentaho.gwt.widgets.client.wizards.SourcesWizardEvents#removeWizardPanelListener(org.pentaho.gwt.widgets.client
   * .wizards.IWizardPanelListener)
   */
  @Override
  public void removeWizardPanelListener( IWizardPanelListener listener ) {
    if ( wizardPanelListenerCollection != null ) {
      wizardPanelListenerCollection.remove( listener );
    }
  }

  /**
   * @param canContinue
   */
  public void setCanContinue( boolean canContinue ) {
    if ( this.canContinue != canContinue ) {
      this.canContinue = canContinue;
      wizardPanelListenerCollection.fireWizardPanelChanged( this );
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.pentaho.gwt.widgets.client.wizards.IWizardPanel#canContinue()
   */
  @Override
  public boolean canContinue() {
    return canContinue;
  }

  /**
   * @param canFinish
   */
  public void setCanFinish( boolean canFinish ) {
    if ( this.canFinish != canFinish ) {
      this.canFinish = canFinish;
      wizardPanelListenerCollection.fireWizardPanelChanged( this );
    }
  }

  public void panelChanged() {
    wizardPanelListenerCollection.fireWizardPanelChanged( this );
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.pentaho.gwt.widgets.client.wizards.IWizardPanel#canFinish()
   */
  @Override
  public boolean canFinish() {
    return canFinish;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.pentaho.gwt.widgets.client.wizards.IWizardPanel#setUserData(java.lang.Object)
   */
  @Override
  public void setUserData( Object userData ) {
    this.userData = userData;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.pentaho.gwt.widgets.client.wizards.IWizardPanel#getUserData()
   */
  @Override
  public Object getUserData() {
    return userData;
  }

  @Override
  public void onResize() {
    // NOP
  }
}
