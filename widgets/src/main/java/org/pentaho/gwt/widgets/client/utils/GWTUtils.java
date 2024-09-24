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

import com.google.gwt.core.client.GWT;

public class GWTUtils {
  /**
   * Gets a value that indicates whether the code is running in
   * super dev mode.
   * <p>
   *   Adapted from https://github.com/gwtproject/gwt/issues/7631#issuecomment-110876132.
   * </p>
   */
  public static boolean isSuperDevMode() {
    return ((SuperDevModeIndicator) GWT.create( SuperDevModeIndicator.class ) )
      .isSuperDevMode();
  }

  public static class SuperDevModeIndicator {
    public boolean isSuperDevMode() {
      return false;
    }
  }

  public static class SuperDevModeIndicatorInSuperDevMode extends SuperDevModeIndicator {
    @Override
    public boolean isSuperDevMode() {
      return true;
    }
  }
}
