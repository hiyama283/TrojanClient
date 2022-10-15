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

package net.sushiclient.client.modules.client;

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.modules.*;

public class TestModule extends BaseModule {
    private final Configuration<DoubleRange> multiplier;
    private final Configuration<IntRange> input;
    private final Configuration<TestSelectList> motionFacing;
    public TestModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        multiplier = provider.get("multiplier", "Multiplier", null, DoubleRange.class, new DoubleRange(0.5, 2, 0.1, 0.1, 1));
        input = provider.get("input", "Input numeric", null, IntRange.class, new IntRange(2, 5, 1, 1));
        motionFacing = provider.get("motion_facing", "Motion facing", null, TestSelectList.class, TestSelectList.Y);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);

        if (motionFacing.getValue() == TestSelectList.X) {
            mc.player.motionX = 0;
            mc.player.motionX = input.getValue().getCurrent() * multiplier.getValue().getCurrent();
        } else if(motionFacing.getValue() == TestSelectList.Z) {
            mc.player.motionZ = 0;
            mc.player.motionZ = input.getValue().getCurrent() * multiplier.getValue().getCurrent();
        } else {
            mc.player.motionY = 0;
            mc.player.motionY = input.getValue().getCurrent() * multiplier.getValue().getCurrent();
        }
        setEnabled(false);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    @Override
    public String getDefaultName() {
        return "TestModule";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.CLIENT;
    }
}
