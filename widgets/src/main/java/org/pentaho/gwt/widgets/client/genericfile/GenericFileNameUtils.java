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
 * Copyright (c) 2023 Hitachi Vantara. All rights reserved.
 */
package org.pentaho.gwt.widgets.client.genericfile;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.pentaho.gwt.widgets.client.utils.NameUtils;

public class GenericFileNameUtils {
  private GenericFileNameUtils() {
  }

  // TODO: For repo, NameUtils.encodeRepositoryPath, ~ is converted to \t...
  // Here, theres pvfs:// prefixes in play as well...
  // See Common-UI's Encoder.encodeRepositoryPath.
  @NonNull
  public static String encodePath( @NonNull String path ) {
    return path
      .replace( ":", "~" )
      .replace( "/", ":" );
  }

  @NonNull
  public static String buildPath( @NonNull String basePath, @NonNull String relativePath ) {
    return basePath + "/" + relativePath;
  }

  public static boolean isValidFolderName( @Nullable String name ) {
    return NameUtils.isValidFolderName( name );
  }
}
