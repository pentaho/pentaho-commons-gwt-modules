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
 * Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.gwt.widgets.client.dialogs;

import java.util.ArrayList;
import java.util.List;

public class GlassPane {

  private static GlassPane instance = new GlassPane();

  private List<GlassPaneListener> listeners = new ArrayList<GlassPaneListener>();

  private boolean shown = false;

  private int dialogCount = 0;

  private GlassPane() {

  }

  public static GlassPane getInstance() {
    return instance;
  }

  public void show() {
    dialogCount++;
    if ( !shown ) {
      shown = true;
      List<GlassPaneListener> listenersToRemove = new ArrayList<GlassPaneListener>();

      for ( GlassPaneListener listener : listeners ) {
        try {
          listener.glassPaneShown();
        } catch ( Exception e ) {
          // If a Listener is a reference from a window that has since closed, it throws an exception here. Remove it.
          listenersToRemove.add( listener );
        }
      }
      removeBadListeners( listenersToRemove );
    }
  }

  private void removeBadListeners( List<GlassPaneListener> removeThese ) {
    for ( GlassPaneListener listener : removeThese ) {
      listeners.remove( listener );
    }
  }

  public void hide() {
    dialogCount--;

    if ( dialogCount < 0 ) { // this shouldn't happen, but let's account for it.
      dialogCount = 0;
    }
    if ( shown && dialogCount == 0 ) {
      shown = false;

      List<GlassPaneListener> listenersToRemove = new ArrayList<GlassPaneListener>();

      for ( GlassPaneListener listener : listeners ) {
        try {
          listener.glassPaneHidden();
        } catch ( Exception e ) {
          // If a Listener is a reference from a window that has since closed, it throws an exception here. Remove it.
          listenersToRemove.add( listener );
        }
      }
      removeBadListeners( listenersToRemove );
    }

  }

  public void addGlassPaneListener( GlassPaneListener listener ) {
    if ( listeners.contains( listener ) == false ) {
      this.listeners.add( listener );
    }
  }

  public void removeGlassPaneListener( GlassPaneListener listener ) {
    if ( listeners.contains( listener ) ) {
      this.listeners.remove( listener );
    }
  }
}
