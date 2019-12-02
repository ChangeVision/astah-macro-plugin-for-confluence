package com.change_vision.astah.file;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.confluence.setup.BootstrapManager;
import com.change_vision.astah.util.Util;

public class AstahBaseDirectory {
    
    private final Logger logger = LoggerFactory.getLogger(AstahBaseDirectory.class);

    private final File astahBase;
    private final Util util = new Util();

    public AstahBaseDirectory(BootstrapManager bootstrapManager){
        astahBase = new File(bootstrapManager.getLocalHome().getAbsolutePath(), "astah");
    }

    public File getDirectory(){
        return astahBase;
    }
    
    public File getExportIniFile(){
        return new File(astahBase,"export.ini");
    }
    
    private File getCommunityJar() {
        return new File(astahBase,"astah-community.jar");
    }

    private File getGsnJar() {
    	return new File(astahBase,"astah-gsn.jar");
    }

    public File getAstahJar(File file) {
        String extension = util.getFileExtension(file);
        if (util.isGsn(extension)) {
            return getGsnJar();
        }
        return getCommunityJar();
    }

    public boolean isValid() {
        boolean astahExists = getDirectory() != null && getDirectory().exists();
        boolean communityJarExists = getCommunityJar() != null && getCommunityJar().exists();
        boolean gsnJarExists = getGsnJar() != null && getGsnJar().exists();
        logger.trace("astah:{} community-jar:{} gsn-jar:{}",new Object[] {astahExists,communityJarExists, gsnJarExists});
        return astahExists && communityJarExists && gsnJarExists;
    }

}
