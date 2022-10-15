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

package net.sushiclient.client.modules.world;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketReceiveEvent;
import net.sushiclient.client.events.player.PlayerTravelEvent;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.world.BlockUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class VirtualTpModule extends BaseModule {

    private final Configuration<Integer> x;
    private final Configuration<Integer> y;
    private final Configuration<Integer> z;
    private final Configuration<Boolean> noClip;
    private final Set<BlockPos> loaded = Collections.synchronizedSet(new HashSet<>());
    private BlockPos initPos;

    public VirtualTpModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        x = provider.get("x", "X", null, Integer.class, 0);
        y = provider.get("y", "Y", null, Integer.class, 0);
        z = provider.get("z", "Z", null, Integer.class, 0);
        noClip = provider.get("no_clip", "No Clip", null, Boolean.class, true);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
        initPos = BlockUtils.toBlockPos(getPlayer().getPositionVector());
        loaded.clear();
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
        getPlayer().noClip = false;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onMove(PlayerTravelEvent e) {
        getPlayer().noClip = noClip.getValue();
    }

    private BlockPos getTargetPos() {
        return new BlockPos(x.getValue(), y.getValue(), z.getValue());
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        BlockPos sent = null;
        BlockPos target = getTargetPos();
        for (int x = -6; x <= 6; x++) {
            for (int y = -1; y <= 5; y++) {
                if (y % 2 == 0) continue;
                for (int z = -6; z <= 6; z++) {
                    BlockPos pos = target
                            .add(new BlockPos(x, y, z))
                            .add(BlockUtils.toBlockPos(getPlayer().getPositionVector()))
                            .subtract(initPos);
                    if (loaded.contains(pos)) continue;
                    sent = pos;
                    break;
                }
            }
        }
        if (sent == null) return;
        sendPacket(new CPacketPlayerTryUseItemOnBlock(sent, EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.5F, 0, 0.5F));
        loaded.add(sent);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketReceive(PacketReceiveEvent e) {
        if (!(e.getPacket() instanceof SPacketBlockChange)) return;
        SPacketBlockChange packet = (SPacketBlockChange) e.getPacket();
        BlockPos pos = packet.getBlockPosition()
                .subtract(getTargetPos())
                .add(initPos);
        Minecraft.getMinecraft().addScheduledTask(() -> {
            getWorld().setBlockState(pos, packet.getBlockState());
        });
//        Sushi.getProfile().getLogger().send(LogLevel.INFO, "Setting block at: (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ") to: " + packet.getBlockState().getBlock().getLocalizedName());
    }

    @Override
    public String getDefaultName() {
        return "VirtualTp";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.WORLD;
    }
}
