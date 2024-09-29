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
