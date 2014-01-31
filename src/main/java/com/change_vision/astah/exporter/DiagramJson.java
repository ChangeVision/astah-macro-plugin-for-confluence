package com.change_vision.astah.exporter;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiagramJson {
    
    private static final ObjectMapper mapper = new ObjectMapper();
    
    private static final Logger logger = LoggerFactory.getLogger(DiagramJson.class);

    private final File file;

    public DiagramJson(ExportRootDirectory exportRoot){
        this.file = new File(exportRoot.getDirectory(),"diagram.json");
    }
    
    public File getFile() {
        return file;
    }

    public boolean exists() {
        return getFile() != null && getFile().exists();
    }

    public String readFile() throws IOException {
        return FileUtils.readFileToString(getFile(), "utf-8");
    }

    public String getName(int number) {
        try {
            String[] diagramNames = mapper.readValue(getFile(), String[].class);
            return diagramNames[number];
        } catch (IOException e) {
            logger.error("can't parse json",e);
            return "ERROR!!";
        }
    }

}
