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
