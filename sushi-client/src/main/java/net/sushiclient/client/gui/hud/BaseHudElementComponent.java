package net.sushiclient.client.gui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.Item;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.config.data.EspColor;
import net.sushiclient.client.gui.ComponentHandler;
import net.sushiclient.client.gui.base.BaseComponent;
import net.sushiclient.client.utils.player.InventoryType;
import net.sushiclient.client.utils.player.InventoryUtils;
import net.sushiclient.client.utils.player.ItemSlot;
import net.sushiclient.client.utils.render.GuiUtils;
import net.sushiclient.client.utils.render.TextPreview;
import net.sushiclient.client.utils.render.TextSettings;

import java.awt.*;
import java.util.HashSet;

abstract public class BaseHudElementComponent extends BaseComponent implements HudElementComponent {

    private static final TextSettings DEFAULT_TEXT_SETTINGS
            = new TextSettings("Calibri", new EspColor(Color.WHITE, false, true), 9, true);
    private final HashSet<Configuration<TextSettings>> textSettings = new HashSet<>();
    private final Configurations configurations;
    private final String id;
    private final String name;
    private boolean active = true;

    public BaseHudElementComponent(Configurations configurations, String id, String name) {
        this.configurations = configurations;
        this.id = id;
        this.name = name;
    }

    protected void renderItem(Item item, boolean showCount) {
        RenderItem renderer = Minecraft.getMinecraft().getRenderItem();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        renderer.zLevel = 200.0F;
        renderer.renderItemAndEffectIntoGUI(item.getDefaultInstance(), (int) (getWindowX() + 1), (int) (getWindowY() + 1));
        renderer.renderItemOverlays(Minecraft.getMinecraft().fontRenderer, item.getDefaultInstance(), (int) (getWindowX() + 1), (int) (getWindowY() + 1));
        renderer.zLevel = 0.0F;
        GlStateManager.enableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();

        if (showCount) {
            ItemSlot[] items = InventoryUtils.findItemSlots(item, null, InventoryType.values());
            int count = 0;
            for (ItemSlot itemSlot : items) {
                count += itemSlot.getItemStack().getCount();
            }

            TextPreview preview = GuiUtils.prepareText(String.valueOf(count), getTextSettings("text").getValue());
            preview.draw(getWindowX() + 13, getWindowY() + 9);

            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }
    }

    protected <T> Configuration<T> getConfiguration(String id, String name, String description, Class<T> tClass, T def) {
        return configurations.get("element." + this.id + "." + id, name, description, tClass, def);
    }

    protected Configuration<TextSettings> getTextSettings(String id) {
        for (Configuration<TextSettings> conf : textSettings) {
            if (conf.getId().equals(id)) return conf;
        }
        Configuration<TextSettings> newConf = getConfiguration(id, "", null, TextSettings.class, getDefaultTextSettings());
        textSettings.add(newConf);
        GuiUtils.prepareFont(newConf.getValue().getFont(), newConf.getValue().getPts());
        return newConf;
    }

    public Configurations getConfigurations() {
        return configurations;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
        for (ComponentHandler handler : getHandlers()) {
            if (handler instanceof HudElementComponentHandler) {
                ((HudElementComponentHandler) handler).setActive(active);
            }
        }
    }

    protected TextSettings getDefaultTextSettings() {
        return DEFAULT_TEXT_SETTINGS;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
}
