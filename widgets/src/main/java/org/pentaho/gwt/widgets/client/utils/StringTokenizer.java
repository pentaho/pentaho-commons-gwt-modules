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

import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated Please use org.pentaho.gwt.widgets.client.utils.string.StringUtils instead. We are gradually moving to a
 *             more logical breakdown of GWT modules for more succinct reuse.
 */
@Deprecated
public class StringTokenizer {
  ArrayList<String> tokens = new ArrayList<String>();

  @Deprecated
  public StringTokenizer( String text, String delimiters ) {
    if ( text == null || "".equals( text ) ) { //$NON-NLS-1$
      return;
    }
    char[] delimiterArray = delimiters.toCharArray();
    List<Character> delimiterList = new ArrayList<Character>();
    for ( char delim : delimiterArray ) {
      delimiterList.add( delim );
    }
    char[] chars = text.toCharArray();

    int sindex = 0;
    int i;
    for ( i = 0; i < chars.length; i++ ) {
      if ( delimiterList.contains( chars[i] ) ) {
        tokens.add( text.substring( sindex, i ) );
        sindex = i + 1;
      }
    }

    if ( sindex < i ) {
      tokens.add( text.substring( sindex ) );
    }
  }

  @Deprecated
  public StringTokenizer( String text, char delimiter ) {
    this( text, new String( new char[] { delimiter } ) );
  }

  @Deprecated
  public int countTokens() {
    return tokens.size();
  }

  @Deprecated
  public String tokenAt( int index ) {
    return (String) tokens.get( index );
  }
}
