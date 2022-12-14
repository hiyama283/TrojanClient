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

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.data.IntRange;

import java.util.function.Supplier;

public class UpdateTimer {

    private final boolean real;
    private final Supplier<Integer> supplier;
    private long last;

    public UpdateTimer(boolean real, int time) {
        this(real, () -> time);
    }

    public UpdateTimer(boolean real, Configuration<IntRange> conf) {
        this(real, () -> conf.getValue().getCurrent());
    }

    public UpdateTimer(boolean real, Supplier<Integer> supplier) {
        this.real = real;
        this.supplier = supplier;
    }

    public synchronized boolean peek() {
        int req = supplier.get();
        if (real) {
            long now = System.currentTimeMillis();
            return req <= now - last;
        } else {
            int now = TickUtils.current();
            return req <= now - last;
        }
    }

    public synchronized boolean update() {
        int req = supplier.get();
        if (real) {
            long now = System.currentTimeMillis();
            if (req <= now - last) {
                last = now;
                return true;
            } else {
                return false;
            }
        } else {
            int now = TickUtils.current();
            if (req <= now - last) {
                last = now;
                return true;
            } else {
                return false;
            }
        }
    }
}
