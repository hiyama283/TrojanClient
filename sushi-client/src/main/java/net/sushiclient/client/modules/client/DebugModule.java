package net.sushiclient.client.modules.client;

import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.modules.*;

public class DebugModule extends BaseModule {

    public DebugModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    @Override
    public String getDefaultName() {
        return "Debug";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.CLIENT;
    }
}
