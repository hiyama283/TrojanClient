package net.sushiclient.client.gui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.utils.player.InventoryType;
import net.sushiclient.client.utils.player.InventoryUtils;
import net.sushiclient.client.utils.player.ItemSlot;
import net.sushiclient.client.utils.render.GuiUtils;
import net.sushiclient.client.utils.render.TextPreview;

public abstract class BaseItemCounterComponent extends BaseHudElementComponent implements ItemCounterComponent {
    public BaseItemCounterComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
    }

    @Override
    public void onRender() {
        RenderItem renderer = Minecraft.getMinecraft().getRenderItem();
        GlStateManager.enableDepth();
        GlStateManager.enableBlend();
        GlStateManager.pushMatrix();
        GlStateManager.translate(getWindowX(), getWindowY(), 0);
        GlStateManager.scale(0, 0, 0);
        RenderHelper.enableGUIStandardItemLighting();
        renderer.renderItemAndEffectIntoGUI(targetItem().getDefaultInstance(), 0, 0);
        renderer.renderItemOverlays(Minecraft.getMinecraft().fontRenderer, targetItem().getDefaultInstance(), 0, 0);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();

        ItemSlot[] item = InventoryUtils.findItemSlots(targetItem(), null, InventoryType.values());
        int count = 0;
        for (ItemSlot itemSlot : item) {
            count += itemSlot.getItemStack().getCount();
        }

        TextPreview preview = GuiUtils.prepareText(String.valueOf(count), getTextSettings("text").getValue());
        preview.draw(getWindowX() + 1, getWindowY() + 1);
        setWidth(preview.getWidth() + 3);
        setHeight(preview.getHeight() + 4);
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
