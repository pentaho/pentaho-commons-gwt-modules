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

package org.pentaho.mantle.login.client;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class MantleLoginDialogTest {
  @Test
  public void testSetReturnLocation() throws Exception {
    final MantleLoginDialog mantleLoginDialog = mock( MantleLoginDialog.class );
    doCallRealMethod().when( mantleLoginDialog ).setReturnLocation( anyString() );

    final Widget widget = mock( Widget.class );
    when( mantleLoginDialog.buildLoginPanel( false ) ).thenReturn( widget );
    final String returnLocation = "returnLocation";
    mantleLoginDialog.setReturnLocation( returnLocation );
    assertEquals( returnLocation, mantleLoginDialog.returnLocation );
    verify( mantleLoginDialog ).setContent( widget );
  }
}
