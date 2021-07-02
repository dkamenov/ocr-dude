package com.kamenov.ocrdude;

import com.kamenov.ocrdude.utils.ScreenshotHelper;
import com.kamenov.ocrdude.utils.SoundEffectHelper;
import com.kamenov.ocrdude.view.CapturePanel;
import com.kamenov.ocrdude.utils.TesseractHelper;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OcrController {

    private static final String INSTALLATION_URL = "https://tesseract-ocr.github.io/tessdoc/Installation.html";
    private static final String TESSERACT_ERROR_TITLE = "Tesseract is not Installed";
    private static final String TESSERACT_ERROR_MESSAGE = "This program needs Tesseract to work properly. Follow "
            + " the instructions at " + INSTALLATION_URL + " to install it.";
    private static final String INSTALL_PROMPT = "(Click \"Yes\" to open page)";

    private TesseractHelper tesseractHelper;
    private JFrame selectionWindow = null;

    @Setter
    private MainView mainView;

    public OcrController(TesseractHelper helper) {
        this.tesseractHelper = helper;
    }


    public void onScreenshotSelected() {
        BufferedImage image = ScreenshotHelper.takeScreenshot();
        showSelectionWindow(image);
    }

    private void showSelectionWindow(BufferedImage image) {
        selectionWindow = new JFrame();
        selectionWindow.add(new CapturePanel(image, this));
        selectionWindow.setUndecorated(true);
        selectionWindow.setAlwaysOnTop(true);
        selectionWindow.pack();
        selectionWindow.setVisible(true);
    }

    public void onImageSelected(BufferedImage image) {
        selectionWindow.dispose();
        try {
            long t1 = System.currentTimeMillis();

            StringSelection stringSelection = new StringSelection(tesseractHelper.extractText(image));
            long t2 = System.currentTimeMillis();
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);

            log.debug("OCR'd text copied to clipboard. Time elapsed: {}s", (t2 - t1) / 1000);
            SoundEffectHelper.soundEffect();
        } catch (UnsatisfiedLinkError ule) {
            showErrorMessageAndExit(TESSERACT_ERROR_TITLE, TESSERACT_ERROR_MESSAGE);
        } catch (Exception e) {
            log.error("Fatal exception", e);
            showErrorMessageAndExit("And error occurred", e.getMessage());
        }
    }

    private void showErrorMessageAndExit(String title, String message) {
        if (Desktop.isDesktopSupported()) {
            if( JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, message + " "
                            + INSTALL_PROMPT, title, JOptionPane.YES_OPTION, JOptionPane.WARNING_MESSAGE)) {
                try {
                    Desktop.getDesktop().browse(new URI(INSTALLATION_URL));
                } catch (IOException | URISyntaxException ignored) {

                }
            }
        } else {
            JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
        }

        mainView.close();
    }

    public void onFileOpenSelected() {

        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home") + "/Desktop");
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage image = ImageIO.read(fileChooser.getSelectedFile());
                log.info("Opened File: " + fileChooser.getSelectedFile().getAbsolutePath());
                showSelectionWindow(image);
            } catch (IOException e) {
                log.warn("Could not open file '{}'", fileChooser.getSelectedFile());
            }
        }
    }

    public void onClose() {
        mainView.close();
    }
}
