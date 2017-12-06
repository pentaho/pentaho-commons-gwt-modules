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

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ResizableDialogDragHandlerTest {

  private ResizableDialogDragHandler handler;

  @Before
  public void setUp() throws Exception {
    final HTML handlerHTML = mock( HTML.class );
    when( handlerHTML.getHTML() ).thenReturn( "" );
    handler = spy( new ResizableDialogDragHandler( handlerHTML ) );
  }

  @Test
  public void testOnDragEnd() throws Exception {
    handler.onDragEnd( mock( DragEndEvent.class ) );
    verify( handler, never() ).clear();
    verify( handler ).log( anyString(), eq( ResizableDialogDragHandler.RED ) );
  }

  @Test
  public void testOnDragStart() throws Exception {
    handler.onDragStart( mock( DragStartEvent.class ) );
    verify( handler, never() ).clear();
    verify( handler ).log( anyString(), eq( ResizableDialogDragHandler.GREEN ) );
  }

  @Test
  public void testOnPreviewDragEnd() throws Exception {
    handler.onPreviewDragEnd( mock( DragEndEvent.class ) );
    verify( handler, never() ).clear();
    verify( handler ).log( anyString(), eq( ResizableDialogDragHandler.BLUE ) );
  }

  @Test
  public void testOnPreviewDragStart() throws Exception {
    handler.onPreviewDragStart( mock( DragStartEvent.class ) );
    verify( handler ).clear();
    verify( handler ).log( anyString(), eq( ResizableDialogDragHandler.BLUE ) );
  }
}
