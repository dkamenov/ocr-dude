package com.kamenov.ocrdude.utils;

import com.kamenov.ocrdude.OcrException;
import java.awt.SystemTray;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITessAPI.TessOcrEngineMode;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

@Slf4j
public class TesseractHelper {
    private static final String DATA_DIR = System.getenv("user.home") + ".ocrdude/data";
    Tesseract tesseract;

    public TesseractHelper(String languageCode) {
        this.tesseract = new Tesseract();
        tesseract.setLanguage(languageCode);
        tesseract.setOcrEngineMode(TessOcrEngineMode.OEM_LSTM_ONLY);

        FileHelper.extractDataFile(languageCode, false);
        log.debug("Setting data path to {}", FileHelper.dataPath().toString());
        tesseract.setDatapath(FileHelper.dataPath().toString());
    }

    public String extractText(BufferedImage image) {
        try {
            return tesseract.doOCR(image);
        } catch (TesseractException e) {
            throw new OcrException("Could not perform OCR", e);
        }
    }
}
