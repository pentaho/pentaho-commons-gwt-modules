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


package org.pentaho.gwt.widgets.client.table;

import com.google.gwt.gen2.table.client.AbstractScrollTable;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.gen2.table.client.ScrollTable;
import com.google.gwt.gen2.table.client.SelectionGrid;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class BaseTableTest {
  private BaseTable baseTable;

  @Before
  public void setUp() throws Exception {
    baseTable = mock( BaseTable.class );
  }

  @Test
  public void testFillWidth() throws Exception {
    doCallRealMethod().when( baseTable ).fillWidth();

    baseTable.scrollTable = mock( ScrollTable.class );
    baseTable.fillWidth();
    verify( baseTable.scrollTable ).fillWidth();
  }

  @Test
  public void testNoFill() throws Exception {
    doCallRealMethod().when( baseTable ).noFill();

    baseTable.scrollTable = mock( ScrollTable.class );
    baseTable.noFill();
    verify( baseTable.scrollTable ).addStyleName( BaseTable.TABLE_NO_FILL );
  }

  @Test
  public void testShowMessage() throws Exception {
    doCallRealMethod().when( baseTable ).showMessage( anyString() );

    baseTable.parentPanel = mock( Panel.class );
    baseTable.scrollTable = mock( ScrollTable.class );
    baseTable.showMessage( "message" );
    verify( baseTable.parentPanel ).clear();
    verify( baseTable.parentPanel ).add( baseTable.scrollTable );
    verify( baseTable ).createTable( any( String[].class ), any( int[].class ), any( Object[][].class ),
            eq( AbstractScrollTable.ResizePolicy.FIXED_WIDTH ), any( SelectionGrid.SelectionPolicy.class ),
            eq( AbstractScrollTable.ScrollPolicy.BOTH ) );
    verify( baseTable ).fillWidth();
  }

  @Test
  public void testPopulateTable() throws Exception {
    doCallRealMethod().when( baseTable ).populateTable( any( Object[][].class ), any( Collection.class ) );

    final Object[][] values = new Object[][] {};
    final Collection objects = mock( Collection.class );
    baseTable.populateTable( values, objects );
    verify( baseTable ).populateDataGrid( any( int[].class ), eq( values ), eq( objects ) );
  }

  @Test
  public void testPopulateTable2() throws Exception {
    doCallRealMethod().when( baseTable ).populateTable( any( Object[][].class ) );

    final Object[][] values = new Object[][] {};
    baseTable.populateTable( values );
    verify( baseTable ).populateDataGrid( any( int[].class ), eq( values ) );
  }

  @Test
  public void testSetWidth() throws Exception {
    doCallRealMethod().when( baseTable ).setWidth( anyString() );

    final String width = "100px";
    baseTable.scrollTable = mock( ScrollTable.class );
    when( baseTable.scrollTable.getHeaderTable() ).thenReturn( mock( FixedWidthFlexTable.class ) );
    when( baseTable.scrollTable.getDataTable() ).thenReturn( mock( FixedWidthGrid.class ) );
    baseTable.setWidth( width );
    assertEquals( width, baseTable.scrollTableWidth );
    verify( baseTable.scrollTable.getHeaderTable() ).setWidth( width );
    verify( baseTable.scrollTable.getDataTable() ).setWidth( width );
  }

  @Test
  public void testSetHeight() throws Exception {
    doCallRealMethod().when( baseTable ).setHeight( anyString() );

    final String height = "100px";
    baseTable.scrollTable = mock( ScrollTable.class );
    baseTable.setHeight( height );
    assertEquals( height, baseTable.scrollTableHeight );
    verify( baseTable.scrollTable ).setHeight( height );
  }

  @Test
  public void testReplaceRow() throws Exception {
    doCallRealMethod().when( baseTable ).replaceRow( anyInt(), any( Object[].class ) );

    baseTable.dataGrid = mock( FixedWidthGrid.class );
    baseTable.replaceRow( 2, new Object[] {1, 2} );
    verify( baseTable.dataGrid, never() ).setHTML( anyInt(), anyInt(), anyString() );
    verify( baseTable.dataGrid, never() ).setWidget( anyInt(), anyInt(), any( Widget.class ) );

    baseTable.replaceRow( 2, new Object[] {"1", "2"} );
    verify( baseTable.dataGrid, times( 2 ) ).setHTML( anyInt(), anyInt(), anyString() );
    verify( baseTable.dataGrid, never() ).setWidget( anyInt(), anyInt(), any( Widget.class ) );

    baseTable.replaceRow( 2, new Object[] {mock( Widget.class ), mock( Widget.class )} );
    verify( baseTable.dataGrid, times( 2 ) ).setHTML( anyInt(), anyInt(), anyString() );
    verify( baseTable.dataGrid, times( 2 ) ).setWidget( anyInt(), anyInt(), any( Widget.class ) );
  }
}
