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

package net.sushiclient.client.task.tasks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.Item;
import net.sushiclient.client.task.TaskAdapter;
import net.sushiclient.client.utils.player.InventoryType;
import net.sushiclient.client.utils.player.InventoryUtils;
import net.sushiclient.client.utils.player.ItemSlot;

import java.util.Comparator;

public class ItemSwitchTask extends TaskAdapter<Item, Boolean> {

    private final Comparator<ItemSlot> comparator;
    private final boolean swap;
    private final boolean fromInventory;

    public ItemSwitchTask(Comparator<ItemSlot> comparator, boolean swap, boolean fromInventory) {
        this.comparator = comparator;
        this.swap = swap;
        this.fromInventory = fromInventory;
    }

    public ItemSwitchTask(Comparator<ItemSlot> comparator, boolean fromInventory) {
        this(comparator, true, fromInventory);
    }

    public ItemSwitchTask(Comparator<ItemSlot> comparator, ItemSwitchMode mode) {
        this(comparator, mode != ItemSwitchMode.NONE, mode != ItemSwitchMode.HOTBAR);
    }

    @Override
    public void tick() throws Exception {
        if (getInput() == null || !swap) {
            stop(true);
            return;
        }
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) {
            stop(false);
            return;
        }

        ItemSlot itemSlot = InventoryUtils.findItemSlot(getInput(), comparator, InventoryType.values());
        if (itemSlot == null) {
            stop(false);
            return;
        }

        // hotbar
        if (itemSlot.getIndex() < 9) {
            InventoryUtils.moveHotbar(itemSlot.getIndex());
            stop(true);
            return;
        }

        if (fromInventory) {
            InventoryUtils.moveToHotbar(itemSlot);
        }

        stop(false);
    }
}
