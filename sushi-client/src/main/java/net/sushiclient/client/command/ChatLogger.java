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

package net.sushiclient.client.command;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.sushiclient.client.ModInformation;

import static net.minecraft.util.text.TextFormatting.*;

public class ChatLogger implements Logger {
    @Override
    public void send(LogLevel level, String message) {
        TextFormatting color;
        if (level == LogLevel.WARN) color = YELLOW;
        else if (level == LogLevel.ERROR) color = RED;
        else color = WHITE;

        String txt = GRAY + "[" + AQUA + ModInformation.name + "-" + ModInformation.version + GRAY + "] " + color + message;
        Minecraft.getMinecraft().ingameGUI.addChatMessage(ChatType.CHAT, new TextComponentString(txt));
    }
}
