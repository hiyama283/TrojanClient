package net.sushiclient.client.gui.hud.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.gui.hud.TextElementComponent;
import net.sushiclient.client.utils.player.InventoryUtils;
import net.sushiclient.client.utils.player.ItemSlot;

import java.util.ArrayList;

public class ArmorWarningComponent extends TextElementComponent {
    public ArmorWarningComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
    }

    @Override
    protected String getText() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        ArrayList<ItemStack> items = new ArrayList<>();
        items.add(player.inventory.armorInventory.get(3));
        items.add(player.inventory.armorInventory.get(2));
        items.add(player.inventory.armorInventory.get(1));
        items.add(player.inventory.armorInventory.get(0));

        for (ItemStack item : items) {
            if (!(item.getItem() instanceof ItemArmor)) continue;
            if (item.getMaxDamage() * 0.5 < item.getItemDamage())
                return TextFormatting.BOLD + TextFormatting.RED.toString() + "Armor low!" + TextFormatting.RESET;
        }
        return "Armor isnt low.";
    }

    @Override
    public String getId() {
        return "armor_warning_component";
    }

    @Override
    public String getName() {
        return "ArmorWarningComponent";
    }
}
