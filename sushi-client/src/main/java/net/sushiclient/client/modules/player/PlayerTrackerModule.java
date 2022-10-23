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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.sushiclient.client.command.GuiLogger;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.player.PlayerDeathEvent;
import net.sushiclient.client.events.player.PlayerPopEvent;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;

import java.util.HashMap;
import java.util.List;

public class PlayerTrackerModule extends BaseModule {
    private final Configuration<Boolean> death;
    private final Configuration<Boolean> pop;

    public PlayerTrackerModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        death = provider.get("death", "Death", null, Boolean.class, true);
        pop = provider.get("pop", "Pop", null, Boolean.class, true);
    }

    private final HashMap<EntityPlayer, Integer> popCount = new HashMap<>();

    @EventHandler(timing = EventTiming.POST)
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (e.getPlayer().equals(getPlayer()) || !death.getValue()) return;

        String s = TextFormatting.RED + e.getPlayer().getName() + TextFormatting.WHITE +
                " has died after ";
        if (popCount.containsKey(e.getPlayer())) {
            s += popCount.get(e.getPlayer());
        } else {
            s += "0";
        }
        s += " pop totems!";

        GuiLogger.send(e.getPlayer().getName().hashCode(), s, 3000);
        popCount.remove(e.getPlayer());
    }

    @EventHandler(timing = EventTiming.POST)
    public void onPlayerPop(PlayerPopEvent e) {
        if (e.getPlayer().equals(getPlayer()) || !pop.getValue()) return;

        if (popCount.containsKey(e.getPlayer())) {
            int i = popCount.get(e.getPlayer());
            i++;
            popCount.replace(e.getPlayer(), i);
        } else {
            popCount.put(e.getPlayer(), 1);
        }

        String s = TextFormatting.RED + e.getPlayer().getName() + TextFormatting.WHITE +
                " has pop " + popCount.get(e.getPlayer()) + " totems!";
        GuiLogger.send(e.getPlayer().getName().hashCode(), s, 3000);
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
        return "PlayerTracker";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }
}
