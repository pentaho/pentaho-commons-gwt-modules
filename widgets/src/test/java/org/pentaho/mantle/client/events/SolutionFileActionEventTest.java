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


package org.pentaho.mantle.client.events;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith( GwtMockitoTestRunner.class )
public class SolutionFileActionEventTest {

  @Test
  public void testDispatch() throws Exception {
    final SolutionFileActionEvent solutionFileActionEvent = mock( SolutionFileActionEvent.class );

    doCallRealMethod().when( solutionFileActionEvent ).dispatch( any( SolutionFileActionEventHandler.class ) );

    final SolutionFileActionEventHandler solutionFileActionEventHandler = mock( SolutionFileActionEventHandler.class );
    solutionFileActionEvent.dispatch( solutionFileActionEventHandler );
    verify( solutionFileActionEventHandler ).onFileAction( solutionFileActionEvent );
  }
}
