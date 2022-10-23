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

package net.sushiclient.client.modules.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.EspColor;
import net.sushiclient.client.config.data.Named;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.EntityUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class PlayerEspModule extends BaseModule {
    private interface PlayerESP {
        void render(EntityPlayer player, boolean fill, boolean outline, Color fillColor, Color outlineColor);
        void unRender(EntityPlayer player, boolean fill, boolean outline, Color fillColor, Color outlineColor);
    }

    public enum PlayerEspModes implements PlayerESP, Named {
        Glow("Glow") {
            @Override
            public void render(EntityPlayer player, boolean fill, boolean outline, Color fillColor, Color outlineColor) {
                player.setGlowing(true);
            }

            @Override
            public void unRender(EntityPlayer player, boolean fill, boolean outline, Color fillColor, Color outlineColor) {
                player.setGlowing(false);
            }
        },
        Outline("Outline") {
            @Override
            public void render(EntityPlayer player, boolean fill, boolean outline, Color fillColor, Color outlineColor) {
                ModelPlayer playerModel = new ModelPlayer(0.0f, false);
                playerModel.bipedHead.showModel = false;
                playerModel.bipedBody.showModel = false;
                playerModel.bipedLeftArmwear.showModel = false;
                playerModel.bipedLeftLegwear.showModel = false;
                playerModel.bipedRightArmwear.showModel = false;
                playerModel.bipedRightLegwear.showModel = false;

                prepareGL();
                GL11.glPushAttrib(1048575);
                GL11.glEnable(2881);
                GL11.glEnable(2848);

                Color fillFinal;
                if (fill)
                    fillFinal = fillColor;
                else
                    fillFinal = new Color(0, 0, 0, 0);

                Color outlineFinal;
                if (outline)
                    outlineFinal = outlineColor;
                else
                    outlineFinal = new Color(0, 0, 0, 0);
                glColor(fillFinal);
                GL11.glPolygonMode(1032, 6914);
                renderEntity(mc.player, playerModel, mc.player.limbSwing, mc.player.limbSwingAmount, (float)mc.player.ticksExisted, mc.player.rotationYawHead, mc.player.rotationPitch, 1);
                glColor(outlineFinal);
                GL11.glPolygonMode(1032, 6913);
                renderEntity(mc.player, playerModel, mc.player.limbSwing, mc.player.limbSwingAmount, (float)mc.player.ticksExisted, mc.player.rotationYawHead, mc.player.rotationPitch, 1);
                GL11.glPolygonMode(1032, 6914);
                GL11.glPopAttrib();
                releaseGL();
            }

            @Override
            public void unRender(EntityPlayer player, boolean fill, boolean outline, Color fillColor, Color outlineColor) {

            }
        }
        ;

        public static void glColor(final Color color) {
            GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        }

        public static void releaseGL() {
            GlStateManager.enableCull();
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.enableDepth();
        }

        public static void prepareGL() {
            GL11.glBlendFunc(770, 771);
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.glLineWidth(1.5f);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.enableAlpha();
            GlStateManager.color(1.0f, 1.0f, 1.0f);
        }

        private final String name;
        PlayerEspModes(String name) {
            this.name = name;
        }
        @Override
        public String getName() {
            return name;
        }

        private static final Minecraft mc = Minecraft.getMinecraft();

        public static void renderEntity(final EntityLivingBase entity, final ModelBase modelBase, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale) {
            mc.getRenderManager();
            final float partialTicks = mc.getRenderPartialTicks();
            final double x = entity.posX - mc.getRenderManager().viewerPosX;
            double y = entity.posY - mc.getRenderManager().viewerPosY;
            final double z = entity.posZ - mc.getRenderManager().viewerPosZ;
            GlStateManager.pushMatrix();
            if (entity.isSneaking()) {
                y -= 0.125;
            }
            final float interpolateRotation = interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
            final float interpolateRotation2 = interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
            final float rotationInterp = interpolateRotation2 - interpolateRotation;
            final float renderPitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
            renderLivingAt(x, y, z);
            final float f8 = handleRotationFloat(entity, partialTicks);
            prepareRotations(entity);
            final float f9 = prepareScale(entity, scale);
            GlStateManager.enableAlpha();
            modelBase.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
            modelBase.setRotationAngles(limbSwing, limbSwingAmount, f8, entity.rotationYawHead, entity.rotationPitch, f9, entity);
            modelBase.render(entity, limbSwing, limbSwingAmount, f8, entity.rotationYawHead, entity.rotationPitch, f9);
            GlStateManager.popMatrix();
        }

        public static float handleRotationFloat(final EntityLivingBase livingBase, final float partialTicks) {
            return 0.0f;
        }

        public static void renderLivingAt(final double x, final double y, final double z) {
            GlStateManager.translate((float)x, (float)y, (float)z);
        }

        public static float prepareScale(final EntityLivingBase entity, final float scale) {
            GlStateManager.enableRescaleNormal();
            GlStateManager.scale(-1.0f, -1.0f, 1.0f);
            final double widthX = entity.getRenderBoundingBox().maxX - entity.getRenderBoundingBox().minX;
            final double widthZ = entity.getRenderBoundingBox().maxZ - entity.getRenderBoundingBox().minZ;
            GlStateManager.scale(scale + widthX, scale * entity.height, scale + widthZ);
            final float f = 0.0625f;
            GlStateManager.translate(0.0f, -1.501f, 0.0f);
            return f;
        }

        public static void prepareRotations(final EntityLivingBase entityLivingBase) {
            GlStateManager.rotate(180.0f - entityLivingBase.rotationYaw, 0.0f, 1.0f, 0.0f);
        }

        public static float interpolateRotation(final float prevYawOffset, final float yawOffset, final float partialTicks) {
            float f;
            for (f = yawOffset - prevYawOffset; f < -180.0f; f += 360.0f) {}
            while (f >= 180.0f) {
                f -= 360.0f;
            }
            return prevYawOffset + partialTicks * f;
        }
    }


    private final Configuration<PlayerEspModes> mode;
    private final Configuration<EspColor> color;
    private final Configuration<Boolean> outline;
    private final Configuration<Boolean> fill;
    public PlayerEspModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        mode = provider.get("mode", "Mode", null, PlayerEspModes.class, PlayerEspModes.Glow);
        color = provider.get("color", "Color", null, EspColor.class,
                new EspColor(new Color(255, 0, 0, 255), false, true));
        outline = provider.get("outline", "outline", null, Boolean.class, true);
        fill = provider.get("fill", "fill", null, Boolean.class, true);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
        for (EntityPlayer player : EntityUtils.getNearbyPlayers(Double.MAX_VALUE)) {
            mode.getValue().unRender(player, fill.getValue(), outline.getValue(), color.getValue().getCurrentColor(), color.getValue().getCurrentColor());
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        for (EntityPlayer player : EntityUtils.getNearbyPlayers(Double.MAX_VALUE)) {
            mode.getValue().render(player, fill.getValue(), outline.getValue(), color.getValue().getCurrentColor(), color.getValue().getCurrentColor());
        }
    }

    @Override
    public String getDefaultName() {
        return "PlayerESP";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.RENDER;
    }
}
