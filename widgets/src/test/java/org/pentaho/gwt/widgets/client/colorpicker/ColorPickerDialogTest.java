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

package org.pentaho.gwt.widgets.client.colorpicker;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith( GwtMockitoTestRunner.class )
public class ColorPickerDialogTest {

  @Test
  public void testGetHexColor() throws Exception {
    ColorPickerDialog dialog = mock( ColorPickerDialog.class );
    doCallRealMethod().when( dialog ).getHexColor();

    final ColorPicker colorPicker = mock( ColorPicker.class );
    final String color = "12abdd";
    when( colorPicker.getHexColor() ).thenReturn( color );
    when( dialog.getContent() ).thenReturn( colorPicker );

    assertEquals( color, dialog.getHexColor() );
  }
}
