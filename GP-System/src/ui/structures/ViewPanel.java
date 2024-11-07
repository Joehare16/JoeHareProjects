package ui.structures;

import javax.swing.JPanel;

public abstract class ViewPanel extends JPanel {
    public abstract void render();

    public ViewPanel() {
        super();
    }

    public void destroy() {
        removeAll();
    }
}