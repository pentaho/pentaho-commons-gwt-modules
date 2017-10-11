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

/**
 * Created by IntelliJ IDEA.
 * User: rfellows
 * Date: 7/12/11
 * Time: 3:55 PM
 * To change this template use File | Settings | File Templates.
 */

function setupJsButtonHover() {
    setupJsHover('.IE button.pentaho-button', 'pentaho-button-hover')
}

function setupJsHover(cssSelector, cssHoverClass) {

    var element=$(cssSelector);

    //Explicitly remove hover effect if on button at load of new dialog for IE9.
    element.removeClass(cssHoverClass);

    element.hover(function() {
        $(this).addClass(cssHoverClass);
    }, function() {
        $(this).removeClass(cssHoverClass);
    });

}

// dcleao: Placing this polyfill because this file is loaded pretty early,
// but it would be better to have a centralized polyfill file.
if (!Function.prototype.bind) {
    (function() {
        var arraySlice = Array.prototype.slice;
        
        Function.prototype.bind = function (ctx) {
            var staticArgs = arraySlice.call(arguments, 1);   
            var fToBind = this;
            
            return function (){
                return fToBind.apply(ctx, staticArgs.concat(arraySlice.call(arguments)));
            };
        };
    }());
}
