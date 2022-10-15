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
import net.sushiclient.client.modules.ActivationType;
import net.sushiclient.client.modules.Keybind;
import net.sushiclient.client.modules.Module;
import org.lwjgl.input.Keyboard;

@CommandAlias(value = "bind", description = "Sets keybinding for a module")
public class BindCommand {

    @Default
    public void onDefault(Logger out, Module module, String... keys) {
        int[] keyCode = new int[keys.length];
        for (int i = 0; i < keys.length; i++) {
            keyCode[i] = Keyboard.getKeyIndex(keys[i].toUpperCase());
            if (keyCode[i] == Keyboard.KEY_NONE) {
                out.send(LogLevel.ERROR, "Key name " + keys[i] + " is not recognized");
                return;
            }
        }
        module.setKeybind(new Keybind(ActivationType.TOGGLE, keyCode));
        out.send(LogLevel.INFO, "Changed keybinding for " + module.getName());
    }
}
