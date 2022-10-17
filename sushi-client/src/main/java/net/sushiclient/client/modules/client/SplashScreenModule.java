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

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.gui.mainmenu.MainMenu;
import net.sushiclient.client.modules.*;

public class SplashScreenModule extends BaseModule {
    private final Configuration<IntRange> backgroundId;

    public SplashScreenModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        backgroundId = provider.get("back_ground_id", "Background id", null, IntRange.class,
                new IntRange(0, MainMenu.backgroundSize - 1, 0, 1));
        MainMenu.background = backgroundId.getValue().getCurrent();
        backgroundId.addHandler(b -> {
            MainMenu.background = b.getCurrent();
        });
    }

    @Override
    public void onEnable() {
        setEnabled(false);
    }

    @Override
    public String getDefaultName() {
        return "Splash screen";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.CLIENT;
    }
}
