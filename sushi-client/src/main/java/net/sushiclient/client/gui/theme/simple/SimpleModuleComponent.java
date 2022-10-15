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

import net.sushiclient.client.gui.AnyPanelComponent;
import net.sushiclient.client.gui.SmoothCollapseComponent;
import net.sushiclient.client.gui.layout.FlowDirection;
import net.sushiclient.client.gui.layout.FlowLayout;
import net.sushiclient.client.modules.Module;

public class SimpleModuleComponent extends AnyPanelComponent {
    private final Module module;

    public SimpleModuleComponent(Module module, SimpleModuleToggleComponent toggleComponent, SmoothCollapseComponent<SimpleModuleConfigComponent> collapseComponent) {
        this.module = module;
        setLayout(new FlowLayout(this, FlowDirection.DOWN));
        add(toggleComponent);
        add(collapseComponent);
    }

    public Module getModule() {
        return module;
    }
}
