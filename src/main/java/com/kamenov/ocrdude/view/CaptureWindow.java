package com.kamenov.ocrdude.view;

import com.kamenov.ocrdude.OcrController;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

public class CaptureWindow extends JFrame implements KeyListener {
    private OcrController controller;

    public CaptureWindow(OcrController controller, BufferedImage image) {
        this.controller = controller;
        addKeyListener(this);
        add(new CapturePanel(image, controller));
        setUndecorated(true);
        setAlwaysOnTop(true);
        pack();
        setVisible(true);
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            controller.onSelectionCanceled();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
