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
 * Copyright (c) 2002-2023 Hitachi Vantara. All rights reserved.
 */

package org.pentaho.gwt.widgets.client.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.event.dom.client.KeyCodes;
import org.pentaho.gwt.widgets.client.i18n.WidgetsLocalizedMessages;
import org.pentaho.gwt.widgets.client.i18n.WidgetsLocalizedMessagesSingleton;
import org.pentaho.gwt.widgets.client.table.ColumnComparators.BaseColumnComparator;
import org.pentaho.gwt.widgets.client.table.ColumnComparators.ColumnComparatorTypes;
import org.pentaho.gwt.widgets.client.utils.ElementUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.gen2.table.client.AbstractScrollTable;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.gen2.table.client.ScrollTable;
import com.google.gwt.gen2.table.client.SelectionGrid;
import com.google.gwt.gen2.table.client.SortableGrid;
import com.google.gwt.gen2.table.client.TableModelHelper;
import com.google.gwt.gen2.table.event.client.RowSelectionHandler;
import com.google.gwt.gen2.table.override.client.FlexTable;
import com.google.gwt.gen2.table.override.client.HTMLTable;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.Widget;
import org.pentaho.gwt.widgets.client.panel.VerticalFlexPanel;

/**
 * <p>
 * Core reusable, table widget for displaying tabular data that is based on a composite of a ScrollTable and a
 * FixedWidthGrid.
 *
 * <p>
 * Usage Notes:
 *
 * <p>
 * <ul>
 * <li>You must call the populateTable or populateTableWithSimpleMessage method AFTER having instanciated it.
 * <li>It's always better to define the width and height right after instanciation.
 * <li>Never set the resize policy to FILL_WIDTH or FireFox will experience an ever growing table.
 * </ul>
 * </p>
 */
@SuppressWarnings( "deprecation" )
public class BaseTable extends Composite {

  private static final WidgetsLocalizedMessages MSGS = WidgetsLocalizedMessagesSingleton.getInstance().getMessages();

  public static final BaseColumnComparator DEFAULT_COLUMN_COMPARATOR = BaseColumnComparator
      .getInstance( ColumnComparatorTypes.STRING_NOCASE );
  public static final String TABLE_NO_FILL = "table-no-fill";

  protected Panel parentPanel = new VerticalFlexPanel();

  protected ScrollTable scrollTable;

  private FixedWidthFlexTable tableHeader;

  protected FixedWidthGrid dataGrid;

  protected String scrollTableWidth;

  protected String scrollTableHeight;

  private int[] columnWidths;

  private int numberOfColumns;

  private SelectionGrid.SelectionPolicy selectionPolicy;

  private BaseColumnComparator[] columnComparators;

  private Collection objects;

  private Map<Element, Object> objectElementMap;

  private BaseTableColumnSorter baseTableColumnSorter;

  private boolean autoSelectionOnFocus = true;

  private final TableListener internalDoubleClickListener = new TableListener() {
    public void onCellClicked( SourcesTableEvents sender, int row, int cell ) {
      for ( TableListener listener : doubleClickListeners ) {
        listener.onCellClicked( sender, row, cell );
      }
    }
  };

  private List<TableListener> doubleClickListeners = new ArrayList<TableListener>();

  private final TableListener internalTableListener = new TableListener() {
    public void onCellClicked( SourcesTableEvents sender, int row, int cell ) {
      for ( TableListener listener : tableListeners ) {
        listener.onCellClicked( sender, row, cell );
      }
    }
  };

  private List<TableListener> tableListeners = new ArrayList<TableListener>();

  /**
   * Simple constructor.
   */
  public BaseTable( String[] tableHeaderNames, int[] columnWidths ) {
    this( tableHeaderNames, columnWidths, null );
  }

  /**
   * Simple constructor.
   */
  public BaseTable( String[] tableHeaderNames, int[] columnWidths, BaseColumnComparator[] columnComparators ) {
    this( tableHeaderNames, columnWidths, columnComparators, null );

  }

  public BaseTable( String[] tableHeaderNames, int[] columnWidths, BaseColumnComparator[] columnComparators,
                    SelectionGrid.SelectionPolicy selectionPolicy, TableColumnSortListener sortListener ) {
    this( tableHeaderNames, columnWidths, true, columnComparators, selectionPolicy, sortListener );
  }

  public BaseTable( String[] tableHeaderNames, int[] columnWidths, boolean fixedColumnWidths,
                    BaseColumnComparator[] columnComparators, SelectionGrid.SelectionPolicy selectionPolicy,
                    TableColumnSortListener sortListener ) {
    this( tableHeaderNames, columnWidths, fixedColumnWidths, columnComparators, selectionPolicy );
    baseTableColumnSorter.setTableColumnSortListener( sortListener );
  }

  public BaseTable( String[] tableHeaderNames, int[] columnWidths, BaseColumnComparator[] columnComparators,
                    SelectionGrid.SelectionPolicy selectionPolicy ) {
    this( tableHeaderNames, columnWidths, true,  columnComparators, selectionPolicy );
  }

  /**
   * Main constructor.
   *
   * Note: For column width values, use -1 to not specify a column width. Note: For column comparators individually, a
   * null value will disable sorting for that column. If you set the columnComparators array to null, all columns will
   * be populated with the default column comparator.
   */
  public BaseTable( String[] tableHeaderNames, int[] columnWidths, boolean fixedColumnWidths,
                   BaseColumnComparator[] columnComparators, SelectionGrid.SelectionPolicy selectionPolicy ) {

    if ( tableHeaderNames != null ) {
      this.columnWidths = columnWidths;
      this.numberOfColumns = tableHeaderNames.length;

      this.parentPanel.setWidth( "100%" );

      if ( selectionPolicy != null ) {
        this.selectionPolicy = selectionPolicy;
      }

      // Set column comparators to default if columnComparators is null
      if ( columnComparators == null ) {
        this.columnComparators = new BaseColumnComparator[tableHeaderNames.length];

        for ( int i = 0; i < this.columnComparators.length; i++ ) {
          this.columnComparators[i] = DEFAULT_COLUMN_COMPARATOR;
        }
      } else {
        this.columnComparators = columnComparators;
      }

      AbstractScrollTable.ResizePolicy resizePolicy = fixedColumnWidths
              ? AbstractScrollTable.ResizePolicy.FLOW
              : AbstractScrollTable.ResizePolicy.FILL_WIDTH;

      // For variable column widths, implement scrolling using CSS alone to circumvent the issues raised by
      // `ScrollPolicy.BOTH` causing detection of the scrollbar width, and then affecting the header table's
      // padding-right, resulting in inconsistent consecutive layouts (non-idempotent).
      AbstractScrollTable.ScrollPolicy scrollPolicy = fixedColumnWidths
              ? AbstractScrollTable.ScrollPolicy.BOTH
              : AbstractScrollTable.ScrollPolicy.DISABLED;

      createTable( tableHeaderNames, columnWidths, new Object[0][0], resizePolicy, selectionPolicy, scrollPolicy );

      this.parentPanel.add( scrollTable );
      fillWidth();

      initWidget( parentPanel );

    } else {
      System.err.println( MSGS.tableHeaderInputError() );
    }
  }

  /**
   * Gets a value that indicates whether a row that receives keyboard focus should be automatically
   * selected.
   * <p>
   *     This only applies when the table's {@link SelectionGrid.SelectionPolicy} is
   *     {@link SelectionGrid.SelectionPolicy#ONE_ROW}.
   * </p>
   * @return boolean
   */
  public boolean isAutoSelectionOnFocus() {
    return autoSelectionOnFocus;
  }

  /** enables the selection on focus
   *
   * @param autoSelectionOnFocus
   */
  public void setAutoSelectionOnFocus( boolean autoSelectionOnFocus ) {
    this.autoSelectionOnFocus = autoSelectionOnFocus;
  }

  /**
   * Creates a table with the given headers, column widths, row/column values, and resize policy.
   */
  protected void createTable( String[] tableHeaderNames, int[] columnWidths, Object[][] rowAndColumnValues,
                             AbstractScrollTable.ResizePolicy resizePolicy,
                             SelectionGrid.SelectionPolicy selectionPolicy,
                             AbstractScrollTable.ScrollPolicy scrollPolicy ) {
    createTableHeader( tableHeaderNames );
    createDataGrid( selectionPolicy, tableHeaderNames.length );
    createScrollTable( resizePolicy, scrollPolicy );
    populateDataGrid( columnWidths, rowAndColumnValues );
  }

  /**
   * Creates and initializes the header for the table.
   */
  private void createTableHeader( String[] tableHeaderNames ) {

    tableHeader = new FixedWidthFlexTable() {
      @Override
      public void onBrowserEvent( Event event ) {
        switch ( DOM.eventGetType( event )) {
          case Event.ONKEYDOWN:
            onKeyDownForTableHeader( event );
            break;
        }
        super.onBrowserEvent( event );
      }
    };

    // Set header values and disable text selection
    final FlexTable.FlexCellFormatter cellFormatter = tableHeader.getFlexCellFormatter();
    for ( int i = 0; i < tableHeaderNames.length; i++ ) {
      tableHeader.setHTML( 0, i, tableHeaderNames[i] );

      tableHeader.getCellFormatter().getElement(0, i ).setTabIndex( i == 0 ? 0 : -1 );

      cellFormatter.setHorizontalAlignment( 0, i, HasHorizontalAlignment.ALIGN_LEFT );
      cellFormatter.setWordWrap( 0, i, false );
      cellFormatter.setStylePrimaryName( 0, i, "overflowHide" );
    }

    if ( this.selectionPolicy == null ) {
      tableHeader.setStylePrimaryName( "disabled" ); //$NON-NLS-1$
    }
    tableHeader.sinkEvents( Event.ONKEYDOWN );
  }

  private void onKeyDownForTableHeader( Event event ) {
    switch (event.getKeyCode()) {
      case KeyCodes.KEY_RIGHT: {
        event.preventDefault();
        moveFocusedColumn( true );
        break;
      }
      case KeyCodes.KEY_LEFT: {
        event.preventDefault();
        moveFocusedColumn( false );
        break;
      }
      case KeyCodes.KEY_SPACE: {
        event.preventDefault();
        int focusedColumn = getFocusedColumn();
        if ( isColumnSortable( focusedColumn )) {
          sortColumn(focusedColumn);
        }
        break;
      }

    }
  }

  private void moveFocusedColumn( boolean isRight ) {
    int focusedColumn = getFocusedColumn();
    if ( focusedColumn < 0 && !isRight ) {
      focusedColumn = tableHeader.getColumnCount();
    }

    int nextColumn = isRight ? focusedColumn + 1 : focusedColumn - 1;
    setFocusedColumn(nextColumn);
  }

  private void setFocusedColumn( int nextColumn ) {
    int columnCount = tableHeader.getColumnCount();
    if (nextColumn >= 0 && nextColumn < columnCount) {
      for (int i = 0; i < columnCount; i++) {
        tableHeader.getCellFormatter().getElement(0,i ).setTabIndex(i == nextColumn ? 0 : -1);
      }
      tableHeader.getCellFormatter().getElement(0, nextColumn ).focus();
    }
  }

  private int getFocusedColumn() {
    int columnCount = tableHeader.getColumnCount();
    for (int i = 0; i < columnCount; i++) {
      if( tableHeader.getCellFormatter().getElement( 0, i ).getTabIndex() == 0){
        return i;
      }
    }

    return -1;
  }

  /**
   * Creates and initializes the data grid.
   */
  private void createDataGrid( SelectionGrid.SelectionPolicy selectionPolicy, int numOfColumns ) {

    dataGrid = new FixedWidthGrid( 0, numOfColumns ) {
      @Override
      public void onBrowserEvent( Event event ) {
        switch ( DOM.eventGetType( event ) ) {
          case Event.ONDBLCLICK: {
            Element td = this.getEventTargetCell( event );
            if ( td == null ) {
              return;
            }
            Element tr = DOM.getParent( td );
            Element body = DOM.getParent( tr );
            int row = DOM.getChildIndex( body, tr ) - 1;
            int column = DOM.getChildIndex( tr, td );
            internalDoubleClickListener.onCellClicked( this, row, column );
            break;
          }
          case Event.ONKEYDOWN:
            onDataGridKeyDown( event );
            break;
        }

        super.onBrowserEvent( event );

        if ( DOM.eventGetType( event ) == Event.ONCLICK ) {
          Element targetRow = this.getEventTargetRow( event );
          Element eventTarget = DOM.eventGetTarget( event );
          if ( targetRow != null && !ElementUtils.isActiveElement( eventTarget )) {
            setFocusableRow( this.getRowIndex( targetRow ), true );
          }
        }
      }
    };

    // disable text highlighting on dataGrid
    ElementUtils.killAllTextSelection( dataGrid.getElement() );

    // Set style
    if ( selectionPolicy == null ) {
      dataGrid.setSelectionPolicy( SelectionGrid.SelectionPolicy.ONE_ROW );
    } else {
      dataGrid.setSelectionPolicy( selectionPolicy );
    }

    // Add table listeners
    dataGrid.addTableListener( internalTableListener );

    // Add table selection listeners
    dataGrid.sinkEvents( Event.ONDBLCLICK | Event.ONKEYDOWN );
    baseTableColumnSorter = new BaseTableColumnSorter();
    dataGrid.setColumnSorter( baseTableColumnSorter );

    if ( this.selectionPolicy == null ) {
      dataGrid.setStylePrimaryName( "disabled" ); //$NON-NLS-1$
    }

  }

  private void onDataGridKeyDown( Event event ) {
    switch (event.getKeyCode()) {
      case KeyCodes.KEY_DOWN: {
        if ( !ElementUtils.isEventDefaultPrevented( event ) ) {
          event.preventDefault();
          moveFocusedRow( true );
        }
        break;
      }
      case KeyCodes.KEY_UP: {
        if ( !ElementUtils.isEventDefaultPrevented( event ) ) {
          event.preventDefault();
          moveFocusedRow( false );
        }
        break;
      }
      case KeyCodes.KEY_SPACE: {
        if ( !DOM.eventGetTarget( event ).getTagName().equalsIgnoreCase( "input" ) ) {
          event.preventDefault();
          int focusedRow = getFocusedRow();
          if ( selectionPolicy == SelectionGrid.SelectionPolicy.MULTI_ROW ) {
            boolean shiftKey = DOM.eventGetShiftKey( event );
            boolean ctrlKey = DOM.eventGetCtrlKey( event )
                    || DOM.eventGetMetaKey( event );
            // Select the rows
            dataGrid.selectRow( focusedRow, ctrlKey, shiftKey );
          } else if ( selectionPolicy == SelectionGrid.SelectionPolicy.ONE_ROW ) {
            dataGrid.selectRow( focusedRow, true );
          }
        }
        break;
      }

    }
  }
  private void moveFocusedRow( boolean isDown ) {
    int focusedRow = getFocusedRow();
    if( focusedRow < 0 && !isDown ){
      focusedRow = dataGrid.getRowCount();
    }

    int nextRow = isDown ? focusedRow + 1 : focusedRow - 1;
    if( nextRow >= 0 && nextRow < dataGrid.getRowCount() ) {
      setFocusableRow( nextRow, true );

      if ( isAutoSelectionOnFocus()
           && SelectionGrid.SelectionPolicy.ONE_ROW.equals(dataGrid.getSelectionPolicy()) ) {
        dataGrid.selectRow( nextRow, true );
      }
    }
  }

  private int getFocusedRow() {
    int rowCount = dataGrid.getRowCount();
    for (int i = 0; i < rowCount; i++) {
      if( dataGrid.getRowFormatter().getElement(i).getTabIndex() == 0){
        return i;
      }
    }

    return -1;
  }

  /**
   * Sets the keyboard navigation on the selected row
   * @param row
   */
  public void setFocusableRow( int row ) {
    setFocusableRow( row, false );
  }

  /**
   * Sets the keyboard navigation and focus on the selected row
   * @param row
   * @param focus
   */
  public void setFocusableRow( int row, boolean focus ) {
    int rowCount = dataGrid.getRowCount();
    if( row >= 0 && row < rowCount ) {
      for (int i = 0; i < rowCount; i++) {
        dataGrid.getRowFormatter().getElement(i).setTabIndex(i == row ? 0 : -1);
      }

      if ( focus ) {
        dataGrid.getRowFormatter().getElement(row).focus();
      }
    }
  }

  /**
   * Creates and initializes the scroll table.
   */
  private void createScrollTable( AbstractScrollTable.ResizePolicy resizePolicy,
                                  AbstractScrollTable.ScrollPolicy scrollPolicy ) {

    scrollTable = new ScrollTable( dataGrid, tableHeader, (BaseTableImages) GWT.create( BaseTableImages.class ) ) {
      protected void resizeTablesVerticallyNow() {

        // Give the data wrapper all remaining height
        int totalHeight = DOM.getElementPropertyInt( getElement(), "clientHeight" );
        if ( totalHeight == 0 ) {
          return;
        }
        super.resizeTablesVerticallyNow();
      }
    };

    scrollTable.addStyleName( "flex-column" );
    // Access ScrollTable's absoluteElem.
    scrollTable.getElement().getFirstChildElement().addClassName( "flex-column" );

    scrollTable.setResizePolicy( resizePolicy );
    scrollTable.setCellPadding( 0 );
    scrollTable.setCellSpacing( 0 );

    scrollTable.setScrollPolicy( scrollPolicy );
    if ( !scrollPolicy.equals( AbstractScrollTable.ScrollPolicy.DISABLED ) ) {
      scrollTable.addStyleName( "fixed-column-widths" );
    }

    // Set column comparators
    if ( columnComparators != null ) {
      for ( int i = 0; i < columnComparators.length; i++ ) {
        if ( columnComparators[i] != null ) {
          scrollTable.setColumnSortable( i, true );
        } else {
          scrollTable.setColumnSortable( i, false );
        }
      }
    }
    if ( this.scrollTableWidth != null ) {
      this.setWidth( scrollTableWidth );
    }
    if ( this.scrollTableHeight != null ) {
      this.setHeight( scrollTableHeight );
    }

    setColumnWidths( columnWidths );
    fillWidth();
  }

  /**
   * Populates the data grid with data then sets the column widths.
   */
  protected void populateDataGrid( int[] columnWidths, Object[][] rowAndColumnValues, Collection objects ) {
    this.objects = objects;
    populateDataGrid( columnWidths, rowAndColumnValues );
  }

  /**
   * Populates the data grid with data then sets the column widths.
   */
  protected void populateDataGrid( int[] columnWidths, Object[][] rowAndColumnValues ) {
    while ( dataGrid.getRowCount() > 0 ) {
      dataGrid.removeRow( 0 );
    }
    // Set table values
    //
    dataGrid.resizeRows( rowAndColumnValues.length );
    for ( int i = 0; i < rowAndColumnValues.length; i++ ) {
      // For even rows, add background highlighting
      if ( i % 2 != 0 ) {
        dataGrid.getRowFormatter().setStyleName( i, "cellTableOddRow" );
      }

      dataGrid.getRowFormatter().getElement(i).setTabIndex( i == 0 ? 0 : -1 );

      for ( int j = 0; j < rowAndColumnValues[i].length; j++ ) {
        Object value = rowAndColumnValues[i][j];

        if ( value != null ) {
          if ( value instanceof String ) {
            dataGrid.setHTML( i, j, value.toString() );
          } else if ( value instanceof Widget ) {
            dataGrid.setWidget( i, j, (Widget) value );
          } else {
            System.err.print( MSGS.invalidDataGridTypeSet() );
            Window.alert( MSGS.invalidDataGridTypeSet() );
            return;
          }
        }
      }
    }

    for ( int j = 0; j < tableHeader.getColumnCount(); j++ ) {
      tableHeader.getCellFormatter().getElement( 0, j ).setTabIndex( j == 0 && isColumnSortable( j ) ? 0 : -1 );
    }

    // Set column widths
    setColumnWidths( columnWidths );

    // Set cell styles/tooltip for data grid cells
    final HTMLTable.CellFormatter cellFormatter = dataGrid.getCellFormatter();
    objectElementMap = new HashMap<Element, Object>();
    Object[] objectArray = null;
    if ( objects != null ) {
      objectArray = objects.toArray();
    }

    for ( int i = 0; i < rowAndColumnValues.length; i++ ) {
      Object object = null;
      if ( objectArray != null ) {
        object = objectArray[i];
      }
      for ( int j = 0; j < rowAndColumnValues[i].length; j++ ) {
        Object value = rowAndColumnValues[i][j];
        Element element = null;
        try {
          element = cellFormatter.getElement( i, j );
        } catch ( Exception e ) {
          //ignore
        }

        if ( element != null ) {

          if ( value != null && value instanceof String && !value.equals( "&nbsp;" ) ) { //$NON-NLS-1$
            element.setTitle( value.toString() );
          }
        }
        if ( object != null ) {
          objectElementMap.put( element, object );
        }
      }
    }
    baseTableColumnSorter.setObjectMap( objectElementMap );
    scrollTable.redraw();
  }

  private void setColumnWidths( int[] columnWidths ) {
    if ( columnWidths != null ) {
      for ( int i = 0; i < columnWidths.length; i++ ) {
        int columnWidth = columnWidths[i];
        if ( columnWidth >= 0 ) {
          // Prevent redistribution of diff column width to subsequent columns.
          dataGrid.setColumnWidth( i, columnWidth );

          if ( columnWidth > 0 ) {
            // Store original widths as preferred, so that, when resizing,
            // column widths converge to these proportions.
            scrollTable.setPreferredColumnWidth( i, columnWidth );
          }

          scrollTable.setColumnWidth( i, columnWidth );
        }
      }
    }
  }

  /**
   * Makes this table fill all available width.
   */
  public void fillWidth() {
    scrollTable.fillWidth();
  }

  public void noFill() {
    scrollTable.addStyleName( TABLE_NO_FILL );
  }

  /**
   * Displays a message to the user in the table instead of data. Deprecated in favor of
   * {@link BaseTable#showMessage(String)}
   */
  @Deprecated
  public void populateTableWithSimpleMessage( final String message ) {
    showMessage( message );
  }

  /**
   * Makes this table display a message instead of the column data.
   *
   * @param message
   *          The message to display.
   */
  public void showMessage( String message ) {

    parentPanel.clear();

    String[] simpleMessageHeaderValues = new String[] { "&nbsp;" }; //$NON-NLS-1$ //$NON-NLS-2$
    String[][] simpleMessageRowAndColumnValues = new String[][] { { message, "&nbsp;" } }; //$NON-NLS-1$

    createTable( simpleMessageHeaderValues, null, simpleMessageRowAndColumnValues,
        AbstractScrollTable.ResizePolicy.FIXED_WIDTH, selectionPolicy, AbstractScrollTable.ScrollPolicy.BOTH );

    parentPanel.add( scrollTable );

    fillWidth();
  }

  /**
   * Creates the table using the default values specified in the constructor but with new data for the rows.
   */
  public void populateTable( Object[][] rowAndColumnValues, Collection objects ) {
    populateDataGrid( columnWidths, rowAndColumnValues, objects );
  }

  /**
   * Creates the table using the default values specified in the constructor but with new data for the rows.
   */
  public void populateTable( Object[][] rowAndColumnValues ) {
    populateDataGrid( columnWidths, rowAndColumnValues );
  }

  /**
   * Adds an additional table listener in addition to the default listener.
   */
  public void addTableListener( TableListener listener ) {
    tableListeners.add( listener );
  }

  /**
   * Adds an additional table selection listener in addition to the default listener.
   */
  public void addRowSelectionHandler( RowSelectionHandler handler ) {
    dataGrid.addRowSelectionHandler( handler );
  }

  /**
   * Adds a listener to fire when a user double-clicks on a table row.
   */
  public void addDoubleClickListener( TableListener listener ) {
    doubleClickListeners.add( listener );
  }

  /**
   * Gets the text within the specified cell.
   */
  public String getText( int row, int column ) {
    return dataGrid.getText( row, column );
  }

  /**
   * Returns the number of columns in the data table.
   */
  public int getNumberOfColumns() {
    return numberOfColumns;
  }

  /**
   * Select a row in the data table.
   */
  public void selectRow( int row ) {
    dataGrid.selectRow( row, false );
  }

  /**
   * Returns the set of selected row indexes.
   */
  public Set<Integer> getSelectedRows() {
    return dataGrid.getSelectedRows();
  }

  /**
   * Deselect all selected rows in the data table.
   */
  public void deselectRows() {
    dataGrid.deselectAllRows();
  }

  /**
   * Default column sorter for this class.
   */
  final class BaseTableColumnSorter extends SortableGrid.ColumnSorter {

    private Map<Element, Object> objMap;

    private TableColumnSortListener sortListener;

    public void setTableColumnSortListener( TableColumnSortListener sortListener ) {
      this.sortListener = sortListener;
    }

    public void setObjectMap( Map<Element, Object> objMap ) {
      this.objMap = objMap;
    }

    private List sortObjectCollection( List<Element> elements ) {
      List objects = new ArrayList();
      for ( Element element : elements ) {
        if ( objMap.containsKey( element ) ) {
          objects.add( objMap.get( element ) );
        }
      }
      return objects;
    }

    public void onSortColumn( SortableGrid grid, TableModelHelper.ColumnSortList sortList,
        SortableGrid.ColumnSorterCallback callback ) {

      // Get the primary column and sort order
      int column = sortList.getPrimaryColumn();
      boolean ascending = sortList.isPrimaryAscending();

      // Apply the default quicksort algorithm
      // Element[] tdElems = new Element[grid.getRowCount()];
      List<Element> tdElems = new ArrayList<Element>();
      for ( int i = 0; i < grid.getRowCount(); i++ ) {
        // first, clear out existing row styling for oddRow.
        grid.getRowFormatter().setStyleName( i, "" );
        tdElems.add( grid.getCellFormatter().getElement( i, column ) );
      }

      if ( grid.getColumnCount() > column ) {
        Collections.sort( tdElems, columnComparators != null && columnComparators[column] != null
            ? columnComparators[column] : DEFAULT_COLUMN_COMPARATOR );
      }

      // Convert tdElems to trElems, reversing if needed
      Element[] trElems = new Element[tdElems.size()];
      List<Element> sortedTdElement = new ArrayList<Element>();

      if ( ascending ) {
        for ( int i = 0; i < tdElems.size(); i++ ) {
          trElems[i] = DOM.getParent( tdElems.get( i ) );
          sortedTdElement.add( tdElems.get( i ) );
        }
      } else {
        int maxElem = tdElems.size() - 1;
        for ( int i = 0; i <= maxElem; i++ ) {
          trElems[i] = DOM.getParent( tdElems.get( maxElem - i ) );
          sortedTdElement.add( tdElems.get( maxElem - i ) );
        }
      }
      callback.onSortingComplete( trElems );
      sortListener.onSortingComplete( sortObjectCollection( sortedTdElement ) );

      // now that the sorting is done, set the alternating row color
      for ( int i = 0; i < grid.getRowCount(); i++ ) {
        if ( i % 2 != 0 ) {
          grid.getRowFormatter().setStyleName( i, "cellTableOddRow" );
        }
      }

    }
  };

  /*
   * (non-Javadoc)
   *
   * @see com.google.gwt.user.client.ui.UIObject#setWidth(java.lang.String)
   */
  @Override
  public void setWidth( final String width ) {
    super.setWidth( width );
    this.scrollTableWidth = width;
    scrollTable.setWidth( width );
    scrollTable.getHeaderTable().setWidth( width );
    scrollTable.getDataTable().setWidth( width );
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.gwt.user.client.ui.UIObject#setHeight(java.lang.String)
   */
  @Override
  public void setHeight( final String height ) {
    super.setHeight( height );
    this.scrollTableHeight = height;
    scrollTable.setHeight( height );
  }

  /**
   * Sets this widget to the desired height. Deprecated signature. Use {@link BaseTable#setHeight(String)}
   */
  @Deprecated
  public void setTableHeight( final String height ) {
    setHeight( height );
  }

  /**
   * Sets this widget to the desired height. Deprecated signature. Use {@link BaseTable#setWidth(String)}
   */
  @Deprecated
  public void setTableWidth( final String width ) {
    setWidth( width );
  }

  public void replaceRow( int row, Object[] data ) {
    for ( int j = 0; j < data.length; j++ ) {
      Object value = data[j];

      if ( value != null ) {
        if ( value instanceof String ) {
          dataGrid.setHTML( row, j, value.toString() );
        } else if ( value instanceof Widget ) {
          dataGrid.setWidget( row, j, (Widget) value );
        } else {
          System.err.print( MSGS.invalidDataGridTypeSet() );
          Window.alert( MSGS.invalidDataGridTypeSet() );
          return;
        }
      }
    }
  }

  /**
   * Suppresses the horizontal scrollbar.
   *
   * @deprecated The horizontal scrollbar is never shown and horizontal overflow is always hidden.
   * Showing the horizontal scrollbar would reveal that data rows always overflow horizontally when the vertical
   * scrollbar is displayed.
   */
  @Deprecated
  public void suppressHorizontalScrolling() {
  }

  public boolean isSortingEnabled() {
    return true;
  }

  public void setSortingEnabled( boolean enabled ) {
  }

  public void setColumnSortable( int column, boolean sortable ) {
    scrollTable.setColumnSortable( column, sortable );
  }

  public boolean isColumnSortable( int column ) {
    return scrollTable.isColumnSortable( column );
  }

  public void sortColumn( int column, boolean ascending ) {
    dataGrid.sortColumn( column, ascending );
    setFocusableRow( 0 );
  }

  public void sortColumn( int column ) {
    dataGrid.sortColumn( column );
    setFocusableRow( 0 );
  }

}
