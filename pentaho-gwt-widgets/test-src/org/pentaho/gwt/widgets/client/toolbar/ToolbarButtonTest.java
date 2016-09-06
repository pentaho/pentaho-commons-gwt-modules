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
* Copyright (c) 2002-2016 Pentaho Corporation..  All rights reserved.
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

import org.mockito.Mockito;
import org.junit.Assert;

@RunWith( GwtMockitoTestRunner.class )
public class ToolbarButtonTest {
  private ToolbarButton button;

  @Before
  public void setUp() throws Exception {
    button = Mockito.mock( ToolbarButton.class );
    button.button = Mockito.mock( DockPanel.class );
  }

  @Test
  public void testSetId() throws Exception {
    Mockito.doCallRealMethod().when( button ).setId( Mockito.anyString() );

    final Element buttonElement = Mockito.mock( Element.class );
    Mockito.when( button.button.getElement() ).thenReturn( buttonElement );
    button.eventWrapper = Mockito.mock( FocusPanel.class );
    final Element eventWrapperElement = Mockito.mock( Element.class );
    Mockito.when( button.eventWrapper.getElement() ).thenReturn( eventWrapperElement );

    final String id = "id";
    button.setId( id );
    Mockito.verify( buttonElement ).setId( Mockito.contains( id ) );
    Mockito.verify( eventWrapperElement ).setId( Mockito.eq( id ) );
  }

  @Test
  public void testSetStylePrimaryName() throws Exception {
    Mockito.doCallRealMethod().when( button ).setStylePrimaryName( Mockito.anyString() );

    final String styleName = "name";
    button.setStylePrimaryName( styleName );
    Mockito.verify( button.button ).setStylePrimaryName( styleName );
  }

  @Test
  @SuppressWarnings( "deprecation" )
  public void testAddStyleMouseListener() throws Exception {
    Mockito.doCallRealMethod().when( button ).addStyleMouseListener();

    button.eventWrapper = Mockito.mock( FocusPanel.class );

    button.addStyleMouseListener();
    Mockito.verify( button.eventWrapper ).addClickListener( Mockito.any( ClickListener.class ) );
    Mockito.verify( button.eventWrapper ).addMouseListener( Mockito.any( MouseListener.class ) );
  }

  @Test
  public void testSetEnabled() throws Exception {
    Mockito.doCallRealMethod().when( button ).setEnabled( Mockito.anyBoolean() );

    final Image disabledImage = Mockito.mock( Image.class );
    button.disabledImage = disabledImage;
    final Image currentImage = Mockito.mock( Image.class );
    button.currentImage = currentImage;
    final Image image = Mockito.mock( Image.class );
    button.image = image;
    button.eventWrapper = Mockito.mock( FocusPanel.class );

    button.setEnabled( true );
    Mockito.verify( button.button ).removeStyleName( Mockito.contains( "disabled" ) );
    Mockito.verify( button.button ).remove( currentImage );
    Mockito.verify( button.button ).add( Mockito.any( Image.class ), Mockito.eq( DockPanel.CENTER ) );
    Mockito.verify( button.button ).setCellHorizontalAlignment( image, DockPanel.ALIGN_CENTER );
    Mockito.verify( button.button ).setCellVerticalAlignment( image, DockPanel.ALIGN_MIDDLE );

    button.setEnabled( false );
    Mockito.verify( button.button ).addStyleName( Mockito.contains( "disabled" ) );
    Mockito.verify( button.button, Mockito.times( 2 ) ).remove( currentImage );
    Mockito.verify( button.button, Mockito.times( 2 ) ).add( Mockito.any( Image.class ), Mockito.eq( DockPanel.CENTER ) );
    Mockito.verify( button.button ).setCellHorizontalAlignment( disabledImage, DockPanel.ALIGN_CENTER );
    Mockito.verify( button.button ).setCellVerticalAlignment( disabledImage, DockPanel.ALIGN_MIDDLE );
  }

  @Test
  public void testSetTempDisabled() throws Exception {
    Mockito.doCallRealMethod().when( button ).setTempDisabled( Mockito.anyBoolean() );

    button.enabled = true;
    button.setTempDisabled( true );
    Mockito.verify( button.button ).setStyleName( Mockito.contains( "disabled" ) );

    button.setTempDisabled( false );
    Mockito.verify( button.button ).setStyleName( Mockito.contains( "disabled" ) );

    button.enabled = false;
    button.setTempDisabled( true );
    Mockito.verify( button.button, Mockito.times( 2 ) ).setStyleName( Mockito.contains( "disabled" ) );

    button.enabled = false;
    button.setTempDisabled( false );
    Mockito.verify( button.button, Mockito.times( 3 ) ).setStyleName( Mockito.contains( "disabled" ) );
  }

  @Test
  public void testSetVisible() throws Exception {
    Mockito.doCallRealMethod().when( button ).setVisible( Mockito.anyBoolean() );

    final boolean visible = true;
    button.setVisible( visible );
    Mockito.verify( button.button ).setVisible( visible );
  }

  @Test
  public void testSetImage() throws Exception {
    Mockito.doCallRealMethod().when( button ).setImage( Mockito.any( Image.class ) );

    final Image currentImage = Mockito.mock( Image.class );
    button.currentImage = currentImage;

    final Image image = Mockito.mock( Image.class );
    final Image calcImage = Mockito.mock( Image.class );
    Mockito.when( button.calculateApporiateImage() ).thenReturn( calcImage );
    button.setImage( image );
    Mockito.verify( button.button ).remove( currentImage );
    Mockito.verify( button.button ).add( calcImage, DockPanel.CENTER );
    Mockito.verify( button.button ).setCellHorizontalAlignment( calcImage, DockPanel.ALIGN_CENTER );
    Mockito.verify( button.button ).setCellVerticalAlignment( calcImage, DockPanel.ALIGN_MIDDLE );
  }

  @Test
  public void testSetDisabledImage() throws Exception {
    Mockito.doCallRealMethod().when( button ).setDisabledImage( Mockito.any( Image.class ) );

    final Image currentImage = Mockito.mock( Image.class );
    button.currentImage = currentImage;
    final Image calcImage = Mockito.mock( Image.class );
    Mockito.when( button.calculateApporiateImage() ).thenReturn( calcImage );

    final Image disabledImage = Mockito.mock( Image.class );
    button.setDisabledImage( disabledImage );
    Mockito.verify( button.button ).remove( currentImage );
    Mockito.verify( button.button ).add( calcImage, DockPanel.CENTER );
    Mockito.verify( button.button ).setCellHorizontalAlignment( calcImage, DockPanel.ALIGN_CENTER );
    Mockito.verify( button.button ).setCellVerticalAlignment( calcImage, DockPanel.ALIGN_MIDDLE );
  }

  @Test
  public void testSetText() throws Exception {
    Mockito.doCallRealMethod().when( button ).setText( Mockito.anyString() );

    button.label = Mockito.mock( Label.class );
    final String text = "text";
    button.setText( text );
    Mockito.verify( button.label ).setText( text );
  }

  @Test
  @SuppressWarnings( "deprecation" )
  public void testSetToolTip() throws Exception {
    Mockito.doCallRealMethod().when( button ).setToolTip( Mockito.anyString() );

    final ToolTip toolTipWidget = Mockito.mock( ToolTip.class );
    button.toolTipWidget = toolTipWidget;
    final FocusPanel eventWrapper = Mockito.mock( FocusPanel.class );
    button.eventWrapper = eventWrapper;

    final String tip = "tip";
    button.setToolTip( tip );
    Mockito.verify( eventWrapper ).removeMouseListener( toolTipWidget );
    Mockito.verify( eventWrapper ).addMouseListener( Mockito.any( MouseListener.class ) );
  }

  @Test
  public void testCalculateApporiateImage() throws Exception {
    Mockito.doCallRealMethod().when( button ).calculateApporiateImage();

    final Image image = Mockito.mock( Image.class );
    final Image disabledImage = Mockito.mock( Image.class );
    button.enabled = true;
    button.disabledImage = null;
    button.image = image;
    Assert.assertEquals( image, button.calculateApporiateImage() );

    button.enabled = false;
    Assert.assertEquals( image, button.calculateApporiateImage() );

    button.disabledImage = disabledImage;
    Assert.assertEquals( disabledImage, button.calculateApporiateImage() );
  }

  @Test
  public void testAddClassName() throws Exception {
    Mockito.doCallRealMethod().when( button ).addClassName( Mockito.anyString() );

    final Image image = Mockito.mock( Image.class );
    button.image = image;
    final Image disabledImage = Mockito.mock( Image.class );
    button.disabledImage = disabledImage;
    final String className = "name";
    button.addClassName( className );
    Mockito.verify( image ).addStyleName( className );
    Mockito.verify( image ).removeStyleName( Mockito.contains( "disabled" ) );
    Mockito.verify( image ).addMouseOverHandler( Mockito.any( MouseOverHandler.class ) );
    Mockito.verify( image ).addMouseOutHandler( Mockito.any( MouseOutHandler.class ) );
    Mockito.verify( image ).addMouseDownHandler( Mockito.any( MouseDownHandler.class ) );
    Mockito.verify( image ).addMouseUpHandler( Mockito.any( MouseUpHandler.class ) );

    Mockito.verify( disabledImage ).addStyleName( className );
    Mockito.verify( disabledImage ).addStyleName( Mockito.contains( "disabled" ) );
    Mockito.verify( disabledImage ).addMouseOverHandler( Mockito.any( MouseOverHandler.class ) );
    Mockito.verify( disabledImage ).addMouseOutHandler( Mockito.any( MouseOutHandler.class ) );
    Mockito.verify( disabledImage ).addMouseDownHandler( Mockito.any( MouseDownHandler.class ) );
    Mockito.verify( disabledImage ).addMouseUpHandler( Mockito.any( MouseUpHandler.class ) );
  }
}
