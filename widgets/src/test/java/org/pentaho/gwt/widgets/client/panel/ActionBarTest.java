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

package org.pentaho.gwt.widgets.client.panel;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ActionBarTest {
  private ActionBar actionBar;

  @Before
  public void setUp() throws Exception {
    actionBar = mock( ActionBar.class );

  }

  @Test
  public void testAddWidget() throws Exception {
    doCallRealMethod().when( actionBar ).addWidget( any( Widget.class ),
        any( HasHorizontalAlignment.HorizontalAlignmentConstant.class ) );

    actionBar.buttonPanel = mock( HorizontalPanel.class );
    final Widget widget = mock( Widget.class );
    final HasHorizontalAlignment.HorizontalAlignmentConstant align = mock(
        HasHorizontalAlignment.HorizontalAlignmentConstant.class );
    actionBar.addWidget( widget, align );
    verify( actionBar.buttonPanel ).add( widget );
    verify( actionBar.buttonPanel ).setCellHorizontalAlignment( widget, align );
    verify( actionBar.buttonPanel ).setCellVerticalAlignment( eq( widget ), any( HasVerticalAlignment.VerticalAlignmentConstant.class )  );
  }

  @Test
  public void testCollapse() throws Exception {
    doCallRealMethod().when( actionBar ).collapse( anyInt() );

    actionBar.state = ActionBar.State.COLLAPSE;
    actionBar.collapse( 20 );
    assertNull( actionBar.collapseEffect );
    verify( actionBar, never() ).setState( any( ActionBar.State.class ) );

    actionBar.state = ActionBar.State.EXPAND;
    final int height = 100;
    when( actionBar.getOffsetHeight() ).thenReturn( height );
    final Element element = mock( Element.class );
    when( actionBar.getElement() ).thenReturn( element );
    actionBar.collapse( 20 );
    assertEquals( actionBar.height, height );
    assertNotNull( actionBar.collapseEffect );
    verify( actionBar ).setState( ActionBar.State.COLLAPSE );

    assertEquals( ActionBar.DURATION, actionBar.collapseEffect.getDuration(), 0 );
    assertFalse( actionBar.collapseEffect.isStarted() );
    assertEquals( element, actionBar.collapseEffect.getEffectElement() );
  }

  @Test
  public void testExpand() throws Exception {
    doCallRealMethod().when( actionBar ).expand( anyInt() );

    actionBar.state = ActionBar.State.EXPAND;
    actionBar.expand( 20 );
    assertNull( actionBar.expandEffect );
    verify( actionBar, never() ).setState( any( ActionBar.State.class ) );

    actionBar.state = ActionBar.State.COLLAPSE;
    final int height = 100;
    when( actionBar.getOffsetHeight() ).thenReturn( height );
    final Element element = mock( Element.class );
    when( actionBar.getElement() ).thenReturn( element );
    actionBar.expand( 20 );
    assertEquals( actionBar.height, height );
    assertNotNull( actionBar.expandEffect );
    verify( actionBar ).setState( ActionBar.State.EXPAND );

    assertEquals( ActionBar.DURATION, actionBar.expandEffect.getDuration(), 0 );
    assertFalse( actionBar.expandEffect.isStarted() );
    assertEquals( element, actionBar.expandEffect.getEffectElement() );
  }
}
