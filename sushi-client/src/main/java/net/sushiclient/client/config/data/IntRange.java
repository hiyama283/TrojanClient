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

package net.sushiclient.client.config.data;

import com.google.gson.annotations.SerializedName;

public class IntRange {
    @SerializedName("current")
    private int current;
    @SerializedName("top")
    private int top;
    @SerializedName("bottom")
    private int bottom;
    @SerializedName("step")
    private int step;

    public IntRange() {
    }

    public IntRange(int current, int top, int bottom, int step) {
        this.current = current;
        this.top = top;
        this.bottom = bottom;
        this.step = step;
    }

    public int getCurrent() {
        return current;
    }

    public int getTop() {
        return top;
    }

    public int getBottom() {
        return bottom;
    }

    public int getStep() {
        return step;
    }

    public IntRange setCurrent(int current) {
        return new IntRange(current, top, bottom, step);
    }
}
