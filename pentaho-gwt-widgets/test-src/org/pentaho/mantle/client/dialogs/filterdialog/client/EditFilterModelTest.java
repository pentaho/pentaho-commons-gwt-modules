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

import com.google.gwt.core.client.JsArrayString;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.mantle.client.dialogs.filterdialog.client.jsni.IPentahoFilter;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith( GwtMockitoTestRunner.class )
public class EditFilterModelTest {

  @GwtMock
  JsArrayString string;

  @Test
  public void testToWidgetAllowsSpaces() {
    EditFilterModel.StaticFilterDataModel filterDataModel = new EditFilterModel.StaticFilterDataModel();
    IPentahoFilter filter = mock( IPentahoFilter.class );
    filterDataModel.selections.add( new EditFilterModel.NameValuePair( " ", " " ) );
    filterDataModel.toWidget( filter );
    verify( filter ).addToValuesArray( " ", " " );
  }

  @Test
  public void testApplyTo() {
    IPentahoFilter filter = mock( IPentahoFilter.class );
    IPentahoFilterFactory factory = mock( IPentahoFilterFactory.class );
    when( factory.create( any( EditFilterModel.PovComponentType.class ) ) ).thenReturn( filter );

    EditFilterModel filterDataModel = spy( new EditFilterModel( factory ) );
    doReturn( string ).when( filterDataModel ).castFromJSObjectToJSArrayString( any( JsArrayString.class ) );
    IPentahoFilter newFilter = filterDataModel.applyTo();

    verify( filter ).setParameterAndDefaultValue( anyString(), eq( string ) );
  }

  @Test
  public void testApplyToWidgetParameter() {
    IPentahoFilter filter = mock( IPentahoFilter.class );
    IPentahoFilterFactory factory = mock( IPentahoFilterFactory.class );
    when( factory.create( any( EditFilterModel.PovComponentType.class ) ) ).thenReturn( filter );

    String parameter = "param";
    when( filter.getParameter() ).thenReturn( parameter );

    EditFilterModel filterDataModel = spy( new EditFilterModel( factory ) );
    filterDataModel.optionalWidget = filter;

    doReturn( string ).when( filterDataModel ).castFromJSObjectToJSArrayString( any( JsArrayString.class ) );
    IPentahoFilter newFilter = filterDataModel.applyTo();

    verify( filter ).setParameter( eq( parameter ) );
  }
}
