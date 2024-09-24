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

package org.pentaho.gwt.widgets.client.utils.module;

/**
 * Collection of utilities for general use throughout GWT Widgets
 * 
 * @author Jordan Ganoff <jganoff@pentaho.com>
 */
public class ModuleUtil {
  /**
   * Alerts a global {@code gwtModuleLoaded()} function that the module name provided was loaded.
   * 
   * @param module
   *          Name of module that has been loaded
   */
  public static native void fireModuleLoaded( String module )/*-{
                                                             if (typeof $wnd.gwtModuleLoaded == 'function') {
                                                             $wnd.gwtModuleLoaded(module);
                                                             }
                                                             }-*/;
}
