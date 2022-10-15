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

package net.sushiclient.client.modules.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketSendEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.player.PlayerUtils;
import net.sushiclient.client.utils.world.BlockUtils;

public class CriticalsModule extends BaseModule {

    private final Configuration<Boolean> motionCheck;

    public CriticalsModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        motionCheck = provider.get("motion_check", "Motion Check", null, Boolean.class, true);
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
        if (BlockUtils.getBlock(PlayerUtils.getPlayerPos(getPlayer()).add(0, 1, 0)) != Blocks.AIR) return;

        if (!(e.getPacket() instanceof CPacketUseEntity)) return;
        CPacketUseEntity packet = (CPacketUseEntity) e.getPacket();
        Entity entity = packet.getEntityFromWorld(getWorld());
        if (!(entity instanceof EntityLivingBase)) return;
        if (packet.getAction() != CPacketUseEntity.Action.ATTACK) return;
        if (motionCheck.getValue() && getPlayer().motionY < -0.1) return;
        sendPacket(new CPacketPlayer.Position(getPlayer().posX, getPlayer().posY + 0.1, getPlayer().posZ, false));
        sendPacket(new CPacketPlayer.Position(getPlayer().posX, getPlayer().posY, getPlayer().posZ, false));
    }

    @Override
    public String getDefaultName() {
        return "Criticals";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.COMBAT;
    }
}
