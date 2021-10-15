package com.kamenov.ocrdude.view;

import com.kamenov.ocrdude.MainView;
import com.kamenov.ocrdude.OcrController;
import com.kamenov.ocrdude.OcrException;
import com.kamenov.ocrdude.utils.TesseractHelper;
import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TrayIconView implements MainView {
    private OcrController controller;
    private TrayIcon trayIcon;
    private CheckboxMenuItem selectedLanguageItem;

    public TrayIconView(OcrController controller) {
        this.controller = controller;
    }

    @Override
    public void display() {
        installSystemTray();
    }

    @Override
    public void hide() {
        // nothing to do
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

            Menu languagesMenu = makeLanguagesMenu();
            popup.add(languagesMenu);

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


    private Menu makeLanguagesMenu() {
        Menu languagesMenu = new Menu("Language");
        for(Entry entry : TesseractHelper.getLanguageCodes().entrySet()) {
            CheckboxMenuItem item = new CheckboxMenuItem((String) entry.getKey());
            item.addItemListener(evt -> {
                this.languageSelected(evt);
            });
            if (controller.getActiveLanguage().equals(entry.getValue())) {
                selectedLanguageItem = item;
                item.setState(true);
            }
            languagesMenu.add(item);
        }
        return languagesMenu;
    }

    public void languageSelected(ItemEvent evt) {
        selectedLanguageItem.setState(false); // uncheck old item
        selectedLanguageItem = (CheckboxMenuItem) evt.getSource();
        log.debug("Selected language: {}", selectedLanguageItem.getLabel());
        controller.onLanguageSelected(TesseractHelper.getLanguageCodes().get(selectedLanguageItem.getLabel()));
    }

    @Override
    public void close() {
        SystemTray.getSystemTray().remove(trayIcon);
    }
}
