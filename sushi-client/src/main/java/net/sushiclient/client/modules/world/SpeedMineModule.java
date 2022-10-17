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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.ConfigurationCategory;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.config.data.EspColor;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.config.data.Named;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketSendEvent;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.events.world.WorldRenderEvent;
import net.sushiclient.client.mixin.AccessorPlayerControllerMP;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.player.*;
import net.sushiclient.client.utils.render.RenderBuilder;
import net.sushiclient.client.utils.render.RenderUtils;
import net.sushiclient.client.utils.world.BlockUtils;

import java.awt.*;
import java.util.Objects;

public class SpeedMineModule extends BaseModule implements ModuleSuffix {

    public enum MiningMode implements Named {
        PACKET("Packet"), AFTER("After"), NONE("None");

        private final String name;

        MiningMode(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    private final Configuration<IntRange> range;
    private final Configuration<Boolean> packetMine;
    private final Configuration<MiningMode> switching;
    private final Configuration<Boolean> antiAbort;
    private final Configuration<Boolean> once;
    private final Configuration<Boolean> resetDamage;
    private final Configuration<Boolean> render;
    private final Configuration<RenderBuilder.Box> renderMode;
    private final Configuration<DoubleRange> outlineWidth;
    private final Configuration<EspColor> unbreakColor;
    private final Configuration<EspColor> breakColor;
    private final Configuration<Boolean> useAirColor;
    private final Configuration<EspColor> airColor;
    private final Configuration<Boolean> strictBreak;
    private final Configuration<Boolean> rotation;
    private final Configuration<Boolean> alwaysRotate;
    private final Configuration<Boolean> rotateOnCanBreak;
    private final Configuration<Boolean> rotateOnBlockIsAvaiable;
    private final Configuration<Boolean> shaderMode;
    private final Configuration<Boolean> renderText;
    private final Configuration<String> textFont;
    private final Configuration<EspColor> textColor;
    private final Configuration<IntRange> textPts;
    private final Configuration<Boolean> textShadow;

    public SpeedMineModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);

        ConfigurationCategory breakSettings = provider.getCategory("break", "Break Settings", null);
        this.range = breakSettings.get("range", "Range", null, IntRange.class, new IntRange(5, 10, 1, 1));
        packetMine = breakSettings.get("packet_mine", "Packet Mine", null, Boolean.class, true);
        antiAbort = breakSettings.get("anti_abort", "Anti Abort", null, Boolean.class, true);
        switching = breakSettings.get("switching", "Switch", null, MiningMode.class, MiningMode.NONE);

        ConfigurationCategory rotate = provider.getCategory("rotate", "Rotate Settings", null);
        rotation = rotate.get("rotate", "Rotate", null, Boolean.class, false);
        alwaysRotate = rotate.get("always_rotate", "Always rotate", null, Boolean.class, false,
                rotation::getValue, false, 0);
        rotateOnCanBreak = rotate.get("rotate_on_can_break", "Rotate on can break", null, Boolean.class, false,
                rotation::getValue, false, 0);
        rotateOnBlockIsAvaiable = rotate.get("rotate_on_block_is_avaiable", "Rotate on block is avaiable", null, Boolean.class,
                false, rotation::getValue, false, 0);

        ConfigurationCategory strict = provider.getCategory("strict", "Strict Settings", null);
        once = strict.get("once", "Once break", null, Boolean.class, false);
        resetDamage = strict.get("reset_damage", "Reset damage", null, Boolean.class, false);
        strictBreak = strict.get("strict_break", "Strict break", null, Boolean.class, false);

        ConfigurationCategory text = provider.getCategory("text_render", "Text render Settings", null);
        renderText = text.get("render_text", "Render text", null, Boolean.class, false);
        textFont = text.get("text_font", "Text font", null, String.class, "minecraft",
                renderText::getValue, false, 0);
        textColor = text.get("text_color", "Text color", null, EspColor.class, new EspColor(Color.WHITE, false, false),
                renderText::getValue, false, 0);
        textPts = text.get("text_pts", "Text pts", null, IntRange.class, new IntRange(9, 15, 1, 1),
                renderText::getValue, false, 0);
        textShadow = text.get("text_shadow", "Text shadow", null, Boolean.class, true);

        ConfigurationCategory render = provider.getCategory("render", "Render Settings", null);
        this.render = render.get("render", "Render", null, Boolean.class, true);
        renderMode = render.get("render_mode", "Render mode", null, RenderBuilder.Box.class, RenderBuilder.Box.BOTH,
                this.render::getValue, false, 0);
        outlineWidth = render.get("outline_width", "Outline width", null, DoubleRange.class,
                new DoubleRange(1.5, 5, 0.1, 0.1, 1),
                () -> renderMode.getValue() != RenderBuilder.Box.FILL, false, 0);
        unbreakColor = render.get("unbreak_color", "Unbreaked color", null, EspColor.class,
                new EspColor(new Color(255, 0, 0, 100), false, true),
                this.render::getValue, false, 0);
        breakColor = render.get("break_color", "Break color", null, EspColor.class,
                new EspColor(new Color(0, 255, 0, 100), false, true),
                this.render::getValue, false, 0);
        useAirColor = render.get("use_air_color", "Use air color", null, Boolean.class, true,
                this.render::getValue, false, 0);
        airColor = render.get("air_color", "Air color", null, EspColor.class,
                new EspColor(new Color(138, 43, 226, 100), false, true),
                () -> this.render.getValue() && useAirColor.getValue(), false, 0);
        shaderMode = render.get("shader_mode", "Shader mode", null, Boolean.class, false,
                this.render::getValue, false, 0);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
        mineDamage = 0;
        suffixText = "";
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
        sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, minePosition, EnumFacing.DOWN));
    }

    private float mineDamage;
    private BlockPos minePosition;

    private final double END_DAMAGE = 1;
    private int beforeSlot;

    private void rotateToMinePos() {
        float[] neededRotations = PositionUtils.getNeededRotations(getPlayer().getPositionVector());
        PositionUtils.require()
                .desyncMode(PositionMask.LOOK)
                .rotation(neededRotations[0], neededRotations[1]);
    }

    @EventHandler(timing = EventTiming.POST)
    public void onClientTick(ClientTickEvent e) {
        BlockPos breakingBlock = BlockUtils.getBreakingBlockPos();
        if (BlockUtils.getBlock(breakingBlock) == Blocks.BEDROCK) return;

        if (PlayerUtils.getDistance(breakingBlock) >= range.getValue().getCurrent()) {
            minePosition = null;
            suffixText = "OUR";
            return;
        } else {
            if (breakingBlock != minePosition) {
                mineDamage = 0;
                chatDebugLog("Reseted MineDamage. cause:Changed doing pos");
            }
            minePosition = breakingBlock;
        }

        if (rotation.getValue() && alwaysRotate.getValue()) {
            rotateToMinePos();
        }

        int nowSlotIndex = ItemSlot.current().getIndex();
        if (strictBreak.getValue() && nowSlotIndex != beforeSlot) {
            mineDamage = 0;
            chatDebugLog("Reseted MineDamage. cause:Changed slot");
        }
        beforeSlot = nowSlotIndex;

        AccessorPlayerControllerMP controller = (AccessorPlayerControllerMP) getController();

        if (once.getValue() && BlockUtils.isAir(getWorld(), minePosition)) {
            abortDig = true;
            sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, minePosition, EnumFacing.DOWN));
            minePosition = null;
        }

        // packet mine
        if (!BlockUtils.isAir(getWorld(), breakingBlock)) {
            if (rotation.getValue() && rotateOnBlockIsAvaiable.getValue())
                rotateToMinePos();

            if (packetMine.getValue() && mineDamage >= END_DAMAGE) {
                if (rotation.getValue() && rotateOnCanBreak.getValue())
                    rotateToMinePos();

                if (switching.getValue() == MiningMode.NONE) {
                    sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakingBlock, EnumFacing.DOWN));
                } else {
                    ItemSlot slot = InventoryUtils.findBestTool(false, true, getWorld().getBlockState(breakingBlock));
                    InventoryUtils.silentSwitch(switching.getValue() == MiningMode.PACKET, slot.getIndex(), () -> {
                        sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakingBlock, EnumFacing.DOWN));
                    });
                }
            }
        }

        if (resetDamage.getValue() && BlockUtils.isAir(getWorld(), breakingBlock)) {
            mineDamage = 0;
            abortDig = true;
            sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, minePosition, EnumFacing.DOWN));
            sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, minePosition, EnumFacing.DOWN));
        }

        float blockStrength = getBlockStrength(getWorld().getBlockState(breakingBlock), breakingBlock);
        if (!Float.isInfinite(blockStrength))
            mineDamage += blockStrength;
        if (blockStrength == 0)
            suffixText = "0 strength";

        if (range.getValue().getCurrent() != 5 && ((AccessorPlayerControllerMP) getController()).getBlockHitDelay() == 5) {
            controller.setBlockHitDelay(range.getValue().getCurrent());
        }
    }

    private boolean abortDig;

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketSend(PacketSendEvent e) {
        if (!antiAbort.getValue()) return;
        if (abortDig) {
            abortDig = false;
            return;
        }
        if (!(e.getPacket() instanceof CPacketPlayerDigging)) return;
        CPacketPlayerDigging packet = (CPacketPlayerDigging) e.getPacket();
        if (packet.getAction() != CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK) return;
        e.setCancelled(true);
    }

    private String suffixText = "";

    @Override
    public String getSuffix() {
        return suffixText;
    }

    @EventHandler(timing = EventTiming.POST)
    public void onWorldRender(WorldRenderEvent e) {
        boolean posIsNull = Objects.isNull(minePosition);
        if (posIsNull && !suffixText.equals("OUR")) suffixText = "";
        if (packetMine.getValue() && !posIsNull) {
            AxisAlignedBB box = getWorld().getBlockState(minePosition).getBoundingBox(getWorld(), minePosition);
            box = box.offset(minePosition).grow(0.002);

            EspColor color;
            float tmpDamage = mineDamage;
            boolean targetIsAir = BlockUtils.isAir(getWorld(), minePosition);
            if (tmpDamage >= END_DAMAGE || targetIsAir) {
                color = breakColor.getValue();
                suffixText = "Break";

                if (targetIsAir) {
                    tmpDamage = 1;
                    color = airColor.getValue();
                    suffixText = "Breaked";
                }
            } else {
                color = unbreakColor.getValue();
                suffixText = "" + String.valueOf(tmpDamage * 100).split("\\.")[0] + "%";
            }

            // box of the mine
            AxisAlignedBB mineBox = mc.world.getBlockState(minePosition).getSelectedBoundingBox(mc.world, minePosition);

            // center of the box
            Vec3d mineCenter = mineBox.getCenter();

            if (renderText.getValue()) {
                RenderUtils.drawText(minePosition, suffixText, textFont.getValue(), textColor.getValue(),
                        textPts.getValue().getCurrent(), textShadow.getValue());
            }

            // shrink
            AxisAlignedBB shrunkMineBox = new AxisAlignedBB(mineCenter.x, mineCenter.y, mineCenter.z, mineCenter.x, mineCenter.y, mineCenter.z);

            // draw box
            if (shaderMode.getValue()) {
                GlStateManager.disableDepth();
                RenderUtils.drawFilled(box, color.getCurrentColor());
                RenderUtils.drawOutline(box, color.getCurrentColor(), outlineWidth.getValue().getCurrent());
                GlStateManager.enableDepth();
            } else {
                GlStateManager.disableDepth();
                RenderUtils.drawBox(new RenderBuilder()
                        .position(shrunkMineBox.grow(((mineBox.minX - mineBox.maxX) * 0.5) * MathHelper.clamp(tmpDamage, 0, 1), ((mineBox.minY - mineBox.maxY) * 0.5) * MathHelper.clamp(tmpDamage, 0, 1), ((mineBox.minZ - mineBox.maxZ) * 0.5) * MathHelper.clamp(tmpDamage, 0, 1)))
                        .color(color.getCurrentColor())
                        .box(renderMode.getValue())
                        .setup()
                        .line((float) outlineWidth.getValue().getCurrent())
                        .cull(false)
                        .shade(false)
                        .alpha(false)
                        .depth(true)
                        .blend()
                        .texture()
                );
                GlStateManager.enableDepth();
            }
        }
    }

    @Override
    public String getDefaultName() {
        return "SpeedMine";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.WORLD;
    }

    /**
     * Searches the most efficient item for a specified position
     *
     * @param state The {@link IBlockState} position to find the most efficient item for
     * @return The most efficient item for the specified position
     */
    public ItemStack getEfficientItem(IBlockState state) {
        return InventoryUtils.findBestTool(false, true, state).getItemStack();
    }

    /**
     * Finds the block strength of a specified block
     *
     * @param state    The {@link IBlockState} block state of the specified block
     * @param position The {@link BlockPos} position of the specified block
     * @return The block strength of the specified block
     */
    public float getBlockStrength(IBlockState state, BlockPos position) {

        // the block's hardness
        float hardness = state.getBlockHardness(mc.world, position);

        // if the block is air, it has no strength
        if (hardness < 0) {
            return 0;
        }

        // verify if the player can harvest the block
        if (!canHarvestBlock(state.getBlock(), position)) {
            return getDigSpeed(state) / hardness / 100F;
        }

        // find the dig speed if the player can't harvest the block
        else {
            return getDigSpeed(state) / hardness / 30F;
        }
    }

    /**
     * Check whether a specified block can be harvested
     *
     * @param block    The {@link Block} block to check
     * @param position The {@link BlockPos} position of the block to check
     * @return Whether the block can be harvested
     */
    @SuppressWarnings("deprecation")
    public boolean canHarvestBlock(Block block, BlockPos position) {

        // get the state of the block
        IBlockState worldState = mc.world.getBlockState(position);
        IBlockState state = worldState.getBlock().getActualState(worldState, mc.world, position);

        // if a tool is not required to harvest the block then we don't need to find the item
        if (state.getMaterial().isToolNotRequired()) {
            return true;
        }

        // find the item and get its harvest tool
        ItemStack stack = getEfficientItem(state);
        String tool = block.getHarvestTool(state);

        // if the tool exists, then verify if the player can harvest the block
        if (stack.isEmpty() || tool == null) {
            return mc.player.canHarvestBlock(state);
        }

        // find the tool's harvest level
        int toolLevel = stack.getItem().getHarvestLevel(stack, tool, mc.player, state);
        if (toolLevel < 0) {
            return mc.player.canHarvestBlock(state);
        }

        // verify if the tool's harvest level is greater than the block's harvest level
        return toolLevel >= block.getHarvestLevel(state);
    }

    /**
     * Finds the dig speed of a specified block
     *
     * @param state {@link IBlockState} The block state of the specified block
     * @return The dig speed of the specified block
     */
    @SuppressWarnings("all")
    public float getDigSpeed(IBlockState state) {

        // base dig speed
        float digSpeed = getDestroySpeed(state);

        if (digSpeed > 1) {
            ItemStack itemstack = getEfficientItem(state);

            // efficiency level
            int efficiencyModifier = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, itemstack);

            // scale by efficiency level
            if (efficiencyModifier > 0 && !itemstack.isEmpty()) {
                digSpeed += StrictMath.pow(efficiencyModifier, 2) + 1;
            }
        }

        // scaled based on haste effect level
        if (mc.player.isPotionActive(MobEffects.HASTE)) {
            digSpeed *= 1 + (mc.player.getActivePotionEffect(MobEffects.HASTE).getAmplifier() + 1) * 0.2F;
        }

        if (mc.player.isPotionActive(MobEffects.MINING_FATIGUE)) {

            // scale based on fatigue effect level
            float fatigueScale;
            switch (mc.player.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier()) {
                case 0:
                    fatigueScale = 0.3F;
                    break;
                case 1:
                    fatigueScale = 0.09F;
                    break;
                case 2:
                    fatigueScale = 0.0027F;
                    break;
                case 3:
                default:
                    fatigueScale = 8.1E-4F;
            }

            digSpeed *= fatigueScale;
        }

        // reduce dig speed if the player is in water
        if (mc.player.isInsideOfMaterial(Material.WATER) && !EnchantmentHelper.getAquaAffinityModifier(mc.player)) {
            digSpeed /= 5;
        }

        // reduce dig speed if the player is not on the ground
        if (!mc.player.onGround) {
            digSpeed /= 5;
        }

        return (digSpeed < 0 ? 0 : digSpeed);
    }

    /**
     * Finds the destroy speed of a specified position
     *
     * @param state {@link IBlockState} The position to get to destroy speed for
     * @return To destroy speed of the specified position
     */
    public float getDestroySpeed(IBlockState state) {

        // base destroy speed
        float destroySpeed = 1;

        // scale by the item's destroy speed
        if (getEfficientItem(state) != null && !getEfficientItem(state).isEmpty()) {
            destroySpeed *= getEfficientItem(state).getDestroySpeed(state);
        }

        return destroySpeed;
    }
}
