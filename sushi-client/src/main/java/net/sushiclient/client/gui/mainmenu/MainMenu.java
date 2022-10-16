package net.sushiclient.client.gui.mainmenu;

import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.sushiclient.client.ModInformation;
import net.sushiclient.client.Sushi;
import net.sushiclient.client.gui.bgm.ChillThemeBGM;
import net.sushiclient.client.gui.mainmenu.particle.ParticleManager;
import org.lwjgl.opengl.GL11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.sushiclient.client.gui.font.FontManager.jelloFont;
import static net.sushiclient.client.gui.font.FontManager.jelloLargeFont;

public class MainMenu extends GuiScreen {
    private static Minecraft mc = Minecraft.getMinecraft();
    private static boolean soundPlayed = false;
    public static void playMusic(ISound sound) {
        if (!soundPlayed && !mc.getSoundHandler().isSoundPlaying(sound)) {
            mc.getSoundHandler().playSound(sound);
            soundPlayed = true;
        }
    }

    public static void stopMusic(ISound sound) {
        if (soundPlayed && mc.getSoundHandler().isSoundPlaying(sound)) {
            mc.getSoundHandler().stopSound(sound);
            soundPlayed = false;
        }
    }

    public static int background = 0;
    public static int backgroundSize = 6;

    static ChangeLog changeLog = new MainMenu.ChangeLog("0", "Null");

    static {
        String url = "https://api.github.com/repos/hiyama283/TrojanClient/releases/tags/" + ModInformation.version;

        InputStream in = null;
        try {
            in = new URL(url).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!Objects.isNull(in)) {
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            Stream<String> streamOfString = new BufferedReader(inputStreamReader).lines();
            String s = streamOfString.collect(Collectors.joining("\n"));

            Sushi.log4j.info(s);
            changeLog = new Gson().fromJson(s, ChangeLog.class);
        }
    }

    static class ChangeLog {
        public String tag_name;
        public String body;

        public ChangeLog(String tag_name, String body) {
            this.tag_name = tag_name;
            this.body = body;
        }
    }

    private final ResourceLocation background1, background2, background3, background4, background5, background6;

    private int animatedX, animatedY;
    private List<CustomButton> buttons;
    private ParticleManager pm;


    public MainMenu() {
        background1 = new ResourceLocation("sushi/background/mainmenu1.png");
        background2 = new ResourceLocation("sushi/background/mainmenu2.png");
        background3 = new ResourceLocation("sushi/background/mainmenu3.png");
        background4 = new ResourceLocation("sushi/background/mainmenu4.png");
        background5 = new ResourceLocation("sushi/background/mainmenu5.png");
        background6 = new ResourceLocation("sushi/background/mainmenu6.png");
        Minecraft.getMinecraft().gameSettings.setSoundLevel(SoundCategory.MUSIC, 0);
    }

    @Override
    public void initGui() {
        // MainMenu.playMusic(ChillThemeBGM.sound);

        buttons = new LinkedList<>();
        pm = new ParticleManager();
        buttons.add(new CustomButton("SinglePlayer", new ResourceLocation("sushi/icon/singleplayer.png"), new GuiWorldSelection(this)));
        buttons.add(new CustomButton("MultiPlayer", new ResourceLocation("sushi/icon/multiplayer.png"), new GuiMultiplayer(this)));
        buttons.add(new CustomButton("Language", new ResourceLocation("sushi/icon/language.png"), new GuiLanguage(this, mc.gameSettings, mc.getLanguageManager())));
        buttons.add(new CustomButton("Settings", new ResourceLocation("sushi/icon/setting.png"), new GuiOptions(this, mc.gameSettings)));
        buttons.add(new CustomButton("Quit", new ResourceLocation("sushi/icon/quit.png"), null));
        //buttons.add(new CustomButton("AltManager", new ResourceLocation("orangette/icon/altmanager.png"), null/*GuiAltManager.instance*/));
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ResourceLocation[] background = new ResourceLocation[]{background1, background2, background3, background4, background5, background6};
        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        ScaledResolution sr = new ScaledResolution(mc);

        ResourceLocation targetBackground;
        try {
            targetBackground = background[MainMenu.background];
        } catch (IndexOutOfBoundsException e) {
            targetBackground = background1;
        }
        mc.getTextureManager().bindTexture(targetBackground);
        drawModalRectWithCustomSizedTexture(-this.animatedX / 4, -this.animatedY / 3, 0, 0, sr.getScaledWidth() / 3 * 4, sr.getScaledHeight() / 3 * 4, sr.getScaledWidth() / 3 * 4, sr.getScaledHeight() / 3 * 4);
        // mc.getTextureManager().bindTexture(new ResourceLocation("sushi/logo.png"));
        // Gui.drawModalRectWithCustomSizedTexture(0, 0, 0F, 0F, 125, 49, 125, 49);
        int xOffset = sr.getScaledWidth() / 2 - 180;
        for (CustomButton cb : buttons) {
            cb.drawScreen(xOffset, sr.getScaledHeight() / 2 - 20, mouseX, mouseY);
            xOffset += 80;
        }
        drawCircle(0, 0, 5, -1);
        jelloLargeFont.drawString("Changelog", 4, 4, 0xc0ffffff);
        jelloLargeFont.drawString(" - " + ModInformation.version, 4, 15, 0xc0ffffff);

        int y = 26;
        for (String s : MainMenu.changeLog.body.split("\n")) {
            jelloLargeFont.drawString(s, 4, y, 0xc0ffffff);
            y += 11;
        }

        jelloLargeFont.drawString(ModInformation.name + " - " + ModInformation.version,
                ((sr.getScaledWidth() / 2) - 20), ((sr.getScaledHeight() / 2) - 50), 0xd0ffffff);
        jelloLargeFont.drawString("By Team shark", sr.getScaledWidth() - jelloLargeFont.getStringWidth("By Team shark") - 4,
                sr.getScaledHeight() - 12, 0xd0ffffff);
        super.drawScreen(mouseX, mouseY, partialTicks);
        pm.render(mouseX, mouseY, sr);
        animatedX += ((mouseX - animatedX) / 1.8) + 0.1;
        animatedY += ((mouseY - animatedY) / 1.8) + 0.1;
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.popMatrix();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (CustomButton cb : buttons) {
            cb.onClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    private class CustomButton {

        private final ResourceLocation resource;
        private final GuiScreen parent;
        private float animatedSize;
        private int posX, posY;
        private final String name;

        public CustomButton(String name, ResourceLocation resource, GuiScreen parent) {
            this.resource = resource;
            this.parent = parent;
            this.name = name;
            net.sushiclient.client.gui.font.FontManager.init();
        }

        public void drawScreen(int posX, int posY, int mouseX, int mouseY) {
            if (isMouseHovering(posX, posY, 48, 48, mouseX, mouseY)) {
                animatedSize = animate(animatedSize, 30);
                jelloFont.drawCenteredString(name, posX + 30, posY + 60, -1);
            } else animatedSize = animate(animatedSize, 25);
            GL11.glColor4f(1, 1, 1, 0.75f);
            mc.getTextureManager().bindTexture(resource);
            Gui.drawModalRectWithCustomSizedTexture(posX - (int) animatedSize / 2 + 25, posY - (int) animatedSize / 2 + 25, 0, 0, (int) (animatedSize * 1.5f), (int) (animatedSize * 1.5f), animatedSize * 1.5f, animatedSize * 1.5f);
            this.posX = posX;
            this.posY = posY;
        }

        public void onClicked(int mouseX, int mouseY, int mouseButton) {
            if (isMouseHovering(posX, posY, 48, 48, mouseX, mouseY)) {
                if (parent == null) mc.shutdown();
                mc.displayGuiScreen(parent);
            }
        }
    }

    private boolean isMouseHovering(float x, float y, float width, float height, int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
    }

    private float animate(final float target, float current) {
        if (Math.abs(current - target) < 0.6F) {
            return current;
        }
        float dif = Math.max(target, current) - Math.min(target, current);
        float factor = dif * 0.6F;
        if (factor < 0.1f) {
            factor = 0.1f;
        }
        if (target > current) {
            current += factor;
        } else {
            current -= factor;
        }
        return current;
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
