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

package net.sushiclient.client.handlers;

import net.minecraft.network.play.client.CPacketChatMessage;
import net.sushiclient.client.Sushi;
import net.sushiclient.client.command.Commands;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketSendEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandHandler {

    @EventHandler(timing = EventTiming.PRE)
    public void onChatSend(PacketSendEvent e) {
        if (!(e.getPacket() instanceof CPacketChatMessage)) return;
        String message = ((CPacketChatMessage) e.getPacket()).getMessage();
        if (message.isEmpty()) return;
        if (message.charAt(0) != Sushi.getProfile().getPrefix()) return;
        e.setCancelled(true);
        List<String> list = Arrays.asList(message.substring(1).split("\\s+"));
        List<String> args = list.size() > 1 ? list.subList(1, list.size()) : Collections.emptyList();
        Commands.execute(Sushi.getProfile().getLogger(), list.get(0), args);
    }
}
