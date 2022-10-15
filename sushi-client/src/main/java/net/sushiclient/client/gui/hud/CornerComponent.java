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

package net.sushiclient.client.gui.hud;

import net.sushiclient.client.gui.base.BaseComponent;
import net.sushiclient.client.utils.render.GuiUtils;

import java.awt.*;

public class CornerComponent extends BaseComponent {

    @Override
    public void onRender() {
        setX(0);
        setY(0);
        setWidth(10);
        setHeight(10);
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), new Color(50, 50, 50, 30));
    }
}
