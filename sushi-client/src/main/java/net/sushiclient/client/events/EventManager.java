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

package net.sushiclient.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketCombatEvent;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.event.entity.EntityEvent;
import net.sushiclient.client.IMC;
import net.sushiclient.client.events.packet.PacketReceiveEvent;
import net.sushiclient.client.events.player.PlayerDeathEvent;
import net.sushiclient.client.events.player.PlayerPopEvent;
import net.sushiclient.client.modules.player.PlayerTrackerModule;

import java.util.List;

public class EventManager implements IMC {
    public EventManager() {
        EventHandlers.register(this);
    }

    private boolean nullCheck() {
        return getPlayer() == null || getWorld() == null;
    }


    @EventHandler(timing = EventTiming.PRE)
    public void prePacketRec(PacketReceiveEvent e) {
        if (nullCheck()) return;

        if (e.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = (SPacketEntityStatus) e.getPacket();
            if (packet.getOpCode() == 35 && packet.getEntity(mc.world) instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) packet.getEntity(mc.world);

                EventHandlers.callEvent(new PlayerPopEvent(EventTiming.PRE, !Minecraft.getMinecraft().isCallingFromMinecraftThread(), player));
                EventHandlers.callEvent(new PlayerPopEvent(EventTiming.POST, !Minecraft.getMinecraft().isCallingFromMinecraftThread(), player));
            } else if (packet.getOpCode() == 3 && packet.getEntity(mc.world) instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) packet.getEntity(mc.world);

                EventHandlers.callEvent(new PlayerDeathEvent(EventTiming.PRE, !Minecraft.getMinecraft().isCallingFromMinecraftThread(), player));
                EventHandlers.callEvent(new PlayerDeathEvent(EventTiming.POST, !Minecraft.getMinecraft().isCallingFromMinecraftThread(), player));
            }
        } else if (e.getPacket() instanceof SPacketCombatEvent) {
            SPacketCombatEvent packet = (SPacketCombatEvent) e.getPacket();
            if (getPlayer().getEntityId() == packet.playerId) {
                EntityPlayerSP player = getPlayer();

                EventHandlers.callEvent(new PlayerDeathEvent(EventTiming.PRE, !Minecraft.getMinecraft().isCallingFromMinecraftThread(), player));
                EventHandlers.callEvent(new PlayerDeathEvent(EventTiming.POST, !Minecraft.getMinecraft().isCallingFromMinecraftThread(), player));
            } else {
                List<EntityPlayer> playerEntities = getWorld().playerEntities;
                playerEntities.remove(getPlayer());
                for (EntityPlayer player : playerEntities) {
                    if (player.getEntityId() == packet.playerId) {
                        EventHandlers.callEvent(new PlayerDeathEvent(EventTiming.PRE, !Minecraft.getMinecraft().isCallingFromMinecraftThread(), player));
                        EventHandlers.callEvent(new PlayerDeathEvent(EventTiming.POST, !Minecraft.getMinecraft().isCallingFromMinecraftThread(), player));
                        return;
                    }
                }
            }
        }
    }
}
