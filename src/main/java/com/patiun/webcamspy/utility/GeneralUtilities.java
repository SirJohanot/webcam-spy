package com.patiun.webcamspy.utility;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GeneralUtilities {

    public static File createFile(String fileName, String extension) {
        File file = new File(fileName + "." + extension);
        try {
            file.createNewFile();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return file;
    }

    public static String getCurrentTimeString() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yy-h-mm-ss"));
    }

}
