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

import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.sushiclient.client.config.Config;
import net.sushiclient.client.config.ConfigInjector;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.input.InputUpdateEvent;
import net.sushiclient.client.events.player.BlockCollisionEvent;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;

public class NoSlowModule extends BaseModule {

    @Config(id = "sneak", name = "Sneak")
    public Boolean sneak = false;

    @Config(id = "block", name = "Block")
    public Boolean block = true;

    @Config(id = "food", name = "Food")
    public Boolean food = true;

    @Config(id = "bow", name = "Bow")
    public Boolean bow = true;

    @Config(id = "potion", name = "potion")
    public Boolean potion = true;

    @Config(id = "shield", name = "Shield")
    public Boolean shield = true;

    public NoSlowModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        new ConfigInjector(provider).inject(this);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
        Blocks.ICE.setDefaultSlipperiness(0.98f);
        Blocks.FROSTED_ICE.setDefaultSlipperiness(0.98f);
        Blocks.PACKED_ICE.setDefaultSlipperiness(0.98f);
        Blocks.SLIME_BLOCK.setDefaultSlipperiness(0.8F);
    }

    @EventHandler(timing = EventTiming.POST)
    public void onInputUpdate(InputUpdateEvent e) {
        if (getPlayer().isRiding()) return;
        if (sneak && getPlayer().isSneaking() || checkItems()) {
            getPlayer().movementInput.moveStrafe *= 5F;
            getPlayer().movementInput.moveForward *= 5F;
        }
    }

    private boolean checkItems() {
        if (!getPlayer().isHandActive()) return false;
        Item activeItem = getPlayer().getActiveItemStack().getItem();
        if (food && activeItem instanceof ItemFood) return true;
        if (bow && activeItem instanceof ItemBow) return true;
        if (potion && activeItem instanceof ItemPotion) return true;
        if (shield && activeItem instanceof ItemShield) return true;
        return false;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        if (block) {
            Blocks.ICE.setDefaultSlipperiness(0.4945f);
            Blocks.FROSTED_ICE.setDefaultSlipperiness(0.4945f);
            Blocks.PACKED_ICE.setDefaultSlipperiness(0.4945f);
            Blocks.SLIME_BLOCK.setDefaultSlipperiness(0.4945F);
        } else {
            Blocks.ICE.setDefaultSlipperiness(0.98f);
            Blocks.FROSTED_ICE.setDefaultSlipperiness(0.98f);
            Blocks.PACKED_ICE.setDefaultSlipperiness(0.98f);
            Blocks.SLIME_BLOCK.setDefaultSlipperiness(0.8F);
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onBlockCollision(BlockCollisionEvent e) {
        if (!block) return;
        if (!getPlayer().equals(e.getEntity())) return;
        e.setCancelled(true);
    }

    @Override
    public String getDefaultName() {
        return "NoSlow";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.MOVEMENT;
    }
}
