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

package org.pentaho.gwt.widgets.client.colorpicker;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ColorPaletteTest {

  @Test
  public void testInit() throws Exception {
    ColorPalette colorPalette = new ColorPalette();

    assertEquals( colorPalette.rows * colorPalette.cols, colorPalette.colorArray.length );
    for ( int i = 0; i < colorPalette.colorArray.length; i++ ) {
      assertEquals( colorPalette.DEFAULT_COLOR, colorPalette.colorArray[i].getHex().toUpperCase() );
    }
  }

  @Test
  public void testRefreshPalette() throws Exception {
    ColorPalette colorPalette = new ColorPalette();
    final Color color1 = new Color();
    color1.setRGB( 255, 0, 0 );
    final Color color2 = new Color();
    color1.setRGB( 0, 255, 0 );
    final Color color3 = new Color();
    color1.setRGB( 0, 0, 255 );
    colorPalette.colorArray = new Color[] { color1, color2, color3 };

    colorPalette.refreshPalette();
    assertEquals( colorPalette.rows * colorPalette.cols, colorPalette.colorArray.length );
    assertEquals( color1, colorPalette.colorArray[0] );
    assertEquals( color2, colorPalette.colorArray[1] );
    assertEquals( color3, colorPalette.colorArray[2] );
    for ( int i = 3; i < colorPalette.colorArray.length; i++ ) {
      assertEquals( colorPalette.DEFAULT_COLOR, colorPalette.colorArray[i].getHex().toUpperCase() );
    }
  }

  @Test
  public void testRefreshDisplay() throws Exception {
    ColorPalette colorPalette = spy( new ColorPalette() );
    colorPalette.mainPanel = spy( colorPalette.mainPanel );

    class Count {
      int count;
    }

    final Count count = new Count();

    when( colorPalette.mainPanel.getWidgetCount() ).thenAnswer( new Answer<Integer>() {
      @Override
      public Integer answer( InvocationOnMock invocationOnMock ) throws Throwable {
        return count.count;
      }
    } );
    when( colorPalette.mainPanel.remove( anyInt() ) ).thenAnswer( new Answer<Boolean>() {
      @Override
      public Boolean answer( InvocationOnMock invocationOnMock ) throws Throwable {
        count.count--;
        return true;
      }
    } );
    doAnswer( new Answer<Void>() {
      @Override
      public Void answer( InvocationOnMock invocationOnMock ) throws Throwable {
        count.count++;
        return null;
      }
    } ).when( colorPalette.mainPanel ).add( any( Widget.class ) );

    colorPalette.refreshDisplay();
    colorPalette.refreshDisplay();
    assertEquals( colorPalette.rows * colorPalette.cols, count.count );
  }
}
