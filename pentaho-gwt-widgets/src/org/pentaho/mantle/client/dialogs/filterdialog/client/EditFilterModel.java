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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import org.pentaho.mantle.client.dialogs.filterdialog.client.PropertiesPanelParameterAssignment.Filter;
import org.pentaho.mantle.client.dialogs.filterdialog.client.PropertiesPanelParameterAssignment.Source;
import org.pentaho.mantle.client.dialogs.filterdialog.client.jsni.FilterDialogConfiguration;
import org.pentaho.mantle.client.dialogs.filterdialog.client.jsni.IPentahoFilter;
import org.pentaho.ui.xul.XulEventSourceAdapter;
import org.pentaho.ui.xul.stereotype.Bindable;

import java.util.ArrayList;
import java.util.List;

public class EditFilterModel extends XulEventSourceAdapter implements java.io.Serializable {

  private IPentahoFilterFactory filterFactory;

  private boolean displayNameChecked;
  private String filterId;
  private String displayName;

  private PovDataType selectedDataType;

  private boolean useFirstValue = true;

  List<FilterDataModel> dataModels = new ArrayList<FilterDataModel>();

  List<FilterComponentModel> componentModels = new ArrayList<FilterComponentModel>();
  private boolean verticalOrientation = true;

  private String widgetId;

  public enum EditType {
    ADD, EDIT
  }

  public enum PovDataType {

    STATIC( 0, "static" ), //$NON-NLS-1$
    SQL( 1, "sql" ), //$NON-NLS-1$
    MQL( 2, "mql" ); //$NON-NLS-1$

    private int index;
    private String name;

    PovDataType( int index, String name ) {
      this.index = index;
      this.name = name;
    }

    public int getIndex() {
      return this.index;
    }

    public String getName() {
      return this.name;
    }

    public static PovDataType parseFromTypeString( String typeString ) {
      for ( PovDataType povDataType : PovDataType.values() ) {
        if ( povDataType.getName().equals( typeString ) ) {
          return povDataType;
        }
      }
      // TODO: log!..
      return null;
    }
  }

  /**
   * Type of CDF component
   */
  public enum PovComponentType {
    // WARNING:enum names are parsed from xul interface and indexes are sync'd,
    // careful when changing them

    DROP_DOWN( "select", 0 ), // Drop Down
    LIST( "selectMultiComponent", 1 ), // Single Value List
    RADIO( "radioComponent", 2 ), // Radio Button
    CHECK( "checkComponent", 3 ), // Check Box
    BUTTON( "multiButtonComponent", 4 ), // Selection Buttons
    TEXT( "textInputComponent", 5 ), // Text Box
    DATE( "dateInputComponent", 6 ); // Date Picker (Calendar Control)

    private final String cdfType;
    private final int index; // index in model&view

    PovComponentType( String cdfType, int index ) {
      this.cdfType = cdfType;
      this.index = index;
    }

    public String getCdfType() {
      return this.cdfType;
    }

    public int getIndex() {
      return this.index;
    }

    public static PovComponentType fromCdfTypeString( String typeString ) {
      for ( PovComponentType compType : PovComponentType.values() ) {
        if ( compType.getCdfType().equals( typeString ) ) {
          return compType;
        }
      }
      return null; // TODO:log!..
    }

  }

  /**
   * Get field names that are specific to the given component type and shouldn't be kept when switching types.
   * <p/>
   * TODO Moved from PentahoPovWidget. This should be delegated down to the PovComponentType.
   *
   * @param compType
   * @return Names of component-specific fields.
   */
  public static final String[] getComponentFieldNames( PovComponentType compType ) {
    switch ( compType ) {
      case LIST:
        return new String[] { "isMultiple", "size" }; //$NON-NLS-1$ //$NON-NLS-2$
      case TEXT:
        return new String[] { "charWidth", "maxChars" }; //$NON-NLS-1$ //$NON-NLS-2$
      case DATE:
        return new String[] { "dateFormat", "startDate", "endDate" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      case CHECK:
      case RADIO:
        return new String[] { "separator" }; //$NON-NLS-1$
      case BUTTON:
        return new String[] { "isMultiple", "separator" }; //$NON-NLS-1$//$NON-NLS-2$
      default:
        return new String[ 0 ];
    }
  }

  private EditType editType = EditType.ADD;

  private PovComponentType selectedComponentType = PovComponentType.DROP_DOWN;

  @Bindable
  public boolean isUseFirstValue() {
    return useFirstValue;
  }

  @Bindable
  public void setUseFirstValue( boolean useFirstValue ) {
    this.useFirstValue = useFirstValue;
  }

  public void setComponentType( PovComponentType componentType ) {
    this.selectedComponentType = componentType;
  }

  @Bindable
  public EditType getEditType() {
    return editType;
  }

  @Bindable
  public void setEditType( EditType editType ) {
    this.editType = editType;
  }

  IPentahoFilter optionalWidget;

  public EditFilterModel( IPentahoFilterFactory filterFactory ) {
    this( null, filterFactory );
  }

  public EditFilterModel( IPentahoFilterFactory filterFactory, IPentahoFilter widget ) {
    this( null, filterFactory, widget );
  }

  public EditFilterModel( FilterDialogConfiguration config, IPentahoFilterFactory filterFactory,
                          IPentahoFilter widget ) {
    this( config, filterFactory );
    editType = EditType.EDIT;
    if ( widget.getParameter() != null ) {
      filterId = widget.getParameter();
    } else {
      filterId = null;
    }
    setDisplayName( widget.getTitle() );
    setDisplayNameChecked( widget.getShowTitle() );
    setVerticalOrientation( widget.isVertical() );
    // force update to UI as the propertychange system can suppress
    this.firePropertyChange( "verticalOrientation", !this.verticalOrientation, this.verticalOrientation );

    PovDataType selectedDataType = null;
    // merge static filter type with model
    if ( widget.isStaticList() ) {
      selectedDataType = PovDataType.STATIC;
    } else if ( widget.isSqlList() ) {
      selectedDataType = PovDataType.SQL;
    } else if ( widget.isMqlList() ) {
      selectedDataType = PovDataType.MQL;
    }

    PovComponentType compType = PovComponentType.fromCdfTypeString( widget.getType() );
    if ( compType != null ) {
      setSelectedComponentType( compType );
      getSelectedComponentModel().apply( widget );
    }

    setSelectedFilterType( selectedDataType );
    if ( getSelectedComponentModel().hasDataModel() ) {
      dataModels.get( selectedDataType.getIndex() ).apply( widget );
    }
    this.setUseFirstValue( widget.getUseFirstValue() );

    this.optionalWidget = widget;
  }

  public IPentahoFilter getOptionalWidget() {
    return optionalWidget;
  }

  public EditFilterModel( FilterDialogConfiguration config, IPentahoFilterFactory filterFactory ) {
    if ( filterFactory == null ) {
      throw new NullPointerException();
    }
    this.filterFactory = filterFactory;
    filterId = null;
    displayName = ""; //$NON-NLS-1$
    setDisplayNameChecked( true );
    setSelectedFilterType( PovDataType.STATIC );
    dataModels.add( new StaticFilterDataModel() );
    dataModels.add( new SqlFilterDataModel() );
    dataModels.add( new MqlFilterDataModel() );

    // componentModels.add(new DropDownComponentModel());
    // componentModels.add(new ListComponentModel());
    // componentModels.add(new RadioComponentModel());
    // componentModels.add(new CheckboxComponentModel());
    // componentModels.add(new ButtonComponentModel());
    // componentModels.add(new TextComponentModel());
    // componentModels.add(new DateComponentModel());
    // ensure order is always as defined in the enum
    // Collections.sort(componentModels,getWidgetTypeModelIndexComparator());
    /*
     * FIXME This is a retarded way of sorting this list but there is no other way for now. GWT 2.0.X doesn't play
     * nicely with Safari 5. http://osdir.com/ml/Google-Web-Toolkit/2010-06/msg01503.html
     */
    List<FilterComponentModel> tmpUnsortedList = new ArrayList<FilterComponentModel>();
    tmpUnsortedList.add( new DropDownComponentModel() );
    tmpUnsortedList.add( new ListComponentModel() );
    tmpUnsortedList.add( new RadioComponentModel() );
    tmpUnsortedList.add( new CheckboxComponentModel() );
    tmpUnsortedList.add( new ButtonComponentModel() );
    tmpUnsortedList.add( new TextComponentModel() );
    tmpUnsortedList.add( new DateComponentModel( config ) );
    for ( int i = 0; i < tmpUnsortedList.size(); i++ ) {
      for ( FilterComponentModel comp : tmpUnsortedList ) {
        if ( comp.getComponentType().index == i ) {
          componentModels.add( comp );
          break;
        }
      }
    }

    setComponentType( PovComponentType.DROP_DOWN );
  }

  static interface FilterDataModel {
    public String getType();

    public void apply( IPentahoFilter provider );

    public void toWidget( IPentahoFilter widget );
  }

  static class NameValuePair {
    String name;
    String value;

    public NameValuePair( String name, String value ) {
      this.name = name;
      this.value = value;
    }

    @Override
    public String toString() {
      return "NameValuePair[name=" + name + ", value=" + value + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

  }

  static class StaticFilterDataModel implements FilterDataModel {
    List<NameValuePair> selections = new ArrayList<NameValuePair>();
    int initialSelection = 0;

    public String getType() {
      return "static"; //$NON-NLS-1$
    }

    public int getInitialSelection() {
      return initialSelection;
    }

    public void setInitialSelection( int initialSelection ) {
      this.initialSelection = initialSelection;
    }

    public void toWidget( IPentahoFilter widget ) {
      widget.switchToStaticList();
      for ( NameValuePair nvp : selections ) {
        String value = null;
        if ( nvp.value != null && nvp.value.length() > 0 ) {
          value = nvp.value;
        }
        widget.addToValuesArray( EditFilterController.unescapeHtmlEntities( nvp.name ), EditFilterController
          .unescapeHtmlEntities( value ) );
      }
    }

    public void apply( IPentahoFilter provider ) {
      int length = provider.getValuesArrayLength();
      for ( int i = 0; i < length; i++ ) {
        String label = provider.getValuesArrayLabelAt( i );
        String value = provider.getValuesArrayValueAt( i );
        if ( value == null ) {
          value = ""; //$NON-NLS-1$
        }
        selections.add( new NameValuePair( label, value ) );
      }

      Object def = provider.getParameterDefault();
      String defaultValue;
      if ( def instanceof Object[] ) {
        defaultValue = ( (Object[]) def )[ 0 ].toString();
      } else {
        defaultValue = def.toString();
      }
      if ( defaultValue != null ) {
        for ( NameValuePair nvp : selections ) {
          if ( defaultValue.equals( nvp.name ) || defaultValue.equals( nvp.value ) ) {
            setInitialSelection( selections.indexOf( nvp ) );
            break;
          }
        }
      }
    }
  }

  abstract static class QueryFilterDataModel implements FilterDataModel {

    protected String povWidgetId = null;
    protected String query = null;

    protected int labelIndex = 0;
    protected int valueIndex = 1;

    protected ArrayList<String> columns = new ArrayList<String>();

    protected enum QueryFilterParams {
      LABEL_IDX( "lblidx" ), VALUE_IDX( "validx" ), FILTER_COLUMNS( "filterColumns" );

      String paramName;

      QueryFilterParams( String paramName ) {
        this.paramName = paramName;
      }

      public String toString() {
        return this.paramName;
      }
    }

    protected List<PropertiesPanelParameterAssignment> queryParameters = null;

    /*
     * ACCESSORS etc
     */

    public void clearColumnList() {
      columns.clear();
    }

    public void addToColumnList( String columnName ) {
      columns.add( columnName );
    }

    List<String> getColumnList() {
      return this.columns;
    }

    public String getQuery() {
      return query;
    }

    public void setQuery( String query ) {
      this.query = query;
    }

    public boolean hasQuery() {
      return query != null && !query.trim().equals( "" );
    }

    public void setLabelIndex( int labelIndex ) {
      this.labelIndex = ( labelIndex >= 0 ) ? labelIndex : 0;
    }

    public int getLabelIndex() {
      return this.labelIndex;
    }

    public void setValueIndex( int valueIndex ) {
      this.valueIndex = valueIndex >= 0 ? valueIndex : 0;
    }

    public int getValueIndex() {
      return this.valueIndex;
    }

    /**
     * model <- widget
     */
    public void apply( IPentahoFilter provider ) {
      this.povWidgetId = provider.getHtmlObject(); // getId();
      String val = provider.getInputParameterValue( QueryFilterParams.LABEL_IDX.toString() );
      if ( val != null && val.trim().length() > 0 ) {
        labelIndex = Integer.parseInt( val );
      }
      val = provider.getInputParameterValue( QueryFilterParams.VALUE_IDX.toString() );
      if ( val != null && val.trim().length() > 0 ) {
        valueIndex = Integer.parseInt( val );
      }

      // get query parameters
      List<Filter> filters = EditFilterController.getOutputParameters( provider.getId() );
      this.queryParameters = new ArrayList<PropertiesPanelParameterAssignment>();
      JsArray<JsArrayString> queryParams = provider.getQueryParameters();
      for ( int i = 0; i <= queryParams.length(); i++ ) {
        JsArrayString param = queryParams.get( i );
        if ( param != null ) {
          boolean isStatic = param.get( 1 ).trim().equals( "" );
          PropertiesPanelParameterAssignment parameter =
            new PropertiesPanelParameterAssignment( param.get( 0 ).substring( 1 ), isStatic ? Source.STATIC
              : Source.DYNAMIC, isStatic ? param.get( 2 ) : param.get( 1 ), filters );
          this.queryParameters.add( parameter );
        }
      }
    }

    /**
     * model -> widget
     */
    public void toWidget( IPentahoFilter widget ) {
      widget.switchToQuerylist( this.getType(), this.getUrl(), this.getCommand() );
      widget.setInputParameterValue( QueryFilterParams.LABEL_IDX.toString(), "" + labelIndex );
      widget.setInputParameterValue( QueryFilterParams.VALUE_IDX.toString(), "" + valueIndex );
      widget.setInputParameterValue( QueryFilterParams.FILTER_COLUMNS.toString(), "true" );

      // set query parameters
      if ( this.queryParameters != null ) {
        widget.clearQueryParameters();
        for ( PropertiesPanelParameterAssignment param : this.queryParameters ) {
          boolean isStatic = param.getSource().equals( PropertiesPanelParameterAssignment.Source.STATIC );
          widget.setQueryParameter( param.getName(), isStatic ? param.get_value_() : param.getSelectedFilterId(),
            isStatic );
        }
        // widget.setParameterListeners(widget.getQueryParameters());
      }

    }

    protected String getUrl() {
      return "dashboards"; //$NON-NLS-1$
    }

    protected abstract String getCommand();

    public List<PropertiesPanelParameterAssignment> getQueryParameters() {
      return this.queryParameters;
    }

    public void setQueryParameters( List<PropertiesPanelParameterAssignment> params ) {
      this.queryParameters = params;
    }

  }

  static class SqlFilterDataModel extends QueryFilterDataModel {
    String datasource;
    private boolean queryApplied = false;

    private enum Param {
      DATASOURCE( "datasource" ), //$NON-NLS-1$
      QUERY( "query" ); //$NON-NLS-1$

      String paramName;

      Param( String paramName ) {
        this.paramName = paramName;
      }

      public String toString() {
        return this.paramName;
      }
    }

    public String getDatasource() {
      return datasource;
    }

    public void setDatasource( String datasource ) {
      this.datasource = datasource;
    }

    public String getType() {
      return "sql"; //$NON-NLS-1$
    }

    protected String getCommand() {
      return "list"; //$NON-NLS-1$
    }

    public void apply( IPentahoFilter widget ) {
      super.apply( widget );
      datasource = widget.getInputParameterValue( Param.DATASOURCE.toString() );
      setQuery( widget.getInputParameterValue( Param.QUERY.toString() ) );
    }

    public void toWidget( IPentahoFilter widget ) {
      // widget.switchToSqlList();
      super.toWidget( widget );
      widget.setInputParameterValue( Param.DATASOURCE.toString(), datasource );
      widget.setInputParameterValue( Param.QUERY.toString(), getQuery() );
    }

    public boolean isQueryApplied() {
      return queryApplied;
    }

    public void setQueryApplied( boolean queryApplied ) {
      this.queryApplied = queryApplied;
    }

  }

  /**
   * Selector using MQL query.
   */
  static class MqlFilterDataModel extends QueryFilterDataModel {

    private enum WidgetParam {
      DATASOURCE( "dataSourceName" ), MODEL_ID( "modelId" ), DOMAIN_ID( "domainId" ), QUERY( "mql" ),
      MQL_JSON( "jsMql" );

      String paramName;

      WidgetParam( String paramName ) {
        this.paramName = paramName;
      }

      public String toString() {
        return this.paramName;
      }
    }

    /*
     * FIELDS
     */
    String datasourceName;
    String domainId; // unused
    String modelId; // unused
    String jsMql;

    /*
     * ACCESSORS
     */
    public void setDataSource( String domainId, String modelId, String dataSourceName ) {
      this.domainId = domainId;
      this.modelId = modelId;
      this.datasourceName = dataSourceName;
    }

    public String getDataSourceName() {
      return this.datasourceName;
    }

    public String getJsMql() {
      return this.jsMql;
    }

    public void setJsMql( String jsMql ) {
      this.jsMql = jsMql;
    }

    /*
     * FilterTypeModel METHODS
     */

    public String getType() {
      return PovDataType.MQL.getName();
    }

    @Override
    protected String getCommand() {
      return "listmql"; //$NON-NLS-1$
    }

    /**
     * model -> widget
     */
    @Override
    public void toWidget( IPentahoFilter widget ) {
      // widget.switchToMqlList();
      super.toWidget( widget );
      widget.setInputParameterValue( WidgetParam.DATASOURCE.toString(), datasourceName );
      widget.setInputParameterValue( WidgetParam.QUERY.toString(), getQuery() ); // only
      // one
      // essential
      // in
      // widget
      widget.setInputParameterValue( WidgetParam.MODEL_ID.toString(), modelId );
      widget.setInputParameterValue( WidgetParam.DOMAIN_ID.toString(), domainId );
      widget.setInputParameterValue( WidgetParam.MQL_JSON.toString(), jsMql );

    }

    /**
     * widget -> model
     */
    @Override
    public void apply( IPentahoFilter widget ) {
      super.apply( widget );
      datasourceName = widget.getInputParameterValue( WidgetParam.DATASOURCE.toString() );
      setQuery( widget.getInputParameterValue( WidgetParam.QUERY.toString() ) );
      modelId = widget.getInputParameterValue( WidgetParam.MODEL_ID.toString() );
      domainId = widget.getInputParameterValue( WidgetParam.DOMAIN_ID.toString() );
      jsMql = widget.getInputParameterValue( WidgetParam.MQL_JSON.toString() );
    }

  } // MqlFilterType

  /*
   * COMPONENT TYPE MODELS (begin)
   */

  static interface FilterComponentModel {
    /**
     * @return CDF component type
     */
    public String getType();

    /**
     * @return Value currently selected in component
     */
    public List<String> getSelectedValue();

    public PovComponentType getComponentType();

    public void toWidget( IPentahoFilter widget );

    public void apply( IPentahoFilter widget );

    public boolean getUseFirst();

    /**
     * @return true if this component gets data from a list or query, false if just user input
     */
    public boolean hasDataModel();
  }

  abstract static class BaseComponentModel implements FilterComponentModel {
    protected String htmlObject;
    protected boolean useFirst = true;

    public boolean getUseFirst() {
      return useFirst;
    }

    public void setUseFirst( boolean useFirst ) {
      this.useFirst = useFirst;
    }

    public String getType() {
      return getComponentType().getCdfType();
    }

    // public List<String> getSelectedValue() {
    // if (htmlObject != null && htmlObject.trim().length() > 0) {
    // List<String> val = new ArrayList<String>();
    // val.add(getFirstValueFromWidget(htmlObject, getType()));
    // return val;
    // } else return null;
    // }

    // TODO:change name
    // private native String getFirstValueFromWidget(String objectHtml, String compType)/*-{
    // if(objectHtml){
    // var wgt = $wnd.$('#' + objectHtml + ' ' + compType);
    // if(wgt) return wgt.val();
    // }
    // return null;
    // }-*/;

    public void toWidget( IPentahoFilter widget ) {
      widget.switchType( this.getComponentType() );
      widget.setUseFirstValue( getUseFirst() );
      this.htmlObject = widget.getHtmlObject();
    }

    public void apply( IPentahoFilter provider ) {
      this.htmlObject = provider.getHtmlObject();
      this.useFirst = provider.getUseFirstValue();
    }

    public boolean hasDataModel() {
      return true;
    }

  }

  abstract static class DefaultValueComponentModel extends BaseComponentModel {
    protected List<String> defaultValue = new ArrayList<String>();

    @Override
    public List<String> getSelectedValue() {
      return getDefaultValue();
    }

    public List<String> getDefaultValue() {
      return defaultValue;
    }

    public void setDefaultValue( List<String> defaultValue ) {
      this.defaultValue = defaultValue;
    }

    @Override
    public void apply( IPentahoFilter provider ) {
      super.apply( provider );
      JsArrayString params = provider.getParameterDefault();
      List<String> values = new ArrayList<String>();
      for ( int i = 0; i < params.length(); i++ ) {
        String value = params.get( i );
        values.add( value );
      }
      setDefaultValue( values );
    }

  }

  static class DropDownComponentModel extends DefaultValueComponentModel {
    public PovComponentType getComponentType() {
      return PovComponentType.DROP_DOWN;
    }

  }

  static class ListComponentModel extends DefaultValueComponentModel {
    protected boolean multiple;

    public boolean isMultiple() {
      return this.multiple;
    }

    public void setMultiple( boolean value ) {
      this.multiple = value;
    }

    private Integer size;

    public void setSize( Integer size ) {
      this.size = size;
    }

    public Integer getSize() {
      return size;
    }

    public PovComponentType getComponentType() {
      return PovComponentType.LIST;
    }

    @Override
    public void toWidget( IPentahoFilter widget ) {
      super.toWidget( widget );
      widget.setMultiple( isMultiple() );
      widget.setSize( getSize() );
    }

    @Override
    public void apply( IPentahoFilter provider ) {
      super.apply( provider );
      setMultiple( provider.isMultiple() );
      setSize( provider.getSize() );
    }
  }

  abstract static class ToggleComponentModel extends DefaultValueComponentModel {

    enum Orientation {
      HORIZONTAL, VERTICAL
    }

    // protected Orientation orientation = Orientation.VERTICAL;
    protected final String V_SEPARATOR = "<br/>";
    protected final String H_SEPARATOR = " ";

    @Override
    public void toWidget( IPentahoFilter widget ) {
      super.toWidget( widget );
      // widget.setSeparator(getOrientation() == Orientation.HORIZONTAL ? H_SEPARATOR : V_SEPARATOR);
    }

    @Override
    public void apply( IPentahoFilter provider ) {
      super.apply( provider );
    }

    protected Orientation getOrientation() {
      return Orientation.VERTICAL;
    }

  }

  private static boolean isStringEmpty( String str ) {
    return str == null || str.trim().equals( "" );
  }

  static class RadioComponentModel extends ToggleComponentModel {
    public PovComponentType getComponentType() {
      return PovComponentType.RADIO;
    }
  }

  static class CheckboxComponentModel extends ToggleComponentModel {
    public PovComponentType getComponentType() {
      return PovComponentType.CHECK;
    }
  }

  static class ButtonComponentModel extends ToggleComponentModel {

    protected boolean multiple;

    public boolean isMultiple() {
      return this.multiple;
    }

    public void setMultiple( boolean value ) {
      this.multiple = value;
    }

    public PovComponentType getComponentType() {
      return PovComponentType.BUTTON;
    }

    @Override
    public void toWidget( IPentahoFilter widget ) {
      super.toWidget( widget );
      widget.setMultiple( this.isMultiple() );
    }

    @Override
    public void apply( IPentahoFilter provider ) {
      super.apply( provider );
      this.setMultiple( provider.isMultiple() );
    }

    @Override
    protected Orientation getOrientation() {
      return Orientation.HORIZONTAL;
    }

  }

  static class TextComponentModel extends DefaultValueComponentModel {
    private Integer width;
    private Integer maxChars;

    public PovComponentType getComponentType() {
      return PovComponentType.TEXT;
    }

    public Integer getWidth() {
      return this.width;
    }

    public void setWidth( Integer width ) {
      this.width = width;
    }

    public Integer getMaxChars() {
      return this.maxChars;
    }

    public void setMaxChars( Integer maxChars ) {
      this.maxChars = maxChars;
    }

    @Override
    public void toWidget( IPentahoFilter widget ) {
      super.toWidget( widget );
      widget.setCharWidth( getWidth() );
      widget.setMaxChars( getMaxChars() );
    }

    @Override
    public void apply( IPentahoFilter widget ) {
      super.apply( widget );
      setWidth( widget.getCharWidth() );
      setMaxChars( widget.getMaxChars() );
    }

    @Override
    public boolean hasDataModel() {
      return false;
    }
  }

  static class DateComponentModel extends DefaultValueComponentModel {
    enum CalendarRestriction {
      NONE, FUTURE_ONLY, PAST_ONLY, RANGE,
    }

    public DateComponentModel( final FilterDialogConfiguration config ) {
      if ( config != null ) {
        setFormat( config.getDefaultDateFormat() );
      }
    }

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private final String TODAY = "TODAY";

    private CalendarRestriction restriction = CalendarRestriction.NONE;
    private String rangeBegin;
    private String rangeEnd;
    private String format = DEFAULT_DATE_FORMAT;

    public CalendarRestriction getRestriction() {
      return restriction;
    }

    public void setRestriction( CalendarRestriction restriction ) {
      this.restriction = restriction;
    }

    // @Override
    // public void setDefaultValue(Object dateStr) {
    // this.defaultValue = dateStr;
    // }

    public String getFormat() {
      return this.format;
    }

    public void setFormat( String dateFormat ) {
      if ( !isStringEmpty( this.format ) ) {
        this.format = dateFormat;
      }
    }

    public String getRangeBegin() {
      return rangeBegin;
    }

    public void setRangeBegin( String rangeBegin ) {
      this.rangeBegin = rangeBegin;
    }

    public String getRangeEnd() {
      return rangeEnd;
    }

    public void setRangeEnd( String rangeEnd ) {
      this.rangeEnd = rangeEnd;
    }

    public PovComponentType getComponentType() {
      return PovComponentType.DATE;
    }

    @Override
    public void toWidget( IPentahoFilter widget ) {
      super.toWidget( widget );
      widget.setDateFormat( this.format );

      switch ( this.restriction ) {
        case FUTURE_ONLY:
          widget.setStartDate( TODAY );
          widget.setEndDate( null );
          break;
        case PAST_ONLY:
          widget.setStartDate( null );
          widget.setEndDate( TODAY );
          break;
        case NONE:
          widget.setStartDate( null );
          widget.setEndDate( null );
          break;
        case RANGE:
          widget.setStartDate( this.rangeBegin );
          widget.setEndDate( this.rangeEnd );
          break;
      }
    }

    @Override
    public void apply( IPentahoFilter provider ) {
      super.apply( provider );
      setFormat( provider.getDateFormat() );
      setRangeBegin( provider.getStartDate() );
      setRangeEnd( provider.getEndDate() );

      if ( getRangeBegin() == TODAY && getRangeEnd() == null ) {
        setRestriction( CalendarRestriction.FUTURE_ONLY );
      } else if ( getRangeBegin() == null && getRangeEnd() == TODAY ) {
        setRestriction( CalendarRestriction.PAST_ONLY );
      } else if ( getRangeBegin() == null && getRangeEnd() == null ) {
        setRestriction( CalendarRestriction.NONE );
      } else {
        setRestriction( CalendarRestriction.RANGE );
      }
    }

    @Override
    public boolean hasDataModel() {
      return false;
    }

  }

  /*
   * Component type models (end)
   */

  /**
   * Generates a filter ID based on the display name.
   */
  private void generateFilterIdIfNecessary() {
    if ( filterId == null ) {
      String uniqueness = String.valueOf( System.currentTimeMillis() );
      filterId = displayName + uniqueness;
    }
  }

  String getFilterId() {
    generateFilterIdIfNecessary();
    return filterId;
  }

  String getFilterInternalName() {
    if ( optionalWidget != null ) {
      return optionalWidget.getName();
    } else {
      return null;
    }
  }

  @Bindable void setDisplayName( String displayName ) {
    this.displayName = displayName;
    // Removed as part of the fix for PDB-1612. This might not be necesary anymore.
    // this.displayName = cleanseDisplayName(displayName);
    generateFilterIdIfNecessary();
  }

  @Bindable
  public String getDisplayName() {
    return displayName;
  }

  @Bindable void setSelectedFilterType( String selectedFilterType ) {
    PovDataType filterType = PovDataType.parseFromTypeString( selectedFilterType );
    setSelectedFilterType( filterType );
  }

  void setSelectedFilterType( PovDataType filterType ) {
    this.selectedDataType = filterType;
  }

  @Bindable
  public String getSelectedFilterType() {
    return selectedDataType.getName();
  }

  public PovDataType getSelectedPovDataType() {
    return selectedDataType;
  }

  public FilterComponentModel getSelectedComponentModel() {
    return getComponentModel( selectedComponentType );
  }

  public PovComponentType getSelectedComponentType() {
    return this.selectedComponentType;
  }

  public void setSelectedComponentType( PovComponentType compType ) {
    this.selectedComponentType = compType;
  }

  public FilterComponentModel getComponentModel( PovComponentType componentType ) {
    return this.componentModels.get( componentType.getIndex() );
  }

  @SuppressWarnings( "unchecked" )
  public <TypeModel extends FilterComponentModel> TypeModel getSpecificComponentModel( Class<TypeModel> typeClass ) {
    for ( FilterComponentModel model : componentModels ) {
      if ( model.getClass().equals( typeClass ) ) {
        return (TypeModel) model;
      }
    }
    return null;
  }

  @SuppressWarnings( "unchecked" )
  public <TypeModel extends FilterDataModel> TypeModel getSpecificDataModel( Class<TypeModel> typeClass ) {
    for ( FilterDataModel model : dataModels ) {
      if ( model.getClass().equals( typeClass ) ) {
        return (TypeModel) model;
      }
    }
    return null;
  }

  @Bindable
  public int getSelectedFilterTypeIndex() {
    if ( selectedDataType != null ) {
      return selectedDataType.getIndex();
    } else {
      return -1;
    }
  }

  public IPentahoFilter applyTo() {
    String originalName = null;
    String parameter = null;
    if ( optionalWidget != null ) {
      originalName = optionalWidget.getOriginalName();
      parameter = optionalWidget.getParameter();
    }

    optionalWidget =
      filterFactory.create( selectedComponentType ); // IPentahoFilter.createIPentahoFilter(selectedComponentType);

    optionalWidget.setId( widgetId );
    optionalWidget.setTitle( displayName );
    optionalWidget.setShowTitle( isDisplayNameChecked() );
    optionalWidget.setVertical( isVerticalOrientation() );
    optionalWidget.setOriginalName( originalName );
    optionalWidget.setParameter( parameter );

    // component model -> widget
    getSelectedComponentModel().toWidget( optionalWidget );
    // data model -> widget
    dataModels.get( getSelectedFilterTypeIndex() ).toWidget( optionalWidget );
    String[] values = null;

    JavaScriptObject array = JsArrayString.createArray();
    if ( getSelectedComponentModel().getUseFirst() == false ) {
      values = getSelectedComponentModel().getSelectedValue().toArray( new String[ 0 ] );

      JsArrayString jsArray = castFromJSObjectToJSArrayString( array );
      for ( int i = 0; i < values.length; i++ ) {
        String value = values[ i ];
        jsArray.set( i, value );
      }

      optionalWidget.setParameterAndDefaultValue( filterId, jsArray );
    } else {
      // Set empty array as default
      optionalWidget.setParameterAndDefaultValue( filterId, castFromJSObjectToJSArrayString( array ) );
    }

    return optionalWidget;
  }

  protected JsArrayString castFromJSObjectToJSArrayString( JavaScriptObject object ) {
    return object.cast();
  }

  @Bindable
  public void setVerticalOrientation( boolean vert ) {

    boolean prevVal = this.verticalOrientation;
    this.verticalOrientation = vert;
    this.firePropertyChange( "verticalOrientation", prevVal, this.verticalOrientation );
  }

  @Bindable
  public boolean isVerticalOrientation() {
    return verticalOrientation;
  }

  public boolean hasComponentTypeChanged( IPentahoFilter widget ) {
    return !widget.getType().equals( getSelectedComponentType().getCdfType() );
  }

  @Bindable
  public void setDisplayNameChecked( boolean displayNameChecked ) {
    this.displayNameChecked = displayNameChecked;
  }

  @Bindable
  public boolean isDisplayNameChecked() {
    return displayNameChecked;
  }

  public FilterDataModel getSelectedFilterDataModel() {
    return dataModels.get( getSelectedFilterTypeIndex() );
  }

  public void setWidgetId( String widgetId ) {
    this.widgetId = widgetId;
  }
}
