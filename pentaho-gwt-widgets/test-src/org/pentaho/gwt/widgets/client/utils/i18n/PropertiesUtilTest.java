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
