package net.sushiclient.client;

import net.sushiclient.client.account.MojangAccounts;
import net.sushiclient.client.gui.theme.Theme;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;

public class Sushi {
    private static Profile profile;
    private static Profiles profiles;
    private static Theme defaultTheme;
    private static final List<Theme> themes = new ArrayList<>();
    private static MojangAccounts mojangAccounts;

    public static final org.apache.logging.log4j.Logger log4j = LogManager.getLogger(ModInformation.name);

    public static int getVersion() {
        return 0;
    }

    public static Profile getProfile() {
        return profile;
    }

    public static void setProfile(Profile profile) {
        Sushi.profile = profile;
    }

    public static Profiles getProfiles() {
        return profiles;
    }

    public static void setProfiles(Profiles profiles) {
        Sushi.profiles = profiles;
    }

    public static Theme getDefaultTheme() {
        return defaultTheme;
    }

    public static void setDefaultTheme(Theme defaultTheme) {
        Sushi.defaultTheme = defaultTheme;
    }

    public static List<Theme> getThemes() {
        return new ArrayList<>(themes);
    }

    public static void setThemes(List<Theme> themes) {
        Sushi.themes.clear();
        Sushi.themes.addAll(themes);
    }

    public static MojangAccounts getMojangAccounts() {
        return mojangAccounts;
    }

    public static void setMojangAccounts(MojangAccounts mojangAccounts) {
        Sushi.mojangAccounts = mojangAccounts;
    }
}
