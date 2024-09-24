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

import java.util.ArrayList;

/**
 * @author wseyler
 * 
 *         Holds a list of IWizardPanelListeners.
 */
@SuppressWarnings( "serial" )
public class WizardPanelListenerCollection extends ArrayList<IWizardPanelListener> {
  /**
   * Fires a wizard changed event to all listeners.
   * 
   * @param sender
   *          - the IWizardPanel sending the event.
   */
  public void fireWizardPanelChanged( IWizardPanel sender ) {
    for ( IWizardPanelListener listener : this ) {
      listener.panelUpdated( sender );
    }
  }
}
