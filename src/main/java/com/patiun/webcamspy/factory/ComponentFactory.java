package com.patiun.webcamspy.factory;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;

public class ComponentFactory {

    private static final Color MAIN_COLOR = Color.GREEN;
    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final Color CARET_COLOR = Color.CYAN;
    private static final Font FONT = new Font("Consolas", Font.BOLD, 18);
    private static final int TEXT_AREA_ROWS = 10;
    private static final int TEXT_AREA_COLUMNS = 36;

    private ComponentFactory() {
    }

    private static void styleComponent(JComponent component) {
        component.setBackground(BACKGROUND_COLOR);
        component.setFont(FONT);
        component.setForeground(MAIN_COLOR);
        component.setOpaque(true);
    }

    public static JButton buildButton(String name) {
        JButton button = new JButton(name);
        styleComponent(button);
        return button;
    }

    public static JTextArea buildTextArea(boolean editable) {
        JTextArea textArea = new JTextArea(TEXT_AREA_ROWS, TEXT_AREA_COLUMNS);
        textArea.setEditable(editable);
        styleComponent(textArea);
        textArea.setCaretColor(CARET_COLOR);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        return textArea;
    }

    public static void setupFrame(JFrame frame, String title) {
        frame.setTitle(title);
        frame.getContentPane().setBackground(BACKGROUND_COLOR);
        frame.setUndecorated(false);
    }

    public static void setupPanel(JPanel panel) {
        styleComponent(panel);
    }

    public static JSpinner buildSpinner(SpinnerModel model) {
        JSpinner spinner = new JSpinner(model);
        styleComponent(spinner);
        return spinner;
    }

}
