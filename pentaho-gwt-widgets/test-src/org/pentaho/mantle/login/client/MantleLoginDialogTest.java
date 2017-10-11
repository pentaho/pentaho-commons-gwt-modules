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
