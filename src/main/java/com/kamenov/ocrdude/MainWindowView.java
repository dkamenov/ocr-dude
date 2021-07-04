package com.kamenov.ocrdude;

import java.awt.BorderLayout;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainWindowView extends JFrame implements MainView {
    private OcrController controller;

    public MainWindowView(OcrController controller) {
        this.controller = controller;
        setTitle("OCR Dude");

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem fileOpen = new JMenuItem(("Open..."));
        fileOpen.addActionListener(e -> controller.onFileOpenSelected());
        fileMenu.add(fileOpen);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        JToolBar toolBar = new JToolBar();
        JButton button = new JButton("Capture");
        button.addActionListener(e -> controller.onScreenshotSelected());
        toolBar.add(button);
        add(toolBar, BorderLayout.NORTH);

        try {
            setIconImage(ImageIO.read(getClass().getResource("/images/general-ocr.png")));
        } catch (Exception ignored) {
            log.warn("Could not load icon image");
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }

    @Override
    public void close() {
        dispose();
    }

    @Override
    public void display() {
        setVisible(true);
    }

    @Override
    public void hide() {
        setVisible(false);
    }
}
