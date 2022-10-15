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

import net.sushiclient.client.Sushi;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.ConfigurationsHandler;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.gui.Components;
import net.sushiclient.client.gui.hud.HudComponent;
import net.sushiclient.client.gui.hud.HudEditComponent;
import net.sushiclient.client.gui.theme.Theme;
import net.sushiclient.client.modules.*;

public class HudModule extends BaseModule {
    private final Theme fallbackTheme;
    private final Configuration<String> theme;
    private HudComponent hudComponent;

    public HudModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        hudComponent = newHudComponent(provider);
        fallbackTheme = Sushi.getDefaultTheme();
        theme = provider.get("theme", "Theme", "HUD Theme", String.class, fallbackTheme.getId());
        provider.temp("editor", "Open Editor", null, Runnable.class, () -> {
            Components.closeAll();
            setEnabled(true);
            Theme theme = fallbackTheme;
            for (Theme t : Sushi.getThemes()) {
                if (t.getId().equalsIgnoreCase(this.theme.getId())) {
                    theme = t;
                    break;
                }
            }
            Components.show(new HudEditComponent(theme, hudComponent), false, false);
        });
        provider.addHandler(new ConfigurationsHandler() {
            @Override
            public void reset() {
                boolean enabled = isEnabled();
                setEnabled(false);
                hudComponent = newHudComponent(provider);
                if (enabled) setEnabled(true);
            }
        });
    }

    protected HudComponent newHudComponent(RootConfigurations configurations) {
        return new HudComponent(configurations, this);
    }

    @Override
    public void onEnable() {
        Components.show(hudComponent, true, false, Components.getAll().size());
    }

    @Override
    public void onDisable() {
        hudComponent.getContext().close();
    }

    @Override
    public String getDefaultName() {
        return "HUD";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.CLIENT;
    }
}
