package com.kamenov.ocrdude.view;

import com.kamenov.ocrdude.MainView;
import com.kamenov.ocrdude.OcrController;
import com.kamenov.ocrdude.OcrException;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TrayIconView implements MainView {
    private OcrController controller;
    private TrayIcon trayIcon;

    public TrayIconView(OcrController controller) {
        this.controller = controller;
    }

    @Override
    public void display() {
        installSystemTray();
    }

    @Override
    public void hide() {
        // nothhing to do
    }

    private void installSystemTray() {
        if (!SystemTray.isSupported())
            throw new OcrException("SystemTray not supported");

        try {
            SystemTray systemTray = SystemTray.getSystemTray();
            InputStream is = ClassLoader.getSystemResourceAsStream("images/general-ocr.png");
            Image image = ImageIO.read(is);

            PopupMenu popup = new PopupMenu();

            MenuItem menuItemSelect = new MenuItem("Select");
            menuItemSelect.addActionListener(e -> controller.onScreenshotSelected());
            popup.add(menuItemSelect);

            MenuItem menuItemFile = new MenuItem("File...");
            menuItemFile.addActionListener(e -> controller.onFileOpenSelected());
            popup.add(menuItemFile);

            MenuItem menuItemClose = new MenuItem("Close");
            menuItemClose.addActionListener(e -> controller.onClose());
            popup.add(menuItemClose);

            trayIcon = new TrayIcon(image, "OCR Dude", popup);
            trayIcon.setImageAutoSize(true);
            systemTray.add(trayIcon);

        } catch (IOException | AWTException e) {
            throw new OcrException("Could not load icon", e);
        }
    }

    @Override
    public void close() {
        SystemTray.getSystemTray().remove(trayIcon);
    }
}
