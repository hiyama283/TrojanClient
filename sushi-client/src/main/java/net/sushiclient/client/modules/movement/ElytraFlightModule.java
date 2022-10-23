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
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.command.GuiLogger;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.player.PlayerTravelEvent;
import net.sushiclient.client.events.tick.GameTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.EntityUtils;
import net.sushiclient.client.utils.player.ItemSlot;
import net.sushiclient.client.utils.player.MovementUtils;

import java.util.Random;

public class ElytraFlightModule extends BaseModule {
    private final Configuration<IntRange> delay;
    private final Configuration<DoubleRange> boost;
    public ElytraFlightModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        delay = provider.get("delay", "Delay", null, IntRange.class,
                new IntRange(15, 20, 1, 1));
        boost = provider.get("boost", "Boost", null, DoubleRange.class,
                new DoubleRange(1.5, 4, 0.1, 0.1, 1));
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    private byte tick = 0;
    private final int id = new Random().nextInt();
    @EventHandler(timing = EventTiming.PRE)
    public void tick(PlayerTravelEvent e) {
        EntityPlayerSP player = getPlayer();

        if (player.isElytraFlying()) {
            player.motionX = 0;
            player.motionY = 0;
            player.motionZ = 0;
            player.noClip = EntityUtils.isInsideBlock(getPlayer());
            player.fallDistance = 0;
            player.onGround = false;

            if (tick >= delay.getValue().getCurrent()) {
                tick = 0;
            }

            if ((player.getTicksElytraFlying() % delay.getValue().getCurrent()) == 0) {

                MovementInput movementInput = player.movementInput;
                if (movementInput.moveStrafe != 0) {
                    Vec3d moveInputs = MovementUtils.getMoveInputs(player);

                    Vec2f vec2f = MovementUtils.toWorld(
                            new Vec2f((float) moveInputs.x, (float) moveInputs.z), player.rotationYaw);

                    player.motionX = boost.getValue().getCurrent() * vec2f.x;
                    player.motionZ = boost.getValue().getCurrent() * vec2f.y;

                    if (movementInput.sneak) {
                        player.motionY = -1;
                    }
                }
            }
        }

        GuiLogger.send(id, String.valueOf(player.getTicksElytraFlying()), 1000);
        GuiLogger.send(id + 1, player.motionX + ":" + player.motionZ, 1000);
    }

    @Override
    public String getDefaultName() {
        return "TestElytraFlight";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.MOVEMENT;
    }
}
