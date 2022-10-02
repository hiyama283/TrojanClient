package net.sushiclient.client.gui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.utils.player.InventoryType;
import net.sushiclient.client.utils.player.InventoryUtils;
import net.sushiclient.client.utils.player.ItemSlot;
import net.sushiclient.client.utils.render.GuiUtils;
import net.sushiclient.client.utils.render.TextPreview;

public abstract class BaseItemCounterComponent extends BaseHudElementComponent implements ItemCounterComponent {
    private final Configuration<Boolean> textMode;
    public BaseItemCounterComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
        textMode = getConfiguration("text_mode", "Text mode", null, Boolean.class, false);
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
            renderItem(targetItem());
        }
    }

    @Override
    public void onRelocate() {
        ItemSlot[] item = InventoryUtils.findItemSlots(targetItem(), null, InventoryType.values());
        int count = 0;
        for (ItemSlot itemSlot : item) {
            count += itemSlot.getItemStack().getCount();
        }
        TextPreview preview = GuiUtils.prepareText(String.valueOf(count), getTextSettings("text").getValue());
        setWidth(preview.getWidth() + 3);
        setHeight(preview.getHeight() + 4);
    }
}
