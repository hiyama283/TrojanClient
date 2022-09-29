package net.sushiclient.client.gui.hud.elements;

import net.minecraft.item.ItemStack;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.gui.hud.TextElementComponent;
import net.sushiclient.client.utils.player.ItemSlot;

public class TrueDurabilityComponent extends TextElementComponent {
    public TrueDurabilityComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
    }

    @Override
    protected String getText() {
        ItemStack item = ItemSlot.current().getItemStack();

        if ((item.getMaxDamage() - item.getItemDamage()) == -1) return item.getDisplayName() + "[Infinite]";
        else if ((item.getMaxDamage() + 1 - item.getItemDamage()) == 1 && item.getMaxDamage() == 1) return item.getDisplayName() + "[Infinite]";
        else return item.getDisplayName() + "[" + (item.getMaxDamage() + 1 - item.getItemDamage()) + "/" + (item.getMaxDamage() + 1) + "]";
    }

    @Override
    public String getId() {
        return "true_durability_component";
    }

    @Override
    public String getName() {
        return "TrueDurabilityComponent";
    }
}
