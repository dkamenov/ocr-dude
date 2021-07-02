package com.kamenov.ocrdude;


import java.awt.AWTException;

public class OcrException extends RuntimeException {
    public OcrException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }

    public OcrException(Throwable rootCause) {
        super(rootCause);
    }

    public OcrException(String message) {
        super(message);
    }
}
