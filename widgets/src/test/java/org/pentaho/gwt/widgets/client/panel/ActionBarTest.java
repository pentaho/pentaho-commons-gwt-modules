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
