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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.sushiclient.client.command.GuiLogger;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.EspColor;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.events.world.WorldRenderEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.player.PlayerUtils;
import net.sushiclient.client.utils.player.PositionUtils;
import net.sushiclient.client.utils.render.RenderUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BurrowESPModule extends BaseModule implements ModuleSuffix {
    private final Configuration<EspColor> color;
    private final List<BlockPos> target = new ArrayList<>();

    public BurrowESPModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        color = provider.get("color", "Color", null, EspColor.class,
                new EspColor(new Color(255, 0, 0), false, true));
    }

    @EventHandler(timing = EventTiming.POST)
    public void onWorldRender(WorldRenderEvent e) {
        for (BlockPos blockPos : new HashSet<>(target)) {
            GlStateManager.disableDepth();
            RenderUtils.drawFilled(getBox(getWorld(), blockPos), color.getValue().getColor());
            GlStateManager.enableDepth();
        }
    }

    @EventHandler(timing = EventTiming.POST)
    public void onClientTick(ClientTickEvent e) {
        target.clear();
        for (EntityPlayer nearbyPlayer : getWorld().playerEntities) {
            BlockPos blockPos = PositionUtils.toBlockPos(nearbyPlayer.getPositionVector());
            GuiLogger.send(blockPos.getX() + ":" + blockPos.getY() + ":" + blockPos.getZ());
            if (PlayerUtils.isPlayerBurrow(nearbyPlayer)) {
                synchronized (target) {
                    target.add(blockPos);
                }
            }
        }
    }

    private AxisAlignedBB getBox(World world, BlockPos origin) {
        return world.getBlockState(origin).getBoundingBox(world, origin).offset(origin);
    }

    @Override
    public String getDefaultName() {
        return "BurrowESP";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.RENDER;
    }

    @Override
    public String getSuffix() {
        return String.valueOf(target.size());
    }
}
