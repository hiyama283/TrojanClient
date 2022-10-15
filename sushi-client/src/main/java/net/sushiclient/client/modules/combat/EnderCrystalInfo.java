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

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import java.io.IOException;

public class EnderCrystalInfo {
    private final int entityId;
    private final Vec3d pos;
    private final AxisAlignedBB box;

    public EnderCrystalInfo(int entityId, Vec3d pos, AxisAlignedBB box) {
        this.entityId = entityId;
        this.pos = pos;
        this.box = box;
    }

    public int getEntityId() {
        return entityId;
    }

    public Vec3d getPos() {
        return pos;
    }

    public AxisAlignedBB getBox() {
        return box;
    }

    public CPacketUseEntity newAttackPacket() {
        CPacketUseEntity packet = new CPacketUseEntity();
        PacketBuffer write = new PacketBuffer(Unpooled.buffer());
        write.writeVarInt(getEntityId());
        write.writeEnumValue(CPacketUseEntity.Action.ATTACK);
        try {
            packet.readPacketData(write);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return packet;
    }
}
