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

/**
 * @author wseyler
 * 
 *         Interface for wizard panels that are displayed by classes implenting the AbstractWizard Dialog. Most users
 *         should subclass the class AbstractWizardPanel and implement the abstract methods.
 */

public interface IWizardPanel extends SourcesWizardEvents {
  /**
   * @param userData
   * 
   *          Allows setting of arbitrary user data for this WizardPanel.
   */
  public void setUserData( Object userData );

  /**
   * @return object that contains userdate
   */
  public Object getUserData();

  /**
   * @return String that represents the name of this IWizardPanel
   * 
   *         This name will be used to populate the Steps List in the dialog so it should be human readable.
   */
  public String getName();

  /**
   * @return boolean Represents the state of the current panels ability to continue. When this becomes true the "Next"
   *         button on the AbstractWizardDialog will become enabled. Concrete classes should determined when the panel
   *         is completed enough for the user to "Continue".
   */
  public boolean canContinue();

  /**
   * @return boolean Represents the state of the current panels ability to finish the wizard operation. When this
   *         becomes true the "Finish" button on the AbstractWizardDialog will become enabled. Concrete classes should
   *         determined when the panel is completed enough for the user to "Finish" the operation.
   */
  public boolean canFinish();
}
