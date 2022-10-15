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

package net.sushiclient.client.command.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.EnumHand;
import net.sushiclient.client.command.LogLevel;
import net.sushiclient.client.command.Logger;
import net.sushiclient.client.command.annotation.CommandAlias;
import net.sushiclient.client.command.annotation.Default;
import net.sushiclient.client.utils.player.ItemSlot;

import java.util.HashSet;
import java.util.Set;

@CommandAlias(value = "peek", description = "Peek shulker")
public class PeekCommand {

    @Default
    public void onDefault(Logger out) {
        Minecraft minecraft = Minecraft.getMinecraft();
        ItemStack nowItem = ItemSlot.hand(EnumHand.MAIN_HAND).getItemStack();
        out.send(LogLevel.INFO, nowItem.getDisplayName());

        if (nowItem.getItem() instanceof ItemShulkerBox) {
            NBTTagCompound nbtTagCompound = nowItem.serializeNBT();
            Set<String> keySet = nbtTagCompound.getKeySet();

            if (!keySet.contains("tag")) {
                return;
            }

            keySet = nbtTagCompound.getCompoundTag("tag").getKeySet();

            if (!keySet.contains("BlockEntityTag")) {
                return;
            }

            keySet = nbtTagCompound.getCompoundTag("tag").getCompoundTag("BlockEntityTag").getKeySet();

            if (!keySet.contains("Items")) {
                return;
            }

            TileEntityShulkerBox tileEntityShulkerBox = new TileEntityShulkerBox();
            tileEntityShulkerBox.writeToNBT(nbtTagCompound);

            minecraft.displayGuiScreen(new GuiShulkerBox(minecraft.player.inventory, tileEntityShulkerBox));

        } else
            out.send(LogLevel.ERROR, "You are not holding shulker.");
    }
}
