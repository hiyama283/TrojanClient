package net.sushiclient.client.gui.mainmenu.particle;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ParticleManager {

    private float lastMouseX, lastMouseY;
    private List<Particle> array;

    public ParticleManager() {
        array = new CopyOnWriteArrayList<>();
    }


    public void render(float mouseX, float mouseY, ScaledResolution sr) {
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.color(1, 1, 1, 1);
        float xOffset = sr.getScaledWidth() / 2 - mouseX;
        float yOffset = sr.getScaledHeight() / 2 - mouseY;
        for (array.size(); array.size() < (int) (sr.getScaledWidth() / 19.2f); array.add(new Particle(sr, RandomUtils.nextFloat() * 3 + 2, RandomUtils.nextFloat() * 5 + 5)))
            ;
        List<Particle> toremove = new ArrayList<>();
        for (Particle p : array) {
            if (p.opacity < 32) {
                p.opacity += 2;
            }
            if (p.opacity > 32) {
                p.opacity = 32;
            }
            Color c = new Color((int) 255, (int) 255, (int) 255, (int) p.opacity);
            drawCircle(p.x + Math.sin(p.ticks / 2) * 50 + -xOffset / 5, (p.ticks * p.speed) * p.ticks / 10 + -yOffset / 5, p.radius * (p.opacity / 32), -1);
            p.ticks += 0.05;// +(0.005*1.777*(GLUtils.getMouseX()-lastMouseX) + 0.005*(GLUtils.getMouseY()-lastMouseY));
            if (((p.ticks * p.speed) * p.ticks / 10 + -yOffset / 5) > sr.getScaledHeight() || ((p.ticks * p.speed) * p.ticks / 10 + -yOffset / 5) < 0 || (p.x + Math.sin(p.ticks / 2) * 50 + -xOffset / 5) > sr.getScaledWidth() || (p.x + Math.sin(p.ticks / 2) * 50 + -xOffset / 5) < 0) {
                toremove.add(p);
            }
        }

        array.removeAll(toremove);
        GlStateManager.color(1, 1, 1, 1);
        GL11.glColor4f(1, 1, 1, 1);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    private void drawCircle(double x, double y, float radius, int color) {
        float f = (color >> 24 & 0xFF) / 255.0f;
        float f2 = (color >> 16 & 0xFF) / 255.0f;
        float f3 = (color >> 8 & 0xFF) / 255.0f;
        float f4 = (color & 0xFF) / 255.0f;
        GL11.glColor4f(f2, f3, f4, f);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GL11.glDisable(3553);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.alphaFunc(516, 0.001f);
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder render = tess.getBuffer();
        for (double i = 0; i < 360; ++i) {
            double cs = i * 3.141592653589793 / 180.0;
            double ps = (i - 1.0) * 3.141592653589793 / 180.0;
            double[] outer = {Math.cos(cs) * radius, -Math.sin(cs) * radius, Math.cos(ps) * radius, -Math.sin(ps) * radius};
            render.begin(6, DefaultVertexFormats.POSITION);
            render.pos(x + outer[2], y + outer[3], 0.0).endVertex();
            render.pos(x + outer[0], y + outer[1], 0.0).endVertex();
            render.pos(x, y, 0.0).endVertex();
            tess.draw();
        }
        GlStateManager.resetColor();
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.disableAlpha();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(3553);
    }
}
