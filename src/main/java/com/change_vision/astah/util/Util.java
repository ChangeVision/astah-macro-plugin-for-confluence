package com.change_vision.astah.util;

import java.io.File;

import org.apache.commons.lang.SystemUtils;

public class Util {

    private static final String SPRIT_CHAR;
    private static final String JAVA_COMMAND_NAME;
    static {
        if (SystemUtils.IS_OS_WINDOWS) {
            SPRIT_CHAR = "\\\\";
            JAVA_COMMAND_NAME = "javaw.exe";
        } else {
            SPRIT_CHAR = File.separator;
            JAVA_COMMAND_NAME = "java";
        }
    }

    public String getJavaCommandName() {
        return JAVA_COMMAND_NAME;
    }

    public String getFileExtension(File file) {
        if (file == null)
            throw new IllegalArgumentException("file is null.");
        String fileName = file.getName();
        int point = fileName.lastIndexOf(".");
        if (point != -1) {
            return fileName.substring(point + 1);
        }
        return fileName;
    }

    public String getRelativePath(String base, File file) {
        if (base == null)
            throw new IllegalArgumentException("base is null.");
        if (file == null)
            throw new IllegalArgumentException("file is null.");

        String absolutePath = file.getAbsolutePath();
        String[] basePathElement = base.split(SPRIT_CHAR);
        String[] absolutePathElement = absolutePath.split(SPRIT_CHAR);
        for (int i = 0; i < basePathElement.length; i++) {
            String baseElement = basePathElement[i];
            String absoluteElement = absolutePathElement[i];
            if (isNotEquals(baseElement, absoluteElement)) {
                return absolutePathElement[i + 1];
            }
        }
        if (basePathElement.length < absolutePathElement.length) {
            StringBuilder result = new StringBuilder();
            for (int i = basePathElement.length; i < absolutePathElement.length; i++) {
                result.append(absolutePathElement[i]);
                result.append(File.separator);
            }
            result.deleteCharAt(result.length() - 1);
            return result.toString();
        }
        return "";
    }

    private boolean isNotEquals(String baseElement, String absoluteElement) {
        return baseElement.equals(absoluteElement) == false;
    }

    public String getFilename(File file) {
        if (file == null)
            throw new IllegalArgumentException("file is null.");
        String fileName = file.getName();
        int point = fileName.lastIndexOf(".");
        if (point != -1) {
            return fileName.substring(0, point);
        }
        return fileName;
    }

}
