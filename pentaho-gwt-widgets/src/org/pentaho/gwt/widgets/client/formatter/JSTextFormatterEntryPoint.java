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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.gwt.widgets.client.formatter;

import com.google.gwt.core.client.EntryPoint;
import org.pentaho.gwt.widgets.client.utils.module.ModuleUtil;

public class JSTextFormatterEntryPoint implements EntryPoint {

  @Override
  public void onModuleLoad() {
    addNativeMethods();
    ModuleUtil.fireModuleLoaded( "formatter" ); //$NON-NLS-1$
  }

  private native void addNativeMethods()/*-{
    $wnd.jsTextFormatter = {
      createFormatter: function(type, pattern) {
        return @org.pentaho.gwt.widgets.client.formatter.JSTextFormatterFactory::createFormatter(Ljava/lang/String;Ljava/lang/String;)(type, pattern);
      },
      createDefaultDateFormatter: function() {
        return @org.pentaho.gwt.widgets.client.formatter.JSDateTextFormatter::createDefaultDateFormatter()();
      }
    }
  }-*/;
}
