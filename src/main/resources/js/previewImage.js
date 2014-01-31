function previewImage( contextPath, theId, theAttachmentId, theAttachmentVersion, theIndex){
  AJS.toInit(function() {
      init_viewer(contextPath, theId,theAttachmentId,theAttachmentVersion, theIndex);
      jQuery("#" + theId + "_diagram_image").colorbox({
          href:function(){
              return jQuery(this).attr("src");
            },
            title:function(){
              return jQuery("#" + theId + "_diagramName").text();
            },
            scalePhotos:false,
            scrolling:true
            });
  });
}
