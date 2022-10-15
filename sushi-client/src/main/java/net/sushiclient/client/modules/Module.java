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

package net.sushiclient.client.modules;

import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.gui.hud.ElementFactory;

import java.util.List;

public interface Module {

    String getId();

    String getName();

    boolean isTemporary();

    boolean isVisible();

    void setVisible(boolean visible);

    boolean isEnabled();

    void setEnabled(boolean enabled);
    void setEnabled(boolean enabled, String message);

    boolean isPaused();

    void setPaused(boolean paused);

    ConflictType[] getConflictTypes();

    Category getCategory();

    void setCategory(Category category);

    Keybind getKeybind();

    void setKeybind(Keybind bind);

    RootConfigurations getConfigurations();

    ModuleFactory getModuleFactory();

    ElementFactory[] getElementFactories();

    void addHandler(ModuleHandler handler);

    boolean removeHandler(ModuleHandler handler);

    List<ModuleHandler> getHandlers();
}
