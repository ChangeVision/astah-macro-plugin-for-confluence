package com.change_vision.astah.exporter;

import java.io.File;

public class DiagramFile {
    
    private ExportRootDirectory exportRoot;

    public DiagramFile(ExportRootDirectory exportRoot){
        this.exportRoot = exportRoot;
    }
    
    public File getFile(int index){
        FileIndexJson fileIndexJson = new FileIndexJson(exportRoot);
        String[] filePaths = fileIndexJson.getFilePaths();
        String filePath = filePaths[index];
        ExportBaseDirectory exportBase = exportRoot.getExportBase();
        return new File(exportBase.getDirectory(),filePath);
    }

}
