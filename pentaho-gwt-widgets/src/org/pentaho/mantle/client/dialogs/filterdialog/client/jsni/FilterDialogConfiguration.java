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

package org.pentaho.mantle.client.dialogs.filterdialog.client.jsni;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import org.pentaho.mantle.client.dialogs.filterdialog.client.EditFilterModel;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Configuration options for a {@link org.pentaho.dashboards.filterdialog.client.FilterDialog}.
 *
 * @author Jordan Ganoff (jganoff@pentaho.com)
 */
public class FilterDialogConfiguration extends JavaScriptObject implements Serializable {
  public static final native FilterDialogConfiguration create()/*-{
    return {
      title: null,
      displayNameSupported: true,
      parametersSupported: true,
      calendarRestrictionsSupported: true,
      textFieldOptionsSupported: true,
      orientationOptionsSupported: true,
      isDisplayNameSupported: true,
      defaultDateFormat: 'yyyy-MM-dd',
      supportedComponentTypes: []
    };
  }-*/;

  // Overlay types always have protected, zero-arg ctors
  protected FilterDialogConfiguration() {
  }

  public final native String getTitle()/*-{
    return this.title;
  }-*/;

  public final native boolean isParametersSupported()/*-{
    return this.parametersSupported;
  }-*/;

  public final native boolean isCalendarRestrictionsSupported()/*-{
    return this.calendarRestrictionsSupported;
  }-*/;

  public final native boolean isTextFieldOptionsSupported()/*-{
    return this.textFieldOptionsSupported;
  }-*/;

  public final Set<EditFilterModel.PovComponentType> getSupportedComponentTypes() {
    Set<EditFilterModel.PovComponentType> supportedComponentTypes = new HashSet<EditFilterModel.PovComponentType>();
    JsArrayString components = getSupportedTypeStrings();
    for ( int i = 0; i < components.length(); i++ ) {
      EditFilterModel.PovComponentType component = EditFilterModel.PovComponentType.valueOf( components.get( i ) );
      if ( component == null ) {
        throw new IllegalArgumentException( "unknown component type '" + components.get( i ) + "'" );
      }
      supportedComponentTypes.add( component );
    }
    return supportedComponentTypes;
  }

  private final native JsArrayString getSupportedTypeStrings()/*-{
    return this.supportedComponentTypes;
  }-*/;

  public final native boolean isOrientationOptionsSupported()/*-{
    return this.orientationOptionsSupported;
  }-*/;

  public final native boolean isDisplayNameSupported()/*-{
    return this.isDisplayNameSupported;
  }-*/;

  public final native String getDefaultDateFormat()/*-{
    return this.defaultDateFormat;
  }-*/;
}
