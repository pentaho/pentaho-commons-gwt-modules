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
* Copyright (c) 2002-2023 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.gwt.widgets.client.toolbar;

import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.gwt.widgets.client.text.ToolTip;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith( GwtMockitoTestRunner.class )
public class ToolbarButtonTest {
  private ToolbarButton button;

  @Before
  public void setUp() throws Exception {
    button = mock( ToolbarButton.class );
    button.button = mock( DockPanel.class );
    button.imageRegistrations = new ArrayList<>();
  }

  @Test
  public void testSetId() throws Exception {
    doCallRealMethod().when( button ).setId( anyString() );

    final Element buttonElement = mock( Element.class );
    when( button.button.getElement() ).thenReturn( buttonElement );
    button.eventWrapper = mock( FocusPanel.class );
    final Element eventWrapperElement = mock( Element.class );
    when( button.eventWrapper.getElement() ).thenReturn( eventWrapperElement );

    final String id = "id";
    button.setId( id );
    verify( buttonElement ).setId( contains( id ) );
    verify( eventWrapperElement ).setId( eq( id ) );
  }

  @Test
  public void testSetStylePrimaryName() throws Exception {
    doCallRealMethod().when( button ).setStylePrimaryName( anyString() );

    final String styleName = "name";
    button.setStylePrimaryName( styleName );
    verify( button.button ).setStylePrimaryName( styleName );
  }

  @Test
  @SuppressWarnings( "deprecation" )
  public void testAddStyleMouseListener() throws Exception {
    doCallRealMethod().when( button ).addStyleMouseListener();

    button.eventWrapper = mock( FocusPanel.class );

    button.addStyleMouseListener();
    verify( button.eventWrapper ).addClickListener( any( ClickListener.class ) );
    verify( button.eventWrapper ).addMouseListener( any( MouseListener.class ) );
  }

  @Test
  public void testSetEnabled() throws Exception {
    doCallRealMethod().when( button ).setEnabled( anyBoolean() );

    final Image disabledImage = mock( Image.class );
    button.disabledImage = disabledImage;
    final Image currentImage = mock( Image.class );
    button.currentImage = currentImage;
    final Image image = mock( Image.class );
    button.image = image;
    button.eventWrapper = mock( FocusPanel.class );
    final Element eventWrapperElement = mock( Element.class );
    when( button.eventWrapper.getElement() ).thenReturn( eventWrapperElement );

    button.setEnabled( true );
    verify( button.button ).removeStyleName( contains( "disabled" ) );
    verify( button.button ).remove( currentImage );
    verify( button.button ).add( any( Image.class ), eq( DockPanel.CENTER ) );
    verify( button.button ).setCellHorizontalAlignment( image, DockPanel.ALIGN_CENTER );
    verify( button.button ).setCellVerticalAlignment( image, DockPanel.ALIGN_MIDDLE );

    button.setEnabled( false );
    verify( button.button ).addStyleName( contains( "disabled" ) );
    verify( button.button, times( 2 ) ).remove( currentImage );
    verify( button.button, times( 2 ) ).add( any( Image.class ), eq( DockPanel.CENTER ) );
    verify( button.button ).setCellHorizontalAlignment( disabledImage, DockPanel.ALIGN_CENTER );
    verify( button.button ).setCellVerticalAlignment( disabledImage, DockPanel.ALIGN_MIDDLE );
  }

  @Test
  public void testSetTempDisabled() throws Exception {
    doCallRealMethod().when( button ).setTempDisabled( anyBoolean() );

    button.enabled = true;
    button.setTempDisabled( true );
    verify( button.button ).setStyleName( contains( "disabled" ) );

    button.setTempDisabled( false );
    verify( button.button ).setStyleName( contains( "disabled" ) );

    button.enabled = false;
    button.setTempDisabled( true );
    verify( button.button, times( 2 ) ).setStyleName( contains( "disabled" ) );

    button.enabled = false;
    button.setTempDisabled( false );
    verify( button.button, times( 3 ) ).setStyleName( contains( "disabled" ) );
  }

  @Test
  public void testSetVisible() throws Exception {
    doCallRealMethod().when( button ).setVisible( anyBoolean() );
    button.eventWrapper = mock( FocusPanel.class );
    final Element eventWrapperElement = mock( Element.class );
    when( button.eventWrapper.getElement() ).thenReturn( eventWrapperElement );
    doNothing().when( button.eventWrapper ).setTabIndex( anyInt() );
    final boolean visible = true;
    button.setVisible( visible );
    verify( button.button ).setVisible( visible );
  }

  @Test
  public void testSetImage() throws Exception {
    doCallRealMethod().when( button ).setImage( any( Image.class ) );

    final Image currentImage = mock( Image.class );
    button.currentImage = currentImage;

    final Image image = mock( Image.class );
    final Image calcImage = mock( Image.class );
    when( button.calculateApporiateImage() ).thenReturn( calcImage );
    button.setImage( image );
    verify( button.button ).remove( currentImage );
    verify( button.button ).add( calcImage, DockPanel.CENTER );
    verify( button.button ).setCellHorizontalAlignment( calcImage, DockPanel.ALIGN_CENTER );
    verify( button.button ).setCellVerticalAlignment( calcImage, DockPanel.ALIGN_MIDDLE );

    verify( button ).updateImages();
  }

  @Test
  public void testSetImageAltText() throws Exception {
    // SETUP
    String altText = "Test Some Alternative Text";
    Image mockImage = mock( Image.class );
    ToolbarButton testInstance = new ToolbarButton( mockImage );

    // EXECUTE
    testInstance.setImageAltText( altText );

    // VERIFY
    verify( mockImage ).setAltText( altText );
  }

  @Test
  public void testSetImageAltText_nullImage() throws Exception {
    // SETUP
    Image mockImage = mock( Image.class );
    ToolbarButton testInstance = new ToolbarButton( null );

    // EXECUTE
    testInstance.setImageAltText( "testSomeText" );

    // VERIFY
    verify( mockImage, never() ).setAltText(  anyString() );
  }

  @Test
  public void testSetDisabledImage() throws Exception {
    doCallRealMethod().when( button ).setDisabledImage( any( Image.class ) );

    final Image currentImage = mock( Image.class );
    button.currentImage = currentImage;
    final Image calcImage = mock( Image.class );
    when( button.calculateApporiateImage() ).thenReturn( calcImage );

    final Image disabledImage = mock( Image.class );
    button.setDisabledImage( disabledImage );
    verify( button.button ).remove( currentImage );
    verify( button.button ).add( calcImage, DockPanel.CENTER );
    verify( button.button ).setCellHorizontalAlignment( calcImage, DockPanel.ALIGN_CENTER );
    verify( button.button ).setCellVerticalAlignment( calcImage, DockPanel.ALIGN_MIDDLE );

    verify( button ).updateImages();
  }

  @Test
  public void testSetText() throws Exception {
    doCallRealMethod().when( button ).setText( anyString() );

    button.label = mock( Label.class );
    final String text = "text";
    button.setText( text );
    verify( button.label ).setText( text );
  }

  @Test
  @SuppressWarnings( "deprecation" )
  public void testSetToolTip() throws Exception {
    doCallRealMethod().when( button ).setToolTip( anyString() );

    final ToolTip toolTipWidget = mock( ToolTip.class );
    button.toolTipWidget = toolTipWidget;
    final FocusPanel eventWrapper = mock( FocusPanel.class );
    button.eventWrapper = eventWrapper;

    final String tip = "tip";
    button.setToolTip( tip );
    verify( eventWrapper ).removeMouseListener( toolTipWidget );
    verify( eventWrapper ).addMouseListener( any( MouseListener.class ) );
  }

  @Test
  public void testCalculateApporiateImage() throws Exception {
    doCallRealMethod().when( button ).calculateApporiateImage();

    final Image image = mock( Image.class );
    final Image disabledImage = mock( Image.class );
    button.enabled = true;
    button.disabledImage = null;
    button.image = image;
    assertEquals( image, button.calculateApporiateImage() );

    button.enabled = false;
    assertEquals( image, button.calculateApporiateImage() );

    button.disabledImage = disabledImage;
    assertEquals( disabledImage, button.calculateApporiateImage() );
  }

  @Test
  public void testAddClassName() throws Exception {
    doCallRealMethod().when( button ).addClassName( anyString() );
    doCallRealMethod().when( button ).updateImages();
    doCallRealMethod().when( button ).updateImg( any( Image.class ), anyBoolean() );

    final Image image = mock( Image.class );
    button.image = image;
    final Image disabledImage = mock( Image.class );
    button.disabledImage = disabledImage;
    final String className = "name";
    button.addClassName( className );

    verify( image ).addStyleName( className );
    verify( image ).removeStyleName( contains( "disabled" ) );
    verify( image ).addMouseOverHandler( any( MouseOverHandler.class ) );
    verify( image ).addMouseOutHandler( any( MouseOutHandler.class ) );
    verify( image ).addMouseDownHandler( any( MouseDownHandler.class ) );
    verify( image ).addMouseUpHandler( any( MouseUpHandler.class ) );

    verify( disabledImage ).addStyleName( className );
    verify( disabledImage ).addStyleName( contains( "disabled" ) );
    verify( disabledImage ).addMouseOverHandler( any( MouseOverHandler.class ) );
    verify( disabledImage ).addMouseOutHandler( any( MouseOutHandler.class ) );
    verify( disabledImage ).addMouseDownHandler( any( MouseDownHandler.class ) );
    verify( disabledImage ).addMouseUpHandler( any( MouseUpHandler.class ) );
  }

  @Test
  public void testGetImageAltText() throws Exception {

    // SETUP 1 : null image
    ToolbarButton testInstance1 = new ToolbarButton( null );

    // EXECUTE & VERIFY
    assertNull( testInstance1.getImageAltText() );

    // SETUP 2: non null image
    String altText = "Test Some Alternative Text";
    Image mockImage = mock( Image.class );
    ToolbarButton testInstance2 = new ToolbarButton( mockImage );
    when( mockImage.getAltText() ).thenReturn( altText );

    // EXECUTE & VERIFY
    assertEquals( altText, testInstance2.getImageAltText() );

  }
}
