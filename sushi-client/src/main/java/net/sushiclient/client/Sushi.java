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

import net.sushiclient.client.account.MojangAccounts;
import net.sushiclient.client.events.EventManager;
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
    private static final EventManager eventManager = new EventManager();

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
    public static EventManager getEventManager() {
        return eventManager;
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
