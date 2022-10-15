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

package net.sushiclient.client.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.*;
import net.sushiclient.client.config.data.EspColor;
import net.sushiclient.client.mixin.AccessorEntityRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector4f;

import java.awt.*;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP;

public class RenderUtils {
    public static Tessellator tessellator = Tessellator.getInstance();

    private static final Matrix4f modelView = new Matrix4f();
    private static final Matrix4f projection = new Matrix4f();
    private static Vec3d interpolated;
    private static Vec3d cameraPos;
    private static Vec3d viewerPos;

    public static void tick() {
        // matrix
        FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, buffer);
        modelView.load(buffer);
        buffer.clear();
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, buffer);
        projection.load(buffer);

        //interpolated
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        interpolated = getInterpolatedPos(player);
        cameraPos = getInterpolatedPos().add(ActiveRenderInfo.getCameraPosition());
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        viewerPos = new Vec3d(renderManager.viewerPosX, renderManager.viewerPosY, renderManager.viewerPosZ);
    }

    private static Vector4f toVec4f(Vec3d vec) {
        return new Vector4f((float) vec.x, (float) vec.y, (float) vec.z, 1);
    }

    private static void mulMVP(Vector4f vec) {
        Matrix4f.transform(modelView, vec, vec);
        Matrix4f.transform(projection, vec, vec);
    }

    public static double getScale(Vec3d pos) {
        Minecraft client = Minecraft.getMinecraft();
        double rad = Math.toRadians(((AccessorEntityRenderer) client.entityRenderer).invokeGetFOVModifier(client.getRenderPartialTicks(), true));
        return 1 / (rad * MathHelper.sqrt(pos.subtract(getViewerPos()).squareDistanceTo(Vec3d.ZERO)));
    }

    public static Vec2f fromWorld(Vec3d pos) {
        Vector4f vec = toVec4f(pos.subtract(getViewerPos()));
        mulMVP(vec);
        if (vec.w < 0) return null; // invisible area
        vec.x *= 1 / vec.w;
        vec.y *= 1 / vec.w;
        int width = GuiUtils.getWidth();
        int height = GuiUtils.getHeight();
        vec.x = width / 2F + (0.5F * vec.x * width + 0.5F);
        vec.y = height / 2F - (0.5F * vec.y * height + 0.5F);
        return new Vec2f(vec.x, vec.y);
    }

    public static void prepare3D() {
        GlStateManager.disableTexture2D();
        GlStateManager.enableCull();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL_DEPTH_CLAMP);
    }

    public static void release3D() {
        GlStateManager.enableTexture2D();
        GlStateManager.disableCull();
        GL11.glDisable(GL_DEPTH_CLAMP);
    }

    public static Vec3d getInterpolatedPos(Entity entity) {
        float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;
        return new Vec3d(x, y, z);
    }

    public static Vec3d getInterpolatedPos() {
        return interpolated;
    }

    public static Vec3d getCameraPos() {
        return cameraPos;
    }

    public static Vec3d getViewerPos() {
        return viewerPos;
    }

    private static void vertex(BufferBuilder builder, double x, double y, double z) {
        builder.pos(x, y, z).endVertex();
    }

    public static void drawLine(Vec3d from, Vec3d to, Color color, double width) {
        GlStateManager.glLineWidth((float) width);
        GuiUtils.setColor(color);
        prepare3D();
        Vec3d d = getViewerPos();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        vertex(builder, from.x - d.x, from.y - d.y, from.z - d.z);
        vertex(builder, to.x - d.x, to.y - d.y, to.z - d.z);
        tessellator.draw();
        release3D();
    }

    public static void drawOutline(AxisAlignedBB box, Color color, double width) {
        GlStateManager.glLineWidth((float) width);
        GuiUtils.setColor(color);
        prepare3D();
        Vec3d d = getViewerPos();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        vertex(builder, box.minX - d.x, box.minY - d.y, box.minZ - d.z);
        vertex(builder, box.minX - d.x, box.minY - d.y, box.maxZ - d.z);
        vertex(builder, box.maxX - d.x, box.minY - d.y, box.maxZ - d.z);
        vertex(builder, box.maxX - d.x, box.minY - d.y, box.minZ - d.z);
        vertex(builder, box.minX - d.x, box.minY - d.y, box.minZ - d.z);
        vertex(builder, box.minX - d.x, box.maxY - d.y, box.minZ - d.z);
        vertex(builder, box.minX - d.x, box.maxY - d.y, box.maxZ - d.z);
        vertex(builder, box.minX - d.x, box.minY - d.y, box.maxZ - d.z);
        vertex(builder, box.maxX - d.x, box.minY - d.y, box.maxZ - d.z);
        vertex(builder, box.maxX - d.x, box.maxY - d.y, box.maxZ - d.z);
        vertex(builder, box.minX - d.x, box.maxY - d.y, box.maxZ - d.z);
        vertex(builder, box.maxX - d.x, box.maxY - d.y, box.maxZ - d.z);
        vertex(builder, box.maxX - d.x, box.maxY - d.y, box.minZ - d.z);
        vertex(builder, box.maxX - d.x, box.minY - d.y, box.minZ - d.z);
        vertex(builder, box.maxX - d.x, box.maxY - d.y, box.minZ - d.z);
        vertex(builder, box.minX - d.x, box.maxY - d.y, box.minZ - d.z);
        tessellator.draw();
        release3D();
    }

    public static void drawFilled(AxisAlignedBB box, Color color) {
        GuiUtils.setColor(color);
        prepare3D();
        Vec3d d = getViewerPos();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION);
        vertex(builder, box.maxX - d.x, box.maxY - d.y, box.maxZ - d.z);
        vertex(builder, box.maxX - d.x, box.maxY - d.y, box.minZ - d.z);
        vertex(builder, box.minX - d.x, box.maxY - d.y, box.maxZ - d.z);
        vertex(builder, box.minX - d.x, box.maxY - d.y, box.minZ - d.z);
        vertex(builder, box.minX - d.x, box.minY - d.y, box.maxZ - d.z);
        vertex(builder, box.minX - d.x, box.minY - d.y, box.minZ - d.z);
        vertex(builder, box.maxX - d.x, box.minY - d.y, box.maxZ - d.z);
        vertex(builder, box.maxX - d.x, box.minY - d.y, box.minZ - d.z);
        tessellator.draw();
        builder.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION);
        vertex(builder, box.minX - d.x, box.minY - d.y, box.minZ - d.z);
        vertex(builder, box.minX - d.x, box.maxY - d.y, box.minZ - d.z);
        vertex(builder, box.maxX - d.x, box.minY - d.y, box.minZ - d.z);
        vertex(builder, box.maxX - d.x, box.maxY - d.y, box.minZ - d.z);
        vertex(builder, box.maxX - d.x, box.minY - d.y, box.maxZ - d.z);
        vertex(builder, box.maxX - d.x, box.maxY - d.y, box.maxZ - d.z);
        vertex(builder, box.minX - d.x, box.minY - d.y, box.maxZ - d.z);
        vertex(builder, box.minX - d.x, box.maxY - d.y, box.maxZ - d.z);
        tessellator.draw();
        release3D();
    }

    public static void drawBox(RenderBuilder renderBuilder) {
        final Minecraft mc = Minecraft.getMinecraft();

        // check if the viewing entity exists
        if (mc.getRenderViewEntity() != null) {
            // draw box
            switch (renderBuilder.getBox()) {
                case FILL:
                    drawFilled(renderBuilder.getAxisAlignedBB(), renderBuilder.getColor());
                    break;
                case OUTLINE:
                    drawOutline(renderBuilder.getAxisAlignedBB(), renderBuilder.getColor(), renderBuilder.getWidth());
                    break;
                case BOTH:
                    drawFilled(renderBuilder.getAxisAlignedBB(), renderBuilder.getColor());
                    drawOutline(renderBuilder.getAxisAlignedBB(), renderBuilder.getColor(), renderBuilder.getWidth());
                    break;
            }

            // build the render
            renderBuilder.build();
        }
    }

    public static void drawText(BlockPos pos, String text, String font, EspColor color, int pts, boolean shadow) {
        Minecraft mc = Minecraft.getMinecraft();
        if (pos == null || text == null) {
            return;
        }
        GlStateManager.pushMatrix();
        glBillboardDistanceScaled((float) pos.getX() + 0.5f, (float) pos.getY() + 0.5f, (float) pos.getZ() + 0.5f, mc.player, 1.0f);
        GlStateManager.disableDepth();
        GlStateManager.translate(-(getTextWidth(text, font, color, pts, shadow) / 2.0), 0.0, 0.0);
        TextPreview preview = GuiUtils.prepareText(text, new TextSettings(font, color, pts, shadow));
        preview.draw(0, 0);
        GlStateManager.popMatrix();
    }

    private static double getTextWidth(String string, String font, EspColor color, int pts, boolean shadow) {
        TextPreview preview = GuiUtils.prepareText(string,
                new TextSettings(font, color, pts, shadow));
        return preview.getWidth();
    }

    public static void glBillboardDistanceScaled(float x, float y, float z, EntityPlayer player, float scale) {
        glBillboard(x, y, z);
        int distance = (int) player.getDistance(x, y, z);
        float scaleDistance = (float) distance / 2.0f / (2.0f + (2.0f - scale));
        if (scaleDistance < 1.0f) {
            scaleDistance = 1.0f;
        }
        GlStateManager.scale(scaleDistance, scaleDistance, scaleDistance);
    }

    public static void glBillboard(float x, float y, float z) {
        Minecraft mc = Minecraft.getMinecraft();
        float scale = 0.02666667f;
        GlStateManager.translate((double) x - mc.getRenderManager().viewerPosX, (double) y - mc.getRenderManager().viewerPosY, (double) z - mc.getRenderManager().viewerPosZ);
        GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-mc.player.rotationYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(mc.player.rotationPitch, mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-scale, -scale, scale);
    }

    public static void drawGradient(double x, double y, double x2, double y2, int col1, int col2) {
        float f = (col1 >> 24 & 0xFF) / 255.0F;
        float f1 = (col1 >> 16 & 0xFF) / 255.0F;
        float f2 = (col1 >> 8 & 0xFF) / 255.0F;
        float f3 = (col1 & 0xFF) / 255.0F;
        float f4 = (col2 >> 24 & 0xFF) / 255.0F;
        float f5 = (col2 >> 16 & 0xFF) / 255.0F;
        float f6 = (col2 >> 8 & 0xFF) / 255.0F;
        float f7 = (col2 & 0xFF) / 255.0F;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y);
        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
        GL11.glColor4d(1.0D, 1.0D, 1.0D, 1.0D);
    }

    public static void drawRect(float left, float top, float right, float bottom, Color color) {
        float alpha = color.getAlpha() / 255.0f;
        float red = color.getRed() / 255.0f;
        float green = color.getGreen() / 255.0f;
        float blue = color.getBlue() / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(left, bottom, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(right, bottom, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(right, top, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(left, top, 0.0).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawBorderRect(double x, double y, double x1, double y1, int color, double lwidth) {
        drawHLine(x, y, x1, y, (float) lwidth, color);
        drawHLine(x1, y, x1, y1, (float) lwidth, color);
        drawHLine(x, y1, x1, y1, (float) lwidth, color);
        drawHLine(x, y1, x, y, (float) lwidth, color);
    }

    public static void drawHLine(double x, double y, double x1, double y1, float width, int color) {
        float var11 = (color >> 24 & 0xFF) / 255.0F;
        float var6 = (color >> 16 & 0xFF) / 255.0F;
        float var7 = (color >> 8 & 0xFF) / 255.0F;
        float var8 = (color & 0xFF) / 255.0F;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(var6, var7, var8, var11);
        GL11.glPushMatrix();
        GL11.glLineWidth(width);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x1, y1);
        GL11.glEnd();
        GL11.glLineWidth(1);
        GL11.glPopMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1, 1, 1, 1);
    }
}
