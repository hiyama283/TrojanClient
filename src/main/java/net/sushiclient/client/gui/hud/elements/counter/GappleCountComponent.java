package net.sushiclient.client.gui.hud.elements.counter;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.gui.hud.BaseItemCounterComponent;
import net.sushiclient.client.gui.hud.BaseItemTextCounterComponent;

public class GappleCountComponent extends BaseItemTextCounterComponent {
    public GappleCountComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
    }

    @Override
    public Item targetItem() {
        return Items.GOLDEN_APPLE;
    }

    @Override
    public String getId() {
        return "gapple_count_component";
    }

    @Override
    public String getName() {
        return "GappleCountComponent";
    }
}
