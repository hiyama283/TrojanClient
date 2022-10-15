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

package net.sushiclient.client.handlers;

import com.google.common.primitives.Ints;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.client.Minecraft;
import net.sushiclient.client.Sushi;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.input.KeyPressEvent;
import net.sushiclient.client.events.input.KeyReleaseEvent;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.ActivationType;
import net.sushiclient.client.modules.Module;

import java.util.ArrayList;

public class KeybindHandler {

    private final IntArrayList heldKeys = new IntArrayList();
    private final ArrayList<Module> heldModules = new ArrayList<>();

    private boolean checkKeybind(Module module) {
        if (module.getKeybind().getKeys().length == 0)
            return false;
        int[] keys = heldKeys.toIntArray();
        for (int key : module.getKeybind().getKeys()) {
            if (!Ints.contains(keys, key)) {
                return false;
            }
        }
        return true;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onKeyPress(KeyPressEvent e) {
        heldKeys.add(e.getKeyCode());
        if (!Minecraft.getMinecraft().inGameHasFocus) return;
        ArrayList<Module> candidates = new ArrayList<>();
        int maxKeys = 0;
        for (Module module : Sushi.getProfile().getModules().getAll()) {
            if (checkKeybind(module)) {
                candidates.add(module);
                int keys = module.getKeybind().getKeys().length;
                if (keys > maxKeys) maxKeys = keys;
            }
        }
        if (candidates.isEmpty()) return;
        int finalMaxKeys = maxKeys;
        candidates.removeIf(it -> it.getKeybind().getKeys().length < finalMaxKeys);
        for (Module module : candidates) {
            if (heldModules.contains(module)) continue;
            heldModules.add(module);
            ActivationType type = module.getKeybind().getActivationType();
            if (type == ActivationType.HOLD)
                module.setEnabled(true);
            else if (type == ActivationType.TOGGLE)
                module.setEnabled(!module.isEnabled());
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onKeyRelease(KeyReleaseEvent e) {
        heldKeys.rem(e.getKeyCode());
        if (!Minecraft.getMinecraft().inGameHasFocus) return;
        for (Module module : new ArrayList<>(heldModules)) {
            if (checkKeybind(module)) continue;
            heldModules.remove(module);
            if (module.getKeybind().getActivationType() == ActivationType.HOLD)
                module.setEnabled(false);
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        if (Minecraft.getMinecraft().inGameHasFocus) return;
        for (Module module : new ArrayList<>(heldModules)) {
            if (module.getKeybind().getActivationType() == ActivationType.HOLD)
                module.setEnabled(false);
        }
        heldModules.clear();
    }
}
