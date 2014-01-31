var viewers = [];

function changeDiagram(contextPath, id, attachmentId, attachmentVersion, index) {
  var diagramIndex = index;
  if (index > viewers[id].diagramLength - 1) {
    diagramIndex = 0;
  } else if (index < 0) {
    diagramIndex = viewers[id].diagramLength - 1;
  } else {
    diagramIndex = index;
  }
  viewers[id].diagramIndex = diagramIndex;
  AJS.$("#" + id + "_diagram_image"  ).hide();

  var diagramName = viewers[id].diagrams[diagramIndex]
  var diagramUrl = contextPath + "/rest/astah/1.0/attachment/image/" + attachmentId + "/" + attachmentVersion + "/" + diagramIndex + ".png";

  AJS.$("#" + id + "_page").text("[" + (viewers[id].diagramIndex + 1) + "/" + viewers[id].diagramLength + "]")
  AJS.$("#" + id + "_diagramName").text(diagramName);
  AJS.$("#" + id + "_diagram_image"  ).attr("src",diagramUrl);
  AJS.$("#" + id + "_diagram_image"  ).fadeIn();
}

function init_viewer(contextPath, id, attachmentId, attachmentVersion, index){
  AJS.log("init_viewer - contextPath: '" + contextPath + "' id: '" + id + "' attachmentId: '" + attachmentId + "' attachmentVersion: '" + attachmentVersion + "' index: '" + index + "'");
  AJS.$("#" + id + "_prev").click(function() {
    changeDiagram(contextPath, id, attachmentId, attachmentVersion, viewers[id].diagramIndex - 1);
  });
  AJS.$("#" + id + "_next").click(function() {
    changeDiagram(contextPath, id, attachmentId, attachmentVersion, viewers[id].diagramIndex + 1);
  });
  getDiagrams(contextPath, id, attachmentId, attachmentVersion, index);
}

function getDiagrams(contextPath, id, attachmentId, attachmentVersion, index){
  AJS.$.getJSON( contextPath + "/rest/astah/1.0/attachment/diagrams/" + attachmentId + "/" + attachmentVersion,
    function(data){
      viewers[id] = { diagrams : data, diagramIndex : 0 ,diagramLength : data.length}
      changeDiagram(contextPath, id, attachmentId, attachmentVersion, index);
    }
  ).error(function(){
      setTimeout("getDiagrams('" + contextPath + "','" + id + "'," + attachmentId + "," + attachmentVersion + ")",5000);
  });
}
