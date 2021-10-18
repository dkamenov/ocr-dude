package com.kamenov.ocrdude;

import com.kamenov.ocrdude.utils.FileHelper;
import com.kamenov.ocrdude.view.TrayIconView;
import java.awt.SystemTray;
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
        new App().run(args);
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
