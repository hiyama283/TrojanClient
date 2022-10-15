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
import net.sushiclient.client.config.data.Named;
import net.sushiclient.client.gui.ConfigComponent;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.gui.theme.simple.SimpleEnumComponent;

public class SimpleNamedComponent<T extends Named> extends SimpleEnumComponent<T> implements ConfigComponent<T> {

    private final Configuration<T> conf;

    public SimpleNamedComponent(ThemeConstants constants, Configuration<T> conf) {
        super(constants, conf.getName(), conf.getValue(), conf.getValueClass());
        this.conf = conf;
    }

    @Override
    public Configuration<T> getValue() {
        return conf;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onChange(Named newValue) {
        conf.setValue((T) newValue);
    }
}
