package com.change_vision.astah.exporter;

import java.io.File;

import com.atlassian.confluence.pages.Attachment;

public class ExportRootDirectory {
    
    private File exportRoot;

    public ExportRootDirectory(ExportBaseDirectory exportBase, Attachment attachment){
        File attachmentIdBase = new File(exportBase.getDirectory(), String.valueOf(attachment.getId()));
        this.exportRoot =  new File(attachmentIdBase, String.valueOf(attachment.getVersion()));
    }

    public File getDirectory(){
        return exportRoot;
    }
}
