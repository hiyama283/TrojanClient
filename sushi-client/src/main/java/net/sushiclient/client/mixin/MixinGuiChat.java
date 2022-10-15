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

package net.sushiclient.client.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import net.sushiclient.client.Sushi;
import net.sushiclient.client.command.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(GuiChat.class)
public class MixinGuiChat {

    private static final int SHADOW_COLOR = new Color(200, 200, 200).getRGB();

    @Inject(at = @At("HEAD"), method = "drawScreen")
    public void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        GuiTextField inputField = ((AccessorGuiChat) this).getInputField();
        String text = inputField.getText();
        if (text.isEmpty()) return;
        if (text.charAt(0) != Sushi.getProfile().getPrefix()) return;
        String complete = Commands.complete(text.substring(1));
        Minecraft.getMinecraft().fontRenderer.drawString(Sushi.getProfile().getPrefix() + complete, inputField.x, inputField.y, SHADOW_COLOR);
    }
}