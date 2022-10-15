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

import net.minecraft.network.play.client.CPacketAnimation;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketSendEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.player.SwingHand;

public class NoSwingModule extends BaseModule {
    private final Configuration<SwingHand> swing;

    public NoSwingModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        swing = provider.get("swing", "Swing arm", null, SwingHand.class, SwingHand.None);
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
    public void onPacketSend(PacketSendEvent e) {
        if (!(e.getPacket() instanceof CPacketAnimation)) return;

        if (swing.getValue() == SwingHand.None) {
            e.setCancelled(true);
            return;
        }

        CPacketAnimation packetAnimation = (CPacketAnimation) e.getPacket();
        if (packetAnimation.getHand() != swing.getValue().getHand()) {
            e.setCancelled(true);
            swing.getValue().swing();
        }
    }

    @Override
    public String getDefaultName() {
        return "NoSwing";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }
}
