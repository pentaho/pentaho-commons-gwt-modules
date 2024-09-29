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
