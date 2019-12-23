package com.change_vision.astah.file;

import java.io.File;

import com.atlassian.confluence.setup.BootstrapManager;

public class ExportBaseDirectory {
    
    private final File exportBase;
    
    public ExportBaseDirectory(BootstrapManager bootstrapManager){
        exportBase = new File(bootstrapManager.getLocalHome().getAbsolutePath(), "astah-exported");
    }
    
    public File getDirectory(){
        return exportBase;
    }

}
