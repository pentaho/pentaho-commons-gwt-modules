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


package org.pentaho.gwt.widgets.client.controls;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedList;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ColorPickerTest {
  @Test
  public void testSetColor() throws Exception {
    ColorPicker cp = mock( ColorPicker.class );
    final ColorPickerListener colorPickerListener = mock( ColorPickerListener.class );
    cp.listeners = new LinkedList<ColorPickerListener>() { {
        add( colorPickerListener );
      } };
    doCallRealMethod().when( cp ).setColor( anyString() );
    final Element element = mock( Element.class );
    when( element.getStyle() ).thenReturn( mock( Style.class ) );
    when( cp.getElement() ).thenReturn( element );

    cp.setColor( "ffffff" );
    verify( colorPickerListener ).colorPicked( cp );
  }
}
