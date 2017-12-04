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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ColorPickerTest {
  @Test
  public void testOnAttach() throws Exception {
    final ColorPicker cp = mock( ColorPicker.class );
    doCallRealMethod().when( cp ).onAttach();
    doNothing().when( cp ).updateSliders();

    cp.onAttach();
    verify( cp ).updateSliders();
    assertEquals( -1, cp.colorMode );
  }

  @Test
  public void testOnMapSelected() throws Exception {
    final ColorPicker cp = mock( ColorPicker.class );
    doCallRealMethod().when( cp ).onMapSelected( anyInt(), anyInt() );

    cp.tbSaturation = mock( TextBox.class );
    cp.tbBrightness = mock( TextBox.class );
    cp.tbHue = mock( TextBox.class );
    cp.tbRed = mock( TextBox.class );
    cp.tbGreen = mock( TextBox.class );
    cp.tbBlue = mock( TextBox.class );

    cp.colorMode = SliderMap.Hue;
    cp.onMapSelected( 1, 1 );
    verify( cp ).onChange( cp.tbHue );

    cp.colorMode = SliderMap.Saturation;
    cp.onMapSelected( 1, 1 );
    verify( cp ).onChange( cp.tbSaturation );

    cp.colorMode = SliderMap.Brightness;
    cp.onMapSelected( 1, 1 );
    verify( cp ).onChange( cp.tbBrightness );

    cp.colorMode = SliderMap.Red;
    cp.onMapSelected( 1, 1 );
    verify( cp ).onChange( cp.tbRed );

    cp.colorMode = SliderMap.Green;
    cp.onMapSelected( 1, 1 );
    verify( cp ).onChange( cp.tbGreen );

    cp.colorMode = SliderMap.Blue;
    cp.onMapSelected( 1, 1 );
    verify( cp ).onChange( cp.tbBlue );
  }

  @Test
  public void testOnBarSelected() throws Exception {
    final ColorPicker cp = mock( ColorPicker.class );
    doCallRealMethod().when( cp ).onBarSelected( anyInt() );

    cp.tbSaturation = mock( TextBox.class );
    cp.tbBrightness = mock( TextBox.class );
    cp.tbHue = mock( TextBox.class );
    cp.tbRed = mock( TextBox.class );
    cp.tbGreen = mock( TextBox.class );
    cp.tbBlue = mock( TextBox.class );

    cp.colorMode = SliderMap.Hue;
    cp.onBarSelected( 1 );
    verify( cp ).onChange( cp.tbHue );

    cp.colorMode = SliderMap.Saturation;
    cp.onBarSelected( 1 );
    verify( cp ).onChange( cp.tbSaturation );

    cp.colorMode = SliderMap.Brightness;
    cp.onBarSelected( 1 );
    verify( cp ).onChange( cp.tbBrightness );

    cp.colorMode = SliderMap.Red;
    cp.onBarSelected( 1 );
    verify( cp ).onChange( cp.tbRed );

    cp.colorMode = SliderMap.Green;
    cp.onBarSelected( 1 );
    verify( cp ).onChange( cp.tbGreen );

    cp.colorMode = SliderMap.Blue;
    cp.onBarSelected( 1 );
    verify( cp ).onChange( cp.tbBlue );
  }

  @Test
  public void testOnClickHue() throws Exception {
    final ColorPicker cp = mock( ColorPicker.class );
    doCallRealMethod().when( cp ).onClick( any( Widget.class ) );

    cp.slidermap = mock( SliderMap.class );
    cp.sliderbar = mock( SliderBar.class );
    cp.rbHue = mock( RadioButton.class );

    cp.onClick( cp.rbHue );
    assertEquals( SliderMap.Hue, cp.colorMode );
    verify( cp.sliderbar ).setColorSelectMode( SliderMap.Hue );
    verify( cp.slidermap ).setColorSelectMode( SliderMap.Hue );
    verify( cp.slidermap ).setOverlayColor( anyString() );
    verify( cp.slidermap ).setSliderPosition( anyInt(), anyInt() );
    verify( cp.sliderbar ).setSliderPosition( anyInt() );
  }

  @Test
  public void testOnClickSaturation() throws Exception {
    final ColorPicker cp = mock( ColorPicker.class );
    doCallRealMethod().when( cp ).onClick( any( Widget.class ) );

    cp.slidermap = mock( SliderMap.class );
    cp.sliderbar = mock( SliderBar.class );
    cp.rbSaturation = mock( RadioButton.class );
    cp.tbHexColor = mock( TextBox.class );

    cp.onClick( cp.rbSaturation );
    assertEquals( SliderMap.Saturation, cp.colorMode );
    verify( cp.sliderbar ).setColorSelectMode( SliderMap.Saturation );
    verify( cp.slidermap ).setColorSelectMode( SliderMap.Saturation );
    verify( cp.slidermap ).setOverlayColor( anyString() );
    verify( cp.slidermap ).setSliderPosition( anyInt(), anyInt() );
    verify( cp.sliderbar ).setSliderPosition( anyInt() );
  }

  @Test
  public void testOnClickBrightness() throws Exception {
    final ColorPicker cp = mock( ColorPicker.class );
    doCallRealMethod().when( cp ).onClick( any( Widget.class ) );

    cp.slidermap = mock( SliderMap.class );
    cp.sliderbar = mock( SliderBar.class );
    cp.rbBrightness = mock( RadioButton.class );

    cp.onClick( cp.rbBrightness );
    assertEquals( SliderMap.Brightness, cp.colorMode );
    verify( cp.sliderbar ).setColorSelectMode( SliderMap.Brightness );
    verify( cp.slidermap ).setColorSelectMode( SliderMap.Brightness );
    verify( cp.slidermap ).setOverlayColor( anyString() );
    verify( cp.slidermap ).setSliderPosition( anyInt(), anyInt() );
    verify( cp.sliderbar ).setSliderPosition( anyInt() );
  }

  @Test
  public void testOnClickRed() throws Exception {
    final ColorPicker cp = mock( ColorPicker.class );
    doCallRealMethod().when( cp ).onClick( any( Widget.class ) );

    cp.slidermap = mock( SliderMap.class );
    cp.sliderbar = mock( SliderBar.class );
    cp.rbRed = mock( RadioButton.class );

    cp.onClick( cp.rbRed );
    assertEquals( SliderMap.Red, cp.colorMode );
    verify( cp.sliderbar ).setColorSelectMode( SliderMap.Red );
    verify( cp.slidermap ).setColorSelectMode( SliderMap.Red );
    verify( cp.slidermap ).setOverlayOpacity( anyInt() );
    verify( cp.slidermap ).setSliderPosition( anyInt(), anyInt() );
    verify( cp.sliderbar ).setSliderPosition( anyInt() );
    verify( cp.sliderbar, times( 4 ) ).setLayerOpacity( anyInt(), anyInt() );
  }

  @Test
  public void testOnClickGreen() throws Exception {
    final ColorPicker cp = mock( ColorPicker.class );
    doCallRealMethod().when( cp ).onClick( any( Widget.class ) );

    cp.slidermap = mock( SliderMap.class );
    cp.sliderbar = mock( SliderBar.class );
    cp.rbGreen = mock( RadioButton.class );

    cp.onClick( cp.rbGreen );
    assertEquals( SliderMap.Green, cp.colorMode );
    verify( cp.sliderbar ).setColorSelectMode( SliderMap.Green );
    verify( cp.slidermap ).setColorSelectMode( SliderMap.Green );
    verify( cp.slidermap ).setOverlayOpacity( anyInt() );
    verify( cp.slidermap ).setSliderPosition( anyInt(), anyInt() );
    verify( cp.sliderbar ).setSliderPosition( anyInt() );
    verify( cp.sliderbar, times( 4 ) ).setLayerOpacity( anyInt(), anyInt() );
  }

  @Test
  public void testOnClickBlue() throws Exception {
    final ColorPicker cp = mock( ColorPicker.class );
    doCallRealMethod().when( cp ).onClick( any( Widget.class ) );

    cp.slidermap = mock( SliderMap.class );
    cp.sliderbar = mock( SliderBar.class );
    cp.rbBlue = mock( RadioButton.class );

    cp.onClick( cp.rbBlue );
    assertEquals( SliderMap.Blue, cp.colorMode );
    verify( cp.sliderbar ).setColorSelectMode( SliderMap.Blue );
    verify( cp.slidermap ).setColorSelectMode( SliderMap.Blue );
    verify( cp.slidermap ).setOverlayOpacity( anyInt() );
    verify( cp.slidermap ).setSliderPosition( anyInt(), anyInt() );
    verify( cp.sliderbar ).setSliderPosition( anyInt() );
    verify( cp.sliderbar, times( 4 ) ).setLayerOpacity( anyInt(), anyInt() );
  }

  @Test
   public void testOnChangeHexColor() throws Exception {
    final ColorPicker cp = mock( ColorPicker.class );
    doCallRealMethod().when( cp ).onChange( any( Widget.class ) );

    cp.tbHexColor = mock( TextBox.class );

    cp.onChange( cp.tbHexColor );

    verify( cp ).updateSliders();
  }

  @Test
  public void testOnChangeRGB() throws Exception {
    final ColorPicker cp = mock( ColorPicker.class );
    doCallRealMethod().when( cp ).onChange( any( Widget.class ) );

    cp.tbSaturation = mock( TextBox.class );
    when( cp.tbSaturation.getText() ).thenReturn( "100" );
    cp.tbBrightness = mock( TextBox.class );
    when( cp.tbBrightness.getText() ).thenReturn( "100" );
    cp.tbHue = mock( TextBox.class );
    when( cp.tbHue.getText() ).thenReturn( "0" );
    cp.tbRed = mock( TextBox.class );
    cp.tbGreen = mock( TextBox.class );
    when( cp.tbGreen.getText() ).thenReturn( "121" );
    cp.tbBlue = mock( TextBox.class );
    when( cp.tbBlue.getText() ).thenReturn( "121" );

    when( cp.tbRed.getText() ).thenReturn( "121" );
    cp.onChange( cp.tbRed );
    verify( cp.tbRed, never() ).setText( anyString() );

    when( cp.tbRed.getText() ).thenReturn( "-5" );
    cp.onChange( cp.tbRed );
    verify( cp.tbRed ).setText( "0" );

    when( cp.tbRed.getText() ).thenReturn( "350" );
    cp.onChange( cp.tbRed );
    verify( cp.tbRed ).setText( "255" );

    verify( cp, times( 3 ) ).updateSliders();
  }

  @Test
  public void testOnChangeHSV() throws Exception {
    final ColorPicker cp = mock( ColorPicker.class );
    doCallRealMethod().when( cp ).onChange( any( Widget.class ) );

    cp.tbSaturation = mock( TextBox.class );
    cp.tbBrightness = mock( TextBox.class );
    cp.tbHue = mock( TextBox.class );
    cp.tbGreen = mock( TextBox.class );
    when( cp.tbGreen.getText() ).thenReturn( "121" );
    cp.tbBlue = mock( TextBox.class );
    when( cp.tbBlue.getText() ).thenReturn( "121" );
    cp.tbRed = mock( TextBox.class );
    when( cp.tbRed.getText() ).thenReturn( "121" );

    when( cp.tbHue.getText() ).thenReturn( "0" );
    when( cp.tbSaturation.getText() ).thenReturn( "100" );
    when( cp.tbBrightness.getText() ).thenReturn( "100" );
    cp.onChange( cp.tbHue );
    verify( cp.tbHue, never() ).setText( anyString() );
    verify( cp.tbSaturation, never() ).setText( anyString() );
    verify( cp.tbBrightness, never() ).setText( anyString() );

    when( cp.tbHue.getText() ).thenReturn( "500" );
    when( cp.tbSaturation.getText() ).thenReturn( "200" );
    when( cp.tbBrightness.getText() ).thenReturn( "200" );
    cp.onChange( cp.tbHue );
    verify( cp.tbHue ).setText( "359" );
    verify( cp.tbSaturation ).setText( "100" );
    verify( cp.tbBrightness ).setText( "100" );

    verify( cp, times( 2 ) ).updateSliders();
  }

  @Test
  public void testOnKeyPress() throws Exception {
    final ColorPicker cp = mock( ColorPicker.class );
    doCallRealMethod().when( cp ).onKeyPress( any( Widget.class ), anyChar(), anyInt() );

    // test hex color
    cp.tbHexColor = mock( TextBox.class );

    String okHexColor = "23afDD";

    for ( char c : okHexColor.toCharArray() ) {
      cp.onKeyPress( cp.tbHexColor, c, 0 );
    }
    verify( cp.tbHexColor, never() ).cancelKey();

    String badHexColor = "hklfj";

    for ( char c : badHexColor.toCharArray() ) {
      cp.onKeyPress( cp.tbHexColor, c, 0 );
    }
    verify( cp.tbHexColor, atLeastOnce() ).cancelKey();

    // test RGB color
    cp.tbRed = mock( TextBox.class );

    String okRedColor = "123";
    for ( char c : okRedColor.toCharArray() ) {
      cp.onKeyPress( cp.tbRed, c, 0 );
    }
    verify( cp.tbRed, never() ).cancelKey();

    String badRedColor = "fsd";
    for ( char c : badRedColor.toCharArray() ) {
      cp.onKeyPress( cp.tbRed, c, 0 );
    }
    verify( cp.tbRed, atLeastOnce() ).cancelKey();
  }

  @Test
  public void testSetRGB() throws Exception {
    final ColorPicker cp = mock( ColorPicker.class );
    doCallRealMethod().when( cp ).setRGB( anyInt(), anyInt(), anyInt() );

    cp.tbSaturation = mock( TextBox.class );
    cp.tbBrightness = mock( TextBox.class );
    cp.tbHue = mock( TextBox.class );
    cp.tbRed = mock( TextBox.class );
    cp.tbGreen = mock( TextBox.class );
    cp.tbBlue = mock( TextBox.class );
    cp.tbHexColor = mock( TextBox.class );
    cp.colorpreview = mock( HTML.class );

    cp.setRGB( 120, 20, 44 );
    verify( cp ).updateSliders();
  }

  @Test
  public void testSetHSV() throws Exception {
    final ColorPicker cp = mock( ColorPicker.class );
    doCallRealMethod().when( cp ).setHSV( anyInt(), anyInt(), anyInt() );

    cp.tbSaturation = mock( TextBox.class );
    cp.tbBrightness = mock( TextBox.class );
    cp.tbHue = mock( TextBox.class );
    cp.tbRed = mock( TextBox.class );
    cp.tbGreen = mock( TextBox.class );
    cp.tbBlue = mock( TextBox.class );
    cp.tbHexColor = mock( TextBox.class );
    cp.colorpreview = mock( HTML.class );

    cp.setHSV( 0, 100, 100 );
    verify( cp ).updateSliders();
  }

  @Test
  public void testSetHex() throws Exception {
    final ColorPicker cp = mock( ColorPicker.class );
    doCallRealMethod().when( cp ).setHex( anyString() );

    cp.tbSaturation = mock( TextBox.class );
    cp.tbBrightness = mock( TextBox.class );
    cp.tbHue = mock( TextBox.class );
    cp.tbRed = mock( TextBox.class );
    cp.tbGreen = mock( TextBox.class );
    cp.tbBlue = mock( TextBox.class );
    cp.tbHexColor = mock( TextBox.class );
    cp.colorpreview = mock( HTML.class );

    cp.setHex( "#12abdd" );
    verify( cp ).updateSliders();
  }
}
