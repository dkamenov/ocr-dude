package com.kamenov.ocrdude.view;

import com.kamenov.ocrdude.OcrController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CapturePanel extends JPanel implements SelectionListener {
    BufferedImage image;
    MouseSelectionAdapter mouse;
    Rectangle selection;
    OcrController controller;

    private static  int CORNER_RECT_SIZE = 5;

    public CapturePanel(BufferedImage img, OcrController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
        image = img;

        mouse = new MouseSelectionAdapter(this);

        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        setSize(img.getWidth(), img.getHeight());
    }
    public Dimension getPreferredSize() {
        return new Dimension(image.getWidth(), image.getHeight());
    }

    private void drawSelectionRect(Graphics g) {
        g.setPaintMode();
        g.setColor(Color.RED);
        g.drawRect(selection.x, selection.y, selection.width, selection.height);
        g.fillRect(selection.x, selection.y, CORNER_RECT_SIZE, CORNER_RECT_SIZE);
        g.fillRect(selection.x + selection.width - CORNER_RECT_SIZE, selection.y + selection.height - CORNER_RECT_SIZE, CORNER_RECT_SIZE, CORNER_RECT_SIZE);
        g.fillRect(selection.x + selection.width - CORNER_RECT_SIZE, selection.y, CORNER_RECT_SIZE, CORNER_RECT_SIZE);
        g.fillRect(selection.x, selection.y + selection.height - CORNER_RECT_SIZE, CORNER_RECT_SIZE, CORNER_RECT_SIZE);
    }

    @Override
    public void paintComponent(Graphics g) {

        BufferedImage drawImage;
        drawImage = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_ARGB);
        Graphics g2 = drawImage.getGraphics();

        if (selection != null) {
            drawSelectionRect(g2);
        }
        g.drawImage(image, 0,0, null);
        g.drawImage(drawImage,0,0,null);
        g2.dispose();
    }

    @Override
    public void selectionChanged(Rectangle rect) {
        selection = rect;
        repaint();
    }

    @Override
    public void selectionDone() {
        log.info("selection done");
        BufferedImage subimage = image.getSubimage(selection.x, selection.y, selection.width, selection.height);
        controller.onImageSelected(subimage);
    }
}
