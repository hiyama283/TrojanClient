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

package net.sushiclient.client.modules.client;

import net.minecraft.network.Packet;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketSendEvent;
import net.sushiclient.client.modules.*;

import java.io.IOException;

public class PacketLogModule extends BaseModule {
    private final Configuration<Boolean> packetSend;
    private final Configuration<Boolean> pre_send;
    private final Configuration<Boolean> post_send;
    private final Configuration<Boolean> packetReceive;
    private final Configuration<Boolean> pre_receive;
    private final Configuration<Boolean> post_receive;

    public PacketLogModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        packetSend = provider.get("packet_send", "Packet send", null, Boolean.class, true);
        pre_send = provider.get("pre_send", "Pre", null, Boolean.class, true,
                packetSend::getValue, false, 0);
        post_send = provider.get("post_send", "Post", null, Boolean.class, true,
                packetSend::getValue, false, 0);
        packetReceive = provider.get("packet_receive", "Packet receive", null, Boolean.class, true);
        pre_receive = provider.get("pre_receive", "Pre", null, Boolean.class, true,
                packetReceive::getValue, false, 0);
        post_receive = provider.get("post_receive", "Post", null, Boolean.class, true,
                packetReceive::getValue, false, 0);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketSendPRE(PacketSendEvent e) throws IOException {
        Packet<?> packet = e.getPacket();

        chatLog("[PRE SEND] Cancelled=" + e.isCancelled() + " ");
    }

    @EventHandler(timing = EventTiming.POST)
    public void onPacketSendPOST(PacketSendEvent e) {

    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    @Override
    public String getDefaultName() {
        return "PacketLog";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.CLIENT;
    }
}
