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

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith( GwtMockitoTestRunner.class )
public class ValidationTextBoxListenerCollectionTest {

  @Test
  public void testFireOnSuccess() throws Exception {
    final ValidationTextBoxListenerCollection collection = new ValidationTextBoxListenerCollection();
    final IValidationTextBoxListener listener = mock( IValidationTextBoxListener.class );
    collection.add( listener );

    final Widget widget = mock( Widget.class );
    collection.fireOnSuccess( widget );
    verify( listener ).onSuccess( widget );
  }

  @Test
  public void testFireOnFailure() throws Exception {
    final ValidationTextBoxListenerCollection collection = new ValidationTextBoxListenerCollection();
    final IValidationTextBoxListener listener = mock( IValidationTextBoxListener.class );
    collection.add( listener );

    final Widget widget = mock( Widget.class );
    collection.fireOnFailure( widget );
    verify( listener ).onFailure( widget );
  }
}
