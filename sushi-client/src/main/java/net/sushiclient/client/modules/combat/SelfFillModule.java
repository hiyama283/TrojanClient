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

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.command.LogLevel;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.player.*;
import net.sushiclient.client.utils.world.BlockUtils;

import java.util.Objects;

public class SelfFillModule extends BaseModule {
    public enum FillItem {
        OBSIDIAN, E_CHEST, HOLDING
    }

    public enum MoveMode {
        TELEPORT, MOTION
    }

    private final Configuration<DoubleRange> moveVal;
    private final Configuration<MoveMode> movementMode;
    private final Configuration<FillItem> item;
    private final Configuration<Boolean> silent;

    public SelfFillModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        moveVal = provider.get("move_value", "Move value", null, DoubleRange.class,
                new DoubleRange(2.5, 4, 0.1, 0.1, 1));
        movementMode = provider.get("movement_mode", "Move mode", null, MoveMode.class, MoveMode.TELEPORT);
        item = provider.get("item", "Item", null, FillItem.class, FillItem.OBSIDIAN);
        silent = provider.get("silent", "Silent", null, Boolean.class, true);
    }

    @Override
    public void onEnable() {
        if (item.getValue() == FillItem.HOLDING) {
            if (ItemSlot.current().getItemStack().getItem() instanceof ItemBlock) {
                BlockPos pos = PlayerUtils.getPlayerPos(getPlayer());

                BlockPos targetPos = null;
                for (EnumFacing value : EnumFacing.values()) {
                    if (BlockUtils.getBlock(pos.add(value.getDirectionVec())) != Blocks.AIR) {
                        targetPos = pos.add(value.getDirectionVec());
                    }
                }

                if (Objects.isNull(targetPos)) {
                    chatLog(LogLevel.ERROR, "Cannot find space...");
                    setEnabled(false);
                    return;
                }

                EnumFacing facing = null;
                for (EnumFacing value : EnumFacing.values()) {
                    if (targetPos.add(value.getDirectionVec()).equals(pos)) {
                        facing = value;
                    }
                }


                if (Objects.isNull(facing)) {
                    chatLog(LogLevel.ERROR, "Cannot find space...");
                    setEnabled(false);
                    return;
                }


                if (movementMode.getValue() == MoveMode.MOTION) {
                    getPlayer().motionY = moveVal.getValue().getCurrent();
                } else if (movementMode.getValue() == MoveMode.TELEPORT) {
                    PositionUtils.move(pos.getX(), pos.getY() + moveVal.getValue().getCurrent(), pos.getZ(), 0, 0,
                            getPlayer().onGround, PositionMask.POSITION);
                }

                BlockPos finalTargetPos = targetPos;
                EnumFacing finalFacing = facing;
                InventoryUtils.silentSwitch(silent.getValue(), ItemSlot.current().getIndex(), () -> {
                    BlockUtils.rightClickBlock(finalTargetPos, finalFacing, new Vec3d(0.5, 0.8, 0.5), true, EnumHand.MAIN_HAND);
                });

                if (movementMode.getValue() == MoveMode.TELEPORT) {
                    PositionUtils.move(pos.getX(), pos.getY() - moveVal.getValue().getCurrent(), pos.getZ(), 0, 0,
                            getPlayer().onGround, PositionMask.POSITION);
                }
            } else {
                chatLog(LogLevel.ERROR, "You are not holding block");
                setEnabled(false);
            }
        } else if (item.getValue() == FillItem.E_CHEST) {
            for (int i = 0; i < InventoryType.HOTBAR.getSize(); i++) {
                if (new ItemSlot(i).getItemStack().getItem() == Item.getItemFromBlock(Blocks.ENDER_CHEST)) {
                    BlockPos pos = PlayerUtils.getPlayerPos(getPlayer());

                    BlockPos targetPos = null;
                    for (EnumFacing value : EnumFacing.values()) {
                        if (BlockUtils.getBlock(pos.add(value.getDirectionVec())) != Blocks.AIR) {
                            targetPos = pos.add(value.getDirectionVec());
                        }
                    }

                    if (Objects.isNull(targetPos)) {
                        chatLog(LogLevel.ERROR, "Cannot find space...");
                        setEnabled(false);
                        return;
                    }

                    EnumFacing facing = null;
                    for (EnumFacing value : EnumFacing.values()) {
                        if (targetPos.add(value.getDirectionVec()).equals(pos)) {
                            facing = value;
                        }
                    }


                    if (Objects.isNull(facing)) {
                        chatLog(LogLevel.ERROR, "Cannot find space...");
                        setEnabled(false);
                        return;
                    }


                    if (movementMode.getValue() == MoveMode.MOTION) {
                        getPlayer().motionY = moveVal.getValue().getCurrent();
                    } else if (movementMode.getValue() == MoveMode.TELEPORT) {
                        PositionUtils.move(pos.getX(), pos.getY() + moveVal.getValue().getCurrent(), pos.getZ(), 0, 0,
                                getPlayer().onGround, PositionMask.POSITION);
                    }

                    BlockPos finalTargetPos = targetPos;
                    EnumFacing finalFacing = facing;
                    InventoryUtils.silentSwitch(silent.getValue(), ItemSlot.current().getIndex(), () -> {
                        BlockUtils.rightClickBlock(finalTargetPos, finalFacing, new Vec3d(0.5, 0.8, 0.5), true, EnumHand.MAIN_HAND);
                    });

                    if (movementMode.getValue() == MoveMode.TELEPORT) {
                        PositionUtils.move(pos.getX(), pos.getY() - moveVal.getValue().getCurrent(), pos.getZ(), 0, 0,
                                getPlayer().onGround, PositionMask.POSITION);
                    }
                    setEnabled(false);
                    return;
                }
            }

            chatLog(LogLevel.ERROR, "Cannot find item");
        } else if (item.getValue() == FillItem.OBSIDIAN) {
            for (int i = 0; i < InventoryType.HOTBAR.getSize(); i++) {
                if (new ItemSlot(i).getItemStack().getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN)) {
                    BlockPos pos = PlayerUtils.getPlayerPos(getPlayer());

                    BlockPos targetPos = null;
                    for (EnumFacing value : EnumFacing.values()) {
                        if (BlockUtils.getBlock(pos.add(value.getDirectionVec())) != Blocks.AIR) {
                            targetPos = pos.add(value.getDirectionVec());
                        }
                    }

                    if (Objects.isNull(targetPos)) {
                        chatLog(LogLevel.ERROR, "Cannot find space...");
                        setEnabled(false);
                        return;
                    }

                    EnumFacing facing = null;
                    for (EnumFacing value : EnumFacing.values()) {
                        if (targetPos.add(value.getDirectionVec()).equals(pos)) {
                            facing = value;
                        }
                    }


                    if (Objects.isNull(facing)) {
                        chatLog(LogLevel.ERROR, "Cannot find space...");
                        setEnabled(false);
                        return;
                    }


                    if (movementMode.getValue() == MoveMode.MOTION) {
                        getPlayer().motionY = moveVal.getValue().getCurrent();
                    } else if (movementMode.getValue() == MoveMode.TELEPORT) {
                        PositionUtils.move(pos.getX(), pos.getY() + moveVal.getValue().getCurrent(), pos.getZ(), 0, 0,
                                getPlayer().onGround, PositionMask.POSITION);
                    }

                    BlockPos finalTargetPos = targetPos;
                    EnumFacing finalFacing = facing;
                    InventoryUtils.silentSwitch(silent.getValue(), ItemSlot.current().getIndex(), () -> {
                        BlockUtils.rightClickBlock(finalTargetPos, finalFacing, new Vec3d(0.5, 0.8, 0.5), true, EnumHand.MAIN_HAND);
                    });

                    if (movementMode.getValue() == MoveMode.TELEPORT) {
                        PositionUtils.move(pos.getX(), pos.getY() - moveVal.getValue().getCurrent(), pos.getZ(), 0, 0,
                                getPlayer().onGround, PositionMask.POSITION);
                    }
                    setEnabled(false);
                    return;
                }
            }

            chatLog(LogLevel.ERROR, "Cannot find item");
        }

        setEnabled(false);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public String getDefaultName() {
        return "Self Fill";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.COMBAT;
    }
}
