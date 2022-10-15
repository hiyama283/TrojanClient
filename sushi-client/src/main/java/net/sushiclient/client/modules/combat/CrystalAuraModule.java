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
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.ConfigurationCategory;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.config.data.EspColor;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketReceiveEvent;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.events.tick.GameTickEvent;
import net.sushiclient.client.events.world.WorldRenderEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.task.forge.TaskExecutor;
import net.sushiclient.client.task.tasks.ItemSwitchMode;
import net.sushiclient.client.task.tasks.ItemSwitchTask;
import net.sushiclient.client.utils.EntityUtils;
import net.sushiclient.client.utils.UpdateTimer;
import net.sushiclient.client.utils.combat.DamageUtils;
import net.sushiclient.client.utils.player.InventoryType;
import net.sushiclient.client.utils.player.InventoryUtils;
import net.sushiclient.client.utils.player.ItemSlot;
import net.sushiclient.client.utils.player.RotateMode;
import net.sushiclient.client.utils.render.RenderUtils;
import net.sushiclient.client.utils.world.BlockUtils;

import java.awt.*;
import java.util.List;
import java.util.*;

public class CrystalAuraModule extends BaseModule {

    private final Configuration<DoubleRange> targetRange;
    private final Configuration<DoubleRange> crystalRange;
    private final Configuration<DoubleRange> wallRange;

    private final Configuration<IntRange> placeCoolTime;
    private final Configuration<IntRange> breakCoolTime;
    private final Configuration<IntRange> recalculationCoolTime;

    private final Configuration<DoubleRange> minDamage;
    private final Configuration<DoubleRange> facePlace;
    private final Configuration<IntRange> maxTargets;
    private final Configuration<Boolean> customDamage;
    private final Configuration<IntRange> customPower;
    private final Configuration<DoubleRange> damageRatio;
    private final Configuration<DoubleRange> maxSelfDamage;
    private final Configuration<DoubleRange> minSelfHp;

    private final Configuration<ItemSwitchMode> switchMode;
    private final Configuration<Boolean> antiWeakness;
    private final Configuration<Boolean> silentSwitch;

    private final Configuration<RotateMode> rotate;
    private final Configuration<Boolean> swing;
    private final Configuration<Boolean> y255Attack;

    private final Configuration<DoubleRange> selfPingMultiplier;
    private final Configuration<Boolean> useInputs;
    private final Configuration<Boolean> constantSpeed;

    private final Configuration<Boolean> outline;
    private final Configuration<EspColor> outlineColor;
    private final Configuration<Boolean> fill;
    private final Configuration<EspColor> fillColor;

    private final Set<EnderCrystalInfo> enderCrystals = new HashSet<>();

    private final UpdateTimer breakTimer;
    private final UpdateTimer placeTimer;
    private final UpdateTimer recalculationTimer;

    private volatile CrystalAttack crystalAttack;
    private volatile CrystalAttack nearbyCrystalAttack;
    private volatile ItemSlot crystalSlot;

    private Vec3d target;
    private boolean rotateDone;

    public CrystalAuraModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        // Range
        ConfigurationCategory range = provider.getCategory("range", "Range Settings", null);
        targetRange = range.get("target_range", "Target Range", null, DoubleRange.class, new DoubleRange(12, 18, 1, 1, 1));
        crystalRange = range.get("crystal_range", "Crystal Range", null, DoubleRange.class, new DoubleRange(4.5, 10, 1, 0.1, 1));
        wallRange = range.get("wall_range", "Wall Range", null, DoubleRange.class, new DoubleRange(3, 6, 1, 0.1, 1));

        // Cool Time
        ConfigurationCategory coolTime = provider.getCategory("cool_time", "Cool Time Settings", null);
        placeCoolTime = coolTime.get("place_cool_time", "Place Delay", null, IntRange.class, new IntRange(20, 1000, 10, 10));
        breakCoolTime = coolTime.get("break_cool_time", "Break Delay", null, IntRange.class, new IntRange(0, 1000, 0, 10));
        recalculationCoolTime = coolTime.get("recalculation_cool_time", "Recalculation Delay", null, IntRange.class, new IntRange(0, 1000, 0, 10));

        // Damage
        ConfigurationCategory damage = provider.getCategory("damage", "Damage Settings", null);
        minDamage = damage.get("min_damage", "Min Damage", null, DoubleRange.class, new DoubleRange(6, 20, 0, 0.2, 1));
        facePlace = damage.get("face_place", "Face Place", null, DoubleRange.class, new DoubleRange(5, 20, 0, 0.2, 1));
        maxTargets = damage.get("max_targets", "Max Targets", null, IntRange.class, new IntRange(1, 10, 1, 1));
        customDamage = damage.get("custom_damage", "Custom Damage", null, Boolean.class, false);
        customPower = damage.get("power", "Power", null, IntRange.class, new IntRange(6, 10, 1, 1), customDamage::getValue, false, 0);
        damageRatio = damage.get("damage_ratio", "Damage Ratio", null, DoubleRange.class, new DoubleRange(0.5, 1, 0, 0.05, 2));
        maxSelfDamage = damage.get("max_self_damage", "Max Self Damage", null, DoubleRange.class, new DoubleRange(6, 20, 0, 0.2, 1));
        minSelfHp = damage.get("min_self_hp", "Min Self HP", null, DoubleRange.class, new DoubleRange(6, 20, 1, 0.1, 1));

        // Switch
        ConfigurationCategory switchCategory = provider.getCategory("switch", "Switch Settings", null);
        switchMode = switchCategory.get("switch", "Switch Mode", null, ItemSwitchMode.class, ItemSwitchMode.INVENTORY);
        antiWeakness = switchCategory.get("anti_weakness", "Anti Weakness", null, Boolean.class, false);
        silentSwitch = switchCategory.get("silent_switch", "Silent Switch", null, Boolean.class, true);

        // Anti-Cheat
        ConfigurationCategory antiCheat = provider.getCategory("anti_cheat", "Anti Cheat", null);
        rotate = antiCheat.get("rotate", "Rotate", null, RotateMode.class, RotateMode.NCP);
        swing = antiCheat.get("swing", "Swing", null, Boolean.class, true);
        y255Attack = antiCheat.get("y_255_attack", "Y 255 Attack", null, Boolean.class, false);

        // Predict
        ConfigurationCategory predict = provider.getCategory("predict", "Predict Settings", null);
        selfPingMultiplier = predict.get("self_ping_multiplier", "Self Multiplier", null, DoubleRange.class, new DoubleRange(1, 10, 0, 0.1, 1));
        useInputs = predict.get("use_inputs", "Use Inputs", null, Boolean.class, true);
        constantSpeed = predict.get("constant_speed", "Constant Speed", null, Boolean.class, true);

        // Render
        ConfigurationCategory render = provider.getCategory("render", "Render Settings", null);
        outline = render.get("outline", "Outline", null, Boolean.class, true);
        outlineColor = render.get("outline_color", "Outline Color", null, EspColor.class, new EspColor(Color.WHITE, false, true), outline::isValid, false, 0);
        fill = render.get("fill", "Fill", null, Boolean.class, true);
        fillColor = render.get("fill_color", "Fill Color", null, EspColor.class, new EspColor(Color.PINK, false, true).setAlpha(50), fill::isValid, false, 0);

        // timer
        placeTimer = new UpdateTimer(true, placeCoolTime);
        breakTimer = new UpdateTimer(true, breakCoolTime);
        recalculationTimer = new UpdateTimer(true, recalculationCoolTime);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
        crystalAttack = null;
        nearbyCrystalAttack = null;
    }

    private double getDamage(Vec3d pos, EntityPlayer player, Vec3d offset) {
        double power = customDamage.getValue() ? customPower.getValue().getCurrent() : 6;
        double damage = DamageUtils.getExplosionDamage(player, offset, pos, power);
        return DamageUtils.applyModifier(player, damage, DamageUtils.EXPLOSION);
    }

    private CrystalAttack getCrystalAttack(int crystal, Vec3d pos, AxisAlignedBB box) {
        ArrayList<Map.Entry<EntityPlayer, Double>> damages = new ArrayList<>();
        for (Entity entity : getWorld().loadedEntityList) {
            if (!(entity instanceof EntityPlayer)) continue;
            double range = targetRange.getValue().getCurrent();
            if (getPlayer().getDistanceSq(entity) > range * range) continue;
            EntityPlayer player = (EntityPlayer) entity;
            if (player.getName().equals(getPlayer().getName())) continue;
            Vec3d offset = EntityUtils.getPingOffset(player, useInputs.getValue(), constantSpeed.getValue(),
                    selfPingMultiplier.getValue().getCurrent());
            damages.add(new AbstractMap.SimpleEntry<>(player, getDamage(pos, player, offset)));
        }
        if (damages.isEmpty()) return null;
        // sort
        damages.sort(Comparator.comparingDouble(Map.Entry::getValue));
        Collections.reverse(damages);
        LinkedHashMap<EntityPlayer, Double> sortedMap = new LinkedHashMap<>();
        int index = 0;
        for (Map.Entry<EntityPlayer, Double> entry : damages) {
            if (index++ >= maxTargets.getValue().getCurrent()) break;
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return new CrystalAttack(crystal, pos, box, sortedMap);
    }

    private boolean checkFacePlace(CrystalAttack attack) {
        for (EntityPlayer player : attack.damages.keySet()) {
            if (player.getHealth() <= facePlace.getValue().getCurrent()) return true;
        }
        return false;
    }

    private boolean filter(CrystalAttack attack, boolean checkCollision) {
        if (attack == null) return false;
        Vec3d crystalPos = attack.info.getPos();
        AxisAlignedBB crystalBox = attack.info.getBox();

        List<Entity> entities = getWorld().getEntitiesWithinAABBExcludingEntity(null, crystalBox);
        entities.removeIf(p -> p instanceof EntityEnderCrystal);
        if (checkCollision && !entities.isEmpty()) return false;

        Vec3d offset = EntityUtils.getPingOffset(getPlayer(), useInputs.getValue(), constantSpeed.getValue(), selfPingMultiplier.getValue().getCurrent());
        double selfDamage = getDamage(crystalPos, getPlayer(), offset);
        double ratio = selfDamage / attack.getTotalDamage();
        if (attack.getTotalDamage() < minDamage.getValue().getCurrent() &&
                (attack.damages.isEmpty() || attack.getTotalDamage() <= 2 || !checkFacePlace(attack))) {
            return false;
        }
        if (selfDamage > maxSelfDamage.getValue().getCurrent()) return false;
        if (ratio > damageRatio.getValue().getCurrent() && !checkFacePlace(attack)) return false;
        if (getPlayer().getHealth() - selfDamage < minSelfHp.getValue().getCurrent()) return false;
        return true;
    }

    private CrystalAttack findBestCrystalAttack(List<CrystalAttack> attacks) {
        CrystalAttack best = null;
        double maxDamage = 0;
        for (CrystalAttack attack : attacks) {
            double damage = attack.getTotalDamage();
            if (damage > maxDamage) {
                maxDamage = damage;
                best = attack;
            }
        }
        return best;
    }

    private void refreshEnderCrystals() {
        synchronized (enderCrystals) {
            enderCrystals.clear();
            for (Entity enderCrystal : getWorld().loadedEntityList) {
                if (!(enderCrystal instanceof EntityEnderCrystal)) continue;
                enderCrystals.add(new EnderCrystalInfo(enderCrystal.getEntityId(), enderCrystal.getPositionVector(), enderCrystal.getEntityBoundingBox()));
            }
        }
    }

    private void refreshCrystalAttack() {
        int distance = (int) Math.ceil(crystalRange.getValue().getCurrent());

        // refresh possible crystal placements
        ArrayList<CrystalAttack> attacks = new ArrayList<>();
        for (int x = -distance; x < distance; x++) {
            for (int y = -distance; y < distance; y++) {
                for (int z = -distance; z < distance; z++) {
                    if (x * x + y * y + z * z > distance * distance) continue;
                    BlockPos pos = new BlockPos(x + getPlayer().posX, y + getPlayer().posY, z + getPlayer().posZ);
                    Vec3d vec = BlockUtils.toVec3d(pos).add(0.5, 1, 0.5);

                    // check whether the block is obsidian/bedrock
                    IBlockState blockState = getWorld().getBlockState(pos);
                    Block block = blockState.getBlock();
                    if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN) continue;

                    // check distance
                    if (BlockUtils.canInteract(pos) || !EntityUtils.canInteract(vec.add(0, 1.7, 0),
                            crystalRange.getValue().getCurrent(), wallRange.getValue().getCurrent())) {
                        continue;
                    }

                    // check collisions
                    pos = pos.add(0, 1, 0);
                    AxisAlignedBB crystal = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(),
                            pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
                    if (getWorld().collidesWithAnyBlock(crystal)) continue;

                    CrystalAttack attack = getCrystalAttack(-1, vec, crystal);
                    if (filter(attack, true)) attacks.add(attack);
                }
            }
        }

        crystalAttack = findBestCrystalAttack(attacks);

        // nearby crystals
        ArrayList<CrystalAttack> nearby = new ArrayList<>();
        synchronized (enderCrystals) {
            for (EnderCrystalInfo entity : enderCrystals) {
                double distanceSq = getPlayer().getPositionVector().squareDistanceTo(entity.getPos());
                if (distanceSq > crystalRange.getValue().getCurrent() * crystalRange.getValue().getCurrent()) continue;
                if (crystalAttack != null && crystalAttack.info.getBox().intersects(entity.getBox())) continue;
                CrystalAttack attack = getCrystalAttack(entity.getEntityId(), entity.getPos(), entity.getBox());
                if (filter(attack, false)) nearby.add(attack);
            }
        }

        nearbyCrystalAttack = findBestCrystalAttack(nearby);
    }

    private EnderCrystalInfo getCollidingEnderCrystal(AxisAlignedBB box) {
        synchronized (enderCrystals) {
            for (EnderCrystalInfo enderCrystalInfo : enderCrystals) {
                if (enderCrystalInfo.getBox().intersects(box)) return enderCrystalInfo;
            }
        }
        return null;
    }

    private void breakEnderCrystal(EnderCrystalInfo enderCrystal) {
        rotate.getValue().rotate(enderCrystal.getPos().add(0, 0.5, 0), true, () -> {
            InventoryUtils.antiWeakness(antiWeakness.getValue(), () -> {
                sendPacket(enderCrystal.newAttackPacket());
                if (swing.getValue()) {
                    sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                }
            });
        }, null);
    }

    private synchronized void breakCrystal() {
        // copy
        CrystalAttack nearbyCrystalAttack = this.nearbyCrystalAttack;

        // break
        if (nearbyCrystalAttack != null && breakTimer.update()) breakEnderCrystal(nearbyCrystalAttack.info);
        this.nearbyCrystalAttack = null;
    }

    private synchronized void placeCrystal() {
        // copy
        CrystalAttack crystalAttack = this.crystalAttack;

        // place
        if (crystalAttack == null) return;
        if (crystalSlot == null) return;
        if (!placeTimer.update()) return;
        Vec3d crystalPos = crystalAttack.info.getPos();
        EnderCrystalInfo colliding = getCollidingEnderCrystal(crystalAttack.info.getBox());
        if (colliding != null && breakTimer.update()) breakEnderCrystal(colliding);
        ItemSlot copy = crystalSlot;
        Vec3d lookAt = crystalPos.add(0, -1, 0)
                .add(0.5, y255Attack.getValue() ? 0 : 1, 0.5);
        rotate.getValue().rotate(lookAt, true, () -> {
            InventoryUtils.silentSwitch(silentSwitch.getValue() && copy.getInventoryType() != InventoryType.OFFHAND,
                    copy.getIndex(), () -> {
                        boolean offhand = copy.getInventoryType() == InventoryType.OFFHAND;
                        if (swing.getValue()) {
                            sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                        }
                        if (y255Attack.getValue()) {
                            sendPacket(new CPacketPlayerTryUseItemOnBlock(BlockUtils.toBlockPos(crystalPos).add(0, -1, 0),
                                    EnumFacing.DOWN, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5F, 0, 0.5F));
                        } else {
                            sendPacket(new CPacketPlayerTryUseItemOnBlock(BlockUtils.toBlockPos(crystalPos).add(0, -1, 0),
                                    EnumFacing.UP, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5F, 1, 0.5F));
                        }
                    });
        }, null);
    }

    @EventHandler(timing = EventTiming.POST)
    public void onClientTick(ClientTickEvent e) {
        refreshEnderCrystals();
        if (recalculationTimer.update()) refreshCrystalAttack();
        if (crystalAttack == null && nearbyCrystalAttack == null) return;
        crystalSlot = InventoryUtils.findItemSlot(Items.END_CRYSTAL, InventoryType.HOTBAR, InventoryType.OFFHAND);
        if (crystalSlot == null || (ItemSlot.current().equals(crystalSlot) && !silentSwitch.getValue() &&
                ItemSlot.offhand().getItemStack().getItem() != Items.END_CRYSTAL)) {
            TaskExecutor.newTaskChain()
                    .supply(Items.END_CRYSTAL)
                    .then(new ItemSwitchTask(null, switchMode.getValue()))
                    .execute();
        }
    }

    @EventHandler(timing = EventTiming.POST)
    public void onGameTickEvent(GameTickEvent e) {
        placeCrystal();
    }

    @EventHandler(timing = EventTiming.POST)
    public void onWorldRender(WorldRenderEvent e) {
        // copy
        CrystalAttack crystalAttack = this.crystalAttack;
        if (crystalAttack == null) return;
        BlockPos pos = BlockUtils.toBlockPos(crystalAttack.info.getPos().subtract(0, 1, 0));
        AxisAlignedBB box = getWorld().getBlockState(pos).getBoundingBox(getWorld(), pos).offset(pos).grow(0.002);
        GlStateManager.disableDepth();
        if (outline.getValue()) RenderUtils.drawOutline(box, outlineColor.getValue().getCurrentColor(), 1);
        if (fill.getValue()) RenderUtils.drawFilled(box, fillColor.getValue().getCurrentColor());
        GlStateManager.enableDepth();
    }

    // break crystal
    @EventHandler(timing = EventTiming.PRE, priority = 1000)
    public void onPacketReceive(PacketReceiveEvent e) {
        if (!(e.getPacket() instanceof SPacketSpawnObject)) return;
        SPacketSpawnObject packet = (SPacketSpawnObject) e.getPacket();
        if (packet.getType() != 51) return;
        synchronized (enderCrystals) {
            Vec3d pos = new Vec3d(packet.getX(), packet.getY(), packet.getZ());
            AxisAlignedBB box = new AxisAlignedBB(pos.x - 0.75, pos.y, pos.z - 0.75, pos.x + 0.75, pos.y + 1.5, pos.z + 0.75);
            enderCrystals.add(new EnderCrystalInfo(packet.getEntityID(), pos, box));
        }
        breakCrystal();
    }

    @Override
    public String getDefaultName() {
        return "CrystalAura";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.COMBAT;
    }

    private static class CrystalAttack {
        final EnderCrystalInfo info;
        final LinkedHashMap<EntityPlayer, Double> damages;
        double cachedTotalDamage = -1;

        CrystalAttack(int entity, Vec3d crystalPos, AxisAlignedBB box, LinkedHashMap<EntityPlayer, Double> damages) {
            this.info = new EnderCrystalInfo(entity, crystalPos, box);
            this.damages = damages;
        }

        double getTotalDamage() {
            if (cachedTotalDamage != -1) return cachedTotalDamage;
            double total = 0;
            for (double damage : damages.values()) total += damage;
            cachedTotalDamage = total;
            return total;
        }
    }

}
