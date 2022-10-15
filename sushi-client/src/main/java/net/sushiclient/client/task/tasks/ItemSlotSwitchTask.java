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
import net.sushiclient.client.task.TaskAdapter;
import net.sushiclient.client.utils.player.InventoryUtils;
import net.sushiclient.client.utils.player.ItemSlot;

public class ItemSlotSwitchTask extends TaskAdapter<ItemSlot, Object> {

    @Override
    public void tick() throws Exception {
        stop(null);
        if (getInput() == null) return;
        ItemSlot itemSlot = getInput();
        EntityPlayerSP player = Minecraft.getMinecraft().player;

        if (itemSlot.getIndex() == player.inventory.currentItem) return;

        // hotbar
        if (itemSlot.getIndex() < 9) {
            InventoryUtils.moveHotbar(itemSlot.getIndex());
        } else {
            InventoryUtils.moveToHotbar(itemSlot);
        }
    }
}
