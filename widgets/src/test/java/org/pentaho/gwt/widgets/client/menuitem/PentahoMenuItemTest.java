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

package org.pentaho.gwt.widgets.client.menuitem;

import com.google.gwt.user.client.Command;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class PentahoMenuItemTest {

  private PentahoMenuItem item;

  @Before
  public void setUp() throws Exception {
    item = mock( PentahoMenuItem.class );
  }

  @Test
  public void testSetEnabled() throws Exception {
    doCallRealMethod().when( item ).setEnabled( anyBoolean() );
    doCallRealMethod().when( item ).updateStyles();

    item.setEnabled( true );
    verify( item ).setStyleName( anyString() );

    item.useCheckUI = true;
    item.setEnabled( false );
    verify( item, times( 2 ) ).setStyleName( anyString() );
  }

  @Test
  public void testSetChecked() throws Exception {
    doCallRealMethod().when( item ).setChecked( anyBoolean() );
    doCallRealMethod().when( item ).updateStyles();

    item.enabled = true;
    item.setChecked( true );
    verify( item ).setStyleName( anyString() );

    item.enabled = false;
    item.setChecked( true );
    verify( item, times( 2 ) ).setStyleName( anyString() );
  }

  @Test
  public void testGetCommand() throws Exception {
    doCallRealMethod().when( item ).getCommand();
    doCallRealMethod().when( item ).setCommand( any( Command.class  ) );

    final Command command = mock( Command.class );
    item.setCommand( command );

    when( item.isEnabled() ).thenReturn( false );
    assertNull( item.getCommand() );

    when( item.isEnabled() ).thenReturn( true );
    assertEquals( command, item.getCommand() );
  }
}
