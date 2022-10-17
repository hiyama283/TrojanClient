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

package net.sushiclient.client.modules.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.sushiclient.client.command.GuiLogger;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.GameTickEvent;
import net.sushiclient.client.gui.hud.elements.NotificationComponent;
import net.sushiclient.client.modules.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PvPInfoModule extends BaseModule {
    private final Configuration<Boolean> enderPearl;
    private final Configuration<Boolean> visualRange;
    public PvPInfoModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        enderPearl = provider.get("ender_pearl", "Ender pearl", null, Boolean.class, true);
        visualRange = provider.get("visual_range", "Visual range", null, Boolean.class, false);
    }

    @EventHandler(timing = EventTiming.POST)
    public void onUpdate(GameTickEvent event) {
        if (enderPearl.getValue()) {
            enderPearl();
        }
        if (visualRange.getValue()) {
            visualRange();
        }
    }

    private final HashSet<EntityPlayer> alreadyVisualPlayer = new HashSet<>();
    private void visualRange() {
        List<EntityPlayer> towardPlayer = new ArrayList<>();
        for (EntityPlayer playerEntity : mc.world.playerEntities) {
            synchronized (alreadyVisualPlayer) {
                if (!alreadyVisualPlayer.contains(playerEntity)) {
                    towardPlayer.add(playerEntity);
                }
            }
        }

        towardPlayer.forEach(b -> {
            String s = b.getName() + " has towarding you!";
            GuiLogger.send(s);
        });

        synchronized (alreadyVisualPlayer) {
            alreadyVisualPlayer.clear();
            alreadyVisualPlayer.addAll(mc.world.playerEntities);
        }
    }

    private boolean enderPearlFlag = false;
    private void enderPearl() {
        if (mc.world == null || mc.player == null) {
            return;
        }

        Entity enderPearl = null;
        for (Entity e : mc.world.loadedEntityList) {
            if (e instanceof EntityEnderPearl) {
                enderPearl = e;
                break;
            }
        }
        if (enderPearl == null) {
            enderPearlFlag = true;
            return;
        }
        EntityPlayer closestPlayer = null;
        for (final EntityPlayer entity : mc.world.playerEntities) {
            if (closestPlayer != null) {
                if (closestPlayer.getDistance(enderPearl) <= entity.getDistance(enderPearl)) {
                    continue;
                }
            }
            closestPlayer = entity;
        }
        if (closestPlayer == mc.player) {
            enderPearlFlag = false;
        }
        if (closestPlayer != null && enderPearlFlag) {
            String faceing = enderPearl.getHorizontalFacing().toString();
            if (faceing.equals("west")) {
                faceing = "east";
            } else if (faceing.equals("east")) {
                faceing = "west";
            }

            String s = ChatFormatting.RED + closestPlayer.getName() + ChatFormatting.DARK_GRAY + " has just thrown a pearl heading " + faceing + "!";
            GuiLogger.send(s);
            enderPearlFlag = false;
        }
    }

    @Override
    public String getDefaultName() {
        return "PvPInfo";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.RENDER;
    }
}
