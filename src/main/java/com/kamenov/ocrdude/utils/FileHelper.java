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
    private static final Path DATA_DIR = Paths.get(getDataDir(), "data");
    private static final Path NATIVE_LIB_DIR = Paths.get(getDataDir(), "lib");
    private static final String TESSERACT_NATIVE_LIB_MAC = "lib/libtesseract.5.dylib";

    private static final String[] DOWNLOAD_URLS = {
            "https://raw.githubusercontent.com/tesseract-ocr/tessdata_best/master/",
            "https://raw.githubusercontent.com/tesseract-ocr/tessdata/master/", // fallback to lower quality
    };

    private static String getDataDir() {
        if (SystemUtils.IS_OS_MAC_OSX) {
            return Paths.get(System.getProperty("user.home"), "Library", "Application Support", "OCR Dude").toString();
        }
        return Paths.get(System.getProperty("user.home"), ".ocrdude").toString();
    }

    public static void extractDataFile(String langCode, boolean forceDownloadNew) {
        try {
            String langFileName = langCode + ".traineddata";

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
                        String.format(downloadUrl + "%s.traineddata", langCode));
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
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(targetFile.toAbsolutePath().toString());
        log.info("Downloading {} -> {}...", url, targetFile);
        long bytes = fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        log.info("Downloaded {} bytes", bytes);
    }

    public static void loadNativeLibSafe() {
        if (!SystemUtils.IS_OS_MAC_OSX) {
            return;
        }

        if (null == FileHelper.class.getClassLoader().getResource(TESSERACT_NATIVE_LIB_MAC)) {
            log.warn("Native lib {} not bundled, skipping...", TESSERACT_NATIVE_LIB_MAC);
            return;
        }
        Path targetPath = NATIVE_LIB_DIR.resolve("libtesseract.dylib");

        if (!Files.exists(targetPath.getParent())) {
            try {
                Files.createDirectories(targetPath.getParent());
            } catch (IOException e) {
                throw new OcrException(e);
            }
        }

        try (
                InputStream in = FileHelper.class.getClassLoader().getResourceAsStream(TESSERACT_NATIVE_LIB_MAC);
                OutputStream out = Files.newOutputStream(targetPath, CREATE, WRITE);
        ) {
            log.info("Copying library to: " + targetPath.toAbsolutePath());
            IOUtils.copy(in, out);
        } catch (IOException e) {
            log.error("Could not copy dynamic library", e);
        }
        System.setProperty("jna.library.path", targetPath.toAbsolutePath().getParent().toString());
    }

    public static Path dataPath() {
        return DATA_DIR;
    }
}
