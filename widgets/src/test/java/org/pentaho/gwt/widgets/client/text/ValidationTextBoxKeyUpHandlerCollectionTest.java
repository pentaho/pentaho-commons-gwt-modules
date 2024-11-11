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

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith( GwtMockitoTestRunner.class )
public class ValidationTextBoxKeyUpHandlerCollectionTest {

  @Test
  public void testFireOnKeyUp() throws Exception {
    ValidationTextBoxKeyUpHandlerCollection handlers = new ValidationTextBoxKeyUpHandlerCollection();
    final KeyUpHandler handler = mock( KeyUpHandler.class );
    handlers.add( handler );

    final KeyUpEvent event = mock( KeyUpEvent.class );
    handlers.fireOnKeyUp( event );
    verify( handler ).onKeyUp( event );
  }
}
