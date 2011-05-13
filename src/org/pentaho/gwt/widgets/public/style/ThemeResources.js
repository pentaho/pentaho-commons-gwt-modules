
var docHead = document.getElementsByTagName("head")[0];

if(window.location.href.indexOf("theme=") > -1){
  var startIdx = window.location.href.indexOf("theme=")+("theme=".length);
  var endIdx = window.location.href.indexOf("&", startIdx) > -1 ? window.location.href.indexOf("&", startIdx) : window.location.href.length;
  active_theme = window.location.href.substring(startIdx, endIdx);
}

includeResources(phoConfig.theme_resources.core, core_theme_tree, PENTAHO_STYLE_ROOT);
includeResources(phoConfig.theme_resources.local, module_theme_tree, MODULE_STYLE_ROOT);

function includeResources(requestedResourceCollection, actualResourceTree, root){
  if(!requestedResourceCollection){
    return;
  }
  var cssPat = /\.css$/;
  for(var i=0; i<requestedResourceCollection.length; i++){
    var baseName = requestedResourceCollection[i];
    var selectedTheme = active_theme;
    
    if(! actualResourceTree[selectedTheme] || actualResourceTree[selectedTheme].indexOf(baseName) < 0){
      selectedTheme = "core";
    }
  
    if(cssPat.test(baseName)){
      var scriptTag = document.createElement("link");
      scriptTag.setAttribute("rel", "stylesheet");
      scriptTag.setAttribute("type", "text/css");
      scriptTag.setAttribute("href", root + "/"+selectedTheme+"/"+baseName);
  
      docHead.appendChild(scriptTag);
    } else {
      var styleTag = document.createElement("script");
      styleTag.setAttribute("type", "text/javascript");
      styleTag.setAttribute("src", root + "/"+selectedTheme+"/"+baseName);

      docHead.appendChild(styleTag);
    }
  }
}
