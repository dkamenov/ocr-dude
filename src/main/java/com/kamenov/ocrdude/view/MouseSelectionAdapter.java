package com.kamenov.ocrdude.view;

import com.kamenov.ocrdude.view.SelectionListener;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MouseSelectionAdapter implements MouseListener, MouseMotionListener {

    private SelectionListener listener;

    @Getter
    private Rectangle selection = new Rectangle();

    public MouseSelectionAdapter(SelectionListener listener) {
        this.listener = listener;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

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
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

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

    @Override
    public void mouseMoved(MouseEvent ignored) {
        // nothing to do
    }
}
