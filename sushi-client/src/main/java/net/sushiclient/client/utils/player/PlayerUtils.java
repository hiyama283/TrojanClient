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

package net.sushiclient.client.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.utils.world.BlockUtils;

public class PlayerUtils {
    public static Minecraft mc = Minecraft.getMinecraft();

    public static double getDistance(Entity e) {
        return mc.player.getDistance(e);
    }

    public static double getDistance(BlockPos pos) {
        return mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ());
    }

    public static double getDistance(Vec3d pos) {
        return mc.player.getDistance(pos.x, pos.y, pos.z);
    }

    public static CPacketPlayer newCPacketPlayer(CPacketPlayer cp, double x, double y, double z,
                                                 float yaw, float pitch, boolean onGround, boolean position, boolean rotation, boolean flying) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (!position) {
            x = cp.getX(player.posX);
            y = cp.getY(player.posY);
            z = cp.getZ(player.posZ);
        }
        if (!rotation) {
            yaw = cp.getYaw(player.rotationYaw);
            pitch = cp.getPitch(player.rotationPitch);
        }
        if (!flying) onGround = cp.isOnGround();
        if ((position && rotation || cp instanceof CPacketPlayer.PositionRotation) ||
                (position && cp instanceof CPacketPlayer.Rotation) ||
                (rotation && cp instanceof CPacketPlayer.Position)) {
            return new CPacketPlayer.PositionRotation(x, y, z, yaw, pitch, onGround);
        } else if (position || cp instanceof CPacketPlayer.Position) {
            return new CPacketPlayer.Position(x, y, z, onGround);
        } else if (rotation || cp instanceof CPacketPlayer.Rotation) {
            return new CPacketPlayer.Rotation(yaw, pitch, onGround);
        } else {
            if (cp.isOnGround() == onGround) return cp;
            else return new CPacketPlayer(onGround);
        }
    }

    public static boolean isPlayerBurrow(EntityPlayer player) {
        BlockPos pos = getEntityPos(player);
        return BlockUtils.getBlock(pos) != Blocks.AIR;
    }

    public static boolean isPlayerBurrow() {
        return isPlayerBurrow(mc.player);
    }

    public static boolean isPlayerInClip(EntityPlayer player) {
        BlockPos pos = getEntityPos(player);
        return BlockUtils.getBlock(pos) != Blocks.AIR || BlockUtils.getBlock(pos.add(0, 1, 0)) != Blocks.AIR;
    }

    public static boolean isPlayerInClip() {
        return isPlayerInClip(mc.player);
    }

    private static BlockPos getEntityPos(Entity e) {
        return new BlockPos(e.posX, e.posY, e.posZ);
    }

    public static BlockPos getPlayerPos(EntityPlayer player) {
        return BlockUtils.toBlockPos(player.getPositionVector());
    }
}
