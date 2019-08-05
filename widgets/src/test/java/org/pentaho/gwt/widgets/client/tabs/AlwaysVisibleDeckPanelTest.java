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
* Copyright (c) 2002-2019 Hitachi Vantara.  All rights reserved.
*/

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
