var viewers = [];
var contextPath = AJS.contextPath();
if( typeof contextPath == 'undefined'){
	contextPath = "";
}

function changeDiagram(id, attachmentId,attachmentVersion,index) {
	var diagramIndex = index;
	if (index > viewers[id].diagramLength - 1) {
		diagramIndex = 0;
	} else if (index < 0) {
		diagramIndex = viewers[id].diagramLength - 1;
	} else {
		diagramIndex = index;
	}
	viewers[id].diagramIndex = diagramIndex;
	AJS.$("#" + id + "_diagram_image"	).hide();
	
	var diagramName = viewers[id].diagrams[diagramIndex]
	var diagramUrl = contextPath + "/rest/astah/1.0/attachment/image/" + attachmentId + "/" + attachmentVersion + "/" + diagramIndex + ".png";

	AJS.$("#" + id + "_page").text("[" + (viewers[id].diagramIndex + 1) + "/" + viewers[id].diagramLength + "]")
	AJS.$("#" + id + "_diagramName").text(diagramName);
	AJS.$("#" + id + "_diagram_image"	).attr("src",diagramUrl);
	AJS.$("#" + id + "_diagram_image"	).fadeIn();
}

function init_viewer(id,attachmentId,attachmentVersion){
	AJS.$("#" + id + "_prev").click(function() {
		changeDiagram(id, attachmentId, attachmentVersion, viewers[id].diagramIndex - 1);
	});
	AJS.$("#" + id + "_next").click(function() {
		changeDiagram(id, attachmentId, attachmentVersion, viewers[id].diagramIndex + 1);
	});
	getDiagrams(id,attachmentId,attachmentVersion);
}

function getDiagrams(id,attachmentId,attachmentVersion){
	AJS.$.getJSON( contextPath + "/rest/astah/1.0/attachment/diagrams/" + attachmentId + "/" + attachmentVersion,
		function(data){
			viewers[id] = { diagrams : data, diagramIndex : 0 ,diagramLength : data.length}
			changeDiagram(id, attachmentId, attachmentVersion, viewers[id].diagramIndex);
		}
	).error(function(){
	    setTimeout("getDiagrams('"+id+"',"+attachmentId+","+attachmentVersion+")",5000);
	});	
}