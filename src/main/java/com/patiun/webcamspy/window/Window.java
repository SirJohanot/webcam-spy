package com.patiun.webcamspy.window;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDevice;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.patiun.webcamspy.service.PhotoCaptor;
import com.patiun.webcamspy.service.VideoRecorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.patiun.webcamspy.factory.ComponentFactory.*;

public class Window {

    private static final int FPS = 15;

    public Window() {
        JFrame frame = buildFrame();

        Webcam webcam = getWebcam();

        JPanel webcamPanel = buildWebcamPanel(webcam);
        frame.add(webcamPanel, BorderLayout.PAGE_START);

        JTextArea webcamProperties = buildWebcamProperties(webcam);
        frame.add(webcamProperties);

        JPanel buttonsPanel = buildButtonsPanel(webcam, frame);
        frame.add(buttonsPanel, BorderLayout.PAGE_END);

        frame.pack();
        frame.setVisible(true);
    }

    private static JFrame buildFrame() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        setupFrame(frame, "Webcam Spy");
        frame.setResizable(false);
        return frame;
    }

    private static Webcam getWebcam() {
        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        return webcam;
    }

    private static WebcamPanel buildWebcamPanel(Webcam webcam) {
        WebcamPanel webcamPanel = new WebcamPanel(webcam);
        webcamPanel.setFPSDisplayed(true);
        webcamPanel.setImageSizeDisplayed(true);
        return webcamPanel;
    }

    private static JTextArea buildWebcamProperties(Webcam webcam) {
        JTextArea webcamProperties = buildTextArea(false);
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> updateWebcamProperties(webcamProperties, webcam), 0, 2, TimeUnit.SECONDS);
        return webcamProperties;
    }

    private static void updateWebcamProperties(JTextArea webcamProperties, Webcam webcam) {
        WebcamDevice webcamDevice = webcam.getDevice();
        webcamProperties.setText("");
        webcamProperties.append("Name: " + webcamDevice.getName() + "\n");
        Dimension resolution = webcamDevice.getResolution();
        String resolutionString = resolution.width + " x " + resolution.height;
        webcamProperties.append("Resolution: " + resolutionString + "\n");
        double fps = webcam.getFPS();
        String fpsString = String.format("%.2f", fps);
        webcamProperties.append("FPS: " + fpsString + "\n");
    }

    private static JPanel buildButtonsPanel(Webcam webcam, JFrame frame) {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());
        setupPanel(buttonsPanel);

        JButton photoButton = buildPhotoButton(webcam);
        buttonsPanel.add(photoButton);

        JButton stopVideoButton = buildStopVideoButton();

        Dimension webcamSize = webcam.getViewSize();
        int webcamWidth = webcamSize.width;
        int webcamHeight = webcamSize.height;

        JButton spyButton = buildSpyButton(frame, webcam, webcamWidth, webcamHeight);

        JButton videoButton = buildVideoButton(webcam, webcamWidth, webcamHeight, stopVideoButton, buttonsPanel, photoButton, spyButton);
        buttonsPanel.add(videoButton);

        buttonsPanel.add(spyButton);

        return buttonsPanel;
    }

    private static JButton buildPhotoButton(Webcam webcam) {
        JButton photoButton = buildButton("Photo");
        photoButton.addActionListener(e -> {
            PhotoCaptor.capturePhoto(webcam);
        });
        return photoButton;
    }

    private static JButton buildStopVideoButton() {
        return buildButton("Stop recording");
    }

    private static JButton buildSpyButton(JFrame frame, Webcam webcam, int webcamWidth, int webcamHeight) {
        JButton spyButton = buildButton("Spy");
        spyButton.addActionListener(a -> {
            frame.setVisible(false);
            while (true) {
                Thread recordingThread = VideoRecorder.recordVideo(webcam, webcamWidth, webcamHeight, FPS);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                recordingThread.interrupt();
            }
        });
        return spyButton;
    }

    private static JButton buildVideoButton(Webcam webcam, int webcamWidth, int webcamHeight, JButton stopVideoButton, JPanel buttonsPanel, JButton photoButton, JButton spyButton) {
        JButton videoButton = buildButton("Start recording");
        videoButton.addActionListener(e -> {
            Thread recordingThread = VideoRecorder.recordVideo(webcam, webcamWidth, webcamHeight, FPS);
            removeAllActionListeners(stopVideoButton);
            stopVideoButton.addActionListener(a -> {
                recordingThread.interrupt();
                replacePanelButtons(buttonsPanel, photoButton, videoButton, spyButton);
            });
            replacePanelButtons(buttonsPanel, photoButton, stopVideoButton);
        });
        return videoButton;
    }

    private static void replacePanelButtons(JPanel panel, JButton... buttons) {
        panel.removeAll();
        panel.revalidate();
        panel.repaint();
        for (JButton button : buttons) {
            panel.add(button);
        }
    }

    private static void removeAllActionListeners(JButton button) {
        for (ActionListener a : button.getActionListeners()) {
            button.removeActionListener(a);
        }
    }

}
