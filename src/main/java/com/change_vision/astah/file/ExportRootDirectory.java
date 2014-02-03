package com.change_vision.astah.file;

import java.io.File;

import com.atlassian.confluence.pages.Attachment;

public class ExportRootDirectory {

    private ExportBaseDirectory exportBase;
    private File exportRoot;
    private long attachmentId;
    private int attachmentVersion;

    public ExportRootDirectory(ExportBaseDirectory exportBase, Attachment attachment){
        long attachmentId = attachment.getId();
        int attachmentVersion = attachment.getVersion();
        initializeExportRoot(exportBase,attachmentId,attachmentVersion);
    }

    public ExportRootDirectory(ExportBaseDirectory exportBase, long attachmentId,
            int attachmentVersion) {
        initializeExportRoot(exportBase, attachmentId, attachmentVersion);
    }

    public ExportRootDirectory(ExportBaseDirectory exportBase, String attachmentId,
            String attachmentVersion) {
        initializeExportRoot(exportBase, Long.valueOf(attachmentId), Integer.valueOf(attachmentVersion));
    }

    private void initializeExportRoot(ExportBaseDirectory exportBase, long attachmentId,
            int attachmentVersion) {
        this.exportBase = exportBase;
        this.attachmentId = attachmentId;
        this.attachmentVersion = attachmentVersion;
        File attachmentIdBase = new File(exportBase.getDirectory(), String.valueOf(attachmentId));
        this.exportRoot =  new File(attachmentIdBase, String.valueOf(attachmentVersion));
    }

    public ExportBaseDirectory getExportBase() {
        return exportBase;
    }

    public File getDirectory(){
        return exportRoot;
    }
    
    public long getAttachmentId() {
        return attachmentId;
    }
    
    public int getAttachmentVersion() {
        return attachmentVersion;
    }
}
