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
 * Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.gwt.widgets.client.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

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

  private Panel parentPanel = new VerticalPanel();

  private ScrollTable scrollTable;

  private FixedWidthFlexTable tableHeader;

  private FixedWidthGrid dataGrid;

  private String scrollTableWidth;

  private String scrollTableHeight;

  private int[] columnWidths;

  private int numberOfColumns;

  private SelectionGrid.SelectionPolicy selectionPolicy;

  private BaseColumnComparator[] columnComparators;

  private Collection objects;

  private Map<Element, Object> objectElementMap;

  private BaseTableColumnSorter baseTableColumnSorter;

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
    this( tableHeaderNames, columnWidths, columnComparators, selectionPolicy );
    baseTableColumnSorter.setTableColumnSortListener( sortListener );
  }

  /**
   * Main constructor.
   * 
   * Note: For column width values, use -1 to not specify a column width. Note: For column comparators individually, a
   * null value will disable sorting for that column. If you set the columnComparators array to null, all columns will
   * be populated with the default column comparator.
   */
  public BaseTable( String[] tableHeaderNames, int[] columnWidths, BaseColumnComparator[] columnComparators,
      SelectionGrid.SelectionPolicy selectionPolicy ) {

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

      createTable( tableHeaderNames, columnWidths, new Object[0][0], AbstractScrollTable.ResizePolicy.FIXED_WIDTH,
          selectionPolicy );

      this.parentPanel.add( scrollTable );
      scrollTable.fillWidth();

      initWidget( parentPanel );

    } else {
      System.err.println( MSGS.tableHeaderInputError() );
    }
  }

  /**
   * Creates a table with the given headers, column widths, and row/column values using the default resize policy of
   * RESIZE_POLICY_FIXED_WIDTH.
   */
  @SuppressWarnings( "unused" )
  private void createTable( String[] tableHeaderNames, int[] columnWidths, Object[][] rowAndColumnValues ) {
    createTable( tableHeaderNames, columnWidths, rowAndColumnValues, ScrollTable.ResizePolicy.FIXED_WIDTH,
        selectionPolicy );
  }

  /**
   * Creates a table with the given headers, column widths, row/column values, and resize policy.
   */
  private void createTable( String[] tableHeaderNames, int[] columnWidths, Object[][] rowAndColumnValues,
      AbstractScrollTable.ResizePolicy resizePolicy, SelectionGrid.SelectionPolicy selectionPolicy ) {
    createTableHeader( tableHeaderNames, columnWidths );
    createDataGrid( selectionPolicy, tableHeaderNames.length );
    createScrollTable( resizePolicy );
    populateDataGrid( columnWidths, rowAndColumnValues );
  }

  /**
   * Creates and initializes the header for the table.
   */
  private void createTableHeader( String[] tableHeaderNames, final int[] columnWidths ) {

    tableHeader = new FixedWidthFlexTable();

    // Set header values and disable text selection
    final FlexTable.FlexCellFormatter cellFormatter = tableHeader.getFlexCellFormatter();
    for ( int i = 0; i < tableHeaderNames.length; i++ ) {
      tableHeader.setHTML( 0, i, tableHeaderNames[i] );
      cellFormatter.setHorizontalAlignment( 0, i, HasHorizontalAlignment.ALIGN_LEFT );
      cellFormatter.setWordWrap( 0, i, false );
      cellFormatter.setStylePrimaryName( 0, i, "overflowHide" );
    }

    if ( this.selectionPolicy == null ) {
      tableHeader.setStylePrimaryName( "disabled" ); //$NON-NLS-1$
    }
  }

  /**
   * Creates and initializes the data grid.
   */
  private void createDataGrid( SelectionGrid.SelectionPolicy selectionPolicy, int numOfColumns ) {

    dataGrid = new FixedWidthGrid( 0, numOfColumns ) {
      @Override
      public void onBrowserEvent( Event event ) {
        Element td = this.getEventTargetCell( event );
        if ( td == null ) {
          return;
        }
        Element tr = DOM.getParent( td );
        Element body = DOM.getParent( tr );
        int row = DOM.getChildIndex( body, tr ) - 1;
        int column = DOM.getChildIndex( tr, td );

        switch ( DOM.eventGetType( event ) ) {
          case Event.ONDBLCLICK: {
            internalDoubleClickListener.onCellClicked( dataGrid, row, column );
          }
          default: {
            break;
          }
        }

        super.onBrowserEvent( event );
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
    dataGrid.sinkEvents( Event.ONDBLCLICK );
    baseTableColumnSorter = new BaseTableColumnSorter();
    dataGrid.setColumnSorter( baseTableColumnSorter );

    if ( this.selectionPolicy == null ) {
      dataGrid.setStylePrimaryName( "disabled" ); //$NON-NLS-1$
    }

  }

  /**
   * Creates and initializes the scroll table.
   */
  private void createScrollTable( AbstractScrollTable.ResizePolicy resizePolicy ) {

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

    scrollTable.setResizePolicy( AbstractScrollTable.ResizePolicy.FLOW );
    scrollTable.setCellPadding( 0 );
    scrollTable.setCellSpacing( 0 );
    scrollTable.setScrollPolicy( ScrollTable.ScrollPolicy.BOTH );

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

    if ( columnWidths != null && columnWidths.length > 0 ) {
      for ( int i = 0; i < columnWidths.length; i++ ) {
        if ( columnWidths[i] > 0 ) {
          scrollTable.setColumnWidth( i, columnWidths[i] );
        }
      }
    }
    if ( scrollTableWidth != null ) {
      scrollTable.setWidth( scrollTableWidth );
    }
    scrollTable.fillWidth();
  }

  /**
   * Populates the data grid with data then sets the column widths.
   */
  private void populateDataGrid( int[] columnWidths, Object[][] rowAndColumnValues, Collection objects ) {
    this.objects = objects;
    populateDataGrid( columnWidths, rowAndColumnValues );
  }

  /**
   * Populates the data grid with data then sets the column widths.
   */
  private void populateDataGrid( int[] columnWidths, Object[][] rowAndColumnValues ) {
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

    // Set column widths
    if ( columnWidths != null ) {
      for ( int i = 0; i < columnWidths.length; i++ ) {
        if ( columnWidths[i] >= 0 ) {
          dataGrid.setColumnWidth( i, columnWidths[i] );
          scrollTable.setColumnWidth( i, columnWidths[i] );
        }
      }
    }

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

  /**
   * Makes this table fill all available width.
   */
  public void fillWidth() {
    scrollTable.fillWidth();
  }

  public void noFill() {
    scrollTable.addStyleName( "table-no-fill" );
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
        AbstractScrollTable.ResizePolicy.FIXED_WIDTH, selectionPolicy );

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

  public void suppressHorizontalScrolling() {
    dataGrid.addStyleName( "hide-h-scrolling" );
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
  }

  public void sortColumn( int column ) {
    dataGrid.sortColumn( column );
  }

}
