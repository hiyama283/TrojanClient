package net.sushiclient.client.gui.hud.elements.counter;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.gui.hud.BaseItemCounterComponent;

public class ObsidianCountComponent extends BaseItemCounterComponent {
    public ObsidianCountComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
    }

    @Override
    public Item targetItem() {
        return Item.getItemFromBlock(Blocks.OBSIDIAN);
    }

    @Override
    public String getId() {
        return "obsidian_count_component";
    }

    @Override
    public String getName() {
        return "ObsidianCountComponent";
    }
}
