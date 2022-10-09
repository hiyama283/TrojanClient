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
