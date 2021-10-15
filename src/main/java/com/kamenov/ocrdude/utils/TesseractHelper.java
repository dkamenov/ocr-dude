package com.kamenov.ocrdude.utils;

import com.kamenov.ocrdude.OcrException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITessAPI.TessOcrEngineMode;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

@Slf4j
public class TesseractHelper {
    private Tesseract tesseract;
    private static LinkedHashMap<String, String> languageCodes;

    @Getter
    private String activeLanguage;

    public TesseractHelper(String languageCode) {
        FileHelper.loadNativeLibSafe();
        tesseract = new Tesseract();
        tesseract.setOcrEngineMode(TessOcrEngineMode.OEM_LSTM_ONLY);
        setLanguage(languageCode);
    }

    public void setLanguage(String langCode) {
        log.info("Switching to '{}'", langCode);
        FileHelper.extractDataFile(langCode, false);
        log.debug("Setting data path to {}", FileHelper.dataPath().toString());
        tesseract.setDatapath(FileHelper.dataPath().toString());
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
        if (null == languageCodes) {
            ClassLoader classLoader = TesseractHelper.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("languages.txt");
            BufferedReader rdr = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            Map<String, String> codes = new LinkedHashMap();
            try {
                while ((line = rdr.readLine()) != null) {
                    String[] fields = line.split("\\|");
                    if (fields.length < 2) {
                        continue;
                    }
                    codes.put(fields[1], fields[0]);
                }
            } catch (IOException e) {
                throw new OcrException("Could not read language codes", e);
            }
            languageCodes = codes.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        }

        return languageCodes;
    }
}
