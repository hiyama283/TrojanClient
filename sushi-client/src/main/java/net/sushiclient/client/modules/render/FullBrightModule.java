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

package net.sushiclient.client.modules.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.client.GameSettingsSaveEvent;
import net.sushiclient.client.modules.*;

public class FullBrightModule extends BaseModule {

    private final GameSettings settings = Minecraft.getMinecraft().gameSettings;
    private float oldGamma;

    public FullBrightModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
    }

    @Override
    public void onEnable() {
        oldGamma = settings.gammaSetting;
        settings.gammaSetting = 15;
    }

    @Override
    public void onDisable() {
        settings.gammaSetting = oldGamma;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPreSave(GameSettingsSaveEvent e) {
        settings.gammaSetting = oldGamma;
    }

    @EventHandler(timing = EventTiming.POST)
    public void onPostSave(GameSettingsSaveEvent e) {
        settings.gammaSetting = 15;
    }

    @Override
    public String getDefaultName() {
        return "FullBright";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.RENDER;
    }
}
