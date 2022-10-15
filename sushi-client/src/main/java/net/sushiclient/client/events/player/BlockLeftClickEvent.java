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

package net.sushiclient.client.events.player;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.sushiclient.client.events.Cancellable;
import net.sushiclient.client.events.Event;
import net.sushiclient.client.events.EventTiming;

public class BlockLeftClickEvent implements Event, Cancellable {

    private final EventTiming timing;
    private final PlayerInteractEvent.LeftClickBlock delegate;

    public BlockLeftClickEvent(EventTiming timing, PlayerInteractEvent.LeftClickBlock delegate) {
        this.timing = timing;
        this.delegate = delegate;
    }

    public EnumHand getHand() {
        return delegate.getHand();
    }

    public ItemStack getItemStack() {
        return delegate.getItemStack();
    }

    public BlockPos getPos() {
        return delegate.getPos();
    }

    public EnumFacing getFace() {
        return delegate.getFace();
    }

    public World getWorld() {
        return delegate.getWorld();
    }

    public Side getSide() {
        return delegate.getSide();
    }

    public EnumActionResult getCancellationResult() {
        return delegate.getCancellationResult();
    }

    public void setCancellationResult(EnumActionResult result) {
        delegate.setCancellationResult(result);
    }


    public Vec3d getHitVec() {
        return delegate.getHitVec();
    }

    public net.minecraftforge.fml.common.eventhandler.Event.Result getUseBlock() {
        return delegate.getUseBlock();
    }

    public net.minecraftforge.fml.common.eventhandler.Event.Result getUseItem() {
        return delegate.getUseItem();
    }

    public void setUseBlock(net.minecraftforge.fml.common.eventhandler.Event.Result triggerBlock) {
        delegate.setUseBlock(triggerBlock);
    }

    public void setUseItem(net.minecraftforge.fml.common.eventhandler.Event.Result triggerItem) {
        delegate.setUseItem(triggerItem);
    }

    @Override
    public boolean isCancelled() {
        return delegate.isCanceled();
    }

    @Override
    public void setCancelled(boolean cancelled) {
        delegate.setCanceled(cancelled);
    }

    @Override
    public EventTiming getTiming() {
        return timing;
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
