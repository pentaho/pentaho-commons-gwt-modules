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


package org.pentaho.gwt.widgets.client.controls;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ErrorLabelTest {

  @Test
  public void testSetErrorMsg() throws Exception {
    ErrorLabel errorLabel = new ErrorLabel( mock( Widget.class ) );
    errorLabel.errorLabel = spy( errorLabel.errorLabel );

    final String errorMsg = "error";
    errorLabel.setErrorMsg( errorMsg );
    verify( errorLabel.errorLabel ).setVisible( true );
    verify( errorLabel.errorLabel ).setText( errorMsg );

    errorLabel.setErrorMsg( "" );
    verify( errorLabel.errorLabel ).setVisible( false );
    verify( errorLabel.errorLabel ).setText( "" );
  }
}
