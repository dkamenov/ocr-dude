package com.kamenov.ocrdude.view;

import java.awt.Rectangle;

public interface SelectionListener {
    void selectionChanged(Rectangle rect);
    void selectionDone();
}
