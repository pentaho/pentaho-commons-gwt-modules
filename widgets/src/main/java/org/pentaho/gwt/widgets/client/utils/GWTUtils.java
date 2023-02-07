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
 * Copyright (c) 2023 Hitachi Vantara. All rights reserved.
 */

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
