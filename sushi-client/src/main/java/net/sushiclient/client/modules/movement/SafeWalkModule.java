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

package net.sushiclient.client.modules.movement;

import net.minecraft.util.math.AxisAlignedBB;
import net.sushiclient.client.config.Config;
import net.sushiclient.client.config.ConfigInjector;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.player.PlayerMoveEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.EntityUtils;

public class SafeWalkModule extends BaseModule {

    @Config(id = "jump", name = "Jump")
    public Boolean jump = true;
    @Config(id = "height", name = "Height")
    public DoubleRange height = new DoubleRange(1.2, 20, 0, 0.2, 1);

    public SafeWalkModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        new ConfigInjector(provider).inject(this);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    public boolean isSafe(AxisAlignedBB box) {
        double size = height.getCurrent();
        return getWorld().collidesWithAnyBlock(box.offset(0, -size / 2, 0).grow(0, size / 2, 0));
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onMove(PlayerMoveEvent e) {
        if (jump && (getPlayer().movementInput.jump || !EntityUtils.isOnGround(getPlayer()))) return;
        if (!isSafe(getPlayer().getEntityBoundingBox())) return;
        if (isSafe(getPlayer().getEntityBoundingBox().offset(e.getX(), 0, e.getZ()))) return;
        getPlayer().motionX = 0;
        getPlayer().motionZ = 0;
        e.setCancelled(true);
    }

    @Override
    public String getDefaultName() {
        return "SafeWalk";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.MOVEMENT;
    }
}
