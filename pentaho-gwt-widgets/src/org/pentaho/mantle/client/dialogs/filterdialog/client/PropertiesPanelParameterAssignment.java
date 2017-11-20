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

package org.pentaho.mantle.client.dialogs.filterdialog.client;

import org.pentaho.ui.xul.XulEventSourceAdapter;
import org.pentaho.ui.xul.stereotype.Bindable;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * Assigns a filter _value to a parameter specified by the dashboard widget.
 *
 * @author mlowery
 */
public class PropertiesPanelParameterAssignment extends XulEventSourceAdapter implements java.io.Serializable {

  private String defaultValue;

  /**
   * This class wraps filter items in the dropdown to associate the id with the friendly name. CDF works on the id.
   *
   * @author cboyden
   */
  public static class Filter {
    private String id;
    private String name;

    public Filter( String id, String name ) {
      this.id = id;
      this.name = name;
    }

    public String getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    /**
     * Called by XUL. Should return friendly name.
     */
    @Override
    public String toString() {
      return name;
    }
  }

  // ~ Static fields/initializers ======================================================================================

  public static enum Source {
    STATIC( "Static" ), DYNAMIC( "Dynamic" ); //$NON-NLS-1$ //$NON-NLS-2$

    private final String key;

    Source( String key ) {
      this.key = key;
    }

    @Override
    public String toString() {
      // TODO mlowery fetch localized string from resource bundle
      return key;
    }
  }

  // ~ Instance fields =================================================================================================

  private String name;

  private Source source;

  // This is null if source is STATIC, otherwise it is the CDF id of the filter
  private String selectedFilterId;

  /**
   * Either a static _value (in the case of source=static) or the name (not id) of a filter (in the case of
   * source=dynamic).
   */
  private String _value_;

  /**
   * List of parameters (aka filters from the Dashboard). Will be shown in a dropdown.
   */
  private List<Filter> filters;

  // ~ Constructors ====================================================================================================

  public PropertiesPanelParameterAssignment() {
    super();
  }

  public PropertiesPanelParameterAssignment( final String name, final Source source, final String _value_,
                                             final List<Filter> filters, String defaultVal ) {
    this( name, source, _value_, filters );
    this.defaultValue = defaultVal;
  }

  public PropertiesPanelParameterAssignment( final String name, final Source source, final String _value_,
                                             final List<Filter> filters ) {
    super();
    this.name = name;
    this.source = source;
    if ( this.source == Source.DYNAMIC ) {
      // The id of the filter is coming in, resolve it to a friendly name
      for ( Filter f : filters ) {
        if ( f.getId().equals( _value_ ) ) {
          this._value_ = f.getName();
          this.selectedFilterId = f.getId();
          break;
        }
      }
    } else {
      this._value_ = _value_;
    }
    this.filters = filters;
  }

  // ~ Methods =========================================================================================================

  @Bindable
  public String getName() {
    return name;
  }

  @Bindable
  public void setName( String name ) {
    String previousValue = this.name;
    this.name = name;
    firePropertyChange( "name", previousValue, this.name ); //$NON-NLS-1$
  }

  @Bindable
  public Source getSource() {
    return source;
  }

  @Bindable
  public void setSource( Source source ) {
    Source previousValue = this.source;
    this.source = source;
    firePropertyChange( "source", previousValue, this.source ); //$NON-NLS-1$
  }

  @Bindable
  public String get_value_() {
    if ( _value_ == null ) {
      _value_ = "";
    }
    return _value_;
  }

  @Bindable
  public void set_value_( String _value_ ) {

    // Value may be a Filter name or the "_value" itself. Find a filter by the same name
    this.setSource( Source.STATIC );
    this.selectedFilterId = null;

    for ( Filter paramObj : filters ) {
      if ( _value_ != null && _value_.equals( paramObj.getName() ) ) {
        this.setSource( Source.DYNAMIC );
        this.selectedFilterId = paramObj.getId();
        break;
      }
    }

    String previousValue = this._value_;
    this._value_ = _value_;
    firePropertyChange( "_value_", previousValue, this._value_ ); //$NON-NLS-1$
  }

  /**
   * Used in XUL combobinding attribute.
   */
  @Bindable
  public Vector<PropertiesPanelParameterAssignment.Source> getSourceOptions() {
    return new Vector<PropertiesPanelParameterAssignment.Source>( Arrays.asList( Source.values() ) );
  }

  /**
   * Used in XUL combobinding attribute.
   */
  @SuppressWarnings( "unchecked" )
  @Bindable
  public Vector getValueOptions() {
    Vector<Filter> v = new Vector<Filter>();

    v.addAll( filters );

    return v;
  }

  /**
   * Used in XUL columntypebinding attribute.
   */
  @Bindable
  public String getCellType() {
    if ( filters == null || filters.size() == 0 ) {
      return "text"; //$NON-NLS-1$
    } else {
      return "editablecombobox"; //$NON-NLS-1$
    }
  }

  @Bindable
  public String getSelectedFilterId() {
    return this.selectedFilterId;
  }

  @Override
  public String toString() {
    return new StringBuilder().append( "PropertiesPanelParameterAssignment[" ).append( "name=" ).append( name )
      .append( //$NON-NLS-1$ //$NON-NLS-2$
        ",source=" ).append( source ).append( ",_value_=" ).append( _value_ )
      .append( ",selectedFilterId=" ) //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
      .append( selectedFilterId ).append( "]" ).toString(); //$NON-NLS-1$

  }

  @Bindable
  public String getDefaultValue() {
    return defaultValue;
  }

  @Bindable
  public void setDefaultValue( String defaultValue ) {
    this.defaultValue = defaultValue;
  }
}
