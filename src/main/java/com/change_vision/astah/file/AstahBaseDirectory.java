package com.change_vision.astah.file;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.confluence.setup.BootstrapManager;

public class AstahBaseDirectory {
    
    private final Logger logger = LoggerFactory.getLogger(AstahBaseDirectory.class);

    private final File astahBase;

    public AstahBaseDirectory(BootstrapManager bootstrapManager){
        astahBase = new File(bootstrapManager.getConfluenceHome(), "astah");
    }

    public File getDirectory(){
        return astahBase;
    }
    
    public File getExportIniFile(){
        return new File(astahBase,"export.ini");
    }
    
    public File getCommunityJar() {
        return new File(astahBase,"astah-community.jar");
    }

    public boolean isValid() {
        boolean astahExists = getDirectory() != null && getDirectory().exists();
        boolean communityJarExists = getCommunityJar() != null && getCommunityJar().exists();
        logger.trace("astah:{} community-jar:{}",astahExists,communityJarExists);
        return astahExists && communityJarExists;
    }

}
