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
 * Copyright (c) 2023 - 2024 Hitachi Vantara. All rights reserved.
 */
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

  // TODO: For repo, NameUtils.encodeRepositoryPath, ~ is converted to \t...
  // Here, theres pvfs:// prefixes in play as well...
  // See Common-UI's Encoder.encodeRepositoryPath.
  @NonNull
  public static String encodePath( @NonNull String path ) {
    return path
      .replace( ":", "~" )
      .replace( PATH_SEPARATOR, ":" );
  }

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

    return path.substring( 0, path.lastIndexOf( PATH_SEPARATOR ) );
  }
}
