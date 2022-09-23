package net.sushiclient.client.modules.movement;

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;

public class MotionMultiplierModule extends BaseModule {
    private final Configuration<DoubleRange> multiplier;
    private final Configuration<Boolean> X;
    private final Configuration<Boolean> Y;
    private final Configuration<Boolean> Z;
    public MotionMultiplierModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        multiplier = provider.get("multiplier", "Multiplier", null, DoubleRange.class,
                new DoubleRange(1.5, 5, 0.1, 0.1, 1));
        X = provider.get("x", "X", null, Boolean.class, true);
        Y = provider.get("y", "Y", null, Boolean.class, false);
        Z = provider.get("z", "Z", null, Boolean.class, true);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        if (X.getValue()) {
            getPlayer().motionX *= multiplier.getValue().getCurrent();
        }

        if (Y.getValue()) {
            getPlayer().motionY *= multiplier.getValue().getCurrent();
        }

        if (Z.getValue()) {
            getPlayer().motionZ *= multiplier.getValue().getCurrent();
        }
    }

    @Override
    public String getDefaultName() {
        return "MotionMultiplier";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.MOVEMENT;
    }
}
