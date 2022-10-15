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

package net.sushiclient.client.gui.theme.simple;

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.gui.AnyPanelComponent;
import net.sushiclient.client.gui.Component;
import net.sushiclient.client.gui.ConfigComponent;
import net.sushiclient.client.gui.Insets;
import net.sushiclient.client.gui.layout.FlowDirection;
import net.sushiclient.client.gui.layout.FlowLayout;
import net.sushiclient.client.gui.theme.Theme;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.List;

public class SimpleConfigCategoryComponent extends AnyPanelComponent {

    private final Theme theme;
    private final Configurations configurations;
    private long nextUpdate;

    public SimpleConfigCategoryComponent(Theme theme, Configurations configurations) {
        this.theme = theme;
        this.configurations = configurations;
        setLayout(new FlowLayout(this, FlowDirection.DOWN));
        setMargin(new Insets(2, 2, 2, 2));
    }

    private boolean contains(Configuration<?> conf) {
        for (Component component : this) {
            if (!(component instanceof ConfigComponent)) continue;
            if (((ConfigComponent<?>) component).getValue().equals(conf)) return true;
        }
        return false;
    }

    private void update() {
        List<Configuration<?>> list;
        if (configurations instanceof RootConfigurations) {
            list = ((RootConfigurations) configurations).getAll(false);
        } else {
            list = configurations.getAll();
        }
        for (Configuration<?> conf : list) {
            if (!conf.isValid()) continue;
            if (contains(conf)) continue;
            ConfigComponent<?> component = theme.newConfigComponent(conf);
            if (component == null) continue;
            component.setMargin(new Insets(0, 0, 0.5, 0));
            add(component, true);
        }
        for (Component component : new ArrayList<>(this)) {
            if (!(component instanceof ConfigComponent)) continue;
            if (((ConfigComponent<?>) component).getValue().isValid()) continue;
            remove(component);
        }

        sort((a, b) -> {
            int aPriority = 1000000;
            int bPriority = 1000000;
            if (a instanceof ConfigComponent)
                aPriority = ((ConfigComponent<?>) a).getValue().getPriority();
            if (b instanceof ConfigComponent)
                bPriority = ((ConfigComponent<?>) b).getValue().getPriority();
            return Integer.compare(aPriority, bPriority);
        });
    }

    @Override
    public void onRelocate() {
        if (System.currentTimeMillis() > nextUpdate) {
            nextUpdate = System.currentTimeMillis() + RandomUtils.nextInt(500, 1000);
            update();
        }
        super.onRelocate();
    }
}