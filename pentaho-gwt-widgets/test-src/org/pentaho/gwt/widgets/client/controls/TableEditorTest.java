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
