package org.pentaho.gwt.widgets.client.utils;

import org.pentaho.gwt.widgets.client.utils.string.StringUtils;

import com.google.gwt.regexp.shared.RegExp;

public class NameUtils {

  private static final RegExp containsReservedCharsPattern = makePattern();
  
  /**
   * Checks for presence of reserved chars as well as illegal permutations of legal chars.
   */
  public static boolean isValidFolderName( final String name ) {
    if ( StringUtils.isEmpty( name ) || // not null, not empty, and not all whitespace
        !name.trim().equals( name ) || // no leading or trailing whitespace
        containsReservedCharsPattern.test( name ) || // no reserved characters
        ".".equals( name ) || // no . //$NON-NLS-1$
        "..".equals( name ) ) { // no .. //$NON-NLS-1$
      return false;
    }
    return true;
  }
  
  public static boolean isValidFileName( final String name) {
    if ( StringUtils.isEmpty( name ) || // not null, not empty, and not all whitespace
        !name.trim().equals( name ) || // no leading or trailing whitespace
        containsReservedCharsPattern.test( name ) ) { // no reserved characters
      return false;
    }
    return true;
  }

  private static RegExp makePattern() {
    // escape all reserved characters as they may have special meaning to regex engine
    StringBuilder buf = new StringBuilder();
    buf.append( ".*[" ); //$NON-NLS-1$
    for ( Character ch : getReservedChars().toCharArray() ) {
      buf.append( "\\" ); //$NON-NLS-1$
      buf.append( ch );
    }
    buf.append( "]+.*" ); //$NON-NLS-1$
    return RegExp.compile( buf.toString() );
  }
  
  /**
   * Returns human readable list of reserved characters with the separator string inserted between each
   * character started with the second character and ending after the n-1 character.
   * 
   * @param separatorString
   * @return
   */
  public static String reservedCharListForDisplay( String separatorString ) {
    StringBuilder sb = new StringBuilder();
    String reservedChars = getReservedChars();
    for ( int i = 0; i < reservedChars.length(); i++ ) {
      if ( !String.valueOf( reservedChars.charAt( i ) ).trim().isEmpty() ) {
        sb.append( reservedChars.charAt( i ) );
        sb.append( separatorString );
      }
    }
    if ( sb.length() > 0 ) {
      sb.delete( sb.length() - separatorString.length(), sb.length() - 1 );
    }
    return sb.toString();
  }
  
  public static native String URLEncode( String value )
  /*-{
    return $wnd.pho.Encoder.encode( value );
  }-*/;
  
  public static native String getReservedChars()
  /*-{
    return $wnd.RESERVED_CHARS; 
  }-*/;
  
  public static native String encodeRepositoryPath( String path )
  /*-{
    return $wnd.pho.Encoder.encodeRepositoryPath( path );
  }-*/;    
  
  public static native String decodeRepositoryPath( String path )
  /*-{
    return $wnd.pho.Encoder.decodeRepositoryPath( path );
  }-*/;   
  }

