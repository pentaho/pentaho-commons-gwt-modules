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

package org.pentaho.mantle.client.dialogs.filterdialog.client;

import org.junit.Assert;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;

import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.impl.XulWindowContainer;
import org.pentaho.ui.xul.dom.dom4j.DocumentDom4J;
import org.pentaho.ui.xul.dom.dom4j.ElementDom4J;
import org.pentaho.ui.xul.binding.Binding;
import org.pentaho.ui.xul.binding.BindingConvertor;
import org.pentaho.ui.xul.binding.BindingFactory;
import org.pentaho.ui.xul.binding.DefaultBindingFactory;
import org.pentaho.ui.xul.gwt.tags.GwtButton;
import org.pentaho.ui.xul.gwt.tags.GwtTextbox;
import org.pentaho.ui.xul.gwt.tags.GwtListbox;
import org.pentaho.ui.xul.gwt.tags.GwtTree;
import org.pentaho.ui.xul.gwt.tags.GwtTreeChildren;
import org.pentaho.ui.xul.gwt.tags.GwtDeck;
import org.pentaho.ui.xul.gwt.tags.GwtRadio;
import org.pentaho.ui.xul.gwt.tags.GwtMenuList;
import org.pentaho.gwt.widgets.client.listbox.CustomListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.CheckBox;

@RunWith( GwtMockitoTestRunner.class )
@WithClassesToStub( value = { CustomListBox.class, RadioButton.class, CheckBox.class } )
public class EditFilterControllerTest {

  EditFilterController editFilterController;

  @Before
  public void setUp() throws Exception {
    editFilterController = new EditFilterController();
  }

  @Test
  public void testCleanseDisplayName() {
    Assert.assertEquals( " -a", editFilterController.cleanseDisplayName( ",;a" ) );
    Assert.assertEquals( "", editFilterController.cleanseDisplayName( "<>" ) );
    Assert.assertEquals( "- -", editFilterController.cleanseDisplayName( "\"/;><,|" ) );
    Assert.assertEquals( "ab", editFilterController.cleanseDisplayName( "\"a'&$b" ) );
  }

  @Test
  public void testUnescapeHtmlEntities() {
    Assert.assertEquals( "&<>/*-", EditFilterController.unescapeHtmlEntities( "&amp;&lt;&gt;/*-" ) );
  }

  @Test
  public void testEscapeHtmlEntities() {
    Assert.assertEquals( "&amp;&lt;&gt;/*-", EditFilterController.escapeHtmlEntities( "&<>/*-" ) );
  }

  @Test
  public void testInit() {
    editFilterController.setSQLSupport( new DisabledSQLSupport() );
    try {
      XulWindowContainer xwc = new XulWindowContainer();
      ElementDom4J el = new ElementDom4J( "element" );
      DocumentDom4J doc = new DocumentDom4J() {
        public XulComponent getElementById( String id ) {
          if ( "editFilterDialog_accept".equals( id ) ) {
            return new GwtButton();
          } else if ( "displayNameText".equals( id ) ) {
            return new GwtTextbox();
          } else if ( "staticSelectionTable".equals( id ) ) {
            GwtTree tree = new GwtTree();
            tree.addChild( new GwtTreeChildren() );
            return tree;
          } else if ( "sqlQueryText".equals( id ) ) {
            return new GwtTextbox();
          } else if ( "mqlFilterSelectedColumns".equals( id ) ) {
            return new GwtListbox();
          } else if ( "filterDataDeck".equals( id ) ) {
            return new GwtDeck();
          } else if ( "useFirstRadio".equals( id ) ) {
            return new GwtRadio();
          } else if ( "paramsSqlQueryText".equals( id ) ) {
            return new GwtTextbox();
          } else if ( "filterTypeList".equals( id ) ) {
            GwtMenuList menu = new GwtMenuList() {
              public void layout() {
              }
            };
            return menu;
          }
          return super.getElementById( id );
        }
      };
      doc.addChild( el );
      xwc.addDocument( doc );
      BindingFactory bf = new DefaultBindingFactory() {
        public Binding createBinding( String sourceId, String sourceAttr, String targetId, String targetAttr,
          BindingConvertor... converters ) {
          return null;
        }
        public Binding createBinding( Object source, String sourceAttr, String targetId, String targetAttr,
          BindingConvertor... converters ) {
          return null;
        }
        public Binding createBinding( Object source, String sourceAttr, Object target, String targetAttr,
          BindingConvertor... converters ) {
          return null;
        }
        public Binding createBinding( String sourceId, String sourceAttr, Object target, String targetAttr,
          BindingConvertor... converters ) {
          return null;
        }
      };
      bf.setDocument( doc );
      editFilterController.setXulDomContainer( xwc );
      editFilterController.setBindingFactory( bf );
      editFilterController.init();
      Assert.assertTrue( editFilterController.isInitialized() );
    } catch ( Exception ex ) {
      ex.printStackTrace();
      fail();
    }
  }
}
