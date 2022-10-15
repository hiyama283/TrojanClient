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

package net.sushiclient.client;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.*;
import net.sushiclient.client.account.EncryptedMojangAccounts;
import net.sushiclient.client.account.MojangAccounts;
import net.sushiclient.client.command.Commands;
import net.sushiclient.client.command.client.*;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.config.GsonRootConfigurations;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.client.WorldLoadEvent;
import net.sushiclient.client.gui.font.FontManager;
import net.sushiclient.client.gui.hud.elements.TextRaderComponent;
import net.sushiclient.client.gui.theme.Theme;
import net.sushiclient.client.gui.theme.simple.SimpleTheme;
import net.sushiclient.client.handlers.*;
import net.sushiclient.client.handlers.forge.*;
import net.sushiclient.client.utils.HWID;
import net.sushiclient.client.utils.gson.ColorTypeAdapter;
import net.sushiclient.client.utils.gson.EnumFactory;
import org.apache.commons.io.FileUtils;
import org.lwjgl.opengl.Display;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SushiInitializer implements Initializer {

    private static final Gson gson;

    static {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapterFactory(new EnumFactory())
                .registerTypeAdapter(Color.class, new ColorTypeAdapter())
                .create();
    }

    private final File baseDir = new File("./sushi");
    private final File modConfigFile = new File(baseDir, "config.json");
    private final File themeDir = new File(baseDir, "themes");
    private ModConfig modConfig;
    private final HashMap<File, GsonRootConfigurations> configs = new HashMap<>();

    private Theme loadTheme(File file, Function<Configurations, Theme> func) {
        JsonObject object = null;
        try {
            if (file.exists())
                object = gson.fromJson(FileUtils.readFileToString(file, StandardCharsets.UTF_8), JsonObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (object == null)
            object = new JsonObject();
        GsonRootConfigurations conf = new GsonRootConfigurations(gson);
        conf.load(object);
        configs.put(file, conf);

        return func.apply(conf);
    }

    private void hwidProtection(String url, String title, String message) {
        String s1 = HWID.getHWID();

        try (InputStream in = new URL(url).openStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(Objects.requireNonNull(in));
            Stream<String> streamOfString = new BufferedReader(inputStreamReader).lines();
            String response = streamOfString.collect(Collectors.joining("\n"));

            if (!(response.contains(s1))) {
                Minecraft mc = Minecraft.getMinecraft();
                mc.shutdown();

                UIManager.put("OptionPane.minimumSize", new Dimension(500, 80));
                JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void construct(FMLConstructionEvent event) {
        Sushi.log4j.info("Construct init");
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        Sushi.log4j.info("Pre init");
    }

    @Override
    public void init(FMLInitializationEvent event) {
        Sushi.log4j.info("init");
        // load config
        try {
            String contents = FileUtils.readFileToString(modConfigFile, StandardCharsets.UTF_8);
            modConfig = gson.fromJson(contents, ModConfig.class);
        } catch (IOException e) {
            modConfig = new ModConfig();
        }
        Sushi.log4j.info("Config loaded");

        // add themes
        ArrayList<Theme> themes = new ArrayList<>();
        Theme fallbackTheme = loadTheme(new File(themeDir, "simple.json"), SimpleTheme::new);
        themes.add(fallbackTheme);
        Sushi.log4j.info("Theme pre loaded");

        for (Theme theme : Sushi.getThemes()) {
            if (theme.getId().equals(modConfig.getTheme())) {
                fallbackTheme = theme;
                break;
            }
        }
        Sushi.setThemes(themes);
        Sushi.setDefaultTheme(fallbackTheme);
        Sushi.log4j.info("Theme loaded");

        // load profile
        GsonProfiles profiles = new GsonProfiles(new File(baseDir, "profiles"), gson);
        Profile profile = profiles.load(modConfig.getName());
        Sushi.setProfiles(profiles);
        Sushi.setProfile(profile);
        profile.load();
        Sushi.log4j.info("Loaded profile");

        // set accounts
        MojangAccounts accounts = new EncryptedMojangAccounts(new File(baseDir, "accounts.txt"));
        Sushi.setMojangAccounts(accounts);
        accounts.refreshAll();
        accounts.load();

        // register events
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        MinecraftForge.EVENT_BUS.register(new MouseInputHandler());
        MinecraftForge.EVENT_BUS.register(new ClientTickHandler());
        MinecraftForge.EVENT_BUS.register(new RenderTickHandler());
        MinecraftForge.EVENT_BUS.register(new ClientChatHandler());
        MinecraftForge.EVENT_BUS.register(new WorldRenderHandler());
        MinecraftForge.EVENT_BUS.register(new DrawBlockHighlightHandler());
        MinecraftForge.EVENT_BUS.register(new ChunkHandler());
        MinecraftForge.EVENT_BUS.register(new BlockLeftClickHandler());
        MinecraftForge.EVENT_BUS.register(new BlockOverlayRenderHandler());
        MinecraftForge.EVENT_BUS.register(new GameOverlayRenderHandler());
        MinecraftForge.EVENT_BUS.register(new GuiOpenHandler());
        EventHandlers.register(new KeyReleaseHandler());
        EventHandlers.register(new KeybindHandler());
        EventHandlers.register(new ComponentMouseHandler());
        EventHandlers.register(new ComponentRenderHandler());
        EventHandlers.register(new ComponentKeyHandler());
        EventHandlers.register(new GameFocusHandler());
        EventHandlers.register(new ConfigurationHandler());
        EventHandlers.register(new CommandHandler());
        EventHandlers.register(new DesyncHandler());
        EventHandlers.register(new TpsHandler());
        EventHandlers.register(new TickHandler());
        EventHandlers.register(new RenderUtilsHandler());
        EventHandlers.register(new BlockBreakHandler());
        EventHandlers.register(new SilentSwitchHandler());
        EventHandlers.register(new PositionPacketHandler());
        EventHandlers.register(this);
        Sushi.log4j.info("Handler registered");

        Commands.register(new HelpCommand());
        Commands.register(new ToggleCommand());
        Commands.register(new ProfileCommand());
        Commands.register(new PrefixCommand());
        Commands.register(new BindCommand());
        Commands.register(new DrawCommand());
        Commands.register(new GhostBlockCommand());
        Commands.register(new ChunkLoadCheckCommand());
        Commands.register(new RestoreCommand());
        Commands.register(new HClipCommand());
        Commands.register(new VClipCommand());
        Commands.register(new PeekCommand());
        Commands.register(this, new SetCommand());
        Sushi.log4j.info("Command registered");
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        Sushi.log4j.info("Post init");
    }

    @Override
    public void complete(FMLLoadCompleteEvent event) {
        Sushi.log4j.info("Completed!");
        Display.setTitle(ModInformation.name + "-" + ModInformation.version);
        Sushi.log4j.info("Title set");
    }

    @net.sushiclient.client.events.EventHandler(timing = EventTiming.PRE)
    public void onWorldLoad(WorldLoadEvent e) {
        if (e.getClient() != null) return;
        try {
            String profileName = Sushi.getProfiles().getName(Sushi.getProfile());
            if (profileName != null) modConfig.setName(profileName);
            modConfig.setTheme(Sushi.getDefaultTheme().getId());
            FileUtils.writeStringToFile(modConfigFile, gson.toJson(modConfig), StandardCharsets.UTF_8);
            for (Map.Entry<File, GsonRootConfigurations> entry : configs.entrySet()) {
                try {
                    FileUtils.writeStringToFile(entry.getKey(), gson.toJson(entry.getValue().save()), StandardCharsets.UTF_8);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
