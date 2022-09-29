package net.sushiclient.client.gui.hud;

import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.utils.player.InventoryType;
import net.sushiclient.client.utils.player.InventoryUtils;
import net.sushiclient.client.utils.player.ItemSlot;

public abstract class BaseItemCounterComponent extends TextElementComponent implements ItemCounterComponent {
    public BaseItemCounterComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
    }

    @Override
    protected String getText() {
        ItemSlot[] item = InventoryUtils.findItemSlots(targetItem(), null, InventoryType.values());
        int count = 0;
        for (ItemSlot itemSlot : item) {
            count += itemSlot.getItemStack().getCount();
        }
        return targetItem().getDefaultInstance().getDisplayName() + "[" + count + "]";
    }
}
