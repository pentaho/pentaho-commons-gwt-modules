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

package org.pentaho.gwt.widgets.client.dialogs;

import com.google.gwt.dom.client.Document;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GlassPane {

  private static GlassPane instance = new GlassPane();

  private Map<String, GlassPaneListener> listeners = new LinkedHashMap<String, GlassPaneListener>();

  private boolean shown = false;

  private int dialogCount = 0;

  private UUIDGenerator uuidGenerator;

  private GlassPane() {
    uuidGenerator = new UUIDGenerator();
  }

  public static GlassPane getInstance() {
    return instance;
  }

  void setUuidGenerator( UUIDGenerator uuidGenerator ) {
    this.uuidGenerator = uuidGenerator;
  }

  public void show() {
    dialogCount++;
    if ( !shown ) {
      shown = true;
      List<String> listenersToRemove = new ArrayList<String>();

      for ( Map.Entry<String, GlassPaneListener> listener : listeners.entrySet() ) {
        try {
          listener.getValue().glassPaneShown();
        } catch ( Exception e ) {
          // If a Listener is a reference from a window that has since closed, it throws an exception here. Remove it.
          listenersToRemove.add( listener.getKey() );
        }
      }
      removeBadListeners( listenersToRemove );
    }
  }

  private void removeBadListeners( List<String> removeThese ) {
    for ( String key : removeThese ) {
      listeners.remove( key );
    }
  }

  public void hide() {
    dialogCount--;

    if ( dialogCount < 0 ) { // this shouldn't happen, but let's account for it.
      dialogCount = 0;
    }
    if ( shown && dialogCount == 0 ) {
      shown = false;

      List<String> listenersToRemove = new ArrayList<String>();

      for ( Map.Entry<String, GlassPaneListener> listener : listeners.entrySet() ) {
        try {
          listener.getValue().glassPaneHidden();
        } catch ( Exception e ) {
          // If a Listener is a reference from a window that has since closed, it throws an exception here. Remove it.
          listenersToRemove.add( listener.getKey() );
        }
      }
      removeBadListeners( listenersToRemove );
    }

  }

  public String addGlassPaneListener( GlassPaneListener listener ) {

    final List<String> keysFound = keysByValue( listener );

    if ( keysFound.isEmpty() ) {
      final String uuid = uuidGenerator.getUniqueId();
      this.listeners.put( uuid, listener );
      return uuid;
    } else {
      return keysFound.get( 0 );
    }

  }


  private List<String> keysByValue( GlassPaneListener listener ) {
    List<String> keys = new ArrayList<String>();
    for ( Map.Entry<String, GlassPaneListener> pair : listeners.entrySet() ) {
      if ( pair.getValue().equals( listener ) ) {
        keys.add( pair.getKey() );
      }
    }
    return keys;
  }

  public void removeGlassPaneListener( GlassPaneListener listener ) {

    final List<String> keysFound = keysByValue( listener );

    if ( !keysFound.isEmpty() ) {
      for ( String key : keysFound ) {
        listeners.remove( key );
      }
    }

  }

  public void removeGlassPaneListenerById( String uuid ) {
    listeners.remove( uuid );
  }

  static class UUIDGenerator {
    public String getUniqueId() {
      return Document.get().createUniqueId();
    }
  }
}
