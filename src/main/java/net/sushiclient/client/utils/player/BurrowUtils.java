package net.sushiclient.client.utils.player;

import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.Sushi;
import net.sushiclient.client.command.LogLevel;
import net.sushiclient.client.utils.world.BlockUtils;

public class BurrowUtils {
    private static void sendPacket(Packet<?> packet) {
        NetHandlerPlayClient connection = Minecraft.getMinecraft().player.connection;
        if (connection != null) connection.sendPacket(packet);
    }

    private static void info(String message) {
        Sushi.getProfile().getLogger().send(LogLevel.INFO, message);
    }

    private static void error(String message) {
        Sushi.getProfile().getLogger().send(LogLevel.ERROR, message);
    }

    public static void burrow(BurrowLogType logType, boolean noBurrowOnShift, boolean onlyInHole,
                              boolean packetPlace, Double moveOffset, EnumHand hand) {
        burrow(logType.getShowError(), logType.getShowSuccess(), noBurrowOnShift, onlyInHole, packetPlace, moveOffset, hand);
    }

    public static void burrow(boolean showError, boolean showSuccessful, boolean noBurrowOnShift, boolean onlyInHole,
                              boolean packetPlace, Double moveOffset, EnumHand hand) {
        Minecraft mc = Minecraft.getMinecraft();
        if (PlayerUtils.isPlayerBurrow() || noBurrowOnShift && mc.player.isSneaking())
            return;

        if (!PositionUtils.isPlayerInHole() && onlyInHole) {
            if (showError) error("You are not in hole!");
            return;
        }

        ItemSlot slot = InventoryUtils.findItemSlot(Item.getItemFromBlock(Blocks.IRON_TRAPDOOR), InventoryType.HOTBAR);
        if (slot == null) {
            if (showError) error("Cannot find iron trapdoor.");
            return;
        }

        BlockPos playerPos = BlockUtils.toBlockPos(mc.player.getPositionVector());
        BlockPos trapPos = null;
        BlockPos[] offsets = new BlockPos[]{
                new BlockPos(1 , 0 , 0) ,
                new BlockPos(-1 , 0 , 0) ,
                new BlockPos(0 , 0 ,1) ,
                new BlockPos(0 , 0 , -1)
        };

        for (BlockPos offset : offsets) {
            BlockPos pos = playerPos.add(offset);
            if(mc.world.getBlockState(pos) instanceof BlockAir) continue;
            trapPos = pos;
        }

        if (trapPos == null) {
            if (showError) info("No trapdoor space.");
            return;
        }

        double x = mc.player.posX;
        double y = mc.player.posY;
        double z = mc.player.posZ;
        sendPacket(new CPacketPlayer.Position(x , y + moveOffset , z , mc.player.onGround));

        EnumFacing facing = null;
        for (EnumFacing value : EnumFacing.values()) {
            BlockPos addPos = trapPos.add(value.getDirectionVec());
            if (addPos.equals(playerPos)) {
                facing = value;
            }
        }

        if (facing == null) {
            if (showError) info("Facing not found.");
            return;
        }

        BlockPos finalTrapPos = trapPos;
        EnumFacing finalFacing = facing;
        InventoryUtils.silentSwitch(packetPlace, slot.getIndex(), () -> {
            BlockUtils.rightClickBlock(finalTrapPos, finalFacing, new Vec3d(0.5 , 0.8 , 0.5), packetPlace, hand);
        });

        sendPacket(new CPacketPlayer.Position(x, y, z, mc.player.onGround));

        if (showSuccessful) {
            info("Successfully trap placed.");
            info("Position x:" + trapPos.getX() + " y:" + trapPos.getY() + " z:" + trapPos.getZ());
        }
    }
}
