package com.change_vision.astah.exporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportSetting {
    
    private static final Logger logger = LoggerFactory.getLogger(ExportSetting.class);

    String[] parse(String setting) {
        logger.debug("setting:{}",setting);
        if (setting == null) {
            throw new IllegalArgumentException("setting is null");
        }
        if (isEmptyLine(setting)) {
            return new String[0];
        }
        String[] splited = setting.split("\\n");
        List<String> parsed = new ArrayList<String>();
        for (String arg : splited) {
            if (isEmptyLine(arg) || isComment(arg)) {
                continue;
            }
            parsed.add(arg);
        }
        return parsed.toArray(new String[]{});
    }

    private boolean isComment(String arg) {
        return arg.startsWith("#");
    }

    private boolean isEmptyLine(String arg) {
        return arg.isEmpty() || arg.matches("\\s+");
    }

    public String[] load(File file) {
        if (file == null) {
            throw new IllegalArgumentException("file is null");
        }
        logger.debug("file:{}",file.getAbsolutePath());
        String settings = readFile(file);
        return parse(settings);
    }

    private String readFile(File file) {
        Reader reader = createFileReader(file);
        if (reader == null) return "";
        return readContents(reader);
    }

    private String readContents(Reader reader) {
        BufferedReader buffered = new BufferedReader(reader);
        String settings = doReadContents(buffered);
        closeReader(buffered);
        return settings;
    }

    private String doReadContents(BufferedReader buffered) {
        StringBuilder readStringBuilder = new StringBuilder();
        String line = null;
        while((line = readNext(buffered)) != null){
            readStringBuilder.append(line);
            readStringBuilder.append('\n');
        }
        return readStringBuilder.toString();
    }

    private void closeReader(Reader reader) {
        try {
            reader.close();
        } catch (IOException e) {
            logger.error("IO Exception",e);
        }
    }

    private String readNext(BufferedReader buffered) {
        String line = null;
        try {
            line = buffered.readLine();
        } catch (IOException e) {
            logger.error("IO Exception",e);
        }
        return line;
    }

    private Reader createFileReader(File file) {
        Reader reader = null;
        try {
            reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            logger.error("file not found",e);
        }
        return reader;
    }

}
