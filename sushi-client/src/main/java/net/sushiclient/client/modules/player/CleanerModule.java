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

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.player.InventoryType;
import net.sushiclient.client.utils.player.InventoryUtils;
import net.sushiclient.client.utils.player.ItemSlot;

import java.util.ArrayList;

public class CleanerModule extends BaseModule implements ModuleSuffix {
    private final Configuration<IntRange> sword;
    private final Configuration<IntRange> pickaxe;
    private final Configuration<IntRange> gapple;
    private final Configuration<IntRange> echest;
    private final Configuration<IntRange> trapdoor;
    private final ArrayList<ItemSlot> itemSlots = new ArrayList<>();

    public CleanerModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        sword = provider.get("sword", "Sword(st)", null, IntRange.class, new IntRange(1, 4, 1, 1));
        pickaxe = provider.get("pickaxe", "Pickaxe(st)", null, IntRange.class, new IntRange(1, 4, 1, 1));
        gapple = provider.get("gapple", "Golden apple(st)", null, IntRange.class, new IntRange(3, 10, 1, 1));
        echest = provider.get("echest", "Ender chest(st)", null, IntRange.class, new IntRange(1, 4, 1, 1));
        trapdoor = provider.get("trapdoor", "Trap door(st)", null, IntRange.class, new IntRange(1, 4, 1, 1));

        for (int i = 0; i < InventoryType.MAIN.getSize(); i++) {
            itemSlots.add(new ItemSlot(i));
        }
        for (int i = 0; i < InventoryType.HOTBAR.getSize(); i++) {
            itemSlots.add(new ItemSlot(i));
        }
    }

    private String suffix = "";

    private void drop(ItemSlot slot) {
        InventoryUtils.clickItemSlot(slot, ClickType.THROW, 1);
    }


    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        int swordCount = 0, pickaxeCount = 0, gappleCount = 0, echestCount = 0, trapdoorCount = 0;
        for (ItemSlot itemSlot : itemSlots) {
            suffix = String.valueOf(itemSlot.getIndex());
            Item item = itemSlot.getItemStack().getItem();

            if (item == Items.DIAMOND_SWORD) {
                if (swordCount == this.sword.getValue().getCurrent()) {
                    drop(itemSlot);
                    return;
                } else
                    swordCount++;
            }
            if (item == Items.DIAMOND_PICKAXE) {
                if (pickaxeCount == this.pickaxe.getValue().getCurrent()) {
                    drop(itemSlot);
                    return;
                } else
                    pickaxeCount++;
            }
            if (item == Items.GOLDEN_APPLE) {
                if (gappleCount == this.gapple.getValue().getCurrent()) {
                    drop(itemSlot);
                    return;
                } else
                    gappleCount++;
            }
            if (item == Item.getItemFromBlock(Blocks.ENDER_CHEST)) {
                if (echestCount == this.echest.getValue().getCurrent()) {
                    drop(itemSlot);
                    return;
                } else
                    echestCount++;
            }
            if (item == Item.getItemFromBlock(Blocks.IRON_TRAPDOOR)) {
                if (trapdoorCount == this.trapdoor.getValue().getCurrent()) {
                    drop(itemSlot);
                    return;
                } else
                    trapdoorCount++;
            }
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
        return "Cleaner";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }

    @Override
    public String getSuffix() {
        return suffix;
    }
}
