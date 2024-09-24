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
package org.pentaho.gwt.widgets.client.genericfile;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.pentaho.gwt.widgets.client.utils.NameUtils;
import org.pentaho.gwt.widgets.client.utils.string.StringTokenizer;

import java.util.ArrayList;
import java.util.List;

public class GenericFileNameUtils {
  public static final String PATH_SEPARATOR = "/";

  private static final RegExp pathWithProtocolPattern = RegExp.compile( "^(\\w+://)(.*)$" );

  private GenericFileNameUtils() {
  }

  @NonNull
  public static native String encodePath( @NonNull String path )
  /*-{
    return $wnd.pho.Encoder.encodeGenericFilePath(path);
  }-*/;

  public static boolean isRepositoryPath( @NonNull String path ) {
    return path.startsWith( PATH_SEPARATOR );
  }

  @NonNull
  public static String buildPath( @NonNull String basePath, @NonNull String relativePath ) {
    if ( basePath.endsWith( PATH_SEPARATOR ) ) {
      return basePath + relativePath;
    }

    return basePath + PATH_SEPARATOR + relativePath;
  }

  public static boolean isValidFolderName( @Nullable String name ) {
    return NameUtils.isValidFolderName( name );
  }

  @NonNull
  public static List<String> splitPath( @Nullable String path ) {
    List<String> segments = new ArrayList<>();

    if ( path != null ) {
      String normalizedPath = path;

      // Extract the root of the path and add as first segment.
      if ( path.startsWith( PATH_SEPARATOR ) ) {
        segments.add( PATH_SEPARATOR );
        normalizedPath = normalizedPath.substring( 1 );
      } else {
        MatchResult match = pathWithProtocolPattern.exec( normalizedPath );
        if ( match != null ) {
          segments.add( match.getGroup( 1 ) );
          normalizedPath = match.getGroup( 2 );
        }
      }

      // Tokenizer already strips a hanging /.
      StringTokenizer st = new StringTokenizer( normalizedPath, PATH_SEPARATOR );
      for ( int i = 0; i < st.countTokens(); i++ ) {
        String token = st.tokenAt( i );
        segments.add( token );
      }
    }

    return segments;
  }

  @NonNull
  public static String getParentPath( @NonNull String path ) {
    if ( path.endsWith( PATH_SEPARATOR ) ) {
      path = path.substring( 0, path.length() - 1 );
    }

    int index = path.lastIndexOf( PATH_SEPARATOR );
    if ( index < 0 ) {
      return "";
    }

    return path.substring( 0, index );
  }
}
