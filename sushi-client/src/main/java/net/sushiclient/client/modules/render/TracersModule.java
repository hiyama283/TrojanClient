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

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.world.WorldRenderEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.render.RenderUtils;

import java.awt.*;

public class TracersModule extends BaseModule {

    private static final float[] RED_HSB = Color.RGBtoHSB(255, 0, 0, null);
    private static final float[] GREEN_HSB = Color.RGBtoHSB(0, 255, 0, null);
    private final Configuration<Boolean> relative;

    public TracersModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        relative = provider.get("relative", "Relative", null, Boolean.class, true);
    }

    private Color getColor(double distance) {
        double gradient = MathHelper.clamp(0, distance / 54, 1);
        double hue = MathHelper.clampedLerp(RED_HSB[0], GREEN_HSB[0], gradient);
        double saturation = MathHelper.clampedLerp(RED_HSB[1], GREEN_HSB[1], gradient);
        double brightness = MathHelper.clampedLerp(RED_HSB[2], GREEN_HSB[2], gradient);
        return Color.getHSBColor((float) hue, (float) saturation, (float) brightness);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    @EventHandler(timing = EventTiming.POST)
    public void onWorldRender(WorldRenderEvent e) {
        for (Entity entity : getWorld().loadedEntityList) {
            if (!(entity instanceof EntityPlayer)) continue;
            if (entity.getName().equals(getPlayer().getName())) continue;
            GlStateManager.disableDepth();
            Color color = getColor(MathHelper.sqrt(entity.getDistanceSq(getPlayer())));
            Vec3d cameraCenter = relative.getValue() ?
                    RenderUtils.getViewerPos().add(RenderUtils.getCameraPos()).subtract(RenderUtils.getInterpolatedPos()) : RenderUtils.getCameraPos();
            RenderUtils.drawLine(cameraCenter, RenderUtils.getInterpolatedPos(entity), color, 1);
            GlStateManager.enableDepth();
        }
    }

    @Override
    public String getDefaultName() {
        return "Tracers";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.RENDER;
    }
}
