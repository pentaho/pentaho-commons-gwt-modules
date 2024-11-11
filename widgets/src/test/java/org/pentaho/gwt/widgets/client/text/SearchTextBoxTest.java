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


package org.pentaho.gwt.widgets.client.text;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.pentaho.gwt.widgets.client.text.SearchTextBox.CLEAR_ICON_CLASSNAME;
import static org.pentaho.gwt.widgets.client.text.SearchTextBox.SEARCH_ICON_CLASSNAME;

@SuppressWarnings( "deprecation" )
@RunWith( GwtMockitoTestRunner.class )
public class SearchTextBoxTest {

  private SearchTextBox searchTextBox;

  @Mock
  private List<ChangeListener> changeListeners;
  @Mock
  private TextBox input;
  @Mock
  private Image icon;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks( this );
    searchTextBox = spy( new SearchTextBox() );

    when( searchTextBox.getInput() ).thenReturn( input );
    when( searchTextBox.getIcon() ).thenReturn( icon );
    when( searchTextBox.getChangeListeners() ).thenReturn( changeListeners );
  }

  @Test
  public void testCreateInputUI() {
    when( input.getElement() ).thenReturn( mock( Element.class ) );

    searchTextBox.createInputUI();

    verify( input.getElement() ).setAttribute( "type", "search" );
    verify( input.getElement() ).setAttribute( "placeholder", "SearchTextBox.placeholder" );

    verify( input ).addKeyUpHandler( any( KeyUpHandler.class ) );
  }

  @Test
  public void testClearText() {
    searchTextBox.clearText();

    verify( input ).setText( "" );
    verify( input ).setFocus( true );

    verify( icon ).removeStyleName( CLEAR_ICON_CLASSNAME );
    verify( icon ).addStyleName( SEARCH_ICON_CLASSNAME );

    verify( searchTextBox ).onChange( searchTextBox );
  }

  @Test
  public void testCreateIconUI() {
    searchTextBox.createIconUI();

    verify( icon ).addStyleName( SEARCH_ICON_CLASSNAME );
    verify( icon ).addClickHandler( any( ClickHandler.class ) );
  }

  @Test
  public void testOnIconClick_searchIconActive() {
    when( icon.getStyleName() ).thenReturn( SEARCH_ICON_CLASSNAME );
    searchTextBox.onIconClick();

    verify( searchTextBox, never() ).clearText();
  }

  @Test
  public void testOnIconClick_clearIconActive() {
    when( icon.getStyleName() ).thenReturn( CLEAR_ICON_CLASSNAME );
    searchTextBox.onIconClick();

    verify( searchTextBox ).clearText();
  }

  @Test
  public void testOnChange() {
    ChangeListener listener = mock( ChangeListener.class );
    when( searchTextBox.getChangeListeners() ).thenReturn( Collections.singletonList( listener ) );

    searchTextBox.onChange( searchTextBox );

    verify( listener ).onChange( searchTextBox );
  }

  @Test
  public void testAddChangeListener() {
    ChangeListener listener = mock( ChangeListener.class );
    searchTextBox.addChangeListener( listener );

    verify( changeListeners ).add( listener );
  }
}
