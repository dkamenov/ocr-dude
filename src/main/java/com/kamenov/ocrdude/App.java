package com.kamenov.ocrdude;

import com.kamenov.ocrdude.utils.FileHelper;
import com.kamenov.ocrdude.view.TrayIconView;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.lang.reflect.InaccessibleObjectException;
import java.util.logging.FileHandler;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.SystemUtils;


@Slf4j
@NoArgsConstructor
public class App extends JFrame {

    private MainView view;

    public static void main(String[] args) {
        preconfigureUi();
        new App().run(args);
    }

    private static void preconfigureUi() {
        if (SystemUtils.IS_OS_MAC) {
            System.setProperty("apple.awt.application.name", FileHelper.APP_NAME);
        } else if (SystemUtils.IS_OS_LINUX) {
            Toolkit xToolkit = Toolkit.getDefaultToolkit();
            try {
                java.lang.reflect.Field awtAppClassNameField = xToolkit.getClass().getDeclaredField("awtAppClassName");
                awtAppClassNameField.setAccessible(true);
                awtAppClassNameField.set(xToolkit, FileHelper.APP_NAME);
            } catch (IllegalAccessException | NoSuchFieldException | InaccessibleObjectException e) {
                log.warn("Could not set App title on Linux", e);
            }
        }
    }
    private void run(String[] args)  {

        try {
            Options options = new Options();
            options.addOption("l", "language", true, "Language code (ISO 639-1)");
            options.addOption("w", "window", false, "Window mode (no tray icon)");

            CommandLineParser parser = new DefaultParser();
            CommandLine cmdLine = parser.parse(options, args);

            boolean windowMode = cmdLine.hasOption("w");
            OcrController controller = new OcrController(cmdLine.getOptionValue('l'));

            if (!SystemTray.isSupported()) {
                log.warn("System tray is not supported on this OS, defaulting to window mode");
            }

            if (!windowMode && SystemTray.isSupported()) {
                view = new TrayIconView(controller);
            } else {
                view = new MainWindowView(controller);
            }
            controller.setMainView(view);
            view.display();
        } catch (OcrException | ParseException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
