package net.sushiclient.client.utils.world;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.sushiclient.client.utils.MathUtils;
import net.sushiclient.client.utils.player.InventoryType;
import net.sushiclient.client.utils.player.InventoryUtils;
import net.sushiclient.client.utils.player.ItemSlot;

import java.util.List;

public class BlockUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();

    private static final float EPSILON = 0.00001F;
    private static BlockPos breakingBlockPos;
    private static int breakingTime;

    public static BlockPos getBreakingBlockPos() {
        return breakingBlockPos;
    }

    public static int getBreakingTime() {
        return breakingTime;
    }

    public static void setBreakingBlock(BlockPos pos, int time) {
        breakingBlockPos = pos;
        breakingTime = time;
    }

    public static BlockPos toBlockPos(Vec3d vec) {
        return new BlockPos(vec.x, vec.y, vec.z);
    }

    public static Vec3d toVec3d(Vec3i pos) {
        return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
    }

    public static boolean isAir(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().isAir(state, world, pos);
    }

    public static boolean isColliding(World world, AxisAlignedBB box) {
        return world.collidesWithAnyBlock(box) || !world.checkNoEntityCollision(box);
    }

    public static boolean canInteract(BlockPos pos) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return true;
        Vec3d checkPos = BlockUtils.toVec3d(pos).add(0.5, 0.5, 0.5);
        return !(player.getDistanceSq(checkPos.x, checkPos.y, checkPos.z) < 64);
    }

    public static EnumFacing getPlaceableSide(BlockPos pos) {
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = pos.offset(side);
            if (mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false)) {
                IBlockState blockState = mc.world.getBlockState(neighbour);
                if (!blockState.getMaterial().isReplaceable())
                    return side;
            }
        }
        return null;
    }

    public static void lowArgPlace(BlockPos pos, boolean packet) {
        Block block = mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof net.minecraft.block.BlockAir) && !(block instanceof net.minecraft.block.BlockLiquid))
            return;
        EnumFacing side = getPlaceableSide(pos);
        if (side == null)
            return;
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = (new Vec3d((Vec3i) neighbour)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(opposite.getDirectionVec())).scale(0.5D));
        if (packet) {
            rightClickBlock(neighbour, hitVec, opposite, EnumHand.MAIN_HAND);
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }

    }

    public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumFacing direction, EnumHand placeHand) {
        float f = (float) (vec.x - (double) pos.getX());
        float f1 = (float) (vec.y - (double) pos.getY());
        float f2 = (float) (vec.z - (double) pos.getZ());
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, placeHand, f, f1, f2));
    }
    public static void rightClickBlock(BlockPos pos, EnumFacing facing, Vec3d hVec, boolean packet, EnumHand placeHand) {
        Vec3d hitVec = (new Vec3d(pos)).add(hVec).add((new Vec3d(facing.getDirectionVec())).scale(0.5D));

        if (packet) {
            rightClickBlock(pos, hitVec, facing, placeHand);
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, facing, hitVec, placeHand);
            mc.player.swingArm(placeHand);
        }
    }

    public static Block getBlock(BlockPos pos) {
        return getBlockState(pos).getBlock();
    }

    public static IBlockState getBlockState(BlockPos pos) {
        return mc.world.getBlockState(pos);
    }

    public static boolean canBreak(BlockPos pos) {
        IBlockState blockState = mc.world.getBlockState(pos);
        Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, mc.world, pos) != -1.0f;
    }

    public static boolean canPlace(World world, BlockPlaceInfo face, PlaceOptions... options) {
        BlockPlaceOption option = new BlockPlaceOption(options);
        BlockPos pos = face.getBlockPos();
        EnumFacing facing = face.getBlockFace() == null ? null : face.getBlockFace().getFacing();
        AxisAlignedBB box = world.getBlockState(pos).getBoundingBox(world, pos).offset(pos);
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player != null && BlockUtils.toVec3d(pos).add(0.5, 0.5, 0.5).squareDistanceTo(player.getPositionVector()) > 36)
            return false;
        if (world.collidesWithAnyBlock(box) && !option.isBlockCollisionIgnored() ||
                !world.checkNoEntityCollision(box) && !option.isEntityCollisionIgnored()) return false;
        if (facing == null) return world.getBlockState(pos).getBlock().canPlaceBlockAt(world, pos);
        else if (isAir(world, pos.offset(facing.getOpposite())) && !option.isAirPlaceIgnored()) return false;
        else return world.getBlockState(pos).getBlock().canPlaceBlockOnSide(world, pos, facing.getOpposite());
    }

    public static EnumFacing getFacing(BlockPos pos, BlockPos centerPos) {
        for (EnumFacing value : EnumFacing.values()) {
            if (pos.add(value.getDirectionVec()).equals(centerPos))
                return value;
        }
        return null;
    }

    public static void place(BlockPlaceInfo info, boolean packet) {
        Minecraft minecraft = Minecraft.getMinecraft();
        PlayerControllerMP controller = minecraft.playerController;
        EntityPlayerSP player = minecraft.player;
        WorldClient world = minecraft.world;
        if (controller == null || player == null || world == null) return;
        BlockPos pos = info.getBlockPos();
        Vec3d vec = BlockUtils.toVec3d(pos);
        BlockFace face = info.getBlockFace();
        NetHandlerPlayClient connection = minecraft.getConnection();
        if (!packet || connection == null) {
            controller.processRightClickBlock(player, world, pos.offset(face.getFacing().getOpposite()), face.getFacing(),
                    face.getPos().add(vec).add(BlockUtils.toVec3d(info.getBlockFace().getFacing().getOpposite().getDirectionVec())), EnumHand.MAIN_HAND);
        } else {
            BlockPos placePos = pos.offset(face.getFacing().getOpposite());
            Vec3d placeVec = face.getPos().add(vec);
            float f = (float) (placeVec.x - (double) placePos.getX());
            float f1 = (float) (placeVec.y - (double) placePos.getY());
            float f2 = (float) (placeVec.z - (double) placePos.getZ());
            connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(placePos, face.getFacing(), EnumHand.MAIN_HAND, f, f1, f2));
        }
    }

    public static float[] getLookVec(Vec3d loc) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return null;
        Vec3d direction = loc.subtract(new Vec3d(player.posX, player.posY + player.eyeHeight, player.posZ)).normalize();
        if (MathUtils.absMinus(direction.y, 1) < EPSILON) {
            // workaround for Math#asin returning Double.NaN
            direction = new Vec3d(direction.x, Math.signum(direction.y) * (1 - EPSILON), direction.z);
        }
        float yaw = (float) (Math.atan2(direction.z, direction.x) * 180 / Math.PI) - 90;
        float pitch = (float) -(Math.asin(direction.y) * 180 / Math.PI);
        return new float[]{yaw, pitch};
    }

    public static float[] getLookVec(BlockPlaceInfo info) {
        Vec3d pos = BlockUtils.toVec3d(info.getBlockPos());
        return getLookVec(info.getBlockFace().getPos()
                .add(pos)
                .add(BlockUtils.toVec3d(info.getBlockFace().getFacing().getOpposite().getDirectionVec())));
    }

    public static BlockPlaceInfo findBlockPlaceInfo(World world, BlockPos input, PlaceOptions... options) {
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPlaceInfo info = new BlockFace(input.offset(facing), facing.getOpposite()).toBlockPlaceInfo(world);
            if (!canPlace(world, info, options)) continue;
            return info;
        }
        return null;
    }

    public static void checkGhostBlock(BlockPos... arr) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        NetHandlerPlayClient connection = Minecraft.getMinecraft().getConnection();
        if (player == null || connection == null) return;
        boolean swap = false;
        ItemSlot current = ItemSlot.current();
        if (current.getItemStack().getItem() instanceof ItemBlock) {
            swap = true;
            for (ItemSlot itemSlot : InventoryType.HOTBAR) {
                if (itemSlot.getItemStack().getItem() instanceof ItemBlock) continue;
                InventoryUtils.moveHotbar(itemSlot.getIndex());
                break;
            }
        }
        for (BlockPos pos : arr) {
            connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.5F, 0, 0.5F));
        }
        if (swap) InventoryUtils.moveHotbar(current.getIndex());
    }

    public static double getMaxHeight(AxisAlignedBB box) {
        WorldClient world = Minecraft.getMinecraft().world;
        List<AxisAlignedBB> collisions = world.getCollisionBoxes(null, box.offset(0, -1, 0));
        boolean updated = false;
        double maxY = 0;
        for (AxisAlignedBB collision : collisions) {
            if (collision.maxY > maxY) {
                updated = true;
                maxY = collision.maxY;
            }
        }
        return updated ? maxY : Double.NaN;
    }
}
