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

import net.minecraft.util.math.MathHelper;

public class TpsUtils {
    private static final double MAX_TPS = 40;
    private static final double MIN_TPS = 0.1;

    private static double tps;

    public static double getTps() {
        return tps;
    }

    public static void setTps(double tps) {
        TpsUtils.tps = MathHelper.clamp(tps, 0.1, 40);
    }
}
