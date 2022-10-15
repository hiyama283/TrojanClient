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

package net.sushiclient.client.task.tasks;

import net.minecraft.network.play.server.SPacketConfirmTransaction;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketReceiveEvent;
import net.sushiclient.client.task.TaskAdapter;

public class TransactionWaitTask extends TaskAdapter<Short, Boolean> {

    private final int maxTicks;
    private boolean received;
    private boolean accepted;
    private int counter;

    public TransactionWaitTask(int maxTicks) {
        this.maxTicks = maxTicks;
    }

    public TransactionWaitTask() {
        this(5);
    }

    @Override
    public void start(Short input) throws Exception {
        super.start(input);
        EventHandlers.register(this);
    }

    @Override
    public void tick() throws Exception {
        if (!received && counter++ < maxTicks) return;
        stop(accepted);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketReceive(PacketReceiveEvent e) {
        if (!(e.getPacket() instanceof SPacketConfirmTransaction)) return;
        SPacketConfirmTransaction packet = (SPacketConfirmTransaction) e.getPacket();
        if (packet.getActionNumber() != getInput()) return;
        received = true;
        accepted = packet.wasAccepted();
    }

    @Override
    public void stop(Boolean result) {
        super.stop(result);
        EventHandlers.unregister(this);
    }
}
