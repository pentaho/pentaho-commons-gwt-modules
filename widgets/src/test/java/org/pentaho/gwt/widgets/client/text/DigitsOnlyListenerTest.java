/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.gwt.widgets.client.text;

import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class DigitsOnlyListenerTest {

  @Test
  @SuppressWarnings( "deprecation" )
  public void testOnKeyPress() throws Exception {
    DigitsOnlyListener listener = new DigitsOnlyListener();
    final TextBox sender = mock( TextBox.class );

    listener.onKeyPress( sender, '1', 1 );
    int[] allowedKeys = new int[] { KeyboardListener.KEY_TAB, KeyboardListener.KEY_BACKSPACE,
        KeyboardListener.KEY_DELETE, KeyboardListener.KEY_ENTER, KeyboardListener.KEY_HOME, KeyboardListener.KEY_END,
        KeyboardListener.KEY_LEFT, KeyboardListener.KEY_UP, KeyboardListener.KEY_RIGHT, KeyboardListener.KEY_DOWN };
    for ( int key : allowedKeys ) {
      listener.onKeyPress( sender, (char) key, 1 );
    }
    verify( sender, never() ).cancelKey();

    listener.onKeyPress( sender, 'd', 1 );
    verify( sender ).cancelKey();
  }
}
