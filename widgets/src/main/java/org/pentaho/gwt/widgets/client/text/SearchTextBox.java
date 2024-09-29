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


package org.pentaho.gwt.widgets.client.text;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.pentaho.gwt.widgets.client.panel.HorizontalFlexPanel;
import org.pentaho.gwt.widgets.client.utils.ImageUtil;
import org.pentaho.gwt.widgets.client.messages.Messages;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings( "deprecation" )
public class SearchTextBox extends HorizontalFlexPanel implements ChangeListener {

  public static final String CLASSNAME = "gwt-search-text-box";
  static final String SEARCH_ICON_CLASSNAME = "pentaho-search-button";
  static final String CLEAR_ICON_CLASSNAME = "pentaho-clear-button";

  private final List<ChangeListener> changeListeners = new ArrayList<>();
  private final TextBox input = new TextBox();
  private final Image icon = ImageUtil.getThemeableImage( "icon-zoomable" );

  public SearchTextBox() {
    super();

    createUI();
  }

  /* Visible for testing */
  TextBox getInput() {
    return this.input;
  }

  /* Visible for testing */
  Image getIcon() {
    return this.icon;
  }

  public String getValue() {
    return getInput().getText();
  }

  public void setValue( String value ) {
    getInput().setText( value );
  }

  List<ChangeListener> getChangeListeners() {
    return this.changeListeners;
  }

  private void createUI() {
    add( createInputUI() );
    add( createIconUI() );

    addStyleName( CLASSNAME );
  }

  /* Visible for testing */
  TextBox createInputUI() {
    TextBox searchInput = getInput();

    Element element = searchInput.getElement();
    element.setAttribute( "type", "search" );
    element.setAttribute( "placeholder", Messages.getString( "SearchTextBox.placeholder" ) );

    searchInput.addKeyUpHandler( event -> {
      Image searchIcon = getIcon();

      if ( "".equals( searchInput.getText() ) ) {
        searchIcon.addStyleName( SEARCH_ICON_CLASSNAME );
        searchIcon.removeStyleName( CLEAR_ICON_CLASSNAME );
      } else {
        searchIcon.addStyleName( CLEAR_ICON_CLASSNAME );
        searchIcon.removeStyleName( SEARCH_ICON_CLASSNAME );
      }

      onChange( this );
    } );

    return searchInput;
  }

  /* Visible for testing */
  Image createIconUI() {
    Image searchIcon = getIcon();
    searchIcon.addStyleName( SEARCH_ICON_CLASSNAME );
    searchIcon.addClickHandler( event -> onIconClick() );

    return searchIcon;
  }

  /* Visible for testing */
  void onIconClick() {
    if ( getIcon().getStyleName().contains( CLEAR_ICON_CLASSNAME ) ) {
      clearText();
    }
  }

  public void clearText() {
    setValue( "" );
    getInput().setFocus( true );

    Image searchIcon = getIcon();
    searchIcon.addStyleName( SEARCH_ICON_CLASSNAME );
    searchIcon.removeStyleName( CLEAR_ICON_CLASSNAME );

    onChange( this );
  }

  @Override
  public void onChange( Widget widget ) {
    getChangeListeners().forEach( listener -> listener.onChange( widget ));
  }

  public void addChangeListener( ChangeListener listener ) {
    getChangeListeners().add( listener );
  }
}
