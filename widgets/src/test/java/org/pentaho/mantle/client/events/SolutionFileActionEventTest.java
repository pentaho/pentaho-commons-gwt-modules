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

package org.pentaho.mantle.client.events;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith( GwtMockitoTestRunner.class )
public class SolutionFileActionEventTest {

  @Test
  public void testDispatch() throws Exception {
    final SolutionFileActionEvent solutionFileActionEvent = mock( SolutionFileActionEvent.class );

    doCallRealMethod().when( solutionFileActionEvent ).dispatch( any( SolutionFileActionEventHandler.class ) );

    final SolutionFileActionEventHandler solutionFileActionEventHandler = mock( SolutionFileActionEventHandler.class );
    solutionFileActionEvent.dispatch( solutionFileActionEventHandler );
    verify( solutionFileActionEventHandler ).onFileAction( solutionFileActionEvent );
  }
}
