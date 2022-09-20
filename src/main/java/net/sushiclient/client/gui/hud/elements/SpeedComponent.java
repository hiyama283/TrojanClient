package net.sushiclient.client.gui.hud.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.gui.hud.TextElementComponent;
import net.sushiclient.client.utils.player.SpeedUtils;

import java.text.DecimalFormat;

public class SpeedComponent extends TextElementComponent {

    private static final DecimalFormat FORMATTER = new DecimalFormat("0.0");
    private final Configuration<String> format;

    public SpeedComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
        format = getConfiguration("format", "Format", null, String.class, "{m/s} m/s");
    }

    @Override
    protected String getText() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return "";
        return format.getValue().replace("{m/s}", FORMATTER.format(SpeedUtils.getMps(player)))
                .replace("{km/h}", FORMATTER.format(SpeedUtils.getKmph(player)));
    }
}
