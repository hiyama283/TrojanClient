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

package net.sushiclient.client.modules.render;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.FakeConfiguration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.config.data.EspColor;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.world.BlockHighlightEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.render.RenderUtils;

import java.awt.*;

public class BlockHighlightModule extends BaseModule {

    private final Configuration<RenderMode> renderMode;
    private final Configuration<Boolean> outline;
    private final Configuration<EspColor> outlineColor;
    private final Configuration<DoubleRange> outlineWidth;
    private final Configuration<Boolean> fill;
    private final Configuration<EspColor> fillColor;

    public BlockHighlightModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        EspColor defaultColor = new EspColor(new Color(255, 0, 0), false, true);
//        renderMode = provider.get("render_mode", "Render Mode", null, RenderMode.class, RenderMode.SURFACE);
        renderMode = new FakeConfiguration<>("render_mode", "Render Mode", null, RenderMode.class, RenderMode.FULL);
        outline = provider.get("outline", "Outline", null, Boolean.class, true);
        outlineColor = provider.get("outline_color", "Outline Color", null, EspColor.class, defaultColor, outline::getValue, false, 0);
        outlineWidth = provider.get("outline_width", "Outline Width", null, DoubleRange.class, new DoubleRange(1.0, 10, 0.1, 0.1, 1), outline::getValue, false, 0);
        fill = provider.get("fill", "Fill", null, Boolean.class, true);
        fillColor = provider.get("fill_color", "Fill Color", null, EspColor.class, defaultColor, fill::getValue, false, 0);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onBlockHighlight(BlockHighlightEvent event) {
        event.setCancelled(true);
        RayTraceResult result = event.getTarget();
        if (result.typeOfHit != RayTraceResult.Type.BLOCK) return;
        BlockPos pos = result.getBlockPos();
        IBlockState state = getWorld().getBlockState(pos);
        if (state.getMaterial() == Material.AIR) return;
        AxisAlignedBB box = state.getBoundingBox(getWorld(), pos).offset(pos).grow(0.002);
        if (renderMode.getValue() == RenderMode.FULL) GlStateManager.disableDepth();
        if (outline.getValue()) {
            RenderUtils.drawOutline(box, outlineColor.getValue().getCurrentColor(), outlineWidth.getValue().getCurrent());
        }
        if (fill.getValue()) {
            RenderUtils.drawFilled(box, fillColor.getValue().getCurrentColor());
        }
        GlStateManager.enableDepth();
    }

    @Override
    public String getDefaultName() {
        return "BlockHighlight";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.RENDER;
    }
}
