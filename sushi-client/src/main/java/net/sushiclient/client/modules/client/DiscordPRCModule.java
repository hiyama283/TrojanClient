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

package net.sushiclient.client.modules.client;

import club.minnced.discord.rpc.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.sushiclient.client.ModInformation;
import net.sushiclient.client.Sushi;
import net.sushiclient.client.command.LogLevel;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.modules.*;

import java.util.Objects;

public class DiscordPRCModule extends BaseModule {
    private final DiscordRPC rpc = DiscordRPC.INSTANCE;
    private Thread thread;
    private final Configuration<Boolean> showIP;
    private final Configuration<String> state;

    public DiscordPRCModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        showIP = provider.get("show_ip", "Show ip", null, Boolean.class, true);
        state = provider.get("state", "State", null, String.class, "Best assist client.");
    }

    @Override
    public void onEnable() {
        DiscordRPC lib = DiscordRPC.INSTANCE;
        String applicationId = "737779695134834696";
        String steamId = "";
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = (user) -> Sushi.getProfile().getLogger().send(LogLevel.INFO, "RPC Ready");
        lib.Discord_Initialize(applicationId, handlers, true, steamId);
        DiscordRichPresence presence = new DiscordRichPresence();
        presence.startTimestamp = System.currentTimeMillis() / 1000; // epoch second
        presence.details = Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu ? "In the main menu." :
                "Playing " + (Minecraft.getMinecraft().getCurrentServerData() != null ?
                        (showIP.getValue() ? "on " + Objects.requireNonNull(Minecraft.getMinecraft().getCurrentServerData())
                                .serverIP + "." : " multiplayer.") : " singleplayer.");
        presence.state = state.getValue();
        presence.smallImageText = ModInformation.name + "-" + ModInformation.version;
        presence.largeImageText = ModInformation.name + "-" + ModInformation.version;
        lib.Discord_UpdatePresence(presence);
        // in a worker thread
        thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                lib.Discord_RunCallbacks();

                presence.details = Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu ? "In the main menu." :
                        "Playing " + (Minecraft.getMinecraft().getCurrentServerData() != null ?
                                (showIP.getValue() ? "on " + Objects.requireNonNull(Minecraft.getMinecraft().getCurrentServerData())
                                        .serverIP + "." : " multiplayer.") : " singleplayer.");
                presence.state = state.getValue();
                presence.smallImageText = ModInformation.name + "-" + ModInformation.version;
                presence.largeImageText = ModInformation.name + "-" + ModInformation.version;
                rpc.Discord_UpdatePresence(presence);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
            }
        }, "RPC-Callback-Handler");
        thread.start();
    }

    @Override
    public void onDisable() {
        if (thread != null && !thread.isInterrupted())
        {
            thread.interrupt();
            thread = null;
        }

        rpc.Discord_Shutdown();
    }

    @Override
    public String getDefaultName() {
        return "RPC";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.CLIENT;
    }
}
