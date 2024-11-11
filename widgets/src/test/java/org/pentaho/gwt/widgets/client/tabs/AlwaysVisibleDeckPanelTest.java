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


package org.pentaho.gwt.widgets.client.tabs;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class AlwaysVisibleDeckPanelTest {

  @Test
  public void testShowWidget() throws Exception {
    final AlwaysVisibleDeckPanel panel = mock( AlwaysVisibleDeckPanel.class );
    doCallRealMethod().when( panel ).showWidget( anyInt() );

    final int index = 2;
    final Widget oldWidget = mock( Widget.class );
    panel.curWidget = oldWidget;

    final Widget newWidget = mock( Widget.class );
    final Element element = mock( Element.class );
    final Element parentElement = mock( Element.class );
    final Style style = mock( Style.class );
    when( parentElement.getStyle() ).thenReturn( style );
    when( element.getParentElement() ).thenReturn( parentElement );
    when( newWidget.getElement() ).thenReturn( element );
    when( panel.getWidget( index ) ).thenReturn( newWidget );
    panel.showWidget( index );
    verify( panel ).moveOffscreen( oldWidget );
    verify( style, times( 4 ) ).setProperty( anyString(), anyString() );
  }

  @Test
  public void testAdd() throws Exception {
    final AlwaysVisibleDeckPanel panel = mock( AlwaysVisibleDeckPanel.class );
    doCallRealMethod().when( panel ).add( any( Widget.class ) );

    final Widget widget = mock( Widget.class );
    final Element element = mock( Element.class );
    final Element parentElement = mock( Element.class );
    final Style style = mock( Style.class );
    when( parentElement.getStyle() ).thenReturn( style );
    when( element.getParentElement() ).thenReturn( parentElement );
    when( widget.getElement() ).thenReturn( element );
    panel.add( widget );
    verify( widget ).setVisible( true );
    verify( style ).setProperty( anyString(), anyString() );
    verify( panel ).moveOffscreen( widget );
  }

  @Test
  public void testRemove() throws Exception {
    final AlwaysVisibleDeckPanel panel = mock( AlwaysVisibleDeckPanel.class );
    doCallRealMethod().when( panel ).remove( any( Widget.class ) );
    doCallRealMethod().when( panel ).getVisibleWidget( );

    final Widget widget = mock( Widget.class );
    final Element element = mock( Element.class );
    when( widget.getElement() ).thenReturn( element );
    panel.remove( widget );
    Assert.assertEquals( -1, panel.getVisibleWidget() );
  }
}
