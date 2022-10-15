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

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketReceiveEvent;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.events.tick.GameTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.EntityInfo;
import net.sushiclient.client.utils.EntityUtils;
import net.sushiclient.client.utils.UpdateTimer;
import net.sushiclient.client.utils.combat.DamageUtils;
import net.sushiclient.client.utils.player.InventoryType;
import net.sushiclient.client.utils.player.InventoryUtils;
import net.sushiclient.client.utils.player.ItemSlot;
import net.sushiclient.client.utils.world.BlockFace;
import net.sushiclient.client.utils.world.BlockPlaceInfo;
import net.sushiclient.client.utils.world.BlockUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

public class AntiPistonAuraModule extends BaseModule {

    private final List<BlockPlaceInfo> spam = Collections.synchronizedList(new ArrayList<>());
    private final HashSet<PistonInfo> pistons = new HashSet<>();
    private final HashSet<EnderCrystalInfo> crystals = new HashSet<>();
    private final Configuration<IntRange> placeCoolTime;
    private final UpdateTimer placeTimer;
    private ItemSlot obsidianSlot;
    private long when;

    public AntiPistonAuraModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        placeCoolTime = provider.get("place_cool_time", "Place Cool Time", null, IntRange.class, new IntRange(20, 1000, 10, 10));
        placeTimer = new UpdateTimer(true, placeCoolTime);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    private PistonInfo getPistonInfo(BlockPos pos) {
        synchronized (pistons) {
            for (PistonInfo candidate : pistons) {
                if (candidate.getBlockPos().equals(pos)) return candidate;
            }
        }
        return null;
    }

    private EnderCrystalInfo getNearbyCrystal(Vec3d vec) {
        synchronized (crystals) {
            for (EnderCrystalInfo candidate : crystals) {
                if (candidate.getPos().squareDistanceTo(vec) < 4) return candidate;
            }
        }
        return null;
    }

    private BlockPlaceInfo findBlockPlaceInfo(World world, BlockPos input) {
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPlaceInfo info = new BlockFace(input.offset(facing), facing.getOpposite()).toBlockPlaceInfo(world);
            BlockPos pos = info.getBlockPos();
            if (BlockUtils.isAir(world, pos.offset(facing))) continue;
            return info;
        }
        return null;
    }

    private void updateAll() {
        synchronized (pistons) {
            pistons.clear();
            forEachNearby(pos -> {
                IBlockState blockState = getWorld().getBlockState(pos);
                if (!(blockState.getBlock() instanceof BlockPistonBase)) return;
                EnumFacing enumFacing = blockState.getValue(BlockDirectional.FACING);
                pistons.add(new PistonInfo(pos, enumFacing));
            });
        }
        synchronized (crystals) {
            crystals.clear();
            for (EntityInfo<EntityEnderCrystal> info : EntityUtils.getNearbyEntities(getPlayer().getPositionVector(), EntityEnderCrystal.class)) {
                if (info.getDistanceSq() > 10) continue;
                EntityEnderCrystal crystal = info.getEntity();
                crystals.add(new EnderCrystalInfo(crystal.getEntityId(), crystal.getPositionVector(), null));
            }
        }
    }

    private void placeObsidian() {
        if (spam.isEmpty()) return;
        ItemSlot copy = obsidianSlot;
        if (copy == null) return;
        if (!placeTimer.update()) return;
        InventoryUtils.silentSwitch(true, copy.getIndex(), () -> {
            for (BlockPlaceInfo info : new ArrayList<>(spam)) {
                if (info == null) continue;
                BlockUtils.place(info, true);
            }
        });
    }

    private void preventPistonAura() {
        forEachNearby(pos -> {
            PistonInfo pistonInfo = getPistonInfo(pos);
            if (pistonInfo == null) return;
            EnumFacing enumFacing = pistonInfo.getFacing();
            EnderCrystalInfo crystal = getNearbyCrystal(BlockUtils.toVec3d(pos.offset(enumFacing)).add(0.5, 0, 0.5));
            if (crystal == null) return;
            Vec3d predicted = crystal.getPos().add(new Vec3d(enumFacing.getDirectionVec()).scale(0.5));
            if (DamageUtils.getCrystalDamage(getPlayer(), predicted) < 30) return;
            spam.add(findBlockPlaceInfo(getWorld(), pos));
            if (!BlockUtils.isAir(getWorld(), pos.offset(enumFacing).offset(enumFacing))) {
                spam.add(findBlockPlaceInfo(getWorld(), pos.offset(enumFacing)));
            }
            when = System.currentTimeMillis();
            sendPacket(crystal.newAttackPacket());
        });
        placeObsidian();
    }

    private void forEachNearby(Consumer<BlockPos> consumer) {
        BlockPos playerPos = BlockUtils.toBlockPos(getPlayer().getPositionVector());
        for (int x = -4; x <= 4; x++) {
            for (int y = 1; y <= 4; y++) {
                for (int z = -4; z <= 4; z++) {
                    BlockPos pos = new BlockPos(playerPos.getX() + x, playerPos.getY() + y, playerPos.getZ() + z);
                    consumer.accept(pos);
                }
            }
        }
    }

    @EventHandler(timing = EventTiming.POST)
    public void onClientTick(ClientTickEvent e) {
        updateAll();
        spam.removeIf(it -> {
            if (it == null) return true;
            Block block = getWorld().getBlockState(it.getBlockPos()).getBlock();
            return block != Blocks.PISTON && block != Blocks.PISTON_HEAD && block != Blocks.PISTON_EXTENSION && block != Blocks.AIR ||
                    System.currentTimeMillis() - when > 1000;
        });
        ItemSlot obsidianSlot = InventoryUtils.findItemSlot(Item.getItemFromBlock(Blocks.OBSIDIAN), InventoryType.values());
        if (obsidianSlot != null && obsidianSlot.getInventoryType() != InventoryType.HOTBAR) {
            if (!spam.isEmpty()) {
                obsidianSlot = InventoryUtils.moveToHotbar(obsidianSlot);
            } else {
                obsidianSlot = null;
            }
        }
        this.obsidianSlot = obsidianSlot;
        preventPistonAura();
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketReceive(PacketReceiveEvent e) {
        if (!(e.getPacket() instanceof SPacketSpawnObject)) return;
        SPacketSpawnObject packet = (SPacketSpawnObject) e.getPacket();
        if (packet.getType() != 51) return;
        Vec3d pos = new Vec3d(packet.getX(), packet.getY(), packet.getZ());
        synchronized (crystals) {
            crystals.add(new EnderCrystalInfo(packet.getEntityID(), pos, null));
        }
        preventPistonAura();
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketReceive2(PacketReceiveEvent e) {
        if (!(e.getPacket() instanceof SPacketBlockChange)) return;
        SPacketBlockChange packet = (SPacketBlockChange) e.getPacket();
        Block block = packet.getBlockState().getBlock();
        if (block != Blocks.PISTON && block != Blocks.PISTON_HEAD) return;
        if (!(packet.getBlockState().getBlock() instanceof BlockPistonBase)) return;
        EnumFacing enumFacing = packet.getBlockState().getValue(BlockDirectional.FACING);
        synchronized (pistons) {
            pistons.add(new PistonInfo(packet.getBlockPosition(), enumFacing));
        }
        preventPistonAura();
    }

    @EventHandler(timing = EventTiming.POST)
    public void onGameTick(GameTickEvent e) {
        placeObsidian();
    }

    @Override
    public String getDefaultName() {
        return "AntiPistonAura";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.COMBAT;
    }

    private static class PistonInfo {
        private final BlockPos blockPos;
        private final EnumFacing facing;

        public PistonInfo(BlockPos blockPos, EnumFacing facing) {
            this.blockPos = blockPos;
            this.facing = facing;
        }

        public BlockPos getBlockPos() {
            return blockPos;
        }

        public EnumFacing getFacing() {
            return facing;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PistonInfo that = (PistonInfo) o;

            if (blockPos != null ? !blockPos.equals(that.blockPos) : that.blockPos != null) return false;
            return facing == that.facing;
        }

        @Override
        public int hashCode() {
            int result = blockPos != null ? blockPos.hashCode() : 0;
            result = 31 * result + (facing != null ? facing.hashCode() : 0);
            return result;
        }
    }
}
