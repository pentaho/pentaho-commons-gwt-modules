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

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import org.pentaho.mantle.client.dialogs.filterdialog.client.EditFilterModel.PovComponentType;

/**
 * A filter that can be edited in the Filter Dialog.
 *
 * @author Jordan Ganoff (jganoff@pentaho.com)
 */
public interface IPentahoFilter {

  String getId();

  void setId( String name );

  String getGUID();

  void setGUID( String guid );

  String getType();

  void setType( String type );

  String getHtmlObject();

  void setHtmlObject( String htmlObject );

  String getName();

  /**
   * Returns the original name of the filter
   *
   * @return String The original name of the filter
   */
  String getOriginalName();

  /**
   * Assigns the original name of the filter
   *
   * @param name The original name of the filter
   */
  void setOriginalName( String name );

  boolean getShowTitle();

  void setShowTitle( boolean showTitle );

  String getTitle();

  void setTitle( String title );

  boolean isStaticList();

  boolean isSqlList();

  boolean isMqlList();

  int getValuesArrayLength();

  String getValuesArrayLabelAt( int id );

  String getValuesArrayValueAt( int id );

  void switchToQuerylist( String filterType, String url, String command );

  /**
   * Redefines widget as static list
   */
  void switchToStaticList();

  String getInputParameterValue( String inputName );

  void setInputParameterValue( String inputName, String inputValue );

  void clearQueryParameters();

  void setQueryParameter( String queryParamName, String value, boolean isStatic );

  JsArray<JsArrayString> getQueryParameters();

  void addToValuesArray( String name, String value );

  /**
   * @return the internal parameter name that this filter represents
   */
  String getParameter();

  void setParameter( String param );

  /**
   * Sets this filter's parameter name and default value.
   *
   * @param param        Internal parameter name
   * @param defaultValue Default value for this filter
   */
  void setParameterAndDefaultValue( String param, JsArrayString defaultValue );

  /**
   * @return this filter's default value
   */
  JsArrayString getParameterDefault();

  /*
   * TEXT-specific
   */
  void setMaxChars( Integer maxChars );

  void setCharWidth( Integer width );

  Integer getCharWidth();

  Integer getMaxChars();

  /*
   * SELECTMULTIPLE-specific
   */
  void setMultiple( boolean isMultiple );

  boolean isMultiple();

  Integer getSize();

  void setSize( Integer size );

  void setDateFormat( String dateFormat );

  String getDateFormat();

  void setStartDate( String startDate );

  void setEndDate( String endDate );

  String getStartDate();

  String getEndDate();

  boolean isVertical();

  void setVertical( boolean vert );

  /**
   * Sets type and removes fields of old one if necessary.
   */
  void switchType( PovComponentType newType );

  boolean getUseFirstValue();

  void setUseFirstValue( boolean useFirst );

}
