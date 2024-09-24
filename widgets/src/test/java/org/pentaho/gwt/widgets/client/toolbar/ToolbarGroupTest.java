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

package org.pentaho.gwt.widgets.client.toolbar;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ToolbarGroupTest {
  private ToolbarGroup toolbarGroup;

  @Before
  public void setUp() throws Exception {
    toolbarGroup = mock( ToolbarGroup.class );
    toolbarGroup.groupLabel = mock( Label.class );
  }

  @Test
  public void testAdd() throws Exception {
    doCallRealMethod().when( toolbarGroup ).add( any( ToolbarButton.class ) );

    toolbarGroup.buttons = mock( List.class );

    final ToolbarButton toolbarButton = mock( ToolbarButton.class );
    when( toolbarGroup.buttons.contains( toolbarButton ) ).thenReturn( true );
    toolbarGroup.add( toolbarButton );
    verify( toolbarGroup.buttons, never() ).add( any( ToolbarButton.class ) );

    when( toolbarGroup.buttons.contains( toolbarButton ) ).thenReturn( false );
    toolbarGroup.add( toolbarButton );
    verify( toolbarGroup.buttons ).add( toolbarButton );
  }

  @Test
  public void testSetEnabled() throws Exception {
    doCallRealMethod().when( toolbarGroup ).setEnabled( anyBoolean() );

    toolbarGroup.enabled = true;
    toolbarGroup.setEnabled( true );
    verify( toolbarGroup.groupLabel, never() ).setStyleName( anyString() );

    toolbarGroup.enabled = false;
    toolbarGroup.setEnabled( false );
    verify( toolbarGroup.groupLabel, never() ).setStyleName( anyString() );

    final ToolbarButton button = mock( ToolbarButton.class );
    toolbarGroup.buttons = new LinkedList<ToolbarButton>() { {
        add( button );
      } };
    final boolean enabled = true;
    toolbarGroup.setEnabled( enabled );
    verify( button ).setEnabled( enabled );
    verify( toolbarGroup.groupLabel ).setStyleName( ToolbarGroup.CSS_ENABLED );
  }

  @Test
  public void testSetTempDisabled() throws Exception {
    doCallRealMethod().when( toolbarGroup ).setTempDisabled( anyBoolean() );

    final ToolbarButton button = mock( ToolbarButton.class );
    toolbarGroup.buttons = new LinkedList<ToolbarButton>() { {
        add( button );
      } };

    final boolean disable = true;
    toolbarGroup.setTempDisabled( disable );
    verify( button ).setTempDisabled( disable );
    verify( toolbarGroup.groupLabel ).setStyleName( ToolbarGroup.CSS_DISABLED );
  }

  @Test
  public void testSetVisible() throws Exception {
    doCallRealMethod().when( toolbarGroup ).setVisible( anyBoolean() );

    toolbarGroup.visible = true;
    toolbarGroup.setVisible( true );
    verify( toolbarGroup.groupLabel, never() ).setVisible( anyBoolean() );

    toolbarGroup.visible = false;
    toolbarGroup.setVisible( false );
    verify( toolbarGroup.groupLabel, never() ).setVisible( anyBoolean() );

    final ToolbarButton button = mock( ToolbarButton.class );
    toolbarGroup.buttons = new LinkedList<ToolbarButton>() { {
        add( button );
      } };
    toolbarGroup.trailingSeparator = mock( Image.class );
    toolbarGroup.leadingSeparator = mock( Image.class );
    final boolean visible = true;
    toolbarGroup.setVisible( visible );
    verify( button ).setVisible( visible );
    verify( toolbarGroup.groupLabel ).setVisible( visible );
    verify( toolbarGroup.trailingSeparator ).setVisible( visible );
    verify( toolbarGroup.leadingSeparator ).setVisible( visible );
  }

  @Test
  public void testSetLabel() throws Exception {
    doCallRealMethod().when( toolbarGroup ).setLabel( anyString() );

    final String label = "label";
    toolbarGroup.setLabel( label );
    verify( toolbarGroup.groupLabel ).setText( label );
  }
}
