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
