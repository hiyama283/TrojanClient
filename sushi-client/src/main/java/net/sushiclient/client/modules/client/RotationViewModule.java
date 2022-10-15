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

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketSendEvent;
import net.sushiclient.client.events.render.EntityRenderEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.player.PositionUtils;

public class RotationViewModule extends BaseModule {

    private volatile float packetYaw;
    private volatile float packetPitch;
    private volatile float lastPacketYaw;
    private volatile float lastPacketPitch;
    private float yaw;
    private float yawHead;
    private float yawOffset;
    private float pitch;
    private float lastYaw;
    private float lastYawHead;
    private float lastYawOffset;
    private float lastPitch;

    public RotationViewModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
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
    public void onPacketSend(PacketSendEvent e) {
        if (!(e.getPacket() instanceof CPacketPlayer)) return;
        CPacketPlayer packet = (CPacketPlayer) e.getPacket();
        lastPacketYaw = packetYaw;
        lastPacketPitch = packetPitch;
        packetYaw = packet.getYaw(getPlayer().rotationYaw);
        packetPitch = packet.getPitch(getPlayer().rotationPitch);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPreRender(EntityRenderEvent e) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (e.getEntityIn() != player) return;
        if (!PositionUtils.getDesyncMode().isRotationDesync()) return;

        yaw = player.rotationYaw;
        yawHead = player.rotationYawHead;
        yawOffset = player.renderYawOffset;
        pitch = player.rotationPitch;

        lastYaw = player.prevRotationYaw;
        lastYawHead = player.prevRotationYawHead;
        lastYawOffset = player.prevRenderYawOffset;
        lastPitch = player.prevRotationPitch;

        player.rotationYaw = packetYaw;
        player.rotationYawHead = packetYaw;
        player.renderYawOffset = packetYaw;
        player.rotationPitch = packetPitch;

        player.prevRotationYaw = lastPacketYaw;
        player.prevRotationYawHead = lastPacketYaw;
        player.prevRenderYawOffset = lastPacketYaw;
        player.prevRotationPitch = lastPacketPitch;
    }

    @EventHandler(timing = EventTiming.POST)
    public void onPostRender(EntityRenderEvent e) {
        if (!PositionUtils.getDesyncMode().isRotationDesync()) return;
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (e.getEntityIn() != player) return;
        player.rotationYaw = yaw;
        player.rotationYawHead = yawHead;
        player.renderYawOffset = yawOffset;
        player.rotationPitch = pitch;

        player.prevRotationYaw = lastYaw;
        player.prevRotationYawHead = lastYawHead;
        player.prevRenderYawOffset = lastYawOffset;
        player.prevRotationPitch = lastPitch;
    }

    @Override
    public String getDefaultName() {
        return "RotationView";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.CLIENT;
    }
}
