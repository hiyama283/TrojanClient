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

package net.sushiclient.client.modules.world;

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.world.WorldTimeGetEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.TickUtils;

public class WorldTimeModule extends BaseModule {

    private final Configuration<Boolean> fastForward;
    private final Configuration<IntRange> time;

    public WorldTimeModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        fastForward = provider.get("fast_forward", "Fast Forward", null, Boolean.class, false);
        time = provider.get("time", "Time", null, IntRange.class, new IntRange(12, 24, 0, 1), () -> !fastForward.getValue(), false, 0);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    public long getWorldTime() {
        if (fastForward.getValue()) {
            return (long) ((TickUtils.current() + getClient().getRenderPartialTicks()) * 100L);
        } else {
            return time.getValue().getCurrent() * 1000L;
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onWorldTimeGet(WorldTimeGetEvent e) {
        e.setWorldTime(getWorldTime());
    }

    @Override
    public String getDefaultName() {
        return "WorldTime";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.WORLD;
    }
}
