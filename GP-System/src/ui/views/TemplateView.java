package ui.views;

import ui.UI;
import ui.structures.ViewPanel;
import engine.Engine;

public class TemplateView extends ViewPanel {
    private UI uiManager;
    private Engine engine;

    public TemplateView(UI uiManager, Engine engine) {
        super();

        this.uiManager = uiManager;
        this.engine = engine;
    }

    public void render() {
        // Your rendering code here
    }
}