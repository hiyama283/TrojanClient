package net.sushiclient.client.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.mixin.AccessorEntityRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector4f;

import java.awt.*;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP;

public class RenderUtils {

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
        builder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
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

            // render bounding box
            AxisAlignedBB axisAlignedBB = renderBuilder.getAxisAlignedBB()
                    .offset(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ);

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
}
