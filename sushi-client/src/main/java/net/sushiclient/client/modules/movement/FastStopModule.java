package net.sushiclient.client.modules.movement;

import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.player.MovementUtils;

public class FastStopModule extends BaseModule {

    public FastStopModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
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

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        Vec3d inputs = MovementUtils.getMoveInputs(getPlayer()).normalize();
        if (inputs.x == 0 && inputs.z == 0) {
            getPlayer().motionX = 0;
            getPlayer().motionZ = 0;
        }
    }

    @Override
    public String getDefaultName() {
        return "FastStop";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.MOVEMENT;
    }
}
