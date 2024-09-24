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

package org.pentaho.mantle.client.events;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.event.shared.HandlerRegistration;

public interface EventBusUtil {
  public static final EventBus EVENT_BUS = GWT.create( SimpleEventBus.class );

  HandlerRegistration addHandler( String eventType, JavaScriptObject handler );

  public void invokeEventBusJSO( JavaScriptObject handler, String parameterJSON );

  public void fireEvent( String eventType, JavaScriptObject parameterMap );
}
