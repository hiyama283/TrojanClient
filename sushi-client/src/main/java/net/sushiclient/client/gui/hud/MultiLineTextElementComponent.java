package net.sushiclient.client.gui.hud;

import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.utils.render.GuiUtils;
import net.sushiclient.client.utils.render.TextPreview;

abstract public class MultiLineTextElementComponent extends BaseHudElementComponent {
    public MultiLineTextElementComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
    }

    @Override
    public void onRender() {
        double x = getWindowX() + 1;
        double y = getWindowY() + 1;
        double width = 0;
        double height = 0;

        for (String s : getText()) {
            TextPreview preview = GuiUtils.prepareText(s, getTextSettings("text").getValue());
            preview.draw(x, y);
            width += preview.getWidth() + 3;
            height += preview.getHeight() + 4;
            y += preview.getHeight() + 1;
        }

        setWidth(width);
        setHeight(height);
    }

    abstract protected String[] getText();
}
