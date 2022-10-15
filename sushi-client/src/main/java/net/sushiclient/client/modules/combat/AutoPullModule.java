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
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.config.Config;
import net.sushiclient.client.config.ConfigInjector;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.task.forge.TaskExecutor;
import net.sushiclient.client.task.tasks.BlockPlaceTask;
import net.sushiclient.client.task.tasks.ItemSwitchTask;
import net.sushiclient.client.utils.EntityUtils;
import net.sushiclient.client.utils.player.PositionMask;
import net.sushiclient.client.utils.player.PositionUtils;
import net.sushiclient.client.utils.player.RotateMode;
import net.sushiclient.client.utils.world.BlockPlaceInfo;
import net.sushiclient.client.utils.world.BlockUtils;

import java.util.*;

public class AutoPullModule extends BaseModule {

    private final WeakHashMap<EntityPlayer, Integer> pulledPlayers = new WeakHashMap<>();

    @Config(id = "rotate_mode", name = "Rotate Mode")
    public RotateMode rotateMode = RotateMode.NCP;

    @Config(id = "anti_hole", name = "Anti Hole")
    public Boolean antiHole = true;

    @Config(id = "anti_phase", name = "Anti Phase")
    public Boolean antiPhase = true;

    @Config(id = "disable_after", name = "Disable After")
    public Boolean disableAfter = true;

    public AutoPullModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        new ConfigInjector(provider).inject(this);
    }

    @Override
    public void onEnable() {
        pulledPlayers.clear();
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    private boolean isInHole(EntityPlayer player) {
        BlockPos floor = BlockUtils.toBlockPos(player.getPositionVector());
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            Block block = getWorld().getBlockState(floor.offset(facing)).getBlock();
            if (block != Blocks.OBSIDIAN && block != Blocks.BEDROCK) return false;
        }
        return true;
    }

    private boolean isPhasing(EntityPlayer player) {
        BlockPos floor = BlockUtils.toBlockPos(player.getPositionVector());
        Block floorBlock = getWorld().getBlockState(floor).getBlock();
        Block roofBlock = getWorld().getBlockState(floor.offset(EnumFacing.UP)).getBlock();
        return floorBlock != Blocks.AIR && roofBlock == Blocks.AIR;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        if (disableAfter) setEnabled(false);
        for (Map.Entry<EntityPlayer, Integer> entry : new HashMap<>(pulledPlayers).entrySet()) {
            int time = entry.getValue() - 1;
            pulledPlayers.put(entry.getKey(), time);
            if (time < 0) pulledPlayers.remove(entry.getKey());
        }

        List<EntityPlayer> players = EntityUtils.getNearbyPlayers(4);
        // remove if 1. not in hole 2. pulled last time 3. in the same hole
        players.removeIf(it -> (antiHole && !isInHole(it)) && (antiPhase && !isPhasing(it)) ||
                pulledPlayers.containsKey(it) ||
                BlockUtils.toBlockPos(getPlayer().getPositionVector()).equals(it.getPosition()));
        players.sort(Comparator.comparingDouble(p -> getPlayer().getDistanceSq(p)));
        if (players.isEmpty()) return;
        EntityPlayer target = players.get(0);
        BlockPos targetPos = BlockUtils.toBlockPos(target.getPositionVector());

        EnumFacing pistonFacing = null;
        BlockPlaceInfo pistonPlace = null;
        BlockPlaceInfo redstonePlace = null;
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos air = targetPos.offset(facing.getOpposite()).offset(EnumFacing.UP);
            BlockPos air1 = air.offset(EnumFacing.UP);
            if (getWorld().getBlockState(air).getBlock() != Blocks.AIR
                    || getWorld().getBlockState(air1).getBlock() != Blocks.AIR) continue;
            BlockPos pistonPos = targetPos.offset(facing).offset(EnumFacing.UP);
            double sin = (pistonPos.getY() - getPlayer().posY) /
                    getPlayer().getPositionVector().distanceTo(BlockUtils.toVec3d(pistonPos).add(0.5, 0.5, 0.5));
            if (Math.abs(sin) > 0.5D) continue;
            BlockPlaceInfo pistonCandidate = BlockUtils.findBlockPlaceInfo(getWorld(), pistonPos);
            if (pistonCandidate == null) continue;
            IBlockState pistonState = getWorld().getBlockState(pistonPos);
            getWorld().setBlockState(pistonPos, Blocks.PISTON.getDefaultState());
            for (EnumFacing facing1 : EnumFacing.values()) {
                BlockPos redstonePos = pistonPos.offset(facing1);
                BlockPlaceInfo redstoneCandidate = BlockUtils.findBlockPlaceInfo(getWorld(), redstonePos);
                getWorld().setBlockState(pistonPos, pistonState);
                if (redstoneCandidate != null) {
                    pistonFacing = facing;
                    pistonPlace = pistonCandidate;
                    redstonePlace = redstoneCandidate;
                    break;
                }
            }
        }
        if (pistonPlace == null) return;

        // rotate
        Vec3d lookAt = getPlayer().getPositionVector().add(new Vec3d(pistonFacing.getDirectionVec()));
        PositionUtils.require()
                .desyncMode(PositionMask.LOOK)
                .lookAt(lookAt);

        TaskExecutor.newTaskChain()
                .supply(Item.getItemFromBlock(Blocks.PISTON))
                .then(new ItemSwitchTask(null, true))
                .abortIfFalse()
                .supply(Collections.singletonList(pistonPlace))
                .then(new BlockPlaceTask(rotateMode, true, false, true))
                .supply(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK))
                .then(new ItemSwitchTask(null, true))
                .abortIfFalse()
                .supply(Collections.singletonList(redstonePlace))
                .then(new BlockPlaceTask(rotateMode, true, false, true))
                .execute();
        pulledPlayers.put(target, 20);
    }

    @Override
    public String getDefaultName() {
        return "AutoPull";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.COMBAT;
    }
}
