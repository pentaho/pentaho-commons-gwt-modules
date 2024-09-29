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


package org.pentaho.gwt.widgets.client.controls;

import com.google.gwt.user.client.ui.ListBox;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class TableEditorTest {
  @Test
  public void testRemoveSelectedItems() throws Exception {
    TableEditor te = spy( new TableEditor( "text" ) );

    te.actionLb = mock( ListBox.class );
    when( te.getItemCount() ).thenReturn( 10 );
    when( te.actionLb.isItemSelected( anyInt() ) ).thenReturn( false );
    when( te.actionLb.isItemSelected( 3 ) ).thenReturn( true );
    when( te.actionLb.isItemSelected( 7 ) ).thenReturn( true );

    te.removeSelectedItems();

    verify( te.actionLb, times( 2 ) ).removeItem( anyInt() );
    verify( te.actionLb ).removeItem( 3 );
    verify( te.actionLb ).removeItem( 7 );
  }

  @Test
  public void testGetNumSelectedItems() throws Exception {
    TableEditor te = spy( new TableEditor( "text" ) );
    te.actionLb = mock( ListBox.class );
    when( te.getItemCount() ).thenReturn( 10 );
    when( te.actionLb.isItemSelected( anyInt() ) ).thenReturn( false );
    when( te.actionLb.isItemSelected( 3 ) ).thenReturn( true );
    when( te.actionLb.isItemSelected( 7 ) ).thenReturn( true );

    assertEquals( 2, te.getNumSelectedItems() );
  }

  @Test
  public void testClearMessage() throws Exception {
    String message = "message";
    TableEditor te = spy( new TableEditor( "text" ) );
    te.actionLb = mock( ListBox.class );
    when( te.actionLb.getItemCount() ).thenReturn( 10 );
    when( te.actionLb.getItemText( anyInt() ) ).thenReturn( "" );
    when( te.actionLb.getItemText( 3 ) ).thenReturn( message );

    te.setMessage( message );

    te.clearMessage();

    verify( te.actionLb ).removeItem( 3 );
  }
}
