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

package org.pentaho.gwt.widgets.client.dialogs;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.UUID;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class GlassPaneTest {

  @Test
  public void testShow() throws Exception {
    GlassPane gp = GlassPane.getInstance();

    final GlassPaneListener listener = mock( GlassPaneListener.class );
    final GlassPaneListener badListener = mock( GlassPaneListener.class );
    doThrow( Exception.class ).when( badListener ).glassPaneShown();
    gp.addGlassPaneListener( listener );
    gp.addGlassPaneListener( badListener );

    gp.show();

    // no exceptions
  }

  @Test
  public void testHide() throws Exception {
    GlassPane gp = GlassPane.getInstance();

    final GlassPaneListener listener = mock( GlassPaneListener.class );
    final GlassPaneListener badListener = mock( GlassPaneListener.class );
    doThrow( Exception.class ).when( badListener ).glassPaneHidden();
    gp.addGlassPaneListener( listener );
    gp.addGlassPaneListener( badListener );

    gp.hide();

    // no exceptions
  }

  @Mock
  private GlassPaneListener listener;
  @Mock
  private GlassPaneListener listener2;


  @Test
  public void testRemove() throws Exception {
    GlassPane gp = GlassPane.getInstance();

    gp.setUuidGenerator( new GlassPane.UUIDGenerator() {

      @Override public String getUniqueId() {
        return UUID.randomUUID().toString();
      }
    } );


    final String uuid = gp.addGlassPaneListener( listener );
    final String uuid2 = gp.addGlassPaneListener( listener2 );

    gp.hide();

    verify( listener, times( 1 ) ).glassPaneHidden();
    verify( listener2, times( 1 ) ).glassPaneHidden();

    gp.show();

    reset( listener, listener2 );

    gp.removeGlassPaneListenerById( uuid );

    gp.hide();

    verify( listener, times( 0 ) ).glassPaneHidden();
    verify( listener2, times( 1 ) ).glassPaneHidden();

    gp.show();

    reset( listener, listener2 );

    gp.removeGlassPaneListener( listener2 );

    gp.hide();

    verify( listener, times( 0 ) ).glassPaneHidden();
    verify( listener2, times( 0 ) ).glassPaneHidden();
  }
}
