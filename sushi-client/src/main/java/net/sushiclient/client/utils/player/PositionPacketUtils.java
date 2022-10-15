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

package net.sushiclient.client.utils.player;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public class PositionPacketUtils {

    private static final Map<Object, Consumer<Integer>> weakListeners = new WeakHashMap<>();
    private static int counter;

    public static void increment() {
        weakListeners.values().forEach(it -> it.accept(++counter));
    }

    public static int current() {
        return counter;
    }

    public static void addListener(Object holder, Consumer<Integer> c) {
        weakListeners.put(holder, c);
    }
}
