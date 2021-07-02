package com.kamenov.ocrdude.utils;

import com.kamenov.ocrdude.OcrException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileHelper {
    private static final Path DATA_DIR = Paths.get(System.getProperty("user.home"), ".ocrdude", "data");

    private static final String[] DOWNLOAD_URLS = {
            "https://raw.githubusercontent.com/tesseract-ocr/tessdata_best/master/",
            "https://raw.githubusercontent.com/tesseract-ocr/tessdata/master/", // fallback to lower quality
    };

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

    public static Path dataPath() {
        return DATA_DIR;
    }
}
