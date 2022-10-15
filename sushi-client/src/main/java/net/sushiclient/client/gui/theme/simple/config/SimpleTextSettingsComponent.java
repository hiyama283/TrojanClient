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
import net.sushiclient.client.config.FakeConfiguration;
import net.sushiclient.client.config.data.EspColor;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.gui.AnyPanelComponent;
import net.sushiclient.client.gui.ConfigComponent;
import net.sushiclient.client.gui.Insets;
import net.sushiclient.client.gui.layout.FlowDirection;
import net.sushiclient.client.gui.layout.FlowLayout;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.utils.render.GuiUtils;
import net.sushiclient.client.utils.render.TextSettings;

public class SimpleTextSettingsComponent extends AnyPanelComponent implements ConfigComponent<TextSettings> {

    private final ThemeConstants constants;
    private final Configuration<TextSettings> configuration;
    private final Configuration<String> font;
    private final Configuration<IntRange> pts;
    private final Configuration<Boolean> shadow;
    private final Configuration<EspColor> color;
    private final SimpleStringComponent fontComponent;
    private final SimpleIntRangeComponent ptsComponent;
    private final SimpleBooleanComponent shadowComponent;
    private final SimpleEspColorComponent colorComponent;

    public SimpleTextSettingsComponent(ThemeConstants constants, Configuration<TextSettings> c) {
        this.constants = constants;
        this.configuration = c;
        this.font = new FakeConfiguration<>("font", "Font", null, String.class, c.getValue().getFont());
        this.pts = new FakeConfiguration<>("pts", "pts", null, IntRange.class, new IntRange(9, 30, 1, 1));
        this.shadow = new FakeConfiguration<>("shadow", "Shadow", null, Boolean.class, true);
        this.color = new FakeConfiguration<>("color", "Color", null, EspColor.class, c.getValue().getColor());

        this.fontComponent = new SimpleStringComponent(constants, font);
        this.ptsComponent = new SimpleIntRangeComponent(constants, pts);
        this.shadowComponent = new SimpleBooleanComponent(constants, shadow);
        this.colorComponent = new SimpleEspColorComponent(constants, color);

        ptsComponent.setMargin(new Insets(0, 1, 0, 1));
        shadowComponent.setMargin(new Insets(0, 1, 0, 1));
        colorComponent.setMargin(new Insets(0, 1, 1, 1));

        c.addHandler(it -> {
            if (!it.getFont().equals(font.getValue())) font.setValue(it.getFont());
            if (it.getPts() != pts.getValue().getCurrent()) pts.setValue(pts.getValue().setCurrent(it.getPts()));
            if (it.hasShadow() != shadow.getValue()) shadow.setValue(it.hasShadow());
            if (!it.getColor().equals(color.getValue())) color.setValue(it.getColor());
        });

        font.addHandler(it -> c.setValue(c.getValue().setFont(it)));
        pts.addHandler(it -> c.setValue(c.getValue().setPts(it.getCurrent())));
        shadow.addHandler(it -> c.setValue(c.getValue().setShadow(it)));
        color.addHandler(it -> c.setValue(c.getValue().setColor(it)));

        setLayout(new FlowLayout(this, FlowDirection.DOWN));
        add(fontComponent);
        add(shadowComponent);
        add(ptsComponent);
        add(colorComponent);
    }

    @Override
    public void onRender() {
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), constants.outlineColor.getValue());
        super.onRender();
    }

    @Override
    public Configuration<TextSettings> getValue() {
        return configuration;
    }
}
