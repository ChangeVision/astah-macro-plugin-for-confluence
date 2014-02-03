package com.change_vision.astah.file;

import java.io.File;
import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileIndexJson {

    private final File file;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(FileIndexJson.class);

    public FileIndexJson(ExportRootDirectory exportRoot) {
        this.file = new File(exportRoot.getDirectory(), "file.json");
    }
    
    public File getFile() {
        return file;
    }

    public String[] getFilePaths() {
        File indexFile = getFile();
        if (indexFile.exists() == false) {
            logger.info("not generated file.json : {}");
            return new String[0];
        }
        try {
            return mapper.readValue(indexFile, String[].class);
        } catch (IOException e) {
            logger.error("can't parse json",e);
            return new String[0];
        }
    }

}
