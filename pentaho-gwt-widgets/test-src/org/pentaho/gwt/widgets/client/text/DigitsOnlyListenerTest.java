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
