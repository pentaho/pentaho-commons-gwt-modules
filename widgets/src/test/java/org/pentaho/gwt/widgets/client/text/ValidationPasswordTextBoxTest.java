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


package org.pentaho.gwt.widgets.client.text;

import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;

@RunWith( GwtMockitoTestRunner.class )
public class ValidationPasswordTextBoxTest {
  @Test
  public void testAddKeyUpHandler() throws Exception {
    ValidationPasswordTextBox textBox = mock( ValidationPasswordTextBox.class );
    doCallRealMethod().when( textBox ).addKeyUpHandler( any( KeyUpHandler.class ) );

    final KeyUpHandler handler = mock( KeyUpHandler.class );
    assertNull( textBox.handlers );
    textBox.addKeyUpHandler( handler );
    assertNotNull( textBox.handlers );
    assertEquals( handler, textBox.handlers.get( 0 ) );
  }

  @Test
  public void testAddValidatableTextBoxListener() throws Exception {
    ValidationPasswordTextBox textBox = mock( ValidationPasswordTextBox.class );
    doCallRealMethod().when( textBox ).addValidatableTextBoxListener( any( IValidationTextBoxListener.class ) );

    final IValidationTextBoxListener listener = mock( IValidationTextBoxListener.class );
    assertNull( textBox.listeners );
    textBox.addValidatableTextBoxListener( listener );
    assertNotNull( textBox.listeners );
    assertEquals( listener, textBox.listeners.get( 0 ) );
  }
}
