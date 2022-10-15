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

package net.sushiclient.client.gui.theme.simple.config;

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.gui.AnyPanelComponent;
import net.sushiclient.client.gui.CollapseMode;
import net.sushiclient.client.gui.ConfigComponent;
import net.sushiclient.client.gui.SmoothCollapseComponent;
import net.sushiclient.client.gui.layout.FlowDirection;
import net.sushiclient.client.gui.layout.FlowLayout;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.gui.theme.simple.SimpleColorPickerComponent;
import net.sushiclient.client.gui.theme.simple.SimpleColorPickerHeaderComponent;

import java.awt.*;

public class SimpleColorComponent extends AnyPanelComponent implements ConfigComponent<Color> {

    private final SimpleColorPickerComponent simpleColorPickerComponent;
    private final Configuration<Color> configuration;
    private boolean ignoreUpdate;

    public SimpleColorComponent(ThemeConstants constants, Configuration<Color> configuration) {
        simpleColorPickerComponent = new SimpleColorPickerComponent(constants, configuration.getName(), configuration.getValue()) {
            @Override
            protected void onChange(Color color) {
                if (!configuration.getValue().equals(color)) {
                    ignoreUpdate = true;
                    configuration.setValue(color);
                    ignoreUpdate = false;
                }
            }
        };
        SmoothCollapseComponent<?> collapseComponent = new SmoothCollapseComponent<>(simpleColorPickerComponent, CollapseMode.DOWN, 100);
        add(new SimpleColorPickerHeaderComponent(constants, collapseComponent, configuration::getValue, configuration.getName()));
        add(collapseComponent);
        setLayout(new FlowLayout(this, FlowDirection.DOWN));
        this.configuration = configuration;
        configuration.addHandler(c -> {
            if (ignoreUpdate) return;
            simpleColorPickerComponent.setColor(c);
        });
    }

    @Override
    public Configuration<Color> getValue() {
        return configuration;
    }
}
