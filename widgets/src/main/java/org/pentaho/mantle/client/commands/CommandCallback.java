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

package org.pentaho.mantle.client.commands;

/**
 * Receives notifications of Command execution. This allows code to perform actions dependent upon the completion.
 * 
 * @author nbaker
 */
public interface CommandCallback {
  void afterExecute();
}
