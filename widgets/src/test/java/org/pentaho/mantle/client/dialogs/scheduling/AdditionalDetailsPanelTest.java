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
* Copyright (c) 2023 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.mantle.client.dialogs.scheduling;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@RunWith( GwtMockitoTestRunner.class )
public class AdditionalDetailsPanelTest {
  private AdditionalDetailsPanel additionalDetail;

  @Before
  public void setUp() throws Exception {
    additionalDetail = mock( AdditionalDetailsPanel.class );
    additionalDetail.logLevel = mock( ListBox.class );
    additionalDetail.enableSafeMode = mock( CheckBox.class );
    additionalDetail.gatherMetrics = mock( CheckBox.class );
  }

  @Test
  @SuppressWarnings( "deprecation" )
  public void testGetEnableSafeMode() throws Exception {
    final boolean enableSafeMode = false;
    when( additionalDetail.enableSafeMode.isChecked() ).thenReturn( enableSafeMode );
    assertEquals( enableSafeMode, additionalDetail.getEnableSafeMode() );
  }

  @Test
  @SuppressWarnings( "deprecation" )
  public void testGetGatherMetrics() throws Exception {
    final boolean gatherMetrics = false;
    when( additionalDetail.gatherMetrics.isChecked() ).thenReturn( gatherMetrics );
    assertEquals( gatherMetrics, additionalDetail.getGatherMetrics() );
  }

}
