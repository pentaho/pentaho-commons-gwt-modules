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

import com.allen_sauer.gwt.dnd.client.DragController;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.pentaho.gwt.widgets.client.panel.PentahoFocusPanel;
import org.pentaho.gwt.widgets.client.text.SearchTextBox;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings( "deprecation" )
@RunWith( GwtMockitoTestRunner.class )
@WithClassesToStub( DefaultListItem.class )
public class CustomListBoxTest {
  @Mock
  private CustomListBox customListBox;

  @Mock
  private SearchTextBox searchTextBox;
  @Mock
  private Label selectedItemPlaceholder;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks( this );

    when( customListBox.isDefaultSelectionEnabled() ).thenReturn( true );
    when( customListBox.isSearchable() ).thenReturn( false );
    when( customListBox.getSearchTextBox() ).thenReturn( searchTextBox );
    when( customListBox.getSelectedItemPlaceholder() ).thenReturn( selectedItemPlaceholder );

    customListBox.items = mock( List.class );
  }

  @Test
  public void testSetTableLayout() throws Exception {
    doCallRealMethod().when( customListBox ).setTableLayout( anyString() );

    customListBox.dropGrid = mock( FlexTable.class );
    final Element dropGridElement = mock( Element.class );
    when( customListBox.dropGrid.getElement() ).thenReturn( dropGridElement );
    final Style style = mock( Style.class );
    when( dropGridElement.getStyle() ).thenReturn( style );

    final String tableLayout = "tableLayout";
    customListBox.setTableLayout( tableLayout );
    verify( style ).setProperty( "tableLayout", tableLayout );

    customListBox.setTableLayout( "" );
    verify( style ).clearProperty( "tableLayout" );

    customListBox.setTableLayout( null );
    verify( style, times( 2 ) ).clearProperty( "tableLayout" );
  }

  @Test
  public void testRemove() throws Exception {
    doCallRealMethod().when( customListBox ).remove( any( ListItem.class ) );

    final ListItem listItem = mock( ListItem.class );
    customListBox.suppressLayout = true;
    customListBox.remove( listItem );

    verify( customListBox.items ).remove( listItem );
    verify( customListBox ).setSelectedIndex( 0 );
    verify( customListBox, never() ).updateUI();

    customListBox.suppressLayout = false;
    customListBox.remove( listItem );

    verify( customListBox.items, times( 2 ) ).remove( listItem );
    verify( customListBox, times( 2 ) ).setSelectedIndex( 0 );
    verify( customListBox ).updateUI();
  }

  @Test
  public void testRemoveAll() throws Exception {
    doCallRealMethod().when( customListBox ).removeAll();

    customListBox.selectedItems = mock( List.class );

    customListBox.suppressLayout = true;
    customListBox.selectedIndex = 1;
    final ChangeListener changeListener = mock( ChangeListener.class );
    customListBox.listeners = new LinkedList<ChangeListener>() { {
        add( changeListener );
      } };
    customListBox.removeAll();

    verify( customListBox.items ).clear();
    verify( customListBox.selectedItems ).clear();
    assertEquals( -1, customListBox.selectedIndex );
    verify( changeListener, never() ).onChange( customListBox );
    verify( customListBox, never() ).updateUI();

    customListBox.suppressLayout = false;
    customListBox.selectedIndex = 1;
    customListBox.removeAll();

    verify( customListBox.items, times( 2 ) ).clear();
    verify( customListBox.selectedItems, times( 2 ) ).clear();
    assertEquals( -1, customListBox.selectedIndex );
    verify( changeListener ).onChange( customListBox );
    verify( customListBox ).updateUI();
  }

  // region addItem( ListItem )
  @Test
  public void testAddItem_ListItem() {
    doCallRealMethod().when( customListBox ).addItem( any( ListItem.class ) );

    final ListItem listItem = mock( ListItem.class );
    customListBox.addItem( listItem );

    verify( customListBox.items, times( 1 ) ).add( listItem );

    verify( customListBox, never() ).setSelectedIndex( 0 );
    verify( customListBox, times( 1 ) ).updateUI();
  }

  @Test
  public void testAddItem_ListItemWithSuppressLayout() {
    doCallRealMethod().when( customListBox ).addItem( any( ListItem.class ) );
    customListBox.suppressLayout = true;

    final ListItem listItem = mock( ListItem.class );
    customListBox.addItem( listItem );

    verify( customListBox.items, times( 1 ) ).add( listItem );
    verify( customListBox, never() ).updateUI();
  }

  @Test
  public void testAddItem_ListItemWithDragController() {
    doCallRealMethod().when( customListBox ).addItem( any( ListItem.class ) );
    customListBox.dragController = mock( DragController.class );

    final ListItem listItem = mock( ListItem.class );
    customListBox.addItem( listItem );

    verify( customListBox.items, times( 1 ) ).add( listItem );
    verify( customListBox.dragController, times( 1 ) ).makeDraggable( any( Widget.class ) );
  }

  @Test
  public void testAddItem_ListItemFirstWithDefaultSelection() {
    doCallRealMethod().when( customListBox ).addItem( any( ListItem.class ) );

    // make this first item
    when( customListBox.items.size() ).thenReturn( 1 );
    customListBox.visible = 1;

    final ListItem listItem = mock( ListItem.class );
    customListBox.addItem( listItem );

    verify( customListBox.items, times( 1 ) ).add( listItem );
    verify( customListBox, times( 1 ) ).setSelectedIndex( 0 );
  }

  @Test
  public void testAddItem_ListItemFirstWithDefaultSelectionDisabled() {
    doCallRealMethod().when( customListBox ).addItem( any( ListItem.class ) );

    // make this first item
    when( customListBox.items.size() ).thenReturn( 1 );
    when( customListBox.isDefaultSelectionEnabled() ).thenReturn( false );

    final ListItem listItem = mock( ListItem.class );
    customListBox.addItem( listItem );

    verify( customListBox.items ).add( listItem );
    verify( customListBox, never() ).setSelectedIndex( 0 );
  }
  // endregion addItem( ListItem )

  // region addItem( String )
  @Test
  public void testAddItem_String() {
    doCallRealMethod().when( customListBox ).addItem( anyString() );

    customListBox.addItem( "item" );

    verify( customListBox.items, times( 1 ) ).add( any( DefaultListItem.class ) );
    verify( customListBox, never() ).setSelectedIndex( 0 );
    verify( customListBox, times( 1 ) ).updateUI();
  }

  @Test
  public void testAddItem_StringWithSuppressLayout() {
    doCallRealMethod().when( customListBox ).addItem( anyString() );
    customListBox.suppressLayout = true;

    customListBox.addItem( "item" );

    verify( customListBox.items, times( 1 ) ).add( any( DefaultListItem.class ) );
    verify( customListBox, never() ).updateUI();
  }

  @Test
  public void testAddItem_StringWithDragController() {
    doCallRealMethod().when( customListBox ).addItem( anyString() );
    customListBox.dragController = mock( DragController.class );

    customListBox.addItem( "item" );

    verify( customListBox.items, times( 1 ) ).add( any( DefaultListItem.class ) );
    verify( customListBox.dragController, times( 1 ) ).makeDraggable( any( Widget.class ) );
  }

  @Test
  public void testAddItem_StringFirstWithDefaultSelection() {
    doCallRealMethod().when( customListBox ).addItem( anyString() );

    // make this first item
    when( customListBox.items.size() ).thenReturn( 1 );

    customListBox.addItem( "item" );

    verify( customListBox.items, times( 1 ) ).add( any( DefaultListItem.class ) );
    verify( customListBox, times( 1 ) ).setSelectedIndex( 0 );
  }

  @Test
  public void testAddItem_StringFirstWithDefaultSelectionDisabled() {
    doCallRealMethod().when( customListBox ).addItem( anyString() );

    // make this first item
    when( customListBox.items.size() ).thenReturn( 1 );
    when( customListBox.isDefaultSelectionEnabled() ).thenReturn( false );

    customListBox.addItem( "item" );

    verify( customListBox.items, times( 1 ) ).add( any( DefaultListItem.class ) );
    verify( customListBox, never() ).setSelectedIndex( 0 );
  }
  // endregion addItem( String )

  @Test
  public void testSetSuppressLayout() throws Exception {
    doCallRealMethod().when( customListBox ).setSuppressLayout( anyBoolean() );

    customListBox.setSuppressLayout( true );
    verify( customListBox, never() ).updateUI();

    customListBox.selectedIndex = -1;
    when( customListBox.items.size() ).thenReturn( 2 );
    customListBox.setSuppressLayout( false );
    verify( customListBox ).updateUI();
    verify( customListBox ).setSelectedIndex( 0 );

    customListBox.selectedIndex = 1;
    final ChangeListener changeListener = mock( ChangeListener.class );
    customListBox.listeners = new LinkedList<ChangeListener>() { {
      add( changeListener );
    } };
    customListBox.setSuppressLayout( false );
    verify( customListBox, times( 2 ) ).updateUI();
    verify( changeListener ).onChange( customListBox );
  }

  @Test
  public void testSetVisibleRowCount() throws Exception {
    doCallRealMethod().when( customListBox ).setVisibleRowCount( anyInt() );

    customListBox.fPanel = mock( PentahoFocusPanel.class );
    customListBox.dropGrid = mock( FlexTable.class );
    customListBox.listScrollPanel = mock( ScrollPanel.class );

    customListBox.visible = 1;
    customListBox.suppressLayout = true;
    customListBox.setVisibleRowCount( 1 );
    verify( customListBox.fPanel, never() ).remove( any( Widget.class ) );
    verify( customListBox.fPanel, never() ).add( any( Widget.class ) );
    verify( customListBox, never() ).updateUI();

    customListBox.visible = 1;
    customListBox.suppressLayout = false;
    customListBox.setVisibleRowCount( 2 );
    verify( customListBox.fPanel ).remove( customListBox.dropGrid );
    verify( customListBox.fPanel ).add( customListBox.listScrollPanel );
    verify( customListBox ).updateUI();

    customListBox.visible = 2;

    customListBox.setVisibleRowCount( 1 );
    verify( customListBox.fPanel ).remove( customListBox.listScrollPanel );
    verify( customListBox.fPanel ).add( customListBox.dropGrid );
    verify( customListBox, times( 2 ) ).updateUI();
  }

  @Test
  public void testSetMaxDropVisible() throws Exception {
    doCallRealMethod().when( customListBox ).setMaxDropVisible( anyInt() );

    customListBox.maxHeight = 3;
    final int maxDropVisible = 5;
    customListBox.setMaxDropVisible( maxDropVisible );
    assertEquals( maxDropVisible * customListBox.maxHeight + "px", customListBox.popupHeight );
  }

  @Test
  public void testSetSelectedItem() throws Exception {
    doCallRealMethod().when( customListBox ).setSelectedItem( any( ListItem.class ) );

    final ListItem item = mock( ListItem.class );

    when( customListBox.items.contains( item ) ).thenReturn( false );
    try {
      customListBox.setSelectedItem( item );
      fail();
    } catch ( RuntimeException e ) {
      // it's OK
    }

    when( customListBox.items.contains( item ) ).thenReturn( true );
    final int index = 2;
    customListBox.selectedIndex = index;
    final ListItem selectedItem = mock( ListItem.class );
    when( customListBox.items.get( index ) ).thenReturn( selectedItem );
    final Integer selectionIndex = 5;
    when( customListBox.items.indexOf( item ) ).thenReturn( selectionIndex );
    customListBox.setSelectedItem( item );
    verify( selectedItem ).onDeselect();
    verify( customListBox ).setSelectedIndex( selectionIndex );
  }

  @Test
  public void testSetSelectedIndex() throws Exception {
    doCallRealMethod().when( customListBox ).setSelectedIndex( anyInt() );

    final int itemsSize = 5;
    when( customListBox.items.size() ).thenReturn( itemsSize );
    try {
      customListBox.setSelectedIndex( 8 );
      fail();
    } catch ( RuntimeException e ) {
      // it's OK
    }

    final int index = 4;
    final ListItem selectedItem = mock( ListItem.class );
    customListBox.selectedItems = mock( List.class );
    final int prevSelectedIndex = 2;
    customListBox.selectedIndex = prevSelectedIndex;
    final ListItem prevSelectedItem = mock( ListItem.class );
    when( customListBox.items.get( prevSelectedIndex ) ).thenReturn( prevSelectedItem );
    when( customListBox.items.get( index ) ).thenReturn( selectedItem );
    when( customListBox.isAttached() ).thenReturn( true );
    customListBox.visible = 1;
    final ChangeListener changeListener = mock( ChangeListener.class );
    customListBox.listeners = new LinkedList<ChangeListener>() { {
        add( changeListener );
      } };
    customListBox.setSelectedIndex( index );
    verify( prevSelectedItem ).onDeselect();
    verify( customListBox.selectedItems ).clear();
    verify( customListBox.selectedItems ).add( selectedItem );
    assertEquals( index, customListBox.selectedIndex );
    verify( selectedItem ).onSelect();
    verify( customListBox ).scrollSelectedItemIntoView();
    verify( customListBox ).updateSelectedDropWidget();
    verify( changeListener ).onChange( customListBox );
  }

  @Test
  public void testSetSelectedIndices() throws Exception {
    doCallRealMethod().when( customListBox ).setSelectedIndices( any( int[].class ) );

    customListBox.multiSelect = false;
    customListBox.setSelectedIndices( new int[] { 2, 3 } );
    verify( customListBox ).setSelectedIndex( 2 );
    verify( customListBox ).setSelectedIndex( anyInt() );

    customListBox.multiSelect = true;
    final ListItem prevSelectedItem = mock( ListItem.class );
    customListBox.selectedItems = new LinkedList<ListItem>() { {
        add( prevSelectedItem );
      } };
    final ChangeListener changeListener = mock( ChangeListener.class );
    customListBox.listeners = new LinkedList<ChangeListener>() { {
        add( changeListener );
      } };
    final ListItem selectedItem1 = mock( ListItem.class );
    final ListItem selectedItem2 = mock( ListItem.class );
    when( customListBox.items.get( 2 ) ).thenReturn( selectedItem1 );
    when( customListBox.items.get( 3 ) ).thenReturn( selectedItem2 );
    when( customListBox.items.size() ).thenReturn( 5 );
    customListBox.setSelectedIndices( new int[] { 2, 3 } );
    verify( prevSelectedItem ).onDeselect();
    assertEquals( 2, customListBox.selectedItems.size() );
    assertEquals( selectedItem1, customListBox.selectedItems.get( 0 ) );
    assertEquals( selectedItem2, customListBox.selectedItems.get( 1 ) );
    verify( selectedItem1 ).onSelect();
    verify( selectedItem2 ).onSelect();
    verify( changeListener ).onChange( customListBox );
  }

  @Test
  public void testGetSelectedItem() throws Exception {
    doCallRealMethod().when( customListBox ).getSelectedItem();

    customListBox.selectedIndex = -1;
    assertNull( customListBox.getSelectedItem() );

    customListBox.selectedIndex = 10;
    when( customListBox.items.size() ).thenReturn( 5 );
    assertNull( customListBox.getSelectedItem() );

    customListBox.selectedIndex = 3;
    final ListItem item = mock( ListItem.class );
    when( customListBox.items.get( customListBox.selectedIndex ) ).thenReturn( item );
    assertEquals( item, customListBox.getSelectedItem() );
  }

  @Test
  public void testGetSelectedIndices() throws Exception {
    doCallRealMethod().when( customListBox ).getSelectedIndices();

    final ListItem selItem1 = mock( ListItem.class );
    final ListItem selItem2 = mock( ListItem.class );
    customListBox.selectedItems = new LinkedList<ListItem>() { {
        add( selItem1 );
        add( selItem2 );
      } };
    customListBox.items = new LinkedList<ListItem>() { {
        add( selItem1 );
        add( mock( ListItem.class ) );
        add( selItem2 );
      } };

    final int[] selectedIndices = customListBox.getSelectedIndices();
    assertEquals( 2, selectedIndices.length );
    assertEquals( 0, selectedIndices[0] );
    assertEquals( 2, selectedIndices[1] );
  }

  @Test
  public void testSetStylePrimaryName() throws Exception {
    doCallRealMethod().when( customListBox ).setStylePrimaryName( anyString() );

    final ListItem item = mock( ListItem.class );
    customListBox.items = new LinkedList<ListItem>() { {
        add( item );
      } };
    final String stylePrimaryName = "test";
    customListBox.setStylePrimaryName( stylePrimaryName );

    verify( item ).setStylePrimaryName( stylePrimaryName );
  }

  @Test
  public void testSetHeight() throws Exception {
    doCallRealMethod().when( customListBox ).setHeight( anyString() );

    customListBox.fPanel = mock( PentahoFocusPanel.class );
    customListBox.listScrollPanel = mock( ScrollPanel.class );
    customListBox.visible = 1;
    final String height = "100px";
    customListBox.setHeight( height );

    verify( customListBox.fPanel ).setHeight( height );
    verify( customListBox.listScrollPanel ).setHeight( "100%" );
    verify( customListBox ).setVisibleRowCount( 15 );
    verify( customListBox ).updateUI();
  }

  @Test
  public void testSetWidth() throws Exception {
    doCallRealMethod().when( customListBox ).setWidth( anyString() );

    customListBox.fPanel = mock( PentahoFocusPanel.class );
    customListBox.listScrollPanel = mock( ScrollPanel.class );
    customListBox.dropGrid = mock( FlexTable.class );
    customListBox.visible = 1;
    final String width = "100px";
    customListBox.setWidth( width );

    verify( customListBox.fPanel ).setWidth( width );
    verify( customListBox.listScrollPanel ).setWidth( "100%" );
    verify( customListBox.dropGrid ).setWidth( "100%" );
    verify( customListBox ).updateUI();
  }

  @Test
  public void testOnMouseUp() throws Exception {
    doCallRealMethod().when( customListBox ).onMouseUp( any( Widget.class ), anyInt(), anyInt() );

    customListBox.visible = 1;
    when( customListBox.isEnabled() ).thenReturn( false );
    customListBox.onMouseUp( mock( Widget.class ), 0, 0 );
    verify( customListBox, never() ).togglePopup();

    customListBox.visible = -1;
    when( customListBox.isEnabled() ).thenReturn( true );
    customListBox.onMouseUp( mock( Widget.class ), 0, 0 );
    verify( customListBox, never() ).togglePopup();

    customListBox.visible = 1;
    when( customListBox.isEnabled() ).thenReturn( true );
    customListBox.onMouseUp( mock( Widget.class ), 0, 0 );
    verify( customListBox ).togglePopup();
  }

  @Test
  public void testItemSelected() throws Exception {
    doCallRealMethod().when( customListBox ).itemSelected( any( ListItem.class ), any( Event.class ) );

    customListBox.fPanel = mock( PentahoFocusPanel.class );
    final ListItem listItem = mock( ListItem.class );
    final Event event = mock( Event.class );

    customListBox.multiSelect = true;
    customListBox.itemSelected( listItem, event );
    verify( customListBox ).setFocus( true );
    verify( customListBox ).handleSelection( listItem, event );
    verify( customListBox, never() ).setSelectedItem( any( ListItem.class ) );
    verify( customListBox, never() ).togglePopup();

    customListBox.multiSelect = false;
    customListBox.visible = 1;
    customListBox.popupShowing = true;
    customListBox.itemSelected( listItem, event );
    verify( customListBox, times( 2 ) ).setFocus( true );
    verify( customListBox ).handleSelection( listItem, event );
    verify( customListBox ).setSelectedItem( any( ListItem.class ) );
    verify( customListBox ).togglePopup();
  }

  @Test
  public void testSetEditable() throws Exception {
    doCallRealMethod().when( customListBox ).setEditable( anyBoolean() );

    customListBox.setEditable( true );
    verify( customListBox ).updateUI();

    customListBox.setEditable( false );
    verify( customListBox, times( 2 ) ).updateUI();
  }

  @Test
  public void testGetValue() throws Exception {
    doCallRealMethod().when( customListBox ).getValue();

    customListBox.editable = true;
    assertNull( customListBox.getValue() );

    customListBox.editableTextBox = mock( TextBox.class );
    final String test = "test";
    when( customListBox.editableTextBox.getText() ).thenReturn( test );
    assertEquals( test, customListBox.getValue() );

    customListBox.editable = false;
    assertNull( customListBox.getValue() );

    final ListItem item = mock( ListItem.class );
    when( item.getText() ).thenReturn( test );
    when( customListBox.getSelectedItem() ).thenReturn( item );
    assertEquals( test, customListBox.getValue() );
  }

  @Test
  public void testSetValue() throws Exception {
    doCallRealMethod().when( customListBox ).setValue( anyString() );

    customListBox.editable = true;
    customListBox.editableTextBox = mock( TextBox.class );
    final String test = "test";
    customListBox.selectedIndex = 5;
    when( customListBox.isTextSame( test ) ).thenReturn( false );
    customListBox.setValue( test );
    verify( customListBox.editableTextBox ).setText( test );
    assertEquals( -1, customListBox.selectedIndex );
    verify( customListBox ).onChange( customListBox.editableTextBox );

    customListBox.editable = false;
    final ListItem item = mock( ListItem.class );
    when( item.getText() ).thenReturn( test );
    customListBox.items = new LinkedList<ListItem>() { {
        add( item );
        add( item );
      } };
    customListBox.setValue( test );
    verify( customListBox ).setSelectedIndex( 0 );
  }

  @Test
  public void testOnChange() throws Exception {
    doCallRealMethod().when( customListBox ).onChange( any( Widget.class ) );

    final ChangeListener listener = mock( ChangeListener.class );
    customListBox.listeners = new LinkedList<ChangeListener>() { {
        add( listener );
      } };

    customListBox.onChange( mock( Widget.class ) );

    verify( listener ).onChange( customListBox );
  }

  @Test
  public void testFindItems() {
    doCallRealMethod().when( customListBox ).findItems( anyString() );
    String searchText = "item1";

    ListItem item1 = mock( ListItem.class );
    when( item1.getText() ).thenReturn( searchText );
    ListItem item2 = mock( ListItem.class );
    when( item2.getText() ).thenReturn( "item2" );

    customListBox.items = new ArrayList<ListItem>() { {
      add( item1 );
      add( item2 );
    } };

    List<ListItem> actual = customListBox.findItems( searchText );
    assertEquals( 1, actual.size() );
  }

  @Test
  public void testOnPopupClosed() {
    doCallRealMethod().when( customListBox ).onPopupClosed( any(), anyBoolean() );
    customListBox.popupShowing = true;

    boolean booleanValue = true; // it does nothing inside "onPopupClosed" method
    customListBox.onPopupClosed( mock( PopupPanel.class ), booleanValue );

    verify( customListBox.getSearchTextBox(), times( 1 ) ).clearText();
    assertFalse( customListBox.popupShowing );
  }

  @Test
  public void testSetEnabled() throws Exception {
    doCallRealMethod().when( customListBox ).setEnabled( anyBoolean() );

    customListBox.editableTextBox = mock( TextBox.class );
    customListBox.arrow = mock( CustomListBox.DropDownArrow.class );
    customListBox.fPanel = mock( PentahoFocusPanel.class );

    verifySetEnabled( true, 1 );
    verifySetEnabled( false, 2 );
  }

  private void verifySetEnabled( boolean enabled, int times ) {
    customListBox.setEnabled( enabled );
    verify( customListBox.editableTextBox ).setEnabled( enabled );
    verify( customListBox.arrow ).setEnabled( enabled );
    verify( customListBox, times( times ) ).setStylePrimaryName( anyString() );
  }

  @Test
  public void testCreateProxy() throws Exception {
    doCallRealMethod().when( customListBox ).createProxy();

    final ListItem item = mock( ListItem.class );
    when( customListBox.getSelectedItem() ).thenReturn( item );

    final Widget proxy = customListBox.createProxy();
    assertEquals( DefaultListItem.class, proxy.getClass() );
    verify( customListBox.getSelectedItem() ).getText();
  }

  @Test
  public void testSetDragController() throws Exception {
    doCallRealMethod().when( customListBox ).setDragController( any( DragController.class ) );

    final DragController dragController = mock( DragController.class );
    final ListItem item = mock( ListItem.class );
    when( item.getWidget() ).thenReturn( mock( Widget.class ) );
    customListBox.items = new LinkedList<ListItem>() { {
      add( item );
      add( item );
    } };
    customListBox.setDragController( dragController );

    verify( dragController, times( 2 ) ).makeDraggable( item.getWidget() );
  }

  @Test
  public void testSetSelectedItemPlaceholderText() {
    doCallRealMethod().when( customListBox ).setSelectedItemPlaceholderText( anyString() );

    String value = "mock placeholder value";
    customListBox.setSelectedItemPlaceholderText( value );

    verify( customListBox.getSelectedItemPlaceholder(), times( 1 ) ).setText( value );
  }

  @Test
  public void testCreatePopupPanel_notSearchable() {
    doCallRealMethod().when( customListBox ).createPopupPanel();

    customListBox.createPopupPanel();

    // verify search was not queried and setup
    verify( customListBox, never() ).getSearchTextBox();
    verify( customListBox.getSearchTextBox(), never() ).addChangeListener( any() );
  }

  @Test
  public void testCreatePopupPanel_searchable() {
    doCallRealMethod().when( customListBox ).createPopupPanel();
    when( customListBox.isSearchable() ).thenReturn( true );

    customListBox.createPopupPanel();

    verify( customListBox, times( 1 ) ).getSearchTextBox();
    verify( customListBox.getSearchTextBox(), times( 1 ) ).addChangeListener( any( ChangeListener.class ) );
  }
}
