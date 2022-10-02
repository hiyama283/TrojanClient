package net.sushiclient.client.gui.hud;

import net.sushiclient.client.utils.render.GuiUtils;

public class HotbarHudElementComponent extends VirtualHudElementComponent {
    @Override
    public String getId() {
        return "hotbar";
    }

    @Override
    public String getName() {
        return "Hotbar";
    }

    @Override
    public void onRelocate() {
        setWindowX(GuiUtils.getWidth() / 2 - 91);
        setWindowY(GuiUtils.getHeight() - 22);
        setWidth(182);
        setHeight(22);
    }
}
