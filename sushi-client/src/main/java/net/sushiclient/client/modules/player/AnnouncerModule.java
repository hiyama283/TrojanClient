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

import net.minecraft.client.entity.EntityPlayerSP;
import net.sushiclient.client.command.GuiLogger;
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

        if (e.getEntityIn() instanceof EntityPlayerSP && e.getEntityIn().isDead) {
            GuiLogger.send(e.getEntityIn().getName() + " Has dead.");
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
