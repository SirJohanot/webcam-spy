package com.patiun.webcamspy.service;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamUtils;

import java.io.File;

import static com.patiun.webcamspy.utility.GeneralUtilities.createFile;
import static com.patiun.webcamspy.utility.GeneralUtilities.getCurrentTimeString;

public class PhotoCaptor {

    public static void capturePhoto(Webcam webcam) {
        File imageFile = createFile(getCurrentTimeString(), "jpg");
        WebcamUtils.capture(webcam, imageFile);
    }
}
