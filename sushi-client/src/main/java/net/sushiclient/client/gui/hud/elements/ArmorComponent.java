/*
 * Contact github.com/hiyama283
 * Project "sushi-client"
 *
 * Copyright 2022 hiyama283
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sushiclient.client.gui.hud.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.gui.hud.BaseHudElementComponent;
import net.sushiclient.client.utils.render.GuiUtils;
import net.sushiclient.client.utils.render.TextPreview;

import java.util.ArrayList;

public class ArmorComponent extends BaseHudElementComponent {

    private static final int MARGIN = 16;

    private final Configuration<Boolean> vertical;
    private final Configuration<DoubleRange> scale;

    public ArmorComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
        vertical = getConfiguration("vertical", "vertical", null, Boolean.class, false);
        scale = getConfiguration("scale", "Scale", null, DoubleRange.class, new DoubleRange(1, 3, 0.7, 0.1, 1));
    }

    @Override
    public void onRender() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;
        ArrayList<ItemStack> items = new ArrayList<>();
        items.add(player.inventory.armorInventory.get(3));
        items.add(player.inventory.armorInventory.get(2));
        items.add(player.inventory.armorInventory.get(1));
        items.add(player.inventory.armorInventory.get(0));

        RenderItem renderer = Minecraft.getMinecraft().getRenderItem();
        double s = scale.getValue().getCurrent();

        GlStateManager.enableDepth();
        GlStateManager.enableBlend();
        GlStateManager.pushMatrix();
        GlStateManager.translate(getWindowX(), getWindowY(), 0);
        GlStateManager.scale(s, s, 0);
        double x = 0;
        double y = 0;
        for (ItemStack item : items) {
            RenderHelper.enableGUIStandardItemLighting();
            renderer.renderItemAndEffectIntoGUI(item, (int) x, (int) y);
            renderer.renderItemOverlays(Minecraft.getMinecraft().fontRenderer, item, (int) x, (int) y);
            RenderHelper.disableStandardItemLighting();

            if (vertical.getValue()) y += MARGIN;
            else x += MARGIN;
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void onRelocate() {
        double base = scale.getValue().getCurrent() * MARGIN;
        if (vertical.getValue()) {
            setWidth(base);
            setHeight(base * 4);
        } else {
            setWidth(base * 4);
            setHeight(base);
        }
    }
}