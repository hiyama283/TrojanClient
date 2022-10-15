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

public class PhaseWalkTimer {
    private long startTime;
    private long endTime;
    public void start() {
        startTime = System.currentTimeMillis();
        this.endTime = 4100L;
    }

    @Override
    public String toString() {
        long nowTime = System.currentTimeMillis() - startTime;
        long rTime = endTime - nowTime;
        if (Math.abs(rTime) != rTime)
            return "0.0";
        else {
            String[] str = String.valueOf(rTime).split("");
            if (str.length == 4)
                return str[0] + "." + str[1];
            else if (str.length == 3)
                return "0." + str[0];
            else
                return "0.0";
        }
    }
}
