package net.sushiclient.client.gui.hud.elements.counter;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.gui.hud.BaseItemCounterComponent;

public class TotemCountComponent extends BaseItemCounterComponent {
    public TotemCountComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
    }

    @Override
    public Item targetItem() {
        return Items.TOTEM_OF_UNDYING;
    }

    @Override
    public String getId() {
        return "totem_count_component";
    }

    @Override
    public String getName() {
        return "TotemCountComponent";
    }
}
