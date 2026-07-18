/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.gwt.widgets.client.wizards;

/**
 * @author wseyler
 * 
 *         Class that sources events for IWizardPanels
 */
public interface SourcesWizardEvents {
  /**
   * @param listener
   * 
   *          Adds listener to list of Listeners monitoring this class
   */
  public void addWizardPanelListener( IWizardPanelListener listener );

  /**
   * @param listener
   * 
   *          Removes listener from list of Listeners monitor this class
   */
  public void removeWizardPanelListener( IWizardPanelListener listener );
}
