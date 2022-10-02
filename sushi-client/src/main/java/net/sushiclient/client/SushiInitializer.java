package net.sushiclient.client;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.sushiclient.client.account.EncryptedMojangAccounts;
import net.sushiclient.client.account.MojangAccounts;
import net.sushiclient.client.command.Commands;
import net.sushiclient.client.command.client.*;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.config.GsonRootConfigurations;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.client.WorldLoadEvent;
import net.sushiclient.client.gui.theme.Theme;
import net.sushiclient.client.gui.theme.simple.SimpleTheme;
import net.sushiclient.client.handlers.*;
import net.sushiclient.client.handlers.forge.*;
import net.sushiclient.client.utils.gson.ColorTypeAdapter;
import net.sushiclient.client.utils.gson.EnumFactory;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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

    @Override
    public void init(FMLInitializationEvent event) {
        // load config
        try {
            String contents = FileUtils.readFileToString(modConfigFile, StandardCharsets.UTF_8);
            modConfig = gson.fromJson(contents, ModConfig.class);
        } catch (IOException e) {
            modConfig = new ModConfig();
        }

        // add themes
        ArrayList<Theme> themes = new ArrayList<>();
        Theme fallbackTheme = loadTheme(new File(themeDir, "simple.json"), SimpleTheme::new);
        themes.add(fallbackTheme);

        for (Theme theme : Sushi.getThemes()) {
            if (theme.getId().equals(modConfig.getTheme())) {
                fallbackTheme = theme;
                break;
            }
        }
        Sushi.setThemes(themes);
        Sushi.setDefaultTheme(fallbackTheme);

        // load profile
        GsonProfiles profiles = new GsonProfiles(new File(baseDir, "profiles"), gson);
        Profile profile = profiles.load(modConfig.getName());
        Sushi.setProfiles(profiles);
        Sushi.setProfile(profile);
        profile.load();

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
        Commands.register(this, new SetCommand());
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
