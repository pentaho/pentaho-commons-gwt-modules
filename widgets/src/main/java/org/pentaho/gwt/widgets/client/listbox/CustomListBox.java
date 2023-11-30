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
 * Copyright (c) 2002-2023 Hitachi Vantara. All rights reserved.
 */

package org.pentaho.gwt.widgets.client.listbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.ui.Focusable;
import org.pentaho.gwt.widgets.client.panel.PentahoFocusPanel;
import org.pentaho.gwt.widgets.client.panel.HorizontalFlexPanel;
import org.pentaho.gwt.widgets.client.panel.ScrollFlexPanel;
import org.pentaho.gwt.widgets.client.panel.VerticalFlexPanel;
import org.pentaho.gwt.widgets.client.text.SearchTextBox;
import org.pentaho.gwt.widgets.client.utils.ElementUtils;
import org.pentaho.gwt.widgets.client.utils.Rectangle;
import org.pentaho.gwt.widgets.client.utils.string.StringUtils;

import com.allen_sauer.gwt.dnd.client.DragController;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.PopupListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * ComplexListBox is a List-style widget can contain custom list-items made (images + text, text + checkboxes) This list
 * is displayed as a drop-down style component by default. If the visibleRowCount property is set higher than 1
 * (default), the list is rendered as a multi-line list box.
 * 
 * <P>
 * Usage:
 * 
 * <p>
 * 
 * <pre>
 * ComplexListBox list = new ComplexListBox();
 * 
 * list.addItem( &quot;Alberta&quot; );
 * list.addItem( &quot;Atlanta&quot; );
 * list.addItem( &quot;San Francisco&quot; );
 * list.addItem( new DefaultListItem( &quot;Testing&quot;, new Image( &quot;16x16sample.png&quot; ) ) );
 * list.addItem( new DefaultListItem( &quot;Testing 2&quot;, new CheckBox() ) );
 * 
 * list.setVisibleRowCount( 6 ); // turns representation from drop-down to list
 * 
 * list.addChangeListener( new ChangeListener() {
 *   public void onChange( Widget widget ) {
 *     System.out.println( &quot;&quot; + list.getSelectedIdex() );
 *   }
 * } );
 * </pre>
 */
@SuppressWarnings( "deprecation" )
public class CustomListBox extends VerticalFlexPanel implements ChangeListener, PopupListener, MouseListener,
    FocusListener, KeyboardListener, ListItemListener, Focusable {
  protected List<ListItem> items = new ArrayList<>();
  protected int selectedIndex = -1;
  private boolean defaultSelectionEnabled = true;

  protected DropDownArrow arrow = new DropDownArrow();
  protected int visible = 1;
  private int maxDropVisible = 15;
  protected boolean editable = false;
  private boolean searchable = false;
  private VerticalPanel listPanel = new VerticalFlexPanel();
  protected ScrollPanel listScrollPanel = new ScrollFlexPanel();

  // Members for drop-down style
  protected FlexTable dropGrid = new FlexTable();
  protected boolean popupShowing = false;
  private DropPopupPanel popup;
  private SearchTextBox searchTextBox = new SearchTextBox();
  private PopupList popupVbox = new PopupList();
  protected PentahoFocusPanel fPanel = new PentahoFocusPanel();
  private ScrollPanel popupScrollPanel = new ScrollPanel();

  protected List<ChangeListener> listeners = new ArrayList<>();
  private final int spacing = 1;
  protected int maxHeight, maxWidth, averageHeight; // height and width of largest ListItem
  private String primaryStyleName;
  private String height, width;
  protected String popupHeight;
  private String popupWidth;
  protected boolean suppressLayout;

  private boolean enabled = true;
  private String val;
  private Command command;
  protected DragController dragController;
  protected boolean multiSelect;

  public CustomListBox() {

    selectedItemWrapper.addStyleName( "flex-row" );
    selectedItemWrapper.addStyleName( "with-scroll-child" );
    dropGrid.addStyleName( "custom-list-drop-grid" );
    dropGrid.addStyleName( HorizontalFlexPanel.STYLE_NAME );

    dropGrid.getColumnFormatter().setWidth( 0, "100%" ); //$NON-NLS-1$
    dropGrid.setWidget( 0, 1, arrow );
    dropGrid.getElement().getStyle().setProperty( "tableLayout", "fixed" );
    arrow.getElement().getParentElement().getStyle().setProperty( "width", "20px" );
    dropGrid.setCellPadding( 0 );
    dropGrid.setCellSpacing( 1 );
    updateUI();

    // Add List Panel to it's scrollPanel
    listScrollPanel.add( listPanel );
    listScrollPanel.setHeight( "100%" ); //$NON-NLS-1$
    listScrollPanel.setWidth( "100%" ); //$NON-NLS-1$
    listScrollPanel.getElement().getStyle().setProperty( "overflowX", "hidden" ); //$NON-NLS-1$ //$NON-NLS-2$
    // listScrollPanel.getElement().getStyle().setProperty("padding",spacing+"px");
    listPanel.addStyleName( "custom-list-list-panel" );
    listPanel.setSpacing( spacing );
    listPanel.setWidth( "100%" ); //$NON-NLS-1$

    // default to drop-down
    fPanel.add( dropGrid );
    fPanel.setHeight( "100%" ); //$NON-NLS-1$
    super.add( fPanel );

    popupScrollPanel.add( popupVbox );
    popupScrollPanel.getElement().getStyle().setProperty( "overflowX", "hidden" ); //$NON-NLS-1$ //$NON-NLS-2$
    popupVbox.setWidth( "100%" ); //$NON-NLS-1$
    popupVbox.setSpacing( spacing );

    fPanel.addMouseListener( this );
    fPanel.addFocusListener( this );
    fPanel.addKeyboardListener( this );
    fPanel.addStyleName( "flex-column" );

    // Clear base style first. Otherwise, calling setStylePrimaryName would only partially clear the base style,
    // because it is composed of multiple classes... Base style is added back from within setStylePrimaryName.
    this.setStyleName( "" );
    this.setStylePrimaryName( "custom-list" );

    setTdStyles( this.getElement() );
    setTdStyles( listPanel.getElement() );

    editableTextBox = new TextBox() {
      @Override
      public void onBrowserEvent( Event event ) {
        // int code = event.getKeyCode();

        switch ( DOM.eventGetType( event ) ) {
          case Event.ONKEYUP:
            String newVal = editableTextBox.getText();
            if ( !newVal.equals( getAcceptedText() ) ) {
              CustomListBox.this.setValue( newVal );
            }
            // event.cancelBubble(true);
            break;

          case Event.ONMOUSEUP:
            super.onBrowserEvent( event );
            event.cancelBubble( true );
            break;

          default:
            return;
        }
      }
    };
    editableTextBox.setStylePrimaryName( "custom-list-textbox" );

    editableTextBox.setWidth( "100%" );
    editableTextBox.sinkEvents( Event.KEYEVENTS );
    editableTextBox.sinkEvents( Event.MOUSEEVENTS );

    selectedItemPlaceholder.addStyleName( "placeholder-label" );
  }

  public void setTableLayout( String tableLayout ) {
    if ( StringUtils.isEmpty( tableLayout ) ) {
      dropGrid.getElement().getStyle().clearProperty( "tableLayout" );
    } else {
      dropGrid.getElement().getStyle().setProperty( "tableLayout", tableLayout );
    }
  }

  private native void setTdStyles( Element ele )/*-{
    var tds = ele.getElementsByTagName("td");

    for(var i = 0; i < tds.length; i++) {
      var td = tds[i];

      if (!td.style) {
        td.className = "customListBoxTdFix";
      } else {
        td.style.padding = "0px";
        td.style.border = "none";
      }
    }
  }-*/;

  /**
   * Removes the passed in ListItem
   * 
   * @param listItem
   *          item to remove
   */
  public void remove( ListItem listItem ) {
    this.items.remove( listItem );
    setSelectedIndex( 0 );

    if ( suppressLayout == false ) {
      updateUI();
    }
  }

  /**
   * Removes all items from the list.
   */
  public void removeAll() {
    this.items.clear();
    this.selectedIndex = -1;
    this.selectedItems.clear();

    if ( this.suppressLayout == false ) {
      for ( ChangeListener l : listeners ) {
        l.onChange( this );
      }
    }
    if ( suppressLayout == false ) {
      updateUI();
    }
  }

  /**
   * Removes all items from the list.
   */
  public void clear() {
    removeAll();
  }

  /**
   * Convenience method to support the more conventional method of child attachment
   * 
   * @param label
   */
  public void add( String label ) {
    this.addItem( label );
  }

  /**
   * Adds the given ListItem to the list control.
   * 
   * @param item
   *          ListItem
   */
  public void addItem( ListItem item ) {
    item.setStylePrimaryName( this.primaryStyleName );
    items.add( item );

    item.setListItemListener( this );

    // If first one added, set selectedIndex to 0
    if ( isDefaultSelectionEnabled() && items.size() == 1 && this.visible == 1 ) {
      setSelectedIndex( 0 );
    }

    if ( !suppressLayout ) {
      updateUI();
    }

    if ( dragController != null ) {
      dragController.makeDraggable( item.getWidget() );
    }
  }

  @Override
  public void add( Widget w ) {
    addItem( (ListItem) w );
  }

  /**
   * Call this method with true will suppress the re-laying out of the widget after every add/remove. This is useful
   * when adding a large batch of items to the listbox.
   */
  public void setSuppressLayout( boolean supress ) {
    this.suppressLayout = supress;
    if ( !suppressLayout ) {

      if ( selectedIndex < 0 && this.items.size() > 0 ) {
        this.setSelectedIndex( 0 ); // notifies listeners
      } else {
        // just notify listeners something has changed.
        for ( ChangeListener l : listeners ) {
          l.onChange( this );
        }
      }

      updateUI();
    }
  }

  /**
   * Convenience method creates a {@link: DefaultListItem} with the given text and adds it to the list control
   * 
   * @param label
   */
  public void addItem( String label ) {
    DefaultListItem item = new DefaultListItem( label );
    item.setStylePrimaryName( this.primaryStyleName );
    items.add( item );
    item.setListItemListener( this );

    // If first one added, set selectedIndex to 0
    if ( isDefaultSelectionEnabled() && items.size() == 1 ) {
      setSelectedIndex( 0 );
    }

    if ( !suppressLayout ) {
      updateUI();
    }

    if ( dragController != null ) {
      dragController.makeDraggable( item.getWidget() );
    }
  }

  /**
   * Returns a list of current ListItems that match {@code search}.
   *
   * @param search - value to filter ListItems
   *
   * @return list of filtered ListItems
   */
  public List<ListItem> findItems( String search ) {
    if ( StringUtils.isEmpty( search ) ) {
      return this.items;
    }

    return this.items.stream()
      .filter( item -> item.getText().contains( search ) )
      .collect( Collectors.toList() );
  }

  /**
   * Returns a list of current ListItems.
   * 
   * @return List of ListItems
   */
  public List<ListItem> getItems() {
    return items;
  }

  /**
   * Sets the number of items to be displayed at once in the lsit control. If set to 1 (default) the list is rendered as
   * a drop-down
   * 
   * @param visibleCount
   *          number of rows to be visible.
   */
  public void setVisibleRowCount( int visibleCount ) {
    int prevCount = visible;
    this.visible = visibleCount;

    if ( visible > 1 && prevCount == 1 ) {
      // switched from drop-down to list
      fPanel.remove( dropGrid );
      fPanel.add( listScrollPanel );
      addStyleName( "list-mode" );
    } else if ( visible == 1 && prevCount > 1 ) {
      // switched from list to drop-down
      fPanel.remove( listScrollPanel );
      fPanel.add( dropGrid );
      removeStyleName( "list-mode" );
    }

    if ( suppressLayout == false ) {
      updateUI();
    }
  }

  /**
   * Returns the number of rows visible in the list
   * 
   * @return number of visible rows.
   */
  public int getVisibleRowCount() {
    return visible;
  }

  protected void updateUI() {
    if ( !this.isAttached() ) {
      return;
    }
    if ( visible > 1 ) {
      updateList();
    } else {
      updateDropDown();
    }
  }

  /**
   * Returns the number of rows to be displayed in the drop-down popup.
   * 
   * @return number of visible popup items.
   */
  public int getMaxDropVisible() {
    return maxDropVisible;
  }

  /**
   * Sets the number of items to be visible in the drop-down popup. If set lower than the number of items a scroll-bar
   * with provide access to hidden items.
   * 
   * @param maxDropVisible
   *          number of items visible in popup.
   */
  public void setMaxDropVisible( int maxDropVisible ) {
    this.maxDropVisible = maxDropVisible;

    // Update the popup to respect this value
    if ( maxHeight > 0 ) { // Items already added
      this.popupHeight = this.maxDropVisible * maxHeight + "px"; //$NON-NLS-1$
    }
  }

  protected TextBox editableTextBox;
  private SimplePanel selectedItemWrapper = new SimplePanel();
  private final Label selectedItemPlaceholder = new Label();

  /* Visible for testing */
  Label getSelectedItemPlaceholder() {
    return this.selectedItemPlaceholder;
  }

  public void setSelectedItemPlaceholderText( String placeholder ) {
    this.getSelectedItemPlaceholder().setText( placeholder );
  }

  protected void updateSelectedDropWidget() {
    Widget selectedWidget = getSelectedItemPlaceholder(); // Default to show in case of empty sets?

    if ( !editable ) { // only show their widget if editable is false
      if ( selectedIndex >= 0 ) {
        selectedWidget = items.get( selectedIndex ).getWidgetForDropdown();
      } else if ( isDefaultSelectionEnabled() && !items.isEmpty() ) {
        selectedWidget = items.get( 0 ).getWidgetForDropdown();
      }
    } else {
      String previousVal = editableTextBox.getText();
      String newVal = getAcceptedText();
      if ( newVal == null ) {
        if ( isDefaultSelectionEnabled() && !items.isEmpty() ) {
          newVal = items.get( 0 ).getValue().toString();
        } else {
          newVal = "";
        }
      }

      if ( !previousVal.equals( newVal ) ) {
        editableTextBox.setText( newVal );
      }

      selectedWidget = editableTextBox;
    }

    this.setTdStyles( selectedWidget.getElement() );

    if ( !selectedWidget.equals( selectedItemWrapper.getWidget() ) ) {
      selectedItemWrapper.setWidget( selectedWidget );
    }

    if ( dropGrid.getWidget( 0, 0 ) == null ) {
      dropGrid.setWidget( 0, 0, selectedItemWrapper );
    }
  }

  private String getAcceptedText() {
    if ( this.val != null ) {
      return this.val;
    } else if ( selectedIndex >= 0 ) {
      return items.get( selectedIndex ).getValue().toString();
    }
    return null;
  }

  /**
   * Called by updateUI when the list is not a drop-down (visible row count > 1)
   */
  private void updateList() {
    listPanel.clear();
    maxHeight = 0;
    maxWidth = 0;

    List<ListItem> listItems = findItems( searchTextBox.getValue() );

    // actually going to average up the heights
    for ( ListItem li : listItems ) {
      Widget w = li.getWidget();

      Rectangle rect = ElementUtils.getSize( w.getElement() );
      // we only care about this if the user hasn't specified a height.
      if ( height == null ) {
        maxHeight += rect.height;
      }
      maxWidth = Math.max( maxWidth, rect.width );

      // Add it to the dropdown
      listPanel.add( w );
      listPanel.setCellWidth( w, "100%" );
    }

    if ( height == null && !listItems.isEmpty() ) {
      maxHeight = Math.round( maxHeight / listItems.size() );
    }

    // we only care about this if the user has specified a visible row count and no height
    if ( height == null ) {
      int h = ( this.visible * ( maxHeight + spacing ) );
      this.listScrollPanel.setHeight( h + "px" );
      fPanel.setHeight( h + "px" );
    } else {
      this.listScrollPanel.setHeight( height );
      fPanel.setHeight( height );
    }

    if ( width == null ) {
      this.fPanel.setWidth( maxWidth + 40 + "px" ); // 20 is scrollbar space
    }
  }

  /**
   * Called by updateUI when the list is a drop-down (visible row count = 1)
   */
  private void updateDropDown() {

    // Update Shown selection in grid
    updateSelectedDropWidget();

    if ( editable ) {
      editableTextBox.setTabIndex( 0 );
      fPanel.addStyleName( "focus-container" );
      fPanel.setTabIndex( -1 );
    } else {
      editableTextBox.setTabIndex( -1 );
      fPanel.removeStyleName( "focus-container" );
      fPanel.setTabIndex( this.enabled ? 0 : -1 );
    }

    // Update popup panel,
    // Calculate the size of the largest list item.
    popupVbox.clear();
    maxWidth = 0;
    averageHeight = 0; // Actually used to set the width of the arrow
    popupHeight = null;

    List<ListItem> listItems = findItems( searchTextBox.getValue() );

    int totalHeight = 0;
    for ( ListItem li : listItems ) {
      Widget w = li.getWidget();

      Rectangle rect = ElementUtils.getSize( w.getElement() );
      maxWidth = Math.max( maxWidth, rect.width );
      maxHeight = Math.max( maxHeight, rect.height );
      totalHeight += rect.height;

      // Add it to the dropdown
      popupVbox.add( w );
      popupVbox.setCellWidth( w, "100%" );
    }

    // Average the height of the items
    if ( !items.isEmpty() ) {
      averageHeight = Math.round( totalHeight / listItems.size() );
    }

    // Set the size of the drop-down based on the largest list item
    if ( width == null ) {
      dropGrid.setWidth( ( maxWidth + 60 ) + "px" );
      this.popupWidth = maxWidth + 60 + "px";
    } else if ( width.equals( "100%" ) ) {
      dropGrid.setWidth( "100%" );
      this.popupWidth = maxWidth + ( spacing * 4 ) + maxHeight + "px";
    } else {
      dropGrid.setWidth( "100%" );
      int w = -1;
      if ( width.indexOf( "px" ) > 0 ) {
        w = Integer.parseInt( this.width.replace( "px", "" ) );
      } else if ( width.indexOf( "%" ) > 0 ) {
        w = Integer.parseInt( this.width.replace( "%", "" ) );
      }

      selectedItemWrapper.setWidth( ( w - ( averageHeight + ( this.spacing * 6 ) ) ) + "px" );
    }

    // Store the size of the popup to respect MaxDropVisible now that we know the item height
    // This cannot be set here as the popup is not visible :(

    if ( maxDropVisible > 0 && listItems.size() > maxDropVisible ) {
      // (Lesser of maxDropVisible or items size) * (Average item height + spacing value)
      this.popupHeight =
          ( Math.min( this.maxDropVisible, listItems.size() ) * ( averageHeight + ( this.spacing * 2 ) ) ) + "px";
    } else {
      this.popupHeight = null;
    }

  }

  protected Focusable getFocusableWidget() {
    return visible != 1 || !editable ? fPanel : editableTextBox;
  }

  @Override
  public int getTabIndex() {
    return getFocusableWidget().getTabIndex();
  }

  @Override
  public void setAccessKey( char key ) {
    getFocusableWidget().setAccessKey( key );
  }

  @Override
  public void setFocus( boolean focused ) {
    getFocusableWidget().setFocus( focused );
  }

  @Override
  public void setTabIndex( int index ) {
    getFocusableWidget().setTabIndex( index );
  }


  /**
   * Used internally to hide/show drop-down popup.
   */
  protected void togglePopup() {
    if ( popupShowing == false ) {

      // This delayed instantiation works around a problem with the underlying GWT widgets that
      // throw errors positioning when the GWT app is loaded in a frame that's not visible.

      if ( popup == null ) {
        popup = createPopupPanel();
      }

      int x = this.getElement().getAbsoluteLeft();
      int y = this.getElement().getAbsoluteTop() + this.getElement().getOffsetHeight() + 1;
      int windowH = Window.getClientHeight();
      int windowW = Window.getClientWidth();

      Rectangle popupSize = ElementUtils.getSize( popup.getElement() );
      if ( y + popupSize.height > windowH ) {
        y = windowH - popupSize.height;
      }
      if ( x + popupSize.width > windowW ) {
        x = windowW - popupSize.width;
      }

      popup.setPopupPosition( x, y );

      popup.show();

      updateDropDown();

      // Set the size of the popup calculated in updateDropDown().
      if ( this.popupHeight != null ) {
        this.popupScrollPanel.getElement().getStyle().setProperty( "height", this.popupHeight );
      } else {
        this.popupScrollPanel.getElement().getStyle().clearProperty( "height" );
      }

      if ( this.popupWidth != null ) {
        String w = Math.max( this.getElement().getOffsetWidth() - 2, this.maxWidth + 10 ) + "px";
        this.popupScrollPanel.getElement().getStyle().setProperty( "width", w );
        popup.getElement().getStyle().setProperty( "width", w );
      } else {
        this.popupScrollPanel.getElement().getStyle().clearProperty( "width" );
        popup.getElement().getStyle().clearProperty( "width" );
      }

      scrollSelectedItemIntoView();

      popupShowing = true;
    } else {
      popup.hide();
    }
  }

  /* Visible for testing */
  DropPopupPanel createPopupPanel() {
    DropPopupPanel panel = new DropPopupPanel();
    panel.addPopupListener( this );

    VerticalPanel content = new VerticalFlexPanel();
    if ( isSearchable() ) {
      SearchTextBox search = getSearchTextBox();

      search.addChangeListener( widget -> updateUI() );
      content.add( search );
    }

    content.add( popupScrollPanel );
    panel.add( content );

    return panel;
  }

  /* Visible for testing */
  SearchTextBox getSearchTextBox() {
    return this.searchTextBox;
  }

  /**
   * Scroll to view currently selected widget, if any.
   */
  public void scrollSelectedItemIntoView() {

    // Scroll to view currently selected widget
    // DOM.scrollIntoView(this.getSelectedItem().getWidget().getElement());
    // Side effect of the previous call scrolls the scrollpanel to the right. Compensate here
    // popupScrollPanel.setHorizontalScrollPosition(0);

    if ( selectedIndex < 0 ) {
      return;
    }

    if ( this.visible > 1 ) {
      this.listScrollPanel.ensureVisible( items.get( selectedIndex ).getWidget() );
      return;
    }

    popupScrollPanel.ensureVisible( items.get( selectedIndex ).getWidget() );
  }

  /**
   * Selects the given ListItem in the list.
   * 
   * @param item
   *          ListItem to be selected.
   */
  public void setSelectedItem( ListItem item ) {
    if ( items.contains( item ) == false ) {
      throw new RuntimeException( "Item not in collection" ); //$NON-NLS-1$
    }
    // Clear previously selected item
    if ( selectedIndex > -1 ) {
      items.get( selectedIndex ).onDeselect();
    }

    setSelectedIndex( items.indexOf( item ) );

  }

  protected List<ListItem> selectedItems = new ArrayList<ListItem>();

  protected void handleSelection( ListItem item, Event evt ) {
    if ( !evt.getCtrlKey() && !evt.getShiftKey() && !evt.getMetaKey() ) {
      for ( ListItem itm : selectedItems ) {
        itm.onDeselect();
      }
      if ( selectedIndex > -1 ) {
        items.get( selectedIndex ).onDeselect();
      }
      selectedItems.clear();
      item.onSelect();
      if ( !selectedItems.contains( item ) ) {
        selectedItems.add( item );
      }
      selectedIndex = items.indexOf( item );

      scrollSelectedItemIntoView();
    } else if ( evt.getShiftKey() ) {
      int idxOfNewSelection = items.indexOf( item );
      int startIdx = Math.min( selectedIndex, idxOfNewSelection );
      int endIndex = Math.max( selectedIndex, idxOfNewSelection );

      for ( int i = startIdx; i <= endIndex; i++ ) {
        if ( !selectedItems.contains( items.get( i ) ) ) {
          selectedItems.add( items.get( i ) );
        }
        items.get( i ).onSelect();
      }

    } else if ( evt.getCtrlKey() || evt.getMetaKey() ) {
      if ( selectedItems.remove( item ) ) {
        item.onDeselect();
      } else {
        item.onSelect();

        if ( !selectedItems.contains( item ) ) {
          selectedItems.add( item );
        }
      }
    } else {
      if ( !selectedItems.contains( item ) ) {
        selectedItems.add( item );
      }
    }

    if ( this.suppressLayout == false ) {
      for ( ChangeListener l : listeners ) {
        l.onChange( this );
      }
    }

  }

  /**
   * Selects the ListItem at the given index (zero-based)
   * 
   * @param idx
   *          index of ListItem to select
   */
  public void setSelectedIndex( int idx ) {
    if ( idx > items.size() ) {
      throw new RuntimeException( "Index out of bounds: " + idx ); //$NON-NLS-1$
    }
    // De-Select the current
    if ( selectedIndex > -1 ) {
      items.get( selectedIndex ).onDeselect();
    }

    selectedItems.clear();
    if ( idx >= 0 && items.size() > idx ) {
      selectedItems.add( items.get( idx ) );
    }

    int prevIdx = selectedIndex;
    if ( idx >= 0 ) {
      selectedIndex = idx;
      items.get( idx ).onSelect();

      this.val = null;
      if ( visible == 1 && this.isAttached() ) {
        scrollSelectedItemIntoView();
      }
      updateSelectedDropWidget();
    }

    if ( this.suppressLayout == false && prevIdx != idx ) {
      for ( ChangeListener l : listeners ) {
        l.onChange( this );
      }
    }
  }

  public void setSelectedIndices( int[] indices ) {
    if ( multiSelect == false ) {
      // throw new IllegalStateException("Cannot select more than one item in a combobox");
      if ( indices.length > 0 ) {
        setSelectedIndex( indices[0] );
      }
      return;
    }
    for ( ListItem item : selectedItems ) {
      item.onDeselect();
    }
    selectedItems.clear();
    for ( int i = 0; i < indices.length; i++ ) {
      int idx = indices[i];
      if ( idx >= 0 && idx < items.size() ) {
        items.get( idx ).onSelect();
        selectedItems.add( items.get( idx ) );
      }
    }

    if ( this.suppressLayout == false ) {
      for ( ChangeListener l : listeners ) {
        l.onChange( this );
      }
    }
  }

  public boolean isDefaultSelectionEnabled() {
    return this.defaultSelectionEnabled;
  }

  public void setDefaultSelectionEnabled( boolean value ) {
    this.defaultSelectionEnabled = value;
  }

  /**
   * Registers a ChangeListener with the list.
   * 
   * @param listener ChangeListener
   */
  public void addChangeListener( ChangeListener listener ) {
    listeners.add( listener );
  }

  /**
   * Removes to given ChangeListener from list.
   * 
   * @param listener
   *          ChangeListener
   */
  public void removeChangeListener( ChangeListener listener ) {
    this.listeners.remove( listener );
  }

  /**
   * Returns the selected index of the list (zero-based)
   * 
   * @return Integer index
   */
  public int getSelectedIndex() {
    return selectedIndex;
  }

  /**
   * Returns the number of listitems in the box
   * 
   * @return number of children
   */
  public int getSize() {
    return this.items.size();
  }

  /**
   * Returns the currently selected item
   * 
   * @return currently selected Item
   */
  public ListItem getSelectedItem() {
    if ( selectedIndex < 0 || selectedIndex > items.size() ) {
      return null;
    }

    return items.get( selectedIndex );
  }

  public List<ListItem> getSelectedItems() {
    return new ArrayList<ListItem>( selectedItems );
  }

  public int[] getSelectedIndices() {
    int[] selectedIndices = new int[selectedItems.size()];
    for ( int i = 0; i < selectedItems.size(); i++ ) {
      selectedIndices[i] = items.indexOf( selectedItems.get( i ) );
    }
    return selectedIndices;
  }

  @Override
  public void setStylePrimaryName( String s ) {
    super.setStylePrimaryName( s );
    addStyleName( VerticalFlexPanel.STYLE_NAME );
    this.primaryStyleName = s;

    // This may have came in late. Update ListItems
    for ( ListItem item : items ) {
      item.setStylePrimaryName( s );
    }
  }

  @Override
  protected void onAttach() {
    super.onAttach();
    updateUI();
  }

  @Override
  /**
   * Calling setHeight will implecitly change the list from a drop-down style to a list style.
   */
  public void setHeight( String s ) {
    this.height = s;
    // user has specified height, focusPanel needs to be 100%;
    this.fPanel.setHeight( s );
    this.listScrollPanel.setHeight( "100%" ); //$NON-NLS-1$
    if ( visible == 1 ) {
      this.setVisibleRowCount( 15 );
    }
    super.setHeight( s );
    updateUI();
  }

  @Override
  public void setWidth( String s ) {
    fPanel.setWidth( s );
    this.listScrollPanel.setWidth( "100%" ); //$NON-NLS-1$
    this.width = s;
    this.popupWidth = s;
    if ( s != null ) {
      dropGrid.setWidth( "100%" ); //$NON-NLS-1$
    }
    super.setWidth( s );
    updateUI();
  }

  // ======================================= Listener methods ===================================== //

  public void onPopupClosed( PopupPanel popupPanel, boolean b ) {
    this.getSearchTextBox().clearText();
    this.popupShowing = false;
  }

  public void onMouseDown( Widget widget, int i, int i1 ) {
  }

  public void onMouseEnter( Widget widget ) {
  }

  public void onMouseLeave( Widget widget ) {
  }

  public void onMouseMove( Widget widget, int i, int i1 ) {
  }

  public void onMouseUp( Widget widget, int i, int i1 ) {
    if ( isEnabled() == false ) {
      return;
    }
    if ( visible == 1 ) { // drop-down mode
      this.togglePopup();
    }
  }

  public void onFocus( Widget widget ) {
    if ( isEnabled() == false ) {
      return;
    }
    // fPanel.setFocus(true);
  }

  public void onLostFocus( Widget widget ) {
  }

  private int shiftOriginIdx = -1;

  public void onKeyDown( Widget widget, char c, int i ) {
    Event event = Event.getCurrentEvent();
    if ( c == 16 ) { // shift
      shiftOriginIdx = selectedIndex;
    }
    switch( event.getKeyCode() ){
      case KeyCodes.KEY_UP:
      case KeyCodes.KEY_DOWN:
        event.preventDefault();
        break;
      case KeyCodes.KEY_TAB:
      case KeyCodes.KEY_ESCAPE:
        if( popupShowing ){
          popup.hide();
        }
        break;
      default:
        break;
    }
  }

  public void onKeyPress( Widget widget, char c, int i ) {
  }

  public void onKeyUp( Widget widget, char c, int i ) {
    if ( isEnabled() == false ) {
      return;
    }
    if ( c == 16 ) {
      shiftOriginIdx = -1;
    }
    boolean fireEvents = false;
    switch ( c ) {
      case 38: // UP
        if ( selectedIndex > 0 ) {

          if ( multiSelect && !Event.getCurrentEvent().getShiftKey() ) {
            for ( ListItem itm : selectedItems ) {
              itm.onDeselect();
            }
            selectedItems.clear();
            ListItem itm = items.get( selectedIndex - 1 );
            selectedIndex = selectedIndex - 1;
            itm.onSelect();
            if ( !selectedItems.contains( itm ) ) {
              selectedItems.add( itm );
            }
            fireEvents = true;
            this.listScrollPanel.ensureVisible( itm.getWidget() );
          } else if ( multiSelect && Event.getCurrentEvent().getShiftKey() ) {

            ListItem itm = items.get( selectedIndex - 1 );

            if ( !selectedItems.contains( itm ) ) {
              selectedItems.add( itm );
            }
            itm.onSelect();
            this.listScrollPanel.ensureVisible( itm.getWidget() );

            ListItem prevItem = items.get( selectedIndex );
            if ( selectedIndex != shiftOriginIdx
                && shiftOriginIdx < selectedIndex
                && selectedItems.contains( prevItem ) ) {
              selectedItems.remove( prevItem );
              prevItem.onDeselect();
            }

            selectedIndex = selectedIndex - 1;

            fireEvents = true;
          } else {
            setSelectedIndex( selectedIndex - 1 );
            scrollSelectedItemIntoView();
          }
          if ( editable ) {
            editableTextBox.selectAll();
          }
        }

        break;
      case 40: // Down
        if ( selectedIndex < items.size() - 1 ) {
          if ( multiSelect && !Event.getCurrentEvent().getShiftKey() ) {
            for ( ListItem itm : selectedItems ) {
              itm.onDeselect();
            }
            selectedItems.clear();
            ListItem itm = items.get( selectedIndex + 1 );
            selectedIndex = selectedIndex + 1;
            itm.onSelect();
            if ( !selectedItems.contains( itm ) ) {
              selectedItems.add( itm );
            }
            fireEvents = true;
            this.listScrollPanel.ensureVisible( itm.getWidget() );
          } else if ( multiSelect && Event.getCurrentEvent().getShiftKey() ) {

            ListItem itm = items.get( selectedIndex + 1 );

            if ( !selectedItems.contains( itm ) ) {
              selectedItems.add( itm );
            }
            itm.onSelect();

            ListItem prevItem = items.get( selectedIndex );
            if ( selectedIndex != shiftOriginIdx
                && shiftOriginIdx > selectedIndex
                && selectedItems.contains( prevItem ) ) {
              selectedItems.remove( prevItem );
              prevItem.onDeselect();
            }

            this.listScrollPanel.ensureVisible( itm.getWidget() );

            selectedIndex = selectedIndex + 1;

            fireEvents = true;
          } else {
            setSelectedIndex( selectedIndex + 1 );
            scrollSelectedItemIntoView();
          }
          if ( editable ) {
            editableTextBox.selectAll();
          }
        }
        break;
      case 13: // Enter
      case 32: // Space
        // Popup should be only shown for drop-down
        if ( this.getVisibleRowCount() == 1 ) {
          this.togglePopup();
        }
        break;
      case 65: // A
        if ( Event.getCurrentEvent().getCtrlKey() ) {
          for ( ListItem item : items ) {
            item.onSelect();
          }
          selectedItems.clear();
          selectedItems.addAll( items );

          fireEvents = true;
        }
        break;

    }
    if ( fireEvents && this.suppressLayout == false ) {
      for ( ChangeListener l : listeners ) {
        l.onChange( this );
      }
    }
  }

  public void setCommand( Command command ) {
    this.command = command;
  }

  // ====================================== Listener Implementations =========================== //

  public void itemSelected( ListItem listItem, Event event ) {
    setFocus( true );
    if ( multiSelect ) {
      handleSelection( listItem, event );
    } else {
      setSelectedItem( listItem );
    }

    // Drop-down mode
    if ( visible == 1 && popupShowing ) {
      togglePopup();
    }

    if ( editable ) {
      editableTextBox.selectAll();
    }
  }

  public void doAction( ListItem listItem ) {
    if ( command != null ) {
      command.execute();
    }
  }

  // ======================================= Inner Classes ===================================== //

  /**
   * Panel used as a drop-down popup.
   */
  private class DropPopupPanel extends PopupPanel {
    public DropPopupPanel() {
      super( true );

      setStyleName( "drop-popup" );
      addStyleName( "responsive" );
    }

    @Override
    public boolean onEventPreview( Event event ) {
      if ( DOM.isOrHasChild( CustomListBox.this.getElement(), DOM.eventGetTarget( event ) ) ) {

        return true;
      }
      return super.onEventPreview( event );
    }

    @Override
    public boolean onKeyDownPreview( char key, int modifiers ) {
      switch ( key ) {
        case KeyboardListener.KEY_ESCAPE:
          popup.hide();
          break;
      }
      return true;
    }
  }

  /**
   * Panel contained in the popup
   */
  private class PopupList extends VerticalPanel {

    public PopupList() {
      this.sinkEvents( Event.MOUSEEVENTS );
    }

    @Override
    public void onBrowserEvent( Event event ) {
      super.onBrowserEvent( event );
    }
  }

  /**
   * This is the arrow rendered in the drop-down.
   */
  protected class DropDownArrow extends SimplePanel {
    private SimplePanel img;
    private boolean enabled = true;

    public DropDownArrow() {
      img = new SimplePanel();

      this.setStylePrimaryName( "combo-arrow" ); //$NON-NLS-1$
      super.add( img );
      ElementUtils.preventTextSelection( this.getElement() );
    }

    public void setEnabled( boolean enabled ) {
      if ( this.enabled == enabled ) {
        return;
      }
      this.enabled = enabled;
      if ( enabled ) {
        this.setStylePrimaryName( "combo-arrow" ); //$NON-NLS-1$
      } else {
        this.setStylePrimaryName( "combo-arrow-disabled" ); //$NON-NLS-1$
      }

    }
  }

  /**
   * Setting editable to true allows the user to specify their own value for the combobox.
   * 
   * @param editable
   */
  public void setEditable( boolean editable ) {
    this.editable = editable;
    this.updateUI();
  }

  public boolean isEditable() {
    return this.editable;
  }

  /**
   * Returns the user-entered value in the case of an editable drop-down
   * 
   * @return Value user has entered
   */
  public String getValue() {
    if ( !editable ) {
      if ( getSelectedItem() != null ) {
        return getSelectedItem().getText();
      } else {
        return null;
      }
    } else {
      return ( editableTextBox != null ) ? editableTextBox.getText() : null;
    }
  }

  boolean isTextSame( String text ) {
    return Objects.equals( this.val, text );
  }

  public void setValue( String text ) {
    if ( isTextSame( text ) ) {
      return;
    }

    this.val = text;

    if ( editable ) {
      editableTextBox.setText( text );
      selectedIndex = -1;
      this.onChange( editableTextBox );
    } else {
      for ( int i = 0; i < items.size(); i++ ) {
        if ( items.get( i ).getText().equals( text ) ) {
          setSelectedIndex( i );
          return;
        }
      }
    }
  }

  public void onChange( Widget sender ) {
    for ( ChangeListener l : listeners ) {
      l.onChange( this );
    }
  }

  public void setEnabled( boolean enabled ) {
    this.enabled = enabled;
    if ( editableTextBox != null ) {
      editableTextBox.setEnabled( enabled );
    }
    arrow.setEnabled( enabled );
    fPanel.setTabIndex( this.enabled ? 0 : -1 );
    this.setStylePrimaryName( this.enabled ? "custom-list" : "custom-list-disabled" );
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setSearchable( boolean searchable ) {
    this.searchable = searchable;
  }

  public boolean isSearchable() {
    return this.searchable;
  }

  public Widget createProxy() {
    DefaultListItem item = new DefaultListItem();
    item.setText( getSelectedItem().getText() );
    return item;
  }

  public void setDragController( DragController controller ) {
    dragController = controller;
    if ( this.items.size() > 0 ) {
      for ( ListItem item : items ) {
        dragController.makeDraggable( item.getWidget() );
      }
    }
  }

  public boolean isMultiSelect() {
    return multiSelect;
  }

  public void setMultiSelect( boolean multiSelect ) {
    this.multiSelect = multiSelect;
  }
}
