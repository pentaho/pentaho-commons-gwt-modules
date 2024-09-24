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

package org.pentaho.gwt.widgets.client.utils;

import com.google.gwt.junit.client.GWTTestCase;

public class NameUtilsIT extends GWTTestCase {
  public String getModuleName() {
    return "org.pentaho.gwt.widgets.Widgets"; //$NON-NLS-1$
  }

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    setUpNative();
  }

  public static native void setUpNative() /*-{
      $wnd.RESERVED_CHARS = '123';
      $wnd.RESERVED_CHARS_DISPLAY = '1, 2, 3';
  }-*/;

  public void testIsValidFolderName() throws Exception {
    assertFalse( NameUtils.isValidFolderName( null ) );
    assertFalse( NameUtils.isValidFolderName( "" ) );
    assertFalse( NameUtils.isValidFolderName( "\t" ) );
    assertFalse( NameUtils.isValidFolderName( "\tabc" ) );
    assertFalse( NameUtils.isValidFolderName( "abc\n" ) );
    assertFalse( NameUtils.isValidFolderName( " \n\t  " ) );
    assertFalse( NameUtils.isValidFolderName( " \t\n  " ) );
    assertFalse( NameUtils.isValidFolderName( "  dhfjskh" ) );
    assertFalse( NameUtils.isValidFolderName( "dfsdf " ) );
    assertFalse( NameUtils.isValidFolderName( "   aBc " ) );
    assertFalse( NameUtils.isValidFolderName( "." ) );
    assertFalse( NameUtils.isValidFolderName( ".." ) );
    assertFalse( NameUtils.isValidFolderName( " ." ) );
    assertFalse( NameUtils.isValidFolderName( ".. " ) );
    assertFalse( NameUtils.isValidFolderName( " .. " ) );
    // can't test RESERVED_CHARS - Java Regex uses // for escape and JS - /
    // assertFalse( NameUtils.isValidFolderName( "sfdfs_1_fsdf" ) );
    assertTrue( NameUtils.isValidFolderName( "edwub_sdkj" ) );
    assertTrue( NameUtils.isValidFolderName( "abc.def" ) );
    assertTrue( NameUtils.isValidFolderName( "AbCdEf" ) );
  }

  public void testIsValidFileName() throws Exception {
    assertFalse( NameUtils.isValidFileName( null ) );
    assertFalse( NameUtils.isValidFileName( "" ) );
    assertFalse( NameUtils.isValidFileName( " \n\t  " ) );
    assertFalse( NameUtils.isValidFileName( "  dhfjskh" ) );
    assertFalse( NameUtils.isValidFileName( "dfsdf " ) );
    // can't test RESERVED_CHARS - Java Regex uses // for escape and JS - /
    // assertFalse( NameUtils.isValidFileName( "sfdfs_1_fsdf" ) );
    assertTrue( NameUtils.isValidFileName( "edwub_sdkj" ) );
  }

  public void testReservedCharListForDisplay() throws Exception {
    assertEquals( "1, 2, 3", NameUtils.reservedCharListForDisplay() );
  }

  public void testReservedCharListForDisplay1() throws Exception {
    assertEquals( "1_2_3", NameUtils.reservedCharListForDisplay( "_" ) );
  }
}
