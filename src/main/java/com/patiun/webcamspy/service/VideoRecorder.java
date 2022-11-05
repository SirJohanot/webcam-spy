package com.patiun.webcamspy.service;

import com.github.sarxos.webcam.Webcam;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameRecorder;
import org.opencv.core.Core;
import org.opencv.videoio.VideoWriter;

import java.awt.image.BufferedImage;
import java.io.File;

import static com.patiun.webcamspy.utility.GeneralUtilities.createFile;
import static com.patiun.webcamspy.utility.GeneralUtilities.getCurrentTimeString;

public class VideoRecorder {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static Thread recordVideo(Webcam webcam, int width, int height, int fps) {
        Thread recordingThread = new Thread(() -> startRecordingVideo(webcam, width, height, fps));
        recordingThread.start();
        return recordingThread;
    }

    private static void startRecordingVideo(Webcam webcam, int width, int height, int fps) {
        File output = createFile(getCurrentTimeString(), "avi");
        try (OpenCVFrameRecorder recorder = new OpenCVFrameRecorder(output, width, height);
             Java2DFrameConverter javaConverter = new Java2DFrameConverter()) {
            recorder.setFrameRate(fps);
            recorder.setVideoCodec(VideoWriter.fourcc('M', 'J', 'P', 'G'));
            recorder.start();
            boolean webcamWasAlreadyOpen = webcam.isOpen();
            if (!webcamWasAlreadyOpen) {
                webcam.open();
            }
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                BufferedImage image = webcam.getImage();
                recorder.record(javaConverter.getFrame(image));
                try {
                    Thread.sleep(1000 / (long) fps);
                } catch (InterruptedException ie) {
                    break;
                }
            }
            if (!webcamWasAlreadyOpen) {
                webcam.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
