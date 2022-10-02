package net.sushiclient.client.modules.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.client.GameSettingsSaveEvent;
import net.sushiclient.client.modules.*;

public class FullBrightModule extends BaseModule {

    private final GameSettings settings = Minecraft.getMinecraft().gameSettings;
    private float oldGamma;

    public FullBrightModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
    }

    @Override
    public void onEnable() {
        oldGamma = settings.gammaSetting;
        settings.gammaSetting = 15;
    }

    @Override
    public void onDisable() {
        settings.gammaSetting = oldGamma;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPreSave(GameSettingsSaveEvent e) {
        settings.gammaSetting = oldGamma;
    }

    @EventHandler(timing = EventTiming.POST)
    public void onPostSave(GameSettingsSaveEvent e) {
        settings.gammaSetting = 15;
    }

    @Override
    public String getDefaultName() {
        return "FullBright";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.RENDER;
    }
}
