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

import net.minecraft.client.Minecraft;
import net.sushiclient.client.ModInformation;
import net.sushiclient.client.Sushi;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.Named;
import net.sushiclient.client.gui.Component;
import net.sushiclient.client.gui.ComponentContext;
import net.sushiclient.client.gui.Components;
import net.sushiclient.client.gui.theme.Theme;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.render.GuiUtils;
import org.lwjgl.input.Keyboard;

public class ClickGuiModule extends BaseModule {
    public enum Names implements Named {
        TROJAN("Trojan"), SHARK_SUSHI("Shark Sushi");
        private final String name;

        Names(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

    private final Theme fallbackTheme;
    private final Configuration<String> theme;
    private final Configuration<Names> nameMode;
    private ComponentContext<Component> context;

    public ClickGuiModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        fallbackTheme = Sushi.getDefaultTheme();
        theme = provider.get("theme", "Theme", "ClickGUI Theme", String.class, fallbackTheme.getId());
        nameMode = provider.get("name_mode", "Name mode", null, Names.class, Names.TROJAN);
        nameMode.addHandler(value -> ModInformation.name = value.getName());
        ModInformation.name = nameMode.getValue().getName();
        // prepare font for SimpleClickGui (for performance reasons)
        // maybe ugly but this should not cause bugs
        getTheme().newClickGui(this);
    }

    @Override
    protected boolean isTemporaryByDefault() {
        return true;
    }

    private Theme getTheme() {
        for (Theme t : Sushi.getThemes()) {
            if (t.getId().equalsIgnoreCase(this.theme.getId())) {
                return t;
            }
        }
        return fallbackTheme;
    }

    @Override
    public void onEnable() {
        Component component = getTheme().newClickGui(this);
        context = Components.show(component, false, false);
        GuiUtils.lockGame(() -> setEnabled(false));
    }

    @Override
    public void onDisable() {
        GuiUtils.unlockGame();
        Minecraft.getMinecraft().setIngameFocus();
        context.close();
    }

    @Override
    public String getDefaultName() {
        return "ClickGUI";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.CLIENT;
    }

    @Override
    public Keybind getDefaultKeybind() {
        return new Keybind(ActivationType.TOGGLE, Keyboard.KEY_RSHIFT);
    }
}
