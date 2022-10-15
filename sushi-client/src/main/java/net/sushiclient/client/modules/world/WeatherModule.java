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

import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.world.RainStrengthGetEvent;
import net.sushiclient.client.events.world.ThunderStrengthGetEvent;
import net.sushiclient.client.modules.*;

public class WeatherModule extends BaseModule {

    public WeatherModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
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
    public void onRainStrengthGetEvent(RainStrengthGetEvent e) {
        e.setValue(0);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onThunderStrengthGetEvent(ThunderStrengthGetEvent e) {
        e.setValue(0);
    }

    @Override
    public String getDefaultName() {
        return "Weather";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.WORLD;
    }
}
