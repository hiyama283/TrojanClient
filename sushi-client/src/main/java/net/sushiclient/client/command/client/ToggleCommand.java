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

package net.sushiclient.client.command.client;

import net.sushiclient.client.command.LogLevel;
import net.sushiclient.client.command.Logger;
import net.sushiclient.client.command.annotation.CommandAlias;
import net.sushiclient.client.command.annotation.Default;
import net.sushiclient.client.modules.Module;

@CommandAlias(value = "toggle", aliases = "t", description = "Toggles a module", syntax = "<module>")
public class ToggleCommand {

    @Default
    public void onDefault(Logger out, Module module) {
        module.setEnabled(!module.isEnabled());
        out.send(LogLevel.INFO, module.getName() + " has been " + (module.isEnabled() ? "enabled" : "disabled"));
    }
}
