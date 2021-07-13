package com.kamenov.ocrdude.utils;

import com.kamenov.ocrdude.OcrException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITessAPI.TessOcrEngineMode;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

@Slf4j
public class TesseractHelper {
    Tesseract tesseract;

    @Getter
    private String activeLanguage;

    public TesseractHelper(String languageCode) {
        tesseract = new Tesseract();
        setLanguage(languageCode);
        tesseract.setOcrEngineMode(TessOcrEngineMode.OEM_LSTM_ONLY);

        FileHelper.extractDataFile(languageCode, false);
        log.debug("Setting data path to {}", FileHelper.dataPath().toString());
        tesseract.setDatapath(FileHelper.dataPath().toString());
    }

    public void setLanguage(String langCode) {
        log.info("Switching to '{}'", langCode);
        activeLanguage = langCode;
        tesseract.setLanguage(activeLanguage);
    }

    public String extractText(BufferedImage image) {
        try {
            return tesseract.doOCR(image);
        } catch (TesseractException e) {
            throw new OcrException("Could not perform OCR", e);
        }
    }

    public static Map<String, String> getLanguageCodes() {
        ClassLoader classLoader = TesseractHelper.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("languages.txt");
        BufferedReader rdr = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        Map<String, String> langCodes = new LinkedHashMap();
        try {
            while ((line = rdr.readLine()) != null) {
                String[] fields = line.split("\\|");
                if(fields.length < 2) {
                    continue;
                }
                langCodes.put(fields[1], fields[0]);
            }
        }catch (IOException e) {
            throw new OcrException("Could not read language codes", e);
        }
        return langCodes;
    }
}
