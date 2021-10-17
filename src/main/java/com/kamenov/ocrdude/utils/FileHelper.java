package com.kamenov.ocrdude.utils;

import com.kamenov.ocrdude.OcrException;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;

@Slf4j
public class FileHelper {
    public static final String APP_NAME = "OCR Dude";
    private static final String HIDDEN_SETTINGS_DIR_NAME = ".ocrdude";
    private static final Path DATA_DIR = Paths.get(getDataDir(), "data");
    private static final Path NATIVE_LIB_DIR = Paths.get(getDataDir(), "lib");
    private static final String LIBTESSERACT_DYLIB = "libtesseract.dylib";
    private static final String TESSERACT_5_NATIVE_LIB_NAME_MAC = "libtesseract.5.dylib";
    private static final String TESSERACT_5_NATIVE_LIB_MAC = "lib/" + TESSERACT_5_NATIVE_LIB_NAME_MAC;
    private static final String JNA_LIBRARY_PATH = "jna.library.path";
    private static final String TRAINEDDATA_SUFFIX = ".traineddata";
    private static final String[] DOWNLOAD_URLS = {
            "https://raw.githubusercontent.com/tesseract-ocr/tessdata_best/master/",
            "https://raw.githubusercontent.com/tesseract-ocr/tessdata/master/", // fallback to lower quality
    };

    private static String getDataDir() {
        String userHome = System.getProperty("user.home");
        Path dataDir = null;
        if (SystemUtils.IS_OS_MAC_OSX) {
            // as per https://developer.apple.com/library/archive/qa/qa1170/_index.html
            dataDir = Paths.get(userHome, "Library", "Application Support", APP_NAME);
        }
        if (SystemUtils.IS_OS_WINDOWS) {
            dataDir = Paths.get(System.getProperty("APPDATA", userHome), APP_NAME);
        }
        if (dataDir == null) {
            dataDir = Paths.get(userHome, HIDDEN_SETTINGS_DIR_NAME);
        }
        return dataDir.toString();
    }

    public static void extractDataFile(String langCode, boolean forceDownloadNew) {
        try {
            String langFileName = langCode + TRAINEDDATA_SUFFIX;

            Path targetFile = DATA_DIR.resolve(langFileName);
            if (!Files.exists(targetFile.getParent())) {
                Files.createDirectories(targetFile.getParent());
            }

            if (Files.exists(targetFile) && !forceDownloadNew) {
                log.info("File '{}' exists - reusing", targetFile);
                return;
            }

            for (String downloadUrl : DOWNLOAD_URLS) {
                URL url = new URL(
                        String.format(downloadUrl + langFileName));
                log.info("File {} does not exist, downloading from: '{}'...", targetFile, url);
                try {
                    downloadFile(url, targetFile);
                    return;
                } catch(FileNotFoundException e) {
                    log.error("File {} does not exist, trying next download location...", url);
                }
            }
            throw new OcrException("Could not download trained data file for language '" + langCode + "'");

        } catch (IOException e) {
            throw new OcrException("Could not extract trained data file", e);
        }
    }

    private static void downloadFile(URL url, Path targetFile) throws IOException {
        // TODO: retries
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(targetFile.toAbsolutePath().toString());
        log.info("Downloading {} -> {}...", url, targetFile);
        long bytes = fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        log.info("Downloaded {} bytes", bytes);
    }


    public static void loadNativeLibSafe() {
        /**
         * Load native lib if bundled in application jar - on Mac only. Tess4j already includes the Windows DLL
         * not feasible to bundle .so on Linux because dynamic libraries are heavily version-dependent
         */
        if (!SystemUtils.IS_OS_MAC_OSX) {
            return;
        }

        if (null == FileHelper.class.getClassLoader().getResource(TESSERACT_5_NATIVE_LIB_MAC)) {
            log.warn("Native lib {} not bundled, skipping loading...", TESSERACT_5_NATIVE_LIB_MAC);
            return;
        }
        Path targetPath = NATIVE_LIB_DIR.resolve(LIBTESSERACT_DYLIB);

        if (!Files.exists(targetPath.getParent())) {
            try {
                Files.createDirectories(targetPath.getParent());
            } catch (IOException e) {
                throw new OcrException(e);
            }
        }

        try (
                InputStream in = FileHelper.class.getClassLoader().getResourceAsStream(TESSERACT_5_NATIVE_LIB_MAC);
                OutputStream out = Files.newOutputStream(targetPath, CREATE, WRITE);
        ) {
            log.info("Copying library to: " + targetPath.toAbsolutePath());
            IOUtils.copy(in, out);
            System.setProperty(JNA_LIBRARY_PATH, targetPath.toAbsolutePath().getParent().toString());
        } catch (IOException e) {
            log.error("Could not copy dynamic library", e);
        }
    }

    public static Path dataPath() {
        return DATA_DIR;
    }
}
