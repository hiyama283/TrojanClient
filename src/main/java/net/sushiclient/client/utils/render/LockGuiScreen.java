package net.sushiclient.client.utils.render;

import net.minecraft.client.gui.GuiScreen;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.render.GuiRenderEvent;
import net.sushiclient.client.utils.player.InputUtils;

class LockGuiScreen extends GuiScreen {

    private final GuiScreen parent;
    private final Runnable onClose;

    public LockGuiScreen(GuiScreen parent, Runnable onClose) {
        this.parent = parent;
        this.onClose = onClose;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        EventHandlers.callEvent(new GuiRenderEvent(EventTiming.PRE));
        super.drawScreen(mouseX, mouseY, partialTicks);
        EventHandlers.callEvent(new GuiRenderEvent(EventTiming.POST));
    }

    @Override
    public void handleKeyboardInput() {
        InputUtils.callKeyEvent();
    }

    @Override
    public void handleMouseInput() {
        InputUtils.callMouseEvent();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public GuiScreen getParent() {
        return parent;
    }

    @Override
    public void onGuiClosed() {
        onClose.run();
    }
}
