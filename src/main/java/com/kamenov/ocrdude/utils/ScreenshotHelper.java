package com.kamenov.ocrdude.utils;

import com.kamenov.ocrdude.OcrException;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public class ScreenshotHelper {
    public static BufferedImage takeScreenshot() {
        Rectangle capture = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        try {
            BufferedImage image = new Robot().createScreenCapture(capture);
            return image;
        } catch (AWTException exp) {
            throw new OcrException("Could not take screeenshot", exp);
        }
    }
}
