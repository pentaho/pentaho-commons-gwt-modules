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

package org.pentaho.mantle.client.dialogs.filterdialog.client.jsni.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import org.pentaho.mantle.client.dialogs.filterdialog.client.EditFilterModel;
import org.pentaho.mantle.client.dialogs.filterdialog.client.EditFilterModel.PovComponentType;
import org.pentaho.mantle.client.dialogs.filterdialog.client.jsni.IPentahoFilter;

/**
 * A filter that can be edited in the Filter Dialog.
 *
 * @author Jordan Ganoff (jganoff@pentaho.com)
 */
public class PentahoFilter extends JavaScriptObject implements IPentahoFilter, java.io.Serializable {

  private static final String DATE_FORMAT = "dateFormat"; //$NON-NLS-1$
  private static final String END_DATE = "endDate"; //$NON-NLS-1$
  private static final String START_DATE = "startDate"; //$NON-NLS-1$
  private static final String SIZE = "size"; //$NON-NLS-1$
  private static final String CHAR_WIDTH = "charWidth"; //$NON-NLS-1$
  private static final String MAX_CHARS = "maxChars"; //$NON-NLS-1$

  // Overlay types always have protected, zero-arg ctors
  protected PentahoFilter() {
  }

  public static final native PentahoFilter create( String type )/*-{
    return {
      type: type,
      name: '',
      parameter: '',
      showTitle: true,
      title: '',
      filterType: 'static',
      useFirstValue: false,
      // Default value(s) for this filter: array of values
      defaultValue: [],

      // The values for a static filter type
      valuesArray: [],

      // Properties used by get/setInputParameterValue().
      // Generally used by QueryFilterDataModel to store properties about the data source.
      dataSourceProperties: {
        // For possible property names see SqlFilterDataModel$Param or MqlFilterDataModel$WidgetParam
      },
      orientation: 'vertical', // vertical or horizontal
      originalName: ''
    };
  }-*/;

  @Override
  public final String getId() {
    return getName();
  }

  @Override
  public final native void setId( String name )/*-{
    this.name = name;
  }-*/;

  @Override
  public final native String getGUID()/*-{
  }-*/;

  @Override
  public final native void setGUID( String guid )/*-{
  }-*/;

  @Override
  public final native String getType()/*-{
    return this.type;
  }-*/;

  @Override
  public final native void setType( String type )/*-{
    this.type = type;
  }-*/;

  @Override
  public final native String getHtmlObject()/*-{
  }-*/;

  @Override
  public final native void setHtmlObject( String htmlObject )/*-{
  }-*/;

  @Override
  public final native String getName()/*-{
    return this.name;
  }-*/;

  @Override
  public final native boolean getShowTitle()/*-{
    return this.showTitle;
  }-*/;

  @Override
  public final native void setShowTitle( boolean showTitle )/*-{
    this.showTitle = showTitle;
  }-*/;

  @Override
  public final native String getTitle()/*-{
    return this.title;
  }-*/;

  @Override
  public final native void setTitle( String title )/*-{
    this.title = title;
  }-*/;

  @Override
  public final native String getOriginalName()/*-{
    return this.originalName;
  }-*/;

  @Override
  public final native void setOriginalName( String name )/*-{
    this.originalName = name;
  }-*/;

  @Override
  public final native boolean isStaticList()/*-{
    return this.filterType == 'static';
  }-*/;

  @Override
  public final native boolean isSqlList()/*-{
    return this.filterType == 'sql';
  }-*/;

  @Override
  public final native boolean isMqlList()/*-{
    return this.filterType == 'mql';
  }-*/;

  @Override
  public final native int getValuesArrayLength()/*-{
    return this.valuesArray.length;
  }-*/;

  @Override
  public final native String getValuesArrayLabelAt( int id )/*-{
    return this.valuesArray[id][1];
  }-*/;

  @Override
  public final native String getValuesArrayValueAt( int id )/*-{
    return this.valuesArray[id][0];
  }-*/;

  @Override
  public final native void switchToQuerylist( String filterType, String url, String command )/*-{
    this.filterType = filterType;
  }-*/;

  @Override
  public final native void switchToStaticList()/*-{
    this.filterType = 'static';
    this.valuesArray = [];
  }-*/;

  @Override
  public final native String getInputParameterValue( String inputName )/*-{
    return this.dataSourceProperties[inputName];
  }-*/;

  @Override
  public final native void setInputParameterValue( String inputName, String inputValue )/*-{
    this.dataSourceProperties[inputName] = inputValue;
  }-*/;

  @Override
  public final native void clearQueryParameters()/*-{
    // TODO Implement when parameters are supported
  }-*/;

  @Override
  public final native void setQueryParameter( String queryParamName, String value, boolean isStatic )/*-{
    // TODO Implement when parameters are supported
  }-*/;

  @Override
  public final native JsArray<JsArrayString> getQueryParameters()/*-{
    return [];
  }-*/;

  @Override
  public final native void addToValuesArray( String name, String value )/*-{
    this.valuesArray.push([ value, name ]);
  }-*/;

  @Override
  public final String getParameter() {
    return getName();
  }

  @Override
  public final native void setParameter( String param ) /*-{
    this.name = param;
  }-*/;

  @Override
  public final native void setParameterAndDefaultValue( String param, JsArrayString defaultValue )/*-{
    this.name = param;
    if (typeof (defaultValue) == "object") {
      if (defaultValue.length == 0) {
        this.defaultValue = [];
      } else {
        this.defaultValue = defaultValue;
      }
    } else {
      this.defaultValue = [ defaultValue ];
    }
  }-*/;

  @Override
  public final native JsArrayString getParameterDefault()/*-{
    return this.defaultValue;
  }-*/;

  @Override
  public final Integer getCharWidth() {
    return getInteger( CHAR_WIDTH );
  }

  @Override
  public final void setCharWidth( Integer width ) {
    setInteger( CHAR_WIDTH, width );
  }

  @Override
  public final Integer getMaxChars() {
    return getInteger( MAX_CHARS );
  }

  @Override
  public final void setMaxChars( Integer maxChars ) {
    setInteger( MAX_CHARS, maxChars );
  }

  @Override
  public final native boolean isMultiple()/*-{
    return this.isMultiple;
  }-*/;

  @Override
  public final native void setMultiple( boolean isMultiple )/*-{
    this.isMultiple = isMultiple;
  }-*/;

  @Override
  public final Integer getSize() {
    return getInteger( SIZE );
  }

  @Override
  public final void setSize( Integer size ) {
    setInteger( SIZE, size );
  }

  @Override
  public final String getDateFormat() {
    return getStringOrNull( DATE_FORMAT );
  }

  @Override
  public final native void setDateFormat( String dateFormat )/*-{
    this.dateFormat = dateFormat;
  }-*/;

  @Override
  public final String getStartDate() {
    return getStringOrNull( START_DATE );
  }

  @Override
  public final void setStartDate( String startDate ) {
    setOrRemove( START_DATE, startDate );
  }

  @Override
  public final String getEndDate() {
    return getStringOrNull( END_DATE );
  }

  @Override
  public final void setEndDate( String endDate ) {
    setOrRemove( END_DATE, endDate );
  }

  @Override
  public final native boolean isVertical()/*-{
    return this.orientation == 'vertical';
  }-*/;

  @Override
  public final native void setVertical( boolean vert )/*-{
    this.orientation = (vert ? 'vertical' : 'horizontal');
  }-*/;

  @Override
  public final void switchType( PovComponentType newType ) {
    PovComponentType oldType = PovComponentType.fromCdfTypeString( this.getType() );
    if ( oldType != newType ) {
      for ( String fieldName : EditFilterModel.getComponentFieldNames( oldType ) ) {
        removeField( fieldName );
      }
    }
    this.setType( newType.getCdfType() );
  }

  @Override
  public final native boolean getUseFirstValue()/*-{
    return this.useFirstValue;
  }-*/;

  @Override
  public final native void setUseFirstValue( boolean useFirst )/*-{
    this.useFirstValue = useFirst;
  }-*/;

  // ////////////////////////////////////////////////////////////////////////////
  // Utility methods
  // ////////////////////////////////////////////////////////////////////////////

  private final Integer getInteger( String name ) {
    if ( this.hasIntValue( name ) ) {
      return new Integer( this.getIntValue( name ) );
    } else {
      return null;
    }
  }

  private final native boolean hasIntValue( String name )/*-{
    return typeof (this[name]) != 'undefined';
  }-*/;

  private final native int getIntValue( String name )/*-{
    return this[name];
  }-*/;

  private final void setInteger( String name, Integer value ) {
    if ( value != null ) {
      setIntValue( name, value.intValue() );
    } else {
      removeField( name );
    }
  }

  private final native void setIntValue( String name, int value )/*-{
    this[name] = value;
  }-*/;

  /**
   * Sets a property's value or removes the property from this filter if the value is null
   *
   * @param name  Property to set
   * @param value Value to set property to. If null, property will be removed from this filter.
   */
  private final native void setOrRemove( String name, String value )/*-{
    if (value == null) {
      if (this[name] != undefined) {
        delete this[name];
      }
    } else {
      this[name] = value;
    }
  }-*/;

  private final native String getStringOrNull( String name )/*-{
    if (this[name] != undefined) {
      return this[name];
    } else {
      return null;
    }
  }-*/;

  /**
   * Deletes a field from this filter
   *
   * @param field Field name to remove
   */
  private final native void removeField( String field )/*-{
    if (this[field] != undefined) {
      delete this[field];
    }
  }-*/;
}
