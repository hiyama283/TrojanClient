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
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.gui.ConfigComponent;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.gui.theme.simple.SimpleBarComponent;
import net.sushiclient.client.utils.render.GuiUtils;
import net.sushiclient.client.utils.render.TextPreview;

public class SimpleIntRangeComponent extends SimpleBarComponent implements ConfigComponent<IntRange> {

    private final ThemeConstants constants;
    private final Configuration<IntRange> config;


    public SimpleIntRangeComponent(ThemeConstants constants, Configuration<IntRange> config) {
        super(constants, (double) (config.getValue().getCurrent() - config.getValue().getBottom()) / (config.getValue().getTop() - config.getValue().getBottom()));
        this.config = config;
        this.constants = constants;
    }

    @Override
    public void onRender() {
        super.onRender();
        GuiUtils.prepareText(config.getName(), constants.font.getValue(), constants.textColor.getValue(), 9, false).draw(getWindowX() + 1, getWindowY() + 1);
        TextPreview preview = GuiUtils.prepareText(Integer.toString(config.getValue().getCurrent()), constants.font.getValue(), constants.textColor.getValue(), 9, false);
        preview.draw(getWindowX() + getWidth() - preview.getWidth() - 1, getWindowY() + 1);
    }

    @Override
    protected void onChange(double progress) {
        int rawCurrent = (int) (progress * (config.getValue().getTop() - config.getValue().getBottom()) + config.getValue().getBottom());
        int current = rawCurrent - (rawCurrent % config.getValue().getStep());
        config.setValue(new IntRange(current, config.getValue().getTop(), config.getValue().getBottom(), config.getValue().getStep()));
    }

    @Override
    public Configuration<IntRange> getValue() {
        return config;
    }
}
