/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.gwt.widgets.client.menuitem;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class CheckBoxMenuItemTest {

  @Test
  public void testSetChecked() throws Exception {
    final CheckBoxMenuItem item = mock( CheckBoxMenuItem.class );
    doCallRealMethod().when( item ).setChecked( anyBoolean() );

    item.setChecked( true );
    verify( item ).setStylePrimaryName( anyString() );

    item.setChecked( false );
    verify( item, times( 2 ) ).setStylePrimaryName( anyString() );
  }
}
