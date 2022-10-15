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

public class DoubleRange {
    @SerializedName("current")
    private double current;
    @SerializedName("top")
    private double top;
    @SerializedName("bottom")
    private double bottom;
    @SerializedName("step")
    private double step;
    @SerializedName("digits")
    private int digits;

    public DoubleRange() {
    }

    public DoubleRange(double current, double top, double bottom, double step, int digits) {
        this.current = current;
        this.top = top;
        this.bottom = bottom;
        this.step = step;
        this.digits = digits;
    }

    public double getCurrent() {
        return current;
    }

    public double getTop() {
        return top;
    }

    public double getBottom() {
        return bottom;
    }

    public double getStep() {
        return step;
    }

    public int getDigits() {
        return digits;
    }

    public DoubleRange setCurrent(double current) {
        return new DoubleRange(current, top, bottom, step, digits);
    }
}
