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

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ErrorLabelTest {

  @Test
  public void testSetErrorMsg() throws Exception {
    ErrorLabel errorLabel = new ErrorLabel( mock( Widget.class ) );
    errorLabel.errorLabel = spy( errorLabel.errorLabel );

    final String errorMsg = "error";
    errorLabel.setErrorMsg( errorMsg );
    verify( errorLabel.errorLabel ).setVisible( true );
    verify( errorLabel.errorLabel ).setText( errorMsg );

    errorLabel.setErrorMsg( "" );
    verify( errorLabel.errorLabel ).setVisible( false );
    verify( errorLabel.errorLabel ).setText( "" );
  }
}
