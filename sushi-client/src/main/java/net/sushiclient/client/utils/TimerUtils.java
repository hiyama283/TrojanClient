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

package net.sushiclient.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;
import net.sushiclient.client.mixin.AccessorMinecraft;
import net.sushiclient.client.mixin.AccessorTimer;

import java.util.HashMap;

public class TimerUtils {
    private static int counter;
    private static final HashMap<Integer, Float> multipliers = new HashMap<>();

    private static void setSpeed(float speed) {
        Timer timer = ((AccessorMinecraft) Minecraft.getMinecraft()).getTimer();
        ((AccessorTimer) timer).setTickLength(50 / speed);
    }

    public static float getTimer() {
        Timer timer = ((AccessorMinecraft) Minecraft.getMinecraft()).getTimer();
        return 50 / ((AccessorTimer) timer).getTickLength();
    }

    private static float getMultiplier() {
        float multiplier = 1;
        for (float f : multipliers.values()) multiplier *= f;
        return multiplier;
    }

    public static int push(float multiplier) {
        multipliers.put(++counter, multiplier);
        setSpeed(getMultiplier());
        return counter;
    }

    public static void pop(int counter) {
        multipliers.remove(counter);
        setSpeed(getMultiplier());
    }
}
