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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Timer;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import org.pentaho.mantle.client.dialogs.filterdialog.client.EditFilterModel.ButtonComponentModel;
import org.pentaho.mantle.client.dialogs.filterdialog.client.EditFilterModel.DateComponentModel;
import org.pentaho.mantle.client.dialogs.filterdialog.client.EditFilterModel.DateComponentModel.CalendarRestriction;
import org.pentaho.mantle.client.dialogs.filterdialog.client.EditFilterModel.DefaultValueComponentModel;
import org.pentaho.mantle.client.dialogs.filterdialog.client.EditFilterModel.FilterComponentModel;
import org.pentaho.mantle.client.dialogs.filterdialog.client.EditFilterModel.FilterDataModel;
import org.pentaho.mantle.client.dialogs.filterdialog.client.EditFilterModel.ListComponentModel;
import org.pentaho.mantle.client.dialogs.filterdialog.client.EditFilterModel.MqlFilterDataModel;
import org.pentaho.mantle.client.dialogs.filterdialog.client.EditFilterModel.NameValuePair;
import org.pentaho.mantle.client.dialogs.filterdialog.client.EditFilterModel.PovComponentType;
import org.pentaho.mantle.client.dialogs.filterdialog.client.EditFilterModel.PovDataType;
import org.pentaho.mantle.client.dialogs.filterdialog.client.EditFilterModel.QueryFilterDataModel;
import org.pentaho.mantle.client.dialogs.filterdialog.client.EditFilterModel.SqlFilterDataModel;
import org.pentaho.mantle.client.dialogs.filterdialog.client.EditFilterModel.StaticFilterDataModel;
import org.pentaho.mantle.client.dialogs.filterdialog.client.EditFilterModel.TextComponentModel;
import org.pentaho.mantle.client.dialogs.filterdialog.client.PropertiesPanelParameterAssignment.Filter;
import org.pentaho.mantle.client.dialogs.filterdialog.client.PropertiesPanelParameterAssignment.Source;
import org.pentaho.mantle.client.dialogs.filterdialog.client.jsni.FilterDialogConfiguration;
import org.pentaho.mantle.client.dialogs.filterdialog.client.jsni.JsniFilterPresaveResult;
import org.pentaho.database.model.IDatabaseConnection;
import org.pentaho.gwt.widgets.client.utils.string.StringUtils;
import org.pentaho.reporting.libraries.base.util.CSVTokenizer;
import org.pentaho.ui.database.event.IConnectionAutoBeanFactory;
import org.pentaho.ui.database.event.IDatabaseConnectionList;
import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.binding.Binding;
import org.pentaho.ui.xul.binding.BindingConvertor;
import org.pentaho.ui.xul.binding.BindingFactory;
import org.pentaho.ui.xul.components.XulButton;
import org.pentaho.ui.xul.components.XulCheckbox;
import org.pentaho.ui.xul.components.XulLabel;
import org.pentaho.ui.xul.components.XulMenuList;
import org.pentaho.ui.xul.components.XulMenuitem;
import org.pentaho.ui.xul.components.XulRadio;
import org.pentaho.ui.xul.components.XulTextbox;
import org.pentaho.ui.xul.components.XulTreeCell;
import org.pentaho.ui.xul.components.XulTreeCol;
import org.pentaho.ui.xul.containers.XulDeck;
import org.pentaho.ui.xul.containers.XulDialog;
import org.pentaho.ui.xul.containers.XulListbox;
import org.pentaho.ui.xul.containers.XulTree;
import org.pentaho.ui.xul.containers.XulTreeCols;
import org.pentaho.ui.xul.containers.XulTreeRow;
import org.pentaho.ui.xul.containers.XulVbox;
import org.pentaho.ui.xul.gwt.tags.GwtMenuList;
import org.pentaho.ui.xul.impl.AbstractXulEventHandler;
import org.pentaho.ui.xul.stereotype.Bindable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EditFilterController extends AbstractXulEventHandler implements java.io.Serializable {

  // Configuration for the filter dialog. Static due to getOutputParameters().
  private static FilterDialogConfiguration configuration;
  List<FilterDialogCallback> callbacks = new ArrayList<FilterDialogCallback>();
  EditFilterModel model;

  /* ************************
   * XUL Components (begin) *
   */

  XulButton okBtn = null;

  XulMenuList<XulMenuitem> filterTypeList = null;
  XulDeck filterTypeDeck = null;
  XulTextbox displayNameText = null;
  XulVbox displayNameOptionContainer = null;

  // for now hardcode the list types in the controller
  XulTree staticSelectionTable = null;
  XulTree testResultsTable = null;

  // sql list
  XulMenuList<XulMenuitem> sqlDatasourceList = null;
  XulTextbox sqlQueryText = null;

  // mql list
  XulListbox mqlFilterSelectedColumns = null;
  XulLabel mqlFilterSelectedDataSource = null;
  XulButton mqlFilterEditButton = null;

  XulTextbox addSelectionLabelText = null;
  XulTextbox addSelectionValueText = null;
  XulDialog addSelectionDialog = null;
  XulTextbox editSelectionLabelText = null;
  XulTextbox editSelectionValueText = null;

  XulTextbox sqlParamLabelText = null;
  XulTextbox sqlParamValueText = null;
  XulTree sqlParameterDefaults = null;
  XulTextbox paramsSqlQueryText = null;

  XulDialog editSelectionDialog = null;
  XulDialog waitingDialog = null;
  XulDialog editFilterDialog = null;
  XulDialog parametersDialog = null;
  XulDialog queryDialog = null;
  XulDialog testResultsDialog = null;
  XulDialog sqlErrorQueryDialog = null;
  XulDialog errorDialog = null;
  XulLabel errorDialogMessage = null;

  XulCheckbox displayNameChecked = null;

  // for enabling/disabling data part of filter definitions
  XulDeck filterDataDeck = null;
  private final int FILTER_DATA_ENABLED = 0;
  private final int FILTER_DATA_DISABLED = 1;

  private final int FILTER_DEFAULT_DECK_STATIC = 0;
  private final int FILTER_DEFAULT_DECK_QUERY = 1;

  // filter component type properties
  XulDeck filterPropertiesDeck = null;

  // buttons
  XulButton sqlParameterButton = null;
  XulButton mqlParameterButton = null;
  XulButton dropDownFilterButton = null;
  XulButton listFilterButton = null;
  XulButton radioFilterButton = null;
  XulButton checkboxFilterButton = null;
  XulButton buttonFilterButton = null;
  XulButton textfieldFilterButton = null;
  XulButton datepickerFilterButton = null;

  // -drop down
  XulMenuList<XulMenuitem> editFilterDropdownLabelListbox = null;
  XulMenuList<XulMenuitem> editFilterDropdownValueListbox = null;

  // -list
  XulMenuList<XulMenuitem> editFilterListLabelListbox = null;
  XulMenuList<XulMenuitem> editFilterListValueListbox = null;
  XulCheckbox editFilterPropListIsMultiple = null;
  XulTextbox editFilterPropListSize = null;

  // -radio
  XulMenuList<XulMenuitem> editFilterRadioLabelListbox = null;
  XulMenuList<XulMenuitem> editFilterRadioValueListbox = null;

  // -checkbox
  XulMenuList<XulMenuitem> editFilterCheckboxLabelListbox = null;
  XulMenuList<XulMenuitem> editFilterCheckboxValueListbox = null;

  // -button
  XulMenuList<XulMenuitem> editFilterButtonLabelListbox = null;
  XulMenuList<XulMenuitem> editFilterButtonValueListbox = null;
  XulCheckbox editFilterPropButtonIsMultiple = null;

  // -text
  XulTextbox editFilterPropTextMaxChars = null;
  XulTextbox editFilterPropTextWidth = null;
  XulVbox editFilterPropTextContainer = null;

  // -date
  XulTextbox editFilterPropDateFormat = null;
  XulTextbox editFilterPropDateFrom = null;
  XulTextbox editFilterPropDateTo = null;

  // -orientation
  XulVbox editButtonOrientationContainer = null;
  XulVbox editCheckboxOrientationContainer = null;
  XulVbox editRadioOrientationContainer = null;
  XulMenuList<XulMenuitem> editButtonOrientation = null;
  XulMenuList<XulMenuitem> editCheckboxOrientation = null;
  XulMenuList<XulMenuitem> editRadioOrientation = null;

  XulVbox editFilterDateRestrictionsContainer = null;

  XulMenuList<XulMenuitem> editFilterDateRestriction = null;

  XulRadio useFirstRadio;
  XulRadio specifyRadio;
  XulTextbox specifyTextbox;
  XulMenuList specifyList;
  XulDeck specifyWidgetDeck;
  List<XulComponent> optionsPanels = new ArrayList<XulComponent>();

  // Filter input parameter tables
  private XulTree dlgFilterParameterAssignmentTable = null;

  /*
   * XUL Components (end)*********************
   */

  // if false, keep models from being updated (when listboxes are populated)
  private boolean updateSelectedIndexes = true;

  private ArrayList<XulMenuList<XulMenuitem>> columnLists = new ArrayList<XulMenuList<XulMenuitem>>();
  private ArrayList<XulMenuList<XulMenuitem>> valueColumnLists = new ArrayList<XulMenuList<XulMenuitem>>();
  private ArrayList<XulMenuList<XulMenuitem>> labelColumnLists = new ArrayList<XulMenuList<XulMenuitem>>();

  IFilterSQLSupport sqlSupport;
  private BindingFactory bf;
  private boolean initialized;
  private boolean showErrorQueryDialog = false;
  private boolean verticalOrientation = true;
  private boolean useFirstValue;

  private Map<PovComponentType, XulButton> povTypeToButtonMapping;
  protected IConnectionAutoBeanFactory connectionAutoBeanFactory;

  public EditFilterController() {
    initialization();
  }

  protected void initialization() {
    connectionAutoBeanFactory = GWT.create( IConnectionAutoBeanFactory.class );
  }

  public void addCallback( FilterDialogCallback c ) {
    if ( c == null ) {
      throw new NullPointerException();
    }
    callbacks.add( c );
  }

  public void setSQLSupport( IFilterSQLSupport sqlSupport ) {
    if ( sqlSupport == null ) {
      throw new NullPointerException();
    }
    this.sqlSupport = sqlSupport;
  }

  /**
   * Called when the Xul Dom is ready, grab all Xul references here.
   */
  @Bindable
  public void init() {
    // debug();
    if ( !this.initialized ) {
      if ( sqlSupport == null ) {
        throw new NullPointerException( "IFilterSQLSupport required" ); //$NON-NLS-1$
      }
      // set variables to respective components
      initFetchComponents();

      // Group components with similar behaviour
      initGroupComponents();

      // set all bindings
      initBindings();

      ( (GwtMenuList) filterTypeList ).layout();

      // js auxiliaries
      defineMqlFilterCallbacks( this );
      // setSqlQueryApplied(this.sqlQueryApplied);

      this.initialized = true;
    }
  }

  /**
   * Associate component references to document components.
   */
  @SuppressWarnings( "unchecked" )
  private void initFetchComponents() {
    okBtn = (XulButton) document.getElementById( "editFilterDialog_accept" ); //$NON-NLS-1$
    filterTypeList = (XulMenuList<XulMenuitem>) document.getElementById( "filterTypeList" ); //$NON-NLS-1$
    filterTypeDeck = (XulDeck) document.getElementById( "filterTypeDeck" ); //$NON-NLS-1$
    displayNameText = (XulTextbox) document.getElementById( "displayNameText" ); //$NON-NLS-1$
    displayNameOptionContainer = (XulVbox) document.getElementById( "displayNameOptionContainer" );
    displayNameChecked = (XulCheckbox) document.getElementById( "displayNameChecked" ); //$NON-NLS-1$
    addSelectionDialog = (XulDialog) document.getElementById( "addSelectionDialog" ); //$NON-NLS-1$
    addSelectionLabelText = (XulTextbox) document.getElementById( "addSelectionLabelText" ); //$NON-NLS-1$
    addSelectionValueText = (XulTextbox) document.getElementById( "addSelectionValueText" ); //$NON-NLS-1$
    editSelectionDialog = (XulDialog) document.getElementById( "editSelectionDialog" ); //$NON-NLS-1$
    waitingDialog = (XulDialog) document.getElementById( "waitingDialog" ); //$NON-NLS-1$
    editSelectionLabelText = (XulTextbox) document.getElementById( "editSelectionLabelText" ); //$NON-NLS-1$
    editSelectionValueText = (XulTextbox) document.getElementById( "editSelectionValueText" ); //$NON-NLS-1$

    sqlParamLabelText = (XulTextbox) document.getElementById( "sqlParamLabelText" ); //$NON-NLS-1$
    sqlParamValueText = (XulTextbox) document.getElementById( "sqlParamValueText" ); //$NON-NLS-1$
    sqlParameterDefaults = (XulTree) document.getElementById( "sqlParameterDefaults" ); //$NON-NLS-1$

    // selection type objects
    staticSelectionTable = (XulTree) document.getElementById( "staticSelectionTable" ); //$NON-NLS-1$
    testResultsTable = (XulTree) document.getElementById( "testResultsTable" ); //$NON-NLS-1$
    sqlDatasourceList = (XulMenuList<XulMenuitem>) document.getElementById( "sqlDatasourceList" ); //$NON-NLS-1$

    sqlQueryText = (XulTextbox) document.getElementById( "sqlQueryText" ); //$NON-NLS-1$
    paramsSqlQueryText = (XulTextbox) document.getElementById( "paramsSqlQueryText" ); //$NON-NLS-1$
    sqlErrorQueryDialog = (XulDialog) document.getElementById( "sqlErrorQueryDialog" ); //$NON-NLS-1$
    errorDialog = (XulDialog) document.getElementById( "errorDialog" );
    errorDialogMessage = (XulLabel) document.getElementById( "errorDialogMessage" );

    editFilterDialog = (XulDialog) document.getElementById( "editFilterDialog" ); //$NON-NLS-1$
    parametersDialog = (XulDialog) document.getElementById( "parametersDialog" ); //$NON-NLS-1$
    testResultsDialog = (XulDialog) document.getElementById( "testResultsDialog" ); //$NON-NLS-1$
    queryDialog = (XulDialog) document.getElementById( "queryDialog" ); //$NON-NLS-1$

    mqlFilterSelectedColumns = (XulListbox) document.getElementById( "mqlFilterSelectedColumns" ); //$NON-NLS-1$
    mqlFilterSelectedDataSource = (XulLabel) document.getElementById( "mqlFilterSelectedDataSource" ); //$NON-NLS-1$
    mqlFilterEditButton = (XulButton) document.getElementById( "mqlFilterEditButton" ); //$NON-NLS-1$

    filterDataDeck = (XulDeck) document.getElementById( "filterDataDeck" ); //$NON-NLS-1$

    sqlParameterButton = (XulButton) document.getElementById( "sqlParameterButton" ); //$NON-NLS-1$
    mqlParameterButton = (XulButton) document.getElementById( "mqlParameterButton" ); //$NON-NLS-1$

    dropDownFilterButton = (XulButton) document.getElementById( "dropDownFilterButton" ); //$NON-NLS-1$
    listFilterButton = (XulButton) document.getElementById( "listFilterButton" ); //$NON-NLS-1$
    radioFilterButton = (XulButton) document.getElementById( "radioFilterButton" ); //$NON-NLS-1$
    checkboxFilterButton = (XulButton) document.getElementById( "checkboxFilterButton" ); //$NON-NLS-1$
    buttonFilterButton = (XulButton) document.getElementById( "buttonFilterButton" ); //$NON-NLS-1$
    textfieldFilterButton = (XulButton) document.getElementById( "textfieldFilterButton" ); //$NON-NLS-1$
    datepickerFilterButton = (XulButton) document.getElementById( "datepickerFilterButton" ); //$NON-NLS-1$

    // filter component type properties
    filterPropertiesDeck = (XulDeck) document.getElementById( "filterPropertiesDeck" ); //$NON-NLS-1$
    // -drop down
    editFilterDropdownLabelListbox =
      (XulMenuList<XulMenuitem>) document.getElementById( "editFilterDropdownLabelListbox" ); //$NON-NLS-1$
    editFilterDropdownValueListbox =
      (XulMenuList<XulMenuitem>) document.getElementById( "editFilterDropdownValueListbox" ); //$NON-NLS-1$

    // -list
    editFilterListLabelListbox =
      (XulMenuList<XulMenuitem>) document.getElementById( "editFilterListLabelListbox" ); //$NON-NLS-1$
    editFilterListValueListbox =
      (XulMenuList<XulMenuitem>) document.getElementById( "editFilterListValueListbox" ); //$NON-NLS-1$
    editFilterPropListIsMultiple = (XulCheckbox) document.getElementById( "editFilterPropListIsMultiple" ); //$NON-NLS-1$
    editFilterPropListSize = (XulTextbox) document.getElementById( "editFilterPropListSize" ); //$NON-NLS-1$
    //if(editFilterPropListSize!=null) editFilterPropListSize.setOninput("return isNumericKeyPress(event);");
    //$NON-NLS-1$
    setFieldNumeric( "editFilterPropListSize" );

    // -radio
    editFilterRadioLabelListbox =
      (XulMenuList<XulMenuitem>) document.getElementById( "editFilterRadioLabelListbox" ); //$NON-NLS-1$
    editFilterRadioValueListbox =
      (XulMenuList<XulMenuitem>) document.getElementById( "editFilterRadioValueListbox" ); //$NON-NLS-1$

    // -checkbox
    editFilterCheckboxLabelListbox =
      (XulMenuList<XulMenuitem>) document.getElementById( "editFilterCheckboxLabelListbox" ); //$NON-NLS-1$
    editFilterCheckboxValueListbox =
      (XulMenuList<XulMenuitem>) document.getElementById( "editFilterCheckboxValueListbox" ); //$NON-NLS-1$

    // -button
    editFilterButtonLabelListbox =
      (XulMenuList<XulMenuitem>) document.getElementById( "editFilterButtonLabelListbox" ); //$NON-NLS-1$
    editFilterButtonValueListbox =
      (XulMenuList<XulMenuitem>) document.getElementById( "editFilterButtonValueListbox" ); //$NON-NLS-1$
    editFilterPropButtonIsMultiple =
      (XulCheckbox) document.getElementById( "editFilterPropButtonIsMultiple" ); //$NON-NLS-1$

    // -text
    editFilterPropTextMaxChars = (XulTextbox) document.getElementById( "editFilterPropTextMaxChars" ); //$NON-NLS-1$
    //notWorking: if(editFilterPropTextMaxChars!=null) editFilterPropTextMaxChars.setOninput("return
    // isNumericKeyPress(event);"); //$NON-NLS-1$
    setFieldNumeric( "editFilterPropTextMaxChars" );
    // setAsNumeric("editFilterPropTextMaxChars"); //$NON-NLS-1$
    editFilterPropTextWidth = (XulTextbox) document.getElementById( "editFilterPropTextWidth" ); //$NON-NLS-1$
    //notWorking: if(editFilterPropTextWidth!=null) editFilterPropTextWidth.setOninput("return isNumericKeyPress
    // (event);"); //$NON-NLS-1$
    setFieldNumeric( "editFilterPropTextWidth" ); //$NON-NLS-1$

    editFilterPropTextContainer = (XulVbox) document.getElementById( "editFilterPropTextContainer" );

    // -date

    editFilterDateRestrictionsContainer =
      (XulVbox) document.getElementById( "editFilterDateRestrictionsContainer" ); //$NON-NLS-1$

    editFilterPropDateFormat = (XulTextbox) document.getElementById( "editFilterPropDateFormat" ); //$NON-NLS-1$
    editFilterPropDateFrom = (XulTextbox) document.getElementById( "editFilterPropDateFrom" ); //$NON-NLS-1$
    editFilterPropDateTo = (XulTextbox) document.getElementById( "editFilterPropDateTo" ); //$NON-NLS-1$
    editFilterDateRestriction =
      (XulMenuList<XulMenuitem>) document.getElementById( "editFilterDateRestriction" ); //$NON-NLS-1$

    editButtonOrientationContainer = (XulVbox) document.getElementById( "button_orientation_container" );
    editCheckboxOrientationContainer = (XulVbox) document.getElementById( "checkbox_orientation_container" );
    editRadioOrientationContainer = (XulVbox) document.getElementById( "radio_orientation_container" );

    editButtonOrientation = (XulMenuList<XulMenuitem>) document.getElementById( "button_orientation" );
    editCheckboxOrientation = (XulMenuList<XulMenuitem>) document.getElementById( "checkbox_orientation" );
    editRadioOrientation = (XulMenuList<XulMenuitem>) document.getElementById( "radio_orientation" );

    dlgFilterParameterAssignmentTable =
      (XulTree) document.getElementById( "dlgFilterParameterAssignmentTable" ); //$NON-NLS-1$

    useFirstRadio = (XulRadio) document.getElementById( "useFirstRadio" );
    specifyRadio = (XulRadio) document.getElementById( "specifyRadio" );
    specifyTextbox = (XulTextbox) document.getElementById( "specifyTextbox" );
    specifyList = (XulMenuList) document.getElementById( "specifyList" );
    specifyWidgetDeck = (XulDeck) document.getElementById( "specifyWidgetDeck" );

    for ( Object s : Arrays.asList( "button-label-container", "checkbox-label-container", "list-label-container",
      "dropdown-label-container", "radio-label-container" ) ) {
      optionsPanels.add( document.getElementById( (String) s ) );
    }

  }

  /**
   * Group components with same behavior.
   */
  private void initGroupComponents() {
    labelColumnLists.add( editFilterDropdownLabelListbox );
    valueColumnLists.add( editFilterDropdownValueListbox );

    labelColumnLists.add( editFilterListLabelListbox );
    valueColumnLists.add( editFilterListValueListbox );

    labelColumnLists.add( editFilterRadioLabelListbox );
    valueColumnLists.add( editFilterRadioValueListbox );

    labelColumnLists.add( editFilterCheckboxLabelListbox );
    valueColumnLists.add( editFilterCheckboxValueListbox );

    labelColumnLists.add( editFilterButtonLabelListbox );
    valueColumnLists.add( editFilterButtonValueListbox );

    columnLists.addAll( labelColumnLists );
    columnLists.addAll( valueColumnLists );

  }

  /**
   * Create component bindings
   */
  private void initBindings() {

    bf.createBinding( okBtn, "disabled", this, "!dialogValid" );
    if ( sqlSupport.hasSqlFilterPerms() ) { // add sql list option
      try {

        XulMenuitem sqlitem = (XulMenuitem) document.createElement( "menuitem" ); //$NON-NLS-1$
        // listitem label="SQL List" value="sql"
        // I18N!
        sqlitem.setLabel( sqlSupport.getSqlListLabel() );
        //  sqlitem.setAttribute("label",MSGS.sqlList()); //$NON-NLS-1$
        sqlitem.setAttribute( "value", EditFilterModel.PovDataType.SQL.getName() ); //$NON-NLS-1$
        filterTypeList.getFirstChild().addChildAt( sqlitem, EditFilterModel.PovDataType.SQL.getIndex() );
      } catch ( XulException e ) {
        e.printStackTrace();
      }

    }

    BindingConvertor<int[], Boolean> buttonConvertor = new BindingConvertor<int[], Boolean>() {
      @Override
      public Boolean sourceToTarget( int[] value ) {
        return ( value == null || value.length == 0 );
      }

      @Override
      public int[] targetToSource( Boolean value ) {
        return null;
      }
    };

    // * BINDINGS (view->model only) *
    bf.setBindingType( Binding.Type.ONE_WAY );
    bf.createBinding( staticSelectionTable,
      "selectedRows", "staticSelectionTableMoveSelectedUp", "disabled",
      buttonConvertor ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    bf.createBinding( staticSelectionTable,
      "selectedRows", "staticSelectionTableMoveSelectedDown", "disabled",
      buttonConvertor ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    bf.createBinding( staticSelectionTable,
      "selectedRows", "staticSelectionTableEditSelected", "disabled",
      buttonConvertor ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    bf.createBinding( staticSelectionTable,
      "selectedRows", "staticSelectionTableRemoveSelected", "disabled",
      buttonConvertor ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    bf.createBinding( sqlQueryText, "value", this, "dialogValid" );

    bf.createBinding( sqlQueryText, "value", this, "sqlQueryApplied", new BindingConvertor<String, Boolean>() {
      @Override
      public Boolean sourceToTarget( String value ) {
        return false;
      }

      @Override
      public String targetToSource( Boolean value ) {
        return null;
      }
    } );

    bf.createBinding( "useFirstRadio", "selected", "specifyTextbox", "disabled" );
    bf.createBinding( "useFirstRadio", "selected", "specifyList", "disabled" );
    // bf.createBinding("specifyRadio", "selected", "specifyTextbox", "!disabled");
    // bf.createBinding("specifyRadio", "selected", "specifyList", "!disabled");

    useFirstRadio.setSelected( true );

    paramsSqlQueryText.addPropertyChangeListener( new PropertyChangeListener() {
      @Override
      public void propertyChange( PropertyChangeEvent evt ) {
        Timer timer = new Timer() {
          public void run() {
            String query = paramsSqlQueryText.getValue();
            setSqlFilterInputParameters( query );
          }
        };
        timer.schedule( 1000 );
      }
    } );

    bf.createBinding( displayNameText, "value", this, "dialogValid" );

    // associate edit button in mql to existence of selected mql columns
    bf.createBinding( mqlFilterSelectedColumns, "rows", "mqlFilterEditButton", "disabled",
      new BindingConvertor<Integer, Boolean>() {
        @Override
        public Boolean sourceToTarget( Integer value ) {
          return value == null || value.intValue() == 0;
        }

        @Override
        public Integer targetToSource( Boolean value ) {
          return null;
        }
      } );

    BindingConvertor<Integer, Integer> intConvertor = new BindingConvertor<Integer, Integer>() {
      @Override
      public Integer sourceToTarget( Integer value ) {
        return value;
      }

      @Override
      public Integer targetToSource( Integer value ) {
        return value;
      }
    };
    BindingConvertor<Boolean, Boolean> boolConvertor = new BindingConvertor<Boolean, Boolean>() {
      @Override
      public Boolean sourceToTarget( Boolean value ) {
        return value;
      }

      @Override
      public Boolean targetToSource( Boolean value ) {
        return value;
      }
    };
    BindingConvertor<String, Integer> stringIntConvertor = new BindingConvertor<String, Integer>() {
      @Override
      public Integer sourceToTarget( String value ) {
        try {
          return Integer.parseInt( value );
        } catch ( NumberFormatException nfe ) {
          return null;
        }
      }

      @Override
      public String targetToSource( Integer value ) {
        return ( value != null ) ? value.toString() : "";
      }
    };

    // text model
    bf.createBinding( editFilterPropTextMaxChars, "value", this, "textMaxChars", stringIntConvertor );
    bf.createBinding( editFilterPropTextWidth, "value", this, "textWidth", stringIntConvertor );

    // 'allow multiple' checkboxes
    bf.createBinding( editFilterPropButtonIsMultiple, "checked", this, "multiple", boolConvertor );
    bf.createBinding( editFilterPropListIsMultiple, "checked", this, "multiple", boolConvertor );

    // Allow the list size to be defined
    bf.createBinding( editFilterPropListSize, "value", this, "size", stringIntConvertor );

    // date
    bf.createBinding( editFilterPropDateFormat, "value", this, "dateFormat" );
    bf.createBinding( editFilterPropDateFrom, "value", this, "dateFrom" );
    bf.createBinding( editFilterPropDateTo, "value", this, "dateTo" );

    // default value
    BindingConvertor<String, List<String>> defaultValueBindingConverter = new BindingConvertor<String, List<String>>() {

      @Override
      public List<String> sourceToTarget( String s ) {
        List<String> val = new ArrayList<String>();
        // support comma separated values
        if ( s == null ) {
          return val;
        }
        CSVTokenizer tokenizer = new CSVTokenizer( s, "|", "\"", true );
        while ( tokenizer.hasMoreTokens() ) {
          val.add( tokenizer.nextToken().trim() );
        }
        return val;
      }

      @Override
      public String targetToSource( List<String> values ) {
        if ( values != null && values.size() > 0 ) {
          return values.get( 0 );
        } else {
          return "";
        }
      }
    };

    // bf.setBindingType(Binding.Type.BI_DIRECTIONAL);

    bf.createBinding( specifyTextbox, "value", this, "defaultValue", defaultValueBindingConverter );

    bf.createBinding( specifyList, "selectedItem", this, "defaultValue", defaultValueBindingConverter );

    bf.setBindingType( Binding.Type.ONE_WAY );

    for ( XulMenuList menuList : valueColumnLists ) {
      if ( menuList != null ) {
        bf.createBinding( menuList, "selectedIndex", this, "selectedValueIndex", intConvertor );
      }
    }
    for ( XulMenuList menuList : labelColumnLists ) {
      if ( menuList != null ) {
        bf.createBinding( menuList, "selectedIndex", this, "selectedLabelIndex", intConvertor );
      }
    }

    bf.setBindingType( Binding.Type.BI_DIRECTIONAL );

    bf.createBinding( "useFirstRadio", "selected", this, "useFirstValue" );
    bf.createBinding( "specifyRadio", "selected", this, "!useFirstValue" );

    List<Binding> bindings = new ArrayList<Binding>();

    BindingConvertor orientationConverter = new BindingConvertor<Boolean, Integer>() {
      @Override
      public Integer sourceToTarget( Boolean vertical ) {
        return ( vertical ) ? 0 : 1;
      }

      @Override
      public Boolean targetToSource( Integer index ) {
        return index == 0;
      }
    };
    bindings.add( bf.createBinding( this, "verticalOrientation", editRadioOrientation, "selectedIndex",
      orientationConverter ) );
    bindings.add( bf.createBinding( this, "verticalOrientation", editCheckboxOrientation, "selectedIndex",
      orientationConverter ) );
    bindings.add( bf.createBinding( this, "verticalOrientation", editButtonOrientation, "selectedIndex",
      orientationConverter ) );

  }

  // FIXME: how to make this work through gwt?..
  private static final native void setFieldNumeric( String id )/*-{
    var field = $doc.getElementById(id);
    if (field) {
      field.onkeypress = $wnd.isNumericKeyPress;
    }
  }-*/;

  @Bindable
  public void setUseFirstValue( boolean useFirstValue ) {
    this.useFirstValue = useFirstValue;
    FilterComponentModel compModel = model.getSelectedComponentModel();
    if ( compModel instanceof DefaultValueComponentModel ) {
      ( (DefaultValueComponentModel) compModel ).setUseFirst( useFirstValue );
    }
    firePropertyChange( "useFirstValue", !useFirstValue, useFirstValue );

  }

  @Bindable
  public boolean getUseFirstValue() {
    return this.useFirstValue;
  }

  @Bindable
  public boolean isDialogValid() {

    boolean retVal = true;
    // Needs a display name
    retVal &= StringUtils.isEmpty( displayNameText.getValue() ) == false;

    // Either has key/value pairs or SQL or MQL
    retVal &=
      staticSelectionTable.getRootChildren().getChildNodes().size() > 0
        || StringUtils.isEmpty( sqlQueryText.getValue() ) == false || mqlFilterSelectedColumns.getRowCount() > 0
        // or no data type is selected (text/date)
        || filterDataDeck.getSelectedIndex() == FILTER_DATA_DISABLED;
    return retVal;
  }

  // logic is in the accessor
  @Bindable
  public void setDialogValid() {
    fireDialogValidCheck();
  }

  private void fireDialogValidCheck() {
    this.firePropertyChange( "dialogValid", null, isDialogValid() );
  }

  public String getDatabaseConnectionServiceURL() {
    String baseUrl = GWT.getHostPageBaseURL();
    return baseUrl + "../../../plugin/data-access/api/connection/list";
  }

  boolean populatedSqlDatasources = false;

  public void populateSqlDatasources() {
    try {
      if ( !populatedSqlDatasources ) {

        String cacheBuster = String.valueOf( new java.util.Date().getTime() );
        String baseUrl = getDatabaseConnectionServiceURL();
        RequestBuilder builder =
          new RequestBuilder( RequestBuilder.GET, URL.encode( baseUrl + "?ts=" + cacheBuster ) ); //$NON-NLS-1$
        try {
          builder.setHeader( "Content-Type", "application/json" ); //$NON-NLS-1$ //$NON-NLS-2$
          // Request request =
          builder.sendRequest( "", new RequestCallback() { //$NON-NLS-1$
            public void onError( Request request, Throwable exception ) {
              // Couldn't connect to server (could be timeout, SOP violation, etc.)
              applyModelAsync();
            }

            public void onResponseReceived( Request request, Response response ) {
              try {
                if ( 200 == response.getStatusCode() ) {
                  AutoBean<IDatabaseConnectionList> bean =
                    AutoBeanCodex.decode( connectionAutoBeanFactory, IDatabaseConnectionList.class, response
                      .getText() );
                  List<IDatabaseConnection> connectionBeanList = bean.as().getDatabaseConnections();
                  Collections.sort( connectionBeanList, new Comparator<IDatabaseConnection>() {
                    public int compare( IDatabaseConnection a, IDatabaseConnection b ) {
                      return a.getName().compareToIgnoreCase( b.getName() );
                    }

                    public boolean equals( Object o ) {
                      return o == this;
                    }
                  } );
                  for ( IDatabaseConnection connectionBean : connectionBeanList ) {
                    addMenuListItem( sqlDatasourceList, connectionBean.getName() );
                  }
                  ( (GwtMenuList) sqlDatasourceList ).layout();
                  populatedSqlDatasources = true;

                  updateSqlDatasource( model );
                }
              } catch ( Exception ignored ) {
                // bury exception
                ignored.printStackTrace();
              }
              applyModelAsync();
            }
          } );
        } catch ( RequestException ignored ) {
          // Couldn't connect to server
          ignored.printStackTrace();
          applyModelAsync();
        }
      } else {
        applyModelAsync();
      }
    } catch ( Exception ignored ) {
      // bury exception
      ignored.printStackTrace();
    }
  }

  /**
   * model -> UI
   */
  public void applyModel( EditFilterModel model ) {
    this.model = model;
    if ( sqlSupport.hasSqlFilterPerms() ) {
      populateSqlDatasources();
    } else {
      applyModelAsync();
    }
  }

  /**
   * model -> UI
   */
  public void applyModelAsync() {
    // initialize state
    displayNameText.setValue( unescapeHtmlEntities( model.getDisplayName() ) );
    displayNameChecked.setChecked( model.isDisplayNameChecked() );

    List<XulMenuitem> listItems =
      (List<XulMenuitem>) (List) filterTypeList.getFirstChild().getElementsByTagName( "menuitem" );

    for ( int i = 0; i < listItems.size(); i++ ) {
      String itemText = (String) ( (XulMenuitem) listItems.get( i ) ).getAttributeValue( "value" );
      if ( itemText.equals( model.getSelectedFilterType() ) ) {
        filterTypeList.setSelectedIndex( i );
        break;
      }
    }

    filterTypeDeck.setSelectedIndex( model.getSelectedFilterTypeIndex() );

    // populate static
    populateSelectionTable();

    // populate sql
    sqlQueryText.setValue( model.getSpecificDataModel( SqlFilterDataModel.class ).getQuery() );
    String dataSource = model.getSpecificDataModel( SqlFilterDataModel.class ).getDatasource();
    setMenuListSelectedItem( sqlDatasourceList, dataSource );
    extractSqlColumnNames( sqlQueryText.getValue(), dataSource, true );

    this.setUseFirstValue( model.isUseFirstValue() );

    // populate mql
    applyMqlModel( model );

    // populate filter component properties
    applyPropertiesModel( model );

    // set filter list for sql/mql
    populateFilterList();

    // PDB-1831 and PIR-1027 - allow to change value on ADD, but not on EDIT
    for ( XulMenuList<XulMenuitem> list : valueColumnLists ) {
      list.setDisabled( model.getEditType() == EditFilterModel.EditType.EDIT );
    }

    editFilterDialog.show();

    bf.setBindingType( Binding.Type.BI_DIRECTIONAL );
    bf.createBinding( model, "verticalOrientation", this, "verticalOrientation" );
    setVerticalOrientation( model.isVerticalOrientation() );
    // force an update
    firePropertyChange( "verticalOrientation", !this.verticalOrientation, this.verticalOrientation );

    updateAcceptButtonStatus();
  }

  @Bindable
  private boolean isVerticalOrientation() {
    return verticalOrientation;
  }

  @Bindable
  public void setVerticalOrientation( boolean vert ) {

    boolean prevVal = this.verticalOrientation;
    this.verticalOrientation = vert;
    firePropertyChange( "verticalOrientation", prevVal, vert );

  }

  private void populateFilterList() {
    switch ( model.getSelectedPovDataType() ) {
      case SQL:
      case MQL:
        QueryFilterDataModel queryModel = (QueryFilterDataModel) model.getSelectedFilterDataModel();
        dlgFilterParameterAssignmentTable.setElements( queryModel.getQueryParameters() );
        sqlParameterDefaults.setElements( queryModel.getQueryParameters() );
        break;
      case STATIC:
        break;
    }
  }

  public void applySqlQuery( String query ) {
    String dataSource = getMenuListSelectedItem( sqlDatasourceList );
    extractSqlColumnNames( query, dataSource, true );
    setSqlQueryApplied( true );
  }

  private void selectLabelValueColumns( QueryFilterDataModel queryModel ) {
    int valueIdx = queryModel.getValueIndex();
    for ( XulMenuList<XulMenuitem> list : valueColumnLists ) {
      if ( list != null ) {
        int listSize = getMenuListSize( list );
        if ( listSize > valueIdx ) {
          setMenuListSelectedIndex( list, valueIdx );
        } else {
          queryModel.setValueIndex( listSize - 1 );
        }
      }
    }

    int labelIdx = queryModel.getLabelIndex();
    for ( XulMenuList<XulMenuitem> list : labelColumnLists ) {
      if ( list != null ) {
        int listSize = getMenuListSize( list );
        if ( listSize > labelIdx ) {
          setMenuListSelectedIndex( list, labelIdx );
        } else {
          queryModel.setLabelIndex( listSize - 1 );
        }
      }
    }

  }

  /**
   * Selects what type of component the filter is.
   */
  @Bindable
  public void setPovWidgetType( String compTypeStr ) throws IllegalArgumentException, NullPointerException {
    PovComponentType compType = Enum.valueOf( PovComponentType.class, compTypeStr );
    setPovWidgetType( compType );
  }

  private void setPovWidgetType( PovComponentType compType ) {

    model.setComponentType( compType );
    filterPropertiesDeck.setSelectedIndex( compType.getIndex() );

    // model -> UI
    applyPropertiesModel( model );
    // disable data options group for these types:
    switch ( compType ) {
      case DATE:
        dateRestrictionSelected(); // fall ok
      case TEXT:
        setDataTypeOptionsEnabled( false );
        setLabelValueOptionsVisible( true );
        hideSelectFirst( true );
        specifyRadio.setSelected( true );
        break;
      default:
        setDataTypeOptionsEnabled( true );
        applyFilterSelections();
        hideSelectFirst( false );
        break;
    }
    setComponentButtonsDisabledStatus();
    fireDialogValidCheck(); // needed when (date|text) <--> ~(date|text)
  }

  private void hideSelectFirst( boolean flag ) {
    useFirstRadio.setDisabled( flag );
  }

  private void setComponentButtonsDisabledStatus() {

    dropDownFilterButton.setDisabled( false );
    listFilterButton.setDisabled( false );
    radioFilterButton.setDisabled( false );
    checkboxFilterButton.setDisabled( false );
    buttonFilterButton.setDisabled( false );
    textfieldFilterButton.setDisabled( false );
    datepickerFilterButton.setDisabled( false );

    switch ( model.getSelectedComponentType() ) {
      case DROP_DOWN:
        dropDownFilterButton.setDisabled( true );
        break;
      case LIST:
        listFilterButton.setDisabled( true );
        break;
      case RADIO:
        radioFilterButton.setDisabled( true );
        break;
      case CHECK:
        checkboxFilterButton.setDisabled( true );
        break;
      case BUTTON:
        buttonFilterButton.setDisabled( true );
        break;
      case TEXT:
        textfieldFilterButton.setDisabled( true );
        break;
      case DATE:
        datepickerFilterButton.setDisabled( true );
        break;
    }
  }

  /**
   * Selects type of data the filter uses.
   */
  @Bindable
  public void typeSelected() {
    if ( !initialized ) {
      return;
    }
    String item =
      filterTypeList.getFirstChild().getChildNodes().get( filterTypeList.getSelectedIndex() ).getAttributeValue(
        "value" );
    PovDataType filterType = PovDataType.parseFromTypeString( item );
    model.setSelectedFilterType( filterType );
    applyFilterSelections();
  }

  private void applyFilterSelections() {
    if ( !initialized ) {
      return;
    }

    PovDataType filterType = model.getSelectedPovDataType();
    filterTypeDeck.setSelectedIndex( model.getSelectedFilterTypeIndex() );

    switch ( filterType ) {
      case MQL:
        applyMqlModel( model );
        setLabelValueOptionsVisible( true );
        break;
      case SQL:
        setLabelValueOptionsVisible( true );
        break;
      case STATIC:
        setLabelValueOptionsVisible( false );
        break;
    }
    setLabelValueOptionsEnabled();
  }

  private String getMenuListSelectedItem( XulMenuList<XulMenuitem> menuList ) {
    return menuList.getFirstChild().getChildNodes().get( menuList.getSelectedIndex() )
      .getAttributeValue( "value" ); //$NON-NLS-1$
  }

  private int getMenuListSize( XulMenuList list ) {
    return list.getFirstChild().getChildNodes().size();
  }

  private void setMenuListSelectedItem( XulMenuList<XulMenuitem> menuList, String itemValue ) {
    List<XulMenuitem> listItems =
      (List<XulMenuitem>) (List) menuList.getFirstChild().getElementsByTagName( "menuitem" ); //$NON-NLS-1$
    for ( int i = 0; i < listItems.size(); i++ ) {
      String itemText = (String) ( (XulMenuitem) listItems.get( i ) ).getAttributeValue( "value" ); //$NON-NLS-1$
      if ( itemText.equals( itemValue ) ) {
        sqlDatasourceList.setSelectedIndex( i );
        break;
      }
    }
  }

  private void setMenuListSelectedIndex( XulMenuList<XulMenuitem> menuList, int idx ) {
    menuList.setSelectedIndex( idx );
  }

  private void clearMenuList( XulMenuList<XulMenuitem> menuList ) {
    List<XulComponent> children = menuList.getFirstChild().getChildNodes();
    for ( XulComponent child : children ) {
      menuList.getFirstChild().removeChild( child );
    }
    ( (GwtMenuList) menuList ).layout();
  }

  private void addMenuListItem( XulMenuList<XulMenuitem> menuList, String item ) {
    try {
      XulMenuitem newItem = (XulMenuitem) document.createElement( "menuitem" ); //$NON-NLS-1$
      newItem.setLabel( item );
      newItem.setAttribute( "value", item ); //$NON-NLS-1$
      menuList.getFirstChild().addChild( newItem );

      ( (GwtMenuList) menuList ).layout();
    } catch ( XulException e ) {
      e.printStackTrace();
    }
  }

  @Bindable
  public void dateRestrictionSelected() {
    if ( !initialized ) {
      return;
    }
    String item = getMenuListSelectedItem( editFilterDateRestriction ); // ((XulListitem)
    // editFilterDateRestriction.getSelectedItem()).getValue();
    CalendarRestriction restriction = Enum.valueOf( CalendarRestriction.class, item );

    // activate interface
    switch ( restriction ) {
      case NONE:
      case FUTURE_ONLY:
      case PAST_ONLY:
        editFilterPropDateFrom.setDisabled( true );
        editFilterPropDateTo.setDisabled( true );
        break;
      case RANGE:
        editFilterPropDateFrom.setDisabled( false );
        editFilterPropDateTo.setDisabled( false );
        break;
    }

    DateComponentModel dateModel = model.getSpecificComponentModel( DateComponentModel.class );
    dateModel.setRestriction( restriction );
  }

  private void setDataTypeOptionsEnabled( boolean enabled ) {
    if ( enabled ) {
      filterDataDeck.setSelectedIndex( FILTER_DATA_ENABLED );
    } else {
      filterDataDeck.setSelectedIndex( FILTER_DATA_DISABLED );
    }
  }

  private void setLabelValueOptionsVisible( boolean visible ) {
    specifyWidgetDeck.setSelectedIndex( ( visible ) ? 0 : 1 );
    for ( XulComponent optionsPanel : optionsPanels ) {
      optionsPanel.setVisible( visible );
    }
  }

  private void setLabelValueOptionsEnabled() {
    boolean enabled = false;
    if ( model.getSelectedComponentModel().hasDataModel() ) {
      SqlFilterDataModel sqlModel = model.getSpecificDataModel( SqlFilterDataModel.class );
      switch ( model.getSelectedPovDataType() ) {
        case SQL:
          // is SqlQuery, query exists and has been applied
          enabled = sqlModel.isQueryApplied();
          if ( enabled ) {
            loadColumnLists( sqlModel );
          }
          break;
        case MQL:
          // is MqlQuery, query exists and has columns
          enabled = mqlFilterSelectedColumns.getRowCount() > 0;
          // sqlModel.setQueryApplied(false);
          break;
        case STATIC:
          break;
      }
    }
    setLabelValueOptionsEnabled( enabled );
  }

  private void setLabelValueOptionsEnabled( boolean enabled ) {
    updateSelectedIndexes = false; // changing enabled will trigger idx update
    for ( XulMenuList<XulMenuitem> mlist : columnLists ) {
      setMenuListEnabled( mlist, enabled );
    }
    updateSelectedIndexes = true;
  }

  private void setMenuListEnabled( XulMenuList<XulMenuitem> mlist, boolean enabled ) {
    mlist.setAttribute( "disabled", enabled ? "false" : "true" );
    ( (GwtMenuList<XulMenuitem>) mlist ).layout();
  }

  private String getStaticListInitialValue() {
    return specifyList.getSelectedItem();
  }

  public void populateSelectionTable() {
    this.updateSelectedIndexes = false;
    // clear table
    staticSelectionTable.getRootChildren().removeAll();
    // clear menulists
    specifyList.setElements( Collections.emptyList() );

    // populate from scratch
    try {
      List<String> statics = new ArrayList<String>();
      for ( EditFilterModel.NameValuePair nvp : ( (EditFilterModel.StaticFilterDataModel) model.dataModels
        .get( 0 ) ).selections ) {
        XulTreeRow row = (XulTreeRow) document.createElement( "treerow" ); //$NON-NLS-1$
        row.addCellText( 0, escapeHtmlEntities( nvp.name ) );
        row.addCellText( 1, escapeHtmlEntities( nvp.value ) );
        staticSelectionTable.addTreeRow( row );
        // clear listboxes
        statics.add( unescapeHtmlEntities( nvp.value ) );
      }
      specifyList.setElements( statics );
    } catch ( XulException e ) {
      e.printStackTrace();
    }

    updateAcceptButtonStatus();
    this.updateSelectedIndexes = true;
  }

  /**
   * Show or hide 'accept' button based on the state of the dialog model
   */
  private boolean updateAcceptButtonStatus() {
    fireDialogValidCheck();
    return isDialogValid();
  }

  public String getName() {
    return "editFilterController"; //$NON-NLS-1$
  }

  @Bindable
  public void addSelection() {
    addSelectionLabelText.setValue( "" ); //$NON-NLS-1$
    addSelectionValueText.setValue( "" ); //$NON-NLS-1$
    addSelectionDialog.show();
    addSelectionLabelText.setFocus();
  }

  @Bindable
  public void editSelection() {
    int[] rows = staticSelectionTable.getSelectedRows();
    if ( rows.length > 0 ) {
      EditFilterModel.NameValuePair nvp =
        ( (EditFilterModel.StaticFilterDataModel) model.dataModels.get( 0 ) ).selections.get( rows[ 0 ] );
      editSelectionLabelText.setValue( unescapeHtmlEntities( nvp.name ) );
      editSelectionValueText.setValue( unescapeHtmlEntities( nvp.value ) );
      editSelectionDialog.show();
      editSelectionLabelText.setFocus();
    }
  }

  @Bindable
  public void editSelectionDialogSave() {
    int[] rows = staticSelectionTable.getSelectedRows();
    if ( rows.length > 0 ) {
      EditFilterModel.NameValuePair nvp =
        ( (EditFilterModel.StaticFilterDataModel) model.dataModels.get( 0 ) ).selections.get( rows[ 0 ] );
      nvp.name = editSelectionLabelText.getValue();
      nvp.value = editSelectionValueText.getValue();
      populateSelectionTable();
      staticSelectionTable.setSelectedRows( new int[] { rows[ 0 ] } );
    }
    editSelectionDialog.hide();
  }

  @Bindable
  public void editSelectionDialogCancel() {
    editSelectionDialog.hide();
  }

  @Bindable
  public void removeSelection() {
    // sort the rows high to low
    int[] rows = staticSelectionTable.getSelectedRows();
    ArrayList<Integer> rowArray = new ArrayList<Integer>();
    for ( int i = 0; i < rows.length; i++ ) {
      rowArray.add( rows[ i ] );
    }
    Collections.sort( rowArray, Collections.reverseOrder() );
    List<EditFilterModel.NameValuePair> list =
      ( (EditFilterModel.StaticFilterDataModel) model.dataModels.get( 0 ) ).selections;
    for ( int i : rowArray ) {
      list.remove( i );
    }

    populateSelectionTable();
  }

  @Bindable
  public void moveUpSelection() {
    int[] rows = staticSelectionTable.getSelectedRows();
    if ( rows.length > 0 ) {
      if ( rows[ 0 ] != 0 ) {
        List<EditFilterModel.NameValuePair> list =
          ( (EditFilterModel.StaticFilterDataModel) model.dataModels.get( 0 ) ).selections;
        Collections.swap( list, rows[ 0 ], rows[ 0 ] - 1 );
        populateSelectionTable();
        staticSelectionTable.setSelectedRows( new int[] { rows[ 0 ] - 1 } );
      }
    }
  }

  @Bindable
  public void moveDownSelection() {
    int[] rows = staticSelectionTable.getSelectedRows();
    if ( rows.length > 0 ) {
      if ( rows[ 0 ] != staticSelectionTable.getRows() - 1 ) {
        List<EditFilterModel.NameValuePair> list =
          ( (EditFilterModel.StaticFilterDataModel) model.dataModels.get( 0 ) ).selections;
        Collections.swap( list, rows[ 0 ], rows[ 0 ] + 1 );
        populateSelectionTable();
        staticSelectionTable.setSelectedRows( new int[] { rows[ 0 ] + 1 } );

      }
    }
  }

  @Bindable
  public void addSelectionDialogAdd() {
    // store info
    String label = addSelectionLabelText.getValue();
    String value = addSelectionValueText.getValue();

    if ( !label.equals( "" ) && !value.equals( "" ) ) { //$NON-NLS-1$ //$NON-NLS-2$
      EditFilterModel.NameValuePair nvp = new EditFilterModel.NameValuePair( label, value );
      ( (EditFilterModel.StaticFilterDataModel) model.dataModels.get( 0 ) ).selections.add( nvp );

      populateSelectionTable();
      addSelectionLabelText.setValue( "" ); //$NON-NLS-1$
      addSelectionValueText.setValue( "" ); //$NON-NLS-1$

      addSelectionLabelText.setFocus();
    }
  }

  public static String escapeHtmlEntities( String text ) {
    return text.replace( "&", "&amp;" ).replace( "\"", "&quot;" ).replace( "'", "&apos;" ).replace( "<", "&lt;" )
      .replace( ">", "&gt;" );
  }

  public static String unescapeHtmlEntities( String text ) {
    return text.replace( "&amp;", "&" ).replace( "&quot;", "\"" ).replace( "&apos;", "'" ).replace( "&lt;", "<" )
      .replace( "&gt;", ">" );
  }

  @Bindable
  public void addSelectionDialogClose() {
    addSelectionDialog.hide();
  }

  @Bindable
  /**
   * generate filter widget to dashboard
   * final UI -> model
   */
  public void save() {
    if ( this.isDialogValid() == false ) {
      return;
    }
    String displayName = cleanseDisplayName( displayNameText.getValue() );
    model.setDisplayName( escapeHtmlEntities( displayName ) );
    model.setDisplayNameChecked( displayNameChecked.isChecked() );

    switch ( model.getSelectedPovDataType() ) {
      case SQL:
        QueryFilterDataModel queryModel = (QueryFilterDataModel) model.getSelectedFilterDataModel();
        queryModel.setQueryParameters( getInputParametersInUI() );
        break;
      case STATIC:
        // setStaticListInitialValue();
        break;
      case MQL:
        break;
    }

    try {
      // populate sql
      String dataSource = getMenuListSelectedItem( sqlDatasourceList );
      model.getSpecificDataModel( SqlFilterDataModel.class ).setDatasource( dataSource );
    } catch ( Exception ignored ) {
      // ignore any xul errors
    }

    model.getSpecificDataModel( EditFilterModel.SqlFilterDataModel.class ).setQuery( sqlQueryText.getValue() );

    JsniFilterPresaveResult presaveResult = null;
    Iterator<FilterDialogCallback> iter = callbacks.iterator();
    while ( presaveResult == null && iter.hasNext() ) {
      presaveResult = iter.next().preSave( model );
    }
    if ( presaveResult != null ) {
      showErrorDialog( presaveResult.getTitle(), presaveResult.getMessage() );
    } else {
      for ( FilterDialogCallback callback : callbacks ) {
        callback.onSave( model );
      }

      editFilterDialog.hide();
    }
  }

  protected String cleanseDisplayName( final String dName ) {
    String cleansed = "";
    cleansed = dName.replace( "\"", "" );
    cleansed = cleansed.replace( "'", "" );
    cleansed = cleansed.replace( "/", "" );
    cleansed = cleansed.replace( ";", "-" );
    cleansed = cleansed.replace( "|", "-" );
    cleansed = cleansed.replace( ">", "" );
    cleansed = cleansed.replace( "<", "" );
    cleansed = cleansed.replace( ",", " " );
    cleansed = cleansed.replace( "&", "" );
    cleansed = cleansed.replace( "$", "" );
    return cleansed;
  }

  private void showErrorDialog( final String title, final String message ) {
    errorDialog.setTitle( title );
    errorDialogMessage.setValue( message );
    errorDialog.show();
  }

  @Bindable
  public void okErrorDialog() {
    errorDialog.hide();
  }

  private List<PropertiesPanelParameterAssignment> getInputParametersInUI() {
    List<PropertiesPanelParameterAssignment> params =
      (List<PropertiesPanelParameterAssignment>) dlgFilterParameterAssignmentTable
        .<PropertiesPanelParameterAssignment>getElements();
    // on some ocassions (no other selectors is one of them), the static value bind will fail; this is an ugly
    // workaround
    if ( params != null ) {
      for ( PropertiesPanelParameterAssignment param : params ) {
        if ( StringUtils.isEmpty( param.get_value_() ) ) {
          // fetch the actual value through jquery
          String actualValue = getParametersSelectedValueThroughDom();
          if ( actualValue != null && StringUtils.isEmpty( actualValue ) ) {
            param.set_value_( actualValue );
          }
          break;
        }
      }
    }
    return params;
  }

  // gets the selected value in the parameters table even when it doesn't appear in _value_
  private native String getParametersSelectedValueThroughDom()/*-{
    var inputBox = $wnd.$('#dlgFilterParameterAssignmentTable tr.selected input.gwt-TextBox');
    if (inputBox.length > 0) return inputBox[0].value;
  }-*/;

  @Bindable
  public void cancel() {
    for ( FilterDialogCallback c : callbacks ) {
      c.onCancel( model );
    }
    editFilterDialog.hide();
  }

  public void showFilterDialog( EditFilterModel model ) {
    configure();
    applyModel( model );
    staticSelectionTable.update();
    updateSqlDatasource( model );

    // set defaults
    setPovWidgetType( model.getSelectedComponentType() );
  }

  private void updateSqlDatasource( EditFilterModel model ) {
    // If this is a SQL filter, set the datasource
    if ( model.getSelectedFilterType().equals( "sql" ) ) { //$NON-NLS-1$
      String dataSource = model.getSpecificDataModel( SqlFilterDataModel.class ).getDatasource();
      if ( sqlDatasourceList != null ) {
        if ( sqlDatasourceList.getChildNodes() != null ) {
          List<XulMenuitem> items =
            (List<XulMenuitem>) (List) sqlDatasourceList.getFirstChild()
              .getElementsByTagName( "menuitem" ); //$NON-NLS-1$
          if ( items != null ) {
            for ( int i = 0; i < items.size(); i++ ) {
              String itemText = (String) ( (XulMenuitem) items.get( i ) ).getAttributeValue( "value" ); //$NON-NLS-1$
              if ( itemText.equals( dataSource ) ) {
                sqlDatasourceList.setSelectedIndex( i );
              }
            }
          }
        }
      }
    }
  }

  private void extractSqlColumnNames( String query, String datasource, final boolean loadColumns ) {
    RequestCallback callback = new RequestCallback() {
      public void onError( Request request, Throwable exception ) {
        // Couldn't connect to server (could be timeout, SOP violation, etc.)
        waitingDialog.hide();
      }

      public void onResponseReceived( Request request, Response response ) {
        waitingDialog.hide();
        extractSqlColumnNamesSuccessHandler( response, loadColumns );
      }
    };

    extractSqlColumnNamesRequest( query, datasource, callback );
  }

  private void extractSqlColumnNamesSuccessHandler( Response response, boolean loadColumns ) {
    if ( 200 == response.getStatusCode() ) {
      JavaScriptObject obj = sqlSupport.parseTestResults( response.getText(), true );
      clearColumnLists();
      // populate columnLists //TODO: refactor, integrate with test?..
      SqlFilterDataModel sqlModel = getSqlFilterModel( model );

      int len = sqlSupport.getLength( obj );
      for ( int i = 0; i < len; i++ ) {
        for ( int j = 0; j < sqlSupport.getRowLength( obj, i ); j++ ) {
          String column = sqlSupport.getValue( obj, i, j );
          addToColumnList( sqlModel, column );
        }
      }
      if ( loadColumns ) {
        loadColumnLists( sqlModel ); // add to interface
      }
      selectLabelValueColumns( sqlModel );

      if ( showErrorQueryDialog ) {
        if ( sqlModel.getColumnList().size() == 0 ) {
          sqlErrorQueryDialog.show();
        }
      }
      showErrorQueryDialog = false;
    }
  }

  private void extractSqlColumnNamesRequest( String query, String datasource, RequestCallback callback ) {
    if ( query == null || query.trim().equals( "" ) || datasource == null ) {
      return;
    }

    waitingDialog.show();

    String url = getFilterSqlQueryUrl( datasource, "list", query, 0, 0, true );
    RequestBuilder builder = new RequestBuilder( RequestBuilder.GET, url );

    builder.setHeader( "Content-Type", "application/xml" ); //$NON-NLS-1$ //$NON-NLS-2$
    try {

      builder.sendRequest( "", callback );
    } catch ( RequestException e ) {
      e.printStackTrace();
    }
  }

  private String getFilterSqlQueryUrl( String datasource, String command, String query, int labelIndex, int valueIndex,
                                       boolean headersOnly ) {

    StringBuilder urlBuild = new StringBuilder();
    urlBuild.append( "../../../api/repos/dashboards/editor?command=" + command + "&datasource=" ); //$NON-NLS-1$
    urlBuild.append( URL.encodeComponent( datasource ) );
    urlBuild.append( "&query=" ); //$NON-NLS-1$
    urlBuild.append( URL.encodeComponent( query ) );
    urlBuild.append( "&headersOnly=" ); //$NON-NLS-1$
    urlBuild.append( headersOnly ? "true" : "false" ); //$NON-NLS-1$ //$NON-NLS-2$
    urlBuild.append( "&lblidx=" ); //$NON-NLS-1$
    urlBuild.append( labelIndex );
    urlBuild.append( "&validx=" ); //$NON-NLS-1$
    urlBuild.append( valueIndex );

    List<PropertiesPanelParameterAssignment> params = getInputParametersInUI();
    if ( params != null ) {
      for ( PropertiesPanelParameterAssignment param : params ) {
        urlBuild.append( "&$" ); //$NON-NLS-1$
        urlBuild.append( URL.encodeComponent( param.getName() ) );
        urlBuild.append( "=" ); //$NON-NLS-1$
        if ( param.getSource() == PropertiesPanelParameterAssignment.Source.STATIC ) {
          urlBuild.append( URL.encodeComponent( param.get_value_() ) );
        } else {
          urlBuild.append( URL.encodeComponent( sqlSupport.getParameterDefault( param.getSelectedFilterId() ) ) );
        }
      }
    }

    return urlBuild.toString();
  }

  @Bindable
  public void displayQueryParameters() {
    if ( model.getSelectedPovDataType().equals( PovDataType.SQL )
      && !model.getSpecificDataModel( SqlFilterDataModel.class ).isQueryApplied() ) {
      // force apply of sql query if not done already
      applySqlQuery( sqlQueryText.getValue() );
    }
    if ( dlgFilterParameterAssignmentTable.getElements().size() > 0 ) {
      parametersDialog.show();
      dlgFilterParameterAssignmentTable.update();
    }
  }

  @Bindable
  public void okQueryDialog() {
    if ( model.getSelectedPovDataType().equals( PovDataType.SQL ) ) {
      String query = paramsSqlQueryText.getValue();
      sqlQueryText.setValue( query );
      applySqlQuery( query );
      showErrorQueryDialog = true;

      QueryFilterDataModel queryModel = (QueryFilterDataModel) model.getSelectedFilterDataModel();
      queryModel.setQueryParameters( getInputParametersInUI() );
    }
    closeQueryDialog();
  }

  @Bindable
  public void okErrorQueryDialog() {
    closeErrorQueryDialog();
    queryDialog.show();
  }

  @Bindable
  public void closeErrorQueryDialog() {
    sqlErrorQueryDialog.hide();
    showErrorQueryDialog = false;
  }

  @Bindable
  public void displayQueryDialog() {
    String query = sqlQueryText.getValue();
    paramsSqlQueryText.setValue( query );
    populateFilterList();
    queryDialog.show();
  }

  @Bindable
  public void closeQueryDialog() {
    queryDialog.hide();
  }

  @Bindable
  public void cancelParametersDialog() {
    parametersDialog.hide();
  }

  @Bindable
  public void closeParametersDialog() {
    QueryFilterDataModel queryModel = (QueryFilterDataModel) model.getSelectedFilterDataModel();
    queryModel.setQueryParameters( getInputParametersInUI() );
    parametersDialog.hide();
  }

  @Bindable
  public void displayTestResults() {

    if ( sqlDatasourceList.getSelectedIndex() < 0 ) {
      if ( getMenuListSize( sqlDatasourceList ) > 0 ) {
        setMenuListSelectedIndex( sqlDatasourceList, 0 );
      } else {
        return;
      }
    }

    String datasource = getMenuListSelectedItem( sqlDatasourceList );
    String query = paramsSqlQueryText.getValue();

    SqlFilterDataModel qModel = (SqlFilterDataModel) model.getSelectedFilterDataModel();
    final String url =
      getFilterSqlQueryUrl( datasource, "executeQuery", query, qModel.getLabelIndex(), qModel.getValueIndex(),
        false );

    waitingDialog.show();

    RequestCallback callback = new RequestCallback() {
      public void onError( Request request, Throwable exception ) {
        // Couldn't connect to server (could be timeout, SOP violation, etc.)
        waitingDialog.hide();
      }

      public void onResponseReceived( Request request, Response response ) {
        extractSqlColumnNamesSuccessHandler( response, false );

        RequestBuilder builder = new RequestBuilder( RequestBuilder.GET, url );
        builder.setHeader( "Content-Type", "application/xml" ); //$NON-NLS-1$ //$NON-NLS-2$
        try {
          builder.sendRequest( "", new RequestCallback() { //$NON-NLS-1$
            public void onError( Request request, Throwable exception ) {
              // Couldn't connect to server (could be timeout, SOP violation, etc.)
              waitingDialog.hide();
            }

            public void onResponseReceived( Request request, Response response ) {
              waitingDialog.hide();
              if ( 200 == response.getStatusCode() ) {
                JavaScriptObject obj = sqlSupport.parseTestResults( response.getText(), false );
                testResultsTable.suppressLayout( true );
                testResultsTable.getRootChildren().removeAll();
                // populate from scratch
                try {
                  int len = sqlSupport.getLength( obj );
                  len = ( len > 10 ) ? 10 : len; // limits to 10 records

                  SqlFilterDataModel sqlModel = getSqlFilterModel( model );
                  List<String> columns = sqlModel.getColumnList();
                  // Remove all the existing columns
                  testResultsTable.getColumns().getChildNodes().clear();

                  // Recreate the columns
                  XulTreeCols treeCols = testResultsTable.getColumns();
                  // Setting column data
                  for ( String columnName : columns ) {
                    try {
                      XulTreeCol treeCol = (XulTreeCol) document.createElement( "treecol" );
                      treeCol.setWidth( 75 );
                      treeCol.setFlex( 1 );
                      treeCol.setLabel( columnName );
                      treeCols.addColumn( treeCol );
                    } catch ( XulException e ) {
                      e.printStackTrace();
                    }
                  }
                  testResultsTable.setColumns( treeCols );

                  for ( int i = 0; i < len; i++ ) {
                    XulTreeRow row = (XulTreeRow) document.createElement( "treerow" ); //$NON-NLS-1$
                    for ( int j = 0; j < columns.size(); j++ ) {
                      XulTreeCell cell = (XulTreeCell) document.createElement( "treecell" ); //$NON-NLS-1$
                      cell.setLabel( escapeHtmlEntities( sqlSupport.getValue( obj, i, j ) ) );
                      row.addCell( cell );
                    }
                    testResultsTable.addTreeRow( row );
                  }
                } catch ( XulException e ) {
                  e.printStackTrace();
                }
                testResultsTable.suppressLayout( false );
                testResultsTable.update();
                testResultsDialog.show();
              }
            }
          } );
        } catch ( RequestException e ) {
          e.printStackTrace();
        }
      }
    };

    extractSqlColumnNamesRequest( query, datasource, callback );
  }

  @Bindable
  public void closeTestResultsDialog() {
    testResultsDialog.hide();
  }

  public void setBindingFactory( BindingFactory bf ) {
    this.bf = bf;
  }

  @Bindable
  public void setFocusAddLabel() {
    addSelectionLabelText.setFocus();
  }

  @Bindable
  public void setFocusAddValue() {
    addSelectionValueText.setFocus();
  }

  @Bindable
  public void setFocusEditLabel() {
    editSelectionLabelText.setFocus();
  }

  @Bindable
  public void setFocusEditValue() {
    editSelectionValueText.setFocus();
  }

  @Bindable
  public native void selectMqlDataSource()/*-{
    $wnd.pho.dashboards.enableWaitCursor(true);
    $wnd.parent.pho.showDatasourceSelectionDialog("dashboard-filters",
        new $wnd.MqlFilterHelper.SelectDataSourceCallback());
  }-*/;

  @Bindable
  public void editMqlQuery() {
    MqlFilterDataModel mqlFilterModel = getMqlFilterModel( model );
    if ( mqlFilterModel != null && mqlFilterModel.getJsMql() != null ) {
      callEditMqlQuery( mqlFilterModel.getJsMql() );
    }
  }

  private native void callEditMqlQuery( String mqlQueryModel )/*-{
    $wnd.pho.dashboards.enableWaitCursor(true);
    $wnd.parent.pho.showMqlEditorDialogWithQueryModel(mqlQueryModel, new $wnd.MqlFilterHelper.MqlEditorCallback());
  }-*/;

  /**
   * Define JS workflow for setting MQL query in filter.
   */
  private static native void defineMqlFilterCallbacks( EditFilterController efc )/*-{
    $wnd.MqlFilterHelper = {};

    $wnd.MqlFilterHelper.SelectDataSourceCallback = function () {
      this.onFinish = function (domainId, modelId, modelName) {
        //set the label
        // CHECKSTYLE IGNORE LineLength FOR NEXT 1 LINES
        efc.@org.pentaho.mantle.client.dialogs.filterdialog.client.EditFilterController::setMqlDatasource(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(domainId, modelId, modelName);
        //call mql editor
        $wnd.parent.pho.showMqlEditorDialog(domainId, modelId, new $wnd.MqlFilterHelper.MqlEditorCallback());
       }

      this.onCancel = function () {
        //do nothing
      }

      this.onReady = function () {
        $wnd.pho.dashboards.enableWaitCursor(false);
      }
    }

    $wnd.MqlFilterHelper.MqlEditorCallback = function () {
      this.onFinish = function (mqlQuery) {
        //put selected columns in listbox and save query xml
        efc.@org.pentaho.mantle.client.dialogs.filterdialog.client.EditFilterController::setMqlQuery(Ljava/lang/String;)(mqlQuery);
      }

      this.onCancel = function () {
        //do nothing
      }

      this.onReady = function () {
        $wnd.pho.dashboards.enableWaitCursor(false);
      }
    }
  }-*/;

  // jsni defineMqlFilterCallbacks
  private void setMqlQuery( String jsMqlQuery ) {
    // deserialize js object
    JSONObject mqlQueryJSObj = JSONParser.parse( jsMqlQuery ).isObject();
    // display columns in list
    populateMqlFilterSelectedColumns( mqlQueryJSObj );
    // get parameters
    setMqlFilterInputParameters( mqlQueryJSObj );

    // extract xml
    String mqlAsXml = getXmlMql( mqlQueryJSObj );
    MqlFilterDataModel mqlFilterModel = getMqlFilterModel( model );
    if ( mqlFilterModel != null ) {
      mqlFilterModel.setQuery( mqlAsXml );
      // save json object
      mqlFilterModel.setJsMql( jsMqlQuery );
    }
    //
    updateAcceptButtonStatus();
  }

  protected List<JSONObject> getMqlQueryColumns( JSONObject mqlQuery ) {

    List<JSONObject> mqlColumns = new ArrayList<JSONObject>();

    JSONObject mqlQueryObj = mqlQuery.get( "MQLQuery" ).isObject();
    JSONObject colsObj = mqlQueryObj.get( "cols" ).isObject();

    // is this the old MqlQuery JSON format?
    if ( colsObj != null ) {
      // ['MQLQuery']['cols']['org.pentaho.commons.metadata.mqleditor.beans.Column'][i]['name']
      JSONValue columnsValue = colsObj.get( "org.pentaho.commons.metadata.mqleditor.beans.Column" ); //$NON-NLS-1$
      JSONArray columns = columnsValue.isArray();

      if ( columns != null ) {
        for ( int i = 0; i < columns.size(); i++ ) {
          mqlColumns.add( columns.get( i ).isObject() );
        }
      }

    } else {
      // is it the new format?
      // ['MQLQuery']['cols'][i]['org.pentaho.commons.metadata.mqleditor.beans.Column']['name']
      JSONArray colsArray = mqlQueryObj.get( "cols" ).isArray();
      if ( colsArray != null ) {
        for ( int i = 0; i < colsArray.size(); i++ ) {
          JSONObject colBean = colsArray.get( i ).isObject();
          JSONObject colObj = colBean.get( "org.pentaho.commons.metadata.mqleditor.beans.Column" ).isObject();
          //PDB-1693: When multiple fields are selected colObj is an array and not an object
          if ( colObj != null ) {
            mqlColumns.add( colObj );
          } else {
            JSONArray columns = colBean.get( "org.pentaho.commons.metadata.mqleditor.beans.Column" ).isArray();
            if ( columns != null ) {
              for ( int j = 0; j < columns.size(); j++ ) {
                mqlColumns.add( columns.get( j ).isObject() );
              }
            }
          }
        }
      }
    }

    return mqlColumns;
  }

  /**
   * Puts column names in UI (clears if null argument).
   *
   * @param mqlQuery
   */
  private void populateMqlFilterSelectedColumns( JSONObject mqlQuery ) {
    if ( mqlFilterSelectedColumns == null ) {
      return;
    }
    mqlFilterSelectedColumns.removeItems();
    setLabelValueOptionsEnabled( true );

    clearColumnLists();
    mqlFilterEditButton.setDisabled( true );

    MqlFilterDataModel mqlModel = getMqlFilterModel( model );

    if ( mqlQuery != null ) {
      try {
        // BISERVER-7488
        List<JSONObject> cols = getMqlQueryColumns( mqlQuery );
        if ( cols.size() > 0 ) {
          for ( JSONObject col : cols ) {
            String columnName = col.get( "name" ).isString().stringValue();
            mqlFilterSelectedColumns.addItem( columnName );
            addToColumnList( mqlModel, columnName );
          }
          loadColumnLists( mqlModel );
          selectLabelValueColumns( mqlModel );
        }

        mqlFilterEditButton.setDisabled( false );

      } catch ( Exception e ) {
        e.printStackTrace();
      }
    }
  }

  private void clearColumnLists() {
    FilterDataModel dataModel = model.getSelectedFilterDataModel();
    if ( model.getSelectedFilterDataModel() instanceof QueryFilterDataModel ) {
      ( (QueryFilterDataModel) dataModel ).clearColumnList();
    }
    this.updateSelectedIndexes = false;
    for ( XulMenuList menuList : columnLists ) {
      if ( menuList != null ) {
        clearMenuList( menuList );
      }
    }

    this.updateSelectedIndexes = true;
  }

  private void addToColumnList( QueryFilterDataModel dataModel, String columnName ) {
    dataModel.addToColumnList( columnName );
  }

  /**
   * Load query column lists (model -> view)
   *
   * @param dataModel
   */
  private void loadColumnLists( QueryFilterDataModel dataModel ) {
    if ( dataModel == null ) {
      return;
    }

    this.updateSelectedIndexes = false;
    for ( XulMenuList<XulMenuitem> menuList : columnLists ) {
      if ( menuList != null ) {
        clearMenuList( menuList );
        for ( String columnName : dataModel.getColumnList() ) {
          addMenuListItem( menuList, columnName );
        }
      }
    }

    this.updateSelectedIndexes = true;
  }

  private String getXmlMql( JSONObject mqlQuery ) {
    String xml = null;
    // ['MQLQuery']['query']
    JSONValue query = mqlQuery.get( "MQLQuery" ).isObject().get( "query" ); //$NON-NLS-1$ //$NON-NLS-2$
    xml = query.isString().stringValue();

    return xml;
  }

  private MqlFilterDataModel getMqlFilterModel( EditFilterModel model ) {
    try {
      return (MqlFilterDataModel) model.dataModels.get( EditFilterModel.PovDataType.MQL.getIndex() );
    } catch ( ClassCastException cce ) {
      cce.printStackTrace();
      return null;
    }
  }

  private SqlFilterDataModel getSqlFilterModel( EditFilterModel model ) {
    try {
      return (SqlFilterDataModel) model.dataModels.get( EditFilterModel.PovDataType.SQL.getIndex() );
    } catch ( ClassCastException cce ) {
      cce.printStackTrace();
      return null;
    }
  }

  private void applyMqlModel( EditFilterModel model ) {
    MqlFilterDataModel mqlModel = getMqlFilterModel( model );
    // data source
    mqlFilterSelectedDataSource.setValue( mqlModel.getDataSourceName() );
    // selected columns
    String jsMqlQuery = mqlModel.getJsMql();
    JSONObject mqlQueryJSObj = ( jsMqlQuery != null ) ? JSONParser.parse( jsMqlQuery ).isObject() : null;

    populateMqlFilterSelectedColumns( mqlQueryJSObj );
  }

  private void setSqlFilterInputParameters( String sqlQuery ) {

    JsArrayString params = extractParametersFromSql( sqlQuery );
    List<Filter> providers = getOutputParameters( model.getFilterInternalName() );
    List<PropertiesPanelParameterAssignment> paramAssignments = new ArrayList<PropertiesPanelParameterAssignment>();
    for ( int i = 0; i <= params.length(); i++ ) {
      String param = params.get( i );
      if ( param != null ) {
        PropertiesPanelParameterAssignment paramAss =
          new PropertiesPanelParameterAssignment( param.toString(), Source.STATIC, " ", providers );
        paramAssignments.add( paramAss );
      }
    }
    dlgFilterParameterAssignmentTable.setElements( paramAssignments );
    sqlParameterDefaults.setElements( paramAssignments );
  }

  private void setMqlFilterInputParameters( JSONObject mql ) {
    List<NameValuePair> params = extractParametersFromMql( mql );
    List<Filter> providers = getOutputParameters( model.getFilterInternalName() );
    List<PropertiesPanelParameterAssignment> paramAssignments = new ArrayList<PropertiesPanelParameterAssignment>();

    for ( NameValuePair param : params ) {
      PropertiesPanelParameterAssignment paramAss =
        new PropertiesPanelParameterAssignment( param.name, Source.STATIC, param.value, providers );
      paramAssignments.add( paramAss );
    }

    MqlFilterDataModel mqlModel = model.getSpecificDataModel( MqlFilterDataModel.class );
    mqlModel.setQueryParameters( paramAssignments );
    dlgFilterParameterAssignmentTable.setElements( paramAssignments );
  }

  protected List<JSONObject> getMqlConditions( JSONObject mqlQuery ) {

    List<JSONObject> mqlConditions = new ArrayList<JSONObject>();

    JSONObject mqlQueryObj = mqlQuery.get( "MQLQuery" ).isObject();
    JSONObject conditionsObj = mqlQueryObj.get( "conditions" ).isObject();

    // is this the old MqlQuery JSON format?
    if ( conditionsObj != null ) {
      // ['MQLQuery']['conditions']['org.pentaho.commons.metadata.mqleditor.beans.Condition'][i]['name']
      JSONValue conditionsValue =
        conditionsObj.get( "org.pentaho.commons.metadata.mqleditor.beans.Condition" ); //$NON-NLS-1$
      JSONArray conditions = conditionsValue.isArray();

      if ( conditions != null ) {
        for ( int i = 0; i < conditions.size(); i++ ) {
          mqlConditions.add( conditions.get( i ).isObject() );
        }
      }

    } else {
      // is it the new format?
      // ['MQLQuery']['conditions'][i]['org.pentaho.commons.metadata.mqleditor.beans.Condition']['name']
      JSONArray conditionsArray = mqlQueryObj.get( "conditions" ).isArray();
      if ( conditionsArray != null ) {
        for ( int i = 0; i < conditionsArray.size(); i++ ) {
          JSONObject conditionsBean = conditionsArray.get( i ).isObject();
          JSONObject conditionObj =
            conditionsBean.get( "org.pentaho.commons.metadata.mqleditor.beans.Condition" ).isObject();
          if ( conditionObj != null ) {
            mqlConditions.add( conditionObj );
          } else if ( conditionsBean.get( "org.pentaho.commons.metadata.mqleditor.beans.Condition" ).isArray() != null ) {
            JSONArray conditionArray =
              conditionsBean.get( "org.pentaho.commons.metadata.mqleditor.beans.Condition" ).isArray();
            for ( int k = 0; k < conditionArray.size(); k++ ) {
              conditionObj = conditionArray.get( k ).isObject();
              if ( conditionObj != null ) {
                mqlConditions.add( conditionObj );
              }
            }
          }
        }
      }
    }

    return mqlConditions;
  }

  private List<NameValuePair> extractParametersFromMql( JSONObject mql ) {
    List<NameValuePair> mqlParameters = new ArrayList<NameValuePair>();
    if ( mql != null ) {
      try {

        List<JSONObject> conditions = getMqlConditions( mql );

        if ( conditions.size() > 0 ) {
          for ( JSONObject cond : conditions ) {
            JSONObject c = cond.get( "condition" ).isObject();
            if ( c.containsKey( "@value" ) ) { // PDB-1243 the case where a condition does not have parameters.
              String condValue = c.get( "@value" ).isString().stringValue().trim(); //$NON-NLS-1$
              if ( condValue.startsWith( "{" ) && condValue.endsWith( "}" ) ) { //$NON-NLS-1$ //$NON-NLS-2$
                // is a parameter
                String name = condValue.substring( 1, condValue.length() - 1 );

                // PDB-1193handle the case where no default value was set
                String value = "";
                if ( c.get( "@defaultValue" ) != null ) {
                  value = c.get( "@defaultValue" ).isString().stringValue();
                }

                mqlParameters.add( new NameValuePair( name, value ) );
              }
            }
          }
        }
      } catch ( Exception e ) {
        e.printStackTrace();
      }
    }
    return mqlParameters;
  }

  public static List<Filter> getOutputParameters( String nameToIgnore ) {
    if ( configuration != null && !configuration.isParametersSupported() ) {
      return Collections.EMPTY_LIST;
    }
    String parameterNamesAsCommaSeparatedString = getParametersAsString( nameToIgnore );

    if ( parameterNamesAsCommaSeparatedString.contains( "&amp;" ) ) {
      parameterNamesAsCommaSeparatedString = parameterNamesAsCommaSeparatedString.replace( "&amp;", "&" );
    }

    List<Filter> filters = new ArrayList<Filter>();
    if ( !StringUtils.isEmpty( parameterNamesAsCommaSeparatedString ) ) {
      String[] tokens = parameterNamesAsCommaSeparatedString.split( "," ); //$NON-NLS-1$
      for ( int i = 0; i < tokens.length; i++ ) {
        String[] f = tokens[ i ].split( ";" ); //$NON-NLS-1$
        filters.add( new Filter( f[ 0 ], f[ 1 ] ) );
      }
    }
    return filters;
  }

  // get dashboard parameters (aka filters) as a comma-separated string
  // (filterId;filterName,filterId2;filterName2..)
  private static native String getParametersAsString( String nameToIgnore )/*-{
    return $wnd.PropertiesPanelHelper.getDashboardParametersString(nameToIgnore);
  }-*/;

  /**
   * Extract parameters in the form <code>${param}</code> from a SQL expression
   */
  private native JsArrayString extractParametersFromSql( String sql )/*-{
    //no pattern support in gwt, had to be done in javascript
    var paramsRegex = /[$][{]([\w _-]+)[}]/g;
    var nameRegex = /([\w _-]+)/;

    var params = sql.match(paramsRegex);
    var paramNames = [];

    if (params) for (var i = 0; i < params.length; i++) {
      paramNames.push(params[i].match(nameRegex)[0]);
    }
    return paramNames;
  }-*/;

  // ** COMPONENT PROPERTIES MODEL BINDING view->model (begin)

  @Bindable
  public void setInitialSelection( Integer index ) {
    StaticFilterDataModel staticModel = (StaticFilterDataModel) model.getSelectedFilterDataModel();
    staticModel.setInitialSelection( index );
  }

  @Bindable
  // bound at init
  public void setDefaultValue( List<String> defaultValue ) {
    if ( !updateSelectedIndexes ) {
      return;
    }
    FilterComponentModel compModel = model.getSelectedComponentModel();
    List<String> prev = null;
    if ( compModel instanceof DefaultValueComponentModel ) {
      prev = getDefaultValue();
      ( (DefaultValueComponentModel) compModel ).setDefaultValue( defaultValue );
    }
    firePropertyChange( "defaultValue", prev, defaultValue );
  }

  public List<String> getDefaultValue() {
    FilterComponentModel compModel = model.getSelectedComponentModel();
    if ( compModel instanceof DefaultValueComponentModel ) {
      return ( (DefaultValueComponentModel) compModel ).getDefaultValue();
    } else {
      return new ArrayList<String>();
    }
  }

  @Bindable
  // bound at init
  public void setDateDefaultValue( String defaultValue ) {
    DateComponentModel dateModel = model.getSpecificComponentModel( DateComponentModel.class );
    List<String> values = new ArrayList<String>();
    values.add( defaultValue );
    dateModel.setDefaultValue( values );
  }

  @Bindable
  public void setSelectedValueIndex( int index ) {
    if ( !updateSelectedIndexes || index < 0 ) {
      return;
    }

    FilterDataModel ftModel = model.getSelectedFilterDataModel();
    if ( ftModel instanceof QueryFilterDataModel ) {
      ( (QueryFilterDataModel) ftModel ).setValueIndex( index );
    }
  }

  @Bindable
  public void setSelectedLabelIndex( int index ) {
    if ( !updateSelectedIndexes || index < 0 ) {
      return;
    }

    FilterDataModel ftModel = model.getSelectedFilterDataModel();
    if ( ftModel instanceof QueryFilterDataModel ) {
      ( (QueryFilterDataModel) ftModel ).setLabelIndex( index );
    }
  }

  @Bindable
  public void setMultiple( boolean value ) {

    switch ( model.getSelectedComponentType() ) {
      case LIST:
        ListComponentModel listModel = model.getSpecificComponentModel( ListComponentModel.class );
        listModel.setMultiple( value );
        break;
      case BUTTON:
        ButtonComponentModel buttonModel = model.getSpecificComponentModel( ButtonComponentModel.class );
        buttonModel.setMultiple( value );
        break;
      default:
        break;
    }

  }

  // COMPONENT PROPERTIES MODEL BINDING view->model (end)

  // ** COMPONENT PROPERTIES MODEL BINDING model->view (begin)

  /**
   * component model -> UI (properties)
   *
   * @param model
   */
  private void applyPropertiesModel( EditFilterModel model ) {

    // applyDataModelDefaults(model);
    applyDefaultValue( model );

    switch ( model.getSelectedComponentType() ) {
      case TEXT:
        TextComponentModel textModel = (TextComponentModel) model.getSelectedComponentModel();
        // max chars
        Integer maxChars = textModel.getMaxChars();
        String value = ( maxChars != null ) ? maxChars.toString() : "";
        editFilterPropTextMaxChars.setValue( value );
        // width
        Integer width = textModel.getWidth();
        value = ( width != null ) ? width.toString() : "";
        editFilterPropTextWidth.setValue( value );
        break;
      case LIST:
        ListComponentModel listModel = model.getSpecificComponentModel( ListComponentModel.class );
        editFilterPropListIsMultiple.setChecked( listModel.isMultiple() );
        Integer size = listModel.getSize();
        String sizeString = ( size != null ) ? size.toString() : "";
        editFilterPropListSize.setValue( sizeString );
        break;
      case BUTTON:
        ButtonComponentModel buttonModel = model.getSpecificComponentModel( ButtonComponentModel.class );
        editFilterPropButtonIsMultiple.setChecked( buttonModel.isMultiple() );
        break;
      case DATE:
        DateComponentModel dateModel = model.getSpecificComponentModel( DateComponentModel.class );
        editFilterPropDateFormat.setValue( dateModel.getFormat() );
        // select right restriction
        for ( int i = 0; i < getMenuListSize( editFilterDateRestriction ); i++ ) {
          editFilterDateRestriction.setSelectedIndex( i );
          value = getMenuListSelectedItem( editFilterDateRestriction ); // (String)
          CalendarRestriction restriction = Enum.valueOf( CalendarRestriction.class, value );
          if ( restriction == dateModel.getRestriction() ) {
            break;
          }
        }
        if ( dateModel.getRestriction() == CalendarRestriction.RANGE ) {
          editFilterPropDateFrom.setValue( dateModel.getRangeBegin() );
          editFilterPropDateTo.setValue( dateModel.getRangeEnd() );
        }
        break;
      default:
        break;
    }
  }

  /**
   * Default Value model->UI
   *
   * @param model
   */
  private void applyDefaultValue( EditFilterModel model ) {
    // ComponentModel
    FilterComponentModel compModel = model.getSelectedComponentModel();
    if ( compModel instanceof DefaultValueComponentModel ) {
      DefaultValueComponentModel dModel = (DefaultValueComponentModel) model.getSelectedComponentModel();
      this.setDefaultValue( dModel.getDefaultValue() );
      this.setUseFirstValue( dModel.getUseFirst() );

      StringBuilder sb = new StringBuilder();
      List<String> defVals = dModel.getDefaultValue();
      if ( defVals != null && defVals.size() > 0 ) {
        for ( String val : defVals ) {
          if ( sb.length() > 0 ) {
            sb.append( "|" );
          }
          sb.append( val.toString() );
        }
      }
      specifyTextbox.setValue( sb.toString() );

      // set the static list initial selections too

      String selectedVal = "";
      Collection<XulMenuitem> items = specifyList.getElements();
      if ( items != null && items.size() > 0 ) {
        defVals = dModel.getDefaultValue();

        if ( defVals != null && defVals.size() > 0 ) {
          selectedVal = dModel.getDefaultValue().get( 0 );
          specifyList.setValue( selectedVal );
        } else {
          specifyList.setSelectedIndex( 0 );
        }
      }

    }
  }

  /**
   * DataModel value/label/initial -> UI
   *
   * @param model
   */
  private void applyDataModelDefaults( EditFilterModel model ) {
    this.updateSelectedIndexes = false;
    switch ( model.getSelectedPovDataType() ) {
      case MQL:
      case SQL:
        setMenuListsIndex( labelColumnLists, getSelectedLabelIndex() );
        setMenuListsIndex( valueColumnLists, getSelectedValueIndex() );
        // selectLabelValueColumns((QueryFilterDataModel)model.getSelectedFilterDataModel());
        break;
      case STATIC:
        break;
    }
    this.updateSelectedIndexes = true;
  }

  private void setListboxesIndex( ArrayList<XulListbox> list, int index ) {
    for ( XulListbox listbox : list ) {
      if ( listbox != null && listbox.getRowCount() > index && index >= 0 ) {
        listbox.setSelectedIndex( index );
      }
    }
  }

  private void setMenuListsIndex( ArrayList<XulMenuList<XulMenuitem>> list, int index ) {
    for ( XulMenuList<XulMenuitem> menuList : list ) {
      if ( menuList != null && index >= 0 && getMenuListSize( menuList ) > 0 ) {
        setMenuListSelectedIndex( menuList, index );
      }

    }
  }

  // COMPONENT PROPERTIES MODEL BINDING model->view (end)

  private int getInitialSelection() {
    StaticFilterDataModel staticModel = (StaticFilterDataModel) model.getSelectedFilterDataModel();
    return staticModel.getInitialSelection();
  }

  private int getSelectedValueIndex() {
    FilterDataModel ftModel = model.getSelectedFilterDataModel();
    if ( ftModel instanceof QueryFilterDataModel ) {
      return ( (QueryFilterDataModel) ftModel ).getValueIndex();
    } else {
      return 0;
    }
  }

  private int getSelectedLabelIndex() {
    FilterDataModel ftModel = model.getSelectedFilterDataModel();
    if ( ftModel instanceof QueryFilterDataModel ) {
      return ( (QueryFilterDataModel) ftModel ).getLabelIndex();
    } else {
      return 0;
    }
  }

  // jsni defineMqlFilterCallbacks
  private void setMqlDatasource( String domainId, String modelId, String dataSourceName ) {
    // put in interface label
    mqlFilterSelectedDataSource.setValue( dataSourceName );
    // store in model
    MqlFilterDataModel mqlFilterModel = this.getMqlFilterModel( model );
    mqlFilterModel.setDataSource( domainId, modelId, dataSourceName );
  }

  public boolean isSqlQueryApplied() {
    return model != null && model.getSpecificDataModel( SqlFilterDataModel.class ).isQueryApplied();
  }

  @Bindable
  public void setSqlQueryApplied( boolean applied ) {
    model.getSpecificDataModel( SqlFilterDataModel.class ).setQueryApplied( applied );
    setLabelValueOptionsEnabled( applied );
  }

  @Bindable
  public void setTextMaxChars( Integer maxChars ) {
    TextComponentModel textModel = model.getSpecificComponentModel( TextComponentModel.class );
    textModel.setMaxChars( maxChars );
  }

  @Bindable
  public void setSize( Integer size ) {
    ListComponentModel listModel = model.getSpecificComponentModel( ListComponentModel.class );
    listModel.setSize( size );
  }

  @Bindable
  public void setTextWidth( Integer width ) {
    TextComponentModel textModel = model.getSpecificComponentModel( TextComponentModel.class );
    textModel.setWidth( width );
  }

  @Bindable
  public void setDateFormat( String dateFormat ) {
    DateComponentModel dateModel = model.getSpecificComponentModel( DateComponentModel.class );
    dateModel.setFormat( dateFormat );
  }

  @Bindable
  public void setDateFrom( String date ) {
    DateComponentModel dateModel = model.getSpecificComponentModel( DateComponentModel.class );
    dateModel.setRangeBegin( date );
  }

  @Bindable
  public void setDateTo( String date ) {
    DateComponentModel dateModel = model.getSpecificComponentModel( DateComponentModel.class );
    dateModel.setRangeEnd( date );
  }

  @Bindable
  public void setStaticListInitialValue( int index ) { // TODO: will be removed
    if ( !updateSelectedIndexes || index < 0 ) {
      return;
    }

    StaticFilterDataModel staticModel = model.getSpecificDataModel( StaticFilterDataModel.class );
    staticModel.setInitialSelection( index );
    setStaticListInitialValue();
  }

  public void setStaticListInitialValue() {

    String value = null;
    int idx = 0;

    if ( useFirstRadio.isSelected() ) {
      idx = 0;
      this.specifyList.setSelectedIndex( 0 );
    } else {
      value = specifyTextbox.getValue();
      this.specifyList.setSelectedItem( value );
    }
    // set in data model
    StaticFilterDataModel staticModel = model.getSpecificDataModel( StaticFilterDataModel.class );
    staticModel.setInitialSelection( idx );
    // default are applied from component model, set there
    if ( model.getSelectedComponentModel() instanceof DefaultValueComponentModel ) {
      DefaultValueComponentModel compModel = (DefaultValueComponentModel) model.getSelectedComponentModel();
      List<String> values = new ArrayList<String>();
      if ( StringUtils.isEmpty( value ) == false ) {
        values.add( value );
      }
      compModel.setDefaultValue( values );
    }
  }

  @Bindable
  public void setOrientationVertical( boolean vert ) {
    model.setVerticalOrientation( vert );
  }

  public static void setConfiguration( FilterDialogConfiguration configuration ) {
    EditFilterController.configuration = configuration;
  }

  private void configure() {
    if ( configuration == null ) {
      return; // fail fast - nothing to do if we're not configured
    }
    // Set the dialog text
    editFilterDialog.setTitle( configuration.getTitle() );

    // Hide parameters button if they are not supported
    sqlParameterButton.setVisible( configuration.isParametersSupported() );
    mqlParameterButton.setVisible( configuration.isParametersSupported() );

    // Hide components that should not be available
    if ( configuration.getSupportedComponentTypes() != null ) {
      if ( povTypeToButtonMapping == null ) {
        povTypeToButtonMapping = new HashMap<PovComponentType, XulButton>();
        povTypeToButtonMapping.put( PovComponentType.BUTTON, buttonFilterButton );
        povTypeToButtonMapping.put( PovComponentType.CHECK, checkboxFilterButton );
        povTypeToButtonMapping.put( PovComponentType.DATE, datepickerFilterButton );
        povTypeToButtonMapping.put( PovComponentType.DROP_DOWN, dropDownFilterButton );
        povTypeToButtonMapping.put( PovComponentType.LIST, listFilterButton );
        povTypeToButtonMapping.put( PovComponentType.RADIO, radioFilterButton );
        povTypeToButtonMapping.put( PovComponentType.TEXT, textfieldFilterButton );
      }
      // Disable all
      for ( PovComponentType c : povTypeToButtonMapping.keySet() ) {
        povTypeToButtonMapping.get( c ).setVisible( false );
      }
      // Only enable the supported ones
      for ( PovComponentType supportedComponent : configuration.getSupportedComponentTypes() ) {
        povTypeToButtonMapping.get( supportedComponent ).setVisible( true );
      }
    }

    // Hide text field options if they are not supported
    editFilterPropTextContainer.setVisible( configuration.isTextFieldOptionsSupported() );

    // Hide date restrictions if they are not supported
    editFilterDateRestrictionsContainer.setVisible( configuration.isCalendarRestrictionsSupported() );

    // Hide orientation options if they are not supported
    editButtonOrientationContainer.setVisible( configuration.isOrientationOptionsSupported() );
    editCheckboxOrientationContainer.setVisible( configuration.isOrientationOptionsSupported() );
    editRadioOrientationContainer.setVisible( configuration.isOrientationOptionsSupported() );

    // Hide the display name option if it's not supported
    displayNameOptionContainer.setVisible( configuration.isDisplayNameSupported() );
  }
  protected boolean isInitialized() {
    return this.initialized;
  }
}
