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
import net.sushiclient.client.gui.ConfigComponent;
import net.sushiclient.client.gui.layout.FlowDirection;
import net.sushiclient.client.gui.layout.FlowLayout;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.gui.theme.simple.SimpleTextComponent;
import net.sushiclient.client.gui.theme.simple.SimpleTextHeaderComponent;

public class SimpleStringComponent extends AnyPanelComponent implements ConfigComponent<String> {

    private final Configuration<String> config;

    public SimpleStringComponent(ThemeConstants constants, Configuration<String> config) {
        this.config = config;
        setLayout(new FlowLayout(this, FlowDirection.DOWN));
        add(new SimpleTextHeaderComponent(constants, config.getName()));
        add(new SimpleTextComponent(constants, config.getValue(), !config.isTemporary()) {
            @Override
            protected void onChange(String text) {
                config.setValue(text);
            }
        });
    }

    @Override
    public Configuration<String> getValue() {
        return config;
    }
}
