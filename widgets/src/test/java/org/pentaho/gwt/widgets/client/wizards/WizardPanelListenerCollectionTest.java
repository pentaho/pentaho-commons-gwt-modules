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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith( GwtMockitoTestRunner.class )
public class WizardPanelListenerCollectionTest {

  @Test
  public void testFireWizardPanelChanged() throws Exception {
    final WizardPanelListenerCollection collection = new WizardPanelListenerCollection();

    final IWizardPanelListener listener = mock( IWizardPanelListener.class );
    collection.add( listener );

    final IWizardPanel panel = mock( IWizardPanel.class );
    collection.fireWizardPanelChanged( panel );

    verify( listener ).panelUpdated( panel );
  }
}
