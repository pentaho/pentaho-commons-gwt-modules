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


package org.pentaho.gwt.widgets.client.utils.i18n;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class PropertiesUtilTest {

  @Test
  public void testMergeProperties() throws Exception {
    final HashMap<String, String> defaultProperties = new HashMap<String, String>() { {
        put( "1", "one" );
        put( "2", "two" );
        put( "3", "three" );
      } };
    final String two_over = "two_over";
    final HashMap<String, String> overrideProperties = new HashMap<String, String>() { {
        put( "2", two_over );
        put( "4", "four" );
      } };
    final HashMap<String, String> mergeProperties = PropertiesUtil
        .mergeProperties( defaultProperties, overrideProperties );
    assertNotNull( mergeProperties );
    assertEquals( 4, mergeProperties.size() );
    assertEquals( two_over, mergeProperties.get( "2" ) );
  }

  @Test
  public void testBuildProperties() throws Exception {
    final HashMap<String, String> defaultProperties = new HashMap<String, String>() {
      {
        put( "1", "one" );
        put( "2", "two" );
        put( "3", "three" );
      }
    };
    final String two_over = "two_over";
    final HashMap<String, String> buildProperties = PropertiesUtil
        .buildProperties( "2=" + two_over + "\n4=four", defaultProperties );
    assertNotNull( buildProperties );
    assertEquals( 4, buildProperties.size() );
    assertEquals( two_over, buildProperties.get( "2" ) );
  }

  @Test
  public void testBuildProperties1() throws Exception {
    final String two = "two";
    final HashMap<String, String> buildProperties = PropertiesUtil.buildProperties( "2=" + two + "\n4=four" );
    assertNotNull( buildProperties );
    assertEquals( 2, buildProperties.size() );
    assertEquals( two, buildProperties.get( "2" ) );
  }
}
