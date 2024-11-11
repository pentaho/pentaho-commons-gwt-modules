/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.gwt.widgets.client.wizards;

/**
 * @author wseyler
 * 
 *         Listener interface for classes that monitor IWizardPanel implemtations.
 */
public interface IWizardPanelListener {

  /**
   * @param wizardPanel
   *          This method is fired by the AbstractWizardPanel when an interesting (canContinue or canFinish) event
   *          occurs. This allows communication between the AbstractWizardDialog and it's Panels so that when a panel is
   *          manipulated and its state becomes ready to continue or finish the AbstractWizardPanel can update it's GUI
   *          accordingly (ie enable/disable buttons).
   */
  public void panelUpdated( IWizardPanel wizardPanel );
}
