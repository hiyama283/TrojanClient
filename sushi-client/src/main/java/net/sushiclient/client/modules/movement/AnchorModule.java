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

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.config.Config;
import net.sushiclient.client.config.ConfigInjector;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.player.PlayerTravelEvent;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.player.PositionMask;
import net.sushiclient.client.utils.player.PositionUtils;
import net.sushiclient.client.utils.render.hole.HoleInfo;
import net.sushiclient.client.utils.render.hole.HoleType;
import net.sushiclient.client.utils.render.hole.HoleUtils;
import net.sushiclient.client.utils.world.BlockUtils;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class AnchorModule extends BaseModule {

    @Config(id = "instant", name = "Instant")
    public Boolean instant = true;

    @Config(id = "step_mode", name = "Step Mode", when = "instant")
    public StepMode stepMode = StepMode.NCP;

    @Config(id = "range", name = "Range")
    public IntRange range = new IntRange(3, 8, 1, 1);

    @Config(id = "pitch_trigger", name = "Pitch Trigger")
    public Boolean pitchTrigger = true;

    @Config(id = "pitch", name = "Pitch", when = "pitch_trigger")
    public IntRange pitch = new IntRange(80, 90, 0, 1);

    @Config(id = "pause_in_hole", name = "Pause In Hole")
    public Boolean pauseInHole = true;

    @Config(id = "pause_ticks", name = "Pause Ticks", when = "pause_in_hole")
    public IntRange pauseTicks = new IntRange(0, 100, 0, 1);

    @Config(id = "disable_in_hole", name = "Disable In Hole")
    public Boolean disableInHole = false;

    @Config(id = "hole_selector", name = "Allowed Hole")
    public HoleSelector selector = HoleSelector.BOTH;

    private int pausing;

    public AnchorModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
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

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        pausing--;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPlayerTravel(PlayerTravelEvent e) {
        boolean isInHole = getHole(0, selector.getAllowedTypes()) != null;
        boolean validPitch = !pitchTrigger || getPlayer().rotationPitch > pitch.getCurrent();
        if (isInHole) {
            if (disableInHole) {
                setEnabled(false);
                return;
            }
            if (pauseInHole) {
                pausing = pauseTicks.getCurrent();
                return;
            }
        }

        if (pausing > 0) return;
        if (!validPitch) return;
        HoleInfo hole = getHole(range.getCurrent(), selector.getAllowedTypes());
        if (hole == null) return;
        double posY = instant ? hole.getBlockPos()[0].getY() : getPlayer().posY;
        getPlayer().motionX = 0;
        getPlayer().motionZ = 0;
        BlockPos floorPos = BlockUtils.toBlockPos(getPlayer().getPositionVector());
        PositionUtils.move(floorPos.getX() + 0.5, getPlayer().posY, floorPos.getZ() + 0.5,
                0, 0, false, PositionMask.POSITION);
        if (instant && getPlayer().motionY < 0) {
            Vec3d d = new Vec3d(floorPos.getX() + 0.5, posY, floorPos.getZ() + 0.5).subtract(getPlayer().getPositionVector());
            stepMode.reverse(d.x, d.y, d.z, false);
        }
    }

    private boolean canAccess(HoleInfo info) {
        BlockPos holePos = info.getBlockPos()[0];
        for (int y = holePos.getY(); y <= getPlayer().posY; y++) {
            BlockPos pos = new BlockPos(holePos.getX(), y, holePos.getZ());
            if (!BlockUtils.isAir(getWorld(), pos)) return false;
        }
        return true;
    }

    private HoleInfo getHole(int range, HoleType... allowed) {
        BlockPos from = BlockUtils.toBlockPos(getPlayer().getPositionVector().subtract(0, range, 0));
        BlockPos to = BlockUtils.toBlockPos(getPlayer().getPositionVector());
        AtomicReference<HoleInfo> result = new AtomicReference<>();
        HoleUtils.findHoles(getWorld(), from, to, false, info -> {
            if (!canAccess(info)) return;
            if (Arrays.asList(allowed).contains(info.getHoleType())) {
                result.set(info);
            }
        });
        return result.get();
    }

    @Override
    public String getDefaultName() {
        return "Anchor";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.MOVEMENT;
    }

}
