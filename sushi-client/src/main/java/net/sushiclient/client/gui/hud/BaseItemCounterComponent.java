package net.sushiclient.client.gui.hud;

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.utils.player.InventoryType;
import net.sushiclient.client.utils.player.InventoryUtils;
import net.sushiclient.client.utils.player.ItemSlot;
import net.sushiclient.client.utils.render.GuiUtils;
import net.sushiclient.client.utils.render.TextPreview;

public abstract class BaseItemCounterComponent extends BaseHudElementComponent implements ItemCounterComponent {
    private final Configuration<Boolean> textMode;
    private final Configuration<DoubleRange> scale;
    private final double MARGIN = 10;
    public BaseItemCounterComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
        textMode = getConfiguration("text_mode", "Text mode", null, Boolean.class, false);
        scale = getConfiguration("scale", "Scale", null, DoubleRange.class, new DoubleRange(1, 3, 0.7, 0.1, 1));
    }

    @Override
    public void onRender() {
        if (textMode.getValue()) {
            ItemSlot[] item = InventoryUtils.findItemSlots(targetItem(), null, InventoryType.values());
            int count = 0;
            for (ItemSlot itemSlot : item) {
                count += itemSlot.getItemStack().getCount();
            }

            TextPreview preview = GuiUtils.prepareText(targetItem().getDefaultInstance().getDisplayName() + "[" +
                    count + "]", getTextSettings("text").getValue());
            preview.draw(getWindowX() + 1, getWindowY() + 1);
            setWidth(preview.getWidth() + 3);
            setHeight(preview.getHeight() + 4);
        } else {
            renderItem(targetItem(), true);
        }
    }

    @Override
    public void onRelocate() {
        double base = scale.getValue().getCurrent() + MARGIN;

        ItemSlot[] item = InventoryUtils.findItemSlots(targetItem(), null, InventoryType.values());
        int count = 0;
        for (ItemSlot itemSlot : item) {
            count += itemSlot.getItemStack().getCount();
        }

        TextPreview preview;
        if (textMode.getValue()) {
            preview = GuiUtils.prepareText(targetItem().getDefaultInstance().getDisplayName() + "[" +
                    count + "]", getTextSettings("text").getValue());
        } else {
            preview = GuiUtils.prepareText(String.valueOf(count), getTextSettings("text").getValue());
        }

        setWidth(base + preview.getWidth());
        setHeight(base + preview.getHeight());
    }
}
