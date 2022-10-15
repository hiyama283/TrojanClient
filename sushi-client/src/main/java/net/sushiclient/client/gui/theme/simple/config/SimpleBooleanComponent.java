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
import net.sushiclient.client.gui.ConfigComponent;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.gui.theme.simple.SimpleToggleComponent;
import net.sushiclient.client.utils.render.GuiUtils;

public class SimpleBooleanComponent extends SimpleToggleComponent<Configuration<Boolean>> implements ConfigComponent<Boolean> {

    private final ThemeConstants constants;
    private final Configuration<Boolean> conf;

    public SimpleBooleanComponent(ThemeConstants constants, Configuration<Boolean> conf) {
        super(constants, conf.getValue());
        this.constants = constants;
        this.conf = conf;
        setHeight(12);
    }

    @Override
    public Configuration<Boolean> getValue() {
        return conf;
    }

    @Override
    public void onRender() {
        setToggled(conf.getValue());
        super.onRender();
        GuiUtils.prepareText(conf.getName(), constants.font.getValue(), constants.textColor.getValue(), 9, false).draw(getWindowX() + 1, getWindowY() + 1);
    }

    @Override
    protected void onChange(boolean newValue) {
        conf.setValue(newValue);
    }
}
