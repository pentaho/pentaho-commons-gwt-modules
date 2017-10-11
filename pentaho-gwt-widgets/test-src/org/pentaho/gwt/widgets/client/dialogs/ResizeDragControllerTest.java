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

package org.pentaho.gwt.widgets.client.dialogs;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ResizeDragControllerTest {
  @Test
  public void testMakeDraggable() throws Exception {
    final AbsolutePanel panel = mock( AbsolutePanel.class );
    ResizeDragController controller = spy( new ResizeDragController( panel ) );

    controller.makeDraggable( panel, WindowPanel.NORTH );

    assertEquals( 1, controller.directionMap.size() );
    assertEquals( panel, controller.directionMap.keySet().iterator().next() );
    assertEquals( WindowPanel.NORTH, controller.directionMap.values().iterator().next() );
  }
}
