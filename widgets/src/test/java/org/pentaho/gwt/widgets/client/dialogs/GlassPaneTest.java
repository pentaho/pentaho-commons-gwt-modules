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
