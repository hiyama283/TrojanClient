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

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.Sushi;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.player.PlayerTravelEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.EntityUtils;
import net.sushiclient.client.utils.PhaseWalkTimer;
import net.sushiclient.client.utils.TpsUtils;
import net.sushiclient.client.utils.player.MovementUtils;
import net.sushiclient.client.utils.player.PlayerUtils;
import net.sushiclient.client.utils.player.PositionMask;
import net.sushiclient.client.utils.player.PositionUtils;
import net.sushiclient.client.utils.world.BlockUtils;

public class PhaseWalkRewriteModule extends BaseModule implements ModuleSuffix {
    private final Configuration<Boolean> tpsSync;
    private final Configuration<Boolean> capAt20;
    private final Configuration<DoubleRange> multiplier;
    private final Configuration<Boolean> jumpLimit;
    private final Configuration<Boolean> shiftLimit;
    private final Configuration<Boolean> voidSafe;
    private final Configuration<IntRange> voidY;
    private boolean suffix;
    private boolean sneakedFlag;
    private boolean jumpedFlag;
    private final PhaseWalkTimer timer = new PhaseWalkTimer();
    private boolean firstStart = true;
    private long startTime = 0;
    public PhaseWalkRewriteModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        tpsSync = provider.get("tps_sync", "Tps sync", null, Boolean.class, false);
        capAt20 = provider.get("cap_at_20", "Cap at 20", null, Boolean.class, true,
                tpsSync::getValue, false, 0);
        multiplier = provider.get("multiplier", "Multiplier", null, DoubleRange.class, new DoubleRange(0.5, 5, 0.1, 0.1, 1));
        jumpLimit = provider.get("jump_limit", "Jump limit", null, Boolean.class, true);
        shiftLimit = provider.get("shift_limit", "Shift limit", null, Boolean.class, true);
        voidSafe = provider.get("void_safe", "Void safe", null, Boolean.class, true);
        voidY = provider.get("void_y", "Void y", null, IntRange.class, new IntRange(60, 65, -1, 1),
                voidSafe::getValue, false, 0);
    }

    private boolean paused = false;
    private void speedModulePauseManager(boolean b) {
        if (paused && b)
            return;

        paused = b;
        for (Module module : Sushi.getProfile().getModules().getAll()) {
            if (!(module instanceof SpeedModule)) continue;
            module.setPaused(b);
        }
    }

    private void checkIsPause() {
        if (paused)
            speedModulePauseManager(false);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPlayerTravel(PlayerTravelEvent e) {
        if (EntityUtils.isInsideBlock(getPlayer()) && PlayerUtils.isPlayerInClip()) {
            if (firstStart) {
                timer.start();
                startTime = System.currentTimeMillis();
                firstStart = false;
            }
            speedModulePauseManager(true);

            EntityPlayerSP player = getPlayer();
            if (player.isElytraFlying()) return;
            Vec3d inputs = MovementUtils.getMoveInputs(player).normalize();

            // chatLog("Input X:" + inputs.x + " Y:" + inputs.y + " Z:" + inputs.z);
            suffix = true;

            player.motionX = 0;
            player.motionY = 0;
            player.motionZ = 0;

            player.noClip = EntityUtils.isInsideBlock(getPlayer());
            player.fallDistance = 0;

            double x;
            double z;

            x = inputs.x;
            z = inputs.z;

            x *= multiplier.getValue().getCurrent();
            z *= multiplier.getValue().getCurrent();
            // chatLog("Multiply X:" + x + " Z:" + z);

            if (tpsSync.getValue()) {
                double tps = TpsUtils.getTps();
                if (capAt20.getValue()) tps = Math.max(20, tps);
                tps /= 20;

                x *= tps;
                z *= tps;
                // chatLog("Tps sync X:" + x + " Z:" + z);
            }

            Vec2f vec = MovementUtils.toWorld(new Vec2f((float) x, (float) z), player.rotationYaw);
            player.motionX = vec.x;
            player.motionY = 0;
            player.motionZ = vec.y;

            if (getPlayer().movementInput.sneak) {
                int underY = (int) player.posY - 1;
                if (shiftLimit.getValue()) {
                    if (!sneakedFlag) {
                        if (voidSafe.getValue() && underY <= voidY.getValue().getCurrent()) return;
                        PositionUtils.move(player.posX, underY, player.posZ, 0, 0, false, PositionMask.POSITION);
                    }
                } else
                    PositionUtils.move(player.posX, underY, player.posZ, 0, 0, false, PositionMask.POSITION);
                sneakedFlag = true;
            } else if(sneakedFlag) {
                sneakedFlag = false;
            }
            if (getPlayer().movementInput.jump) {
                int upY = (int) (player.posY + 1);
                if (jumpLimit.getValue()) {
                    if (!jumpedFlag)
                        PositionUtils.move(player.posX, upY, player.posZ, 0, 0, false, PositionMask.POSITION);
                } else
                    PositionUtils.move(player.posX, upY, player.posZ, 0, 0, false, PositionMask.POSITION);
                jumpedFlag = true;
            } else if(jumpedFlag) {
                jumpedFlag = false;
            }

        } else {
            suffix = false;
            firstStart = true;
            speedModulePauseManager(false);

            if (startTime != 0) {
                chatDebugLog(String.valueOf(System.currentTimeMillis() - startTime));
                startTime = 0;
            }
        }
    }

    @Override
    public String getSuffix() {
        if (suffix && BlockUtils.getBlock(getPlayer().getPosition()) == Blocks.ANVIL)
            return "Enabled [ANVIL]";
        else
            return suffix ? "Enabled [" + timer.toString() + "]" : "Disabled";
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
        checkIsPause();
    }

    @Override
    public String getDefaultName() {
        return "PhaseWalkRewrite";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.MOVEMENT;
    }
}
