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
