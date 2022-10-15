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

package net.sushiclient.client.modules.player;

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.TimerUtils;
import net.sushiclient.client.utils.TpsUtils;

public class TimerModule extends BaseModule {

    private final Configuration<DoubleRange> multiplier;
    private final Configuration<Boolean> tpsSync;
    private int counter;

    public TimerModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        multiplier = provider.get("multiplier", "Mutliplier", null, DoubleRange.class, new DoubleRange(1, 10, 0.1, 0.1, 1));
        tpsSync = provider.get("tps_sync", "TPS Sync", null, Boolean.class, false);
        multiplier.addHandler(d -> {
            if (!isEnabled()) return;
            TimerUtils.pop(counter);
            push();
        });
    }

    private void push() {
        float m = (float) multiplier.getValue().getCurrent();
        if (tpsSync.getValue()) {
            m = (float) TpsUtils.getTps() / 20;
        }
        counter = TimerUtils.push(m);
    }

    @Override
    public void onEnable() {
        push();
    }

    @Override
    public void onDisable() {
        TimerUtils.pop(counter);
    }

    @Override
    public String getDefaultName() {
        return "Timer";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }
}
