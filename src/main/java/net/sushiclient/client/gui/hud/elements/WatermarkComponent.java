package net.sushiclient.client.gui.hud.elements;

import net.sushiclient.client.ModInformation;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.gui.hud.TextElementComponent;

public class WatermarkComponent extends TextElementComponent {
    public WatermarkComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
    }

    @Override
    protected String getText() {
        return ModInformation.name + "-" + ModInformation.version + "\nWelcome to " + ModInformation.name + " " + ModInformation.version;
    }

    @Override
    public String getId() {
        return "watermark";
    }

    @Override
    public String getName() {
        return "Watermark";
    }
}
