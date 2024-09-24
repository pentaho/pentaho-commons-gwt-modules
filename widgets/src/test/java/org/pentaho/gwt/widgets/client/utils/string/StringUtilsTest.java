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

package org.pentaho.gwt.widgets.client.utils.string;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringUtilsTest {

  @Test
  public void testIsEmpty() throws Exception {
    assertTrue( StringUtils.isEmpty( null ) );
    assertTrue( StringUtils.isEmpty( "" ) );
    assertTrue( StringUtils.isEmpty( "   " ) );
    assertTrue( StringUtils.isEmpty( "\n\t " ) );
    assertFalse( StringUtils.isEmpty( "asd" ) );
  }

  @Test
  public void testDefaultString() throws Exception {
    final String aDefault = "default";
    assertEquals( aDefault, StringUtils.defaultString( null, aDefault ) );
    assertEquals( aDefault, StringUtils.defaultString( "", aDefault ) );
    assertEquals( aDefault, StringUtils.defaultString( "\n\t ", aDefault ) );
    final String str = "test";
    assertEquals( str, StringUtils.defaultString( str, aDefault ) );
  }

  @Test
  public void testDefaultString1() throws Exception {
    final String defaultStr = "";
    assertEquals( defaultStr, StringUtils.defaultString( "" ) );
    assertEquals( defaultStr, StringUtils.defaultString( "\n" + "\t " ) );
    assertEquals( defaultStr, StringUtils.defaultString( null ) );
  }

  @Test
  public void testDefaultIfEmpty() throws Exception {
    final String aDefault = "default";
    assertEquals( aDefault, StringUtils.defaultIfEmpty( null, aDefault ) );
    assertEquals( aDefault, StringUtils.defaultIfEmpty( "", aDefault ) );
    assertEquals( aDefault, StringUtils.defaultIfEmpty( "\n\t ", aDefault ) );
    final String str = "test";
    assertEquals( str, StringUtils.defaultIfEmpty( str, aDefault ) );
  }

  @Test
  public void testAddStringToInt() throws Exception {
    assertEquals( "105", StringUtils.addStringToInt( "100", 5 ) );
    try {
      StringUtils.addStringToInt( "erw", 5 );
      fail();
    } catch ( NumberFormatException e ) {
      // expected
    }
  }

  @Test
  public void testMultiplyStringWithInt() throws Exception {
    assertEquals( "500", StringUtils.multiplyStringWithInt( "100", 5 ) );
    try {
      StringUtils.multiplyStringWithInt( "erw", 5 );
      fail();
    } catch ( NumberFormatException e ) {
      // expected
    }
  }

  @Test
  public void testDivideStringWithInt() throws Exception {
    assertEquals( "20", StringUtils.divideStringWithInt( "100", 5 ) );
    try {
      StringUtils.divideStringWithInt( "erw", 5 );
      fail();
    } catch ( NumberFormatException e ) {
      // expected
    }
  }

  @Test
  public void testContainsAnyChars() throws Exception {
    assertFalse( StringUtils.containsAnyChars( null, null ) );
    assertFalse( StringUtils.containsAnyChars( "sdsad", null ) );
    assertFalse( StringUtils.containsAnyChars( null, "sd" ) );
    assertFalse( StringUtils.containsAnyChars( "123", "qwe" ) );
    assertTrue( StringUtils.containsAnyChars( "123", "1wew" ) );
  }

  @Test
  public void testIsPositiveInteger() throws Exception {
    assertFalse( StringUtils.isPositiveInteger( "" ) );
    assertFalse( StringUtils.isPositiveInteger( "2147483649" ) );
    assertFalse( StringUtils.isPositiveInteger( "1111111111111111111" ) );
    assertFalse( StringUtils.isPositiveInteger( "-123" ) );
    assertTrue( StringUtils.isPositiveInteger( "123" ) );
  }

  @Test
  public void testCountMatches() throws Exception {
    assertEquals( 0, StringUtils.countMatches( null, null ) );
    assertEquals( 0, StringUtils.countMatches( "sdwede", null ) );
    assertEquals( 0, StringUtils.countMatches( null, "Ewfeefw" ) );
    assertEquals( 1, StringUtils.countMatches( "132131232_ttt_23123213", "ttt" ) );
    assertEquals( 4, StringUtils.countMatches( "ttt_13_ttt_231_ttt_23213_ttt", "ttt" ) );
  }
}
