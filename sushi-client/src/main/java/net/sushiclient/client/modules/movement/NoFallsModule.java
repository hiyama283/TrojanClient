/*
 * Contact github.com/hiyama283
 * Project "sushi-client"
 *
 * Copyright 2022 hiyama283
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sushiclient.client.modules.movement;

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;

public class NoFallsModule extends BaseModule {
    private final Configuration<DoubleRange> lagBackToggleMotion;
    private final Configuration<IntRange> lagBackMotionValue;
    public NoFallsModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        lagBackToggleMotion = provider.get("lag_back_toggle_motion", "Lagback toggle motion", null, DoubleRange.class,
                new DoubleRange(1, 2, 0.1, 0.1, 1));
        lagBackMotionValue = provider.get("lag_back_motion_val", "Lagback motion val", null, IntRange.class, new IntRange(10, 20, 5, 1));
    }

    @EventHandler(timing = EventTiming.POST)
    public void onClientTick(ClientTickEvent e) {
        if (getPlayer().motionY <= -(lagBackToggleMotion.getValue().getCurrent())) {
            getPlayer().motionY = lagBackMotionValue.getValue().getCurrent();
            getPlayer().fallDistance = 0;
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
        return "No Falls";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.MOVEMENT;
    }
}
