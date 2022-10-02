package net.sushiclient.client.gui.hud.elements.counter;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.gui.hud.BaseItemCounterComponent;

public class CrystalCountComponent extends BaseItemCounterComponent {
    public CrystalCountComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
    }

    @Override
    public Item targetItem() {
        return Items.END_CRYSTAL;
    }

    @Override
    public String getId() {
        return "crystal_count_component";
    }

    @Override
    public String getName() {
        return "CrystalCountComponent";
    }
}
