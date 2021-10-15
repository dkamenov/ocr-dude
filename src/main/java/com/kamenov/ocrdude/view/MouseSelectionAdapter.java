package com.kamenov.ocrdude.view;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputAdapter;
import lombok.Getter;


public class MouseSelectionAdapter extends MouseInputAdapter {

    private SelectionListener listener;

    @Getter
    private Rectangle selection = new Rectangle();

    public MouseSelectionAdapter(SelectionListener listener) {
        this.listener = listener;
    }


    @Override
    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        selection.setRect(p.x, p.y, 0,0);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        listener.selectionDone();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point location = e.getPoint();

        int width = location.x - selection.x;
        int height = location.y - selection.y;

        selection.width = width;
        selection.height = height;

        listener.selectionChanged(normalizeRect(selection));
    }

    private Rectangle normalizeRect(Rectangle rect) {

        Rectangle newRect = new Rectangle();
        newRect.x = Math.min(rect.x, rect.x + rect.width);
        newRect.width = Math.abs(rect.width);
        newRect.y = Math.min(rect.y, rect.y + rect.height);
        newRect.height = Math.abs(rect.height);

        return newRect;
    }
}
