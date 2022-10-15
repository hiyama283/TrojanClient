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

package net.sushiclient.client.modules.world;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.sushiclient.client.config.Config;
import net.sushiclient.client.config.ConfigInjector;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketSendEvent;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;

import java.util.ArrayList;

public class PacketRecordModule extends BaseModule implements ModuleSuffix {

    private final ArrayList<Packet<?>> packets = new ArrayList<>();

    @Config(id = "playing", name = "Playing")
    public Boolean playing = false;

    @Config(id = "recordingPacket", name = "Recording Packet")
    public Boolean recordingPacket = false;

    @Config(id = "ignorePosition", name = "Ignore Position")
    public Boolean ignorePosition = true;

    @Config(id = "ignoreLook", name = "Ignore Look")
    public Boolean ignoreLook = false;

    @Config(id = "delay", name = "Delay")
    public IntRange delay = new IntRange(1, 100, 1, 1);

    private int sleep = 0;
    private int index = 0;

    public PacketRecordModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        new ConfigInjector(provider).inject(this);
        provider.get("clear_packets", "Clear Packets", null, Runnable.class, packets::clear, () -> true, true, 0);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
        sleep = 0;
        index = 0;
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        if (!playing) return;
        if (packets.isEmpty()) return;
        if (sleep-- > 0) return;
        Packet<?> packet = packets.get(index++ % packets.size());
        sendPacket(packet);

        if (index % packets.size() == 0) {
            sleep = delay.getCurrent();
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketSend(PacketSendEvent e) {
        if (!recordingPacket) return;

        if (ignorePosition && e.getPacket() instanceof CPacketPlayer.Position) {
            return;
        }

        if (ignoreLook && (e.getPacket() instanceof CPacketPlayer.PositionRotation || e.getPacket() instanceof CPacketPlayer.Rotation)) {
            return;
        }

        packets.add(e.getPacket());
    }

    @Override
    public String getDefaultName() {
        return "PacketRecord";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.WORLD;
    }

    @Override
    public String getSuffix() {
        return Integer.toString(packets.size());
    }
}
