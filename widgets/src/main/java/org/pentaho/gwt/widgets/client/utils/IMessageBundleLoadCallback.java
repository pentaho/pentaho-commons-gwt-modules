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


package org.pentaho.gwt.widgets.client.utils;

/**
 * This class is a simple callback for ResourceBundle
 * 
 * @author Michael D'Amour
 * @deprecated use org.pentaho.gwt.widgets.client.utils.i18n.IMessageBundleLoadCallback instead
 */
@Deprecated
public interface IMessageBundleLoadCallback {

  /**
   * This method is called by ResourceBundle when the ResourceBundle has been loaded. Once loaded, a resource is safe to
   * use.
   * 
   * @param bundleName
   *          The name of the bundle which has loaded. This is provided in case the caller is loading multiple bundles
   *          and wants to be notified specifically for each loaded bundle.
   */
  @Deprecated
  public void bundleLoaded( String bundleName );
}
