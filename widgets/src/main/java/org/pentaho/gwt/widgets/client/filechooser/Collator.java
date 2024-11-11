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


package org.pentaho.gwt.widgets.client.filechooser;

/**
 * @author Rowell Belen
 */
public class Collator {

  private Collator() {
  } // prevent public instantiation

  public static final Collator getInstance() {
    return instance;
  }

  private static final Collator instance = new Collator();

  public native int compare( String source, String target ); /*-{
                                                             return source.localeCompare( target );
                                                             }-*/

}
