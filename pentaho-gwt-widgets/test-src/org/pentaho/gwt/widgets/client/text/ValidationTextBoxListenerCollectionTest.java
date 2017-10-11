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

package org.pentaho.gwt.widgets.client.text;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith( GwtMockitoTestRunner.class )
public class ValidationTextBoxListenerCollectionTest {

  @Test
  public void testFireOnSuccess() throws Exception {
    final ValidationTextBoxListenerCollection collection = new ValidationTextBoxListenerCollection();
    final IValidationTextBoxListener listener = mock( IValidationTextBoxListener.class );
    collection.add( listener );

    final Widget widget = mock( Widget.class );
    collection.fireOnSuccess( widget );
    verify( listener ).onSuccess( widget );
  }

  @Test
  public void testFireOnFailure() throws Exception {
    final ValidationTextBoxListenerCollection collection = new ValidationTextBoxListenerCollection();
    final IValidationTextBoxListener listener = mock( IValidationTextBoxListener.class );
    collection.add( listener );

    final Widget widget = mock( Widget.class );
    collection.fireOnFailure( widget );
    verify( listener ).onFailure( widget );
  }
}
