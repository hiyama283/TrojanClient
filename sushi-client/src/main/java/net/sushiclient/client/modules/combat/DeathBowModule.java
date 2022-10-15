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

import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketSendEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.player.ItemSlot;

public class DeathBowModule extends BaseModule {

    private final Configuration<IntRange> amount;

    public DeathBowModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        amount = provider.get("amount", "Amount", null, IntRange.class, new IntRange(10, 200, 0, 1));
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
        if (!(e.getPacket() instanceof CPacketPlayerDigging)) return;
        CPacketPlayerDigging packet = (CPacketPlayerDigging) e.getPacket();
        if (packet.getAction() != CPacketPlayerDigging.Action.RELEASE_USE_ITEM) return;
        if (ItemSlot.current().getItemStack().getItem() != Items.BOW) return;
        sendPacket(new CPacketEntityAction(getPlayer(), CPacketEntityAction.Action.START_SPRINTING));
        for (int i = 0; i < amount.getValue().getCurrent(); i++) {
            sendPacket(new CPacketPlayer.Position(getPlayer().posX, getPlayer().posY - 1E-10, getPlayer().posZ, true));
            sendPacket(new CPacketPlayer.Position(getPlayer().posX, getPlayer().posY + 1E-10, getPlayer().posZ, false));
        }
    }

    @Override
    public String getDefaultName() {
        return "DeathBow";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.COMBAT;
    }
}
