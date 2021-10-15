package com.kamenov.ocrdude.utils;

import com.kamenov.ocrdude.OcrException;
import java.awt.AWTException;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

public class ScreenshotHelper {
    public static BufferedImage takeScreenshot() {
        try {
            Rectangle capture = getCapturableRect();
            BufferedImage image = new Robot().createScreenCapture(capture);
            return image;
        } catch (AWTException exp) {
            throw new OcrException("Could not take screeenshot", exp);
        }
    }

    private static Rectangle getCapturableRect() {
        Rectangle capture = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(new JFrame().getGraphicsConfiguration());
        capture.x += insets.left;
        capture.y += insets.top;
        capture.width -= insets.right+insets.left;
        capture.height -= insets.bottom+insets.top;
        return capture;
    }
}
