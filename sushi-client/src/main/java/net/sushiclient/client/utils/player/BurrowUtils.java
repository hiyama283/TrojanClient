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

import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.Sushi;
import net.sushiclient.client.command.LogLevel;
import net.sushiclient.client.modules.Module;
import net.sushiclient.client.modules.client.DebugModule;
import net.sushiclient.client.utils.world.BlockUtils;

public class BurrowUtils {
    private static void sendPacket(Packet<?> packet) {
        NetHandlerPlayClient connection = Minecraft.getMinecraft().player.connection;
        if (connection != null) connection.sendPacket(packet);
    }

    private static void chatDebugLog(String message) {
        for (Module module : Sushi.getProfile().getModules().getAll()) {
            if (!(module instanceof DebugModule)) continue;
            if (module.isEnabled()) {
                info(message);
            }
        }
    }

    private static void info(String message) {
        Sushi.getProfile().getLogger().send(LogLevel.INFO, message);
    }

    private static void error(String message, boolean showError) {
        if (showError) Sushi.getProfile().getLogger().send(LogLevel.ERROR, message);
    }

    public static boolean burrow(BurrowLogType logType, boolean noBurrowOnShift, boolean onlyInHole,
                                 boolean packetPlace, Double moveOffset, EnumHand hand, boolean faceObsidian) {
        return burrow(logType.getShowError(), logType.getShowSuccess(), noBurrowOnShift, onlyInHole, packetPlace, moveOffset, hand, faceObsidian);
    }

    public static boolean burrow(boolean showError, boolean showSuccessful, boolean noBurrowOnShift, boolean onlyInHole,
                                 boolean packetPlace, Double moveOffset, EnumHand hand, boolean faceObsidian) {
        Minecraft mc = Minecraft.getMinecraft();
        if (PlayerUtils.isPlayerBurrow() || noBurrowOnShift && mc.player.isSneaking())
            return true;

        if (!PositionUtils.isPlayerInHole() && onlyInHole) {
            error("You are not in hole!", showError);
            return false;
        }

        ItemSlot slot = InventoryUtils.findItemSlot(Item.getItemFromBlock(Blocks.IRON_TRAPDOOR), InventoryType.HOTBAR);
        if (slot == null) {
            error("Cannot find iron trapdoor.", showError);
            return false;
        }

        BlockPos playerPos = BlockUtils.toBlockPos(mc.player.getPositionVector());
        BlockPos trapPos = null;
        BlockPos[] offsets = new BlockPos[]{
                new BlockPos(1, 0, 0),
                new BlockPos(-1, 0, 0),
                new BlockPos(0, 0, 1),
                new BlockPos(0, 0, -1)
        };

        for (BlockPos offset : offsets) {
            BlockPos pos = playerPos.add(offset);
            if (mc.world.getBlockState(pos) instanceof BlockAir) continue;
            trapPos = pos;
        }

        if (trapPos == null) {
            error("No trapdoor space.", showError);
            return false;
        }

        double x = mc.player.posX;
        double y = mc.player.posY;
        double z = mc.player.posZ;
        sendPacket(new CPacketPlayer.Position(x, y + moveOffset, z, mc.player.onGround));

        EnumFacing facing = null;
        for (EnumFacing value : EnumFacing.values()) {
            BlockPos addPos = trapPos.add(value.getDirectionVec());
            if (addPos.equals(playerPos)) {
                facing = value;
            }
        }

        if (facing == null) {
            error("Facing not found.", showError);
            return false;
        }

        BlockPos finalTrapPos = trapPos;
        EnumFacing finalFacing = facing;
        InventoryUtils.silentSwitch(packetPlace, slot.getIndex(), () -> {
            BlockUtils.rightClickBlock(finalTrapPos, finalFacing, new Vec3d(0.5, 0.8, 0.5), true, hand);
        });

        sendPacket(new CPacketPlayer.Position(x, y, z, mc.player.onGround));

        if (faceObsidian) {
            sendPacket(new CPacketPlayer.Position(x, y - 1, z, mc.player.onGround));
            ItemSlot obbSlot = InventoryUtils.findItemSlot(Item.getItemFromBlock(Blocks.OBSIDIAN), InventoryType.HOTBAR);
            if (obbSlot == null) {
                error("Cannot find obsidian!", showError);
                return false;
            }

            EnumFacing findFacing = null;
            for (EnumFacing value : EnumFacing.values()) {
                if (BlockUtils.getBlock(playerPos.add(0, 1, 0).add(value.getDirectionVec())) != Blocks.AIR)
                    findFacing = value;
            }

            if (findFacing == null) findFacing = EnumFacing.DOWN;

            EnumFacing finalFindFacing = findFacing;
            InventoryUtils.silentSwitch(packetPlace, obbSlot.getIndex(), () -> {
                BlockUtils.rightClickBlock(playerPos, finalFindFacing, new Vec3d(0.5, 0.8, 0.5), packetPlace, hand);
            });
        }

        if (showSuccessful) {
            info("Successfully trap placed.");
            chatDebugLog("Position x:" + trapPos.getX() + " y:" + trapPos.getY() + " z:" + trapPos.getZ());
        }
        return true;
    }
}
