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
  $(cssSelector).hover(function() {
    $(this).addClass(cssHoverClass);
  }, function() {
    $(this).removeClass(cssHoverClass);
  });
}