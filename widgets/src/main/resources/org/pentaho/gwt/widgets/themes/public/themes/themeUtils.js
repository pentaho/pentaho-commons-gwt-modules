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
