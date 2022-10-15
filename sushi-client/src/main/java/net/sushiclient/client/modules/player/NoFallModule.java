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

package net.sushiclient.client.modules.player;

import net.minecraft.network.play.client.CPacketPlayer;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketSendEvent;
import net.sushiclient.client.events.player.PlayerPacketEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.player.PlayerUtils;

public class NoFallModule extends BaseModule implements ModuleSuffix {

    private final Configuration<NoFallMode> noFallMode;
    private final Configuration<DoubleRange> distance;
    private final Configuration<Boolean> pauseOnElytra;
    private boolean isElytraFlying;
    private double fallY;

    public NoFallModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        noFallMode = provider.get("mode", "Mode", null, NoFallMode.class, NoFallMode.PACKET);
        distance = provider.get("distance", "Distance", null, DoubleRange.class, new DoubleRange(3, 20, 1, 0.5, 1),
                () -> noFallMode.getValue() == NoFallMode.PACKET, false, 0);
        pauseOnElytra = provider.get("elytra_pause", "Pause On Elytra", null, Boolean.class, true);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @EventHandler(timing = EventTiming.POST)
    public void onPlayerUpdate(PlayerPacketEvent e) {
        isElytraFlying = getPlayer().isElytraFlying();
    }

    @EventHandler(timing = EventTiming.PRE, priority = 10000)
    public void onPacketSend(PacketSendEvent e) {
        if (!(e.getPacket() instanceof CPacketPlayer)) return;
        CPacketPlayer packet = (CPacketPlayer) e.getPacket();
        double posY = packet.getY(getPlayer().posY);
        if (packet.isOnGround() || posY > fallY) fallY = posY;
        double fallDistance = fallY - posY;

        boolean onGround = packet.isOnGround();
        NoFallMode mode = noFallMode.getValue();
        if (mode == NoFallMode.PACKET) {
            if (fallDistance > distance.getValue().getCurrent() &&
                    (!isElytraFlying || !pauseOnElytra.getValue())) {
                fallY = posY;
                onGround = true;
            }
        } else if (mode == NoFallMode.ON_GROUND) {
            fallY = posY;
            onGround = true;
        } else if (mode == NoFallMode.FLY) {
            onGround = false;
        } else if (mode == NoFallMode.FALL_DISTANCE) {
            fallY = posY;
            getPlayer().fallDistance = 0;
        }
        e.setPacket(PlayerUtils.newCPacketPlayer(packet, 0, 0, 0, 0, 0, onGround, false, false, true));
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    @Override
    public String getDefaultName() {
        return "NoFall";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }

    @Override
    public String getSuffix() {
        return noFallMode.getValue().getName();
    }
}
