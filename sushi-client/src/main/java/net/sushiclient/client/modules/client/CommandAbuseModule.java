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
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import org.apache.commons.lang3.RandomStringUtils;

public class CommandAbuseModule extends BaseModule {

    private final Configuration<DoubleRange> delay;
    private final Configuration<IntRange> length;
    private final Configuration<Boolean> noCap;
    private final Configuration<String> command;
    private int sleep;

    public CommandAbuseModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        this.command = provider.get("command", "Command", null, String.class, "/ignore ");
        this.length = provider.get("length", "Length", null, IntRange.class, new IntRange(32, 256, 0, 1));
        this.noCap = provider.get("no_cap", "No Cap", null, Boolean.class, false);
        this.delay = provider.get("delay", "Delay", null, DoubleRange.class, new DoubleRange(3, 10, 0, 0.2, 2));
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
        sleep = 0;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        if (sleep-- > 0) return;
        String cmd = command.getValue();
        int len = Math.min(256 - cmd.length(), length.getValue().getCurrent());
        if (len < 1) return;
        String name = RandomStringUtils.randomAlphanumeric(len);
        if (noCap.getValue()) name = name.toLowerCase();
        getPlayer().sendChatMessage(cmd + name);
        sleep = (int) (delay.getValue().getCurrent() * 20);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    @Override
    public String getDefaultName() {
        return "CommandAbuse";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.CLIENT;
    }
}
