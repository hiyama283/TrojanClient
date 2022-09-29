package net.sushiclient.client.modules.player;

import net.minecraft.client.entity.EntityPlayerSP;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.render.EntityRenderEvent;
import net.sushiclient.client.modules.*;

public class AnnouncerModule extends BaseModule {
    private final Configuration<DoubleRange> range;
    private final Configuration<Boolean> autoEz;
    private final Configuration<Boolean> pop;
    public AnnouncerModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        range = provider.get("range", "Range", null, DoubleRange.class, new DoubleRange(3, 5, 0.1, 0.1, 1));
        autoEz = provider.get("auto_ez", "AutoEz", null, Boolean.class, true);
        pop = provider.get("pop", "Pop", null, Boolean.class, true);
    }

    /*
    @EventHandler(timing = EventTiming.PRE)
    public void onPlayerPop(EntityRenderEvent e) {
        if (!pop.getValue()) return;

        if (e.getEntityIn().getDistance(getPlayer()) > range.getValue().getCurrent()) return;

        if (getPlayer() != null) {
            Entity entity = e.getEntityIn();

            // if (PlayerUtils.getDistance(entity) > range.getValue().getCurrent()) return;

            if (entity instanceof EntityPlayerSP) {
                chatLog(entity.getName() + " Has pop.");
            }
        }
    }

     */

    @EventHandler(timing = EventTiming.PRE)
    public void onPlayerDead(EntityRenderEvent e) {
        if (!autoEz.getValue()) return;

        if (e.getEntityIn().getDistance(getPlayer()) > range.getValue().getCurrent()) return;

        if (e.getEntityIn() instanceof  EntityPlayerSP && e.getEntityIn().isDead) {
            chatLog(e.getEntityIn().getName() + " Has dead.");
        }
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
        return "Announcer";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }
}
